package services;

import jakarta.ws.rs.Consumes;
import jakarta.ws.rs.GET;
import jakarta.ws.rs.POST;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
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
	@Produces(MediaType.APPLICATION_JSON)
	public Response add(Person person) {
		System.out.println(person.toString());
		Person savedPerson = personDao.saveUser(person);
		return Response.status(Response.Status.CREATED)
				.entity(savedPerson)
				.build();
	}
}