package services;

import java.io.IOException;
import java.io.PrintStream;
import java.net.Socket;
import java.util.Map;
import java.util.Scanner;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;



@WebServlet("/FAuth")
public class FAuth extends HttpServlet {
  private static final long serialVersionUID = 1L;

  public FAuth() {
    super();
  }
  
  
  
  ////////////////////////////////////Connect to TCP client and store respose ////////////////////////////////////////////////////////////////////////////
  private String client2server(String requestgeo, String ipaddr, int portnum) throws IOException {	  
	  Socket clientgeo   = new Socket(ipaddr, portnum); // connects to TCP Client 
	  PrintStream reqgeo = new PrintStream(clientgeo.getOutputStream(), true); // request to our outputstream their inputstream
	  Scanner resgeo     = new Scanner(clientgeo.getInputStream());
	  //log.printf("Connected to server %s:%d\n", clientgeo.getInetAddress(), clientgeo.getPort());
	 
	 reqgeo.println(requestgeo);
	 String response = resgeo.nextLine();	 
     //log.println(response);			     
	// log.printf("Closing <%s:%d>\n", clientgeo.getInetAddress(), clientgeo.getLocalPort());
	 clientgeo.close();
	  return response;
  }
///////////////////////////////////////////// end metod //////////////////////////////////////////////////////////////////////////////
  
  
//http://localhost:4413/B/total?a=50000     expected input atm
  
  // do get will do everything. 
  protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
    PrintStream out = new PrintStream(response.getOutputStream());



    Map<String, String[]> parameters = request.getParameterMap();

    if (parameters.containsKey("p1")) {
    

      String username = request.getParameter("p1");
      String password = request.getParameter("p2");

      String requestgeo = username + " " + password;  // this works 
      String res = client2server(requestgeo,"130.63.96.79",38919);  
      String[] point = res.split("\\s+");
      res = point[point.length-1];
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