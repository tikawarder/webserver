package service;

import static jakarta.ws.rs.client.ClientBuilder.newClient;

import jakarta.ws.rs.client.Entity;
import jakarta.ws.rs.core.GenericType;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import model.Person;

import java.time.LocalDate;
import java.util.List;

public class RestClientService {
	private static final String SERVER_URL = "http://database-server:8081/api/persons";

	public static List<Person> getPersons() {
		return newClient()
				.target(SERVER_URL)
				.request(MediaType.APPLICATION_JSON)
				.get(new GenericType<>() {
				});
	}

	public static Response sendPersonToServer(String name, LocalDate date, String city) {
		Person person = Person.builder()
				.name(name)
				.birthDay(date)
				.city(city)
				.build();
		return newClient()
				.target(SERVER_URL)
				.request()
				.accept(MediaType.APPLICATION_JSON)
				.post(Entity.json(person));
	}
}
