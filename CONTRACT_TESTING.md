# Contract Testing — DatabaseServer ↔ AuthService

## Mi ez és miért csináltuk?

A microservices architektúrában a szervizek HTTP-n kommunikálnak egymással.
Ez egy **implicit megállapodást** hoz létre: ha az AuthService megváltoztatja az
endpoint URL-jét, response formátumát vagy státuszkódját, a DatabaseServer csendben eltörik.

A **Spring Cloud Contract** ezt formalizálja:
- A contract fájl rögzíti a megállapodást
- A provider teszt ellenőrzi, hogy az AuthService valóban ezt csinálja
- A consumer teszt ellenőrzi, hogy a DatabaseServer jól hívja az AuthService-t

Mindez **Docker és éles szerviz nélkül** fut, minden `mvn test` után.

---

## Az ellenőrzött contract

```
DatabaseServer (consumer) hívja:
  GET /api/auth/validate/testuser  →  200 OK, body: true
  GET /api/auth/validate/unknown   →  200 OK, body: false
```

---

## Fájlok

### AuthService (provider)

| Fájl | Szerepe |
|------|---------|
| `src/test/resources/contracts/validateExistingUser.groovy` | Contract: létező user → true |
| `src/test/resources/contracts/validateNonExistingUser.groovy` | Contract: nem létező user → false |
| `src/test/java/authservice/contract/ContractBase.java` | MockMvc setup + testuser létrehozása H2-ben |
| `src/test/resources/application-test.properties` | H2 konfiguráció tesztekhez |

### DatabaseServer (consumer)

| Fájl | Szerepe |
|------|---------|
| `src/test/java/databaseserver/contract/AuthServiceClientContractTest.java` | Consumer teszt a generált WireMock stub ellen |
| `src/main/java/databaseserver/services/AuthServiceClient.java` | Refaktorálva: `@Value` constructor injection a tesztelhetőségért |

---

## Hogyan fut?

### 1. Provider — contract fájlokból teszt + stub JAR generálása

```bash
cd AuthService
mvn test          # generálja a ContractVerifierTest-et és futtatja
mvn install       # berakja a stubs.jar-t a lokális Maven repo-ba (~/.m2)
```

A plugin generálja ezt (nem kézzel írjuk):
```java
// target/generated-test-sources/contracts/authservice/contract/ContractVerifierTest.java
class ContractVerifierTest extends ContractBase {
    @Test void validate_validateExistingUser() { ... }
    @Test void validate_validateNonExistingUser() { ... }
}
```

### 2. Consumer — stub runner a generált JAR ellen

```bash
cd DatabaseServer
mvn test -Dtest=AuthServiceClientContractTest
```

A `@AutoConfigureStubRunner` elindít egy **WireMock szervert** a 9999-es porton,
feltölti a stub JAR-ból az előre rögzített request/response párokat,
az `AuthServiceClient` ezt a mock szervert hívja — az éles AuthService nem szükséges.

---

## Mi törik el, ha valaki megváltoztatja az AuthService-t?

| Változás | Hol válik pirossá |
|----------|-------------------|
| URL megváltozik (`/api/auth/check/`) | AuthService provider teszt |
| Response formátum változik (pl. `{"valid": true}`) | AuthService provider teszt + DatabaseServer consumer teszt |
| HTTP státuszkód változik (pl. 404) | Mindkét helyen |

---

## Kapcsolódó fogalmak (interjúra)

**Strangler Fig Pattern** — fokozatos microservice kiemelés a monolitból.
A contract teszt az a biztonsági háló, ami jelzi, ha egy kiemelés eltörte a kommunikációt.

**Provider-driven contract** (Spring Cloud Contract) — a provider írja a contract-ot.
Alternatíva: **Consumer-driven contract** (Pact) — a consumer írja, polyglot rendszerekhez.

**Characterization test** — a monolit *jelenlegi* viselkedését rögzíti kiemelés előtt.
Ezt kellett volna a DatabaseServer → AuthService kiemelés előtt is megírni.
