import org.springframework.cloud.contract.spec.Contract

Contract.make {
    description "non-existing user returns false"
    request {
        method GET()
        url "/api/auth/validate/unknown"
    }
    response {
        status OK()
        bodyMatchers {
            jsonPath('$', byRegex('false'))
        }
        headers { contentType(applicationJson()) }
    }
}
