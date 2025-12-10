import javafx.animation.*;
import javafx.application.Application;
import javafx.geometry.Bounds;
import javafx.geometry.Insets;
import javafx.geometry.Point2D;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.*;
import javafx.scene.media.AudioClip;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;
import javafx.scene.text.FontWeight;
import javafx.stage.Stage;
import javafx.scene.media.Media;
import javafx.scene.media.MediaPlayer;
import javafx.scene.text.Font;
import javafx.util.Duration;

public class Main extends Application {

    private static MediaPlayer mediaPlayer;
    private double happiness = 0.60;
    private double hunger = 0.70;
    private double energy = 0.50;
    private double cleanliness = 0.70;
    private ImageView foodDisplay;
    private int foodIndex = 0;
    private final String[] foods = {"peas.png", "birdseed.png", "corn.png", "oats.png"};
    private static Image selectedCharacter = null;
    // Adjustable sizes per scene
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
        userBox.setLayoutX(120);
        userBox.setLayoutY(240);

        PasswordField passwordField = new PasswordField();
        passwordField.setPromptText("Enter password");
        passwordField.setMaxWidth(150);
        passwordField.setPrefWidth(150);
        passwordField.setStyle("-fx-font-size: 11px; -fx-pref-height: 22px;");
        passwordField.getStyleClass().add("login-field");

        VBox passBox = new VBox(4, passLabel, passwordField);
        passBox.setAlignment(Pos.CENTER);
        passBox.setLayoutX(120);
        passBox.setLayoutY(300);

        Label error = new Label();
        error.getStyleClass().add("error-label");
        error.setVisible(false);
        error.setLayoutX(80);
        error.setLayoutY(425);

        Button loginButton = new Button("🦆 Sign In");
        loginButton.getStyleClass().add("login-button");
        loginButton.setPrefWidth(110);
        loginButton.setPrefHeight(26);
        loginButton.setStyle("-fx-font-size: 11px;");
        loginButton.setLayoutX(140);
        loginButton.setLayoutY(380);
        loginButton.setOnAction(e -> {
            String user = usernameField.getText();
            String pass = passwordField.getText();
            if (user.equals("Zashkie") && pass.equals("1234")) {
                DuckHouse(stage, user);
            } else {
                error.setText("Invalid Username or Password");
                error.setVisible(true);
            }
        });

        Pane layoutPane = new Pane(userBox, passBox, loginButton, error);

        Button signUpBtn = new Button("Sign Up");
        signUpBtn.getStyleClass().add("login-button");
        signUpBtn.setFont(Font.font(9));
        signUpBtn.setPrefWidth(110);
        signUpBtn.setStyle("-fx-font-size: 11px;");
        signUpBtn.setPrefHeight(26);
        signUpBtn.setPadding(new Insets(1, 4, 1, 4));
        signUpBtn.setLayoutX(275);
        signUpBtn.setLayoutY(445);
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
        scene.getStylesheets().add(getClass().getResource("/DuckStyle.css").toExternalForm());

        stage.setTitle("QuackMate - Login");
        Image icon = new Image(getClass().getResource("/QuackMate.png").toExternalForm());
        stage.getIcons().add(icon);
        stage.setResizable(false);
        stage.setScene(scene);
        stage.show();

        if (mediaPlayer == null) {
            String musicFile = getClass().getResource("/bgmusic.mp3").toExternalForm();
            Media sound = new Media(musicFile);
            mediaPlayer = new MediaPlayer(sound);
            mediaPlayer.setCycleCount(MediaPlayer.INDEFINITE);
            mediaPlayer.play();
        }
    }

    private void aboutUs(Stage stage) {
        aboutUs(stage, ""); // pass empty username
    }

    public void signUpFrame(Stage stage) {

        StackPane root = new StackPane();

        // Background image
        Image bgImage = new Image(getClass().getResource("/MainLoginGrame.jpg").toExternalForm());
        ImageView bgView = new ImageView(bgImage);
        bgView.setPreserveRatio(false);
        bgView.setFitWidth(400); // match scene width
        bgView.setFitHeight(500); // match scene height

        // --- CENTER CONTAINER ---
        VBox centerBox = new VBox(15);
        centerBox.setAlignment(Pos.CENTER);

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

        BorderPane overlay = new BorderPane();
        overlay.setPickOnBounds(false);
        Pane backPane = new Pane();
        Button backBtn = new Button("⬅");
        backBtn.getStyleClass().add("login-button");
        backBtn.setPrefHeight(30);
        backBtn.setPrefWidth(70);
        backBtn.setStyle("-fx-font-size: 13px;");
        backBtn.setOnAction(e -> start(stage));
        backBtn.setLayoutX(10);
        backBtn.setLayoutY(10);

        backPane.getChildren().add(backBtn);
        overlay.setTop(backPane);

        // Create button
        Button signupButton = new Button("Create");
        signupButton.getStyleClass().add("login-button");
        signupButton.setPrefWidth(130);
        signupButton.setOnAction(e -> {
            String user = usernameField.getText();
            String pass = passwordField.getText();
            String confirm = confirmField.getText();

            if (user.isEmpty() || pass.isEmpty() || confirm.isEmpty()) {
                error.setText("Please fill out all fields");
                error.setVisible(true);
                return;
            }

            if (!pass.equals(confirm)) {
                error.setText("Passwords do not match");
                error.setVisible(true);
                return;
            }

            DuckHouse(stage, user);
        });

        centerBox.getChildren().addAll(title, userBox, passBox, confirmBox, error, signupButton);

        root.getChildren().addAll(bgView, centerBox, overlay);

        Scene scene = new Scene(root, 400, 500);
        scene.getStylesheets().add(getClass().getResource("/DuckStyle.css").toExternalForm());
        stage.setScene(scene);
        stage.setResizable(false);
        stage.show();
    }

    private void DuckHouse(Stage stage, String username) {
        BorderPane layout = sceneTemplate(stage, "house.png", username);
        StackPane root = (StackPane) stage.getScene().getRoot();

        // CHARACTER SIZE SETTINGS
        double DEFAULT_SIZE = 80;     // original duck
        double SELECTED_SIZE = 130;   // selected duck

        // Use selected character if chosen
        Image characterToUse = (selectedCharacter != null)
                ? selectedCharacter
                : new Image(getClass().getResource("/dockie.png").toExternalForm());

        ImageView charac = new ImageView(characterToUse);

        // APPLY DIFFERENT SIZE
        charac.setFitWidth((selectedCharacter != null) ? SELECTED_SIZE : DEFAULT_SIZE);

        charac.setPreserveRatio(true);
        StackPane.setAlignment(charac, Pos.BOTTOM_CENTER);
        StackPane.setMargin(charac, new Insets(0, 0, 210, 0));

        Image sign = new Image(getClass().getResource("/sign.png").toExternalForm());
        ImageView signView = new ImageView(sign);
        signView.setFitWidth(180);
        signView.setPreserveRatio(true);
        StackPane.setAlignment(signView, Pos.TOP_CENTER);
        StackPane.setMargin(signView, new Insets(70, 0, 0, 0));

        Label userLabel = new Label(username + "'s House");
        userLabel.setFont(Font.font("Arial", FontWeight.BOLD, 15));
        userLabel.setTextFill(Color.WHITE);
        userLabel.setStyle("-fx-background-color: transparent; -fx-border-color: transparent;");
        userLabel.setPrefWidth(180);
        userLabel.setPrefHeight(40);
        StackPane.setAlignment(userLabel, Pos.TOP_CENTER);
        StackPane.setMargin(userLabel, new Insets(130, 0, 0, 57));

        if (!root.getChildren().contains(charac)) root.getChildren().add(charac);
        if (!root.getChildren().contains(signView)) root.getChildren().add(signView);
        if (!root.getChildren().contains(userLabel)) root.getChildren().add(userLabel);
    }
    private void kitchen(Stage stage, String username) {
        BorderPane layout = sceneTemplate(stage, "kitchen.png", username);
        StackPane root = (StackPane) stage.getScene().getRoot();

        double DEFAULT_SIZE = 150;
        double SELECTED_SIZE = 255;

        Image characterToUse = (selectedCharacter != null)
                ? selectedCharacter
                : new Image(getClass().getResource("/dockieKitchen.png").toExternalForm());

        ImageView duck = new ImageView(characterToUse);

        duck.setFitWidth((selectedCharacter != null) ? SELECTED_SIZE : DEFAULT_SIZE);
        duck.setPreserveRatio(true);
        StackPane.setAlignment(duck, Pos.BOTTOM_CENTER);
        StackPane.setMargin(duck, new Insets(0, -200, 135, 0));
        root.getChildren().add(duck);
        duck.setMouseTransparent(true);

        Pane foodPane = new Pane();
        foodPane.setPrefSize(200, 300);
        layout.setCenter(foodPane);

        Button btnLeft = new Button(); btnLeft.getStyleClass().add("arrow-button-left");
        btnLeft.setPrefSize(30, 30); btnLeft.setLayoutX(40); btnLeft.setLayoutY(180);

        Button btnRight = new Button(); btnRight.getStyleClass().add("arrow-button-right");
        btnRight.setPrefSize(30, 30); btnRight.setLayoutX(175); btnRight.setLayoutY(180);

        foodPane.getChildren().addAll(btnLeft, btnRight);

        createFood(foodPane, duck);

        btnLeft.setOnAction(e -> {
            foodIndex = (foodIndex - 1 + foods.length) % foods.length;
            updateFood(foodPane, duck);
        });

        btnRight.setOnAction(e -> {
            foodIndex = (foodIndex + 1) % foods.length;
            updateFood(foodPane, duck);
        });

        stage.setScene(new Scene(layout, 400, 500));
        stage.show();
    }

    // EATING ANIMATION
    private void showMouthOpen(ImageView duck) {
        double originalWidth = duck.getFitWidth();
        double originalTranslateX = duck.getTranslateX();
        double originalTranslateY = duck.getTranslateY();

        duck.setImage(new Image(getClass().getResource("/eating.png").toExternalForm()));
        duck.setFitWidth(305);                     // eating duck size
        duck.setTranslateX(originalTranslateX + 1); // move right 20px
        duck.setTranslateY(originalTranslateY + 60); // move down 50px

        PauseTransition pt = new PauseTransition(Duration.millis(200)); // short duration
        pt.setOnFinished(e -> {
            duck.setImage(new Image(getClass().getResource("/dockieKitchen.png").toExternalForm()));
            duck.setFitWidth(originalWidth);
            duck.setTranslateX(originalTranslateX);
            duck.setTranslateY(originalTranslateY);
        });
        pt.play();
    }

    private void createFood(Pane foodPane, ImageView duck) {
        foodDisplay = new ImageView(new Image(getClass().getResourceAsStream("/" + foods[foodIndex])));
        foodDisplay.setFitWidth(100);
        foodDisplay.setPreserveRatio(true);
        foodDisplay.setLayoutX(70);
        foodDisplay.setLayoutY(90);

        makeDraggable(foodDisplay, duck, foodPane);
        foodPane.getChildren().add(foodDisplay);
    }

    private void updateFood(Pane foodPane, ImageView duck) {
        foodPane.getChildren().remove(foodDisplay);
        createFood(foodPane, duck);
    }

    private boolean isFoodNearDuck(ImageView food, ImageView duck, double threshold) {
        double duckCenterX = duck.getBoundsInParent().getMinX() + duck.getBoundsInParent().getWidth() / 2;
        double duckCenterY = duck.getBoundsInParent().getMinY() + duck.getBoundsInParent().getHeight() / 2;

        double foodCenterX = food.getBoundsInParent().getMinX() + food.getBoundsInParent().getWidth() / 2;
        double foodCenterY = food.getBoundsInParent().getMinY() + food.getBoundsInParent().getHeight() / 2;

        double distance = Math.hypot(duckCenterX - foodCenterX, duckCenterY - foodCenterY);
        return distance <= threshold;
    }

    private void makeDraggable(ImageView food, ImageView duck, Pane pane) {
        final Delta dragDelta = new Delta();

        food.setOnMousePressed(e -> {
            dragDelta.x = e.getSceneX() - food.getLayoutX();
            dragDelta.y = e.getSceneY() - food.getLayoutY();
            food.toFront();
        });

        food.setOnMouseDragged(e -> {
            food.setLayoutX(e.getSceneX() - dragDelta.x);
            food.setLayoutY(e.getSceneY() - dragDelta.y);
            food.toFront();

            // ⭐ Check if food is near duck while dragging
            if (isFoodNearDuck(food, duck, 80)) {
                showMouthOpen(duck);
            }
        });

        food.setOnDragDetected(e -> {
            food.startFullDrag();
            duck.setMouseTransparent(false);
            food.toFront();
            e.consume();
        });

        // Feeding event (when released on duck)
        duck.setOnMouseDragReleased(e -> {
            Object src = e.getGestureSource();
            if (src instanceof ImageView draggedFood) {
                draggedFood.toFront();
                showMouthOpen(duck);

                foodIndex = (foodIndex + 1) % foods.length;
                updateFood(pane, duck);
            }
            duck.setMouseTransparent(true);
        });

        food.setOnMouseReleased(e -> duck.setMouseTransparent(true));
    }


    private static class Delta { double x, y; }
    // Add these as class-level variables
    private ImageView brushBubble;
    private FadeTransition brushBubbleFade;

    private void bathRoom(Stage stage, String username) {
        BorderPane layout = sceneTemplate(stage, "cr.png", username);
        StackPane root = (StackPane) stage.getScene().getRoot();

        // CHARACTER SIZE SETTINGS
        double DEFAULT_SIZE = 150;
        double SELECTED_SIZE = 250;

        Image bucket = new Image(getClass().getResource("/water (1).png").toExternalForm());
        Image brush = new Image(getClass().getResource("/scrub.png").toExternalForm());

        Image characterToUse = (selectedCharacter != null)
                ? selectedCharacter
                : new Image(getClass().getResource("/dockieBath.png").toExternalForm());

        ImageView dockieView = new ImageView(characterToUse);
        dockieView.setFitWidth((selectedCharacter != null) ? SELECTED_SIZE : DEFAULT_SIZE);
        dockieView.setPreserveRatio(true);
        StackPane.setAlignment(dockieView, Pos.CENTER);
        dockieView.setTranslateY(20);

        ImageView brushView = new ImageView(brush);
        ImageView bucketView = new ImageView(bucket);
        brushView.setFitHeight(60); brushView.setFitWidth(60);
        bucketView.setFitHeight(60); bucketView.setFitWidth(60);

        Button bucketBtn = new Button();
        bucketBtn.setGraphic(bucketView);
        bucketBtn.setStyle("-fx-background-color: transparent;");

        Button brushBtn = new Button();
        brushBtn.setGraphic(brushView);
        brushBtn.setStyle("-fx-background-color: transparent;");

        StackPane.setAlignment(brushBtn, Pos.CENTER_RIGHT);
        brushBtn.setTranslateX(-20); brushBtn.setTranslateY(40);
        StackPane.setAlignment(bucketBtn, Pos.CENTER_LEFT);
        bucketBtn.setTranslateX(10); bucketBtn.setTranslateY(40);

        root.getChildren().addAll(dockieView, bucketBtn, brushBtn);

        double[] bucketOrig = { bucketBtn.getTranslateX(), bucketBtn.getTranslateY() };
        double[] brushOrig = { brushBtn.getTranslateX(), brushBtn.getTranslateY() };

        // Make draggable
        makeDraggable(brushBtn, brushOrig, dockieView, root, true);   // brush
        makeDraggable(bucketBtn, bucketOrig, dockieView, root, false); // bucket
    }
    private void makeDraggable(Button btn, double[] origPos, ImageView duck, StackPane parentForBubbles, boolean isBrush) {
        final double[] mouseOffset = new double[2];

        // --- Sound Effect for bucket ---
        AudioClip splashSound = new AudioClip(getClass().getResource("/WaterSplashSFX.mp3").toExternalForm());

        // --- Sound Effect for brush ---
        AudioClip brushSound = new AudioClip(getClass().getResource("").toExternalForm());

        // Splash for bucket
        Image splashImage = new Image(getClass().getResource("/splash.png").toExternalForm());
        ImageView splash = new ImageView(splashImage);
        splash.setFitWidth(200);
        splash.setPreserveRatio(true);
        splash.setVisible(false);

        // Brush bubble (only if brush)
        if (isBrush) {
            brushBubble = new ImageView(new Image(getClass().getResource("/bubbles.png").toExternalForm()));
            brushBubble.setFitWidth(280);
            brushBubble.setPreserveRatio(true);
            brushBubble.setVisible(false);
            parentForBubbles.getChildren().add(brushBubble);
            brushBubble.toFront();

            brushBubbleFade = new FadeTransition(Duration.millis(4000), brushBubble);
            brushBubbleFade.setFromValue(0);
            brushBubbleFade.setToValue(1);
        }

        StackPane parent = (StackPane) duck.getParent();
        parent.getChildren().add(splash);
        splash.toFront();

        btn.setOnMousePressed(e -> {
            mouseOffset[0] = e.getSceneX() - btn.getTranslateX();
            mouseOffset[1] = e.getSceneY() - btn.getTranslateY();
            btn.toFront();
        });

        btn.setOnMouseDragged(e -> {
            btn.setTranslateX(e.getSceneX() - mouseOffset[0]);
            btn.setTranslateY(e.getSceneY() - mouseOffset[1]);

            if (isBrush && brushBubble != null && btn.getBoundsInParent().intersects(duck.getBoundsInParent())) {

                // Bubble follows brush
                double brushX = btn.getTranslateX() + btn.getWidth() / 2;
                double brushY = btn.getTranslateY() + btn.getHeight() / 2;
                brushBubble.setLayoutX(brushX - brushBubble.getFitWidth() / 2);
                brushBubble.setLayoutY(brushY - brushBubble.getFitHeight() / 2);

                if (!brushBubble.isVisible()) {
                    brushBubble.setVisible(true);
                    brushBubbleFade.playFromStart();
                }

                // --- PLAY BRUSH SOUND ---
                brushSound.play();
            }

            btn.toFront();
        });

        btn.setOnMouseReleased(e -> {
            Bounds btnBounds = btn.localToScene(btn.getBoundsInLocal());
            Bounds duckBounds = duck.localToScene(duck.getBoundsInLocal());

            if (btnBounds.intersects(duckBounds)) {
                if (!isBrush) { // bucket splash

                    // --- Play the splash sound ---
                    splashSound.play();

                    double duckCenterX = (duckBounds.getMinX() + duckBounds.getMaxX()) / 2;
                    double duckCenterY = (duckBounds.getMinY() + duckBounds.getMaxY()) / 2;

                    Point2D splashPos = parent.sceneToLocal(duckCenterX, duckCenterY);
                    splash.setLayoutX(splashPos.getX() - splash.getFitWidth() / 2);
                    splash.setLayoutY(splashPos.getY() - splash.getFitHeight() / 2);
                    splash.setVisible(true);

                    PauseTransition pt = new PauseTransition(Duration.millis(500));
                    pt.setOnFinished(ev -> splash.setVisible(false));
                    pt.play();

                    // Hide brush bubble when bucket is used
                    if (brushBubble != null) {
                        brushBubble.setVisible(false);
                        brushBubbleFade.stop();
                    }
                }
            }

            btn.setTranslateX(origPos[0]);
            btn.setTranslateY(origPos[1]);
            duck.setOpacity(1.0);
        });
    }

    private void bedRoom(Stage stage, String username) {
        BorderPane layout = sceneTemplate(stage, "room.png", username);
        StackPane root = (StackPane) stage.getScene().getRoot();

        // CHARACTER SIZE SETTINGS
        double DEFAULT_SIZE = 150;
        double SELECTED_SIZE = 250;

        Image awakeDuck = (selectedCharacter != null)
                ? selectedCharacter
                : new Image(getClass().getResource("/dockieBed.png").toExternalForm());

        Image sleepingDuck = new Image(getClass().getResource("/DuckSleeping.png").toExternalForm());
        Image lampOff = new Image(getClass().getResource("/lambing.png").toExternalForm());
        Image lampOn = new Image(getClass().getResource("/lamning.png").toExternalForm());
        Image bgOff = new Image(getClass().getResource("/nightver.png").toExternalForm());
        Image bgOn = new Image(getClass().getResource("/room.png").toExternalForm());

        ImageView charac = new ImageView(awakeDuck);

        // APPLY DIFFERENT SIZE
        charac.setFitWidth((selectedCharacter != null) ? SELECTED_SIZE : DEFAULT_SIZE);

        charac.setPreserveRatio(true);
        StackPane.setAlignment(charac, Pos.BOTTOM_CENTER);
        StackPane.setMargin(charac, new Insets(0, 0, 180, 0));
        if (!root.getChildren().contains(charac)) root.getChildren().add(charac);

        ImageView bgView = (ImageView) root.getChildren().get(0);
        ImageView lampView = new ImageView(lampOn);
        lampView.setFitWidth(80); lampView.setPreserveRatio(true);

        ToggleButton lampButton = new ToggleButton();
        lampButton.setGraphic(lampView);
        lampButton.getStyleClass().add("lamp-button");
        lampButton.setSelected(true);

        lampButton.setOnAction(e -> {
            if (lampButton.isSelected()) {
                lampView.setImage(lampOn);
                bgView.setImage(bgOn);
                charac.setImage(awakeDuck);

                // RESET TO CORRECT SIZE
                charac.setFitWidth((selectedCharacter != null) ? SELECTED_SIZE : DEFAULT_SIZE);

                StackPane.setMargin(charac, new Insets(0, 0, 180, 0));
            } else {
                lampView.setImage(lampOff);
                bgView.setImage(bgOff);
                charac.setImage(sleepingDuck);

                // SLEEPING SIZE (kept your original!)
                charac.setFitWidth(210);
                StackPane.setMargin(charac, new Insets(0, 0, 160, 0));
            }
        });

        StackPane.setAlignment(lampButton, Pos.TOP_RIGHT);
        StackPane.setMargin(lampButton, new Insets(60, 30, 0, 0));
        if (!root.getChildren().contains(lampButton)) root.getChildren().add(lampButton);
    }

    private int outfitIndex = 0;

    private void closet(Stage stage, String username) {
        BorderPane layout = sceneTemplate(stage, "closet.png", username);
        StackPane root = (StackPane) stage.getScene().getRoot();

        // --- Character Images (3 available characters) ---
        Image[] characters = new Image[] {
                new Image(getClass().getResource("DuckCatHat.png").toExternalForm()),
                new Image(getClass().getResource("DuckFrogHat.png").toExternalForm()),
                new Image(getClass().getResource("DuckStitchHat.png").toExternalForm())
        };

        // Main preview ImageView
        ImageView charac = new ImageView(characters[0]);
        charac.setFitWidth(250);
        charac.setPreserveRatio(true);
        StackPane.setAlignment(charac, Pos.BOTTOM_CENTER);
        StackPane.setMargin(charac, new Insets(0, 0, 180, 18));
        root.getChildren().add(charac);

        // --- Arrow Buttons ---
        Button leftArrow = new Button();
        leftArrow.getStyleClass().add("arrow-button-left");

        Button rightArrow = new Button();
        rightArrow.getStyleClass().add("arrow-button-right");

        StackPane.setAlignment(leftArrow, Pos.CENTER_LEFT);
        StackPane.setAlignment(rightArrow, Pos.CENTER_RIGHT);
        StackPane.setMargin(leftArrow, new Insets(0, 0, 0, 10));
        StackPane.setMargin(rightArrow, new Insets(0, 10, 0, 0));
        root.getChildren().addAll(leftArrow, rightArrow);

        // Switch preview character
        leftArrow.setOnAction(e -> {
            outfitIndex = (outfitIndex - 1 + characters.length) % characters.length;
            charac.setImage(characters[outfitIndex]);
        });

        rightArrow.setOnAction(e -> {
            outfitIndex = (outfitIndex + 1) % characters.length;
            charac.setImage(characters[outfitIndex]);
        });

// --- SELECT AND REMOVE BUTTONS IN HBOX ---
        Button selectBtn = new Button("Use");
        selectBtn.getStyleClass().add("buy-button");
        selectBtn.setPrefSize(90, 20);

        Button removeBtn = new Button("Remove");
        removeBtn.getStyleClass().add("buy-button");
        removeBtn.setPrefSize(90, 20);

// Put buttons in HBox
        HBox buttonBox = new HBox(20); // spacing
        buttonBox.getChildren().addAll(selectBtn, removeBtn);
        buttonBox.setAlignment(Pos.CENTER);

// Overlay on top of root
        StackPane.setAlignment(buttonBox, Pos.BOTTOM_CENTER);
        StackPane.setMargin(buttonBox, new Insets(300, 0, 80, 0)); // distance from bottom
        root.getChildren().add(buttonBox);
        buttonBox.toFront(); // make sure it's clickable

// Button actions
        selectBtn.setOnAction(e -> selectedCharacter = characters[outfitIndex]);
        removeBtn.setOnAction(e -> selectedCharacter = null);

    }

    private BorderPane sceneTemplate(Stage stage, String bgFile, String username) {
        Image backGround = new Image(getClass().getResource("/" + bgFile).toExternalForm());
        ImageView bg = new ImageView(backGround);
        bg.setPreserveRatio(false);

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

        StackPane root = new StackPane(bg, layout);

        Scene scene = new Scene(root, 400, 500);
        scene.getStylesheets().add(getClass().getResource("/DuckStyle.css").toExternalForm());

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
        picBtn.setOnAction(e -> cardFlip(stage, username));
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
        picBtn2.setOnAction(e -> flappyDuck(stage, username));
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
    public void flappyDuck (Stage stage, String username){
        StackPane root = new StackPane();
        root.setStyle("-fx-background-color: #fff9d6;");

        Scene scene = new Scene(root, 400, 500);
        scene.getStylesheets().add(getClass().getResource("/DuckStyle.css").toExternalForm());

        Button backBtn = new Button("❮");
        backBtn.getStyleClass().add("back-button");
        backBtn.setOnAction(e -> GamesFrame(stage, username));

        StackPane.setAlignment(backBtn, Pos.TOP_LEFT);
        StackPane.setMargin(backBtn, new Insets(10, 0, 0, 10));

        root.getChildren().add(backBtn);

        stage.setScene(scene);
        stage.show();
    }

    public void cardFlip (Stage stage, String username){
        StackPane root = new StackPane();
        root.setStyle("-fx-background-color: #fff9d6;");

        Scene scene = new Scene(root, 400, 500);
        scene.getStylesheets().add(getClass().getResource("/DuckStyle.css").toExternalForm());

        Button backBtn = new Button("❮");
        backBtn.getStyleClass().add("back-button");
        backBtn.setOnAction(e -> GamesFrame(stage, username));

        StackPane.setAlignment(backBtn, Pos.TOP_LEFT);
        StackPane.setMargin(backBtn, new Insets(10, 0, 0, 10));

        root.getChildren().add(backBtn);

        stage.setScene(scene);
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
        Image poweredImg = new Image(getClass().getResource("/powered.png").toExternalForm());
        ImageView poweredBy = new ImageView(poweredImg);
        poweredBy.setFitWidth(200);
        poweredBy.setPreserveRatio(true);

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

        // Mute Button
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

        Image i = new Image(getClass().getResource("/i.png").toExternalForm());
        ImageView iView = new ImageView(i);
        iView.setFitWidth(40);
        iView.setPreserveRatio(true);

        Button infoBtn = new Button();
        infoBtn.setGraphic(iView);
        infoBtn.setStyle("-fx-background-color: transparent; -fx-padding: 0;");
        infoBtn.setShape(new Circle(25)); // shape
        infoBtn.setLayoutX(355); // adjust pa left or right
        infoBtn.setLayoutY(460); // adjust pa up or down
        infoBtn.setOnAction(e -> aboutUs(stage, username));

        Pane buttonPane = new Pane(btn1, btn2, duckView, muteBtn, infoBtn);

        root.getChildren().addAll(buttonPane, poweredBy);

        StackPane.setAlignment(poweredBy, Pos.BOTTOM_CENTER);
        StackPane.setMargin(poweredBy, new Insets(0, 0, 10, 0));

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
        scene.getStylesheets().add(getClass().getResource("/DuckStyle.css").toExternalForm());

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


    private void aboutUs(Stage stage, String username) {
        StackPane root = new StackPane();

        Image bg = new Image(getClass().getResource("/MainLoginGrame.jpg").toExternalForm());
        ImageView backGround = new ImageView(bg);
        backGround.setPreserveRatio(false);
        root.getChildren().add(backGround);

        Image sign = new Image(getClass().getResource("/QuackNet.png").toExternalForm());
        ImageView signView = new ImageView(sign);
        signView.setFitWidth(250);
        signView.setPreserveRatio(true);
        StackPane.setAlignment(signView, Pos.TOP_CENTER);
        StackPane.setMargin(signView, new Insets(-60, 0, 0, 0));
        root.getChildren().add(signView);

        Button backBtn = new Button("❮");
        backBtn.getStyleClass().add("back-button2");
        backBtn.setOnAction(e -> SettingsFrame(stage, username));
        StackPane.setAlignment(backBtn, Pos.TOP_LEFT);
        StackPane.setMargin(backBtn, new Insets(10, 0, 0, 10));
        root.getChildren().add(backBtn);

        int buttonSize = 80;

        java.util.function.BiFunction<String, String, VBox> createProfile = (img, name) -> {
            Button picBtn = createCircularImageButton(img, buttonSize);

            Label label = new Label(name);
            label.getStyleClass().add("form-label");
            label.setFont(Font.font(10));
            label.setAlignment(Pos.CENTER);
            label.setMaxWidth(buttonSize);

            VBox box = new VBox(5, picBtn, label);
            box.setAlignment(Pos.CENTER);

            return box;
        };

        VBox p1 = createProfile.apply("/joshua.png", "Joshua Largado");
        VBox p2 = createProfile.apply("/katleen.png", "Katleen Kriones");
        VBox p3 = createProfile.apply("/zash.png", "Zashkie Bontia");

        HBox hBoxTop = new HBox(20, p1, p2, p3);
        hBoxTop.setLayoutX(35);
        hBoxTop.setLayoutY(110);

        VBox p4 = createProfile.apply("/grace.png", "Grace Galua");
        VBox p5 = createProfile.apply("/hazel.png", "Hazel Brigoli");
        VBox p6 = createProfile.apply("/flor.png", "Flor Gamali");

        HBox hBoxBottom = new HBox(20, p4, p5, p6);
        hBoxBottom.setLayoutX(35);
        hBoxBottom.setLayoutY(270);

        Pane floatingPane = new Pane(hBoxTop, hBoxBottom);
        floatingPane.setPickOnBounds(false);
        root.getChildren().add(floatingPane);

        Scene scene = new Scene(root, 400, 500);
        scene.getStylesheets().add(getClass().getResource("/DuckStyle.css").toExternalForm());

        backGround.fitWidthProperty().bind(scene.widthProperty());
        backGround.fitHeightProperty().bind(scene.heightProperty());

        stage.setScene(scene);
        stage.setResizable(false);
        stage.show();
    }

    private Button createCircularImageButton(String imagePath, int size) {
        Image img = new Image(getClass().getResource(imagePath).toExternalForm());
        ImageView imgView = new ImageView(img);
        imgView.setFitWidth(size);
        imgView.setFitHeight(size);
        imgView.setPreserveRatio(true);

        Circle clip = new Circle(size / 2, size / 2, size / 2);
        imgView.setClip(clip);

        Button btn = new Button();
        btn.setStyle("-fx-background-color: transparent;");
        btn.setGraphic(imgView);
        btn.setPrefSize(size, size);
        return btn;
    }

    public static void main(String[] args) {
        launch(args);
    }
}
