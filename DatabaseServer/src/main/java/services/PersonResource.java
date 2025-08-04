package services;

import jakarta.ws.rs.Consumes;
import jakarta.ws.rs.GET;
import jakarta.ws.rs.POST;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.core.MediaType;
import model.Person;

import java.util.List;

@Path("/persons")
public class PersonResource {
	private final PersonDao personDao = new PersonDao();

	@GET
	@Produces(MediaType.APPLICATION_JSON)
	public List<Person> getAll() {
		return personDao.listUsers();
	}

	@POST
	@Consumes(MediaType.APPLICATION_JSON)
	public void add(Person person) {
		System.out.println(person.toString());
		personDao.saveUser(person);
	}
}