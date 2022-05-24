package services;


import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintStream;
import java.net.InetAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

public class Geo2 extends Thread{
	private static PrintStream log = System.out;

	  private Socket client;
	  private Geo2(Socket client) {
		  this.client = client;
	  }
	
	 private static String client2server(String requestgeo, String ipaddr, int portnum) throws IOException {	  
		  Socket clientgeo   = new Socket(ipaddr, portnum); // connects to geo
		  PrintStream reqgeo = new PrintStream(clientgeo.getOutputStream(), true); // request to our outputstream their inputstream
		  Scanner resgeo     = new Scanner(clientgeo.getInputStream());
		  log.printf("Connected to server %s:%d\n", clientgeo.getInetAddress(), clientgeo.getPort());
		 
		 reqgeo.println(requestgeo);
		 String response = resgeo.nextLine();	 
	     log.println(response);			     
		 log.printf("Closing <%s:%d>\n", clientgeo.getInetAddress(), clientgeo.getLocalPort());
		 clientgeo.close();
		  return response;
	  }


	public void run() {
		// give ipaddr and port of geo to void main

		try {
			
			// basic stuff to open socket client passage
		      Socket client   = this.client; // Makes sure that client is closed at end of try-statement.
		      Scanner req     = new Scanner(client.getInputStream()); // client request
		      PrintStream res = new PrintStream(client.getOutputStream(), true); // server response
		      String response = null;

		    //make it telnet 
			res.print("Enter your request, then press <Enter>: ");
			String request = req.nextLine();
			String[] point = request.split("\\s+");
		
			
		 //if 3 arguemnats are provided 
			 if (point.length == 3) {
				 // we have 1 cordinate and cookie at the end 
				 ArrayList<String> points = new ArrayList<String>();
				 String file_name = point[2];				 
				 Scanner inFile1 = new Scanner(new File(file_name)).useDelimiter("\\s");				 
				 while(inFile1.hasNext()) {
					points.add(inFile1.next());					 
				 }
				 	
				 // at this point we got all 4 cordinates
				 String requestgeo;
				 requestgeo = points.get(0) + " " + points.get(1) + " " + point[0] + " "+ point[1];
				 res.println(requestgeo);
				 
				 
				 // add the connection to geo here 
	
		         response = client2server(requestgeo,"130.63.96.79",38909);    

				 
			 }
			 // if 2 arguments are provided store them in to a file (cookie)
			 else {
				 // store the value in some cookie, txt file 
				 
				  BufferedWriter outputWriter = new BufferedWriter(new FileWriter("Geo2cookie.txt"));
				 // write cordinates to Geo2cookie file 
				  for (int i = 0; i < point.length; i++) {
					  if (i == 1) {
						  outputWriter.write(point[i]);
					  }else
					  {
					    outputWriter.write(point[i]+" ");
					  }
				  }
				  
				 outputWriter.flush();  
				 outputWriter.close();
				 response = ("Geo2cookie.txt");
			     
			     
				 
			 }
			 res.println(response);			     
			 res.printf("Closing <%s:%d>\n", client.getInetAddress(), client.getLocalPort());
			 client.close();
			 
			 
		} catch (Exception e) {
			log.println(e);
		} finally {
			log.println("Client connection closed.");
		}
	}
	
	 public static void main(String[] args) throws Exception {
			// TODO Auto-generated method stub
			 int port = 38939;
			    InetAddress host = InetAddress.getLocalHost(); // .getLoopbackAddress();
			    try (ServerSocket server = new ServerSocket(port, 0, host)) {
			      log.printf("Server listening on %s:%d\n", server.getInetAddress(), server.getLocalPort());
			      while (true) {
			        Socket client = server.accept();
			        (new Geo2(client)).start();

			      }
			  }

		 }
	
	
	
}

