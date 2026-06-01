package view;

import controller.StudentManager;
import javafx.geometry.Insets;
import javafx.scene.Parent;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.stage.Stage;
import model.Student;

import java.util.ArrayList;

public class EditStudentView {

    private VBox root = new VBox(15);
    private Student student;
    private StudentManager manager;
    
    private VBox tugasBox = new VBox(10);
    private ArrayList<TextField> tugasFields = new ArrayList<>();

    public EditStudentView(Student student, String guruUsername) {
        this.student = student;
        this.manager = new StudentManager(guruUsername);

        root.setPadding(new Insets(30));
        root.setStyle("-fx-background-color: #0f172a;");

        Label title = new Label("Edit Data Siswa");
        title.setStyle("-fx-text-fill: white; -fx-font-size: 28px; -fx-font-weight: bold;");
        VBox.setMargin(title, new Insets(0, 0, 15, 0));

        TextField nisField = createField("NIS Siswa");
        nisField.setText(student.getNis());
        // Kunci NIS telah dilepas sehingga Guru bisa memperbaikinya jika ada salah ketik

        TextField namaField = createField("Nama Lengkap");
        namaField.setText(student.getNama());

        TextField kelasField = createField("Kelas");
        kelasField.setText(student.getKelas());

        HBox nilaiBox = new HBox(10);
        TextField utsField = createField("Nilai UTS");
        utsField.setText(String.valueOf(student.getUts()));
        
        TextField uasField = createField("Nilai UAS");
        uasField.setText(String.valueOf(student.getUas()));
        
        HBox.setHgrow(utsField, Priority.ALWAYS);
        HBox.setHgrow(uasField, Priority.ALWAYS);
        nilaiBox.getChildren().addAll(utsField, uasField);

        for (int i = 0; i < student.getTugasList().size(); i++) {
            TextField tugasField = createField("Nilai Tugas " + (i + 1));
            tugasField.setText(String.valueOf(student.getTugasList().get(i)));
            tugasFields.add(tugasField);
            tugasBox.getChildren().add(tugasField);
        }

        Button tambahTugasBtn = new Button("+ Tambah Tugas");
        tambahTugasBtn.setStyle("-fx-background-color: #334155; -fx-text-fill: white; -fx-font-size: 13px; -fx-font-weight: bold; -fx-background-radius: 8; -fx-padding: 10; -fx-cursor: hand; -fx-border-color: #475569; -fx-border-radius: 8;");
        tambahTugasBtn.setMaxWidth(Double.MAX_VALUE);
        tambahTugasBtn.setOnAction(event -> {
            TextField tugasField = createField("Nilai Tugas " + (tugasFields.size() + 1));
            tugasFields.add(tugasField);
            tugasBox.getChildren().add(tugasField);
        });

        Button simpanBtn = new Button("Simpan Perubahan");
        simpanBtn.setStyle("-fx-background-color: #1E6FD9; -fx-text-fill: white; -fx-font-size: 14px; -fx-font-weight: bold; -fx-background-radius: 8; -fx-padding: 12; -fx-cursor: hand;");
        simpanBtn.setMaxWidth(Double.MAX_VALUE);
        VBox.setMargin(simpanBtn, new Insets(15, 0, 0, 0));

        simpanBtn.setOnAction(e -> {
            try {
                String nis = nisField.getText().trim();
                String nama = namaField.getText().trim();
                String kelas = kelasField.getText().trim();
                String utsStr = utsField.getText().trim();
                String uasStr = uasField.getText().trim();

                if (nis.isEmpty() || nama.isEmpty() || kelas.isEmpty() || utsStr.isEmpty() || uasStr.isEmpty()) {
                    AlertHelper.showError("Semua form Identitas dan Nilai wajib diisi penuh!"); 
                    return;
                }

                if (nis.contains(";") || nama.contains(";") || kelas.contains(";")) {
                    AlertHelper.showError("Input dilarang mengandung karakter titik koma (;)"); 
                    return;
                }

                if (!nis.matches("\\d+")) { AlertHelper.showError("NIS harus murni angka bulat!"); return; }
                if (nama.matches(".*\\d.*")) { AlertHelper.showError("Nama siswa tidak boleh mengandung angka!"); return; }

                // PENGAMAN BARU: Mengecek jika NIS atau Nama diubah agar tidak bertabrakan dengan data lain
                boolean nisChanged = !nis.equals(this.student.getNis());
                boolean namaChanged = !nama.equalsIgnoreCase(this.student.getNama());

                if (nisChanged || namaChanged) {
                    for (Student s : manager.getStudents()) {
                        if (s.getNis().equals(this.student.getNis())) continue; // Lewati siswa yang sedang diedit ini

                        if (nisChanged && s.getNis().equals(nis)) {
                            AlertHelper.showError("NIS ini sudah dipakai oleh siswa lain!");
                            return;
                        }
                        if (namaChanged && s.getNama().equalsIgnoreCase(nama)) {
                            AlertHelper.showError("Nama siswa ini sudah terdaftar di sistem!");
                            return;
                        }
                    }
                }

                double uts = Double.parseDouble(utsStr);
                double uas = Double.parseDouble(uasStr);

                if (uts < 0 || uts > 100 || uas < 0 || uas > 100) { AlertHelper.showError("Nilai ujian harus berada di rentang 0 - 100"); return; }

                ArrayList<Double> tugasList = new ArrayList<>();
                for (TextField tf : tugasFields) {
                    String text = tf.getText().trim();
                    if (text.isEmpty()) {
                        tugasList.add(0.0);
                    } else {
                        double nilai = Double.parseDouble(text);
                        if (nilai < 0 || nilai > 100) { AlertHelper.showError("Nilai tugas harus berada di rentang 0 - 100"); return; }
                        tugasList.add(nilai);
                    }
                }

                double akhir = manager.calculateFinalScore(uts, uas, tugasList);
                Student newStudent = new Student(nis, nama, kelas, uts, uas, tugasList, akhir);

                manager.updateStudent(this.student, newStudent);
                AlertHelper.showInfo("Sukses", "Data siswa berhasil diperbarui!");
                
                Stage stage = (Stage) simpanBtn.getScene().getWindow();
                stage.close();

            } catch (NumberFormatException ex) {
                AlertHelper.showError("Format nilai salah! Kolom nilai hanya boleh diisi angka/desimal.");
            } catch (Exception ex) {
                AlertHelper.showError("Terjadi kesalahan sistem saat memproses input data.");
            }
        });

        root.getChildren().addAll(
                title, 
                new Label("Data Identitas"), nisField, namaField, kelasField,
                new Label("Nilai Ujian"), nilaiBox,
                new Label("Nilai Tugas"), tambahTugasBtn, tugasBox,
                new Region(), simpanBtn
        );

        for (javafx.scene.Node node : root.getChildren()) {
            if (node instanceof Label && node != title) {
                node.setStyle("-fx-text-fill: #94a3b8; -fx-font-size: 12px; -fx-font-weight: bold;");
            }
        }
    }

    private TextField createField(String prompt) {
        TextField field = new TextField();
        field.setPromptText(prompt);
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