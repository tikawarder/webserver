package org.example;

public class Main {
	public static void main(String[] args) {
		UserDao dao = new UserDao();
		dao.insertUser("Teszt Elek", "teszt@pelda.hu");
		dao.listUsers();
	}
}
