package com.example.demo;

import io.restassured.RestAssured;
import io.restassured.http.ContentType;
import io.restassured.response.Response;
import org.junit.jupiter.api.*;
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

	private String UUID;


	@BeforeAll
	static void TimeOutTest(){
		try {
			Thread.sleep(5000);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
	}

	/*
	real-estate-service
	*/

	@Order(1)
	@Test
	void testPostCreate() throws IOException {
		RestAssured.baseURI = port + "/" + epr;

		String requestBody = new String(Files.readAllBytes(Paths.get("src/test/resources/RealEstateRequestBody.txt")));

		Response response = given()
				.contentType(ContentType.JSON)
				.body(requestBody)
				.when().post("/");

		assertEquals(201, response.getStatusCode());
	}

	@Order(2)
	@Test
	void testPutUpdate()throws IOException {
		RestAssured.baseURI = port + "/" + epr;

		String requestBody = new String(Files.readAllBytes(Paths.get("src/test/resources/RealEstateRequestBody.txt")));

		Response response = given()
				.contentType(ContentType.JSON)
				.body(requestBody)
				.when().put("/");

		assertEquals(200, response.getStatusCode());
	}

	@Order(3)
	@Test
	void testGetAll(){

		RestAssured.baseURI = port + "/" + epr;

		Response response = RestAssured.get("/");

		assertEquals(200, response.getStatusCode());
	}

	@Order(4)
	@Test
	void testGetById(){
		RestAssured.baseURI = port + "/" + epr;

		Response response = RestAssured.get("/1");

		assertEquals(200, response.getStatusCode());
	}

	@Order(5)
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
	void testDeleteDelete(){
		RestAssured.baseURI = port + "/" + epr;

		Response response = given()
				.when()
				.delete("/1");

		assertEquals(200, response.getStatusCode());
	}

	/*
	agency-service
	*/
	@Test
	void testPutCreateContract() throws IOException {
		RestAssured.baseURI = port + "/" + epa;

		String requestBody = new String(Files.readAllBytes(Paths.get("src/test/resources/AgencyRequestBody.txt")));

		Response response = given()
				.contentType(ContentType.JSON)
				.body(requestBody)
				.when().put("/");

		assertEquals(201, response.getStatusCode());


	}

	@Test
	void testGetFindByInternalId(){

	}

	@Test
	void testGetFind(){

	}

	@Test
	void testPostApprove(){

	}

	@Test
	void testPostDisapprove(){

	}
}
