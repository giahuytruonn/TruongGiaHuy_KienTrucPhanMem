package app;

import database.DatabaseConnection;

public class Main {
    public static void main(String[] args) {
        DatabaseConnection db1 = DatabaseConnection.getInstance();
        db1.connect();
        
        System.out.println();
        
        DatabaseConnection db2 = DatabaseConnection.getInstance();
        db2.connect();
        
        System.out.println();
        
        System.out.println("db1 == db2: " + (db1 == db2));
        System.out.println("db1 hashCode: " + db1.hashCode());
        System.out.println("db2 hashCode: " + db2.hashCode());
        
        db1.disconnect();
    }
}
