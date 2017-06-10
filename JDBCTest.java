
//     ---------------------
import java.sql.*;
// -----------------------

import java.util.*;
import java.io.*;
import java.lang.*;

public class JDBCTest {

	private static Connection conn;
	private static int target = 1;

	//////////////////////////////////////////////////////////////////////////////////////
	//////////////////////////////////////////// CREATE
	////////////////////////////////////////////////////////////////////////////////////// CONNECTION///////////////////////

	public static void main(String args[]) {

		try {
			Class.forName("com.mysql.jdbc.Driver").newInstance();
		} catch (Exception ex) {
			System.out.println("Driver not found");
			System.out.println(ex);
		}
		;


		conn = openConnection();
		
		try {
			Statement test = conn.createStatement();
			String t=  "SELECT * FROM Securities WHERE ticker='GOOG'";
			ResultSet r = test.executeQuery(t);
		} catch (SQLException e) {
			// TODO Auto-generated catch block
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
		
		//TODO might be username instead
		String url = "jdbc:mysql://cslvm74.csc.calpoly.edu/" + database + "?";

		try {
			
			c = DriverManager.getConnection(url, username, password);

		} catch (Exception ex) {
			System.out.println("Could not open connection");
			System.out.println(ex);
		}
		
		return c; 
	}
>>>>>>> 41c192afaa09fd0d41bcd615fb0e90125950f054

}

/*
		System.out.println("Connected");
		
		try {
			Statement s1 = conn.createStatement();
			String table = "CREATE TABLE Books ";
			table = table + "(LibCode INT, Title VARCHAR(50), Author VARCHAR (50),";
			table = table + "PRIMARY KEY (LibCode) )";

			System.out.println(table);
			// s1.executeUpdate("use dekhtyar");
			s1.executeUpdate(table);

		} catch (Exception ee) {
			System.out.println(ee);
		}

		try {
			Statement s2 = conn.createStatement();
			s2.executeUpdate("INSERT INTO Books VALUES(1, 'Database Systems','Ullman')");
			s2.executeUpdate("INSERT INTO Books VALUES(2, 'Artificial Intelligence', 'Russel, Norvig')");
			s2.executeUpdate("INSERT INTO Books VALUES(3, 'Problem Solving in C', 'Hanly, Koffman')");

		} catch (Exception ee) {
			System.out.println(ee);
		}

		try {
			Statement s3 = conn.createStatement();
			ResultSet result = s3.executeQuery("SELECT Title, Author FROM Books");
			boolean f = result.next();
			while (f) {
				String s = result.getString(1);
				String a = result.getString(2);
				System.out.println(s + ", " + a);
				f = result.next();
			}

		} catch (Exception ee) {
			System.out.println(ee);
		}

		try {
			String psText = "INSERT INTO Books VALUES(?,?,?)";
			PreparedStatement ps = conn.prepareStatement(psText);

			ps.setInt(1, 4);
			ps.setString(2, "A Guide to LaTeX");
			ps.setString(3, "Kopka, Daly");

			ps.executeUpdate();
		} catch (Exception e03) {
			System.out.println(e03);
		}

		try {
			Statement s4 = conn.createStatement();
			ResultSet result = s4.executeQuery("SELECT Title, Author FROM Books");
			boolean f = result.next();
			while (f) {
				String s = result.getString(1);
				String a = result.getString(2);
				System.out.println(s + ", " + a);
				f = result.next();
			}

			s4.executeUpdate("DROP TABLE Books");
		} catch (Exception ee) {
			System.out.println(ee);
		}

		try {
			conn.close();
		} catch (Exception ex) {
			System.out.println("Unable to close connection");
		}
		;
*/
