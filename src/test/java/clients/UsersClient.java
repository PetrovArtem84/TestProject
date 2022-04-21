package clients;

import entities.PostUserResponseEntity;
import entities.UserDataEntity;
import io.restassured.RestAssured;
import io.restassured.response.ValidatableResponse;
import io.restassured.specification.RequestSpecification;

import java.util.List;

import static consts.ResponseCodes.BAD_REQUEST;
import static consts.ResponseCodes.SUCCESS;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.notNullValue;

public class UsersClient {

    public UsersClient(){
        RestAssured.baseURI = "http://3.145.97.83";
        RestAssured.port = 3333;
    }

    public void createUserExpectError(String userName,
                                      String email,
                                      String password,
                                      String expectedMessage) {
        RequestSpecification httpRequest = RestAssured.given()
                .formParam("username", userName)
                .formParam("email", email)
                .formParam("password", password);

        httpRequest.post("/user/create")
                .then().assertThat().statusCode(BAD_REQUEST)
                .body("success", equalTo(false))
                .body("message[0]", equalTo(expectedMessage));
    }

    public UserDataEntity createUser(String userName, String email, String password){
        RequestSpecification httpRequest = RestAssured.given()
                .formParam("username", userName)
                .formParam("email", email)
                .formParam("password", password);

        ValidatableResponse response = httpRequest.post("/user/create")
                .then().assertThat().statusCode(SUCCESS)
                .body("success", equalTo(true))
                .body("message", equalTo("User Successully created"))
                .body("details.email", equalTo(email))
                .body("details.username", equalTo(userName))
                .body("details.password", notNullValue())
                .body("details.created_at", notNullValue())
                .body("details.updated_at", notNullValue());

        PostUserResponseEntity responseDetails =
                response.extract().as(PostUserResponseEntity.class);
//        usersCount += 1;
        return responseDetails.getDetails();
    }

    public List<UserDataEntity> getUsersFromService(){
        return RestAssured.given().get("/user/get")
                .then().assertThat().statusCode(SUCCESS)
                .extract().body().jsonPath().getList("", UserDataEntity.class);
    }
}
