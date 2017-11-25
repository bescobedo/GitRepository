package com.esi.docusign;

import com.docusign.esign.api.*;
import com.docusign.esign.client.*;
//import com.docusign.esign.client.auth.AccessTokenListener;
import com.docusign.esign.model.*;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;


import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.sql.ResultSet;
import java.sql.Statement;
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
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
//import java.util.ArrayList;
//import java.io.IOException;
//import java.net.URI;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Properties;

import com.migcomponents.migbase64.Base64;

public class docusignMain {

	 String IntegratorKey; 
	 String sqlUrl = null;
	 String BaseUrl = "https://demo.docusign.net/restapi";
	 String RequestSignatureFilePath = null;
	 String SignedFilesPath = null;
	 // final String SignTest1File = "/src/test/docs/SignTest1.pdf";
      String EnvelopeId;
	  String Document ;
	  String CompanySigneeName;
	  String CompanySigneeEmail;
	  String CustomerSigneeName;
	  String CustomerSigneeEmail;
	  String Status;
	  String Response ;
	  String UserName;
	  String Password;
	  String Request;
	  //Use to send a document for signature
	public docusignMain(String Request, String Document, String CompanySigneeName, String CompanySigneeEmail, String CustomerSigneeName, String CustomerSigneeEmail){
		
		boolean SqlUpdate = false;
	 
		String AccountId = null;

		  
		 // String Status= args[1];
		  
		    Properties prop = new Properties();
		   	InputStream input = null;

		   	try {
		   		input = new FileInputStream("C:\\Users\\humberto\\OneDrive\\GitRepository\\DocuSign\\src\\resources\\config\\config.properties");

		   		// load a properties file
		   		prop.load(input);

		   		// get the property value and print it out
		   		System.out.println(prop.getProperty("IntegratorKey"));
		   		 IntegratorKey=prop.getProperty("IntegratorKey");
		   		System.out.println(prop.getProperty("UserName"));
		   		UserName = prop.getProperty("UserName");
		   		System.out.println(prop.getProperty("Password"));
		   		Password =prop.getProperty("Password");
		   		System.out.println(prop.getProperty("BaseUrl"));
		   		BaseUrl = prop.getProperty("BaseUrl");
		   		System.out.println(prop.getProperty("RequestSignatureFilePath"));
		   		RequestSignatureFilePath = prop.getProperty("RequestSignatureFilePath");
		   		System.out.println(prop.getProperty("SqliteUrl"));
		   		sqlUrl = prop.getProperty("SqliteUrl");
		   		
		   	} catch (IOException ex) {
		   		ex.printStackTrace();
		   	} finally {
		   		if (input != null) {
		   			try {
		   				input.close();
		   			} catch (IOException e) {
		   				e.printStackTrace();
		   			}
		   		}
		   	}
		
		if (Request == "Send"){
			  
			  
			  Sqlite sql = new Sqlite();
			  Connection sqlConn = sql.connect(sqlUrl);
			  boolean DocuSignInsert = sql.insertNew(Document, Request, CompanySigneeName, CompanySigneeEmail, CustomerSigneeName, CustomerSigneeEmail, sqlConn);;
			
			  if (DocuSignInsert == true){
				    
				    String PathDocument = RequestSignatureFilePath + Document;
				    
				    DocSign sigrequest = new DocSign();
				    
					sigrequest.RequestASignature(PathDocument,CompanySigneeName, CompanySigneeEmail, CustomerSigneeName, CustomerSigneeEmail,UserName, Password, IntegratorKey,  BaseUrl);
					EnvelopeId = sigrequest.EnvelopeId;
					String NewStatus = sigrequest.Status;
				
					SqlUpdate= sql.update(EnvelopeId, Request, Document,NewStatus, sql.connect(sqlUrl));
					
					
			        if (SqlUpdate = false){
			        	Response = "Cound not Update Sqlite";
			        	System.out.println("Could not Update");
			        }
			        else{
			        	Response = "Sqlite Updated";
			        	System.out.println("Updated");
			        }
			  }
			  else{
				  Response = "Sqlite could not be inserted.";
				  System.out.println("Could not insert");
				  
			  }
		  }
		
	   
	}
	//--use to get the document
	public docusignMain(String Request){
		Statement stmt = null;
		Properties prop = new Properties();
	   	InputStream input = null;

	   	try {
	   		input = new FileInputStream("C:\\Users\\humberto\\OneDrive\\GitRepository\\DocuSign\\src\\resources\\config\\config.properties");

	   		// load a properties file
	   		prop.load(input);

	   		// get the property value and print it out
	   		System.out.println(prop.getProperty("IntegratorKey"));
	   		 IntegratorKey=prop.getProperty("IntegratorKey");
	   		System.out.println(prop.getProperty("UserName"));
	   		UserName = prop.getProperty("UserName");
	   		System.out.println(prop.getProperty("Password"));
	   		Password =prop.getProperty("Password");
	   		System.out.println(prop.getProperty("BaseUrl"));
	   		BaseUrl = prop.getProperty("BaseUrl");
	   		System.out.println(prop.getProperty("SignedFilesPath"));
	   		SignedFilesPath = prop.getProperty("SignedFilesPath");
	   		System.out.println(prop.getProperty("SqliteUrl"));
	   		sqlUrl = prop.getProperty("SqliteUrl");
	   		
	   	} catch (IOException ex) {
	   		ex.printStackTrace();
	   	} finally {
	   		if (input != null) {
	   			try {
	   				input.close();
	   			} catch (IOException e) {
	   				e.printStackTrace();
	   			}
	   		}
	   	}
		
		
		if (Request == "Retrieve"){
			System.out.println("in retrive");		
			
			try{
				
		   DocSign getDocument = new DocSign();
			
			Sqlite sql = new Sqlite();
			 Connection sqlConn = sql.connect(sqlUrl);
			 sqlConn.setAutoCommit(false);
			 stmt = sqlConn.createStatement();
			 ResultSet rs  = stmt.executeQuery("Select Document, EnvelopeId from DocuSign where Status='sent'");
			 System.out.println("After Result Set");
			 while ( rs.next() ) {
		         String Document = rs.getString("Document");
		         String EnvelopeId = rs.getString("EnvelopeId");
		         System.out.println("in while");	
		         getDocument.DownLoadEnvelopeDocuments(Document,UserName,Password,IntegratorKey, BaseUrl, EnvelopeId,SignedFilesPath); 
			 }
			
		    } catch(Exception e){
		    	System.err.println(e.getClass().getName() + ": " + e.getMessage() );
		    	System.exit(0);
		    }
			
			
		}
		
		
		
		
		   
	}  
	  
  public static void main (String[] args) {
	  
	     String UserName = "hescobedo@hotmail.com";
	     String Password = "Beanbag01$";
		 String IntegratorKey = "f771b51b-9943-475e-9cf6-d827df69488c"; 
		 String sqlUrl = null;
		// final String ClientSecret = "b4dccdbe-232f-46cc-96c5-b2f0f7448f8f";
		 //final String RedirectURI = "https://www.docusign.com/api";
		 String BaseUrl = "https://demo.docusign.net/restapi";
		// final String OAuthBaseUrl = "https://account-d.docusign.com";
		// final String SignTest1File = "C:\\Users\\humberto\\OneDrive\\Documents\\docusign-java-client-master\\docusign-java-client-master\\src\\test\\docs\\SignTest1.pdf";
		// final String SignTest1File = "/resources/config/config.properties";
		 String RequestSigntureFilePath = null;
		 // final String SignTest1File = "/src/test/docs/SignTest1.pdf";
	     String EnvelopeId = "48a37c6f-c484-43b7-b469-ec02f5207114";
	     // final String EnvelopeId = "c0f42f4f-b96d-412a-a674-9ff27c718d47";
	     //74fdd034-fe7f-4353-978d-5df964460711	 
		// final String EnvelopeId = "74fdd034-fe7f-4353-978d-5df964460711";
    // ApiClient apiClient = new ApiClient(BaseUrl);
		  String Document = "test2.pdf";
		  String CompanySigneeName= "Company Signee";
		  String CompanySigneeEmail= "hescobedo@hotmail.com";
		  String CustomerSigneeName= "Customer Signee";
		  String CustomerSigneeEmail= "hescobe@gmail.com";
		  String Status= "Send";
	  docusignMain sendDoc= new docusignMain("Send", "test5.pdf", "Bert Escobedo", "hescobedo@hotmail.com", "Dude Escobedo", "hescobedo@hotmail.com");
		 // docusignMain getDoc = new docusignMain("Retrieve");
		   
		 // docusignMain dMain = 
		//  new docusignMain("Send", Document, CompanySigneeName, CompanySigneeEmail, CustomerSigneeName, CustomerSigneeEmail);
		//  dMain..
		  
		  /*		 
        String AccountId = null;

 
	     Properties prop = new Properties();
	   	InputStream input = null;

	   	try {
//  /resources/config/config.properties
	   		input = new FileInputStream("C:\\Users\\humberto\\OneDrive\\GitRepository\\DocuSign\\src\\resources\\config\\config.properties");

	   		// load a properties file
	   		prop.load(input);

	   		// get the property value and print it out
	   		System.out.println(prop.getProperty("IntegratorKey"));
	   		 IntegratorKey=prop.getProperty("IntegratorKey");
	   		System.out.println(prop.getProperty("UserName"));
	   		UserName = prop.getProperty("UserName");
	   		System.out.println(prop.getProperty("Password"));
	   		Password =prop.getProperty("Password");
	   		System.out.println(prop.getProperty("BaseUrl"));
	   		BaseUrl = prop.getProperty("BaseUrl");
	   		System.out.println(prop.getProperty("RequestSignatureFilePath"));
	   		RequestSigntureFilePath = prop.getProperty("RequestSignatureFilePath");
	   		System.out.println(prop.getProperty("SqliteUrl"));
	   		sqlUrl = prop.getProperty("SqliteUrl");
	   		
	   	} catch (IOException ex) {
	   		ex.printStackTrace();
	   	} finally {
	   		if (input != null) {
	   			try {
	   				input.close();
	   			} catch (IOException e) {
	   				e.printStackTrace();
	   			}
	   		}
	   	}
	  
	  
	  if (Status == "Send"){
	  
 
		  Sqlite sql = new Sqlite();
		  Connection sqlConn = sql.connect(sqlUrl);
		  boolean DocuSignInsert = sql.insertNew(Document, Status, CompanySigneeName, CompanySigneeEmail, CustomerSigneeName, CustomerSigneeEmail, sqlConn);;
		
		  if (DocuSignInsert == true){
			    
			    String PathDocument = RequestSigntureFilePath + Document;
			    
			    DocSign sigrequest = new DocSign();
			    
				sigrequest.RequestASignature(PathDocument,CompanySigneeName, CompanySigneeEmail, CustomerSigneeName, CustomerSigneeEmail,UserName, Password, IntegratorKey,  BaseUrl);
				EnvelopeId = sigrequest.EnvelopeId;
				String NewStatus = sigrequest.Status;
			
				boolean SqlUpdate= sql.update(EnvelopeId, Status, Document,NewStatus, sql.connect(sqlUrl));
				
				
		        if (SqlUpdate = false){
		        	
		        	System.out.println("Cound not Update");
		        }
		        else{
		        	System.out.println("Updated");
		        }
		  }
		  else{
			  System.out.println("Cound not insert");
			  
		  }
		  
		  
		  
		  
	  }
	  else 
	  {
		  
		  
		  
	  }*/
		  
  }
   	
    /*	ApiClient apiClient = new ApiClient(BaseUrl);

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
		}*/
		
	
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
		
		
		
	

  
  private Connection connect() {
      // SQLite connection string
      String url = "jdbc:sqlite:C://sqlite/db/test.db";
      Connection conn = null;
      try {
          conn = DriverManager.getConnection(url);
      } catch (SQLException e) {
          System.out.println(e.getMessage());
      }
      return conn;
  }
  
  public boolean insert(String Document,String CustomerSignee,String CompanySignee,String Status,String CompanySigneeName,String CompanySigneeEmail,String CustomerSigneeName,String CustomerSigneeEmail) {
	  Date date = new Date();
	 
	  String sql = "INSERT INTO DocuSign(Document,Status,CompanySigneeName,CompanySigneeEmail,CustomerSigneeName,CustomerSigneeEmail,DateCreated) VALUES(?,?,?,?,?,?,?,?,?)";

      try (Connection conn = this.connect();
          PreparedStatement pstmt = conn.prepareStatement(sql)) {
          pstmt.setString(1, Document);
          pstmt.setString(4, Status);
          pstmt.setString(5, CompanySigneeName);
          pstmt.setString(6, CompanySigneeEmail);
          pstmt.setString(6, CustomerSigneeName);
          pstmt.setString(6, CustomerSigneeEmail);
          pstmt.setString(7,date.toString());
          pstmt.executeUpdate();
          return true;
      } catch (SQLException e) {
          System.out.println(e.getMessage());
        return false;
      }
      
      
      
  }

  public void selectAll(){
      String sql = "SELECT Document,Status,CompanySigneeName,CompanySigneeEmail,CustomerSigneeName,CustomerSigneeEmail FROM DocuSign WHERE Status ='Send'";
      
      try (Connection conn = this.connect();
           Statement stmt  = conn.createStatement();
           ResultSet rs    = stmt.executeQuery(sql)){
          
          // loop through the result set
          while (rs.next()) {
        	  
        	  
              System.out.println(rs.getInt("id") +  "\t" + 
                                 rs.getString("name") + "\t" +
                                 rs.getDouble("capacity"));
          }
      } catch (SQLException e) {
          System.out.println(e.getMessage());
      }
  }
  
  
  
  public void update(String EnvelopeId, String Status, String Document) {
	  Date date = new Date();
	  String DateUpdated = date.toString();
	  
      String sql = "UPDATE DocuSign SET EnvelopeId = ? , "
              + "Status = ?, DateUpdated = ?" 
              + "WHERE Document = ? AND EnvelopeId IS NULL";

      try (Connection conn = this.connect();
              PreparedStatement pstmt = conn.prepareStatement(sql)) {
          
          // set the corresponding param
          pstmt.setString(1, EnvelopeId);
          pstmt.setString(2, Status);
          pstmt.setString(3, Document);
          pstmt.setString(4, DateUpdated);
          // update 
          pstmt.executeUpdate();
      } catch (SQLException e) {
          System.out.println(e.getMessage());
      }
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
		signer.setEmail(UserName);
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
			String envelopeId = (envelopeSummary.getEnvelopeId());
			String status =  envelopeSummary.getStatus();
			
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