package controller;

import model.Guru;
import java.io.*;
import java.util.ArrayList;

public class GuruManager {
    private final String FILE_PATH = "guru_data.txt";
    private ArrayList<Guru> listGuru = new ArrayList<>();

    public GuruManager() { loadData(); }

    public void addGuru(Guru newGuru) { listGuru.add(newGuru); saveData(); }
    public void deleteGuru(Guru guru) { listGuru.removeIf(g -> g.getUsername().equals(guru.getUsername())); saveData(); }
    public ArrayList<Guru> getListGuru() { return listGuru; }

    public boolean isUsernameExists(String username) {
        return listGuru.stream().anyMatch(g -> g.getUsername().equalsIgnoreCase(username));
    }

    public boolean isNuptkExists(String nuptk) {
        return listGuru.stream().anyMatch(g -> g.getNuptk().equals(nuptk));
    }

    public boolean isNamaExists(String nama) {
        return listGuru.stream().anyMatch(g -> g.getNama().equalsIgnoreCase(nama));
    }

    public Guru authenticate(String username, String password) {
        for (Guru g : listGuru) {
            if (g.getUsername().equals(username) && g.getPassword().equals(password)) return g;
        }
        return null;
    }

    public void loadData() {
        listGuru.clear();
        File file = new File(FILE_PATH);
        if (!file.exists()) return;

        try (BufferedReader br = new BufferedReader(new FileReader(file))) {
            String line;
            boolean isFirstLine = true;
            while ((line = br.readLine()) != null) {
                if (isFirstLine) {
                    isFirstLine = false;
                    if (line.startsWith("USERNAME;PASSWORD")) continue; 
                }
                String[] parts = line.split(";");
                if (parts.length == 5) {
                    listGuru.add(new Guru(parts[0], parts[1], parts[2], parts[3], parts[4]));
                }
            }
        } catch (Exception e) { System.out.println("Gagal membaca data guru."); }
    }

    private void saveData() {
        try (PrintWriter pw = new PrintWriter(new FileWriter(FILE_PATH))) {
            pw.println("USERNAME;PASSWORD;NAMA LENGKAP;NUPTK;MATA PELAJARAN");
            for (Guru g : listGuru) {
                pw.println(g.getUsername() + ";" + g.getPassword() + ";" + g.getNama() + ";" + g.getNuptk() + ";" + g.getMataPelajaran());
            }
        } catch (Exception e) { System.out.println("Gagal menyimpan data guru."); }
    }
}