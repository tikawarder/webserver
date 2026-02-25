package controller;

import javax.servlet.*;
import javax.servlet.annotation.WebFilter;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.io.IOException;

@WebFilter(
        urlPatterns = {"/search", "/search.jsp", "/store", "/form.jsp"},
        dispatcherTypes = {
                DispatcherType.REQUEST,
                DispatcherType.FORWARD,
                DispatcherType.INCLUDE
        })
public class SecurityFilter implements Filter {

    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain)
            throws IOException, ServletException {

        HttpServletRequest httpRequest = (HttpServletRequest) request;
        HttpServletResponse httpResponse = (HttpServletResponse) response;

        if (!isLoggedIn(httpRequest, httpResponse)) {
            return;
        }

        HttpSession session = httpRequest.getSession(false);

        if (!isIDORsafe(httpRequest, httpResponse, session)) {
            return;
        }

        if (!isCSRFsafe(httpRequest, httpResponse, session)) {
            return;
        }

        chain.doFilter(request, response);
    }

    private boolean isLoggedIn(HttpServletRequest httpRequest, HttpServletResponse httpResponse) throws IOException {
        HttpSession session = httpRequest.getSession(false);

        if (session == null || session.getAttribute("loggedInUser") == null) {
            httpResponse.sendRedirect(httpRequest.getContextPath() + "/login.jsp");
            return false;
        }
        return true;
    }

    private boolean isIDORsafe(HttpServletRequest httpRequest, HttpServletResponse httpResponse, HttpSession session) throws IOException {
        String requestURI = httpRequest.getRequestURI();
        String role = (String) session.getAttribute("role");

        if ("guest".equals(role)) {
            if (requestURI.endsWith("/form.jsp") || requestURI.endsWith("/store")) {
                httpResponse.sendError(HttpServletResponse.SC_FORBIDDEN, "Access Denied: Guests cannot access this page.");
                return false;
            }

            if (requestURI.endsWith("/search.jsp")) {
                httpResponse.sendRedirect(httpRequest.getContextPath() + "/search?nameInput=guest");
                return false;
            }

            if (requestURI.endsWith("/search")) {
                String nameInput = httpRequest.getParameter("nameInput");
                if (nameInput == null || !nameInput.equals("guest")) {
                    System.out.println("IDOR attempt blocked for user: " + session.getAttribute("loggedInUser"));
                    httpResponse.sendError(HttpServletResponse.SC_FORBIDDEN, "IDOR Blocked: You can only search for your own data.");
                    return false;
                }
            }
        }
        return true;
    }

    private boolean isCSRFsafe(HttpServletRequest httpRequest, HttpServletResponse httpResponse, HttpSession session) throws IOException {
        if ("POST".equalsIgnoreCase(httpRequest.getMethod())) {
            String sessionToken = (String) session.getAttribute("csrfToken");
            String requestToken = httpRequest.getParameter("_csrf");

            if (sessionToken == null || requestToken == null || !sessionToken.equals(requestToken)) {
                httpResponse.sendError(HttpServletResponse.SC_FORBIDDEN, "CSRF Validation Failed");
                return false;
            }
        }
        return true;
    }

    @Override
    public void init(FilterConfig filterConfig) throws ServletException {}

    @Override
    public void destroy() {}
}