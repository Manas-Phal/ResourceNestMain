package org.example.models;

import java.sql.Connection;

public class Main {
    public static void main(String[] args) {

        Connection conn = DBConnection.connect();

        if(conn != null){
            System.out.println("Database ready!");
        }
    }
}
