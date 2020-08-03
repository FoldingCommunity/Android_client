package com.sonymobile.androidapp.gridcomputing.views;

import android.app.TimePickerDialog;
import android.content.Context;
import android.content.res.TypedArray;
import android.text.format.DateFormat;
import android.util.AttributeSet;
import android.view.View;
import android.widget.TimePicker;
import com.sonymobile.androidapp.gridcomputing.preferences.PrefUtils;


import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;

import androidx.preference.DialogPreference;

public class TimeEndPreference extends DialogPreference {
    private Calendar calendar;
    private TimePicker picker = null;

    public TimeEndPreference(Context ctxt) {
        this(ctxt, null);
    }

    public TimeEndPreference(Context ctxt, AttributeSet attrs) {
        this(ctxt, attrs, android.R.attr.dialogPreferenceStyle);
    }

    public TimeEndPreference(Context ctxt, AttributeSet attrs, int defStyle) {
        super(ctxt, attrs, defStyle);

        setPositiveButtonText("Set");
        setNegativeButtonText("Cancel");
        calendar = new GregorianCalendar();
    }



//    @Override
//    protected View onCreateDialogView() {
//        picker = new TimePicker(getContext());
//        return (picker);
//    }
//
//    @Override
//    protected void onBindDialogView(View v) {
//        super.onBindDialogView(v);
//        picker.setCurrentHour(calendar.get(Calendar.HOUR_OF_DAY));
//        picker.setCurrentMinute(calendar.get(Calendar.MINUTE));
//    }
//
//    @Override
//    protected void onDialogClosed(boolean positiveResult) {
//        super.onDialogClosed(positiveResult);
//
//        if (positiveResult) {
//            calendar.set(Calendar.HOUR_OF_DAY, picker.getCurrentHour());
//            calendar.set(Calendar.MINUTE, picker.getCurrentMinute());
//
//            setSummary(getSummary());
//            if (callChangeListener(calendar.getTimeInMillis())) {
//                persistLong(calendar.getTimeInMillis());
//                notifyChanged();
//            }
//        }
//    }

    @Override
    protected Object onGetDefaultValue(TypedArray a, int index) {
        return (a.getString(index));
    }

    @Override
    protected void onSetInitialValue(boolean restoreValue, Object defaultValue) {

        if (restoreValue) {
            if (defaultValue == null) {
                calendar.setTimeInMillis(getPersistedLong(System.currentTimeMillis()));
            } else {
                calendar.setTimeInMillis(Long.parseLong(getPersistedString((String) defaultValue)));
            }
        } else {
            if (defaultValue == null) {
                calendar.setTimeInMillis(System.currentTimeMillis());
            } else {
                calendar.setTimeInMillis(Long.parseLong((String) defaultValue));
            }
        }
        setSummary(getSummary());
    }

    @Override
    public CharSequence getSummary() {
        if (calendar == null) {
            return null;
        }
        return DateFormat.getTimeFormat(getContext()).format(new Date(calendar.getTimeInMillis()));
    }

    @Override
    protected void onClick() {
        Calendar mcurrentTime = Calendar.getInstance();
        int hour = mcurrentTime.get(Calendar.HOUR_OF_DAY);
        int minute = mcurrentTime.get(Calendar.MINUTE);
        TimePickerDialog mTimePicker;
        mTimePicker = new TimePickerDialog(getContext(), new TimePickerDialog.OnTimeSetListener() {
            @Override
            public void onTimeSet(TimePicker timePicker, int selectedHour, int selectedMinute) {
//                eReminderTime.setText( selectedHour + ":" + selectedMinute);
                calendar = Calendar.getInstance();
                calendar.set(Calendar.HOUR_OF_DAY, selectedHour);
                calendar.set(Calendar.MINUTE, selectedMinute);

                PrefUtils.setLongValue("settings_pref", "END_TIME", calendar.getTimeInMillis());

                setSummary(getSummary());
                if (callChangeListener(calendar.getTimeInMillis())) {
                    persistLong(calendar.getTimeInMillis());
                    notifyChanged() ;
                }
            }
        }, hour, minute, true);//Yes 24 hour time
        mTimePicker.setTitle("Select Time");
        mTimePicker.show();


    }
}