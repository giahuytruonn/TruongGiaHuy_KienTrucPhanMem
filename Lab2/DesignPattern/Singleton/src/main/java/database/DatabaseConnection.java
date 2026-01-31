package database;

public class DatabaseConnection {
    private static DatabaseConnection instance;
    
    private DatabaseConnection() {
        System.out.println("DatabaseConnection được tạo lần đầu");
    }
    
    public static DatabaseConnection getInstance() {
        if (instance == null) {
            instance = new DatabaseConnection();
        }
        return instance;
    }
    
    public void connect() {
        System.out.println("Kết nối database...");
    }
    
    public void disconnect() {
        System.out.println("Ngắt kết nối database...");
    }
}
