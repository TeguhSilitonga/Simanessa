package model;

public class User {
    protected String username;
    protected String password;
    protected String role; // Variabel baru untuk membedakan "Admin" dan "Guru"

    // Constructor yang diminta oleh Guru.java dan LoginView.java
    public User(String username, String password, String role) {
        this.username = username;
        this.password = password;
        this.role = role;
    }

    // Getter dan Setter
    public String getUsername() { 
        return username; 
    }
    
    public void setUsername(String username) { 
        this.username = username; 
    }

    public String getPassword() { 
        return password; 
    }
    
    public void setPassword(String password) { 
        this.password = password; 
    }

    // Fungsi getRole() yang sebelumnya dicari oleh VS Code dan tidak ketemu
    public String getRole() { 
        return role; 
    }
    
    public void setRole(String role) { 
        this.role = role; 
    }

    // Fungsi showRole() untuk ditampilkan di UI Sidebar
    public String showRole() {
        return role;
    }
}