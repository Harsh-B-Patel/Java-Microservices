package services;

import java.io.IOException;
import java.io.PrintStream;
import java.net.InetAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.Scanner;
import g.Util;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement; // import java.sql.Statement;
import java.sql.SQLException;
import java.sql.ResultSet;


public class Auth extends Thread{

	  private static PrintStream log = System.out;
	  private Socket client;
	  private Auth(Socket client) {
		  this.client = client;
	  }
	  
	  
	  public void run() {
		  log.printf("Connection from to %s:%d\n", client.getInetAddress(), client.getPort());
		  try 
		    {
		      // basic stuff to open socket client passage
		      Socket client   = this.client; // Makes sure that client is closed at end of try-statement.
		      Scanner req     = new Scanner(client.getInputStream()); // client request
		      PrintStream res = new PrintStream(client.getOutputStream(), true); // server response
		    
		      
		      
		      //log.print("Enter Username and Password");
		      String up = req.nextLine();  // get client request in next line. 
		      
		      
		      String[] point = up.split("\\s+"); // same as \s+, devide string at space
		      String username = point[0];
		      String password = point[1];
		      
		      
		      //connect to sql database		      
		      String home = System.getProperty("user.home");
		      String url  = "jdbc:sqlite:" + home + "/4413/pkg/sqlite/Models_R_US.db";

		      Connection connection = DriverManager.getConnection(url);
		      log.printf("Connected to database: %s\n", connection.getMetaData().getURL());
		      String query = "SELECT * FROM client WHERE name = ?";
		      

		       PreparedStatement statement = connection.prepareStatement(query);
		       statement.setString(1, username); // fils in username value at first ? 
		       
		       // execute query to database and check
		       ResultSet rs = statement.executeQuery();
		       
		       String foundname;
		       int count = 0;
		       String salt;
		       String dtb_hash;
		       String computed_hash;
		       String response = "FAILURE";
		       
		       if ( rs.next()) {
		    	   // check username with database name, compute and compare hash
		    	   foundname = rs.getString("name");
		    	   if ( foundname.equals(username)) {  // not really necessary I think
		    		   count = (rs.getInt("count"));

						salt = (rs.getString("salt"));

						dtb_hash = (rs.getString("hash"));

						computed_hash = g.Util.hash(password, salt, count);
						
						if (computed_hash.equals(dtb_hash)) {
							response = "OK";
						}
		    		   
		    	   }
		    	   
		       }
		     
		       res.println(response);
		       log.printf("Closing <%s:%d>\n", client.getInetAddress(), client.getLocalPort());
		       client.close();
		      
		      
		    }
		    catch(Exception e) {
		    	e.printStackTrace();
		    }
	  }
	
	
	 public static void main(String[] args) throws Exception {
		// TODO Auto-generated method stub
		 int port = 38919;
		    InetAddress host = InetAddress.getLocalHost(); // .getLoopbackAddress();
		    try (ServerSocket server = new ServerSocket(port, 0, host)) {
		      log.printf("Server listening on %s:%d\n", server.getInetAddress(), server.getLocalPort());
		      while (true) {
		        Socket client = server.accept();
		        (new Auth(client)).start();

		      }
		  }

	 }

}
