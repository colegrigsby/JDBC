// Cole Grigsby + Robert Weber 


import java.sql.*;
// -----------------------

import java.util.*;
import java.io.*;
import java.lang.*;

public class JDBC {

	private static Connection conn;
	private static String tickName;
	
	public static void main(String args[]) {

		try {
			Class.forName("com.mysql.jdbc.Driver").newInstance();
		} catch (Exception ex) {
			System.out.println("Driver not found");
			System.out.println(ex);
		}

		String ticker = args[0];
		
		
		conn = openConnection();
		
		try {
			Statement test = conn.createStatement();
			String t=  "SELECT * FROM Securities WHERE ticker='"+ticker+"'";
			ResultSet r = test.executeQuery(t);
			boolean f = r.next();
			while (f) {
				String s = r.getString(1);
				tickName = r.getString(2);
				System.out.println(s + ", " + tickName);
				f = r.next();
			}
			//System.out.println(topTable("2016 Top Traded", Arrays.asList("Ticker", "Name"), Selector.GenQuery2(conn)).render());
		} catch (SQLException e) {
			e.printStackTrace();
		}
		
		runAndWrite(conn, ticker);
		
		
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
		ArrayList<String> years = Selector.years(conn); 
		HashMap<String, ArrayList<ArrayList<String>>> abs = new HashMap<>();
		
		for (String y: years){
			abs.put(y, Selector.GenQuery3_1(conn, y));
		}
		
		HashMap<String, ArrayList<ArrayList<String>>> rel = new HashMap<>();
		
		for (String y: years){
			rel.put(y, Selector.GenQuery3_2(conn, y));
		}
		
		r.topFivePerYear(years, abs, rel);
		
		// 4
		head = Arrays.asList("Ticker", "Name");
		r.topTable("Top Ten Stocks to Watch in 2017", head, Selector.GenQuery4(conn));
		
		// 5 
                ArrayList<ArrayList<String>> assesments = new ArrayList<ArrayList<String>>();

                ArrayList<ArrayList<String>> ratios = Selector.GenQuery5_1(conn);
                ArrayList<ArrayList<String>> increases = Selector.GenQuery5_2(conn);
                ArrayList<ArrayList<String>> volumes = Selector.GenQuery5_3(conn);

                for (int i = 0; i < ratios.size(); i++) {
                    float upRat = Float.parseFloat(ratios.get(i).get(1));
                    float percInc = Float.parseFloat(increases.get(i).get(1));
                    float stdVol = Float.parseFloat(volumes.get(i).get(1));

                    String str;

                    if (percInc < 0 && stdVol < 0) str = "Declining";
                    else if (percInc > 0.05 && stdVol < 0) str = "Increasing (Potential peak)";
                    else if (percInc < -0.05 && stdVol > 0) str = "Declining (Potential trough)";
                    else if (percInc > 0.05 && stdVol > 0) {
                        if (upRat > 1.1) str = "Increasing (More steady market)";
                        else str = "Increasing (More volatile market)";
                    }
                    else str = "Stagnant";

                    ArrayList<String> assesment = new ArrayList<String>();
                    assesment.add(ratios.get(i).get(0));
                    //assesment.add(ratios.get(i).get(1));
                    //assesment.add(increases.get(i).get(1));
                    //assesment.add(volumes.get(i).get(1));
                    assesment.add(str);

                    assesments.add(assesment);
                }

                head = Arrays.asList("Sector", "Assesment");
		r.topTable("Sector Assesments", head, assesments);
		r.tickerInfo(ticker, tickName);
		
		// 1 
		ArrayList<ArrayList<String>> a = Selector.IndivQuery1(conn, ticker);
		String start = a.get(0).get(0);
		String end = a.get(0).get(1);
		r.dateInfo(start, end);
		
		// 2

		r.byYearInfo(Selector.IndivQuery2(conn, ticker));
		
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
