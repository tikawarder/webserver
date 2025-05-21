import myData.Person;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class DataDao {
	private static final String URL = "jdbc:mysql://localhost:3307/usersdb";
	private static final String USER = "root";
	private static final String PASS = "root";

	public void insertUser(String name, String birthString, String city) {
		Date birth = Date.valueOf(birthString);
		String sql = "INSERT INTO users (name, birth, city) VALUES (?, ?, ?)";

		try (Connection conn = DriverManager.getConnection(URL, USER, PASS);
			 PreparedStatement stmt = conn.prepareStatement(sql)) {

			stmt.setString(1, name);
			stmt.setDate(2, birth);
			stmt.setString(3, city);
			stmt.executeUpdate();

		} catch (SQLException e) {
			e.printStackTrace();		}
	}

	public List<Person> listUsers() {
		String sql = "SELECT * FROM users";
		List<Person> persons = new ArrayList<Person>();

		try (Connection conn = DriverManager.getConnection(URL, USER, PASS);
			 Statement stmt = conn.createStatement();
			 ResultSet rs = stmt.executeQuery(sql)) {

			while (rs.next()) {
				int id = rs.getInt("id");
				String name = rs.getString("name");
				Date birthDate = rs.getDate("birth");
				String city = rs.getString("city");

				Person person = new Person(id, name, birthDate, city);
				persons.add(person);
			}

		} catch (SQLException e) {
			e.printStackTrace();
		}
		return persons;
	}
}