package cz.fi.muni.pv217.employee.management.resources;

import cz.fi.muni.pv217.employee.management.SalaryResource;
import cz.fi.muni.pv217.employee.management.entity.Salary;
import cz.fi.muni.pv217.employee.management.providers.MockCoreServer;
import cz.fi.muni.pv217.employee.management.providers.MockKeycloakTestResource;
import cz.fi.muni.pv217.employee.management.providers.MockLeaveServer;
import cz.fi.muni.pv217.employee.management.util.TestUtil;
import io.quarkus.test.common.QuarkusTestResource;
import io.quarkus.test.common.http.TestHTTPEndpoint;
import io.quarkus.test.junit.QuarkusTest;
import io.restassured.response.Response;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import javax.transaction.Transactional;

import static cz.fi.muni.pv217.employee.management.util.TestUtil.getAccessToken;
import static io.restassured.RestAssured.given;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.is;

@QuarkusTest
@QuarkusTestResource(MockCoreServer.class)
@QuarkusTestResource(MockLeaveServer.class)
@QuarkusTestResource(MockKeycloakTestResource.class)
@TestHTTPEndpoint(SalaryResource.class)
public class SalaryResourceTest {

    @BeforeAll
    @Transactional
    public static void init(){
        TestUtil.USER_SALARY1.persist();
        TestUtil.USER_SALARY2.persist();
    }

    @Test
    void testListNoAuth() {
        given()
                .when()
                .get()
                .then()
                    .statusCode(401);
    }

    @Test
    void testListAdmin() {
        given()
                .when().auth().oauth2(
                        getAccessToken("non-existent", "admin"))
                .get()
                .then()
                .statusCode(200)
                .body("size()", is(2));
    }

    @Test
    void testListUser() {
        given()
                .when().auth().oauth2(
                        getAccessToken("non-existent", "user"))
                .get()
                .then()
                .statusCode(403);
    }

    @Test
    void testGetNoSalaryUser() {
        given()
                .when().auth().oauth2(
                        getAccessToken("non-existent"))
                .get("/1")
                .then()
                .statusCode(403);
    }

    @Test
    void testGetUser() {
        given()
                .when().auth().oauth2(
                    getAccessToken("user"))
                .get("/1")
                .then()
                .statusCode(200)
                .body("\"employee_name\"", is("user"));
    }

    @Test
    void testGetAdmin() {
        given()
                .when().auth().oauth2(
                    getAccessToken("non-existent", "admin"))
                .get("/1")
                .then()
                .statusCode(200)
                .body("\"employee_name\"", is("user"));
    }

    @Test
    void testGetEmployeeUser() {
        given()
                .when().auth().oauth2(
                    getAccessToken("user"))
                .get("/employee")
                .then()
                .statusCode(200)
                .body("size()", is(1));
    }

    @Test
    void testGetEmployeeNonExistentUser() {
        given()
                .when().auth().oauth2(
                    getAccessToken("non-existent"))
                .get("/employee")
                .then()
                .statusCode(200)
                .body("size()", is(0));
    }

    @Test
    @Transactional
    void testCreateWithoutVacation() {
        // Do request and save it
        Response response =
                given()
                .when().auth().oauth2(
                getAccessToken("non-existent", "admin"))
                .contentType("application/json")
                .queryParam("from", "2018-01-01")
                .queryParam("to", "2018-01-31")
                .post("/employee/1");
        // Check the response
        response.then()
                .statusCode(200)
                .body(
                        "salary", is(135.0F),
                        "worked_days", is(2),
                        "leave_days", is(0),
                        "employee_name", is("user"),
                        "from_date", equalTo("2018-01-01"),
                        "to_date", equalTo("2018-01-31")
                );

        // Delete the created salary because transactions suck in integration tests
        Long id = Long.valueOf((Integer) response.jsonPath().get("id"));
        Salary.deleteById(id);
    }

    @Test
    @Transactional
    void testCreateWithVacation() {
        // Do request and save it
        Response response =
                given()
                .when().auth().oauth2(
                getAccessToken("non-existent", "admin"))
                .contentType("application/json")
                .queryParam("from", "2018-01-01")
                .queryParam("to", "2018-01-31")
                .post("/employee/2");
        // Check the response
        response.then()
                .statusCode(200)
                .body(
                        "salary", is(205.0F),
                        "worked_days", is(2),
                        "leave_days", is(2),
                        "employee_name", is("user"),
                        "from_date", equalTo("2018-01-01"),
                        "to_date", equalTo("2018-01-31")
                );

        // Delete the created salary because transactions suck in integration tests
        Long id = Long.valueOf((Integer) response.jsonPath().get("id"));
        Salary.deleteById(id);
    }

    @Test
    void testCreateNonExistingUser() {
        given()
                .when().auth().oauth2(
                getAccessToken("non-existent", "admin"))
                .contentType("application/json")
                .queryParam("from", "2018-01-01")
                .queryParam("to", "2018-01-31")
                .post("/employee/3")
                .then()
                .statusCode(404);
    }

    @Test
    void testCreateWrongAuth() {
        given()
                .when().auth().oauth2(
                getAccessToken("non-existent"))
                .contentType("application/json")
                .queryParam("from", "2018-01-01")
                .queryParam("to", "2018-01-31")
                .post("/employee/2")
                .then()
                .statusCode(403);
    }

    @Test
    void testCreateNoAuth() {
        given()
                .when()
                .contentType("application/json")
                .queryParam("from", "2018-01-01")
                .queryParam("to", "2018-01-31")
                .post("/employee/1")
                .then()
                .statusCode(401);
    }
}
