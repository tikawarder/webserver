package controller;

import javax.servlet.*;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.*;
import java.io.*;
import java.net.URL;
import java.net.URLConnection;

import java.net.HttpURLConnection;
import java.net.InetAddress;

@WebServlet("/fetch-avatar")
public class AvatarFetchServlet extends HttpServlet {

    private boolean isSafeUrl(String urlString) {
        try {
            URL url = new URL(urlString);
            String protocol = url.getProtocol();
            
            // Only HTTP and HTTPS are allowed (blocking file://, gopher://, ftp://)
            if (!"http".equalsIgnoreCase(protocol) && !"https".equalsIgnoreCase(protocol)) {
                return false;
            }

            // DNS resolution and IP address validation to protect internal networks
            String host = url.getHost();
            InetAddress address = InetAddress.getByName(host);
            
            // Blocking loopback (127.0.0.1), private networks (10.x.x.x, 192.168.x.x), and local addresses
            if (address.isAnyLocalAddress() || address.isLoopbackAddress() ||
                address.isLinkLocalAddress() || address.isSiteLocalAddress() ||
                address.isMulticastAddress()) {
                return false;
            }
            
            // Blocking metadata IP addresses of cloud providers (AWS/GCP/Azure)
            if (address.getHostAddress().equals("169.254.169.254")) {
                return false;
            }

            return true;
        } catch (Exception e) {
            return false; // If it cannot be parsed or resolved (DNS error), drop it
        }
    }

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws IOException, ServletException {
        String avatarUrl = request.getParameter("url");
        
        if (avatarUrl == null || avatarUrl.isEmpty()) {
            response.sendError(HttpServletResponse.SC_BAD_REQUEST, "Please provide the URL of your avatar (parameter: url=...)");
            return;
        }

        // SSRF PROTECTION: Validate URL before establishing the connection
        if (!isSafeUrl(avatarUrl)) {
            response.sendError(HttpServletResponse.SC_FORBIDDEN, "Access to the requested URL is denied for security reasons.");
            return;
        }

        try {
            URL url = new URL(avatarUrl);
            HttpURLConnection connection = (HttpURLConnection) url.openConnection();
            
            // SSRF PROTECTION 2: Disable redirects to prevent DNS Rebinding attacks
            connection.setInstanceFollowRedirects(false); 
            
            // Set timeouts to prevent Denial of Service attacks
            connection.setConnectTimeout(5000);
            connection.setReadTimeout(5000);
            
            // Check HTTP status code
            int status = connection.getResponseCode();
            if (status != HttpURLConnection.HTTP_OK) {
                response.sendError(HttpServletResponse.SC_BAD_GATEWAY, "Target server returned status: " + status);
                return;
            }
            
            // 2. Read the network response
            InputStream inputStream = connection.getInputStream();
            BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream));
            StringBuilder content = new StringBuilder();
            String line;
            while ((line = reader.readLine()) != null) {
                content.append(line).append("\n");
            }
            reader.close();
            
            // 3. Return to the client
            response.setContentType("text/plain");
            response.getWriter().write("Content of the provided URL:\n\n" + content.toString());
            
        } catch (Exception e) {
            response.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR, "Error downloading external resource: " + e.getMessage());
        }
    }
}
