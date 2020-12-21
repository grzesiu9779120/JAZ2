package pl.edu.pjwstk.jaz.zad2;

import io.restassured.http.ContentType;
import org.junit.BeforeClass;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.http.HttpStatus;
import org.springframework.test.context.junit4.SpringRunner;
import pl.edu.pjwstk.jaz.IntegrationTest;

import static io.restassured.RestAssured.given;

@RunWith(SpringRunner.class)
@IntegrationTest
public class AuthenticationTest {


    @BeforeClass
    public static void beforeClass() throws  Exception{
        given()
                .body(new RegisterRequest("admin","admin123")) //admin
                .contentType(ContentType.JSON)
        .when()
                .post("/api/register")
        .thenReturn();


        given()
                .body(new RegisterRequest("user","user123")) //user
                .contentType(ContentType.JSON)
        .when()
                .post("/api/register")
        .thenReturn();
    }
    @Test
    public void tryToLoginRegisteredUserShouldResponseStatus200(){
        given()
        .when()
                .body(new LoginRequest("admin","admin123"))
                .contentType(ContentType.JSON)
                .post("/api/login")
        .then()
                .statusCode(200);

    }
    @Test
    public void tryToLoginUnregisteredUserShouldResponseStatus500(){
        given()
        .when()
                .body(new LoginRequest("adam","adasdasdsa"))
                .contentType(ContentType.JSON)
                .post("/api/login")
        .then()
                .statusCode(HttpStatus.INTERNAL_SERVER_ERROR.value());

    }

    @Test
    public void loginUserAdminAndGetAccessToEndPointOnlyForAdminShouldResponseStatus200(){
       var response =
       given()
       .when()
                .body(new LoginRequest("admin","admin123"))
                .contentType(ContentType.JSON)
                .post("/api/login")
       .thenReturn();

       given()
               .cookies(response.getCookies())
               .get("/api/onlyAdmin")
       .then()
               .statusCode(200);
    }
    @Test
    public void loginDefaultUserAndCheckHisAccessToOnlyAdminShouldResponseStatus403(){
        var response =
         given()
         .when()
                .body(new LoginRequest("user","user123"))
                .contentType(ContentType.JSON)
                .post("/api/login")
         .thenReturn();

        given()
                .cookies(response.getCookies())
                .get("/api/onlyAdmin")
        .then()
                .statusCode(HttpStatus.FORBIDDEN.value());
    }
    @Test
    public void loginDefaultUserAndCheckHisAccessToReadShouldResponseStatus200(){
        var response =
        given()
        .when()
                .body(new LoginRequest("user","user123"))
                .contentType(ContentType.JSON)
                .post("/api/login")
        .thenReturn();

        given()
                .cookies(response.getCookies())
                .get("/api/read")
        .then()
                .statusCode(200);
    }
    @Test
    public void loginAdminUserAndCheckHisAccessToReadShouldResponseStatus200(){
        var response =
        given()
        .when()
                .body(new LoginRequest("admin","admin123"))
                .contentType(ContentType.JSON)
                .post("/api/login")
        .thenReturn();

        given()
                .cookies(response.getCookies())
                .get("/api/read")
        .then()
                .statusCode(200);
    }
    @Test
    public void loginUserAccessToIsReadyResponseStatus200(){
        var response =
        given()
        .when()
                .body(new LoginRequest("admin","admin123"))
                .contentType(ContentType.JSON)
                .post("/api/login")
        .thenReturn();

        given()
                .cookies(response.getCookies())
                .get("/api/auth0/is-ready")
        .then()
                .statusCode(200);
    }
    @Test
    public void accessToPathAuth0withLoginUserShouldResponseStatus200() {
        var response =
        given()
        .when()
                .body(new LoginRequest("admin","admin123"))
                .contentType(ContentType.JSON)
                .post("/api/login")
        .thenReturn();

        given()
                .cookies(response.getCookies())
                .get("/api/auth0/filterUse")
        .then()
                .statusCode(200);
    }
    @Test
    public void accessToPathAuth0WithoutLoggedUserShouldResponseStatusUnauthorised() {
        given()
                .get("/api/auth0/filterUse")
        .then()
                .statusCode(HttpStatus.UNAUTHORIZED.value());
    }
}
