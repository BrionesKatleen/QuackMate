import backEnd.models.User;
import backEnd.services.UserService;
import backEnd.services.sessionManager;
import database.DB;
import java.sql.SQLException;

import javafx.application.Application;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.stage.Stage;
import javafx.scene.media.Media;
import javafx.scene.media.MediaPlayer;
import javafx.scene.text.Font;

public class Main extends Application {

    private static MediaPlayer mediaPlayer; // persistent music
    private double happiness = 0.60;
    private double hunger = 0.70;
    private double energy = 0.50;
    private double cleanliness = 0.40;
    private ImageView foodDisplay;
    private int foodIndex = 0;
    private final String[] foods = {"peas.png", "birdseed.png", "corn.png", "oats.png"};

    @Override
    public void start(Stage stage) {

        Label userLabel = new Label("Username");
        userLabel.getStyleClass().add("form-label");
        userLabel.setFont(Font.font(10));
        userLabel.setMaxWidth(Double.MAX_VALUE);
        userLabel.setAlignment(Pos.CENTER);

        Label passLabel = new Label("Password");
        passLabel.getStyleClass().add("form-label");
        passLabel.setFont(Font.font(10));

        TextField usernameField = new TextField();
        usernameField.setPromptText("Enter username");
        usernameField.setMaxWidth(150);
        usernameField.setPrefWidth(150);
        usernameField.setStyle("-fx-font-size: 11px; -fx-pref-height: 22px;");
        usernameField.getStyleClass().add("login-field");

        VBox userBox = new VBox(4, userLabel, usernameField);
        userBox.setAlignment(Pos.CENTER);
        userBox.setLayoutX(130);
        userBox.setLayoutY(240);

        PasswordField passwordField = new PasswordField();
        passwordField.setPromptText("Enter password");
        passwordField.setMaxWidth(150);
        passwordField.setPrefWidth(150);
        passwordField.setStyle("-fx-font-size: 11px; -fx-pref-height: 22px;");
        passwordField.getStyleClass().add("login-field");

        VBox passBox = new VBox(4, passLabel, passwordField);
        passBox.setAlignment(Pos.CENTER);
        passBox.setLayoutX(130);
        passBox.setLayoutY(300);

        Label error = new Label();
        error.getStyleClass().add("error-label");
        error.setVisible(false);
        error.setLayoutX(140);
        error.setLayoutY(350);

        Button loginButton = new Button("🦆 Sign In");
        loginButton.getStyleClass().add("login-button");
        loginButton.setPrefWidth(110);
        loginButton.setPrefHeight(26);
        loginButton.setStyle("-fx-font-size: 11px;");
        loginButton.setLayoutX(150);
        loginButton.setLayoutY(380);
        loginButton.setOnAction(e -> {
            String username = usernameField.getText().trim(); // CHANGED
            String password = passwordField.getText().trim(); // CHANGED

            User user = UserService.login(username, password);

            if(user != null && user.isActive()) {
                // SET CURRENT USER IN SESSION
                sessionManager.getInstance().setCurrentUser(user);

                // UPDATE LAST LOGIN IN DB
                user.updateLastLogin();
                DB.updateUser(user);

                // AUTHENTICATION DONE GOES TO THE HOUSE FRAME
                DuckHouse(stage, username);
            } else {
                error.setText("Invalid Username or Password :(");
                error.setVisible(true);
            }
        });

        Pane layoutPane = new Pane(userBox, passBox, loginButton, error);

        Button signUpBtn = new Button("Don't have an account? Sign Up");
        signUpBtn.getStyleClass().add("signup-button");
        signUpBtn.setFont(Font.font(9));
        signUpBtn.setPadding(new Insets(1, 4, 1, 4));
        signUpBtn.setLayoutX(210);
        signUpBtn.setLayoutY(455);
        signUpBtn.setOnAction(e -> signUpFrame(stage));

        layoutPane.getChildren().add(signUpBtn);

        Image bgImage = new Image(getClass().getResource("/MainLoginGrame.jpg").toExternalForm());
        ImageView bgView = new ImageView(bgImage);
        bgView.setPreserveRatio(false);

        Image logoImage = new Image(getClass().getResource("/ducki.png").toExternalForm());
        ImageView logoView = new ImageView(logoImage);
        logoView.setFitWidth(250);
        logoView.setPreserveRatio(true);
        logoView.setLayoutX(70);
        logoView.setLayoutY(1);

        Pane logoPane = new Pane(logoView);
        logoPane.setMouseTransparent(true);

        StackPane root = new StackPane(bgView, layoutPane, logoPane);
        Scene scene = new Scene(root, 400, 500);
        bgView.fitWidthProperty().bind(scene.widthProperty());
        bgView.fitHeightProperty().bind(scene.heightProperty());
        scene.getStylesheets().add(getClass().getResource("DuckStyle.css").toExternalForm());

        stage.setTitle("QuackMate - Login");
        Image icon = new Image(getClass().getResource("/QuackMate.png").toExternalForm());
        stage.getIcons().add(icon);
        stage.setResizable(false);
        stage.setScene(scene);
        stage.show();

        if (mediaPlayer == null) {
            String musicFile = getClass().getResource("bgmusic.mp3").toExternalForm();
            Media sound = new Media(musicFile);
            mediaPlayer = new MediaPlayer(sound);
            mediaPlayer.setCycleCount(MediaPlayer.INDEFINITE);
            mediaPlayer.play();
        }
    }

    public void signUpFrame(Stage stage) {

        Image bgImage = new Image(getClass().getResource("/MainLoginGrame.jpg").toExternalForm());
        ImageView bgView = new ImageView(bgImage);
        bgView.setPreserveRatio(false);
        bgView.setFitWidth(450);
        bgView.setFitHeight(600);

        Pane root = new Pane(bgView);

        // --- CENTER CONTAINER ---
        VBox centerBox = new VBox(15);
        centerBox.setAlignment(Pos.CENTER);
        centerBox.setPrefWidth(450);           // so it centers horizontally
        centerBox.setLayoutY(170);             // moves the whole block downward

        // Title
        Label title = new Label("Create Account");
        title.getStyleClass().add("title-label");

        // Username
        Label userLabel = new Label("Username");
        userLabel.getStyleClass().add("form-label");
        TextField usernameField = new TextField();
        usernameField.setPromptText("Enter username");
        usernameField.getStyleClass().add("login-field");
        usernameField.setMaxWidth(160);
        VBox userBox = new VBox(3, userLabel, usernameField);
        userBox.setAlignment(Pos.CENTER);

        // Password
        Label passLabel = new Label("Password");
        passLabel.getStyleClass().add("form-label");
        PasswordField passwordField = new PasswordField();
        passwordField.setPromptText("Enter password");
        passwordField.getStyleClass().add("login-field");
        passwordField.setMaxWidth(160);
        VBox passBox = new VBox(3, passLabel, passwordField);
        passBox.setAlignment(Pos.CENTER);

        // Confirm Password
        Label confirmLabel = new Label("Confirm Password");
        confirmLabel.getStyleClass().add("form-label");
        PasswordField confirmField = new PasswordField();
        confirmField.setPromptText("Re-enter password");
        confirmField.getStyleClass().add("login-field");
        confirmField.setMaxWidth(160);
        VBox confirmBox = new VBox(3, confirmLabel, confirmField);
        confirmBox.setAlignment(Pos.CENTER);

        // Error message
        Label error = new Label();
        error.getStyleClass().add("error-label");
        error.setVisible(false);

        // Button
        Button signupButton = new Button("Create");
        signupButton.getStyleClass().add("login-button");
        signupButton.setPrefWidth(130);

        signupButton.setOnAction(e -> {
            String username = usernameField.getText().trim(); // CHANGED
            String password = passwordField.getText();
            String confirm = confirmField.getText();
            String recoveryPin = "1234"; // TEMPORARY
            //String recoveryPin = recoveryPinField.getText(); => Implement this when you have added textfield for
            // recoveryPin

            if (username.isEmpty() || password.isEmpty() || confirm.isEmpty()) {
                error.setText("Please fill out all fields");
                error.setVisible(true);
                return;
            }

            if (!password.equals(confirm)) {
                error.setText("Passwords do not match");
                error.setVisible(true);
                return;
            }

            if(!UserService.validateUsername(username)) {
                error.setText("Invalid username(3-28 characters)");
                error.setVisible(true);
                return;
            }
            if(!UserService.validatePassword(password)) {
                error.setText("Password must be at least 6 characters");
                error.setVisible(true);
                return;
            }

            // ATTEMPT TO REGISTER USER CALL BACKEND
            User newUser = UserService.register(username, password, recoveryPin);
            if(newUser != null) {
                // SET SESSION
                sessionManager.getInstance().setCurrentUser(newUser);
                // GO TO DUCKHOUSE
                DuckHouse(stage, username);
            } else {
                error.setText("Username already exists or registration failed");
                error.setVisible(true);
            }
        });

        // Add all elements to center box
        centerBox.getChildren().addAll(title, userBox, passBox, confirmBox, error, signupButton);
        root.getChildren().add(centerBox);

        Scene scene = new Scene(root, 450, 600);
        scene.getStylesheets().add(getClass().getResource("DuckStyle.css").toExternalForm());
        stage.setScene(scene);
        stage.show();
    }



    private void DuckHouse(Stage stage, String username) {
        sceneTemplate(stage, "balay.png", username);
    }

    private void kitchen(Stage stage, String username) {

        BorderPane layout = sceneTemplate(stage, "eat.png", username);

        // ====== FOOD + BUTTON HOLDER (FREE POSITIONING) ======
        Pane foodPane = new Pane();
        foodPane.setPrefSize(100, 100);

        // ====== FOOD IMAGE ======
        Image img = new Image(getClass().getResourceAsStream(foods[foodIndex]));
        foodDisplay = new ImageView(img);
        foodDisplay.setFitWidth(90);   // <-- SMALLER FOOD SIZE
        foodDisplay.setPreserveRatio(true);

        // >>> CHANGE FOOD POSITION HERE <<<
        foodDisplay.setLayoutX(55);
        foodDisplay.setLayoutY(130);

        // ====== LEFT BUTTON ======
        Button btnLeft = new Button();
        btnLeft.getStyleClass().add("arrow-button-left");
        btnLeft.setPrefSize(30, 30);
        btnLeft.setOnAction(e -> {
            foodIndex = (foodIndex - 1 + foods.length) % foods.length;
            updateFoodImage();
        });

        btnLeft.setLayoutX(20);
        btnLeft.setLayoutY(200);

        // ====== RIGHT BUTTON ======
        Button btnRight = new Button();
        btnRight.getStyleClass().add("arrow-button-right");
        btnRight.setPrefSize(30, 30);
        btnRight.setOnAction(e -> {
            foodIndex = (foodIndex + 1) % foods.length;
            updateFoodImage();
        });

        btnRight.setLayoutX(140);
        btnRight.setLayoutY(200);

        // Add to pane
        foodPane.getChildren().addAll(foodDisplay, btnLeft, btnRight);

        // Add pane to layout center (background stays eat.png)
        layout.setCenter(foodPane);

        stage.setScene(new Scene(layout, 400, 500));
        stage.show();
    }

    private void updateFoodImage() {
        Image img = new Image(getClass().getResourceAsStream(foods[foodIndex]));
        foodDisplay.setImage(img);
    }

    private void bathRoom(Stage stage, String username) {
        BorderPane layout = sceneTemplate(stage, "cr.png", username);

        Image brush = new Image(getClass().getResource("water (1).png").toExternalForm());
        Image bucket = new Image(getClass().getResource("scrub.png").toExternalForm());

        ImageView brushView = new ImageView(brush);
        ImageView bucketView = new ImageView(bucket);

        brushView.setFitHeight(80);
        brushView.setFitWidth(80);
        bucketView.setFitHeight(80);
        bucketView.setFitWidth(80);

        Button brushBtn = new Button();
        brushBtn.setGraphic(brushView);
        brushBtn.setStyle("-fx-background-color: transparent;");
        brushBtn.setOnAction(e -> System.out.println("Nag banlaw ang Itik"));

        Button bucketBtn = new Button();
        bucketBtn.setGraphic(bucketView);
        bucketBtn.setStyle("-fx-background-color: transparent;");
        bucketBtn.setOnAction(e -> System.out.println("Nag lugod ang itik"));

        StackPane root = (StackPane) stage.getScene().getRoot();
        StackPane.setAlignment(bucketBtn, Pos.CENTER_RIGHT);
        StackPane.setMargin(bucketBtn, new Insets(0, 0, 100, 20));
        StackPane.setAlignment(brushBtn, Pos.CENTER_LEFT);
        StackPane.setMargin(brushBtn, new Insets(0, 20, 100, 0));
        root.getChildren().addAll(bucketBtn, brushBtn);
    }

    private void bedRoom(Stage stage, String username) {
        BorderPane layout = sceneTemplate(stage, "room.png", username);

        Image lampOff = new Image(getClass().getResource("lambing.png").toExternalForm());
        Image lampOn = new Image(getClass().getResource("lamning.png").toExternalForm());
        Image bgOff = new Image(getClass().getResource("nightver.png").toExternalForm());
        Image bgOn = new Image(getClass().getResource("room.png").toExternalForm());
        Image awakeDuck = new Image(getClass().getResource("dockie.png").toExternalForm());
        Image sleepingDuck = new Image(getClass().getResource("sleepyhead.png").toExternalForm());

        StackPane root = (StackPane) stage.getScene().getRoot();
        ImageView bgView = (ImageView) root.getChildren().get(0);
        ImageView charac = (ImageView) root.getChildren().get(root.getChildren().size() - 1);

        ImageView lampView = new ImageView(lampOn);
        lampView.setFitWidth(80);
        lampView.setPreserveRatio(true);

        ToggleButton lampButton = new ToggleButton();
        lampButton.setGraphic(lampView);
        lampButton.getStyleClass().add("lamp-button");
        lampButton.setSelected(true);
        charac.setImage(awakeDuck);
        bgView.setImage(bgOn);

        lampButton.setOnAction(e -> {
            if (lampButton.isSelected()) {
                lampView.setImage(lampOn);
                bgView.setImage(bgOn);
                charac.setImage(awakeDuck);
                charac.setFitWidth(150);
                charac.setPreserveRatio(true);
                StackPane.setMargin(charac, new Insets(0, 0, 180, 0));
            } else {
                lampView.setImage(lampOff);
                bgView.setImage(bgOff);
                charac.setImage(sleepingDuck);
                charac.setFitWidth(140);
                charac.setPreserveRatio(true);
                StackPane.setMargin(charac, new Insets(0, 0, 160, 0));
            }
        });

        StackPane.setAlignment(lampButton, Pos.TOP_RIGHT);
        StackPane.setMargin(lampButton, new Insets(60, 30, 0, 0));
        root.getChildren().add(lampButton);
    }

    private void closet(Stage stage, String username) {
        BorderPane layout = sceneTemplate(stage, "closet.png", username);

        Button leftArrow = new Button();
        leftArrow.getStyleClass().add("arrow-button-left");
        leftArrow.setOnAction(e -> System.out.println("Left arrow clicked!"));

        Button rightArrow = new Button();
        rightArrow.getStyleClass().add("arrow-button-right");
        rightArrow.setOnAction(e -> System.out.println("Right arrow clicked!"));

        StackPane root = (StackPane) stage.getScene().getRoot();
        StackPane.setAlignment(leftArrow, Pos.CENTER_LEFT);
        StackPane.setAlignment(rightArrow, Pos.CENTER_RIGHT);
        StackPane.setMargin(leftArrow, new Insets(0, 0, 0, 10));
        StackPane.setMargin(rightArrow, new Insets(0, 10, 0, 0));
        root.getChildren().addAll(leftArrow, rightArrow);
    }

    private BorderPane sceneTemplate(Stage stage, String bgFile, String username) {
        Image backGround = new Image(getClass().getResource(bgFile).toExternalForm());
        ImageView bg = new ImageView(backGround);
        bg.setPreserveRatio(false);

        Image character = new Image(getClass().getResource("dockie.png").toExternalForm());
        ImageView charac = new ImageView(character);
        charac.setFitWidth(150);
        charac.setPreserveRatio(true);

        Button duckMenuBtn = new Button("🦆");
        duckMenuBtn.getStyleClass().add("duck-button");
        duckMenuBtn.setOnAction(e -> statsFrame(stage, username));

        Button topPlayBtn = new Button("🎮");
        topPlayBtn.getStyleClass().add("duck-button");
        topPlayBtn.setOnAction(e -> GamesFrame(stage, username));

        Button settingsBtn = new Button("⚙");
        settingsBtn.getStyleClass().add("mute-button");
        settingsBtn.setOnAction(e -> SettingsFrame(stage, username));

        ToggleButton switchBtn = new ToggleButton();
        switchBtn.getStyleClass().add("switch-button");
        switchBtn.setSelected(false);
        switchBtn.setOnAction(e -> System.out.println(switchBtn.isSelected() ? "Switch ON" : "Switch OFF"));

        HBox topControls = new HBox(10, settingsBtn, switchBtn);
        topControls.setAlignment(Pos.CENTER_LEFT);

        VBox leftStack = new VBox(5, topControls, duckMenuBtn, topPlayBtn);
        leftStack.setAlignment(Pos.TOP_LEFT);

        Button btn1 = new Button("🏠");
        Button btn2 = new Button("🛏");
        Button btn3 = new Button("\uD83C\uDF73");
        Button btn4 = new Button("\uD83E\uDDE5");
        Button btn5 = new Button("🛁");

        Button[] bottomButtons = {btn1, btn2, btn3, btn4, btn5};
        for (Button btn : bottomButtons) {
            btn.getStyleClass().add("duck-button");
            btn.setPrefSize(50, 50);
        }

        btn1.setOnAction(e -> DuckHouse(stage, username));
        btn2.setOnAction(e -> bedRoom(stage, username));
        btn3.setOnAction(e -> kitchen(stage, username));
        btn4.setOnAction(e -> closet(stage, username));
        btn5.setOnAction(e -> bathRoom(stage, username));

        HBox bottomRow = new HBox(20, btn1, btn2, btn3, btn4, btn5);
        bottomRow.setAlignment(Pos.CENTER);
        bottomRow.setPadding(new Insets(10));

        BorderPane layout = new BorderPane();
        layout.setTop(leftStack);
        layout.setBottom(bottomRow);

        StackPane root = new StackPane(bg, layout, charac);
        StackPane.setAlignment(charac, Pos.BOTTOM_CENTER);
        StackPane.setMargin(charac, new Insets(0, 0, 180, 0));

        Scene scene = new Scene(root, 400, 500);
        scene.getStylesheets().add(getClass().getResource("DuckStyle.css").toExternalForm());

        bg.fitWidthProperty().bind(scene.widthProperty());
        bg.fitHeightProperty().bind(scene.heightProperty());

        stage.setScene(scene);
        stage.setResizable(false);
        stage.setMaximized(false);
        stage.show();

        return layout;
    }

    private void GamesFrame(Stage stage, String username) {
        Image bg = new Image(getClass().getResource("/game.png").toExternalForm());
        ImageView backGround = new ImageView(bg);
        backGround.setPreserveRatio(false);

        Image playIcon = new Image(getClass().getResource("/game2.png").toExternalForm());
        ImageView playView = new ImageView(playIcon);
        playView.setFitWidth(200);
        playView.setFitHeight(200);
        playView.setPreserveRatio(true);

        Button picBtn = new Button();
        picBtn.setGraphic(playView);
        picBtn.getStyleClass().add("duck-button");
        picBtn.setStyle("-fx-background-radius: 0; -fx-padding: 0;");
        picBtn.setOnAction(e -> System.out.println("Picture button 1 clicked!"));
        picBtn.setOnMousePressed(e -> { picBtn.setScaleX(0.9); picBtn.setScaleY(0.9); });
        picBtn.setOnMouseReleased(e -> { picBtn.setScaleX(1.0); picBtn.setScaleY(1.0); });

        Image playIcon2 = new Image(getClass().getResource("/duckierun.png").toExternalForm());
        ImageView playView2 = new ImageView(playIcon2);
        playView2.setFitWidth(200);
        playView2.setFitHeight(200);
        playView2.setPreserveRatio(true);

        Button picBtn2 = new Button();
        picBtn2.setGraphic(playView2);
        picBtn2.getStyleClass().add("duck-button");
        picBtn2.setStyle("-fx-background-radius: 0; -fx-padding: 0;");
        picBtn2.setOnAction(e -> System.out.println("Picture button 2 clicked!"));
        picBtn2.setOnMousePressed(e -> { picBtn2.setScaleX(0.9); picBtn2.setScaleY(0.9); });
        picBtn2.setOnMouseReleased(e -> { picBtn2.setScaleX(1.0); picBtn2.setScaleY(1.0); });

        VBox buttonBox = new VBox(20, picBtn, picBtn2);
        buttonBox.setAlignment(Pos.CENTER);

        Button btn1 = new Button("⬅");
        btn1.getStyleClass().addAll("duck-button", "house-button");
        btn1.setPrefSize(50, 50);
        btn1.setOnAction(e -> DuckHouse(stage, username));

        BorderPane layout = new BorderPane();
        layout.setTop(btn1);
        BorderPane.setAlignment(btn1, Pos.TOP_LEFT);
        layout.setCenter(buttonBox);

        StackPane root = new StackPane(backGround, layout);
        Scene scene = new Scene(root, 400, 500, Color.BLACK);
        scene.getStylesheets().add(getClass().getResource("/DuckStyle.css").toExternalForm());

        backGround.fitWidthProperty().bind(scene.widthProperty());
        backGround.fitHeightProperty().bind(scene.heightProperty());

        stage.setScene(scene);
        stage.setResizable(false);
        stage.setMaximized(false);
        stage.show();
    }

    public void SettingsFrame(Stage stage, String username) {

        StackPane root = new StackPane();
        root.setStyle("-fx-background-color: #fff9d6;"); // Set background color

        // Duck image
        Image duckImg = new Image(getClass().getResource("/dockie.png").toExternalForm());
        ImageView duckView = new ImageView(duckImg);
        duckView.setFitWidth(150);
        duckView.setPreserveRatio(true);
        duckView.setLayoutX(130);
        duckView.setLayoutY(30);

        // Powered by image
        Image poweredImg = new Image(getClass().getResource("powered.png").toExternalForm());
        ImageView poweredBy = new ImageView(poweredImg);
        poweredBy.setFitWidth(200);
        poweredBy.setPreserveRatio(true);

        // i.png image
        Image i = new Image(getClass().getResource("i.png").toExternalForm());
        ImageView itoo = new ImageView(i);
        itoo.setFitWidth(50);
        itoo.setPreserveRatio(true);

        // Buttons
        Button btn1 = new Button("⬅");
        btn1.getStyleClass().addAll("logout-button", "house-button");
        btn1.setPrefSize(50, 50);
        btn1.setOnAction(e -> DuckHouse(stage, username));
        btn1.setLayoutX(10);
        btn1.setLayoutY(10);

        Button btn2 = new Button("Log Out");
        btn2.getStyleClass().addAll("logout-button");
        btn2.setPrefSize(125, 35);
        btn2.setLayoutX(140);
        btn2.setLayoutY(230);
        btn2.setAlignment(Pos.CENTER);
        btn2.setOnAction(e -> start(stage));

        // ====== Mute Button Added Here ======
        Button muteBtn = new Button("🔇");
        muteBtn.getStyleClass().add("mute-button");
        muteBtn.setPrefSize(50, 50);
        muteBtn.setLayoutX(330);
        muteBtn.setLayoutY(10);
        muteBtn.setOnAction(e -> {
            if (mediaPlayer != null) {
                mediaPlayer.setMute(!mediaPlayer.isMute());
                muteBtn.setText(mediaPlayer.isMute() ? "🔇" : "🔊");
            }
        });

        Pane buttonPane = new Pane(btn1, btn2, duckView, muteBtn);

        // Add all to root
        root.getChildren().addAll(buttonPane, poweredBy, itoo);

        // Position poweredBy at bottom center
        StackPane.setAlignment(poweredBy, Pos.BOTTOM_CENTER);
        StackPane.setMargin(poweredBy, new Insets(0, 0, 10, 0));

        // Position itoo at bottom right
        StackPane.setAlignment(itoo, Pos.BOTTOM_RIGHT);
        StackPane.setMargin(itoo, new Insets(0, 10, 10, 0));

        Scene scene = new Scene(root, 400, 500);
        scene.getStylesheets().add(getClass().getResource("/DuckStyle.css").toExternalForm());

        stage.setResizable(false);
        stage.setMaximized(false);
        stage.setScene(scene);
        stage.show();
    }

    public void statsFrame(Stage stage, String username) {
        StackPane root = new StackPane();
        root.setStyle("-fx-background-color: #fff9d6;");

        Pane card = new Pane();
        card.setPrefSize(340, 420);

        VBox centerBox = new VBox(35);
        centerBox.setAlignment(Pos.CENTER);

        centerBox.layoutXProperty().bind(
                card.widthProperty().subtract(centerBox.widthProperty()).divide(2)
        );
        centerBox.layoutYProperty().bind(
                card.heightProperty().subtract(centerBox.heightProperty()).divide(2)
        );

        Label levelLabel = new Label("Level 1");
        levelLabel.getStyleClass().add("level-badge");

        VBox statsBox = new VBox(25);
        statsBox.setAlignment(Pos.CENTER);

        statsBox.getChildren().addAll(
                makeStat("Happiness", happiness),
                makeStat("Hunger", hunger),
                makeStat("Energy", energy),
                makeStat("Cleanliness", cleanliness)
        );

        centerBox.getChildren().addAll(levelLabel, statsBox);
        card.getChildren().add(centerBox);

        BorderPane overlay = new BorderPane();
        overlay.setPickOnBounds(false);

        Pane homePane = new Pane();
        Button homeBtn = new Button("🏠");
        homeBtn.getStyleClass().add("duck-button");
        homeBtn.setOnAction(e -> DuckHouse(stage, username));
        homeBtn.setLayoutX(10);
        homeBtn.setLayoutY(10);

        homePane.getChildren().add(homeBtn);
        overlay.setTop(homePane);

        root.getChildren().addAll(card, overlay);

        Scene scene = new Scene(root, 400, 500);
        scene.getStylesheets().add(getClass().getResource("DuckStyle.css").toExternalForm());

        stage.setScene(scene);
        stage.show();
    }

    private VBox makeStat(String name, double value){
        Label label = new Label(name);
        label.getStyleClass().add("stat-label");

        ProgressBar bar = new ProgressBar(value);
        bar.getStyleClass().add("stat-bar");
        bar.setPrefWidth(230);

        VBox v = new VBox(5, label, bar);
        v.setAlignment(Pos.CENTER_LEFT);
        return v;
    }

    public static void main(String[] args) {

        DB.initialize(); // ENSURE ALL TABLES ARE CREATED
        DB.addSampleData(); // ADDS TEST USERS
        DB.printAllUsers(); // CHECKS IF THE USER IS ADDED
        launch(args);

    }
}
