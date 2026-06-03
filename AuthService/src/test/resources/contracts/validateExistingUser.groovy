import org.springframework.cloud.contract.spec.Contract

Contract.make {
    description "existing user returns true"
    request {
        method GET()
        url "/api/auth/validate/testuser"
    }
    response {
        status OK()
        body($(consumer(true), producer(true)))
        headers { contentType(applicationJson()) }
    }
}
