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
public class AuthenticationFilter implements Filter {

    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain)
            throws IOException, ServletException {

        HttpServletRequest httpRequest = (HttpServletRequest) request;
        HttpServletResponse httpResponse = (HttpServletResponse) response;
        HttpSession session = httpRequest.getSession(false);

        boolean isLoggedIn = (session != null && session.getAttribute("loggedInUser") != null);

        if (!isLoggedIn) {
            httpResponse.sendRedirect(httpRequest.getContextPath() + "/login.jsp");
            return;
        }

        if ("POST".equalsIgnoreCase(httpRequest.getMethod())) {

            String sessionToken = (String) session.getAttribute("csrfToken");
            String requestToken = httpRequest.getParameter("_csrf");

            if (sessionToken == null || requestToken == null || !sessionToken.equals(requestToken)) {
                httpResponse.sendError(HttpServletResponse.SC_FORBIDDEN, "CSRF Validation Failed");
                return;
            }
        }
        chain.doFilter(request, response);
    }

    @Override
    public void init(FilterConfig filterConfig) throws ServletException {}

    @Override
    public void destroy() {}
}