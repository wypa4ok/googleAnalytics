Welcome to bb_google_analytics.
This app was made to simplify the process of making reports using Google Analytics.

The program can be used through command line only.
NOTE: JRE/JDK needs to be installed on the computer in order to run the app.

The application accepts multiple parameters and most of the are required.
_________________________________________________________________________________________________________________________
Parameters:
-acc   : (required) The AccountId of the Google Analytics Account you want to use (eg. 12345678);
-prop  : (required) The propertyId of the property under the specified Account  you want to report on (eg. AA-12345678-9);
-prof  : (required) The ProfileId of the profile(View) under the specified Property you want to query (eg. 12345678);
-start : (required) The start date of the query in a format : yyyy-mm-dd (eg. 2015-04-18);
-end   : (required) The end date of the query in a format : yyyy-mm-dd (eg. 2015-05-18);
-met   : (required) The metrics you want to see in the report in the format : ga:metricName (eg. ga:Users,ga:Sessions);
-dim   : (required) Dimensions you want to use to build the report in the format : ga:dimensionName (eg. ga:Campaign,ga:pagePath);
-fil   : (optional) Filters that you want to apply to your report in the format : ga:filterName 
	 (eg. ga:Campaign=~_1d;ga:Medium==crm;ga:Source==email;ga:pagePath=~smth.html)
         NOTE: == - exact match
         =~ - regex match
         ; - AND
         ,(comma) - OR 
-seg   : (optional) Segments you want to use in your query in the format : ga:segmentName (eg. ga:Country);
-path  : (optional) Path to the directory you want the report to be saved ( eg. /user/workspace/reports);
__________________________________________________________________________________________________________________________

The result of running the application is a csv file with with the name: UnsampledReport_ddMMyyyy_HHMM.csv, 
where ddMMyyyy and HHMM represent date and time of creation of the report;
__________________________________________________________________________________________________________________________

For more info about Google Analytics API please refer to:
https://developers.google.com/analytics/devguides/reporting/core/v3/reference

To learn which dimensions and which metrics ate available for you check this:
https://developers.google.com/analytics/devguides/reporting/core/dimsmets

To try out your query online please reference:
https://ga-dev-tools.appspot.com/query-explorer/

To learn more about authentication using Google Analytics API go to:
https://developers.google.com/analytics/devguides/reporting/core/v2/gdataAuthentication

More info about Google Drive API can be found here:
https://developers.google.com/drive/web/quickstart/quickstart-java

_____________________________________________________________________________________________________________________________

About the project:
Source folder contains two packages: analytics and drive;
Analytics contains classes that are responsible for using Google Analytics API and creation of
unsampled reports itself;
Drive contains classes that use Google Drive API and are responsible for connecting to Google Drive
and downloading reports to the computer. 

Two approaches were implemented here: using Installed Application(analytics.Analytics_app.java + drive.DriveTest.java) 
and Service Account(analytics.Analytics_Report.java + drive.GoogleDriveAccess.java).

Credentials for google accounts are stored within the project(client_secret.json and client_secret_an.p12); 
client_secret.json - used for Installed application implementation;
client_secret_an.p12 - used for Service Account implementation;
________________________________________________________________________________________________________________________________

Thank you,
Pavlo Petrushko