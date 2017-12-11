package com.esi.docusign;
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
import com.docusign.esign.model.CompositeTemplate;
import com.docusign.esign.model.Document;
import com.docusign.esign.model.EnvelopeDefinition;
import com.docusign.esign.model.EnvelopeDocumentsResult;
import com.docusign.esign.model.EnvelopeSummary;
import com.docusign.esign.model.LoginAccount;
import com.docusign.esign.model.LoginInformation;
import com.docusign.esign.model.Recipients;
import com.docusign.esign.model.SignHere;
//import com.docusign.esign.model.Signer;
import com.docusign.esign.model.Tabs;
import com.docusign.esign.model.TemplateRole;
import com.docusign.esign.model.Text;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.migcomponents.migbase64.Base64;
import com.esi.docusigntemplate.*;

public class DocuSign {

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
		String templateId = "9435e702-f428-4683-82d0-a90fe481d68a";
		//Loading document into byte stream 
	
		//9435e702-f428-4683-82d0-a90fe481d68a
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
  
		/****/

		
		Signer compsignee = new Signer();
		compsignee.setRoleName("CompanySignee");
		compsignee.setName(CompanySigneeName);
		compsignee.setEmail(CompanySigneeEmail);
		compsignee.setRoutingOrder("1");
		compsignee.setRecipientId("1");
		
		
		Signer custsignee = new Signer();
		custsignee.setRoleName("CustomerSignee");
		custsignee.setName(CustomerSigneeName);
		custsignee.setEmail(CompanySigneeEmail);
		custsignee.setRoutingOrder("2");
		custsignee.setRecipientId("2");
	
		com.esi.docusigntemplate.Recipients recip = new com.esi.docusigntemplate.Recipients();
		List<Signer> signers = new ArrayList<Signer>();
		signers.add(compsignee);
		signers.add(custsignee);
		recip.setSigners(signers);
		
        InlineTemplate inltemp = new InlineTemplate ();
        inltemp.setRecipients(recip);
        inltemp.setSequence("2");
		
        List<InlineTemplate> inlineTemplates =new ArrayList<InlineTemplate>();
        inlineTemplates.add(inltemp);
        
		ServerTemplate stemp = new ServerTemplate();
		stemp.setTemplateId(templateId);
		stemp.setSequence("1");
		List<ServerTemplate> serverTemplates = new ArrayList<ServerTemplate>();
		serverTemplates.add(stemp)		;
		
        
		com.esi.docusigntemplate.Document doc = new com.esi.docusigntemplate.Document();
		String base64Doc = Base64.encodeToString(fileBytes, false);
		doc.setDocumentBase64(base64Doc);
		doc.setName(Document);
		doc.setDocumentId("1");
        
		com.esi.docusigntemplate.CompositeTemplate  comptmpl = new com.esi.docusigntemplate.CompositeTemplate();
		comptmpl.setDocument(doc);
		
		
		comptmpl.setInlineTemplates(inlineTemplates);
		comptmpl.setServerTemplates(serverTemplates);
		
		

        
        
		// create an envelope to be signed
		EnvelopeDefinition envDef = new EnvelopeDefinition();
	//	envDef.setEmailSubject("Please Sign my Java SDK Envelope");
	//	envDef.setEmailBlurb("Hello, Please sign my Java SDK Envelope.");
	
		
		
		envDef.setTemplateId(templateId);
		
		
		
		TemplateRole tRole = new TemplateRole();
		tRole.setRoleName("CompanySignee");
		tRole.setName(CompanySigneeName);
		tRole.setEmail(CompanySigneeEmail);
		
		
		TemplateRole cRole = new TemplateRole();
		cRole.setRoleName("CustomerSignee");
		cRole.setName(CustomerSigneeName);
		cRole.setEmail(CustomerSigneeEmail);

		
		// create a list of template roles and add our newly created role
		List<TemplateRole> templateRolesList = new ArrayList<TemplateRole>();
		templateRolesList.add(tRole);
		//List<TemplateRole> templateRolesList2 = new ArrayList<TemplateRole>();
		templateRolesList.add(cRole);

		// assign template role(s) to the envelope 
		envDef.setTemplateRoles(templateRolesList);
		
		// add a document to the envelope
		com.docusign.esign.model.Document doc2 = new com.docusign.esign.model.Document();
		String base64Doc2 = Base64.encodeToString(fileBytes, false);
		doc2.setDocumentBase64(base64Doc);
		doc2.setName("TestFile.pdf");
		doc2.setDocumentId("1");
	//	doc.setTemplateRequired(templateId);
		
		com.docusign.esign.model.CompositeTemplate ctemp = new com.docusign.esign.model.CompositeTemplate();
	      
		//ctemp.setDocument(base64Doc2);
		
	//	doc.

		List<com.docusign.esign.model.Document> docs = new ArrayList<com.docusign.esign.model.Document>();
		docs.add(doc2);
		envDef.setDocuments(docs);
       
		if (CompanySigneeName != null && CustomerSigneeEmail != null){

			
			
		}
		else if (CompanySigneeName == null && CustomerSigneeEmail != null){
			
		}
		
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

		//	Assert.assertNotNull(loginInfo);
		//	Assert.assertNotNull(loginInfo.getLoginAccounts());
		//	Assert.assertTrue(loginInfo.getLoginAccounts().size() > 0);
			
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
	
	
	public  void RequestASignature(String Document,String CompanySigneeName,String CompanySigneeEmail, String CustomerSigneeName, String CustomerSigneeEmail, String UserName, String Password, String IntegratorKey, String BaseUrl, String TemplateId) {


		TemplateId = "9435e702-f428-4683-82d0-a90fe481d68a";
		
		byte[] fileBytes = null;
		try {
			Path path = Paths.get(Document);
			fileBytes = Files.readAllBytes(path);
		} catch (IOException ioExcp) {
			Assert.assertEquals(null, ioExcp);
		}
  
	
		
		Signer compsignee = new Signer();
		compsignee.setRoleName("InternalSigner");
		compsignee.setName(CompanySigneeName);
		compsignee.setEmail(CompanySigneeEmail);
		compsignee.setRoutingOrder("1");
		compsignee.setRecipientId("1");
		
		
		Signer custsignee = new Signer();
		custsignee.setRoleName("ExternalSigner");
		custsignee.setName(CustomerSigneeName);
		custsignee.setEmail(CompanySigneeEmail);
		custsignee.setRoutingOrder("2");
		custsignee.setRecipientId("2");
	
		com.esi.docusigntemplate.Recipients recip = new com.esi.docusigntemplate.Recipients();
		List<com.esi.docusigntemplate.Signer> signers = new ArrayList<com.esi.docusigntemplate.Signer>();
		signers.add(compsignee);
		signers.add(custsignee);
		recip.setSigners(signers);
		
        InlineTemplate inltemp = new InlineTemplate ();
        inltemp.setRecipients(recip);
        inltemp.setSequence("2");
		
        List<InlineTemplate> inlineTemplates =new ArrayList<InlineTemplate>();
        inlineTemplates.add(inltemp);
        
		ServerTemplate stemp = new ServerTemplate();
		stemp.setTemplateId(TemplateId);
		stemp.setSequence("1");
		List<com.esi.docusigntemplate.ServerTemplate> serverTemplates = new ArrayList<com.esi.docusigntemplate.ServerTemplate>();
		serverTemplates.add(stemp);
		
        
		com.esi.docusigntemplate.Document doc = new com.esi.docusigntemplate.Document();
		String base64Doc = Base64.encodeToString(fileBytes, false);
		doc.setDocumentBase64(base64Doc);
		doc.setName(Document);
		doc.setDocumentId("1");
        
		com.esi.docusigntemplate.CompositeTemplate  comptmpl = new com.esi.docusigntemplate.CompositeTemplate();
		comptmpl.setDocument(doc);
		comptmpl.setInlineTemplates(inlineTemplates);
		comptmpl.setServerTemplates(serverTemplates);
		
		List<com.esi.docusigntemplate.CompositeTemplate> compositeTemplates = new ArrayList<com.esi.docusigntemplate.CompositeTemplate>();
		compositeTemplates.add(comptmpl);
		
		com.esi.docusigntemplate.CompositeTemplateRequest tempreqt = new com.esi.docusigntemplate.CompositeTemplateRequest();
		tempreqt.setCompositeTemplates(compositeTemplates);
		tempreqt.setStatus("Sent");
		
		
		EnvelopeDefinition envDef = new EnvelopeDefinition();
		envDef.setCompositeTemplates(compositeTemplates);
		envDef.setStatus("sent");
		
	//envDef.setCompositeTemplates(compositeTemplates);

      
		if (CompanySigneeName != null && CustomerSigneeEmail != null){

			
			
		}
		else if (CompanySigneeName == null && CustomerSigneeEmail != null){
			
		}
		
	
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
			
			List<LoginAccount> loginAccounts = loginInfo.getLoginAccounts();
	
			String accountId = loginInfo.getLoginAccounts().get(0).getAccountId();

			// parse first account's baseUrl
			String[] accountDomain = loginInfo.getLoginAccounts().get(0).getBaseUrl().split("/v2");

			// below code required for production, no effect in demo (same
			// domain)
			apiClient.setBasePath(accountDomain[0]);
			
			Configuration.setDefaultApiClient(apiClient);

			EnvelopesApi envelopesApi = new EnvelopesApi();
			
			//EnvelopeSummary envelopeSummary = envelopesApi.createE;
			EnvelopeSummary envelopeSummary = envelopesApi.createEnvelope(accountId, envDef);
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
	//	signer.setTabs(tabs);
        
		List<SignHere> signHereTabs2 = new ArrayList<SignHere>();
		signHereTabs2.add(signHere2);
		Tabs tabs2 = new Tabs();
		tabs2.setSignHereTabs(signHereTabs2);
	//	signer2.setTabs(tabs2);
		
		
		// Above causes issue
		
		envDef.setRecipients(new Recipients());
	//	envDef.getRecipients().setSigners(new ArrayList<Signer>());
	//	envDef.getRecipients().getSigners().add(signer);
		//
		//envDef.getRecipients().setSigners(new ArrayList<Signer>());
	//     envDef.getRecipients().getSigners().add(signer2); 
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
