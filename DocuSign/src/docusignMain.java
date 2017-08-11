
import com.docusign.esign.api.*;
import com.docusign.esign.client.*;
//import com.docusign.esign.client.auth.AccessTokenListener;
import com.docusign.esign.model.*;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
//import java.io.File;
//import java.awt.Desktop;
//import junit.framework.Assert;
//import  org.junit.Assert.*;
//import static org.junit.Assert.assertNotNull;
//import static org.junit.Assert.;
//import org.apache.oltu.oauth2.common.token.BasicOAuthToken;
//import org.junit.After;
//import org.junit.AfterClass;
import org.junit.Assert;
//import org.junit.Before;
//import org.junit.BeforeClass;
//import org.junit.Test;

import java.util.ArrayList;
import java.util.List;
import java.awt.Desktop;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
//import java.util.ArrayList;
//import java.io.IOException;
//import java.net.URI;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

import com.migcomponents.migbase64.Base64;

public class docusignMain {
	
  public static void main (String[] args) {
	  
	   //  final String UserName = "node_sdk@mailinator.com";
	  final String UserName = "hescobedo@hotmail.com";
		// final String Password = "qweqweasd";
	  final String Password = "Beanbag01$";
		// final String IntegratorKey = "ae30ea4e-3959-4d1c-b867-fcb57d2dc4df";
		 final String IntegratorKey = "f771b51b-9943-475e-9cf6-d827df69488c"; 
		// final String ClientSecret = "b4dccdbe-232f-46cc-96c5-b2f0f7448f8f";
		 //final String RedirectURI = "https://www.docusign.com/api";

		 final String BaseUrl = "https://demo.docusign.net/restapi";
		// final String OAuthBaseUrl = "https://account-d.docusign.com";
		// final String SignTest1File = "C:\\Users\\humberto\\OneDrive\\Documents\\docusign-java-client-master\\docusign-java-client-master\\src\\test\\docs\\SignTest1.pdf";
		 final String SignTest1File = "/src/test/SignTest1.pdf";
		 // final String SignTest1File = "/src/test/docs/SignTest1.pdf";
	//	final String EnvelopeId = "48a37c6f-c484-43b7-b469-ec02f5207114";
	  final String EnvelopeId = "c0f42f4f-b96d-412a-a674-9ff27c718d47";
	//74fdd034-fe7f-4353-978d-5df964460711	 
		// final String EnvelopeId = "74fdd034-fe7f-4353-978d-5df964460711";
   // ApiClient apiClient = new ApiClient(BaseUrl);
       String AccountId = null;
   

    	ApiClient apiClient = new ApiClient(BaseUrl);

		String creds = createAuthHeaderCreds(UserName, Password, IntegratorKey);
		apiClient.addDefaultHeader("X-DocuSign-Authentication", creds);
		Configuration.setDefaultApiClient(apiClient);

		try {

			AuthenticationApi authApi = new AuthenticationApi();
			AuthenticationApi.LoginOptions loginOps = authApi.new LoginOptions();
			loginOps.setApiPassword("true");
			loginOps.setIncludeAccountIdGuid("true");
			LoginInformation loginInfo = authApi.login(loginOps);

			Assert.assertNotSame(null, loginInfo);
			Assert.assertNotNull(loginInfo.getLoginAccounts());
			Assert.assertTrue(loginInfo.getLoginAccounts().size() > 0);
			List<LoginAccount> loginAccounts = loginInfo.getLoginAccounts();
			Assert.assertNotNull(loginAccounts.get(0).getAccountId());
            AccountId  =loginAccounts.get(0).getAccountId();
			System.out.println("LoginInformation: " + loginInfo);

			// parse first account's baseUrl
			String[] accountDomain = loginInfo.getLoginAccounts().get(0).getBaseUrl().split("/v2");

			// below code required for production, no effect in demo (same
			// domain)
			apiClient.setBasePath(accountDomain[0]);
			Configuration.setDefaultApiClient(apiClient);
			
		} catch (ApiException ex) {
			System.out.println("Exception: " + ex);
		}
		
		RequestASignature(SignTest1File,UserName, Password, IntegratorKey,  BaseUrl);
	//ListDocuments( UserName,Password, IntegratorKey,  BaseUrl, EnvelopeId) ;
	//	DownLoadEnvelopeDocuments(SignTest1File,UserName,Password, IntegratorKey, BaseUrl, EnvelopeId);
//	Envelope getEnvelope  = 
		//EnvelopesApi.getEnvelope(AccountId, AccountId);
		
	/*	EnvelopesApi getEnv = new EnvelopesApi();
		try {
			Envelope Env = new Envelope();
			Env = getEnv.getEnvelope(AccountId, EnvelopeId);
			String status = Env.getStatus();
			System.out.println("EnvelopeStatus: " + status);
			Assert.assertNotNull(status);
		} catch (ApiException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		// use the |accountId| we retrieved through the Login API
		//String accountId = loginAccounts.get(0).getAccountId();

		// instantiate a new EnvelopesApi object
		EnvelopesApi envelopesApi = new EnvelopesApi();  

		// the list status changes call requires at least a from_date
		EnvelopesApi.ListStatusChangesOptions options = envelopesApi.new ListStatusChangesOptions();

		// set from date to filter envelopes (ex: Dec 1, 2015)
		options.setFromDate("2015/12/01");

		// call the listStatusChanges() API
		try {
			EnvelopesInformation envelopes = envelopesApi.listStatusChanges(AccountId, options);
            System.out.println("EnvelopesInformation: " + envelopes);
		
		} catch (ApiException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}*/
		
		
		
	}

  




	private static String createAuthHeaderCreds(String userName, String password, String integratorKey) {
		DocuSignCredentials dsCreds = new DocuSignCredentials(userName, password, integratorKey);

		String creds = null;
		try {
			ObjectMapper mapper = new ObjectMapper();
			creds = mapper.writeValueAsString(dsCreds);
		} catch (JsonProcessingException ex) {
			creds = "";
		}

		return creds;
	   }
	
	private static void RequestASignature(String SignTest1File,String UserName,String Password, String IntegratorKey, String BaseUrl) {

		byte[] fileBytes = null;
		try {
			// String currentDir = new java.io.File(".").getCononicalPath();

			String currentDir = System.getProperty("user.dir");
			
			Path path = Paths.get(currentDir + SignTest1File);
			fileBytes = Files.readAllBytes(path);
		} catch (IOException ioExcp) {
			Assert.assertEquals(null, ioExcp);
		}

		// create an envelope to be signed
		EnvelopeDefinition envDef = new EnvelopeDefinition();
		envDef.setEmailSubject("Please Sign my Java SDK Envelope");
		envDef.setEmailBlurb("Hello, Please sign my Java SDK Envelope.");

		// add a document to the envelope
		Document doc = new Document();
		String base64Doc = Base64.encodeToString(fileBytes, false);
		doc.setDocumentBase64(base64Doc);
		doc.setName("TestFile.pdf");
		doc.setDocumentId("1");

		List<Document> docs = new ArrayList<Document>();
		docs.add(doc);
		envDef.setDocuments(docs);

		// Add a recipient to sign the document
		Signer signer = new Signer();
		signer.setEmail("PDScott@Express-Scripts.com");
	//	signer.setEmail("CJGreer@Express-Scripts.com");
	//	signer.setEmail(UserName);
	//	signer.setName("Chris Greer");
		signer.setName("Patty Scott");
		signer.setRecipientId("1");

		// Create a SignHere tab somewhere on the document for the signer to
		// sign
		SignHere signHere = new SignHere();
		signHere.setDocumentId("1");
		signHere.setPageNumber("1");
		signHere.setRecipientId("1");
		signHere.setXPosition("100");
		signHere.setYPosition("100");
		signHere.setScaleValue("0.5");

		List<SignHere> signHereTabs = new ArrayList<SignHere>();
		signHereTabs.add(signHere);
		Tabs tabs = new Tabs();
		tabs.setSignHereTabs(signHereTabs);
		signer.setTabs(tabs);

		// Above causes issue
		envDef.setRecipients(new Recipients());
		envDef.getRecipients().setSigners(new ArrayList<Signer>());
		envDef.getRecipients().getSigners().add(signer);

		// send the envelope (otherwise it will be "created" in the Draft folder
		envDef.setStatus("sent");

		try {

			ApiClient apiClient = new ApiClient();
			apiClient.setBasePath(BaseUrl);

			String creds = createAuthHeaderCreds(UserName, Password, IntegratorKey);
			apiClient.addDefaultHeader("X-DocuSign-Authentication", creds);
			Configuration.setDefaultApiClient(apiClient);

			AuthenticationApi authApi = new AuthenticationApi();

			AuthenticationApi.LoginOptions loginOptions = authApi.new LoginOptions();
			loginOptions.setApiPassword("true");
			loginOptions.setIncludeAccountIdGuid("true");
			LoginInformation loginInfo = authApi.login(loginOptions);

			Assert.assertNotNull(loginInfo);
			Assert.assertNotNull(loginInfo.getLoginAccounts());
			Assert.assertTrue(loginInfo.getLoginAccounts().size() > 0);
			List<LoginAccount> loginAccounts = loginInfo.getLoginAccounts();
			Assert.assertNotNull(loginAccounts.get(0).getAccountId());

			String accountId = loginInfo.getLoginAccounts().get(0).getAccountId();

			// parse first account's baseUrl
			String[] accountDomain = loginInfo.getLoginAccounts().get(0).getBaseUrl().split("/v2");

			// below code required for production, no effect in demo (same
			// domain)
			apiClient.setBasePath(accountDomain[0]);
			Configuration.setDefaultApiClient(apiClient);

			EnvelopesApi envelopesApi = new EnvelopesApi();

			EnvelopeSummary envelopeSummary = envelopesApi.createEnvelope(accountId, envDef);

			Assert.assertNotNull(envelopeSummary);
			Assert.assertNotNull(envelopeSummary.getEnvelopeId());
			Assert.assertEquals("sent", envelopeSummary.getStatus());

			System.out.println("EnvelopeSummary: " + envelopeSummary);

		} catch (ApiException ex) {
			System.out.println("Exception: " + ex);
			Assert.assertEquals(null, ex);
		}
	}

	private static void ListDocuments(String UserName,String Password, String IntegratorKey, String BaseUrl, String EnvelopeId) {
		try {

			ApiClient apiClient = new ApiClient();
			apiClient.setBasePath(BaseUrl);

			String creds = createAuthHeaderCreds(UserName, Password, IntegratorKey);
			apiClient.addDefaultHeader("X-DocuSign-Authentication", creds);
			Configuration.setDefaultApiClient(apiClient);

			AuthenticationApi authApi = new AuthenticationApi();
			LoginInformation loginInfo = authApi.login();

			Assert.assertNotNull(loginInfo);
			Assert.assertNotNull(loginInfo.getLoginAccounts());
			Assert.assertTrue(loginInfo.getLoginAccounts().size() > 0);
			List<LoginAccount> loginAccounts = loginInfo.getLoginAccounts();
			Assert.assertNotNull(loginAccounts.get(0).getAccountId());

			String accountId = loginInfo.getLoginAccounts().get(0).getAccountId();

			// parse first account's baseUrl
			String[] accountDomain = loginInfo.getLoginAccounts().get(0).getBaseUrl().split("/v2");

			// below code required for production, no effect in demo (same
			// domain)
			apiClient.setBasePath(accountDomain[0]);
			Configuration.setDefaultApiClient(apiClient);

			EnvelopesApi envelopesApi = new EnvelopesApi();

			EnvelopeDocumentsResult docsList = envelopesApi.listDocuments(accountId, EnvelopeId);
			Assert.assertNotNull(docsList);
			Assert.assertEquals(EnvelopeId, docsList.getEnvelopeId());

			System.out.println("EnvelopeDocumentsResult: " + docsList);
		} catch (ApiException ex) {
			System.out.println("Exception: " + ex);
			Assert.assertEquals(null, ex);
		}
	}
	
	private static void DownLoadEnvelopeDocuments(String SignTest1File,String UserName,String Password, String IntegratorKey, String BaseUrl, String EnvelopeId) {

		byte[] fileBytes = null;
		try {
			// String currentDir = new java.io.File(".").getCononicalPath();

			String currentDir = System.getProperty("user.dir");

			Path path = Paths.get(currentDir + SignTest1File);
			fileBytes = Files.readAllBytes(path);
		} catch (IOException ioExcp) {
			Assert.assertEquals(null, ioExcp);
		}

		// create an envelope to be signed
		EnvelopeDefinition envDef = new EnvelopeDefinition();
		envDef.setEmailSubject("Please Sign my Java SDK Envelope");
		envDef.setEmailBlurb("Hello, Please sign my Java SDK Envelope.");

		// add a document to the envelope
		Document doc = new Document();
		String base64Doc = Base64.encodeToString(fileBytes, false);
		doc.setDocumentBase64(base64Doc);
		doc.setName("TestFile.pdf");
		doc.setDocumentId("1");

		List<Document> docs = new ArrayList<Document>();
		docs.add(doc);
		envDef.setDocuments(docs);

		// Add a recipient to sign the document
		Signer signer = new Signer();
		signer.setEmail(UserName);
		String name = "Chris Greer";
		signer.setName(name);
		signer.setRecipientId("1");

		// this value represents the client's unique identifier for the signer
		String clientUserId = "2939";
		signer.setClientUserId(clientUserId);

		// Create a SignHere tab somewhere on the document for the signer to
		// sign
		Text text = new Text();
		text.setDocumentId("1");
		text.setPageNumber("1");
		text.setRecipientId("1");
		text.setXPosition("100");
		text.setYPosition("100");

		List<Text> textTabs = new ArrayList<Text>();
		textTabs.add(text);
		Tabs tabs = new Tabs();
		tabs.setTextTabs(textTabs);
		signer.setTabs(tabs);

		// Above causes issue
		envDef.setRecipients(new Recipients());
		envDef.getRecipients().setSigners(new ArrayList<Signer>());
		envDef.getRecipients().getSigners().add(signer);

		// send the envelope (otherwise it will be "created" in the Draft folder
		envDef.setStatus("sent");

		try {

			ApiClient apiClient = new ApiClient();
			apiClient.setBasePath(BaseUrl);

			String creds = createAuthHeaderCreds(UserName, Password, IntegratorKey);
			apiClient.addDefaultHeader("X-DocuSign-Authentication", creds);
			Configuration.setDefaultApiClient(apiClient);

			AuthenticationApi authApi = new AuthenticationApi();
			LoginInformation loginInfo = authApi.login();

			Assert.assertNotSame(null, loginInfo);
			Assert.assertNotNull(loginInfo.getLoginAccounts());
			Assert.assertTrue(loginInfo.getLoginAccounts().size() > 0);
			List<LoginAccount> loginAccounts = loginInfo.getLoginAccounts();
			Assert.assertNotNull(loginAccounts.get(0).getAccountId());

			String accountId = loginInfo.getLoginAccounts().get(0).getAccountId();

			// parse first account's baseUrl
			String[] accountDomain = loginInfo.getLoginAccounts().get(0).getBaseUrl().split("/v2");

			// below code required for production, no effect in demo (same
			// domain)
			apiClient.setBasePath(accountDomain[0]);
			Configuration.setDefaultApiClient(apiClient);

			EnvelopesApi envelopesApi = new EnvelopesApi();
			EnvelopeSummary envelopeSummary = envelopesApi.createEnvelope(accountId, envDef);

			Assert.assertNotNull(envelopeSummary);
			Assert.assertNotNull(envelopeSummary.getEnvelopeId());
			EnvelopeId = envelopeSummary.getEnvelopeId();

			System.out.println("EnvelopeSummary: " + envelopeSummary);

			byte[] pdfBytes = envelopesApi.getDocument(accountId, envelopeSummary.getEnvelopeId(), "combined");
			Assert.assertTrue(pdfBytes.length > 0);
			
			  try {
		      String fileName = "test.pdf" ;
			 File pdfFile = new File(fileName);
			 // File pdfFile = File.createTempFile("ds_", "pdf", null);
			 //  File pdfFile = File.createTempFile("ds_", "pdf", new File("C:/"));
			  FileOutputStream fos = new FileOutputStream(pdfFile);
			  fos.write(pdfBytes);
			  fos.close();
			 Desktop.getDesktop().open(pdfFile);
	
			} catch (Exception ex) {
			Assert.fail("Could not create pdf File");
	        }
			 

		} catch (ApiException ex) {
			System.out.println("Exception: " + ex);
			Assert.assertEquals(null, ex);
		}

	}
	
	
	
	
	
}