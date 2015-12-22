package analytics;

	import argparser.ArgParser;
import argparser.StringHolder;

import com.google.api.client.auth.oauth2.Credential;
import com.google.api.client.extensions.java6.auth.oauth2.AuthorizationCodeInstalledApp;
import com.google.api.client.extensions.jetty.auth.oauth2.LocalServerReceiver;
import com.google.api.client.googleapis.auth.oauth2.GoogleAuthorizationCodeFlow;
import com.google.api.client.googleapis.auth.oauth2.GoogleClientSecrets;
import com.google.api.client.googleapis.javanet.GoogleNetHttpTransport;
import com.google.api.client.http.javanet.NetHttpTransport;
import com.google.api.client.json.JsonFactory;
import com.google.api.client.json.gson.GsonFactory;
import com.google.api.client.util.store.FileDataStoreFactory;
import com.google.api.services.analytics.Analytics;
import com.google.api.services.analytics.AnalyticsScopes;
import com.google.api.services.analytics.Analytics.Management.UnsampledReports.Insert;
import com.google.api.services.analytics.model.Accounts;
import com.google.api.services.analytics.model.GaData;
import com.google.api.services.analytics.model.Profiles;
import com.google.api.services.analytics.model.UnsampledReport;
import com.google.api.services.analytics.model.Webproperties;

import drive.DriveTest;
import drive.GoogleDriveAccess;

	import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.security.GeneralSecurityException;
import java.text.SimpleDateFormat;
import java.util.Date;


	/**
	 * A simple example of how to access the Google Analytics API.
	 */
	public class Analytics_app {
	
	  private static final String CLIENT_SECRET_JSON_RESOURCE = "client_secret.json";
	  private static final File DATA_STORE_DIR = new File(
	      System.getProperty("user.home"), ".store/hello_analytics");

	  private static final String APPLICATION_NAME = "Hello Analytics";
	  private static final JsonFactory JSON_FACTORY = GsonFactory.getDefaultInstance();
	  private static NetHttpTransport httpTransport;
	  private static FileDataStoreFactory dataStoreFactory;

	  private static Analytics initializeAnalytics() throws Exception {

	    httpTransport = GoogleNetHttpTransport.newTrustedTransport();
	    dataStoreFactory = new FileDataStoreFactory(DATA_STORE_DIR);

	    // Load client secrets.
	    GoogleClientSecrets clientSecrets = GoogleClientSecrets.load(JSON_FACTORY,
	        new InputStreamReader(Analytics_app.class
	            .getResourceAsStream(CLIENT_SECRET_JSON_RESOURCE)));

	    // Set up authorization code flow for all auth scopes.
	    GoogleAuthorizationCodeFlow flow = new GoogleAuthorizationCodeFlow
	        .Builder(httpTransport, JSON_FACTORY, clientSecrets,
	        AnalyticsScopes.all()).setDataStoreFactory(dataStoreFactory)
	        .build();

	    // Authorize.
	    Credential credential = new AuthorizationCodeInstalledApp(flow,
	        new LocalServerReceiver()).authorize("user");

	    // Construct the Analytics service object.
	    return new Analytics.Builder(httpTransport, JSON_FACTORY, credential)
	        .setApplicationName(APPLICATION_NAME).build();
	  }
	  
	  
	  public static void main(String[] args) {
			// TODO Auto-generated method stub
				StringHolder acc = new StringHolder();
				StringHolder prop = new StringHolder();
				StringHolder prof = new StringHolder();
				StringHolder s_date = new StringHolder();
				StringHolder e_date = new StringHolder();
				StringHolder metr = new StringHolder();
				StringHolder dim = new StringHolder();
				StringHolder fil = new StringHolder();
				StringHolder seg = new StringHolder();
				StringHolder path = new StringHolder();

				

				// create the parser and specify the allowed options ...

				ArgParser parser = new ArgParser("java -jar installed_test.jar");
				parser.addOption("-acc %s #name of the operating file", acc);
				parser.addOption("-prop %s #name of the operating file", prop);
				parser.addOption("-prof %s #name of the operating file", prof);
				parser.addOption("-start %s #name of the operating file", s_date);
				parser.addOption("-end %s #name of the operating file", e_date);
				parser.addOption("-met %s #name of the operating file", metr);
				parser.addOption("-dim %s #name of the operating file", dim);
				parser.addOption("-fil %s #name of the operating file", fil);
				parser.addOption("-seg %s #name of the operating file", seg);
				parser.addOption("-path %s #name of the operating file", path);
				
				parser.matchAllArgs (args);
				
				String account = acc.value;
				String property = prop.value;
				String profile = prof.value;
				String start_date = s_date.value;
				String end_date = e_date.value;
				String metrics = metr.value;
				String dimensions = dim.value;
				String filters = fil.value;
				String segments = seg.value;
				String filePath = path.value;

				try {
					Analytics analytics = initializeAnalytics();
					//System.out.println(profile);
					System.out.println(account + " "+ property +" " + profile);
					printResultsToFile(getResults(analytics, account, property, profile, start_date, end_date,
							metrics, dimensions, filters, segments), filePath);
				} catch (Exception e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
		  }

	  private static UnsampledReport getResults(Analytics analytics, String account, String property, String profile,
				String start_date, String end_date, String metrics,
				String dimensions, String filters, String segments) throws IOException, InterruptedException {
					  
			   UnsampledReport report = new UnsampledReport();
					       report.setDimensions(dimensions);
					       report.setMetrics(metrics);
					       report.setStartDate(start_date);
					       report.setEndDate(end_date);
					       report.setFilters(filters);
					       if (segments != null)  report.setSegment(segments);
					       
					       Date today = new Date();
					       SimpleDateFormat sdf = new SimpleDateFormat("ddMMyyyy_HHmm");
					       String date = sdf.format(today);
					       
					       report.setTitle("UnsampledReport_" + date);

					   Insert insertRequest = analytics.management().unsampledReports()
					   .insert(account,property, profile, report); 
					   
					   UnsampledReport createdReport = insertRequest.execute();	
					   System.out.println(createdReport.getDriveDownloadDetails());
			   
		   return createdReport;
		}
		
		@SuppressWarnings("all")
		public static void printResultsToFile(UnsampledReport report, String path) throws InterruptedException, IOException {

			try {
				DriveTest.printFile(report.getTitle() + ".csv", path);
			} catch (GeneralSecurityException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} 

		}
	}

