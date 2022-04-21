import io.restassured.RestAssured;
import io.restassured.response.ValidatableResponse;
import io.restassured.specification.RequestSpecification;
import org.apache.commons.lang3.RandomStringUtils;
import org.testng.Assert;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import java.util.List;
import java.util.Random;
import static org.hamcrest.Matchers.*;

public class UsersTest {

    private int usersCount = 0;
    private String username;
    private String email;
    private String password;

    @BeforeClass
    public void setup() {
        RestAssured.baseURI = "http://3.145.97.83";
        RestAssured.port = 3333;
        usersCount = getUsersFromService().size();
    }

    @BeforeMethod
    public void beforeMethod(){
        username = getRandomLengthString(1, 10000);
        email = getRandomLengthString(1, 10000) + "@gmail.com";
        password = getRandomLengthString(1, 10000);
    }

    @Test
    public void testUserCreatedAndFoundInGet(){
        var createdUserDetails = createUser(username, email, password);
        //todo date validations should be added
        var usersList = getUsersFromService();
        UserData2 userInGetResponse = getUserFromListById(usersList, createdUserDetails.getId());

        Assert.assertNotNull(userInGetResponse, "Expected created user in get response and found by id");
        Assert.assertEquals(usersList.size(), usersCount, "users count mismatch, expected increased by 1");
        Assert.assertEquals(userInGetResponse.getUsername(), createdUserDetails.getUsername(), "usernames mismatch");
        Assert.assertEquals(userInGetResponse.getEmail(), createdUserDetails.getEmail(), "emails mismatch");
        Assert.assertEquals(userInGetResponse.getPassword(), createdUserDetails.getPassword(), "passwords mismatch");
        Assert.assertEquals(userInGetResponse.getCreated_at(), createdUserDetails.getCreated_at(), "created date mismatch");
        Assert.assertEquals(userInGetResponse.getUpdated_at(), createdUserDetails.getUpdated_at(), "updated date mismatch");
    }

    @Test
    public void testUserSameNameIsNotCreatedTwice(){
        var new_email = getRandomLengthString(1, 100);
        var createdUserDetails = createUser(username, email, password);
        createUserExpectError(username, new_email, password, 400, "This username is taken. Try another.");

        var usersList = getUsersFromService();
        UserData2 userInGetResponse = getUserFromListById(usersList, createdUserDetails.getId());

        Assert.assertNotNull(userInGetResponse, "Expected created user in get response and found by id");
        Assert.assertEquals(usersList.size(), usersCount, "users count mismatch, expected increased by 1");
        Assert.assertEquals(userInGetResponse.getUsername(), createdUserDetails.getUsername(), "usernames mismatch");
        Assert.assertEquals(userInGetResponse.getEmail(), createdUserDetails.getEmail(), "emails mismatch");
        Assert.assertEquals(userInGetResponse.getPassword(), createdUserDetails.getPassword(), "passwords mismatch");
        Assert.assertEquals(userInGetResponse.getCreated_at(), createdUserDetails.getCreated_at(), "created date mismatch");
        Assert.assertEquals(userInGetResponse.getUpdated_at(), createdUserDetails.getUpdated_at(), "updated date mismatch");
    }
    private String getRandomLengthString(int minLength, int maxLength){
        var random = new Random();
        return RandomStringUtils.random(
                random.nextInt(maxLength - minLength) + minLength,
                true, true);
    }

    private List<UserData2> getUsersFromService(){
        return RestAssured.given().get("/user/get")
                .then().assertThat().statusCode(200)
                .extract().body().jsonPath().getList("", UserData2.class);
    }

    private UserData2 getUserFromListById(List<UserData2> users, Long UserId){
        UserData2 userInGetResponse = null;
        for (UserData2 userData2 : users) {
            if (userData2.getId().equals(UserId)) {
                userInGetResponse = userData2;
                break;
            }
        }
        return userInGetResponse;
    }

    private UserData2 createUser(String userName, String email, String password){
        RequestSpecification httpRequest = RestAssured.given()
                .formParam("username", userName)
                .formParam("email", email)
                .formParam("password", password);

        ValidatableResponse response = httpRequest.post("/user/create")
                .then().assertThat().statusCode(200)
                .body("success", equalTo(true))
                .body("message", equalTo("User Successully created"))
                .body("details.email", equalTo(email))
                .body("details.username", equalTo(userName))
                .body("details.password", notNullValue());

        PostUserResponseEntity responseDetails =
                response.extract().as(PostUserResponseEntity.class);
        usersCount += 1;
        return responseDetails.getDetails();
    }

    private void createUserExpectError(String userName,
                                       String email,
                                       String password,
                                       int expectedCode,
                                       String expectedMessage) {
        RequestSpecification httpRequest = RestAssured.given()
                .formParam("username", userName)
                .formParam("email", email)
                .formParam("password", password);

        httpRequest.post("/user/create")
                .then().assertThat().statusCode(expectedCode)
                .body("success", equalTo(false))
                .body("message[0]", equalTo(expectedMessage));
    }
}
