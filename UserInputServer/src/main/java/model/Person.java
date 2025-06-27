package model;

import jakarta.json.bind.annotation.JsonbDateFormat;
import java.time.LocalDate;

public class Person {
	private int id;
	private String name;
	@JsonbDateFormat("yyyy-MM-dd")
	private LocalDate birthDay;
	private String city;

	public Person(int id, String name, LocalDate birthDay, String city) {
		this.id = id;
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

	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	@Override
	public String toString() {
		return "Person{name='" + name + "', birth=" + birthDay + ", city='" + city + "'}";
	}
}
