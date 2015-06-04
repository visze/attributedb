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
	
	public static Connection getConnection() throws SQLException {
		if (CONNECTION == null)
			connect();
		return CONNECTION;
	}
	
	public static void closeConnection() throws SQLException {
		CONNECTION.close();

	}

	private static void connect() throws SQLException {
		String url = "jdbc:postgresql://"+HOST+":"+PORT+"/"+DATABASE;
		Properties props = new Properties();
		props.setProperty("user",USER);
		props.setProperty("password",PASSWORD);
		props.setProperty("ssl","true");
		props.setProperty("sslfactory","org.postgresql.ssl.NonValidatingFactory");
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


}
