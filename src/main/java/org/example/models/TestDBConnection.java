package org.example.models;

import java.sql.Connection;
import java.sql.DriverManager;

public class TestDBConnection {

    public static void main(String[] args) {

        try {

            // correct project database path
            String url = "jdbc:sqlite:C:/Users/manas/IdeaProjects/ResourceNestMain/resources.db";

            // print database location
            System.out.println("Connecting to DB...");
            System.out.println("DB Path: " + url);

            Connection conn = DriverManager.getConnection(url);

            if (conn != null) {
                System.out.println("Connection SUCCESSFUL");
            }

            conn.close();

        } catch (Exception e) {
            System.out.println("Connection FAILED");
            e.printStackTrace();
        }
    }
}
