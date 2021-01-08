package cz.fi.muni.pv217.employee.management.resources;

import cz.fi.muni.pv217.employee.management.WorkdayResource;
import cz.fi.muni.pv217.employee.management.providers.MockKeycloakTestResource;
import cz.fi.muni.pv217.employee.management.providers.MockVacationServer;
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
import static org.hamcrest.Matchers.is;

@QuarkusTest
@QuarkusTestResource(MockKeycloakTestResource.class)
@QuarkusTestResource(MockVacationServer.class)
@QuarkusTestResource(H2DatabaseTestResource.class)
@TestHTTPEndpoint(WorkdayResource.class)
public class WorkdayResourceTest {

    @BeforeEach
    @Transactional
    public void init(){
        EMPLOYEE1.id = null;
        ORDER.id = null;
        WORKDAY1.id = null;
        WORKDAY2.id = null;
        EMPLOYEE1.persist();
        ORDER.persist();
        WORKDAY1.persist();
        WORKDAY2.persist();
    }

    @Test
    @Transactional
    void testCreateWorkdayWhenEmployeeHasNotVacation() {
        JSONObject employee = new JSONObject();
        employee.put("id", "1");
        employee.put("username", "martin");
        employee.put("name", "Martin");
        employee.put("surname", "Hrasko");
        employee.put("dateOfBirth", "1990-01-01");
        employee.put("insuranceCompany", "Union");
        employee.put("mobile", "0900000000");
        employee.put("address", "Brno");
        employee.put("startContract", "2020-11-11");
        employee.put("hourlyWage", "5");

        JSONObject order = new JSONObject();
        order.put("id", "2");
        order.put("name", "Jozef");
        order.put("surname", "Orac");
        order.put("mobile", "0910000000");
        order.put("state", "INPROGRESS");
        order.put("info", "Zasad mrkvu");

        JSONObject requestParams = new JSONObject();
        requestParams.put("date", "2020-12-28");
        requestParams.put("hours", "8");
        requestParams.put("employee", employee);
        requestParams.put("order", order);

        given()
                .when().auth().oauth2(
                getAccessToken("adam"))
                .contentType("application/json")
                .body(requestParams.toJSONString())
                .post("/create")
                .then()
                .statusCode(200)
                .body(    "date", is("2020-12-28"),
                        "hours", is(8F),
                        "employee.id", is(1),
                        "order.id", is(2));
    }

    @Test
    @Transactional
    void testCreateWorkdayWhenEmployeeHasVacation() {
        JSONObject employee = new JSONObject();
        employee.put("id", "1");
        employee.put("username", "martin");
        employee.put("name", "Martin");
        employee.put("surname", "Hrasko");
        employee.put("dateOfBirth", "1990-01-01");
        employee.put("insuranceCompany", "Union");
        employee.put("mobile", "0900000000");
        employee.put("address", "Brno");
        employee.put("startContract", "2020-11-11");
        employee.put("hourlyWage", "5");

        JSONObject order = new JSONObject();
        order.put("id", "2");
        order.put("name", "Jozef");
        order.put("surname", "Orac");
        order.put("mobile", "0910000000");
        order.put("state", "INPROGRESS");
        order.put("info", "Zasad mrkvu");

        JSONObject requestParams = new JSONObject();
        requestParams.put("date", "2020-12-29");
        requestParams.put("hours", "6.5");
        requestParams.put("employee", employee);
        requestParams.put("order", order);

        given()
                .when().auth().oauth2(
                getAccessToken("adam"))
                .contentType("application/json")
                .body(requestParams.toJSONString())
                .post("/create")
                .then()
                .statusCode(412);
    }

    @Test
    @Transactional
    void testUpdateWorkdayWhenEmployeeHasNotVacation() {
        JSONObject employee = new JSONObject();
        employee.put("id", "1");
        employee.put("username", "martin");
        employee.put("name", "Martin");
        employee.put("surname", "Hrasko");
        employee.put("dateOfBirth", "1990-01-01");
        employee.put("insuranceCompany", "Union");
        employee.put("mobile", "0900000000");
        employee.put("address", "Brno");
        employee.put("startContract", "2020-11-11");
        employee.put("hourlyWage", "5");

        JSONObject order = new JSONObject();
        order.put("id", "2");
        order.put("name", "Jozef");
        order.put("surname", "Orac");
        order.put("mobile", "0910000000");
        order.put("state", "INPROGRESS");
        order.put("info", "Zasad mrkvu");

        JSONObject requestParams = new JSONObject();
        requestParams.put("date", "2020-12-28");
        requestParams.put("hours", "8");
        requestParams.put("employee", employee);
        requestParams.put("order", order);

        given()
                .when().auth().oauth2(
                getAccessToken("adam"))
                .contentType("application/json")
                .body(
                        requestParams.toJSONString()
                )
                .put("/3/update")
                .then()
                .statusCode(200)
                .body(    "date", is("2020-12-28"),
                        "hours", is(8F),
                        "employee.id", is(1),
                        "order.id", is(2));
    }

    @Test
    @Transactional
    void testUpdateWorkdayWhenEmployeeHasVacation() {
        JSONObject employee = new JSONObject();
        employee.put("id", "1");
        employee.put("username", "martin");
        employee.put("name", "Martin");
        employee.put("surname", "Hrasko");
        employee.put("dateOfBirth", "1990-01-01");
        employee.put("insuranceCompany", "Union");
        employee.put("mobile", "0900000000");
        employee.put("address", "Brno");
        employee.put("startContract", "2020-11-11");
        employee.put("hourlyWage", "5");

        JSONObject order = new JSONObject();
        order.put("id", "2");
        order.put("name", "Jozef");
        order.put("surname", "Orac");
        order.put("mobile", "0910000000");
        order.put("state", "INPROGRESS");
        order.put("info", "Zasad mrkvu");

        JSONObject requestParams = new JSONObject();
        requestParams.put("date", "2020-12-29");
        requestParams.put("hours", "6.5");
        requestParams.put("employee", employee);
        requestParams.put("order", order);

        given()
                .when().auth().oauth2(
                getAccessToken("adam"))
                .contentType("application/json")
                .body(requestParams.toJSONString())
                .put("3/update")
                .then()
                .statusCode(412);
    }

    @Test
    @Transactional
    void testDeleteWorkdayAdmin() {
        given()
                .when().auth().oauth2(
                getAccessToken("adam", "admin"))
                .contentType("application/json")
                .delete("/3/delete")
                .then()
                .statusCode(200);

        given()
                .when().auth().oauth2(
                getAccessToken("adam", "admin"))
                .get()
                .then()
                .statusCode(200)
                .body("size()", Matchers.is(1));
    }

    @Test
    void testDeleteWorkdayUser() {
        given()
                .when().auth().oauth2(
                getAccessToken("adam"))
                .contentType("application/json")
                .delete("/3/delete")
                .then()
                .statusCode(403);
    }

    @Test
    void testGetWorkdays() {
        given()
                .when().auth().oauth2(
                getAccessToken("adam"))
                .get()
                .then()
                .statusCode(200)
                .body("size()", Matchers.is(2));

    }

    @Test
    void testGetWorkday() {
        given()
                .when().auth().oauth2(
                getAccessToken("adam"))
                .get("/3")
                .then()
                .statusCode(200)
                .body("\"date\"", Matchers.is("2020-12-28"))
                .body("\"hours\"", Matchers.is(8.5F));
    }

    @Test
    void testGetAllWorkdaysOfEmployeeInRange1() {
        given()
                .when().auth().oauth2(
                getAccessToken("adam"))
                .queryParam("from", "2020-12-20")
                .queryParam("to", "2020-12-27")
                .get("/employee/1/date")
                .then()
                .statusCode(200)
                .body("size()", Matchers.is(1));
    }

    @Test
    void testGetAllWorkdaysOfEmployeeInRange2() {
        given()
                .when().auth().oauth2(
                getAccessToken("adam"))
                .queryParam("from", "2020-12-20")
                .queryParam("to", "2020-12-31")
                .get("/employee/1/date")
                .then()
                .statusCode(200)
                .body("size()", Matchers.is(2));
    }

    @Test
    void testGetAllWorkdaysForOrder() {
        given()
                .when().auth().oauth2(
                getAccessToken("adam"))
                .get("/order/2")
                .then()
                .statusCode(200)
                .body("size()", Matchers.is(2));
    }
}
