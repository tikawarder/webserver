package controller;

import javax.servlet.*;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.*;
import java.io.*;
import java.net.URL;
import java.net.URLConnection;

@WebServlet("/fetch-avatar")
public class AvatarFetchServlet extends HttpServlet {
    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws IOException, ServletException {
        // SSRF VULNERABILITY: User-supplied URL is used directly without validation!
        String avatarUrl = request.getParameter("url");
        
        if (avatarUrl == null || avatarUrl.isEmpty()) {
            response.sendError(HttpServletResponse.SC_BAD_REQUEST, "Please provide the URL of your avatar (parameter: url=...)");
            return;
        }

        try {
            // 1. The server ITSELF initiates a request to the provided URL (SSRF danger)
            URL url = new URL(avatarUrl);
            URLConnection connection = url.openConnection();
            
            // 2. Read the network response
            InputStream inputStream = connection.getInputStream();
            BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream));
            StringBuilder content = new StringBuilder();
            String line;
            while ((line = reader.readLine()) != null) {
                content.append(line).append("\n");
            }
            reader.close();
            
            // 3. Return to the client (or it could be saved to the server)
            response.setContentType("text/plain");
            response.getWriter().write("Content of the provided URL:\n\n" + content.toString());
            
        } catch (Exception e) {
            response.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR, "Error downloading external resource: " + e.getMessage());
        }
    }
}
