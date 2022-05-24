package services;

import java.io.IOException;
import java.io.PrintStream;
import java.io.Writer;
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

import model.cordinates;

@WebServlet("/GeoWeb")
public class GeoWeb extends HttpServlet {
	private static final long serialVersionUID = 1L;

	public GeoWeb() {
		super();
	}

	private String client2server(String requestgeo, String ipaddr, int portnum) throws IOException {
		Socket clientgeo = new Socket(ipaddr, portnum); // connects to TCP Client
		PrintStream reqgeo = new PrintStream(clientgeo.getOutputStream(), true); // request to our outputstream their
		// inputstream
		Scanner resgeo = new Scanner(clientgeo.getInputStream());

		reqgeo.println(requestgeo);
		String response = resgeo.nextLine();
		clientgeo.close();
		return response;
	}

	// do get will do everything.
	protected void doGet(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {
		Writer out = response.getWriter();
		response.setContentType("text/plain");

		HttpSession session = request.getSession(true);

		if (request.getParameter("lat") != null && (session.getAttribute("lat") == null)
				&& (session.getAttribute("lng") == null)) {
			String lat = request.getParameter("lat");
			String lng = request.getParameter("lng");

			session.setAttribute("lat", lat);
			session.setAttribute("lng", lng);

			out.write("RECIEVED");

		} else if (request.getParameter("lat") != null && (session.getAttribute("lat") != null)
				&& (session.getAttribute("lng") != null)) {
			// session has previous values stored ?
			String lat1 = (String) session.getAttribute("lat");
			String lng1 = (String) session.getAttribute("lng");
			String lat2 = request.getParameter("lat");
			String lng2 = request.getParameter("lng");

			// send to geo
			String requestgeo = lat1 + " " + lng1 + " " + lat2 + " " + lng2;
			String geoip=  getServletContext().getInitParameter("GeoServiceIP");
			int geoprt = Integer.parseInt(getServletContext().getInitParameter("GeoServicePort"));
			
			String res = client2server(requestgeo, geoip, geoprt); // use xml file parameters here

			// parse it and only keep last
			String[] point = res.split("\\s+");
			res = point[point.length - 1];
			res = "The distance from (" + lat1 + ", " + lng1 + ") to (" + lat2 + ", " + lng2 + " ) is: " + res + " km";

			out.write(res);

			// set for next time
			session.setAttribute("lat", lat2);
			session.setAttribute("lng", lng2);
		}

		else {
			response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
		}
	}

	protected void doPost(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {
		doGet(request, response);
	}
}