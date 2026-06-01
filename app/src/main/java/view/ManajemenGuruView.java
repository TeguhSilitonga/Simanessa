package view;

import controller.GuruManager;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.*;
import javafx.stage.Modality;
import javafx.stage.Stage;
import model.Guru;

import java.io.File;
import java.io.PrintWriter;

public class ManajemenGuruView {

    private BorderPane root = new BorderPane();
    private TableView<Guru> table = new TableView<>();
    private GuruManager guruManager = new GuruManager();
    private ObservableList<Guru> data = FXCollections.observableArrayList();
    
    private TextField searchField = new TextField(); 

    public ManajemenGuruView() {
        root.setPadding(new Insets(30));
        root.setStyle("-fx-background-color: #0f172a;");

        applyModernTableStyle();

        VBox leftContent = new VBox(20);
        
        HBox headerBox = new HBox(20);
        headerBox.setAlignment(Pos.CENTER_LEFT);
        Label title = new Label("Manajemen Data Guru");
        title.setStyle("-fx-text-fill: white; -fx-font-size: 32px; -fx-font-weight: bold;");
        
        searchField.setPromptText("🔍 Cari Nama atau NUPTK...");
        searchField.setPrefWidth(250);
        searchField.setStyle("-fx-background-color: #1e293b; -fx-text-fill: white; -fx-prompt-text-fill: #94a3b8; -fx-background-radius: 8; -fx-padding: 10 15; -fx-border-color: #334155; -fx-border-radius: 8;");
        searchField.textProperty().addListener((obs, oldVal, newVal) -> applyFilter());

        headerBox.getChildren().addAll(title, searchField);
        leftContent.getChildren().add(headerBox);

        createColumns();
        refreshTable();
        
        // Memaksa tabel untuk membagi ruang kosong ke semua kolom secara proporsional
        table.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY_FLEX_LAST_COLUMN);
        
        Label emptyLabel = new Label("Belum ada data pendidik yang terdaftar.");
        emptyLabel.setStyle("-fx-text-fill: #64748b; -fx-font-size: 16px; -fx-font-weight: bold;");
        table.setPlaceholder(emptyLabel);

        VBox.setVgrow(table, Priority.ALWAYS);
        leftContent.getChildren().add(table);

        VBox rightPanel = new VBox(15);
        rightPanel.setPrefWidth(350);
        rightPanel.setPadding(new Insets(25));
        rightPanel.setStyle("-fx-background-color: #1e293b; -fx-background-radius: 15; -fx-effect: dropshadow(three-pass-box, rgba(0,0,0,0.2), 10, 0, 0, 5);");
        BorderPane.setMargin(rightPanel, new Insets(0, 0, 0, 20));

        Label formTitle = new Label("Form Registrasi Guru");
        formTitle.setStyle("-fx-text-fill: white; -fx-font-size: 20px; -fx-font-weight: bold;");
        VBox.setMargin(formTitle, new Insets(0, 0, 10, 0));

        TextField namaField = createField("Nama Lengkap Guru");
        TextField nuptkField = createField("NUPTK Guru");
        TextField mapelField = createField("Mata Pelajaran");
        TextField userField = createField("Username Akun");
        PasswordField passField = new PasswordField();
        passField.setPromptText("Password Akun");
        passField.setStyle("-fx-background-color: #334155; -fx-text-fill: white; -fx-prompt-text-fill: #94a3b8; -fx-background-radius: 8; -fx-padding: 12;");

        Button simpanBtn = new Button("Daftarkan Guru Baru");
        simpanBtn.setStyle("-fx-background-color: #1E6FD9; -fx-text-fill: white; -fx-font-size: 14px; -fx-font-weight: bold; -fx-background-radius: 8; -fx-padding: 12; -fx-cursor: hand;");
        simpanBtn.setMaxWidth(Double.MAX_VALUE);

        simpanBtn.setOnAction(e -> {
            String nama = namaField.getText().trim();
            String nuptk = nuptkField.getText().trim();
            String mapel = mapelField.getText().trim();
            String username = userField.getText().trim();
            String password = passField.getText().trim();

            if (nama.isEmpty() || nuptk.isEmpty() || mapel.isEmpty() || username.isEmpty() || password.isEmpty()) {
                AlertHelper.showError("Semua kolom input wajib diisi!"); return;
            }
            
            // Validasi format nama dan karakter dilarang
            if (!nama.matches("^[a-zA-Z\\s]+$")) {
                AlertHelper.showError("Nama guru hanya boleh berisi huruf abjad dan spasi!"); return;
            }

            if (mapel.contains(";") || username.contains(";") || password.contains(";")) {
                AlertHelper.showError("Input tidak boleh mengandung karakter titik koma (;)"); return;
            }

            if (!nuptk.matches("\\d+")) { AlertHelper.showError("NUPTK harus berupa angka bulat!"); return; }
            if (username.contains(" ")) { AlertHelper.showError("Username tidak boleh mengandung spasi!"); return; }
            
            // Pengecekan duplikasi data di sistem
            if (guruManager.isUsernameExists(username) || username.equalsIgnoreCase("admin")) {
                AlertHelper.showError("Username sudah terdaftar! Gunakan username lain."); return;
            }

            if (guruManager.isNuptkExists(nuptk)) {
                AlertHelper.showError("NUPTK ini sudah terdaftar atas nama guru lain!"); return;
            }

            if (guruManager.isNamaExists(nama)) {
                AlertHelper.showError("Nama pendidik ini sudah terdaftar di sistem!"); return;
            }

            Guru newGuru = new Guru(username, password, nama, nuptk, mapel);
            guruManager.addGuru(newGuru);
            refreshTable(); 
            applyFilter();
            namaField.clear(); nuptkField.clear(); mapelField.clear(); userField.clear(); passField.clear();
            AlertHelper.showInfo("Sukses", "Akun guru berhasil didaftarkan ke dalam sistem!");
        });

        rightPanel.getChildren().addAll(
                formTitle, new Label("Biodata Pendidik"), namaField, nuptkField, mapelField,
                new Label("Kredensial Login"), userField, passField, new Region(), simpanBtn
        );

        for (javafx.scene.Node node : rightPanel.getChildren()) {
            if (node instanceof Label && node != formTitle) node.setStyle("-fx-text-fill: #94a3b8; -fx-font-size: 12px; -fx-font-weight: bold;");
        }

        root.setCenter(leftContent); root.setRight(rightPanel);

        // Fitur klik ganda untuk memunculkan jendela Edit Guru
        table.setRowFactory(tv -> {
            TableRow<Guru> row = new TableRow<>();
            row.setOnMouseClicked(event -> {
                if (event.getClickCount() == 2 && !row.isEmpty()) {
                    Guru g = row.getItem();
                    EditGuruView editView = new EditGuruView(g);
                    Stage stage = new Stage();
                    stage.setScene(new Scene(editView.getView(), 450, 600));
                    stage.setTitle("Edit Data Guru");
                    stage.setResizable(false);
                    stage.initModality(Modality.APPLICATION_MODAL); 
                    stage.showAndWait(); 
                    refreshTable(); 
                    applyFilter();
                }
            });
            return row;
        });
    }

    private void refreshTable() {
        guruManager.loadData();
        data.clear(); 
        data.addAll(guruManager.getListGuru());
        applyFilter(); 
    }
    
    // Logika pencarian data pada tabel
    private void applyFilter() {
        String keyword = searchField.getText().toLowerCase();
        ObservableList<Guru> filtered = FXCollections.observableArrayList();
        for (Guru g : data) {
            if (g.getNama().toLowerCase().contains(keyword) || g.getNuptk().contains(keyword)) {
                filtered.add(g);
            }
        }
        table.setItems(filtered);
    }

    @SuppressWarnings("unchecked")
    private void createColumns() {
        table.getColumns().clear();

        TableColumn<Guru, String> nuptkCol = new TableColumn<>("NUPTK");
        nuptkCol.setCellValueFactory(new PropertyValueFactory<>("nuptk"));
        nuptkCol.setPrefWidth(100); 
        nuptkCol.setComparator((n1, n2) -> {
            try { return Long.valueOf(n1).compareTo(Long.valueOf(n2)); } 
            catch (NumberFormatException e) { return n1.compareTo(n2); }
        });

        TableColumn<Guru, String> namaCol = new TableColumn<>("Nama Pendidik");
        namaCol.setCellValueFactory(new PropertyValueFactory<>("nama"));
        namaCol.setPrefWidth(200);

        TableColumn<Guru, String> mapelCol = new TableColumn<>("Mata Pelajaran");
        mapelCol.setCellValueFactory(new PropertyValueFactory<>("mataPelajaran"));
        mapelCol.setPrefWidth(150);

        TableColumn<Guru, String> userCol = new TableColumn<>("Username");
        userCol.setCellValueFactory(new PropertyValueFactory<>("username"));
        userCol.setPrefWidth(120);

        TableColumn<Guru, String> passCol = new TableColumn<>("Password");
        passCol.setCellValueFactory(new PropertyValueFactory<>("password"));
        passCol.setPrefWidth(120);
        
        // Sensor otomatis untuk menyembunyikan karakter password dari layar
        passCol.setCellFactory(param -> new TableCell<Guru, String>() {
            @Override
            protected void updateItem(String item, boolean empty) {
                super.updateItem(item, empty);
                if (empty || item == null) {
                    setText(null);
                } else {
                    setText("••••••••"); 
                }
            }
        });

        TableColumn<Guru, Void> aksiCol = new TableColumn<>("Aksi");
        aksiCol.setPrefWidth(80);
        aksiCol.setCellFactory(param -> new TableCell<>() {
            private final Button delBtn = new Button("🗑");
            {
                delBtn.setStyle("-fx-background-color: #dc2626; -fx-text-fill: white; -fx-font-weight: bold; -fx-background-radius: 6; -fx-cursor: hand;");
                delBtn.setOnAction(event -> {
                    Guru g = getTableView().getItems().get(getIndex());
                    if (AlertHelper.showConfirm("Hapus Guru", "Hapus akun " + g.getNama() + "?")) {
                        guruManager.deleteGuru(g); refreshTable(); 
                    }
                });
            }
            @Override
            protected void updateItem(Void item, boolean empty) {
                super.updateItem(item, empty);
                if (empty) setGraphic(null); else { setGraphic(delBtn); setStyle("-fx-alignment: CENTER;"); }
            }
        });

        // Kunci posisi kolom agar pengguna tidak bisa menggeser atau mengubah urutannya
        nuptkCol.setReorderable(false);
        namaCol.setReorderable(false);
        mapelCol.setReorderable(false);
        userCol.setReorderable(false);
        passCol.setReorderable(false);
        aksiCol.setReorderable(false);

        table.getColumns().addAll(nuptkCol, namaCol, mapelCol, userCol, passCol, aksiCol);
    }

    private void applyModernTableStyle() {
        try {
            File cssFile = new File("modern_table.css");
            PrintWriter writer = new PrintWriter(cssFile);
            writer.println(".table-view { -fx-background-color: #1e293b; -fx-background-radius: 12px; -fx-border-radius: 12px; -fx-padding: 10px; }");
            writer.println(".table-view .column-header-background { -fx-background-color: transparent; }");
            writer.println(".table-view .column-header, .table-view .filler { -fx-background-color: transparent; -fx-border-width: 0 0 2 0; -fx-border-color: #334155; -fx-size: 40px; }");
            writer.println(".table-view .column-header .label { -fx-text-fill: #94a3b8; -fx-font-weight: bold; -fx-font-size: 13px; }");
            writer.println(".table-view .table-row-cell { -fx-background-color: #1e293b; -fx-border-width: 0 0 1 0; -fx-border-color: #334155; -fx-cell-size: 45px; }");
            writer.println(".table-view .table-row-cell:empty { -fx-background-color: #1e293b; -fx-border-color: transparent; }");
            writer.println(".table-view .table-row-cell:hover { -fx-background-color: #334155; }");
            writer.println(".table-view .table-cell { -fx-text-fill: white; -fx-font-size: 14px; -fx-alignment: center-left; }");
            writer.println(".table-view .scroll-bar:horizontal, .table-view .scroll-bar:vertical { -fx-background-color: transparent; }");
            writer.println(".table-view .scroll-bar:horizontal .thumb, .table-view .scroll-bar:vertical .thumb { -fx-background-color: #475569; -fx-background-radius: 5px; }");
            writer.close();
            root.getStylesheets().clear(); root.getStylesheets().add(cssFile.toURI().toString());
        } catch (Exception e) { e.printStackTrace(); }
    }

    private TextField createField(String prompt) {
        TextField field = new TextField(); field.setPromptText(prompt);
        field.setStyle("-fx-background-color: #334155; -fx-text-fill: white; -fx-prompt-text-fill: #94a3b8; -fx-background-radius: 8; -fx-padding: 12;");
        return field;
    }

    public Parent getView() { return root; }
}