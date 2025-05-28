package model;

import jakarta.persistence.EntityManagerFactory;
import jakarta.persistence.Persistence;

public class JPAUtil {
	private static final EntityManagerFactory emf = Persistence.createEntityManagerFactory("MysqlPersistence");

	public static EntityManagerFactory getEntityManagerFactory() {
		return emf;
	}

	public static void close() {
		emf.close();
	}
}
