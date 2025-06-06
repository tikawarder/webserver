package model;

import java.time.LocalDate;
import jakarta.xml.bind.annotation.XmlRootElement;

@XmlRootElement
public class Person {

	private String name;
	private LocalDate birthDay;
	private String city;

	public Person(String name, LocalDate birthDay, String city) {
		this.name = name;
		this.birthDay = birthDay;
		this.city = city;
	}

	public Person() {
	}

	public String getName() {
		return name;
	}

	public LocalDate getBirthDay() {
		return birthDay;
	}

	public String getCity() {
		return city;
	}

	public void setName(String name) {
		this.name = name;
	}

	public void setBirthDay(LocalDate birthDay) {
		this.birthDay = birthDay;
	}

	public void setCity(String city) {
		this.city = city;
	}

	@Override
	public String toString() {
		return "Person{name='" + name + "', birth=" + birthDay + ", city='" + city + "'}";
	}
}
