import io.restassured.RestAssured;
import io.restassured.response.Response;
import io.restassured.specification.RequestSpecification;
import org.apache.commons.lang3.RandomStringUtils;
import org.testng.Assert;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;

import java.util.Random;
import static org.hamcrest.Matchers.*;

public class UsersTest {

    @BeforeClass
    public void setup() {
        RestAssured.baseURI = "http://3.145.97.83";
        RestAssured.port = 3333;
    }

    @Test
    public void testCreateUser(){

        var username = getRandomLengthString(1, 10000);
        var email = getRandomLengthString(1, 10000);
        var password = getRandomLengthString(1, 10000);

        RequestSpecification httpRequest = RestAssured.given()
                .formParam("username",username)
                .formParam("email",email)
                .formParam("password",password);

        Response response = httpRequest.post("/user/create");
        response.then().assertThat().statusCode(200)
                .body("success", equalTo(true))
                .body("message", equalTo("User Successully created"))
                .body("details.email", equalTo(email))
                .body("details.username", equalTo(username))
                .body("details.password", notNullValue());

        PostUserResponseEntity responseDetails =
                response.then().extract().as(PostUserResponseEntity.class);
        var createdUserDetails = responseDetails.getDetails();

        Response getUsersResponse = RestAssured.given().get("/user/get");
        getUsersResponse.then().assertThat().statusCode(200);
        var usersList = getUsersResponse.then().extract().body().jsonPath().getList("", UserData2.class);
        UserData2 userInGetResponse = null;
        for (UserData2 userData2 : usersList) {
            if (userData2.getId().equals(createdUserDetails.getId())) {
                userInGetResponse = userData2;
                break;
            }
        }

        Assert.assertNotNull(userInGetResponse);
        Assert.assertEquals(userInGetResponse.getUsername(), createdUserDetails.getUsername());
        Assert.assertEquals(userInGetResponse.getEmail(), createdUserDetails.getEmail());
        Assert.assertEquals(userInGetResponse.getPassword(), createdUserDetails.getPassword());
        Assert.assertEquals(userInGetResponse.getCreated_at(), createdUserDetails.getCreated_at());
        Assert.assertEquals(userInGetResponse.getUpdated_at(), createdUserDetails.getUpdated_at());
    }

    private String getRandomLengthString(int minLength, int maxLength){
        var random = new Random();
        return RandomStringUtils.random(
                random.nextInt(maxLength - minLength) + minLength,
                true, true);
    }
}
