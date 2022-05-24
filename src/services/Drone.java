package services;

import java.io.IOException;
import java.io.PrintStream;
import java.net.Socket;
import java.net.URL;
import java.util.Map;
import java.util.Scanner;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import com.google.gson.JsonObject;
import com.google.gson.JsonParser;



@WebServlet("/drone")
public class Drone extends HttpServlet {
	private static final long serialVersionUID = 1L;

	public Drone() {
		super();
	}

	
	private String client2server(String requestgeo, String ipaddr, int portnum) throws IOException {	  
		Socket clientgeo   = new Socket(ipaddr, portnum); // connects to TCP Client 
		PrintStream reqgeo = new PrintStream(clientgeo.getOutputStream(), true); // request to our outputstream their inputstream
		Scanner resgeo     = new Scanner(clientgeo.getInputStream());
		//log.printf("Connected to server %s:%d\n", clientgeo.getInetAddress(), clientgeo.getPort());

		reqgeo.println(requestgeo);
		String response = resgeo.nextLine();	 
		//log.println(response);			     
		//log.printf("Closing <%s:%d>\n", clientgeo.getInetAddress(), clientgeo.getLocalPort());
		clientgeo.close();
		return response;
	}

	public static String getAddress(String response, String key) throws Exception {
		/*
		 * Send request to api 
		 * return its response as a String
		 */

		URL url = new URL("https://www.mapquestapi.com/geocoding/v1/address?key="+key+ "&location=" + response + "&outFormat=json");
		Scanner http = new Scanner(url.openStream());
		String payload = "";
		while(http.hasNext()) {
			payload += http.nextLine();
		}
		return payload;
	}

	//http://localhost:4413/B/drone?location1=4700+Keele+Street+Toronto&location2=40+Tuxedo+Court+Toronto   expected input atm

	// do get will do everything. 
	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		PrintStream out = new PrintStream(response.getOutputStream());



		Map<String, String[]> parameters = request.getParameterMap();

		if (parameters.containsKey("location1")) {


			String address1 = request.getParameter("location1");
			String payload = "";
			address1 = address1.replace(' ', '+');
			String key = getServletContext().getInitParameter("MapQuestAPIKey");
			
			try {
				payload = getAddress(address1, key);
			} catch (Exception e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
			}  // get response from API
			
			String address2 = request.getParameter("location2");
			String payload2 = "";
			address2 = address2.replace(' ', '+');
			
			try {
				payload2 = getAddress(address2, key);
			} catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}  // get response from API

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

			String res = "The estimated delivery time is: "+ time +" minutes.";

			out.println(res);

		}

		else {
			response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
		}
	}

	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		doGet(request, response);
	}
}