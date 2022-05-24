package services;

import java.io.BufferedReader;
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

public class Loc1 extends Thread{

	public static PrintStream log = System.out;
	private Socket client;
	
	public Loc1(Socket client) {
		this.client = client;
	}
	
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

	public void run() {
		try {
			log.printf("Client Added with info @ %s %d\n",client.getInetAddress(), client.getLocalPort());
			Scanner in = new Scanner(client.getInputStream());
			PrintStream out = new PrintStream(client.getOutputStream());
			out.println("Enter the address: ");
			String address = in.nextLine();
			String payload = "";
			address = address.replace(' ', '+');
			payload = getAddress(address);  // get response from API
			
			// Parse it into JSON
			JsonParser parser = new JsonParser();
			/*
			 * For arrays in Json do asjsonarray().get(0)
			 * for object do jsonobjects()
			 * 
			 */
			JsonObject latling = parser.parse(payload)
					.getAsJsonObject().getAsJsonArray("results").get(0)
				    .getAsJsonObject().getAsJsonArray("locations").get(0)
				    .getAsJsonObject().getAsJsonObject("latLng");
			
			String lat = latling.get("lat").getAsString();
			String lon = latling.get("lng").getAsString();
			
			String response = "{ \"lat\": " + lat + ", \"lng\": " + lon + " }";
			
			if (39.390897 == Double.parseDouble(lat) && -99.066067 == Double.parseDouble(lon) ){
				 response = "{ \"lat\": null, \"lng\": null }";
			}
			
			out.println(response);
			client.close();
	
			
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	
	public static void main(String[] args) throws Exception {
		// TODO Auto-generated method stub
		int port = 46882;
		InetAddress ip = InetAddress.getLocalHost();
		ServerSocket server = new ServerSocket(port, 0, ip);
		log.printf("Loc Server listening @ %s %d\n",server.getInetAddress(), server.getLocalPort() );
		while(true) {
			Socket client = server.accept();
			new Loc1(client).start();
		}
	}
}
