
import java.sql.Connection;

import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import org.sqlite.*;
//import com.docusign.esign.model.Date;
import java.util.Date;;

public class Sqlite {

	 public Connection connect(String sqlurl) {

		 String url = sqlurl;
	      Connection conn = null;
	      try {
	          conn = DriverManager.getConnection(url);
	      } catch (SQLException e) {
	          System.out.println(e.getMessage());
	      }
	      return conn;
	  }
	 
	  public boolean insertNew(String Document, String Status,String CompanySigneeName,String CompanySigneeEmail,String CustomerSigneeName,String CustomerSigneeEmail,Connection sqlconn) {
		
		  Date date = new Date();
		 
		  String sql = "INSERT INTO DocuSign(Document,Status,CompanySigneeName,CompanySigneeEmail,CustomerSigneeName,CustomerSigneeEmail,DateCreated) VALUES(?,?,?,?,?,?,?)";

	      try (Connection conn = sqlconn;
	          PreparedStatement pstmt = conn.prepareStatement(sql)) {
	          pstmt.setString(1, Document);
	          pstmt.setString(2, Status);
	          pstmt.setString(3, CompanySigneeName);
	          pstmt.setString(4, CompanySigneeEmail);
	          pstmt.setString(5, CustomerSigneeName);
	          pstmt.setString(6, CustomerSigneeEmail);
	          pstmt.setString(7,date.toString());
	          pstmt.executeUpdate();
	        //  conn.close();
	          return true;
	      } catch (SQLException e) {
                                                                                                                                                                                                                                                            System.out.println(e.getMessage());
	        return false;
	      }
	      finally{
	    	  
	      }
	      }
	      
	      
	      public boolean update(String EnvelopeId, String Status, String Document,String NewStatus, Connection sqlconn) {
	    	  Date date = new Date();
	    	  String DateUpdated = date.toString();
	    	  
	          String sql = "UPDATE DocuSign SET EnvelopeId = ? , "
	                  + "Status = ?, DateUpdated = ?" 
	                  + "WHERE Document = ? AND Status= ? AND EnvelopeId IS NULL";

	          try (Connection conn = sqlconn;
	                  PreparedStatement pstmt = conn.prepareStatement(sql)) {
	              
	              // set the corresponding param
	              pstmt.setString(1, EnvelopeId);
	              pstmt.setString(2, NewStatus);
	              pstmt.setString(3, DateUpdated);
	              pstmt.setString(4, Document);
	              pstmt.setString(5, Status);
	              // update 
	              pstmt.executeUpdate();
	              //conn.close();
	              return true;
	          } catch (SQLException e) {
	              System.out.println(e.getMessage());
	              return false;
	          } 
	      
	        
	          
	      
	      
	      
	  }
	
	
	
	
	
}
