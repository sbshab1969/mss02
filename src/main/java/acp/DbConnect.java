package acp;

import java.io.*;
import java.sql.*;
import java.util.*;

public class DbConnect {
  private static Connection dbConnection = buildConnection();

  private static String driver;
  private static String conn;
  private static String login;
  private static String pass;
  
  private static Connection buildConnection() {
    Connection myConnection = null;
    loadFileProp();
    try {
//      Class.forName(driver);
      Class.forName(driver).newInstance();
      myConnection = DriverManager.getConnection(conn, login, pass);
    } catch (Exception e) { 
      e.printStackTrace();
    }
    return myConnection;
  }

  private static void loadFileProp() {
    // 1. Открытие файла
    FileInputStream fis = null;
    Properties props = new Properties();
    try {
      fis = new FileInputStream("oracle.conf");
      props.loadFromXML(fis);
    } catch (Exception e) {
      e.printStackTrace();
    }
    if (fis != null) {
      try {
        fis.close();
      } catch (IOException e) {
        e.printStackTrace();
      }
    }  
    driver = props.getProperty("Driver");
    conn   = props.getProperty("ConnectionString");
    login  = props.getProperty("User");
    pass   = props.getProperty("Password");
  }

  public static void disconnect() {
  //  System.out.println("disconnect");
    if (dbConnection == null) {
      return;
    }
    try {
	  dbConnection.close();
    } catch (SQLException e) {
	  e.printStackTrace();
    }
  	dbConnection = null;
  }

  public static Connection getDbConnection() {
    return dbConnection;
  }

  public static boolean testConnection() {
    if (dbConnection == null) {
      return false;
    } else { 
      return true;
    }  
  }

}
