package services;

import java.io.IOException;
import java.io.PrintStream;
import java.net.InetAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.Scanner;

public class Geo extends Thread{
	  private static PrintStream log = System.out;

	  private Socket client;
	  private Geo(Socket client) {
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
		      String response;
		      String request = req.nextLine();  // get client request in next line. 
		      
		      String[] point = request.split("\\s+"); // same as \s+, devide string at space
		      
		      // could do if check for correct order of input
		      double lat1 = Double.parseDouble(point[0]) * (Math.PI/180); // Point 1 latitude
		      double lon1 = Double.parseDouble(point[1]) * (Math.PI/180); // Point 1 Longitude
			  double lat2 = Double.parseDouble(point[2]) * (Math.PI/180); // Point 2 Latitude
			  double lon2 = Double.parseDouble(point[3]) * (Math.PI/180); // Point 2 Longitude
			  
			  double x,y, dist;
			  y = Math.cos(lat1) * Math.cos(lat2);
			  x = Math.sin((lat2-lat1)/2) * Math.sin((lat2-lat1)/2) + y * Math.sin((lon2-lon1)/2) * Math.sin((lon2-lon1)/2);
			  dist = Math.atan2(Math.sqrt(x), Math.sqrt(1 - x)) * 12742; 
				
			  response = "The geodesic distance between the points is: " + dist;
			  
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
		 int port = 38909;
		    InetAddress host = InetAddress.getLocalHost(); // .getLoopbackAddress();
		    try (ServerSocket server = new ServerSocket(port, 0, host)) {
		      log.printf("Server listening on %s:%d\n", server.getInetAddress(), server.getLocalPort());
		      while (true) {
		        Socket client = server.accept();
		        (new Geo(client)).start();

		      }
		  }

	 }
}

