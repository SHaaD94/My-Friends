package com.github.shaad.myfriends.endpoints

import io.quarkus.test.junit.QuarkusTest
import io.restassured.RestAssured.given
import org.hamcrest.Matchers.equalTo
import org.junit.jupiter.api.Test

@QuarkusTest
class TwoInstanceFunctionalTest {

    @Test
    fun `Should properly add friend`() {
        given()
            .`when`().post("/add/person")
            .then()
            .statusCode(200)
            .body(equalTo("Hello!"))
    }

    @Test
    fun `Should properly add friendship`() {
        given()
            .`when`().post("/add/friendship")
            .then()
            .statusCode(200)
            .body(equalTo("Hello!"))
    }

}