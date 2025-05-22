package model;

import java.sql.Date;

public class Person {
	private int id;
	private String name;
	private Date birthDay;
	private String city;

	public Person(int id, String name, Date birthDay, String city) {
		this.id = id;
		this.name = name;
		this.birthDay = birthDay;
		this.city = city;
	}

	public int getId() {
		return id;
	}

	public String getName() {
		return name;
	}

	public Date getBirthDay() {
		return birthDay;
	}

	public String getCity() {
		return city;
	}
}
