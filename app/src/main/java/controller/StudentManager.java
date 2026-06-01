package controller;

import com.itextpdf.text.*;
import com.itextpdf.text.pdf.PdfPCell;
import com.itextpdf.text.pdf.PdfPTable;
import com.itextpdf.text.pdf.PdfWriter;
import model.Student;
import model.Guru; // Pastikan ini di-import agar bisa membaca data Guru
import model.WeightConfig;

import java.io.*;
import java.util.ArrayList;

public class StudentManager {
        
    private ArrayList<Student> students = new ArrayList<>();
    
    // Perbaikan: Variabel FILE_NAME tidak lagi statis/final
    private String FILE_NAME; 

    // Perbaikan: Wajib menerima username guru agar file terpisah (Isolasi Data)
    public StudentManager(String guruUsername) {
        this.FILE_NAME = "student_data_" + guruUsername + ".dat";
        loadData();
    }

    @SuppressWarnings("unchecked")
    public void loadData() {
        try {
            ObjectInputStream in = new ObjectInputStream(new FileInputStream(FILE_NAME));
            students = (ArrayList<Student>) in.readObject();
            in.close();
        } catch (Exception e) {
            students = new ArrayList<>();
        }
    }

    public void saveData() {
        try {
            ObjectOutputStream out = new ObjectOutputStream(new FileOutputStream(FILE_NAME));
            out.writeObject(students);
            out.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public ArrayList<Student> getStudents() {
        loadData();
        return students;
    }

    public void addStudent(Student student) {
        students.add(student);
        normalizeAllTasks();
        refreshAllStudentScores();
        saveData();
    }

    public void deleteStudent(Student student) {
        for (int i = 0; i < students.size(); i++) {
            if (students.get(i).getNis().equals(student.getNis())) {
                students.remove(i);
                break;
            }
        }
        refreshAllStudentScores();
        saveData();
    }

    public void updateStudent(Student oldStudent, Student newStudent) {
        for (int i = 0; i < students.size(); i++) {
            Student current = students.get(i);
            if (current.getNis().equals(oldStudent.getNis())) {
                students.set(i, newStudent);
                break;
            }
        }
        normalizeAllTasks();
        refreshAllStudentScores();
        saveData();
    }

    public boolean isDuplicate(String nis, String nama) {
        for (Student s : students) {
            if (s.getNis().equalsIgnoreCase(nis) || s.getNama().equalsIgnoreCase(nama)) {
                return true;
            }
        }
        return false;
    }

    public int getMaxTugas() {
        int max = 0;
        for (Student s : students) {
            if (s.getTugasList().size() > max) {
                max = s.getTugasList().size();
            }
        }
        return max;
    }

    public void normalizeAllTasks() {
        int maxTugas = getMaxTugas();
        for (Student s : students) {
            while (s.getTugasList().size() < maxTugas) {
                s.getTugasList().add(0.0);
            }
        }
    }

    public double calculateFinalScore(double uts, double uas, ArrayList<Double> tugasList) {
        double total = 0;
        for (double t : tugasList) {
            total += t;
        }

        double rataTugas = 0;
        if (!tugasList.isEmpty()) {
            rataTugas = total / tugasList.size();
        }
        return (uts * WeightConfig.utsWeight / 100.0) + (uas * WeightConfig.uasWeight / 100.0) + (rataTugas * WeightConfig.tugasWeight / 100.0);
    }

    public void refreshAllStudentScores() {
        for (Student s : students) {
            double akhir = calculateFinalScore(s.getUts(), s.getUas(), s.getTugasList());
            s.setNilaiAkhir(akhir);

            if (akhir >= 75) {
                s.setStatus("Lulus");
            } else {
                s.setStatus("Tidak Lulus");
            }
        }
        saveData();
    }

    // Fungsi Bantuan untuk membuat kolom tanpa garis (Kop Identitas)
    private PdfPCell createNoBorderCell(String text) {
        PdfPCell cell = new PdfPCell(new Phrase(text));
        cell.setBorder(Rectangle.NO_BORDER);
        return cell;
    }

    // Fungsi Export PDF yang sudah di-Upgrade
    public void exportPDF(Guru guruAktif) {
        try {
            // 1. Nama file dinamis agar tidak menimpa file guru lain
            String namaFileStr = guruAktif.getNama().replace(" ", "_");
            String dest = "Laporan_Nilai_" + namaFileStr + ".pdf";

            Document document = new Document();
            PdfWriter.getInstance(document, new FileOutputStream(dest));
            document.open();

            // 2. Judul Utama
            Paragraph title = new Paragraph("LAPORAN HASIL BELAJAR SISWA", FontFactory.getFont(FontFactory.HELVETICA_BOLD, 18));
            title.setAlignment(Element.ALIGN_CENTER);
            document.add(title);
            document.add(new Paragraph(" "));
            document.add(new Paragraph(" ")); // Spasi tambahan

            // 3. Kop Identitas Guru (Tabel Tanpa Garis agar Titik Dua Sejajar)
            PdfPTable infoTable = new PdfPTable(3);
            infoTable.setWidthPercentage(100);
            infoTable.setWidths(new float[]{2.5f, 0.2f, 7.3f}); // Proporsi lebar kolom

            // Baris Nama
            infoTable.addCell(createNoBorderCell("Nama Pendidik"));
            infoTable.addCell(createNoBorderCell(":"));
            infoTable.addCell(createNoBorderCell(guruAktif.getNama()));

            // Baris NUPTK
            infoTable.addCell(createNoBorderCell("NUPTK"));
            infoTable.addCell(createNoBorderCell(":"));
            infoTable.addCell(createNoBorderCell(guruAktif.getNuptk()));

            // Baris Mata Pelajaran
            infoTable.addCell(createNoBorderCell("Mata Pelajaran"));
            infoTable.addCell(createNoBorderCell(":"));
            infoTable.addCell(createNoBorderCell(guruAktif.getMataPelajaran()));

            // Baris Tanggal Cetak
            infoTable.addCell(createNoBorderCell("Tanggal Cetak"));
            infoTable.addCell(createNoBorderCell(":"));
            infoTable.addCell(createNoBorderCell(java.time.LocalDate.now().toString()));

            document.add(infoTable);
            document.add(new Paragraph(" ")); // Jarak sebelum tabel nilai siswa

            // 4. Tabel Nilai Siswa (Kode Lama Anda)
            PdfPTable table = new PdfPTable(7);
            table.setWidthPercentage(100); // Agar tabel memenuhi lebar kertas

            table.addCell("NIS");
            table.addCell("Nama");
            table.addCell("UTS");
            table.addCell("UAS");
            table.addCell("Tugas");
            table.addCell("Nilai Akhir");
            table.addCell("Status");

            for (Student s : students) {
                table.addCell(s.getNis());
                table.addCell(s.getNama());
                table.addCell(String.valueOf(s.getUts()));
                table.addCell(String.valueOf(s.getUas()));

                String tugasText = "";
                for (int i = 0; i < s.getTugasList().size(); i++) {
                    tugasText += "T" + (i + 1) + ": " + s.getTugasList().get(i) + "\n";
                }

                table.addCell(tugasText);
                table.addCell(String.valueOf(s.getNilaiAkhir()));
                table.addCell(s.getStatus());
            }
            
            document.add(table);
            document.close();
            
            System.out.println("PDF Berhasil dibuat dengan nama: " + dest);
            
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}