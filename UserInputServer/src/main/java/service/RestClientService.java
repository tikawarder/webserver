package service;

import jakarta.ws.rs.client.Client;
import jakarta.ws.rs.client.ClientBuilder;
import jakarta.ws.rs.client.Entity;
import jakarta.ws.rs.core.GenericType;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import model.Person;

import java.time.LocalDate;
import java.util.List;

public class RestClientService {
	private static final Client client = ClientBuilder.newClient();
	public static final String SERVER_URL = "http://database-server:8081/api/persons";

	public static List<Person> getPersons() {
		return client
				.target(SERVER_URL) // REST endpoint
				.request(MediaType.APPLICATION_JSON)
				.get(new GenericType<List<Person>>() {
				});
	}

	public static Response sendPersonToServer(String name, LocalDate date, String city) {
		Person person = new Person();
		person.setName(name);
		person.setBirthDay(date);
		person.setCity(city);

		Client client = ClientBuilder.newClient();

		return client
				.target(SERVER_URL)
				.request()
				.post(Entity.json(person));
	}
}
