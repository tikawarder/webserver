package org.example;

import java.sql.*;

public class UserDao {
	private static final String URL = "jdbc:mysql://localhost:3307/usersdb";
	private static final String USER = "root";
	private static final String PASS = "rootpass";

	public void insertUser(String name, String email) {
		String sql = "INSERT INTO users (name, email) VALUES (?, ?)";

		try (Connection conn = DriverManager.getConnection(URL, USER, PASS);
			 PreparedStatement stmt = conn.prepareStatement(sql)) {

			stmt.setString(1, name);
			stmt.setString(2, email);
			stmt.executeUpdate();
			System.out.println("Felhasználó beszúrva!");

		} catch (SQLException e) {
			e.printStackTrace();
		}
	}

	public void listUsers() {
		String sql = "SELECT * FROM users";

		try (Connection conn = DriverManager.getConnection(URL, USER, PASS);
			 Statement stmt = conn.createStatement();
			 ResultSet rs = stmt.executeQuery(sql)) {

			while (rs.next()) {
				System.out.printf("ID: %d, Név: %s, Email: %s%n",
						rs.getInt("id"),
						rs.getString("name"),
						rs.getString("email"));
			}

		} catch (SQLException e) {
			e.printStackTrace();
		}
	}
}
