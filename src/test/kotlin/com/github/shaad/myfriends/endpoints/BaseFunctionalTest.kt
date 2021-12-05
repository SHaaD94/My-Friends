package com.github.shaad.myfriends.endpoints

import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.module.kotlin.KotlinModule
import com.fasterxml.jackson.module.kotlin.readValue
import com.github.shaad.myfriends.domain.*
import io.restassured.RestAssured
import io.restassured.http.ContentType

abstract class BaseFunctionalTest {
    private val objectMapper = ObjectMapper().registerModule(KotlinModule())

    fun getFriends(person: String): GetFriendsResponse = objectMapper.readValue(
        RestAssured.given().`when`().with().body(objectMapper.writeValueAsBytes(GetFriendsRequest(person)))
            .contentType(ContentType.JSON).post("/person/friends").then().statusCode(200).extract().body().asString()
    )

    fun getHandshakes(p1: String, p2: String): GetHandshakesResponse = objectMapper.readValue(
        RestAssured.given().`when`().with().body(objectMapper.writeValueAsBytes(GetHandshakesRequest(p1, p2)))
            .contentType(ContentType.JSON).post("/person/handshakes").then().statusCode(200).extract().body().asString()
    )

    fun addPerson(name: String) {
        RestAssured.given().`when`().with().body(objectMapper.writeValueAsBytes(AddPersonRequest(name)))
            .contentType(ContentType.JSON).post("/person/add").then().statusCode(204)
    }

    fun removePerson(name: String) {
        RestAssured.given().`when`().with().body(objectMapper.writeValueAsBytes(RemovePersonRequest(name)))
            .contentType(ContentType.JSON).delete("/person/remove").then().statusCode(204)
    }

    fun removeFriendship(p1: String, p2: String) {
        RestAssured.given().`when`().with().body(objectMapper.writeValueAsBytes(RemoveFriendshipRequest(p1, p2)))
            .contentType(ContentType.JSON).delete("/friendship/remove").then().statusCode(204)
    }

    fun checkExistence(name: String): Boolean {
        return RestAssured.given().`when`().with().body(objectMapper.writeValueAsBytes(AddPersonRequest(name)))
            .contentType(ContentType.JSON).post("/person/exists").then().statusCode(200).extract().body().path("exists")
    }

    fun addFriendship(from: String, to: String) {
        RestAssured.given().`when`().with().body(objectMapper.writeValueAsBytes(AddFriendshipRequest(from, to)))
            .contentType(ContentType.JSON).post("/friendship/add").then().statusCode(204)
    }

}