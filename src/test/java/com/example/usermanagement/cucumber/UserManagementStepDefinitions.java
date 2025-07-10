
package com.example.usermanagement.cucumber;

import com.example.usermanagement.entity.User;
import com.example.usermanagement.repository.UserRepository;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.cucumber.java.en.Given;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.http.*;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

public class UserManagementStepDefinitions {

    @LocalServerPort
    private int port;

    @Autowired
    private TestRestTemplate restTemplate;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private ObjectMapper objectMapper;

    private ResponseEntity<String> lastResponse;
    private User testUser;
    private Long testUserId;
    private HttpHeaders headers;

    @Given("the application is running")
    public void theApplicationIsRunning() {
        // Application is already running due to @SpringBootTest
        assertNotNull(restTemplate);
    }

    @Given("I have valid authentication token")
    public void iHaveValidAuthenticationToken() {
        headers = new HttpHeaders();
        headers.set("Authorization", "Bearer valid-token-123");
        headers.setContentType(MediaType.APPLICATION_JSON);
    }

    @When("I create a user with username {string} and email {string}")
    public void iCreateAUserWithUsernameAndEmail(String username, String email) {
        testUser = new User();
        testUser.setUsername(username);
        testUser.setEmail(email);
        testUser.setPassword("password123");
        testUser.setFirstName("Test");
        testUser.setLastName("User");

        HttpEntity<User> request = new HttpEntity<>(testUser, headers);
        lastResponse = restTemplate.postForEntity(
            "http://localhost:" + port + "/api/users",
            request,
            String.class
        );
    }

    @Then("the user should be created successfully")
    public void theUserShouldBeCreatedSuccessfully() {
        assertEquals(HttpStatus.CREATED, lastResponse.getStatusCode());
    }

    @Then("the user should have id assigned")
    public void theUserShouldHaveIdAssigned() throws Exception {
        User createdUser = objectMapper.readValue(lastResponse.getBody(), User.class);
        assertNotNull(createdUser.getId());
        testUserId = createdUser.getId();
    }

    @Given("a user exists with username {string} and email {string}")
    public void aUserExistsWithUsernameAndEmail(String username, String email) {
        testUser = new User();
        testUser.setUsername(username);
        testUser.setEmail(email);
        testUser.setPassword("password123");
        testUser.setFirstName("Test");
        testUser.setLastName("User");

        HttpEntity<User> request = new HttpEntity<>(testUser, headers);
        ResponseEntity<String> response = restTemplate.postForEntity(
            "http://localhost:" + port + "/api/users",
            request,
            String.class
        );

        try {
            User createdUser = objectMapper.readValue(response.getBody(), User.class);
            testUserId = createdUser.getId();
        } catch (Exception e) {
            fail("Failed to create test user: " + e.getMessage());
        }
    }

    @When("I request the user by ID")
    public void iRequestTheUserByID() {
        HttpEntity<String> request = new HttpEntity<>(headers);
        lastResponse = restTemplate.exchange(
            "http://localhost:" + port + "/api/users/" + testUserId,
            HttpMethod.GET,
            request,
            String.class
        );
    }

    @Then("I should receive the user details")
    public void iShouldReceiveTheUserDetails() {
        assertEquals(HttpStatus.OK, lastResponse.getStatusCode());
    }

    @Then("the username should be {string}")
    public void theUsernameShouldBe(String expectedUsername) throws Exception {
        User user = objectMapper.readValue(lastResponse.getBody(), User.class);
        assertEquals(expectedUsername, user.getUsername());
    }

    @When("I update the user's email to {string}")
    public void iUpdateTheUsersEmailTo(String newEmail) {
        testUser.setEmail(newEmail);
        HttpEntity<User> request = new HttpEntity<>(testUser, headers);
        lastResponse = restTemplate.exchange(
            "http://localhost:" + port + "/api/users/" + testUserId,
            HttpMethod.PUT,
            request,
            String.class
        );
    }

    @Then("the user should be updated successfully")
    public void theUserShouldBeUpdatedSuccessfully() {
        assertEquals(HttpStatus.OK, lastResponse.getStatusCode());
    }

    @Then("the user's email should be {string}")
    public void theUsersEmailShouldBe(String expectedEmail) throws Exception {
        User user = objectMapper.readValue(lastResponse.getBody(), User.class);
        assertEquals(expectedEmail, user.getEmail());
    }

    @When("I delete the user")
    public void iDeleteTheUser() {
        HttpEntity<String> request = new HttpEntity<>(headers);
        lastResponse = restTemplate.exchange(
            "http://localhost:" + port + "/api/users/" + testUserId,
            HttpMethod.DELETE,
            request,
            String.class
        );
    }

    @Then("the user should be deleted successfully")
    public void theUserShouldBeDeletedSuccessfully() {
        assertEquals(HttpStatus.OK, lastResponse.getStatusCode());
    }

    @Then("the user should not exist in the system")
    public void theUserShouldNotExistInTheSystem() {
        Optional<User> user = userRepository.findById(testUserId);
        assertFalse(user.isPresent());
    }

    @Given("multiple users exist in the system")
    public void multipleUsersExistInTheSystem() {
        // Create test users
        for (int i = 1; i <= 3; i++) {
            User user = new User();
            user.setUsername("testuser" + i);
            user.setEmail("test" + i + "@example.com");
            user.setPassword("password123");
            
            HttpEntity<User> request = new HttpEntity<>(user, headers);
            restTemplate.postForEntity(
                "http://localhost:" + port + "/api/users",
                request,
                String.class
            );
        }
    }

    @When("I search for users with term {string}")
    public void iSearchForUsersWithTerm(String searchTerm) {
        HttpEntity<String> request = new HttpEntity<>(headers);
        lastResponse = restTemplate.exchange(
            "http://localhost:" + port + "/api/users/search?q=" + searchTerm,
            HttpMethod.GET,
            request,
            String.class
        );
    }

    @Then("I should receive a list of matching users")
    public void iShouldReceiveAListOfMatchingUsers() throws Exception {
        assertEquals(HttpStatus.OK, lastResponse.getStatusCode());
        List<?> users = objectMapper.readValue(lastResponse.getBody(), List.class);
        assertTrue(users.size() > 0);
    }

    @When("I deactivate the user")
    public void iDeactivateTheUser() {
        HttpEntity<String> request = new HttpEntity<>(headers);
        lastResponse = restTemplate.exchange(
            "http://localhost:" + port + "/api/users/" + testUserId + "/deactivate",
            HttpMethod.PATCH,
            request,
            String.class
        );
    }

    @Then("the user should be deactivated successfully")
    public void theUserShouldBeDeactivatedSuccessfully() {
        assertEquals(HttpStatus.OK, lastResponse.getStatusCode());
    }

    @Then("the user should be marked as inactive")
    public void theUserShouldBeMarkedAsInactive() {
        Optional<User> user = userRepository.findById(testUserId);
        assertTrue(user.isPresent());
        assertFalse(user.get().getIsActive());
    }
}
