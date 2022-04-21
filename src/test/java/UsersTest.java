import entities.PostUserResponseEntity;
import entities.UserDataEntity;
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

import static consts.ResponseCodes.BAD_REQUEST;
import static consts.ResponseCodes.SUCCESS;
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
        username = getRandomLengthString(1, 100);
        email = getRandomLengthString(1, 100) + "@gmail.com";
        password = getRandomLengthString(1, 100);
    }

    @Test
    public void testUserCreatedAndFoundInGet(){
//        var currentDateTime = LocalDateTime.now();
        var createdUserDetails = createUser(username, email, password);
        var createdAtTime = createdUserDetails.getParsedCreated_at();
        var updatedAtTime = createdUserDetails.getParsedUpdated_at();
//        Period.between(currentDateTime, createdAtTime)

        var usersList = getUsersFromService();
        UserDataEntity userInGetResponse = getUserFromListById(usersList, createdUserDetails.getId());

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
        createUserExpectError(username, new_email, password, BAD_REQUEST, "This username is taken. Try another.");

        var usersList = getUsersFromService();
        UserDataEntity userInGetResponse = getUserFromListById(usersList, createdUserDetails.getId());

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

    private List<UserDataEntity> getUsersFromService(){
        return RestAssured.given().get("/user/get")
                .then().assertThat().statusCode(SUCCESS)
                .extract().body().jsonPath().getList("", UserDataEntity.class);
    }

    private UserDataEntity getUserFromListById(List<UserDataEntity> users, Long UserId){
        UserDataEntity userInGetResponse = null;
        for (UserDataEntity userDataEntity : users) {
            if (userDataEntity.getId().equals(UserId)) {
                userInGetResponse = userDataEntity;
                break;
            }
        }
        return userInGetResponse;
    }

    private UserDataEntity createUser(String userName, String email, String password){
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
