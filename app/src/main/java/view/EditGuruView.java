package view;

import controller.GuruManager;
import javafx.geometry.Insets;
import javafx.scene.Parent;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.stage.Stage;
import model.Guru;

public class EditGuruView {

    private VBox root = new VBox(15);
    private Guru guru;
    private GuruManager guruManager = new GuruManager();

    public EditGuruView(Guru guru) {
        this.guru = guru;

        root.setPadding(new Insets(30));
        root.setStyle("-fx-background-color: #0f172a;");

        Label title = new Label("Edit Data Guru");
        title.setStyle("-fx-text-fill: white; -fx-font-size: 28px; -fx-font-weight: bold;");
        VBox.setMargin(title, new Insets(0, 0, 10, 0));

        TextField namaField = createField("Nama Lengkap Guru");
        namaField.setText(guru.getNama());

        TextField nuptkField = createField("NUPTK Guru");
        nuptkField.setText(guru.getNuptk());

        TextField mapelField = createField("Mata Pelajaran");
        mapelField.setText(guru.getMataPelajaran());

        TextField userField = createField("Username Akun");
        userField.setText(guru.getUsername());

        TextField passField = createField("Password Akun");
        passField.setText(guru.getPassword());

        Button simpanBtn = new Button("Simpan Perubahan");
        simpanBtn.setStyle("-fx-background-color: #1E6FD9; -fx-text-fill: white; -fx-font-size: 14px; -fx-font-weight: bold; -fx-background-radius: 8; -fx-padding: 12; -fx-cursor: hand;");
        simpanBtn.setMaxWidth(Double.MAX_VALUE);
        VBox.setMargin(simpanBtn, new Insets(15, 0, 0, 0));

        simpanBtn.setOnAction(e -> {
            String nama = namaField.getText().trim();
            String nuptk = nuptkField.getText().trim();
            String mapel = mapelField.getText().trim();
            String username = userField.getText().trim();
            String password = passField.getText().trim();

            if (nama.isEmpty() || nuptk.isEmpty() || mapel.isEmpty() || username.isEmpty() || password.isEmpty()) {
                AlertHelper.showError("Semua kolom input wajib diisi!"); return;
            }

            // FIX BUG 3: Validasi Nama Sangat Ketat (Hanya huruf dan spasi)
            if (!nama.matches("^[a-zA-Z\\s]+$")) {
                AlertHelper.showError("Nama guru hanya boleh berisi huruf abjad dan spasi!"); return;
            }

            if (mapel.contains(";") || username.contains(";") || password.contains(";")) {
                AlertHelper.showError("Input tidak boleh mengandung karakter titik koma (;)"); return;
            }

            if (!nuptk.matches("\\d+")) { AlertHelper.showError("NUPTK harus berupa angka bulat!"); return; }
            if (username.contains(" ")) { AlertHelper.showError("Username tidak boleh mengandung spasi!"); return; }
            
            guruManager.loadData();

            if (!nuptk.equals(this.guru.getNuptk()) && guruManager.isNuptkExists(nuptk)) {
                AlertHelper.showError("NUPTK ini sudah dipakai oleh guru lain!"); return;
            }

            if (!username.equalsIgnoreCase(this.guru.getUsername())) {
                if (guruManager.isUsernameExists(username) || username.equalsIgnoreCase("admin")) {
                    AlertHelper.showError("Username sudah terdaftar! Gunakan username lain."); return;
                }
            }
            
            if (!nama.equalsIgnoreCase(this.guru.getNama()) && guruManager.isNamaExists(nama)) {
                AlertHelper.showError("Nama pendidik ini sudah dipakai oleh guru lain!"); return;
            }

            guruManager.deleteGuru(this.guru); 
            Guru updatedGuru = new Guru(username, password, nama, nuptk, mapel);
            guruManager.addGuru(updatedGuru); 

            AlertHelper.showInfo("Sukses", "Data akun guru berhasil diperbarui!");
            Stage stage = (Stage) simpanBtn.getScene().getWindow();
            stage.close();
        });

        root.getChildren().addAll(
            title, new Label("Biodata Pendidik"), namaField, nuptkField, mapelField, 
            new Label("Kredensial Login"), userField, passField, simpanBtn
        );

        for (javafx.scene.Node node : root.getChildren()) {
            if (node instanceof Label && node != title) node.setStyle("-fx-text-fill: #94a3b8; -fx-font-size: 12px; -fx-font-weight: bold;");
        }
    }

    private TextField createField(String prompt) {
        TextField field = new TextField(); field.setPromptText(prompt);
        field.setStyle("-fx-background-color: #334155; -fx-text-fill: white; -fx-prompt-text-fill: #94a3b8; -fx-background-radius: 8; -fx-padding: 12;");
        return field;
    }

    public Parent getView() {
        ScrollPane scrollPane = new ScrollPane(root);
        scrollPane.setFitToWidth(true);
        scrollPane.setStyle("-fx-background: #0f172a; -fx-background-color: #0f172a; -fx-border-color: transparent;");
        return scrollPane;
    }
}