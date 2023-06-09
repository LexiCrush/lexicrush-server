package com.springboot.app.springbootfirstapp;

import java.sql.Connection;
import java.sql.DriverManager;

public class DBUtil {

    public static Connection getConnection() throws Exception {

        Class.forName("org.sqlite.JDBC");
        String url = "jdbc:sqlite:src/main/resources/NounBankSQlite.db";
        return DriverManager.getConnection(url);
    }

    public static Connection getAuthConnection() throws Exception {

        Class.forName("org.sqlite.JDBC");
        String url = "jdbc:sqlite:src/main/resources/UserStorage.db";
        return DriverManager.getConnection(url);
    }
}
