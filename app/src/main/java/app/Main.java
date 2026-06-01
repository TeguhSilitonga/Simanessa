package app;

import javafx.animation.PauseTransition;
import javafx.application.Application;
import javafx.application.Platform;
import javafx.scene.Scene;
import javafx.scene.layout.StackPane;
import javafx.stage.Stage;
import javafx.util.Duration;
import model.User;
import view.DashboardViewAdmin;
import view.DashboardViewGuru;
import view.LoginView;
import view.SplashScreen;

public class Main extends Application {
    private Stage primaryStage;

    @Override
    public void start(Stage primaryStage) {
        this.primaryStage = primaryStage;
        
        Scene mainScene = new Scene(new StackPane(), 1200, 800);
        this.primaryStage.setScene(mainScene);
        this.primaryStage.setMaximized(true);

        SplashScreen.show(primaryStage);

        PauseTransition delay = new PauseTransition(Duration.seconds(8));
        delay.setOnFinished(event -> showLogin());
        delay.play();
    }

    public void showLogin() {
        LoginView loginView = new LoginView(user -> handleLoginSuccess(user));
        
        primaryStage.getScene().setRoot(loginView.getView());
        primaryStage.setTitle("SIMANESSA - Login");
        primaryStage.setMaximized(true); 
    }

    private void handleLoginSuccess(User user) {
        if (user.getRole().equals("Admin")) {
            DashboardViewAdmin adminDb = new DashboardViewAdmin(this);
            primaryStage.getScene().setRoot(adminDb.getView());
            primaryStage.setTitle("SIMANESSA - Admin Dashboard");
        } else {
            DashboardViewGuru guruDb = new DashboardViewGuru(primaryStage, user, this);
            primaryStage.getScene().setRoot(guruDb.getView());
            primaryStage.setTitle("SIMANESSA - Guru Dashboard");
        }
        
        primaryStage.setMaximized(true); 
    }

    public void logoutWithSplash() {
        SplashScreen.show(primaryStage);
        primaryStage.setMaximized(true);

        PauseTransition delay = new PauseTransition(Duration.seconds(3));
        delay.setOnFinished(event -> showLogin());
        delay.play();
    }

    public void closeApplication() {
        Platform.exit();
        System.exit(0);
    }

    public static void main(String[] args) {
        launch(args);
    }
}