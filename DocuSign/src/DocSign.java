import java.awt.Desktop;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;

import javax.ws.rs.core.HttpHeaders;

import org.junit.Assert;

import com.docusign.esign.api.AuthenticationApi;
import com.docusign.esign.api.EnvelopesApi;
import com.docusign.esign.api.AuthenticationApi.LoginOptions;
import com.docusign.esign.client.ApiClient;
import com.docusign.esign.client.ApiException;
import com.docusign.esign.client.Configuration;
import com.docusign.esign.model.Document;
import com.docusign.esign.model.EnvelopeDefinition;
import com.docusign.esign.model.EnvelopeDocumentsResult;
import com.docusign.esign.model.EnvelopeSummary;
import com.docusign.esign.model.LoginAccount;
import com.docusign.esign.model.LoginInformation;
import com.docusign.esign.model.Recipients;
import com.docusign.esign.model.SignHere;
import com.docusign.esign.model.Signer;
import com.docusign.esign.model.Tabs;
import com.docusign.esign.model.Text;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.migcomponents.migbase64.Base64;

public class DocSign {

	String Status;
	String EnvelopeId;
	
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
	
	
	
	public  void RequestASignature(String Document,String CompanySigneeName,String CompanySigneeEmail, String CustomerSigneeName, String CustomerSigneeEmail, String UserName, String Password, String IntegratorKey, String BaseUrl) {

		//Document,Status,CompanySigneeName,CompanySigneeEmail,CustomerSigneeName,CustomerSigneeEmail FROM DocuSign WHERE Status ='Send'";
		
		//Loading document into byte stream 
		
		byte[] fileBytes = null;
		try {
			// String currentDir = new java.io.File(".").getCononicalPath();

			//String currentDir = System.getProperty("user.dir");
			
			//Path path = Paths.get(currentDir + SignTest1File);
			Path path = Paths.get(Document);
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
       
		if (CompanySigneeName != null && CustomerSigneeEmail != null){

		}
		else if (CompanySigneeName == null && CustomerSigneeEmail != null){
			
		}
		
		// Add a recipient to sign the document
		Signer signer = new Signer();
		signer.setEmail(CompanySigneeEmail);
		signer.setName(CompanySigneeName);
		signer.setRecipientId("1");
		signer.setRoutingOrder("1");
		
		Signer signer2 = new Signer();
		signer2.setEmail(CustomerSigneeEmail);
		signer2.setName(CustomerSigneeName);
		signer2.setRecipientId("2");
		signer2.setRoutingOrder("2");
		
		// Create a SignHere tab somewhere on the document for the signer to
		// sign
		SignHere signHere = new SignHere();
		signHere.setDocumentId("1");
		signHere.setPageNumber("1");
		signHere.setRecipientId("1");
		signHere.setXPosition("100");
		signHere.setYPosition("100");
		signHere.setScaleValue("0.5");
	
		SignHere signHere2 = new SignHere();
		signHere2.setDocumentId("1");
		signHere2.setPageNumber("1");
		signHere2.setRecipientId("2");
		signHere2.setXPosition("500");
		signHere2.setYPosition("100");
		signHere2.setScaleValue("0.5");
		
		List<SignHere> signHereTabs = new ArrayList<SignHere>();
		signHereTabs.add(signHere);
	//signHereTabs.add(signHere2);
		
		Tabs tabs = new Tabs();
		tabs.setSignHereTabs(signHereTabs);
		signer.setTabs(tabs);
        
		List<SignHere> signHereTabs2 = new ArrayList<SignHere>();
		signHereTabs2.add(signHere2);
		Tabs tabs2 = new Tabs();
		tabs2.setSignHereTabs(signHereTabs2);
		signer2.setTabs(tabs2);
		
		
		// Above causes issue
		
		envDef.setRecipients(new Recipients());
		envDef.getRecipients().setSigners(new ArrayList<Signer>());
		envDef.getRecipients().getSigners().add(signer);
		//
		//envDef.getRecipients().setSigners(new ArrayList<Signer>());
	     envDef.getRecipients().getSigners().add(signer2); 
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
			
			EnvelopeId = (envelopeSummary.getEnvelopeId());
			Status =  envelopeSummary.getStatus();
			
			Assert.assertEquals("sent", envelopeSummary.getStatus());

			System.out.println("EnvelopeSummary: " + envelopeSummary);

			
			
			
		} catch (ApiException ex) {
			System.out.println("Exception: " + ex);
			Assert.assertEquals(null, ex);
		}
			
	}
	public  void RequestOneSignature(String Document,String CompanySigneeName,String CompanySigneeEmail, String CustomerSigneeName, String CustomerSigneeEmail, String UserName, String Password, String IntegratorKey, String BaseUrl) {

		//Document,Status,CompanySigneeName,CompanySigneeEmail,CustomerSigneeName,CustomerSigneeEmail FROM DocuSign WHERE Status ='Send'";
		
		//Loading document into byte stream 
		
		byte[] fileBytes = null;
		try {
			// String currentDir = new java.io.File(".").getCononicalPath();

			//String currentDir = System.getProperty("user.dir");
			
			//Path path = Paths.get(currentDir + SignTest1File);
			Path path = Paths.get(Document);
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
       
		if (CompanySigneeName != null && CustomerSigneeEmail != null){

		}
		else if (CompanySigneeName == null && CustomerSigneeEmail != null){
			
		}
		
		// Add a recipient to sign the document
		Signer signer = new Signer();
		signer.setEmail(CompanySigneeEmail);
		signer.setName(CompanySigneeName);
		signer.setRecipientId("1");
		signer.setRoutingOrder("1");
		
		Signer signer2 = new Signer();
		signer2.setEmail(CustomerSigneeEmail);
		signer2.setName(CustomerSigneeName);
		signer2.setRecipientId("2");
		signer2.setRoutingOrder("2");
		
		// Create a SignHere tab somewhere on the document for the signer to
		// sign
		SignHere signHere = new SignHere();
		signHere.setDocumentId("1");
		signHere.setPageNumber("1");
		signHere.setRecipientId("1");
		signHere.setXPosition("100");
		signHere.setYPosition("100");
		signHere.setScaleValue("0.5");
	
		SignHere signHere2 = new SignHere();
		signHere2.setDocumentId("1");
		signHere2.setPageNumber("1");
		signHere2.setRecipientId("2");
		signHere2.setXPosition("500");
		signHere2.setYPosition("100");
		signHere2.setScaleValue("0.5");
		
		List<SignHere> signHereTabs = new ArrayList<SignHere>();
		signHereTabs.add(signHere);
	//signHereTabs.add(signHere2);
		
		Tabs tabs = new Tabs();
		tabs.setSignHereTabs(signHereTabs);
		signer.setTabs(tabs);
        
		List<SignHere> signHereTabs2 = new ArrayList<SignHere>();
		signHereTabs2.add(signHere2);
		Tabs tabs2 = new Tabs();
		tabs2.setSignHereTabs(signHereTabs2);
		signer2.setTabs(tabs2);
		
		
		// Above causes issue
		
		envDef.setRecipients(new Recipients());
		envDef.getRecipients().setSigners(new ArrayList<Signer>());
		envDef.getRecipients().getSigners().add(signer);
		//
		//envDef.getRecipients().setSigners(new ArrayList<Signer>());
	     envDef.getRecipients().getSigners().add(signer2); 
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
			
			EnvelopeId = (envelopeSummary.getEnvelopeId());
			Status =  envelopeSummary.getStatus();
			
			Assert.assertEquals("sent", envelopeSummary.getStatus());

			System.out.println("EnvelopeSummary: " + envelopeSummary);

			
			
			
		} catch (ApiException ex) {
			System.out.println("Exception: " + ex);
			Assert.assertEquals(null, ex);
		}
			
	}
	
	public void DownLoadEnvelopeDocuments(String Document,String UserName,String Password, String IntegratorKey, String BaseUrl, String EnvelopeId,String Directory) {

		
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
			/*EnvelopeSummary envelopeSummary = envelopesApi.createEnvelope(accountId, envDef);

			Assert.assertNotNull(envelopeSummary);
			Assert.assertNotNull(envelopeSummary.getEnvelopeId());
			EnvelopeId = envelopeSummary.getEnvelopeId();

			System.out.println("EnvelopeSummary: " + envelopeSummary);*/
             
			byte[] pdfBytes = envelopesApi.getDocument(accountId, EnvelopeId, "combined");
			Assert.assertTrue(pdfBytes.length > 0);
			
			  try {
		      String fileName = Document ;
			 File pdfFile = new File(fileName);
			 // File pdfFile = File.createTempFile("ds_", "pdf", null);
			 //  File pdfFile = File.createTempFile("ds_", "pdf", new File("C:/"));
			  FileOutputStream fos = new FileOutputStream(Directory + pdfFile);
			  fos.write(pdfBytes);
			  fos.close();
			// Desktop.getDesktop().open(pdfFile);
			 // apiClient.getResponseHeaders();
			
			  
			  
			  
			  } catch (Exception ex) {
			Assert.fail("Could not create pdf File");
	        }
			 

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
	
	
	
	
	
	
	
}
