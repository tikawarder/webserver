package model;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import jakarta.xml.bind.annotation.XmlAccessType;
import jakarta.xml.bind.annotation.XmlAccessorType;
import jakarta.xml.bind.annotation.XmlRootElement;
import jakarta.xml.bind.annotation.adapters.XmlJavaTypeAdapter;
import services.LocalDateAdapter;

import java.time.LocalDate;

@Entity
@Table(name = "users")
@XmlRootElement
@XmlAccessorType(XmlAccessType.FIELD)
public class Person {
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private int id;
	private String name;
	@XmlJavaTypeAdapter(LocalDateAdapter.class)
	private LocalDate birthDay;
	private String city;

	public Person(String name, LocalDate birthDay, String city) {
		this.name = name;
		this.birthDay = birthDay;
		this.city = city;
	}

	public Person() {
	}

	public int getId() {
		return id;
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

	public void setId(int id) {
		this.id = id;
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
		return "Person{id=" + id + ", name='" + name + "', birth=" + birthDay + ", city='" + city + "'}";
	}
}
