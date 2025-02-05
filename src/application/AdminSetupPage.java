package application;

import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

import java.sql.SQLException;

import databasePart1.*;

/**
 * The SetupAdmin class handles the setup process for creating an administrator account.
 * This is intended to be used by the first user to initialize the system with admin credentials.
 */
public class AdminSetupPage {

	private final DatabaseHelper databaseHelper;

	public AdminSetupPage(DatabaseHelper databaseHelper) {
		this.databaseHelper = databaseHelper;
	}

	public void show(Stage primaryStage) {
		// Input fields for userName and password
		TextField userNameField = new TextField();
		userNameField.setPromptText("Enter Admin userName");
		userNameField.setMaxWidth(250);

		PasswordField passwordField = new PasswordField();
		passwordField.setPromptText("Enter Password");
		passwordField.setMaxWidth(250);

		Button setupButton = new Button("Setup");

		// Label to display error messages for invalid input or registration issues
		Label errorLabel = new Label();
		errorLabel.setStyle("-fx-text-fill: red; -fx-font-size: 12px;");

		setupButton.setOnAction(a -> {
			// Retrieve user input
			String userName = userNameField.getText();
			String password = passwordField.getText();

			// Strings to display error message to user for invalid usernames and passwords
			// SetUpError is used to prevent blank logins
			String userNameErrMessage = UserNameRecognizer.checkForValidUserName(userName);  
			String passwordErrMessage = PasswordRecognizer.evaluatePassword(password);
			String errorMessages = "";
			Boolean setUpError = false;

			try {
				// Check if the username is valid
				if(userNameErrMessage != "") {
					errorMessages += userNameErrMessage;
					setUpError = true;
				}

				// Check if the password is valid
				if(passwordErrMessage != "") {
					errorMessages += passwordErrMessage += "\n";
					setUpError = true;
				}

				// Check if the username or password is invalid
				if (!setUpError) {
					// Check if the user already exists
					if(!databaseHelper.doesUserExist(userName)) {

						// Create a new User object with admin role and register in the database
						User user=new User(userName, password, "admin");
						databaseHelper.register(user);
						System.out.println("Administrator setup completed.");

						// Navigate to the Welcome Login Page
						new WelcomeLoginPage(databaseHelper).show(primaryStage,user);
					} else {
						errorMessages += "This useruserName is taken!!.. Please use another to setup an account\n";
					}
				}

				errorLabel.setText(errorMessages);


			} catch (SQLException e) {
				System.err.println("Database error: " + e.getMessage());
				e.printStackTrace();
			}
		});

		VBox layout = new VBox(10, userNameField, passwordField, setupButton, errorLabel);
		layout.setStyle("-fx-padding: 20; -fx-alignment: center;");

		primaryStage.setScene(new Scene(layout, 800, 400));
		primaryStage.setTitle("Administrator Setup");
		primaryStage.show();
	}
}
