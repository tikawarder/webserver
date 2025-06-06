package services;

import jakarta.jws.WebMethod;
import jakarta.jws.WebService;
import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityManagerFactory;
import jakarta.persistence.TypedQuery;
import model.Person;

import java.util.List;

@WebService
public class PersonDao {
	private EntityManagerFactory emFactory;

	public PersonDao(EntityManagerFactory emFactory) {
		this.emFactory = emFactory;
	}

	@WebMethod
	public void saveUser(Person person) {
		EntityManager entityManager = emFactory.createEntityManager();
		try {
			entityManager.getTransaction().begin();
			entityManager.persist(person);
			entityManager.getTransaction().commit();
		} catch (Exception ex) {
			entityManager.getTransaction().rollback();
			throw ex;
		} finally {
			entityManager.close();
		}
	}

	@WebMethod
	public List<Person> listUsers() {
		EntityManager entityManager = emFactory.createEntityManager();
		try {
			TypedQuery<Person> query = entityManager.createQuery("SELECT p FROM Person p", Person.class);
			return query.getResultList();
		} finally {
			entityManager.close();
		}
	}

	@WebMethod
	public Person getPersonByName(String name) {
		return new Person();
	}
}