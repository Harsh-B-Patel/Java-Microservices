package services;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintStream;
import java.net.InetAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.URL;
import java.net.URLConnection;
import java.util.Scanner;

import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;

import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

public class Drone1 extends Thread{

	public static PrintStream log = System.out;
	private Socket client;

	public Drone1(Socket client) {
		this.client = client;
	}

	////////////////////////////////////Connect to TCP client and store respose ////////////////////////////////////////////////////////////////////////////
	private String client2server(String requestgeo, String ipaddr, int portnum) throws IOException {	  
		Socket clientgeo   = new Socket(ipaddr, portnum); // connects to TCP Client 
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
	///////////////////////////////////////////// end method //////////////////////////////////////////////////////////

	////////////////////////////// API Request method /////////////////////////////////////////////////////////

	public static String getAddress(String response) throws Exception {
		/*
		 * Send request to api 
		 * return its response as a String
		 */
		URL url = new URL("https://www.mapquestapi.com/geocoding/v1/address?key=nfL61jlKdCKN38BUuQBrzytGw3sggVvI&location=" + response + "&outFormat=json");
		Scanner http = new Scanner(url.openStream());
		String payload = "";
		while(http.hasNext()) {
			payload += http.nextLine();
		}
		return payload;

	}
	///////////////////////////////////////////// end method //////////////////////////////////////////////////////////

	public void run() {
		try {
			log.printf("Client Added with info @ %s %d\n",client.getInetAddress(), client.getLocalPort());
			Scanner in = new Scanner(client.getInputStream());
			PrintStream out = new PrintStream(client.getOutputStream());
			String response = "";

			//// get source and destination and API them /////////////////////////

			out.println("Enter the Source address: ");
			String address = in.nextLine();
			String payload = "";
			address = address.replace(' ', '+');
			payload = getAddress(address);  // get response from API

			out.println("Enter the Destination address: ");
			String address2 = in.nextLine();
			String payload2 = "";
			address2 = address2.replace(' ', '+');
			payload2 = getAddress(address);  // get response from API

			///////////////////parse lat lon from JSon ///////////////////////

			// Parse it into JSON
			JsonParser parser = new JsonParser();
			/*
			 * For arrays in Json do asjsonarray().get(0)
			 * for object do jsonobjects()
			 * 			 */
			JsonObject latling = parser.parse(payload)
					.getAsJsonObject().getAsJsonArray("results").get(0)
					.getAsJsonObject().getAsJsonArray("locations").get(0)
					.getAsJsonObject().getAsJsonObject("latLng");	

			Double s_lat = Double.parseDouble(latling.get("lat").getAsString());
			Double s_lon = Double.parseDouble(latling.get("lng").getAsString());


			JsonParser parser1 = new JsonParser();
			JsonObject latling2 = parser1.parse(payload2)
					.getAsJsonObject().getAsJsonArray("results").get(0)
					.getAsJsonObject().getAsJsonArray("locations").get(0)
					.getAsJsonObject().getAsJsonObject("latLng");	

			Double d_lat = Double.parseDouble(latling2.get("lat").getAsString());
			Double d_lon = Double.parseDouble(latling2.get("lng").getAsString());

			/////////////// finsih parsing all lats and lons  ///////////////////////////////

			//////////// send to geo /////////////////////
			String requestgeo = s_lat + " " + s_lon + " " + d_lat + " "+ d_lon; 
			String Distnace_response = client2server(requestgeo,"130.63.96.79",38909);    

			/////////////// get just value from Geo response ///////////////

			String[] point = Distnace_response.split("\\s+");          
			double distance = Double.parseDouble(point[point.length - 1]);

			/////////////////// calculations for time ////////////////////////////////////////
			double time = (distance / 150) * 60 ;

			response = "The estimated delivery time is: "+ time +" minutes.";
			out.println(response);
			client.close();


		} catch (Exception e) {
			e.printStackTrace();
		}
	}


	public static void main(String[] args) throws Exception {
		// TODO Auto-generated method stub
		int port = 46892;
		InetAddress ip = InetAddress.getLocalHost();
		ServerSocket server = new ServerSocket(port, 0, ip);
		log.printf("Drone Server listening @ %s %d\n",server.getInetAddress(), server.getLocalPort() );
		while(true) {
			Socket client = server.accept();
			new Drone1(client).start();
		}
	}
}
