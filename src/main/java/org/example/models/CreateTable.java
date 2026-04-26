package org.example.models;

import java.sql.Connection;
import java.sql.Statement;

public class CreateTable {

    public static void main(String[] args) {

        // The full table structure including the missing 'bookmark' column
        String sql = "CREATE TABLE IF NOT EXISTS resources (" +
                "id INTEGER PRIMARY KEY AUTOINCREMENT," +
                "subject TEXT," +
                "title TEXT," +
                "link TEXT," +
                "type TEXT," +
                "difficulty TEXT  "+
                "bookmark INTEGER DEFAULT 0)"; // Added this line

        try (Connection conn = DBConnection.connect();
             Statement stmt = conn.createStatement()) {

            if (conn == null) return;

            // 1. Create the table if it doesn't exist
            stmt.execute(sql);

            // 2. REPAIR STEP: In case the table exists but is missing the column
            try {
                stmt.execute("ALTER TABLE resources ADD COLUMN bookmark INTEGER DEFAULT 0");
                System.out.println("Added missing 'bookmark' column.");
            } catch (Exception e) {
                // If it already exists, this will error out—we can ignore it safely.
            }

            System.out.println("✅ Database Table is ready and updated!");

        } catch (Exception e) {
            System.out.println("❌ Failed to setup table");
            e.printStackTrace();
        }
    }
}