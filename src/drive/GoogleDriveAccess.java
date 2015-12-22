package drive;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.PrintWriter;
import java.security.GeneralSecurityException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import analytics.Analytics_app;

import com.google.api.client.googleapis.auth.oauth2.GoogleCredential;
import com.google.api.client.http.HttpTransport;
import com.google.api.client.http.javanet.NetHttpTransport;
import com.google.api.client.json.jackson2.JacksonFactory;
import com.google.api.services.drive.Drive;
import com.google.api.services.drive.DriveScopes;
import com.google.api.services.drive.Drive.Files;
import com.google.api.services.drive.model.File;
import com.google.api.services.drive.model.FileList;

@SuppressWarnings("all")
public class GoogleDriveAccess {
	private static final String SERVICE_ACCOUNT_EMAIL = "smth@developer.gserviceaccount.com";
	private static final String SERVICE_ACCOUNT_PKCS12_FILE_PATH = "client_secrets_an.p12";

	
	public static Drive initialize() throws GeneralSecurityException,
			IOException {

		InputStream is = Analytics_app.class.getResourceAsStream(SERVICE_ACCOUNT_PKCS12_FILE_PATH);
		byte[] buffer = new byte[is.available()];
		is.read(buffer);
		
		java.io.File targetFile = new java.io.File("keys_analytics.txt");
		OutputStream outStream = new FileOutputStream(targetFile);
		outStream.write(buffer);
		outStream.flush();
		outStream.close();
		
		HttpTransport httpTransport = new NetHttpTransport();
		JacksonFactory jsonFactory = new JacksonFactory();
		GoogleCredential credential = new GoogleCredential.Builder()
				.setTransport(httpTransport)
				.setJsonFactory(jsonFactory)
				.setServiceAccountId(SERVICE_ACCOUNT_EMAIL)
				.setServiceAccountScopes(Arrays.asList(DriveScopes.DRIVE))
				.setServiceAccountPrivateKeyFromP12File(targetFile)
				.build();
		//new java.io.File(SERVICE_ACCOUNT_PKCS12_FILE_PATH)
		Drive service = new Drive.Builder(httpTransport, jsonFactory, null)
				.setHttpRequestInitializer(credential)
				.setApplicationName("google_analytics")
				.build();
		return service;

	}
	
	public static void clean(){
		java.io.File f = new java.io.File("keys_analytics.txt");
		System.out.println(f.delete());
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
			//System.out.println(f.getTitle());
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

		Drive service = initialize();
		System.out.println("Connected");
		
		String fileId = null;
		try {
			System.out.println("Processing...");
			while (checkForFile(service, fileName) == null) {
				Thread.sleep(60000);
			}
			fileId = checkForFile(service, fileName);
			File file = service.files().get(fileId).execute();
			int i;
			char c;

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
			clean();

		} catch (IOException e) {
			System.out.println("An error occured: " + e);
		}
	}

}
