package controller;

import jakarta.xml.ws.Endpoint;
import soapWebService.DataProcessorImpl;

public class SoapServerLauncher {
	public static void main(String[] args) {
		String url = "http://database-server:8081/ws/decoder";
		Endpoint.publish(url, new DataProcessorImpl());
		System.out.println("SOAP service is running here: " + url + "?wsdl");
	}
}