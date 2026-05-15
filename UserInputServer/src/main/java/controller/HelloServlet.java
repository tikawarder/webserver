package controller;

import javax.servlet.RequestDispatcher;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.Cookie;
import javax.servlet.http.HttpSession;

import model.Person;
import service.RestClientService;

import java.io.IOException;
import java.util.List;

@WebServlet("/hello")
public class HelloServlet extends HttpServlet {
	protected void doGet(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {

		List<Person> persons = RestClientService.getPersons();

		handleSession(request, response);
		setCookies(request, response);

		request.setAttribute("persons", persons);

		RequestDispatcher dispatcher = request.getRequestDispatcher("/list.jsp");
		dispatcher.forward(request, response);
	}

	private Cookie getCookieByName (HttpServletRequest request, String name){
		Cookie[] cookies = request.getCookies();
		Cookie getCookie = null;
		if (cookies != null) {
			for (Cookie cookie : cookies) {
				System.out.println("Cookie: " + cookie.getName() + " " + cookie.getValue());
				if (name.equals(cookie.getName())) {
					getCookie = cookie;
				}
			}
		}
		return getCookie;
	}

	private void setCookies (HttpServletRequest request, HttpServletResponse response){
		Cookie cookie = getCookieByName(request,"JSESSIONID");
		cookie.setHttpOnly(false);
		response.addCookie(cookie);

		Cookie myCookie = new Cookie("My_id", "12345");
		myCookie.setHttpOnly(false);
		response.addCookie(myCookie);
	}

	private void handleSession (HttpServletRequest request, HttpServletResponse response){
		HttpSession session = request.getSession();
		session.setAttribute("username", "tamas");
		System.out.println(session.getId());
		System.out.println(session.getAttributeNames());
	}
}