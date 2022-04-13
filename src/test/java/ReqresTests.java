import io.restassured.response.Response;
import io.restassured.response.ResponseBody;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
import org.junit.jupiter.params.provider.ValueSource;

import static io.restassured.RestAssured.given;
import static io.restassured.http.ContentType.JSON;
import static java.lang.String.format;
import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.hamcrest.CoreMatchers.*;

public class ReqresTests {
    String BaseUrl = "https://reqres.in/";

    @CsvSource({
            "1, api/users?page=1, eve.holt@reqres.in",
            "2, api/users?page=2, rachel.howell@reqres.in"
    })
    @DisplayName("Check user list")
    @ParameterizedTest(name = "On page {0}, looking for user with email {2}")
    void listUsers(int page, String url, String email) {

//        String pathUrl = format("https://reqres.in/api/users?page='%s'", url);

        given()
                .when()
                .get(BaseUrl + url)
                .then()
                .statusCode(200)
                .body("page", is(page))
                .body("data.email", hasItem(email));
    }


    @CsvSource({
            "1, api/users/1, george.bluth@reqres.in",
            "2, api/users/2, janet.weaver@reqres.in"
    })
    @DisplayName("Check single users")
    @ParameterizedTest(name = "Check {0} user, it`s should be owner of {2}")
    void singleUsers(int id, String url, String email) {

        given()
                .when()
                .get(BaseUrl + url)
                .then()
                .statusCode(200)
                .body("data.id", is(id))
                .body("data.email", is(email));

    }


    @ValueSource(strings = {"1", "2", "111", "40000000000"})
    @DisplayName("Single User not found")
    @ParameterizedTest (name = "Search user number {0}")
    void singleUserNotFound(String  idUser) {

        given()
                .when()
                .get(BaseUrl + "api/users/" + idUser)
                .then()
                .statusCode(404)
                .body(is("{}"));

    }


    @DisplayName("Create new user")
    @Test
    void createNewUser() {
        String data = "{ \"name\": \"Harry\", \"job\": \"Magician\" }";

        given()
                .contentType(JSON)
                .body(data)
                .when()
                .post(BaseUrl + "api/users")
                .then()
                .statusCode(201)
                .body("name", is("Harry"))
                .body("job", is("Magician"));
    }


    @DisplayName("Update user with PUT method")
    @Test
    void updateNewUserWithPUT() {
        String data = "{ \"name\": \"Harry\", \"job\": \"Troublemaker\" }";

        given()
                .contentType(JSON)
                .body(data)
                .when()
                .put(BaseUrl + "api/users")
                .then()
                .statusCode(200)
                .body("name", is("Harry"))
                .body("job", is("Troublemaker"));
    }


    @DisplayName("Update user with PATCH method")
    @Test
    void updateNewUserWithPATCH() {
        String data = "{ \"name\": \"Harry\", \"job\": \"Auror\" }";

        given()
                .contentType(JSON)
                .body(data)
                .when()
                .patch(BaseUrl + "api/users")
                .then()
                .statusCode(200)
                .body("name", is("Harry"))
                .body("job", is ("Auror"));
    }


    @DisplayName("Delete user")
    @Test
    void deleteUser() {
    Response response =
       given()
               .when()
               .delete(BaseUrl + "api/users/2")
               .then()
               .statusCode(204)
               .extract().response();
        System.out.println("Response should be empty: " + response.asString());


    }


    @DisplayName("Successful registration")
    @Test
    void successfulRegistration() {
        String data = "{ \"email\": \"eve.holt@reqres.in\", \"password\": \"pistol\" }";

        String response =
                given()
                        .contentType(JSON)
                        .body(data)
                        .when()
                        .post(BaseUrl + "api/register")
                        .then()
                        .statusCode(200)
                        .extract().response().path("token");

        assertThat(response).isEqualTo("QpwL5tke4Pnpja7X4");
    }


    @DisplayName("Unsuccessful registration")
    @Test
    void unsuccessfulRegistration(){
        String data = "{ \"email\": \"sydney@fife\" }";

        String response =
        given()
                .contentType(JSON)
                .body(data)
                .when()
                .post(BaseUrl + "api/register")
                .then()
                .statusCode(400)
                .extract().response().path("error");

        assertThat(response).isEqualTo("Missing password");
    }


    @DisplayName("Successful login")
    @Test
    void successfulLogin() {
        String data = "{ \"email\": \"eve.holt@reqres.in\", \"password\": \"cityslicka\" }";

        given()
                .contentType(JSON)
                .body(data)
                .when()
                .post(BaseUrl + "api/login")
                .then()
                .statusCode(200)
                .body("token", is("QpwL5tke4Pnpja7X4"));
    }


    @DisplayName("Unsuccessful login")
    @Test
    void unsuccessfulLogin() {
        String data = "{ \"email\": \"peter@klaven\" }";

        String response =
        given()
                .contentType(JSON)
                .body(data)
                .when()
                .post(BaseUrl + "api/login")
                .then()
                .statusCode(400)
                .extract().response().path("error");

        assertThat(response).isEqualTo("Missing password");
    }


}
