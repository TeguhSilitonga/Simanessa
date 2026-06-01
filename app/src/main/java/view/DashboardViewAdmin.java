package view;

import app.Main;
import javafx.animation.TranslateTransition;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Parent;
import javafx.scene.control.*;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.*;
import javafx.util.Duration;

public class DashboardViewAdmin {

    private StackPane root = new StackPane();
    private BorderPane basePane = new BorderPane();
    private Main app;
    
   
    private Parent manajemenGuruCache;

    public DashboardViewAdmin(Main app) {
        this.app = app;
        root.getChildren().add(basePane);
        basePane.setStyle("-fx-background-color: #0f172a;");
        
        // Inisialisasi layar Manajemen Guru SATU KALI saja
        manajemenGuruCache = new ManajemenGuruView().getView();

        VBox homeContent = new VBox(20);
        homeContent.setAlignment(Pos.CENTER);
        Label title = new Label("PANEL UTAMA ADMINISTRATOR");
        title.setStyle("-fx-text-fill: white; -fx-font-size: 36px; -fx-font-weight: bold;");
        Label subtitle = new Label("Silakan geser mouse ke ujung kiri untuk mengakses menu navigasi.");
        subtitle.setStyle("-fx-text-fill: #94a3b8; -fx-font-size: 16px;");
        homeContent.getChildren().addAll(title, subtitle);
        
        basePane.setCenter(homeContent);

        VBox sidebar = new VBox(15);
        sidebar.setPadding(new Insets(30, 20, 30, 20));
        sidebar.setPrefWidth(260);
        sidebar.setMaxWidth(260);
        sidebar.setStyle("-fx-background-color: #1e293b; -fx-border-color: #334155; -fx-border-width: 0 1 0 0; -fx-effect: dropshadow(three-pass-box, rgba(0,0,0,0.6), 20, 0, 5, 0);");
        StackPane.setAlignment(sidebar, Pos.CENTER_LEFT);
        sidebar.setTranslateX(-260);

        Label brandTitle = new Label("ADMIN SISTEM");
        brandTitle.setStyle("-fx-text-fill: white; -fx-font-size: 24px; -fx-font-weight: 900; -fx-letter-spacing: 2px;");

        Region div1 = new Region(); div1.setPrefHeight(1); div1.setStyle("-fx-background-color: #334155;");

        Button dashboardBtn = createSidebarBtn("🏠 Dashboard");
        Button manageGuruBtn = createSidebarBtn("👥 Data Akun Guru");
        
        Button logoutBtn = new Button("🚪 Logout Admin");
        logoutBtn.setMaxWidth(Double.MAX_VALUE);
        logoutBtn.setStyle("-fx-background-color: #7F1D1D; -fx-text-fill: white; -fx-font-weight: bold; -fx-padding: 12; -fx-background-radius: 8; -fx-cursor: hand;");
        
        logoutBtn.setOnAction(e -> {
            if (AlertHelper.showConfirm("Logout", "Keluar dari sesi administrator?")) app.logoutWithSplash();
        });

        Region spacer = new Region(); VBox.setVgrow(spacer, Priority.ALWAYS);
        sidebar.getChildren().addAll(brandTitle, div1, dashboardBtn, manageGuruBtn, spacer, logoutBtn);
        root.getChildren().add(sidebar);

        TranslateTransition slideIn = new TranslateTransition(Duration.millis(250), sidebar); slideIn.setToX(0);
        TranslateTransition slideOut = new TranslateTransition(Duration.millis(250), sidebar); slideOut.setToX(-260);
        final boolean[] isOpen = {false};

        root.addEventFilter(MouseEvent.MOUSE_MOVED, e -> {
            double x = e.getSceneX();
            if (x <= 25 && !isOpen[0]) { slideOut.stop(); slideIn.playFromStart(); isOpen[0] = true; }
            else if (x > 260 && isOpen[0]) { slideIn.stop(); slideOut.playFromStart(); isOpen[0] = false; }
        });

        dashboardBtn.setOnAction(e -> {
            basePane.setCenter(homeContent);
            slideOut.playFromStart(); isOpen[0] = false;
        });

        manageGuruBtn.setOnAction(e -> {
            // Cukup panggil cache yang sudah ada, jangan buat 'new ManajemenGuruView()' lagi
            basePane.setCenter(manajemenGuruCache);
            slideOut.playFromStart(); isOpen[0] = false;
        });
    }

    private Button createSidebarBtn(String text) {
        Button btn = new Button(text);
        btn.setMaxWidth(Double.MAX_VALUE); btn.setAlignment(Pos.CENTER_LEFT); btn.setPadding(new Insets(12, 15, 12, 15));
        btn.setStyle("-fx-background-color: transparent; -fx-text-fill: #CBD5E1; -fx-font-size: 15px; -fx-font-weight: bold; -fx-background-radius: 8; -fx-cursor: hand;");
        btn.setOnMouseEntered(e -> btn.setStyle("-fx-background-color: #2563EB; -fx-text-fill: white; -fx-font-size: 15px; -fx-font-weight: bold; -fx-background-radius: 8; -fx-cursor: hand;"));
        btn.setOnMouseExited(e -> btn.setStyle("-fx-background-color: transparent; -fx-text-fill: #CBD5E1; -fx-font-size: 15px; -fx-font-weight: bold; -fx-background-radius: 8; -fx-cursor: hand;"));
        return btn;
    }

    public Parent getView() { return root; }
}