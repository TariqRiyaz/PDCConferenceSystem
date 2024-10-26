/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

/**
 *
 * @author tariq
 */
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class TestDerbyConnection {
    public static void main(String[] args) {
        String url = "jdbc:derby:conferenceDB;create=true";
        try {
            Class.forName("org.apache.derby.jdbc.EmbeddedDriver");
            Connection conn = DriverManager.getConnection(url);
            System.out.println("Connection successful!");
            conn.close();
        } catch (ClassNotFoundException e) {
            System.out.println("Derby driver not found.");
            e.printStackTrace();
        } catch (SQLException e) {
            System.out.println("Connection failed.");
            e.printStackTrace();
        }
    }
}

