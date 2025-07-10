
Feature: User Management
  As an administrator
  I want to manage users in the system
  So that I can maintain user accounts

  Background:
    Given the application is running
    And I have valid authentication token

  Scenario: Create a new user
    When I create a user with username "testuser" and email "test@example.com"
    Then the user should be created successfully
    And the user should have id assigned

  Scenario: Get user by ID
    Given a user exists with username "existinguser" and email "existing@example.com"
    When I request the user by ID
    Then I should receive the user details
    And the username should be "existinguser"

  Scenario: Update user information
    Given a user exists with username "updateuser" and email "update@example.com"
    When I update the user's email to "newemail@example.com"
    Then the user should be updated successfully
    And the user's email should be "newemail@example.com"

  Scenario: Delete a user
    Given a user exists with username "deleteuser" and email "delete@example.com"
    When I delete the user
    Then the user should be deleted successfully
    And the user should not exist in the system

  Scenario: Search users
    Given multiple users exist in the system
    When I search for users with term "test"
    Then I should receive a list of matching users

  Scenario: Deactivate a user
    Given a user exists with username "deactivateuser" and email "deactivate@example.com"
    When I deactivate the user
    Then the user should be deactivated successfully
    And the user should be marked as inactive
