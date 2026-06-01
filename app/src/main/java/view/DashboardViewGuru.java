package view;

import app.Main;
import controller.StudentManager;
import javafx.animation.TranslateTransition;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Parent;
import javafx.scene.control.*;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.*;
import javafx.stage.Stage;
import javafx.util.Duration;
import model.Student;
import model.Guru;
import model.User;

public class DashboardViewGuru {

    private StackPane root = new StackPane();
    private BorderPane basePane = new BorderPane();
    
    // Kosongkan inisialisasi awal di sini
    private StudentManager manager; 

    private Label totalLabel = new Label("0");
    private Label lulusLabel = new Label("0");
    private Label gagalLabel = new Label("0");
    private Label rataLabel = new Label("0");
    private Parent dataSiswaCache;

    public DashboardViewGuru(Stage primaryStage, User user, Main app) {
        Guru guruAktif = (Guru) user;
        
        // HUBUNGKAN MANAGER KE FILE TXT KHUSUS GURU INI
        this.manager = new StudentManager(guruAktif.getUsername());
        
        root.getChildren().add(basePane);
        basePane.setStyle("-fx-background-color: #0f172a;");
        
        // BUKA HALAMAN DATA SISWA DAN KIRIMKAN USERNAME GURU INI
        dataSiswaCache = new DataSiswaView(guruAktif.getUsername()).getView();

        VBox content = new VBox(40); 
        content.setAlignment(Pos.CENTER); 
        content.setPadding(new Insets(50));

        VBox headerBox = new VBox(10);
        headerBox.setAlignment(Pos.CENTER);
        
        Label title = new Label("Selamat Datang, " + guruAktif.getNama());
        title.setStyle("-fx-text-fill: white; -fx-font-size: 42px; -fx-font-weight: bold;");
        
        Label subtitle = new Label("Mata Pelajaran: " + guruAktif.getMataPelajaran());
        subtitle.setStyle("-fx-text-fill: #38bdf8; -fx-font-size: 18px; -fx-font-weight: bold;");
        
        headerBox.getChildren().addAll(title, subtitle);

        HBox cards = new HBox(30); 
        cards.setAlignment(Pos.CENTER);
        cards.getChildren().addAll(
                createCard("Total Siswa", totalLabel),
                createCard("Lulus", lulusLabel),
                createCard("Tidak Lulus", gagalLabel),
                createCard("Rata-rata", rataLabel)
        );

        content.getChildren().addAll(headerBox, cards);

        ScrollPane scrollPane = new ScrollPane(content);
        scrollPane.setFitToWidth(true); scrollPane.setFitToHeight(true); 
        scrollPane.setStyle("-fx-background: transparent; -fx-background-color: transparent;");
        basePane.setCenter(scrollPane);

        VBox sidebar = new VBox(15); 
        sidebar.setPadding(new Insets(30, 20, 30, 20)); 
        sidebar.setPrefWidth(260); sidebar.setMaxWidth(260); 
        sidebar.setStyle("-fx-background-color: #1e293b; -fx-border-color: #334155; -fx-border-width: 0 1 0 0; -fx-effect: dropshadow(three-pass-box, rgba(0,0,0,0.8), 20, 0, 5, 0);");
        StackPane.setAlignment(sidebar, Pos.CENTER_LEFT);
        sidebar.setTranslateX(-260); 

        Label brandTitle = new Label("SIMANESSA");
        brandTitle.setStyle("-fx-text-fill: white; -fx-font-size: 24px; -fx-font-weight: 900; -fx-letter-spacing: 2px;");
        Region divider1 = new Region(); divider1.setPrefHeight(1); divider1.setStyle("-fx-background-color: #334155;");

        VBox roleBox = new VBox(3);
        roleBox.setAlignment(Pos.CENTER_LEFT);
        Label roleLabel = new Label("NUPTK: " + guruAktif.getNuptk());
        roleLabel.setStyle("-fx-text-fill: #94a3b8; -fx-font-size: 13px; -fx-font-weight: bold;");
        Label nameLabel = new Label(guruAktif.getMataPelajaran());
        nameLabel.setStyle("-fx-text-fill: #38bdf8; -fx-font-size: 14px; -fx-font-weight: bold;");
        roleBox.getChildren().addAll(roleLabel, nameLabel);

        Region divider2 = new Region(); divider2.setPrefHeight(1); divider2.setStyle("-fx-background-color: #334155;");

        Button dashboardBtn = createSidebarBoxButton("🏠 Dashboard");
        Button siswaBtn = createSidebarBoxButton("📚 Data Siswa");

        Region spacer = new Region(); VBox.setVgrow(spacer, Priority.ALWAYS);

        Button logoutBtn = new Button("🚪 Logout Guru");
        logoutBtn.setMaxWidth(Double.MAX_VALUE);
        logoutBtn.setStyle("-fx-background-color: #7F1D1D; -fx-text-fill: white; -fx-font-weight: bold; -fx-padding: 12; -fx-background-radius: 8; -fx-cursor: hand;");
        logoutBtn.setOnAction(e -> {
            if (AlertHelper.showConfirm("Logout", "Keluar dari akun guru?")) app.logoutWithSplash();
        });

        sidebar.getChildren().addAll(brandTitle, divider1, roleBox, divider2, dashboardBtn, sidebarButtonSpacer(), siswaBtn, spacer, logoutBtn);
        root.getChildren().add(sidebar);

        TranslateTransition slideIn = new TranslateTransition(Duration.millis(250), sidebar); slideIn.setToX(0);
        TranslateTransition slideOut = new TranslateTransition(Duration.millis(250), sidebar); slideOut.setToX(-260);
        final boolean[] isSidebarOpen = {false};

        root.addEventFilter(MouseEvent.MOUSE_MOVED, e -> {
            double mouseX = e.getSceneX();
            if (mouseX <= 25 && !isSidebarOpen[0]) { slideOut.stop(); slideIn.playFromStart(); isSidebarOpen[0] = true; } 
            else if (mouseX > 260 && isSidebarOpen[0]) { slideIn.stop(); slideOut.playFromStart(); isSidebarOpen[0] = false; }
        });

        dashboardBtn.setOnAction(event -> {
            manager.loadData(); refreshStats(); basePane.setCenter(scrollPane);
            slideOut.playFromStart(); isSidebarOpen[0] = false;
        });

        siswaBtn.setOnAction(event -> {
            basePane.setCenter(dataSiswaCache);
            slideOut.playFromStart(); isSidebarOpen[0] = false;
        });

        refreshStats();
    }

    private Button createSidebarBoxButton(String text) {
        Button btn = new Button(text);
        btn.setMaxWidth(Double.MAX_VALUE); btn.setAlignment(Pos.CENTER_LEFT); btn.setPadding(new Insets(12, 15, 12, 15));
        btn.setStyle("-fx-background-color: transparent; -fx-text-fill: #CBD5E1; -fx-font-size: 15px; -fx-font-weight: bold; -fx-background-radius: 8; -fx-cursor: hand;");
        btn.setOnMouseEntered(e -> btn.setStyle("-fx-background-color: #2563EB; -fx-text-fill: white; -fx-font-size: 15px; -fx-font-weight: bold; -fx-background-radius: 8; -fx-cursor: hand;"));
        btn.setOnMouseExited(e -> btn.setStyle("-fx-background-color: transparent; -fx-text-fill: #CBD5E1; -fx-font-size: 15px; -fx-font-weight: bold; -fx-background-radius: 8; -fx-cursor: hand;"));
        return btn;
    }

    private Region sidebarButtonSpacer() { Region r = new Region(); r.setPrefHeight(2); return r; }

    private VBox createCard(String title, Label value) {
        VBox card = new VBox(15); card.setAlignment(Pos.CENTER); card.setPadding(new Insets(30)); card.setPrefWidth(250); 
        card.setStyle("-fx-background-color: #1e293b; -fx-background-radius: 20; -fx-effect: dropshadow(three-pass-box, rgba(0,0,0,0.2), 10, 0, 0, 5);");
        Label titleLabel = new Label(title); titleLabel.setStyle("-fx-text-fill: #93c5fd; -fx-font-size: 18px;");
        value.setStyle("-fx-text-fill: white; -fx-font-size: 48px; -fx-font-weight: bold;"); 
        card.getChildren().addAll(titleLabel, value);
        return card;
    }

    private void refreshStats() {
        manager.loadData();
        int total = manager.getStudents().size(); int lulus = 0; int gagal = 0; double totalNilai = 0;
        for (Student s : manager.getStudents()) {
            totalNilai += s.getNilaiAkhir();
            if (s.getStatus().equals("Lulus")) lulus++; else gagal++;
        }
        double rata = (total > 0) ? (totalNilai / total) : 0;
        totalLabel.setText(String.valueOf(total)); lulusLabel.setText(String.valueOf(lulus));
        gagalLabel.setText(String.valueOf(gagal)); rataLabel.setText(String.format("%.2f", rata));
    }

    public Parent getView() { return root; }
}