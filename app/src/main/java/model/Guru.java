package model;

public class Guru extends User {
    private String nama;
    private String nuptk;
    private String mataPelajaran;

    public Guru(String username, String password, String nama, String nuptk, String mataPelajaran) {
        super(username, password, "Guru");
        this.nama = nama;
        this.nuptk = nuptk;
        this.mataPelajaran = mataPelajaran;
    }

    public String getNama() { return nama; }
    public void setNama(String nama) { this.nama = nama; }

    public String getNuptk() { return nuptk; }
    public void setNuptk(String nuptk) { this.nuptk = nuptk; }

    public String getMataPelajaran() { return mataPelajaran; }
    public void setMataPelajaran(String mataPelajaran) { this.mataPelajaran = mataPelajaran; }
}