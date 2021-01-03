package cz.fi.muni.pv217.employee.management.resources;

import cz.fi.muni.pv217.employee.management.OrderResource;
import cz.fi.muni.pv217.employee.management.providers.MockKeycloakTestResource;
import io.quarkus.test.common.QuarkusTestResource;
import io.quarkus.test.common.http.TestHTTPEndpoint;
import io.quarkus.test.h2.H2DatabaseTestResource;
import io.quarkus.test.junit.QuarkusTest;
import io.restassured.response.Response;
import net.minidev.json.JSONObject;
import org.hamcrest.Matchers;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import javax.transaction.Transactional;

import static cz.fi.muni.pv217.employee.management.util.TestUtil.ORDER;
import static cz.fi.muni.pv217.employee.management.util.TestUtil.getAccessToken;
import static io.restassured.RestAssured.given;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.is;

@QuarkusTest
@QuarkusTestResource(MockKeycloakTestResource.class)
@QuarkusTestResource(H2DatabaseTestResource.class)
@TestHTTPEndpoint(OrderResource.class)
public class OrderResourceTest {

    @BeforeEach
    @Transactional
    public void init(){
        ORDER.id = null;
        ORDER.persist();
    }

    @Test
    void testCreateOrderUser() {
        given()
                .when().auth().oauth2(
                getAccessToken("adam"))
                .contentType("application/json")
                .post("/create")
                .then()
                .statusCode(403);
    }

    @Test
    void testUpdateOrderUser() {
        given()
                .when().auth().oauth2(
                getAccessToken("adam"))
                .contentType("application/json")
                .body(new JSONObject().toJSONString())
                .put("/1/update")
                .then()
                .statusCode(403);
    }

    @Test
    void testDeleteOrderUser() {
        given()
                .when().auth().oauth2(
                getAccessToken("adam"))
                .contentType("application/json")
                .delete("/1/delete")
                .then()
                .statusCode(403);
    }

    @Test
    void testGetOrdersUser() {
        Response response =
        given()
                .when().auth().oauth2(
                getAccessToken("adam"))
                .get();
        response.prettyPrint();
//                .then()
//                .statusCode(200)
//                .body("size()", Matchers.is(1));
    }

    @Test
    void testGetOrderUser() {
        given()
                .when().auth().oauth2(
                getAccessToken("adam"))
                .get("/1")
                .then()
                .statusCode(200)
                .body("\"name\"", Matchers.is("Jozef"))
                .body("\"surname\"", Matchers.is("Orac"));
    }

    @Test
    void testSearchOrdersUser() {
        given()
                .when().auth().oauth2(
                getAccessToken("adam"))
                .queryParam("search", "Or")
                .get("/search")
                .then()
                .statusCode(200)
                .body("size()", Matchers.is(1));
    }

    @Test
    @Transactional
    void testCreateOrderAdmin() {
        JSONObject requestParams = new JSONObject();
        requestParams.put("name", "Igor");
        requestParams.put("surname", "Biely");
        requestParams.put("mobile", "0902000000");
        requestParams.put("state", "PENDING");
        requestParams.put("info", "Zapoj internet");

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
                .body(    "name", is("Igor"),
                        "surname", is("Biely"),
                        "mobile", is("0902000000"),
                        "state", is("PENDING"),
                        "info", equalTo("Zapoj internet"));
    }

    @Test
    @Transactional
    void testUpdateOrderAdmin() {
        JSONObject requestParams = new JSONObject();
        requestParams.put("mobile", "0902000000");
        requestParams.put("state", "PENDING");
        requestParams.put("info", "Zapoj internet");


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
                .body( "mobile", is("0902000000"),
                        "state", is("PENDING"),
                        "info", equalTo("Zapoj internet"));
    }

    @Test
    @Transactional
    void testDeleteOrderAdmin() {
        given()
                .when().auth().oauth2(
                getAccessToken("martin", "admin"))
                .contentType("application/json")
                .delete("/1/delete")
                .then()
                .statusCode(200);

        given()
                .when().auth().oauth2(
                getAccessToken("martin", "admin"))
                .get()
                .then()
                .statusCode(200)
                .body("size()", Matchers.is(0));
    }

    @Test
    void testGetOrdersAdmin() {
        given()
                .when().auth().oauth2(
                getAccessToken("martin", "admin"))
                .get()
                .then()
                .statusCode(200)
                .body("size()", Matchers.is(1));
    }

    @Test
    void testGetOrderAdmin() {
        given()
                .when().auth().oauth2(
                getAccessToken("martin", "admin"))
                .contentType("application/json")
                .get("/1")
                .then()
                .statusCode(200)
                .body("\"name\"", Matchers.is("Jozef"))
                .body("\"surname\"", Matchers.is("Orac"));
    }

    @Test
    void testSearchOrdersAdmin() {
        given()
                .when().auth().oauth2(
                getAccessToken("martin", "admin"))
                .contentType("application/json")
                .queryParam("search", "Or")
                .get("/search")
                .then()
                .statusCode(200)
                .body("size()", Matchers.is(1));
    }
}
