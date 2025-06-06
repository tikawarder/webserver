package soapWebService;

import jakarta.jws.WebService;
import jakarta.persistence.EntityManagerFactory;
import jakarta.persistence.Persistence;
import model.Person;
import services.PersonDao;

import java.util.List;

@WebService(endpointInterface = "soapWebService.DataProcessor")
public class DataProcessorImpl implements DataProcessor {
	private EntityManagerFactory emf = Persistence.createEntityManagerFactory("MysqlPersistence");

	@Override
	public String decodeAndStore(Person person) {
		PersonDao dao = new PersonDao(emf);
		dao.saveUser(person);
		return "Stored: " + person.toString();
	}

	@Override
	public List<Person> getPersons(){
		PersonDao dao = new PersonDao(emf);
		return dao.listUsers();
	}
}