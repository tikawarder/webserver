package model;

import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityManagerFactory;
import jakarta.persistence.Persistence;

import java.util.List;

public class PersonDao {
	EntityManagerFactory factory = Persistence.createEntityManagerFactory("MysqlPersistence");
	EntityManager entityManager = factory.createEntityManager();

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


	public List listUsers() {
		return entityManager.createQuery("select p from Person p").getResultList();
	}

	public void closeFactory() {
		factory.close();
	}
}