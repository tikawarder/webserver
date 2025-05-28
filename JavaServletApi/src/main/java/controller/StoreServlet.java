package controller;

import model.JPAUtil;
import model.Person;
import model.PersonDao;

import javax.servlet.*;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.*;
import java.io.IOException;

@WebServlet("/store")
public class StoreServlet extends HttpServlet {
	@Override
	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws IOException, ServletException {
		String name = request.getParameter("name");
		String birthDate = request.getParameter("birthdate");
		String city = request.getParameter("city");

		PersonDao dao = new PersonDao(JPAUtil.getEntityManagerFactory());
		Person person = new Person(name, DateFormatter.toLocalDateFormat(birthDate), city);
		dao.saveUser(person);

		request.getRequestDispatcher("report.jsp").forward(request, response);
	}
}