package services;

import controller.RestServerLauncher;
import jakarta.persistence.EntityManager;
import jakarta.persistence.TypedQuery;
import model.Person;

import java.util.List;

public class PersonDao {
	private final EntityManager entityManager = RestServerLauncher.getEntityManagerFactory().createEntityManager();

	public void saveUser(Person person) {
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

	public Person getPersonByName(String name) {
		try {
			TypedQuery<Person> query = entityManager.createQuery("SELECT p FROM Person p WHERE p.name = :name", Person.class);
			query.setParameter("name", name);
			return query.getSingleResult();
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