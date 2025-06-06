package utils;

import org.apache.commons.text.StringEscapeUtils;

public class InputSanitizer {
		public static String sanitize(String input) {
			if (input == null) {
				return "";
			}
			if (input.length() > 20) {
				return input.substring(0, 20);
			}
			return StringEscapeUtils.escapeHtml4(input);
		}
}

