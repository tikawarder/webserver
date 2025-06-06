
package soapclient;

import javax.xml.namespace.QName;
import jakarta.xml.bind.JAXBElement;
import jakarta.xml.bind.annotation.XmlElementDecl;
import jakarta.xml.bind.annotation.XmlRegistry;


/**
 * This object contains factory methods for each 
 * Java content interface and Java element interface 
 * generated in the soapclient package. 
 * <p>An ObjectFactory allows you to programatically 
 * construct new instances of the Java representation 
 * for XML content. The Java representation of XML 
 * content can consist of schema derived interfaces 
 * and classes representing the binding of schema 
 * type definitions, element declarations and model 
 * groups.  Factory methods for each of these are 
 * provided in this class.
 * 
 */
@XmlRegistry
public class ObjectFactory {

    private final static QName _DecodeAndStore_QNAME = new QName("http://soapWebService/", "decodeAndStore");
    private final static QName _DecodeAndStoreResponse_QNAME = new QName("http://soapWebService/", "decodeAndStoreResponse");
    private final static QName _GetPersons_QNAME = new QName("http://soapWebService/", "getPersons");
    private final static QName _GetPersonsResponse_QNAME = new QName("http://soapWebService/", "getPersonsResponse");
    private final static QName _Person_QNAME = new QName("http://soapWebService/", "person");

    /**
     * Create a new ObjectFactory that can be used to create new instances of schema derived classes for package: soapclient
     * 
     */
    public ObjectFactory() {
    }

    /**
     * Create an instance of {@link DecodeAndStore }
     * 
     */
    public DecodeAndStore createDecodeAndStore() {
        return new DecodeAndStore();
    }

    /**
     * Create an instance of {@link DecodeAndStoreResponse }
     * 
     */
    public DecodeAndStoreResponse createDecodeAndStoreResponse() {
        return new DecodeAndStoreResponse();
    }

    /**
     * Create an instance of {@link GetPersons }
     * 
     */
    public GetPersons createGetPersons() {
        return new GetPersons();
    }

    /**
     * Create an instance of {@link GetPersonsResponse }
     * 
     */
    public GetPersonsResponse createGetPersonsResponse() {
        return new GetPersonsResponse();
    }

    /**
     * Create an instance of {@link Person }
     * 
     */
    public Person createPerson() {
        return new Person();
    }

    /**
     * Create an instance of {@link LocalDate }
     * 
     */
    public LocalDate createLocalDate() {
        return new LocalDate();
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link DecodeAndStore }{@code >}
     * 
     * @param value
     *     Java instance representing xml element's value.
     * @return
     *     the new instance of {@link JAXBElement }{@code <}{@link DecodeAndStore }{@code >}
     */
    @XmlElementDecl(namespace = "http://soapWebService/", name = "decodeAndStore")
    public JAXBElement<DecodeAndStore> createDecodeAndStore(DecodeAndStore value) {
        return new JAXBElement<DecodeAndStore>(_DecodeAndStore_QNAME, DecodeAndStore.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link DecodeAndStoreResponse }{@code >}
     * 
     * @param value
     *     Java instance representing xml element's value.
     * @return
     *     the new instance of {@link JAXBElement }{@code <}{@link DecodeAndStoreResponse }{@code >}
     */
    @XmlElementDecl(namespace = "http://soapWebService/", name = "decodeAndStoreResponse")
    public JAXBElement<DecodeAndStoreResponse> createDecodeAndStoreResponse(DecodeAndStoreResponse value) {
        return new JAXBElement<DecodeAndStoreResponse>(_DecodeAndStoreResponse_QNAME, DecodeAndStoreResponse.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link GetPersons }{@code >}
     * 
     * @param value
     *     Java instance representing xml element's value.
     * @return
     *     the new instance of {@link JAXBElement }{@code <}{@link GetPersons }{@code >}
     */
    @XmlElementDecl(namespace = "http://soapWebService/", name = "getPersons")
    public JAXBElement<GetPersons> createGetPersons(GetPersons value) {
        return new JAXBElement<GetPersons>(_GetPersons_QNAME, GetPersons.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link GetPersonsResponse }{@code >}
     * 
     * @param value
     *     Java instance representing xml element's value.
     * @return
     *     the new instance of {@link JAXBElement }{@code <}{@link GetPersonsResponse }{@code >}
     */
    @XmlElementDecl(namespace = "http://soapWebService/", name = "getPersonsResponse")
    public JAXBElement<GetPersonsResponse> createGetPersonsResponse(GetPersonsResponse value) {
        return new JAXBElement<GetPersonsResponse>(_GetPersonsResponse_QNAME, GetPersonsResponse.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link Person }{@code >}
     * 
     * @param value
     *     Java instance representing xml element's value.
     * @return
     *     the new instance of {@link JAXBElement }{@code <}{@link Person }{@code >}
     */
    @XmlElementDecl(namespace = "http://soapWebService/", name = "person")
    public JAXBElement<Person> createPerson(Person value) {
        return new JAXBElement<Person>(_Person_QNAME, Person.class, null, value);
    }

}
