package com.example.demo;

import io.restassured.RestAssured;
import io.restassured.http.ContentType;
import io.restassured.response.Response;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;

import static io.restassured.RestAssured.given;
import static org.junit.jupiter.api.Assertions.assertEquals;

@SpringBootTest
class RealEstateEntityAgencyApplicationTests {
	private final String port = "http://localhost:8080";
	private final String epr = "real-estate-service";
	private final String epa = "agency-service";


	@BeforeEach
	void TimeOutTest(){
		try {
			Thread.sleep(5000);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
	}

	/*
	real-estate-service
	*/

	@Test
	void testGetAll(){

		RestAssured.baseURI = port + "/" + epr;

		Response response = RestAssured.get("/");

		assertEquals(200, response.getStatusCode());
	}

	@Test
	void testGetById(){
		RestAssured.baseURI = port + "/" + epr;

		Response response = RestAssured.get("/3");

		assertEquals(200, response.getStatusCode());
	}

	@Test
	void testGetFindByAddress(){
		RestAssured.baseURI = port + "/" + epr;

		Response response = RestAssured.given()
				.contentType(ContentType.JSON)
				.body("{\"street\": \"Пушкино\"}")
				.when().get("/address/find");

		assertEquals(200, response.getStatusCode());
	}

	@Test
	void testPostCreate() throws IOException {
		RestAssured.baseURI = port + "/" + epr;

		String requestBody = new String(Files.readAllBytes(Paths.get("src/main/resources/RequestBody.txt")));

		Response response = given()
				.contentType(ContentType.JSON)
				.body(requestBody)
				.when().post("/");

		assertEquals(201, response.getStatusCode());
	}

	@Test
	void testPutUpdate()throws IOException {
		RestAssured.baseURI = port + "/" + epr;

		String requestBody = new String(Files.readAllBytes(Paths.get("src/main/resources/RequestBody.txt")));

		Response response = given()
				.contentType(ContentType.JSON)
				.body(requestBody)
				.when().put("/3");

		assertEquals(200, response.getStatusCode());
	}



	@Test
	void testDeleteDelete(){
		RestAssured.baseURI = port + "/" + epr;

		Response response = given()
				.when()
				.delete("/3");

		assertEquals(200, response.getStatusCode());
	}

	/*
	agency-service
	*/
	/*
	@Test
	void testGetFindByInternalId(){

		RestAssured.baseURI = port + "/" + epa;

		Response response = RestAssured.get("/3");

		assertEquals(200, response.getStatusCode());
	}

	@Test
	void testGetById(){
		RestAssured.baseURI = port + "/" + epr;

		Response response = RestAssured.get("/3");

		assertEquals(200, response.getStatusCode());
	}
	*/
}
