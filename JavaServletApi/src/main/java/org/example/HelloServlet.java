package org.example;

import jakarta.servlet.*;
import jakarta.servlet.http.*;
import java.io.IOException;
import java.io.PrintWriter;

import jakarta.servlet.annotation.WebServlet;

@WebServlet("/hello")
public class HelloServlet extends HttpServlet {
	protected void doPost(HttpServletRequest req, HttpServletResponse resp)
			throws ServletException, IOException {

		String name = req.getParameter("name");
		String birthdate = req.getParameter("birthdate");

		resp.setContentType("text/html;charset=UTF-8");
		PrintWriter out = resp.getWriter();

		out.println("<html><body>");
		out.println("<h1>Üdv, " + name + "!</h1>");
		out.println("<p>Születési dátumod: " + birthdate + "</p>");
		out.println("</body></html>");
	}
}