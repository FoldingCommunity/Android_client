# Folding@Home Android Client


## Overview:

This project is a new and updated version of the Sony F@H app that was released in 2014 and unfortunately lost support in 2017. Many thanks goes to the developers at Sony and Anton for the great foundation that the old app provided. The architecture of this app is mostly similar, but we now have support for a wide range of Android SDKs, including the newest stable API (Level 30). Crucially, the app can now run jobs on 64-bit mobile processors. The app also now has a fresh coat of paint, with new icons, color scheme, and a central animation. Please note that the design and programming of the app is not final. The core functionality of the app is all included and working, but we have not yet removed testing code or connected the clients to the remote queue server. 


## Native Libraries:

The new app features a fully custom package of dynamic libraries. The runtime environment is a Nodejs instance. Linked against the native C++ library is a custom mobile version of Nodejs 12.6 (credit: janeasystems). Paired with Nodejs are npm modules, primarily OpenMM. The files contain a freshly compiled version 7.4 of OpenMM, complete with FFTW for CPU support. The OpenMM communicates through an abstraction layer to the Node (using specially designed JS bindings and architecture). Jobs are loaded into the node as scripts that run on OpenMM. 

Each of these libraries is neatly bundled inside of the project file system. The Nodejs library can be found at relative path folding-android/node. The scripts are in folding-android/app/src/main/assets and the npm modules are in folding-android/app/src/main/assets/node_modules. Barring easily-fixed gradle errors, anyone with the newest version of Android Studio (4.0) should be able to clone this repository and run the app themselves. If you have any issues, please see the contact email below. 

## Job-Server:

The Job Queue server acts as a middleman between the client and Folding’s servers. The server receives jobs (which are really just descriptions that include details on how to connect to the Work Server) from the Assignment Server. The “jobs” are stored in a redis database queue and popped off whenever a client requests a job. 


The server’s code was changed to update the keys and increase the response efficiency by reducing unnecessary logging. 

## Android Client UI: 

### LoginActivity - 

LoginActivity is a simple login screen that allows users to sign into the app under either an existing or new username. Okhttp is used to make http requests through the folding at home JSON api to check whether the username is new. The user’s donor id linked to the username is retrieved through an http request and used in other parts of the app. The app currently does not have the ability to create a new account because the http request needed to do so requires an authentication token. We would also like to implement the passkey system in LoginActivity in the future. 

### SummaryActivity -

SummaryActivity is the main page of the app. Folding is initiated and paused by the onClick listener for the play/pause on the status bar, which also displays color coded information about the prerequisite conditions for folding (wifi, battery life, charging) retrieved from ConditionsHandler. SummaryActivity also displays the current job’s cause and elapsed time, which are accessed through the job server. Below the status bar is information about the user’s current contribution. Research description, research cause, and contributed time are all shownFinally, animation is linked to folding. 


### SettingsActivity -

Preferences here are implemented through a default Android settings activity and preferences resource file. The current supported preferences are a hard stop on computation and a slider to adjust the minimum battery level for folding. 

In the future, more preferences could be added (such as the ability to fold on cellular networks).

### StatsActivity -

In StatsActivity, users can see information about their contributions to F@H. Using OkHTTP, the app makes url requests through the folding at home JSON api to get live information on users’ individual, team, and project stats. Users have access to work units, percentile, and credit values for both their individual and team contributions, along with information about the most recent project they contributed to. 


## How Folding starts:

Using the conditions handler, the app continually checks for three main prerequisite conditions: battery life, charging state, and network connectivity. Once these conditions have been met and the user taps the start button, The ServiceManager’s resume function is called. The ServiceManager spawns the ComputeService which communicates with the ComputeEnvironment. The ComputeEnvironment is the direct contact between the app and the JS runtime environment. The ComputeEnvironment  starts the Node using a native function and maintains a communication relay with the Node, listening for status changes and providing needed information and/or commands. These signals are handled through a local HTTP server that runs on port 3000. 


Other activities and files are either helpers, supplementals, or leftovers from the old app that have been retained in case of future use. 


If you have any questions or comments please contact dimaggioanthony379@gmail.com . 

Thanks!


### Links/Attributions:

OpenMM: http://openmm.org/

FFTW: http://www.fftw.org/

NodeJS: https://code.janeasystems.com/nodejs-mobile

Sony Mobile Development: https://github.com/sonyxperiadev

F@H Stats API: https://stats.foldingathome.org/api

