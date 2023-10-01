package com.example.demo;

import io.restassured.RestAssured;
import io.restassured.http.ContentType;
import io.restassured.response.Response;
import org.springframework.boot.test.context.SpringBootTest;
import org.testng.annotations.*;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;

import static io.restassured.RestAssured.given;
import static org.testng.Assert.assertEquals;

@SpringBootTest
class RealEstateEntityAgencyApplicationTests {
	private final String port = "http://localhost:8080";
	private final String epr = "real-estate-service";
	private final String epa = "agency-service";

	private String UUID;


	@BeforeClass
	public static void TimeOutTest(){
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
	void testPostCreate() throws IOException {
		RestAssured.baseURI = port + "/" + epr;

		String requestBody = new String(Files.readAllBytes(Paths.get("src/test/resources/RealEstateRequestBody.txt")));

		Response response = given()
				.contentType(ContentType.JSON)
				.body(requestBody)
				.when().post("/");

		assertEquals(201, response.getStatusCode());
	}

	@Test(dependsOnMethods = "testPostCreate")
	void testPutUpdate()throws IOException {
		RestAssured.baseURI = port + "/" + epr;

		String requestBody = new String(Files.readAllBytes(Paths.get("src/test/resources/RealEstateRequestBody.txt")));

		Response response = given()
				.contentType(ContentType.JSON)
				.body(requestBody)
				.when().put("/");

		assertEquals(200, response.getStatusCode());
	}

	@Test(dependsOnMethods = "testPutUpdate")
	void testGetAll(){

		RestAssured.baseURI = port + "/" + epr;

		Response response = RestAssured.get("/");

		assertEquals(200, response.getStatusCode());
	}

	@Test(dependsOnMethods = "testGetById")
	void testGetById(){
		RestAssured.baseURI = port + "/" + epr;

		Response response = RestAssured.get("/1");

		assertEquals(200, response.getStatusCode());
	}

	@Test(dependsOnMethods = "testGetFindByAddress")
	void testGetFindByAddress(){
		RestAssured.baseURI = port + "/" + epr;

		Response response = RestAssured.given()
				.contentType(ContentType.JSON)
				.body("{\"street\": \"Пушкино\"}")
				.when().get("/address/find");

		assertEquals(200, response.getStatusCode());
	}

	@Test(dependsOnMethods = "testGetFindByAddress")
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
	@Test(dependsOnMethods = "testDeleteDelete")
	void testPutCreateContract() throws IOException {
		RestAssured.baseURI = port + "/" + epa;

		String requestBody = new String(Files.readAllBytes(Paths.get("src/test/resources/AgencyRequestBody.txt")));

		Response response = given()
				.contentType(ContentType.JSON)
				.body(requestBody)
				.when().put("/");

		assertEquals(201, response.getStatusCode());


	}

	@Test(dependsOnMethods = "testPutCreateContract")
	void testGetFindByInternalId(){

	}

	@Test(dependsOnMethods = "testGetFindByInternalId")
	void testGetFind(){

	}

	@Test(dependsOnMethods = "testGetFind")
	void testPostApprove(){

	}

	@Test(dependsOnMethods = "testPostApprove")
	void testPostDisapprove(){

	}
}
