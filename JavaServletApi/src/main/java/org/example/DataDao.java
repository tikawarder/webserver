package org.example;

import java.sql.*;

public class DataDao {
	private static final String URL = "jdbc:mysql://localhost:3307/usersdb";
	private static final String USER = "root";
	private static final String PASS = "rootpass";

	public void insertUser(String name, String birthString, String city) {
		Date birth = Date.valueOf(birthString);
		String sql = "INSERT INTO data (name, birth, city) VALUES (?, ?, ?)";

		try (Connection conn = DriverManager.getConnection(URL, USER, PASS);
			 PreparedStatement stmt = conn.prepareStatement(sql)) {

			stmt.setString(1, name);
			stmt.setDate(2, birth);
			stmt.setString(3, city);
			stmt.executeUpdate();
			System.out.println("Felhasználó beszúrva!");

		} catch (SQLException e) {
			e.printStackTrace();		}
	}

	public void listUsers() {
		String sql = "SELECT * FROM data";

		try (Connection conn = DriverManager.getConnection(URL, USER, PASS);
			 Statement stmt = conn.createStatement();
			 ResultSet rs = stmt.executeQuery(sql)) {

			while (rs.next()) {
				int id = rs.getInt("id");
				String name = rs.getString("name");
				Date birthDate = rs.getDate("birth");
				String city = rs.getString("city");

				System.out.printf("ID: %d, Név: %s, Szül: %s, Város: %s%n", id, name, birthDate.toString(), city);
			}

		} catch (SQLException e) {
			e.printStackTrace();
		}
	}
}
