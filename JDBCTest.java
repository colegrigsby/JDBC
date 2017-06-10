// Cole Grigsby + Robert Weber 


import java.sql.*;
// -----------------------

import java.util.*;
import java.io.*;
import java.lang.*;

public class JDBCTest {

	private static Connection conn;
	
	public static void main(String args[]) {

		try {
			Class.forName("com.mysql.jdbc.Driver").newInstance();
		} catch (Exception ex) {
			System.out.println("Driver not found");
			System.out.println(ex);
		}

		String ticker = args[1];
		
		conn = openConnection();
		
		try {
			Statement test = conn.createStatement();
			String t=  "SELECT * FROM Securities WHERE ticker='"+ticker+"'";
			ResultSet r = test.executeQuery(t);
			boolean f = r.next();
			while (f) {
				String s = r.getString(1);
				String a = r.getString(2);
				System.out.println(s + ", " + a);
				f = r.next();
			}
			
		} catch (SQLException e) {
			e.printStackTrace();
		}

	}
	
	private static Connection openConnection() {
		Connection c = null;
		String username = "", password = "", database = ""; 
		
		File f;
		BufferedReader r; 
		try {
			f = new File("credentials.in");
			r = new BufferedReader(new FileReader(f));
			username=r.readLine();
			password=r.readLine();
			database=r.readLine(); 
			r.close();
		}
		catch(Exception e){
			System.out.println("credentials not found");
			System.out.println(e);
		}

		String url = "jdbc:mysql://cslvm74.csc.calpoly.edu/" + database + "?";

		try {
			
			c = DriverManager.getConnection(url, username, password);

		} catch (Exception ex) {
			System.out.println("Could not open connection");
			System.out.println(ex);
		}
		
		return c; 
	}

}
