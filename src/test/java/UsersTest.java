import clients.UsersClient;
import entities.UserDataEntity;
import io.restassured.RestAssured;
import org.apache.commons.lang3.RandomStringUtils;
import org.testng.Assert;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import java.util.List;
import java.util.Random;

public class UsersTest {

    private int usersCount = 0;
    private String username;
    private String email;
    private String password;
    private UsersClient usersClient;

    @BeforeClass
    public void setup() {
        usersClient = new UsersClient();
        usersCount = usersClient.getUsersFromService().size();
    }

    @BeforeMethod
    public void beforeMethod(){
        username = getRandomLengthString(1, 100);
        email = getRandomLengthString(1, 100) + "@gmail.com";
        password = getRandomLengthString(1, 100);
    }

    @Test
    public void testUserCreatedAndFoundInGet(){
        var createdUserDetails = usersClient.createUser(username, email, password);
        usersCount++;

        var usersList = usersClient.getUsersFromService();
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
        var createdUserDetails = usersClient.createUser(username, email, password);
        usersCount++;

        usersClient.createUserExpectError(username, new_email, password, "This username is taken. Try another.");

        var usersList = usersClient.getUsersFromService();
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
}
