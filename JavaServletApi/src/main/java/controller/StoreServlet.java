package controller;

import utils.ContextListener;
import model.Person;
import model.PersonDao;
import utils.InputSanitizer;

import javax.servlet.*;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.*;
import java.io.IOException;
import java.time.LocalDate;

@WebServlet("/store")
public class StoreServlet extends HttpServlet {
	@Override
	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws IOException, ServletException {
		String name =  InputSanitizer.sanitize(request.getParameter("name"));
		LocalDate birthDate = LocalDate.parse(request.getParameter("birthdate"));
		String city =  InputSanitizer.sanitize(request.getParameter("city"));

		Person person = new Person(name, birthDate, city);
		PersonDao dao = new PersonDao(ContextListener.getEntityManagerFactory());
		dao.saveUser(person);

		request.getRequestDispatcher("report.jsp").forward(request, response);
	}
}