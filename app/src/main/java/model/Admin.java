package model;

public class Admin extends User {

    private String levelAkses;

    public Admin(String username, String password) {
        super(username, password, "Admin");
        this.levelAkses = "Super Administrator";
    }

    public String getLevelAkses() {
        return levelAkses;
    }

    public void setLevelAkses(String levelAkses) {
        this.levelAkses = levelAkses;
    }
}