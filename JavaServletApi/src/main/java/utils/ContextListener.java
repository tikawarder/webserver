package utils;

import jakarta.persistence.EntityManagerFactory;
import jakarta.persistence.Persistence;

import javax.servlet.ServletContextEvent;
import javax.servlet.ServletContextListener;
import javax.servlet.annotation.WebListener;

@WebListener
public class ContextListener implements ServletContextListener {
	private static EntityManagerFactory emf;

	public static EntityManagerFactory getEntityManagerFactory() {
		return emf;
	}

	@Override
	public void contextInitialized(ServletContextEvent sce) {
		emf = Persistence.createEntityManagerFactory("MysqlPersistence");
		sce.getServletContext().setAttribute("emf", emf);
	}

	@Override
	public void contextDestroyed(ServletContextEvent sce) {
		EntityManagerFactory emf = (EntityManagerFactory) sce.getServletContext().getAttribute("emf");
		if (emf != null && emf.isOpen()) {
			emf.close();
		}
	}
}
