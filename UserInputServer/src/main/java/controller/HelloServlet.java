package controller;

import soapclient.DataProcessor;
import soapclient.DataProcessorImplService;
import soapclient.Person;

import javax.servlet.RequestDispatcher;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import java.io.IOException;
import java.util.List;

@WebServlet("/hello")
public class HelloServlet extends HttpServlet {
	protected void doGet(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {
		DataProcessorImplService service = new DataProcessorImplService();
		DataProcessor port = service.getDataProcessorImplPort();

		List<Person> persons = port.getPersons();

		request.setAttribute("persons", persons);

		RequestDispatcher dispatcher = request.getRequestDispatcher("/list.jsp");
		dispatcher.forward(request, response);
	}
}