package controller;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

public class DateFormatter {
	public static LocalDate toLocalDateFormat(String date) {
		DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");
		return LocalDate.parse(date, formatter);
	}
}
