package controller;

import service.InputSanitizer;

import javax.servlet.*;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.*;
import java.io.IOException;
import java.time.LocalDate;

import service.RestClientService;

import jakarta.ws.rs.core.Response;

@WebServlet("/store")
public class StoreServlet extends HttpServlet {
	@Override
	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws IOException, ServletException {
		String name =  InputSanitizer.sanitize(request.getParameter("name"));
		String stringLocalDate = InputSanitizer.sanitize(request.getParameter("birthdate"));
		String city =  InputSanitizer.sanitize(request.getParameter("city"));

		LocalDate date = LocalDate.parse(stringLocalDate);

		//this response can be used for checking the Rest communication
		Response clientResponse = RestClientService.sendPersonToServer(name, date, city);

		request.getRequestDispatcher("report.jsp").forward(request, response);
	}
}