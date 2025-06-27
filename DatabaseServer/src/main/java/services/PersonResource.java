package services;

import jakarta.persistence.EntityManagerFactory;
import jakarta.persistence.Persistence;
import jakarta.ws.rs.*;
import jakarta.ws.rs.core.MediaType;
import model.Person;

import java.util.List;

@Path("/persons")
public class PersonResource {
	private EntityManagerFactory emf = Persistence.createEntityManagerFactory("MysqlPersistence");
	private final PersonDao personDao = new PersonDao(emf);

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