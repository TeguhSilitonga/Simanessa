package view;

import controller.GuruManager;
import javafx.animation.PauseTransition;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Parent;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.KeyCode;
import javafx.scene.layout.*;
import javafx.util.Duration;
import model.Admin; 
import model.Guru;
import model.User;
import java.util.function.Consumer;

public class LoginView {

    private StackPane root = new StackPane();
    private int attempt = 0;
    private GuruManager guruManager = new GuruManager();

    public LoginView(Consumer<User> onLoginSuccess) {
        root.setStyle("-fx-background-color: #0f172a;");

        VBox card = new VBox(20);
        card.setMaxSize(400, 500);
        card.setPadding(new Insets(40));
        card.setAlignment(Pos.CENTER);
        card.setStyle("-fx-background-color: #ffffff; -fx-background-radius: 15; -fx-effect: dropshadow(three-pass-box, rgba(0,0,0,0.5), 25, 0, 0, 10);");

        try {
            Image img = new Image(getClass().getResource("/images/Logo.jpg").toExternalForm());
            ImageView logoView = new ImageView(img);
            logoView.setFitWidth(200);
            logoView.setPreserveRatio(true);
            card.getChildren().add(logoView);
        } catch (Exception e) {}

        Label subtitle = new Label("Portal Login SIMANESSA");
        subtitle.setStyle("-fx-font-size: 16px; -fx-text-fill: #475569; -fx-font-weight: bold;");

        String fieldStyle = "-fx-background-color: #f1f5f9; -fx-text-fill: #1e293b; -fx-prompt-text-fill: #94a3b8; -fx-background-radius: 8; -fx-border-color: #cbd5e1; -fx-border-radius: 8; -fx-padding: 12;";

        TextField userField = new TextField();
        userField.setPromptText("Username");
        userField.setStyle(fieldStyle);

        PasswordField passField = new PasswordField();
        passField.setPromptText("Password");
        passField.setStyle(fieldStyle);

        Label errorLabel = new Label();
        errorLabel.setStyle("-fx-text-fill: #ef4444; -fx-font-size: 13px; -fx-font-weight: bold; -fx-alignment: center; -fx-text-alignment: center;");
        errorLabel.setMaxWidth(Double.MAX_VALUE);
        errorLabel.setAlignment(Pos.CENTER);
        errorLabel.setWrapText(true);
        errorLabel.setMinHeight(Region.USE_PREF_SIZE); 
        errorLabel.managedProperty().bind(errorLabel.visibleProperty()); 
        errorLabel.setVisible(false);

        VBox passBox = new VBox(8, passField, errorLabel);

        Button loginBtn = new Button("Sign In");
        loginBtn.setMaxWidth(Double.MAX_VALUE);
        loginBtn.setStyle("-fx-background-color: #1E6FD9; -fx-text-fill: white; -fx-font-weight: bold; -fx-background-radius: 8; -fx-padding: 12; -fx-cursor: hand;");

        // Fitur eksekusi login melalui tombol Enter di keyboard
        userField.setOnKeyPressed(e -> {
            if (e.getCode() == KeyCode.ENTER) passField.requestFocus();
        });
        passField.setOnKeyPressed(e -> {
            if (e.getCode() == KeyCode.ENTER) loginBtn.fire();
        });

        loginBtn.setOnAction(e -> {
            errorLabel.setVisible(false); 
            
            String user = userField.getText().trim();
            String pass = passField.getText().trim();
            
            if (user.isEmpty() || pass.isEmpty()) {
                errorLabel.setText("Username dan Password tidak boleh kosong!");
                errorLabel.setVisible(true);
                return;
            }

            // Memanggil model Admin secara eksplisit
            if (user.equals("admin") && pass.equals("admin123")) {
                attempt = 0;
                onLoginSuccess.accept(new Admin("admin", "admin123"));
                return;
            }

            Guru guru = guruManager.authenticate(user, pass);
            if (guru != null) {
                attempt = 0;
                onLoginSuccess.accept(guru);
            } else {
                attempt++;
                errorLabel.setText("Username atau Password salah!");
                errorLabel.setVisible(true);
            }

            // Sistem pembekuan tombol jika gagal login 3 kali beruntun
            if (attempt >= 3) {
                loginBtn.setDisable(true);
                errorLabel.setText("Terlalu banyak percobaan login.\nDibekukan 15 detik.");
                errorLabel.setVisible(true);
                
                PauseTransition p = new PauseTransition(Duration.seconds(15));
                p.setOnFinished(ev -> { 
                    loginBtn.setDisable(false); 
                    attempt = 0; 
                    errorLabel.setVisible(false);
                });
                p.play();
            }
        });

        card.getChildren().addAll(subtitle, userField, passBox, loginBtn);
        root.getChildren().add(card);
    }

    public Parent getView() { return root; }
}