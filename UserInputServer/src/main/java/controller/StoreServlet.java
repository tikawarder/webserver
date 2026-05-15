package controller;

import service.InputSanitizer;

import javax.servlet.*;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.*;
import java.io.IOException;
import java.time.LocalDate;

import service.InputSanitizer;
import model.Person;
import service.RestClientService;

import jakarta.ws.rs.core.Response;

@WebServlet("/store")
public class StoreServlet extends HttpServlet {
	@Override
	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws IOException, ServletException {
        HttpSession session = request.getSession(false);

        if (session == null || session.getAttribute("loggedInUser") == null) {
			response.sendRedirect("login.jsp");
			return;
		}

		String sessionToken = (String) session.getAttribute("csrfToken");
		String requestToken = request.getParameter("_csrf");

		if (sessionToken == null || requestToken == null || !sessionToken.equals(requestToken)) {
			System.out.println("CSRF attack happened! IP: " + request.getRemoteAddr());

			response.sendError(HttpServletResponse.SC_FORBIDDEN, "CSRF Validation Failed");
			return;
		}

        String name =  InputSanitizer.sanitize(request.getParameter("name"));
        String stringLocalDate = InputSanitizer.sanitize(request.getParameter("birthdate"));
        String city =  InputSanitizer.sanitize(request.getParameter("city"));

		try {
			LocalDate date = LocalDate.parse(stringLocalDate);
			Response clientResponse = RestClientService.sendPersonToServer(name, date, city);

			if (clientResponse.getStatusInfo().getFamily() == Response.Status.Family.SUCCESSFUL) {
				Person savedPerson = clientResponse.readEntity(Person.class);
				request.setAttribute("person", savedPerson);
			}

			request.getRequestDispatcher("report.jsp").forward(request, response);
		} catch (java.time.format.DateTimeParseException e) {
			System.err.println("Invalid date format provided: " + stringLocalDate);
			response.sendError(HttpServletResponse.SC_BAD_REQUEST, "Invalid date format!");
		} catch (Exception e) {
			System.err.println("Unexpected error in StoreServlet: " + e.getMessage());
			response.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR, "Unexpected internal error occurred!");
		}
	}
}