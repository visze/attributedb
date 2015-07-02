package de.charite.compbio.attributedb.db;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.Properties;

public class DatabaseConnection {

	private static Connection CONNECTION = null;
	private static String HOST;
	private static String DATABASE;
	private static String USER;
	private static String PASSWORD;
	private static int PORT = 5432;
	private static boolean SQLITE = false;
	private static boolean H2 = false;

	public static Connection getConnection() throws SQLException {
		if (CONNECTION == null)
			connect();
		return CONNECTION;
	}

	public static void closeConnection() throws SQLException {
		CONNECTION.close();

	}

	private static void connect() throws SQLException {
		String url = "jdbc:postgresql://" + HOST + ":" + PORT + "/" + DATABASE;
		
		Properties props = new Properties();
		if (USER != null)
			props.setProperty("user", USER);
		if (PASSWORD != null)
			props.setProperty("password", PASSWORD);
		
		if (SQLITE)
			url = "jdbc:sqlite:" + DATABASE;
		else if (H2)
				url = "jdbc:h2:" + DATABASE;
		else {
			props.setProperty("ssl", "true");
			props.setProperty("sslfactory", "org.postgresql.ssl.NonValidatingFactory");
		}
		CONNECTION = DriverManager.getConnection(url, props);

	}

	public static void setHOST(String hOST) {
		HOST = hOST;
	}

	public static void setDATABASE(String dATABASE) {
		DATABASE = dATABASE;
	}

	public static void setUSER(String uSER) {
		USER = uSER;
	}

	public static void setPASSWORD(String pASSWORD) {
		PASSWORD = pASSWORD;
	}

	public static void setPORT(int pORT) {
		PORT = pORT;
	}
	
	public static void setSQLITE(boolean sqLite) {
		DatabaseConnection.SQLITE = sqLite;
	}

	public static void setH2(boolean b) {
		DatabaseConnection.H2 = b;
		
	}

}
