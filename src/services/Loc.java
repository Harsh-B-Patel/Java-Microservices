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



@WebServlet("/loc")
public class Loc extends HttpServlet {
	private static final long serialVersionUID = 1L;

	public Loc() {
		super();
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

	//http://localhost:4413/B/loc?location=4700+Keele+Street+Toronto     expected input atm

	// do get will do everything. 
	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		PrintStream out = new PrintStream(response.getOutputStream());



		Map<String, String[]> parameters = request.getParameterMap();

		if (parameters.containsKey("location")) {


			String address = request.getParameter("location");

			address = address.replace(' ', '+');
			String payload = "";
			try {
				String key = getServletContext().getInitParameter("MapQuestAPIKey");
				payload = getAddress(address, key);
			} 
			catch (Exception e) {
				e.printStackTrace();
			}  // get response from API

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

			String res = "{ \"lat\": " + lat + ", \"lng\": " + lon + " }";

			if (39.390897 == Double.parseDouble(lat) && -99.066067 == Double.parseDouble(lon) ){
				res = "{ \"lat\": null, \"lng\": null }";
			}

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