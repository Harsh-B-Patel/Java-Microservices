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
import java.io.ByteArrayOutputStream;


import com.google.gson.Gson;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.Marshaller;



public class Quote extends Thread{
	
	  private static PrintStream log = System.out;
	  private Socket client;
	  private Quote(Socket client) {
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
		      String response = null;
		      
		      // prof said not needed. Too Bad!
		      res.print("Enter ID and Format(json or xml)");
		      String idf = req.nextLine();  		      
		      
		      // devide input by space into a string array. 
		      String[] point = idf.split("\\s+"); // same as \s+, devide string at space
		      String id = point[0];
		      String format = point[1];
		      
		 
		      // link derby database below
		      String url   = "jdbc:derby://localhost:64413/EECS"; // all same to sql except url
		      Connection connection = DriverManager.getConnection(url);
		      log.printf("Connected to database: %s\n", connection.getMetaData().getURL());
		      
		      String query = "SELECT * FROM Hr.Product WHERE id = ?";
		      
		      PreparedStatement statement = connection.prepareStatement(query);
		      statement.setString(1, id); 
		      
		      ResultSet rs = statement.executeQuery();
		      
		      // decide if entered format is JSON or XML
		      boolean check = false;
		      Product product = new Product();
		      if ( rs.next()) {
		    	  check = true;
		    	  product.setID(rs.getString("id"));
		    	  product.setName(rs.getString("name"));
		    	  product.setPrice(rs.getDouble("cost"));
		    	  
		      }
		            
		      if (format.equals("json")) {
		    	  Gson gson = new Gson();
		    	  response = gson.toJson(product);
		    	  res.println(response);
		    	  
		    	  
		      }
		      
		      if (format.equals("xml")) {
		    	  ByteArrayOutputStream baos = new ByteArrayOutputStream();
		          JAXBContext context = JAXBContext.newInstance(Product.class);
		          Marshaller m = context.createMarshaller();
		          m.setProperty(Marshaller.JAXB_FORMATTED_OUTPUT, Boolean.TRUE);
		          m.marshal(product, baos);
		          res.println(baos);
		      
		          }	      
		     
		      log.printf("Closing <%s:%d>\n", client.getInetAddress(), client.getLocalPort());
		      client.close();
		      
		    }
		    catch(Exception e) {
		    	e.printStackTrace();
		    }
	  }
		 public static void main(String[] args) throws Exception {
				// TODO Auto-generated method stub
				 int port = 38929;
				    InetAddress host = InetAddress.getLocalHost(); // .getLoopbackAddress();
				    try (ServerSocket server = new ServerSocket(port, 0, host)) {
				      log.printf("Server listening on %s:%d\n", server.getInetAddress(), server.getLocalPort());
				      while (true) {
				        Socket client = server.accept();
				        (new Quote(client)).start();

				      }
				  }
			 }
}
