package services;

import java.io.IOException;
import java.io.PrintStream;
import java.util.Map;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;



@WebServlet("/service")    // will decide servlet name in url
public class service extends HttpServlet {
  private static final long serialVersionUID = 1L;

  public service() {
    super();
  }
  
  
//http://localhost:4413/B/total?a=50000     expected input atm
  protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
    PrintStream out = new PrintStream(response.getOutputStream());


    Map<String, String[]> parameters = request.getParameterMap();

    if (parameters.containsKey("a")) {
      String a          = request.getParameter("a");
      out.println(a);
    } else {
      response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
    }
  }

  protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
    doGet(request, response);
  }
}