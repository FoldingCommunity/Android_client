package com.sonymobile.androidapp.gridcomputing.fragments;


import android.os.Bundle;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import org.json.JSONArray;
import org.json.JSONException;


import com.sonymobile.androidapp.gridcomputing.R;
import com.sonymobile.androidapp.gridcomputing.conditions.ConditionType;
import com.sonymobile.androidapp.gridcomputing.log.Log;

/**
 * Fragment used to show the detailed stats information.
 */
public class StatsPageFragment extends Fragment {

    private static final String STATS_EXTRA_KEY =
            "com.sonymobile.androidapp.gridcomputing.STATS_EXTRA_KEY";

    private static final String NAME_EXTRA_KEY =
            "com.sonymobile.androidapp.gridcomputing.NAME_EXTRA_KEY";

    //User/team credits
    private TextView creditText;

    //User/team ranking
    private TextView rankText;

    //User/team percentile
    private TextView percentileText;

    //Dynamic progress bar
    private ProgressBar progressWheel;

    //Stats name
    private TextView name;

    //name var
    private static String mName;

    //numeric info!
    private static long[] mStats;


    /**
     * Creates a fragment instance based on stats.
     *
     * @return a new fragment instance.
     */
    public static StatsPageFragment newInstance(final String name, final long[] stats) {
        Bundle args = new Bundle();
        args.putSerializable(NAME_EXTRA_KEY, name);
        args.putSerializable(STATS_EXTRA_KEY, String.valueOf(stats[0]));
        args.putSerializable("WUS", String.valueOf(stats[1]));
        args.putSerializable("CREDIT", String.valueOf(stats[2]));


        StatsPageFragment fragment = new StatsPageFragment();
        fragment.setArguments(args);

        return fragment;
    }

    @Override
    public View onCreateView(final LayoutInflater inflater, final ViewGroup container,
                             final Bundle savedInstanceState) {
        ViewGroup rootView = (ViewGroup) inflater.inflate(R.layout.stats_detail_item, container,
                false);


        name = (TextView) rootView.findViewById(R.id.textView3);
        creditText = (TextView) rootView.findViewById(R.id.credit_text);
        rankText = (TextView) rootView.findViewById(R.id.rank_text);
        percentileText = (TextView) rootView.findViewById(R.id.Percentile_text);
        progressWheel = (ProgressBar) rootView.findViewById(R.id.progressWheel);

        return rootView;
    }

    @Override
    public void onViewCreated(final View view, @Nullable final Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        mName = (String) getArguments().getSerializable(NAME_EXTRA_KEY);
        mStats = new long[3];
        mStats[0] = Long.parseLong((String) getArguments().getSerializable(STATS_EXTRA_KEY));
        mStats[1] = Long.parseLong((String) getArguments().getSerializable("WUS"));
        mStats[2] = Long.parseLong((String) getArguments().getSerializable("CREDIT"));

        setItem(mName, mStats[0], mStats[1], mStats[2]);

    }

    /**
     * Sets the content of this fragment.
     *
     */
    private void setItem(final String teamName, final long teamPercentile, final long teamwus, final long teamCredit) {
        name.setText(teamName);
        progressWheel.setProgress((int) teamPercentile);
        percentileText.setText(String.valueOf(teamPercentile) + " Percentile");
        rankText.setText("WUs: " + Long.toString(teamwus));
        creditText.setText(Long.toString(teamCredit) + " Pts.");
    }
}
