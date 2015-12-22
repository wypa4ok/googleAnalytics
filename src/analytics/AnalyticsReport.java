package analytics;

import argparser.ArgParser;
import argparser.StringHolder;

import com.google.api.client.auth.oauth2.Credential;
import com.google.api.client.extensions.java6.auth.oauth2.AuthorizationCodeInstalledApp;
import com.google.api.client.extensions.jetty.auth.oauth2.LocalServerReceiver;
import com.google.api.client.googleapis.auth.oauth2.GoogleAuthorizationCodeFlow;
import com.google.api.client.googleapis.auth.oauth2.GoogleClientSecrets;
import com.google.api.client.googleapis.auth.oauth2.GoogleCredential;
import com.google.api.client.googleapis.javanet.GoogleNetHttpTransport;
import com.google.api.client.http.javanet.NetHttpTransport;
import com.google.api.client.json.JsonFactory;
import com.google.api.client.json.gson.GsonFactory;
import com.google.api.client.util.IOUtils;
import com.google.api.client.util.store.FileDataStoreFactory;
import com.google.api.services.analytics.Analytics;
import com.google.api.services.analytics.Analytics.Management.UnsampledReports.Insert;
import com.google.api.services.analytics.AnalyticsScopes;
import com.google.api.services.analytics.model.UnsampledReport;










import drive.GoogleDriveAccess;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.URL;
import java.security.GeneralSecurityException;
import java.text.SimpleDateFormat;
import java.util.Date;

@SuppressWarnings("all")
public class AnalyticsReport {
	
	


	// The directory where the user's credentials will be stored.
	
	private static final String APPLICATION_NAME = "google_analytics";
	private static final JsonFactory JSON_FACTORY = GsonFactory.getDefaultInstance();
	private static NetHttpTransport httpTransport;
	//private static FileDataStoreFactory dataStoreFactory;
	private static final String SERVICE_ACCOUNT_EMAIL = "smth@developer.gserviceaccount.com";
	static String KEY_FILE_LOCATION = "client_secrets_an.p12";
	
	private static Analytics initializeAnalytics() throws Exception {
		InputStream is = Analytics_app.class.getResourceAsStream(KEY_FILE_LOCATION);
		byte[] buffer = new byte[is.available()];
		is.read(buffer);
		
		File targetFile = new File("keys_analytics.txt");
		OutputStream outStream = new FileOutputStream(targetFile);
		outStream.write(buffer);
		outStream.flush();
		outStream.close();
		/*final java.io.File DATA_STORE_DIR = new java.io.File(
				System.getProperty("user.home"), ".store/hello_analytics"); */

		httpTransport = GoogleNetHttpTransport.newTrustedTransport();
		//dataStoreFactory = new FileDataStoreFactory(DATA_STORE_DIR);

		// Load client secrets.
		GoogleCredential credential = new GoogleCredential.Builder()
        .setTransport(httpTransport)
        .setJsonFactory(JSON_FACTORY)
        .setServiceAccountId(SERVICE_ACCOUNT_EMAIL)
        .setServiceAccountPrivateKeyFromP12File(targetFile)
        .setServiceAccountScopes(AnalyticsScopes.all())
        .build();
		//new File(KEY_FILE_LOCATION)
    // Construct the Analytics service object.
		//new File("keys.txt").delete();
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
			//System.out.println(account + " " + metrics);
			printResultsToFile(getResults(analytics, account, property, profile, start_date, end_date,
					metrics, dimensions, filters, segments), filePath);
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
	}
	
	@SuppressWarnings("all")
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
		   
	   return createdReport;
	}
	
	@SuppressWarnings("all")
	public static void printResultsToFile(UnsampledReport report, String path) throws InterruptedException, IOException {

		try {
			GoogleDriveAccess.printFile(report.getTitle() + ".csv", path);
		} catch (GeneralSecurityException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} 

	}

}
