package drive;
import com.google.api.client.auth.oauth2.Credential;
import com.google.api.client.extensions.java6.auth.oauth2.AuthorizationCodeInstalledApp;
import com.google.api.client.extensions.jetty.auth.oauth2.LocalServerReceiver;
import com.google.api.client.googleapis.auth.oauth2.GoogleClientSecrets;
import com.google.api.client.googleapis.javanet.GoogleNetHttpTransport;
import com.google.api.client.http.GenericUrl;
import com.google.api.client.http.HttpResponse;
import com.google.api.client.http.HttpTransport;
import com.google.api.client.json.JsonFactory;
import com.google.api.client.json.jackson2.JacksonFactory;
import com.google.api.client.googleapis.auth.oauth2.GoogleAuthorizationCodeFlow;
import com.google.api.client.util.store.FileDataStoreFactory;
import com.google.api.services.drive.DriveScopes;
import com.google.api.services.drive.Drive.Files;
import com.google.api.services.drive.model.*;
import com.google.api.services.drive.Drive;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.security.GeneralSecurityException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;


public class DriveTest {
	
	private static final String APPLICATION_NAME = "analytics";

	    /** Directory to store user credentials. */
	    private static final java.io.File DATA_STORE_DIR = new java.io.File(
	        System.getProperty("user.home"), ".credentials/drive-api-quickstart");

	    /** Global instance of the {@link FileDataStoreFactory}. */
	    private static FileDataStoreFactory DATA_STORE_FACTORY;

	    /** Global instance of the JSON factory. */
	    private static final JsonFactory JSON_FACTORY =
	        JacksonFactory.getDefaultInstance();

	    /** Global instance of the HTTP transport. */
	    private static HttpTransport HTTP_TRANSPORT;

	    /** Global instance of the scopes required by this quickstart. */
	    private static final List<String> SCOPES =
	        Arrays.asList(DriveScopes.DRIVE);

	    static {
	        try {
	            HTTP_TRANSPORT = GoogleNetHttpTransport.newTrustedTransport();
	            DATA_STORE_FACTORY = new FileDataStoreFactory(DATA_STORE_DIR);
	        } catch (Throwable t) {
	            t.printStackTrace();
	            System.exit(1);
	        }
	    }

	    /**
	     * Creates an authorized Credential object.
	     * @return an authorized Credential object.
	     * @throws IOException
	     */
	    public static Credential authorize() throws IOException {
	        // Load client secrets.
	        InputStream in =
	            DriveTest.class.getResourceAsStream("client_secret.json");
	        GoogleClientSecrets clientSecrets =
	            GoogleClientSecrets.load(JSON_FACTORY, new InputStreamReader(in));

	        // Build flow and trigger user authorization request.
	        GoogleAuthorizationCodeFlow flow =
	                new GoogleAuthorizationCodeFlow.Builder(
	                        HTTP_TRANSPORT, JSON_FACTORY, clientSecrets, SCOPES)
	                .setDataStoreFactory(DATA_STORE_FACTORY)
	                .setAccessType("online")
	                .build();
	        Credential credential = new AuthorizationCodeInstalledApp(
	            flow, new LocalServerReceiver()).authorize("user");
	        System.out.println(
	                "Credentials saved to " + DATA_STORE_DIR.getAbsolutePath());
	        return credential;
	    }

	    /**
	     * Build and return an authorized Drive client service.
	     * @return an authorized Drive client service
	     * @throws IOException
	     */
	    public static Drive getDriveService() throws IOException {
	        Credential credential = authorize();
	        return new Drive.Builder(
	                HTTP_TRANSPORT, JSON_FACTORY, credential)
	                .setApplicationName(APPLICATION_NAME)
	                .build();
	    }
	
	private static List<File> retrieveAllFiles(Drive service)
			throws IOException {
		List<File> result = new ArrayList<File>();
		Files.List request = service.files().list();

		do {
			try {
				FileList files = request.execute();
				result.addAll(files.getItems());
				request.setPageToken(files.getNextPageToken());
			} catch (IOException e) {
				System.out.println("An error occurred: " + e);
				e.printStackTrace();
				request.setPageToken(null);
			}
		} while (request.getPageToken() != null
				&& request.getPageToken().length() > 0);

		return result;
	}

	private static String checkForFile(Drive service, String fileName)
			throws IOException {
		String fileId = null;
		List<File> files = retrieveAllFiles(service);
		
		for (File f : files) {
			if (f.getTitle().equals(fileName)) {
				fileId = f.getId();
			}
		}
		return fileId;
	}

	private static InputStream downloadFile(Drive service, File file) {
	   
		if (file.getDownloadUrl() != null && file.getDownloadUrl().length() > 0) {
			try {
				return service.files().get(file.getId()).executeMediaAsInputStream();
			} catch (IOException e) {
				// An error occurred.
				e.printStackTrace();
				return null;
			}
		} else {
			return null;
		} 
	}


	public static void printFile(String fileName, String filePath) throws InterruptedException,
			GeneralSecurityException, IOException {

		Drive service = getDriveService();
		System.out.println("Connected");
		
		String fileId = null;
		try {
			System.out.println("Processing...");
			while (checkForFile(service, fileName) == null) {
				Thread.sleep(60000);
			}
			fileId = checkForFile(service, fileName);
			System.out.println(fileId);
			File file = service.files().get(fileId).execute();
			int i;
			char c;
			System.out.println(file + " " + file.getTitle());
			if(filePath != null) fileName = filePath + "/" + fileName;
			@SuppressWarnings("resource")
			PrintWriter writer = new PrintWriter(fileName, "UTF-8");
			
			InputStream is = downloadFile(service, file);			
			InputStreamReader reader = new InputStreamReader(is);
			BufferedReader bufferedReader = new BufferedReader(reader);
			BufferedWriter bw = new BufferedWriter(writer);
			String line;
			
			while ((line = bufferedReader.readLine()) != null) {
				bw.write(line);
				bw.newLine();
				bw.flush();
			}
			System.out.println("File " + file.getTitle() + " has been loaded.");

		} catch (IOException e) {
			System.out.println("An error occured: " + e);
		}
	}

}
