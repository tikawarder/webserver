import com.sun.net.httpserver.HttpServer;
import com.sun.net.httpserver.HttpHandler;
import com.sun.net.httpserver.HttpExchange;

import java.io.IOException;
import java.io.OutputStream;
import java.net.InetSocketAddress;

public class SimpleHttpServer {
	public static void main(String[] args) throws IOException {
		HttpServer server = HttpServer.create(new InetSocketAddress(8081), 0);
		server.createContext("/", new RootHandler());
		server.setExecutor(null);
		server.start();
		System.out.println("Server started on port 8081");
	}

	static class RootHandler implements HttpHandler {
		@Override
		public void handle(HttpExchange exchange) throws IOException {
			String response = "<html><body><h1>Hello from Java!</h1></body></html>";
			exchange.sendResponseHeaders(200, response.getBytes().length);
			try (OutputStream os = exchange.getResponseBody()) {
				os.write(response.getBytes());
			}
		}
	}
}