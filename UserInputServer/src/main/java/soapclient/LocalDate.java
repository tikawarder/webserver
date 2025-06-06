
package soapclient;

import jakarta.xml.bind.annotation.XmlAccessType;
import jakarta.xml.bind.annotation.XmlAccessorType;
import jakarta.xml.bind.annotation.XmlType;

import java.time.format.DateTimeFormatter;


/**
 * <p>Java class for localDate complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="localDate"&gt;
 *   &lt;complexContent&gt;
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType"&gt;
 *       &lt;sequence&gt;
 *       &lt;/sequence&gt;
 *     &lt;/restriction&gt;
 *   &lt;/complexContent&gt;
 * &lt;/complexType&gt;
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "localDate")
public class LocalDate {
	private String value;

	public LocalDate() {
		// Default constructor needed by JAXB
	}

	public LocalDate(String dateString) {
		this.value = dateString;
	}

	public LocalDate(LocalDate javaDate) {
		this.value = javaDate.toString();
	}

	public String getValue() {
		return value;
	}

	public void setValue(String value) {
		this.value = value;
	}

	public java.time.LocalDate toJavaLocalDate() {
		return java.time.LocalDate.parse(value, DateTimeFormatter.ISO_LOCAL_DATE);
	}

	@Override
	public String toString() {
		return value;
	}
}