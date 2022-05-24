package services;

import java.io.IOException;
import java.io.PrintWriter;
import java.io.Writer;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;


/**
 * Servlet implementation class OAuth
 */
@WebServlet("/OAuth.do")
public class OAuth extends HttpServlet {
	private static final long serialVersionUID = 1L;
	public static final String REDIRECT = "https://www.eecs.yorku.ca/~roumani/servers/auth/oauth.cgi?back=http://localhost:4413/ProjB/OAuth.do";

	protected void doGet(HttpServletRequest request, HttpServletResponse response)throws ServletException, IOException 
	{
		 PrintWriter res = response.getWriter();
		if (request.getParameter("calc") != null) 
		{
			response.sendRedirect(REDIRECT);	
		} 
		else if(request.getParameter("user") != null)
			// sample user=harsh&name=Harsh P&hash=
		{
			String user = request.getParameter("user");
			String name = request.getParameter("name");
			
			res.println("Hello, " + name + ". You are logged in as " + user + ".");
		}else 
		{
			this.getServletContext().getRequestDispatcher("/OAuth.html").forward(request, response);
		}
	}

	protected void doPost(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {
		// TODO Auto-generated method stub
		doGet(request, response);
	}

}