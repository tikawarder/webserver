package services;

import controller.RestServerLauncher;
import jakarta.persistence.EntityManager;
import jakarta.persistence.TypedQuery;
import model.Person;

import java.util.List;

public class PersonDao {
	private final EntityManager entityManager = RestServerLauncher.getEntityManagerFactory().createEntityManager();

	public Person saveUser(Person person) {
		try {
			entityManager.getTransaction().begin();
			entityManager.persist(person);
			entityManager.getTransaction().commit();
			return person;
		} catch (Exception ex) {
			entityManager.getTransaction().rollback();
			throw ex;
		} finally {
			entityManager.close();
		}
	}

	public List<Person> getPersonsByName(String name) {
		try {
			String searchPattern = "%" + name + "%";
			TypedQuery<Person> query = entityManager.createQuery("SELECT p FROM Person p WHERE p.name LIKE :namePattern", Person.class);
			query.setParameter("namePattern", searchPattern);
			return query.getResultList();
		} finally {
			entityManager.close();
		}
	}

	public List<Person> listUsers() {
		try {
			TypedQuery<Person> query = entityManager.createQuery("SELECT p FROM Person p", Person.class);
			return query.getResultList();
		} finally {
			entityManager.close();
		}
	}
}