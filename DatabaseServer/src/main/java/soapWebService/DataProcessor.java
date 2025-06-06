package soapWebService;

import jakarta.jws.WebService;
import jakarta.jws.WebMethod;
import model.Person;

import java.util.List;

@WebService
public interface DataProcessor {
	@WebMethod
	String decodeAndStore(Person person);

	@WebMethod
	List<Person> getPersons();
}
