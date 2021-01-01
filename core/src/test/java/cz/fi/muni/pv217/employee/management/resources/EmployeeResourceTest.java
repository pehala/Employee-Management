package cz.fi.muni.pv217.employee.management.resources;

import cz.fi.muni.pv217.employee.management.EmployeeResource;
import cz.fi.muni.pv217.employee.management.entity.Employee;
import cz.fi.muni.pv217.employee.management.providers.MockKeycloakTestResource;
import io.quarkus.test.common.QuarkusTestResource;
import io.quarkus.test.common.http.TestHTTPEndpoint;
import io.quarkus.test.junit.QuarkusTest;
import io.restassured.response.Response;
import net.minidev.json.JSONObject;
import org.hamcrest.Matchers;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import javax.transaction.Transactional;

import static cz.fi.muni.pv217.employee.management.util.TestUtil.*;
import static io.restassured.RestAssured.given;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.is;

@QuarkusTest
@QuarkusTestResource(MockKeycloakTestResource.class)
@TestHTTPEndpoint(EmployeeResource.class)
public class EmployeeResourceTest {

    @BeforeAll
    @Transactional
    public static void init(){
        EMPLOYEE.persist();
        createAnotherEmployee().persist();
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

        // Do request and save it
        Response response =
            given()
                .when().auth().oauth2(
                    getAccessToken("martin", "admin"))
                .contentType("application/json")
                .body(
                        requestParams.toJSONString()
                )
                .post("/create");
        // Check the response
        response.then()
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
        // Delete the created employee because transactions suck in integration tests
        Long id = Long.valueOf((Integer) response.jsonPath().get("id"));
        Employee.deleteById(id);
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

        // Do request and save it
        Response response =
            given()
                .when().auth().oauth2(
                    getAccessToken("martin", "admin"))
                .contentType("application/json")
                .body(
                        requestParams.toJSONString()
                )
                .put("/1/update");
        // Check the response
        response.then()
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
        createAnotherEmployee().persist();
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