// Cole Grigsby + Robert Weber 


import java.sql.*;
// -----------------------

import java.util.*;
import java.io.*;
import java.lang.*;

public class JDBC {

	private static Connection conn;
	
	public static void main(String args[]) {

		try {
			Class.forName("com.mysql.jdbc.Driver").newInstance();
		} catch (Exception ex) {
			System.out.println("Driver not found");
			System.out.println(ex);
		}

		String ticker = args[0];
		
		
		conn = openConnection();
		
		runAndWrite(conn, ticker);
		
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
			//System.out.println(topTable("2016 Top Traded", Arrays.asList("Ticker", "Name"), Selector.GenQuery2(conn)).render());
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
	
	public static void runAndWrite(Connection conn, String ticker){
		Report r = new Report(ticker);
		
		String fullYear = Selector.getFullYear(conn, ticker);
		
		//1 
		r.info(Selector.GenQuery1_1(conn), Selector.GenQuery1_2(conn),
				Selector.GenQuery1_3(conn), Selector.GenQuery1_4(conn));
		
		//2 
		List<String> head = Arrays.asList("Ticker", "Name");
		r.topTable("Most Heavily Traded 2016 Stocks", head, Selector.GenQuery2(conn));
		
		//3 
		//TODO r.topFivePerYear(Selector.GenQuery3(conn));
		
		// 4
		head = Arrays.asList("Ticker", "NAME TOFIX");
		r.topTable("Top Ten Stocks to Watch in 2017", head, Selector.GenQuery4(conn));
		
		// 5 
		
		
		// 1 
		ArrayList<ArrayList<String>> a = Selector.IndivQuery1(conn, ticker);
		String start = a.get(0).get(0);
		String end = a.get(1).get(0);
		r.dateInfo(start, end);
		
		// 2

		r.byYearInfo(Selector.IndivQuery2_1(conn, ticker));
		
		//3 
		r.lastYearInfo(fullYear, Selector.IndivQuery3_1(conn, ticker, fullYear));
		
		//4 
		head = Arrays.asList("Year", "Month");
		//TODO convert months to names 
		r.byYearBestMonth(Selector.IndivQuery4(conn, ticker));
		
		//5 
		List<String> dates = Arrays.asList("2015-01-01", "2015-06-01", "2015-10-01",
				"2016-01-01","2015-05-01","2015-10-01");
		for (String date: dates){
			//TODO 
			//r.predictions(Selector.IndivQuery5_1(conn, ticker, date));
			
		}
		
		// 6
		//r.outcomes(data);
		for (String date: dates){
			//TODO 
			//r.outcomes(Selector.IndivQuery6_1(conn, ticker, date));
			
		}
		
		//7
		r.compareTop(fullYear, Selector.IndivQuery7_1(conn, ticker, fullYear), Selector.IndivQuery7_2(conn, ticker, fullYear));
		
		// 8
		String ticker2 = "UAL";
		r.compareNearby(fullYear, ticker2, Selector.IndivQuery8(conn, ticker),Selector.IndivQuery8(conn, ticker2));
		
		r.writeFile();
		
	}

}
