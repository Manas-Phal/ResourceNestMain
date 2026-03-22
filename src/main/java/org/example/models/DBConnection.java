package org.example.models;

import java.sql.Connection;
import java.sql.DriverManager;

public class DBConnection {
    public static Connection connect() {
        try {
            // Using your absolute path
            String url = "jdbc:sqlite:C:/Users/manas/IdeaProjects/ResourceNestMain/resources.db";

            Class.forName("org.sqlite.JDBC");
            Connection conn = DriverManager.getConnection(url);

            System.out.println("✅ DB Connected Successfully");
            return conn;
        } catch (Exception e) {
            System.out.println("❌ DB Connection Failed");
            e.printStackTrace();
            return null;
        }
    }
}