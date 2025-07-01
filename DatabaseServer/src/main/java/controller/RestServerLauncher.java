package controller;

import static jakarta.persistence.Persistence.createEntityManagerFactory;
import static java.net.URI.create;
import static org.glassfish.jersey.grizzly2.httpserver.GrizzlyHttpServerFactory.createHttpServer;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import jakarta.persistence.EntityManagerFactory;
import lombok.Getter;
import org.glassfish.grizzly.http.server.HttpServer;
import org.glassfish.jersey.jackson.JacksonFeature;
import org.glassfish.jersey.jackson.internal.jackson.jaxrs.json.JacksonJaxbJsonProvider;
import org.glassfish.jersey.server.ResourceConfig;
import java.net.URI;

import services.RestResource;

public class RestServerLauncher {
	private static final EntityManagerFactory FACTORY = createEntityManagerFactory("MysqlPersistence");
	private static final URI API_URI = create("http://0.0.0.0:8081/api/");
	@Getter
	private static HttpServer server;

	public static void main(String[] args) {
		RestResource resource = new RestResource(new ObjectMapper(), new JavaTimeModule(), SerializationFeature.WRITE_DATES_AS_TIMESTAMPS, new JacksonJaxbJsonProvider());
		ResourceConfig config = resource.getRestProviderConfig( "services", JacksonFeature.class);

		server = createHttpServer(API_URI, config);

		System.out.println("REST server started at " + API_URI);
	}

	public static EntityManagerFactory getEntityManagerFactory() {
		return FACTORY;
	}
}