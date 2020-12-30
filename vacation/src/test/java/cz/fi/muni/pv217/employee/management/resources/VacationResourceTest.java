package cz.fi.muni.pv217.employee.management.resources;

import cz.fi.muni.pv217.employee.management.VacationResource;
import cz.fi.muni.pv217.employee.management.entity.Vacation;
import cz.fi.muni.pv217.employee.management.providers.MockCoreServer;
import cz.fi.muni.pv217.employee.management.providers.MockKeycloakTestResource;
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
@QuarkusTestResource(MockKeycloakTestResource.class)
@TestHTTPEndpoint(VacationResource.class)
public class VacationResourceTest {

    @BeforeAll
    @Transactional
    public static void init() {
        TestUtil.USER_VACATION1.persist();
        TestUtil.USER_VACATION2.persist();
    }

    @Test
    void testGetAllNoAuth() {
        given()
                .when()
                .get()
                .then()
                .statusCode(401);
    }

    @Test
    void testGetAllAdmin() {
        given()
                .when()
                .auth()
                .oauth2(getAccessToken("non-existent", "admin"))
                .get()
                .then()
                .statusCode(200)
                .body("size()", is(2));
    }

    @Test
    void testGetAllUser() {
        given()
                .when()
                .auth()
                .oauth2(getAccessToken("non-existent", "user"))
                .get()
                .then()
                .statusCode(403);
    }

    @Test
    void testGetNoVacationUser() {
        given()
                .when()
                .auth()
                .oauth2(getAccessToken("non-existent"))
                .get("/1")
                .then()
                .statusCode(403);
    }

    @Test
    void testGetUser() {
        given()
                .when()
                .auth()
                .oauth2(getAccessToken("user"))
                .get("/1")
                .then()
                .statusCode(200)
                .body("\"employee_id\"", is(1));
    }

    @Test
    void testGetAdmin() {
        given()
                .when()
                .auth()
                .oauth2(getAccessToken("non-existent", "admin"))
                .get("/1")
                .then()
                .statusCode(200)
                .body("\"employee_id\"", is(1));
    }

    @Test
    void testGetByEmployeeAdmin() {
        given()
                .when()
                .auth()
                .oauth2(getAccessToken("non-existent", "admin"))
                .contentType("application/json")
                .queryParam("from", "2018-01-01")
                .queryParam("to", "2022-01-31")
                .get("/employee/1")
                .then()
                .statusCode(200)
                .body("size()", is(1));
    }

    @Test
    void testGetByEmployeeAdminEmpty() {
        given()
                .when()
                .auth()
                .oauth2(getAccessToken("non-existent", "admin"))
                .contentType("application/json")
                .queryParam("from", "2018-01-01")
                .queryParam("to", "2019-01-31")
                .get("/employee/1")
                .then()
                .statusCode(200)
                .body("size()", is(0));
    }

    @Test
    void testGetByEmployeeUser() {
        given()
                .when()
                .auth()
                .oauth2(getAccessToken("user"))
                .contentType("application/json")
                .queryParam("from", "2018-01-01")
                .queryParam("to", "2022-01-31")
                .get("/employee/1")
                .then()
                .statusCode(200)
                .body("size()", is(1));
    }

    @Test
    void testHasEmployeeVacationOnDateEmpty() {
        given()
                .when()
                .auth()
                .oauth2(getAccessToken("user"))
                .contentType("application/json")
                .queryParam("date", "2021-01-02")
                .get("/employee/1/date")
                .then()
                .statusCode(200)
                .body(is("false"));
    }

    @Test
    void testHasEmployeeVacationOnDate() {
        given()
                .when()
                .auth()
                .oauth2(getAccessToken("user"))
                .contentType("application/json")
                .queryParam("date", "2021-01-01")
                .get("/employee/1/date")
                .then()
                .statusCode(200)
                .body(is("true"));
    }

    @Test
    void testCreateVacationUser() {
        given()
                .when().auth().oauth2(
                getAccessToken("user"))
                .contentType("application/json")
                .post("/create")
                .then()
                .statusCode(403);
    }

    @Test
    void testUpdateVacationUser() {
        given()
                .when().auth().oauth2(
                getAccessToken("user"))
                .contentType("application/json")
                .put("/1/update")
                .then()
                .statusCode(403);
    }

    @Test
    void testDeleteVacationUser() {
        given()
                .when().auth().oauth2(
                getAccessToken("user"))
                .contentType("application/json")
                .delete("/1/delete")
                .then()
                .statusCode(403);
    }
}
