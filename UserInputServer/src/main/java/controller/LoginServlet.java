package controller;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.io.IOException;
import java.util.UUID;

@WebServlet("/login")
public class LoginServlet extends HttpServlet {
    private static final String VALID_USERNAME = "user";
    private static final String VALID_PASSWORD = "password";

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        String username = request.getParameter("username");
        String password = request.getParameter("pwd");

        if (isValidCredentials(username, password)) {
            HttpSession oldSession = request.getSession(false);

            if (oldSession != null) {
                oldSession.invalidate();
            }

            HttpSession session = request.getSession(true);

//            String csrfToken = UUID.randomUUID().toString();
//            session.setAttribute("csrfToken", csrfToken);
            session.setAttribute("loggedInUser", username);

            response.sendRedirect(request.getContextPath() + "/search.jsp");
        } else {
            request.setAttribute("errorMessage", "Wrong username or password");
            request.getRequestDispatcher("/login.jsp").forward(request, response);
        }
    }

    private boolean isValidCredentials(String username, String password) {
        return VALID_USERNAME.equals(username) && VALID_PASSWORD.equals(password);
    }
}