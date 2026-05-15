package controller;

import model.Person;
import service.RestClientService;

import javax.servlet.RequestDispatcher;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.io.IOException;
import java.util.List;
import java.util.UUID;

@WebServlet("/login")
public class LoginServlet extends HttpServlet {

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        String username = request.getParameter("username");
        String password = request.getParameter("pwd");

        String role = getRoleForCredentials(username, password);

        if (role != null) {
            HttpSession oldSession = request.getSession(false);

            if (oldSession != null) {
                oldSession.invalidate();
            }

            HttpSession session = request.getSession(true);

            setCSRFtoken(session);
            setRole(session, username, role);

            if ("guest".equals(role)) {
                response.sendRedirect(request.getContextPath() + "/search?nameInput=guest");
            } else if ("admin".equals(role)) {
                response.sendRedirect(request.getContextPath() + "/search.jsp");
            }
        } else {
            request.setAttribute("errorMessage", "Wrong username or password");
            request.getRequestDispatcher("/login.jsp").forward(request, response);
        }
    }

    private void setRole(HttpSession session, String username, String role) {
        session.setAttribute("loggedInUser", username);
        session.setAttribute("role", role);

        System.out.println("User logged in: " + username + " with role: " + role);
    }

    private void setCSRFtoken(HttpSession session) {
        String csrfToken = UUID.randomUUID().toString();
        session.setAttribute("csrfToken", csrfToken);
    }

    private String getRoleForCredentials(String username, String password) {
        if ("admin".equals(username) && "admin".equals(password)) {
            return "admin";
        }
        if ("guest".equals(username) && "guest".equals(password)) {
            return "guest";
        }
        return null;
    }
}