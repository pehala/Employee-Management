package cz.fi.muni.pv217.employee.management.resources;

import cz.fi.muni.pv217.employee.management.EmployeeResource;
import cz.fi.muni.pv217.employee.management.providers.MockKeycloakTestResource;
import io.quarkus.test.common.QuarkusTestResource;
import io.quarkus.test.common.http.TestHTTPEndpoint;
import io.quarkus.test.h2.H2DatabaseTestResource;
import io.quarkus.test.junit.QuarkusTest;
import net.minidev.json.JSONObject;
import org.hamcrest.Matchers;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import javax.transaction.Transactional;

import static cz.fi.muni.pv217.employee.management.util.TestUtil.*;
import static io.restassured.RestAssured.given;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.is;

@QuarkusTest
@QuarkusTestResource(MockKeycloakTestResource.class)
@QuarkusTestResource(H2DatabaseTestResource.class)
@TestHTTPEndpoint(EmployeeResource.class)
public class EmployeeResourceTest {

    @BeforeEach
    @Transactional
    public void init(){
        EMPLOYEE1.id = null;
        EMPLOYEE2.id = null;
        EMPLOYEE1.persist();
        EMPLOYEE2.persist();
    }

    @Test
    void testCreateEmployeeUser() {
        given()
                .when().auth().oauth2(
                    getAccessToken("adam"))
                .contentType("application/json")
                .post("/create")
                .then()
                .statusCode(403);
    }

    @Test
    void testUpdateEmployeeUser() {
        given()
                .when().auth().oauth2(
                    getAccessToken("adam"))
                .contentType("application/json")
                .put("/1/update")
                .then()
                .statusCode(403);
    }

    @Test
    void testDeleteEmployeeUser() {
        given()
                .when().auth().oauth2(
                    getAccessToken("adam"))
                .contentType("application/json")
                .delete("/1/delete")
                .then()
                .statusCode(403);
    }

    @Test
    void testGetEmployeesUser() {
        given()
                .when().auth().oauth2(
                    getAccessToken("adam"))
                .get()
                .then()
                .statusCode(403);
    }

    @Test
    void testGetEmployeeUser() {
        given()
                .when().auth().oauth2(
                    getAccessToken("adam"))
                .get("/1")
                .then()
                .statusCode(403);
    }

    @Test
    void testGetMeUser() {
        given()
                .when().auth().oauth2(
                    getAccessToken("adam"))
                .get("/me")
                .then()
                .statusCode(200)
                .body("\"username\"", Matchers.is("adam"));
    }

    @Test
    void testSearchEmployeesUser() {
        given()
                .when().auth().oauth2(
                    getAccessToken("adam"))
                .queryParam("search", "ad")
                .get("/search")
                .then()
                .statusCode(403);
    }

    @Test
    @Transactional
    void testCreateEmployeeAdmin() {
        JSONObject requestParams = new JSONObject();
        requestParams.put("username", "igor");
        requestParams.put("name", "Igor");
        requestParams.put("surname", "Biely");
        requestParams.put("dateOfBirth", "2000-01-01");
        requestParams.put("insuranceCompany", "Union");
        requestParams.put("mobile", "0902000000");
        requestParams.put("address", "Brno");
        requestParams.put("startContract", "2019-01-01");
        requestParams.put("hourlyWage", "5");

        given()
            .when().auth().oauth2(
                getAccessToken("martin", "admin"))
            .contentType("application/json")
            .body(
                    requestParams.toJSONString()
            )
            .post("/create")
            .then()
            .statusCode(200)
            .body(    "username", is("igor"),
                    "name", is("Igor"),
                    "surname", is("Biely"),
                    "dateOfBirth", equalTo("2000-01-01"),
                    "insuranceCompany", is("Union"),
                    "mobile", is("0902000000"),
                    "address", is("Brno"),
                    "startContract", equalTo("2019-01-01"),
                    "hourlyWage", is(5));
    }

    @Test
    @Transactional
    void testUpdateEmployeeAdmin() {
        JSONObject requestParams = new JSONObject();
        requestParams.put("name", "Igor");
        requestParams.put("dateOfBirth", "2000-01-01");
        requestParams.put("insuranceCompany", "Union");
        requestParams.put("mobile", "0902000000");
        requestParams.put("address", "Brno");
        requestParams.put("startContract", "2019-01-01");
        requestParams.put("hourlyWage", "6");

        given()
            .when().auth().oauth2(
                getAccessToken("martin", "admin"))
            .contentType("application/json")
            .body(
                    requestParams.toJSONString()
            )
            .put("/1/update")
            .then()
            .statusCode(200)
            .body(    "id", is(1),
                    "username", is("martin"),
                    "name", is("Igor"),
                    "dateOfBirth", equalTo("2000-01-01"),
                    "insuranceCompany", is("Union"),
                    "mobile", is("0902000000"),
                    "address", is("Brno"),
                    "startContract", equalTo("2019-01-01"),
                    "hourlyWage", is(6));
    }

    @Test
    @Transactional
    void testDeleteEmployeeAdmin() {
        given()
                .when().auth().oauth2(
                    getAccessToken("martin", "admin"))
                .contentType("application/json")
                .delete("/2/delete")
                .then()
                .statusCode(200);

        given()
                .when().auth().oauth2(
                getAccessToken("martin", "admin"))
                .get()
                .then()
                .statusCode(200)
                .body("size()", Matchers.is(1));
    }

    @Test
    void testGetEmployeesAdmin() {

        given()
                .when().auth().oauth2(
                    getAccessToken("martin", "admin"))
                .get()
                .then()
                .statusCode(200)
                .body("size()", Matchers.is(2));
    }

    @Test
    void testGetEmployeeAdmin() {
        given()
                .when().auth().oauth2(
                    getAccessToken("martin", "admin"))
                .contentType("application/json")
                .get("/1")
                .then()
                .statusCode(200)
                .body("\"username\"", Matchers.is("martin"));
    }

    @Test
    void testGetMeAdmin() {
        given()
                .when().auth().oauth2(
                    getAccessToken("martin", "admin"))
                .get("/me")
                .then()
                .statusCode(200)
                .body("\"username\"", Matchers.is("martin"));
    }

    @Test
    void testSearchEmployeesAdmin() {
        given()
                .when().auth().oauth2(
                    getAccessToken("martin", "admin"))
                .contentType("application/json")
                .queryParam("search", "ad")
                .get("/search")
                .then()
                .statusCode(200)
                .body("size()", Matchers.is(1));
    }
}