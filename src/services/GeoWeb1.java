package services;


/*
 * Class cordinates will store and the cordinates between stessions 
 */
import java.io.PrintStream;
import java.net.URL;
import java.util.Scanner;


/*
 * Parse URL to get username and password from it
 * acts as TCP client to Auth and print back to browser 
 * similar to Gateway in Project A
 */


import java.io.IOException;
import java.io.PrintStream;
import java.net.InetAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.URLDecoder;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.Map;
import java.util.HashMap;
import java.util.List;
import java.util.Scanner;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import model.cordinates;



public class GeoWeb1 extends Thread {

  private static final PrintStream log = System.out;
  private static final Map<Integer, String> httpResponseCodes = new HashMap<>();
  private static final Pattern isDouble = Pattern.compile("^[+-]?([0-9]+)([.][0-9]+)?(E[+-]?[0-9]+)?$");
  private static final String[] redirectedEndpoints = {
    "/add",
    "/subtract",
    "/multiply",
    "/divide",
    "/exponent"
  };

  static {
    httpResponseCodes.put(100, "HTTP CONTINUE");
    httpResponseCodes.put(101, "SWITCHING PROTOCOLS");
    httpResponseCodes.put(200, "OK");
    httpResponseCodes.put(201, "CREATED");
    httpResponseCodes.put(202, "ACCEPTED");
    httpResponseCodes.put(203, "NON AUTHORITATIVE INFORMATION");
    httpResponseCodes.put(204, "NO CONTENT");
    httpResponseCodes.put(205, "RESET CONTENT");
    httpResponseCodes.put(206, "PARTIAL CONTENT");
    httpResponseCodes.put(300, "MULTIPLE CHOICES");
    httpResponseCodes.put(301, "MOVED PERMANENTLY");
    httpResponseCodes.put(302, "MOVED TEMPORARILY");
    httpResponseCodes.put(303, "SEE OTHER");
    httpResponseCodes.put(304, "NOT MODIFIED");
    httpResponseCodes.put(305, "USE PROXY");
    httpResponseCodes.put(400, "BAD REQUEST");
    httpResponseCodes.put(401, "UNAUTHORIZED");
    httpResponseCodes.put(402, "PAYMENT REQUIRED");
    httpResponseCodes.put(403, "FORBIDDEN");
    httpResponseCodes.put(404, "NOT FOUND");
    httpResponseCodes.put(405, "METHOD NOT ALLOWED");
    httpResponseCodes.put(406, "NOT ACCEPTABLE");
    httpResponseCodes.put(407, "PROXY AUTHENTICATION REQUIRED");
    httpResponseCodes.put(408, "REQUEST TIME OUT");
    httpResponseCodes.put(409, "CONFLICT");
    httpResponseCodes.put(410, "GONE");
    httpResponseCodes.put(411, "LENGTH REQUIRED");
    httpResponseCodes.put(412, "PRECONDITION FAILED");
    httpResponseCodes.put(413, "REQUEST ENTITY TOO LARGE");
    httpResponseCodes.put(414, "REQUEST URI TOO LARGE");
    httpResponseCodes.put(415, "UNSUPPORTED MEDIA TYPE");
    httpResponseCodes.put(500, "INTERNAL SERVER ERROR");
    httpResponseCodes.put(501, "NOT IMPLEMENTED");
    httpResponseCodes.put(502, "BAD GATEWAY");
    httpResponseCodes.put(503, "SERVICE UNAVAILABLE");
    httpResponseCodes.put(504, "GATEWAY TIME OUT");
    httpResponseCodes.put(505, "HTTP VERSION NOT SUPPORTED");
  }

  private Socket client;

  private GeoWeb1(Socket client) {
    this.client = client;
  }

  private void sendHeaders(PrintStream res, int code, String contentType, String response) {
    sendHeaders(res, code, contentType, response, new String[]{});
  }
  private void sendHeaders(PrintStream res, int code, String contentType, String response, String[] headers) {
    // send HTTP Headers
    res.printf("HTTP/1.1 %d %s\n", code, httpResponseCodes.get(code));
    res.println("Server: Java HTTP Server : 1.0");
    res.println("Date: " + new Date());
    res.println("Content-type: " + contentType);
    res.println("Content-length: " + response.getBytes().length);
    Arrays.stream(headers).forEach(h -> res.println(h));
    res.println(); // blank line between headers and content, very important !
  }

  // seperates them all at & sign 
  // requires input after ? in http://host:port/SRV?p1=v1&p2=v2.. url
  private Map<String, String> getQueryStrings(String qs) throws Exception {
    Map<String, String> queries = new HashMap<>();
    String[] fields = qs.split("&");
    
    for (String field : fields) {
      String[] pairs = field.split("=", 2);
      if (pairs.length == 2) {
        queries.put(pairs[0], URLDecoder.decode(pairs[1], "UTF-8"));
      }
    }

    return queries;
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
///////////////////////////////////////////// end metod //////////////////////////////////////////////////////////////////////////////
  public void run() {
    final String clientAddress = String.format("%s:%d", client.getInetAddress(), client.getPort());
    log.printf("Connected to %s\n", clientAddress);

    try (
      Socket client   = this.client; // Makes sure that client is closed at end of try-statement.
      Scanner req     = new Scanner(client.getInputStream());
      PrintStream res = new PrintStream(client.getOutputStream(), true);
    ) {
      String request = req.nextLine();
      String method, resource, version;
      String response = "";

      List<String> headers = new ArrayList<>();
      
      try (Scanner parse = new Scanner(request)) {
        method   = parse.next();
        resource = parse.next();
        version  = parse.next(); 
      }

      int status = 200;
///////////////////////////////start stuff thats supposed to be changed /////////////////////////////////////////////////////////////
      try {
    	  if (cordinates.length() > 0) {
          	  response = resource;
              Map<String, String> qs = getQueryStrings(resource.substring(resource.indexOf('?') + 1));
              double lat1 = Double.parseDouble(qs.get("p1"));
              double lon1 = Double.parseDouble(qs.get("p2"));
              
              // get previous cordinates from list
              String ar[];
              ar = cordinates.get_last_on_map();
              
              // send it to geo 
              String requestgeo = lat1 + " " + lon1 + " " + ar[0] + " "+ ar[1]; 
              response = client2server(requestgeo,"130.63.96.79",38909);    
          }
    	  else if (cordinates.length() == 0) {
    		  
    		  // add to cidinates the 2 paratemeter 
    		  response = resource;
              Map<String, String> qs = getQueryStrings(resource.substring(resource.indexOf('?') + 1));
              String lat1 = qs.get("p1");
              String lon1 = qs.get("p2");
              
              cordinates.add_map(lat1, lon1);
              response = "RECEIVED";
    	  }
        else {
          status = 404;
        }
    	  
    	  
        res.println(response);
        client.close();
        
        //////////////////////////////end to do work//////////////////////////////////////////////////////////////////
      } catch (Exception e) {
        log.println(e);
        e.printStackTrace(log);
        status = 500;
      }

      if (status != 200 && response.isEmpty()) {
        response = httpResponseCodes.get(status);
      }

      if (headers.size() > 0) {
        sendHeaders(res, status, "text/plain", response, headers.toArray(new String[]{}));
      } else {
        sendHeaders(res, status, "text/plain", response);
      }

      res.println(response);
      res.flush(); // flush character output stream buffer      
    } catch (Exception e) {
      log.println(e);
    } finally {
      log.printf("Disconnected from %s\n", clientAddress);
    }
  }

  public static void main(String[] args) throws Exception {
    int port = 0;
    InetAddress host = InetAddress.getLocalHost(); // .getLoopbackAddress();
    try (ServerSocket server = new ServerSocket(port, 0, host)) {
      log.printf("Server listening on %s:%d\n", server.getInetAddress(), server.getLocalPort());
      while (true) {
        Socket client = server.accept();
        (new GeoWeb1(client)).start();
      }
    }
  }
}