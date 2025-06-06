package controller;

import soapclient.LocalDate;
import soapclient.Person;
import soapclient.DataProcessor;
import soapclient.DataProcessorImplService;
import utils.InputSanitizer;

import javax.servlet.*;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.*;
import java.io.IOException;

@WebServlet("/store")
public class StoreServlet extends HttpServlet {
	@Override
	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws IOException, ServletException {
		String name =  InputSanitizer.sanitize(request.getParameter("name"));
		String stringLocalDate = InputSanitizer.sanitize(request.getParameter("birthdate"));
		String city =  InputSanitizer.sanitize(request.getParameter("city"));

		Person person = new Person();
		person.setName(name);
		person.setBirthDay(new LocalDate(stringLocalDate));
		person.setCity(city);

		DataProcessorImplService service = new DataProcessorImplService();
		DataProcessor port = service.getDataProcessorImplPort();
		port.decodeAndStore(person);

		request.getRequestDispatcher("report.jsp").forward(request, response);
	}
}