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

		DataDao dao = new DataDao();
		dao.insertUser(name, birthDate, city);

		request.getRequestDispatcher("report.jsp").forward(request, response);
	}
}