package application;

import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

import java.sql.SQLException;

import databasePart1.*;

/**
 * The UserLoginPage class provides a login interface for users to access their accounts.
 * It validates the user's credentials and navigates to the appropriate page upon successful login.
 */
public class UserLoginPage {

	private final DatabaseHelper databaseHelper;

	public UserLoginPage(DatabaseHelper databaseHelper) {
		this.databaseHelper = databaseHelper;
	}

	public void show(Stage primaryStage) {
		// Input field for the user's userName, password
		TextField userNameField = new TextField();
		userNameField.setPromptText("Enter userName");
		userNameField.setMaxWidth(250);

		PasswordField passwordField = new PasswordField();
		passwordField.setPromptText("Enter Password");
		passwordField.setMaxWidth(250);

		// Label to display error messages
		Label errorLabel = new Label();
		errorLabel.setStyle("-fx-text-fill: red; -fx-font-size: 12px;");


		Button loginButton = new Button("Login");

		loginButton.setOnAction(a -> {
			// Retrieve user inputs
			String userName = userNameField.getText();
			String password = passwordField.getText();

			// Strings to display error message to user for invalid usernames and passwords
			String userNameErrMessage = UserNameRecognizer.checkForValidUserName(userName);  
			String passwordErrMessage = PasswordRecognizer.evaluatePassword(password);
			String errorMessages = "";

			try {

				// Check if the username is valid
				if(userNameErrMessage != "") {
					errorMessages += userNameErrMessage;
				}

				// Check if the password is valid
				if(passwordErrMessage != "") {
					errorMessages += passwordErrMessage += "\n";
				}


				User user=new User(userName, password, "");
				WelcomeLoginPage welcomeLoginPage = new WelcomeLoginPage(databaseHelper);


				// Retrieve the user's role from the database using userName
				String role = databaseHelper.getUserRole(userName);

				if(role!=null) {
					user.setRole(role);
					if(databaseHelper.login(user)) {
						welcomeLoginPage.show(primaryStage,user);
					}
					else {
						// Display an error if the login fails
						errorMessages += "Error logging in: Incorrect password\n";
					}
				}
				else {
					// Display an error if the account does not exist
					errorMessages += "user account doesn't exist\n";
				}

				errorLabel.setText(errorMessages);

			} catch (SQLException e) {
				System.err.println("Database error: " + e.getMessage());
				e.printStackTrace();
			} 
		});

		VBox layout = new VBox(10);
		layout.setStyle("-fx-padding: 20; -fx-alignment: center;");
		layout.getChildren().addAll(userNameField, passwordField, loginButton, errorLabel);

		primaryStage.setScene(new Scene(layout, 800, 400));
		primaryStage.setTitle("User Login");
		primaryStage.show();
	}
}
