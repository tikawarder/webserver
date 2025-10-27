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
			String unsafeName = "%" + name + "%";
			String unsafeJpql = "SELECT p FROM Person p WHERE p.name LIKE '" + unsafeName + "'";
			TypedQuery<Person> query = entityManager.createQuery(unsafeJpql, Person.class);
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