/*Cole Grigsby + Robert Weber 
 * 
 * 
 */

import java.sql.*;
import java.util.*;
import java.io.*;
import java.lang.*;
import java.util.ArrayList;

public class Selector {

    public static ArrayList<ArrayList<String>> execQuery(Connection conn, String query, int columns) {
        ArrayList<ArrayList<String>> returnList = new ArrayList<ArrayList<String>>();

        try {
            Statement statement = conn.createStatement();
            ResultSet results = statement.executeQuery(query);
            boolean check = results.next();

            while (check) {
                ArrayList<String> row = new ArrayList<String>();

                for (int i = 0; i < columns; i++) {
                    row.add(results.getString(i + 1));
                }

                returnList.add(row);
                check = results.next();
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return returnList; 
    }

    public static ArrayList<ArrayList<String>> execPrepQuery(PreparedStatement statement, int columns) {
        ArrayList<ArrayList<String>> returnList = new ArrayList<ArrayList<String>>();

        try {
            ResultSet results = statement.executeQuery();
            boolean check = results.next();

            while (check) {
                ArrayList<String> row = new ArrayList<String>();

                for (int i = 0; i < columns; i++) {
                    row.add(results.getString(i + 1));
                }

                returnList.add(row);
                check = results.next();
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return returnList; 
    }

    public static ArrayList<ArrayList<String>> tickerQuery(Connection conn, String query, int columns, String ticker, int tickCnt) {
        ArrayList<ArrayList<String>> returnList = new ArrayList<ArrayList<String>>();

        try {
            PreparedStatement statement = conn.prepareStatement(query);
            for (int i = 0; i < tickCnt; i++) {
                statement.setString(i + 1, ticker);
            }
            return execPrepQuery(statement, columns);
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return new  ArrayList<ArrayList<String>>();
    }

    public static ArrayList<ArrayList<String>> dateQuery(Connection conn, String query, int columns, String ticker, String date) {
        ArrayList<ArrayList<String>> returnList = new ArrayList<ArrayList<String>>();

        try {
            PreparedStatement statement = conn.prepareStatement(query);
            
            statement.setString(1, ticker);
            statement.setString(2, date);
            statement.setString(3, date);

            return execPrepQuery(statement, columns);
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return new  ArrayList<ArrayList<String>>();
    }

    public static String getFullYear(Connection conn, String ticker) {
        String query =
            "SELECT year"
            + " FROM (SELECT YEAR(day) as year, MIN(Day) AS Min, MAX(Day) AS Max"
            + " FROM AdjustedPrices P"
            + " WHERE Ticker=?"
            + " GROUP BY year) a"
            + " WHERE MONTH(Min)=1 and MONTH(Max)=12"
            + " ORDER BY year DESC"
            + " LIMIT 1;";

        return tickerQuery(conn, query, 1, ticker, 1).get(0).get(0);
    }

    public static String getFullYearTwoStock(Connection conn, String ticker1, String ticker2) {
        String query =
            "SELECT year"
            + " FROM (SELECT YEAR(day) as year, MIN(Day) AS Min, MAX(Day) AS Max"
            + " FROM AdjustedPrices P"
            + " WHERE Ticker=?"
            + " GROUP BY year) a"
            + " JOIN"
            + " (SELECT YEAR(day) as year, MIN(Day) AS Min, MAX(Day) AS Max"
            + " FROM AdjustedPrices P"
            + " WHERE Ticker=?"
            + " GROUP BY year) b"
            + " USING (year, Min, Max)"
            + " WHERE MONTH(Min)=1 and MONTH(Max)=12"
            + " ORDER BY year DESC"
            + " LIMIT 1;";

        try {
            PreparedStatement statement = conn.prepareStatement(query);

            statement.setString(1, ticker1);
            statement.setString(2, ticker2);

            return execPrepQuery(statement, 1).get(0).get(0);
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return "";
    }


    public static String GenQuery1_1(Connection conn) {
        String query = 
            "SELECT COUNT(DISTINCT Ticker)"
            + " FROM Prices"
            + " WHERE YEAR(Day) < 2016;";

        return execQuery(conn, query, 1).get(0).get(0);
    }

    public static String GenQuery1_2(Connection conn) {
        String query =
            "SELECT COUNT(DISTINCT Ticker)"
            + " FROM Prices"
            + " WHERE YEAR(Day) < 2017;";

        return execQuery(conn, query, 1).get(0).get(0);
    }

    public static String GenQuery1_3(Connection conn) {
        String query =
            "SELECT COUNT(*)"
            + " FROM (SELECT Ticker, Close"
            + " FROM AdjustedPrices A"
            + " WHERE Day = (SELECT MAX(DAY)"
            + " FROM AdjustedPrices"
            + " WHERE YEAR(Day) = 2015)"
            + " GROUP BY Ticker, Close) AP1,"
            + " (SELECT Ticker, Close"
            + " FROM AdjustedPrices A"
            + " WHERE Day = (SELECT MAX(DAY)"
            + " FROM AdjustedPrices"
            + " WHERE YEAR(Day) = 2016)"
            + " GROUP BY Ticker, Close) AP2"
            + " WHERE AP1.Ticker = AP2.Ticker"
            + " AND AP1.Close < AP2.Close;";

        return execQuery(conn, query, 1).get(0).get(0);
    }

    public static String GenQuery1_4(Connection conn) {
        String query =
            "SELECT COUNT(*)"
            + " FROM (SELECT Ticker, Close"
            + " FROM AdjustedPrices A"
            + " WHERE Day = (SELECT MAX(DAY)"
            + " FROM AdjustedPrices"
            + " WHERE YEAR(Day) = 2015)"
            + " GROUP BY Ticker, Close) AP1,"
            + " (SELECT Ticker, Close"
            + " FROM AdjustedPrices A"
            + " WHERE Day = (SELECT MAX(DAY)"
            + " FROM AdjustedPrices"
            + " WHERE YEAR(Day) = 2016)"
            + " GROUP BY Ticker, Close) AP2"
            + " WHERE AP1.Ticker = AP2.Ticker"
            + " AND AP1.Close > AP2.Close;";

        return execQuery(conn, query, 1).get(0).get(0);
    }

    public static ArrayList<ArrayList<String>> GenQuery2(Connection conn) {
        String query =
            "SELECT S.Ticker, S.Name"
            + " FROM (SELECT S.Ticker, S.Name, SUM(Volume) AS Vol"
            + " FROM Prices P JOIN Securities S ON P.Ticker = S.Ticker"
            + " WHERE YEAR(Day) = 2016"
            + " GROUP BY S.Ticker, S.Name) S"
            + " WHERE 10 >="
            + " (SELECT COUNT(*)"
            + " FROM (SELECT SUM(Volume) AS Vol"
            + " FROM Prices P JOIN Securities S ON P.Ticker = S.Ticker"
            + " WHERE YEAR(Day) = 2016"
            + " GROUP BY S.Ticker) S1"
            + " WHERE S.Vol <= S1.Vol)"
            + " ORDER BY Vol;";

        return execQuery(conn, query, 2);
    }

    
    public static ArrayList<String> years(Connection conn){
    	String query = "SELECT YEAR(day) as year FROM AdjustedPrices GROUP BY year;";
    	ArrayList<String> y = new ArrayList<>();
    	Statement s;
		try {
			s = conn.createStatement();
	    	ResultSet r = s.executeQuery(query);
	    	
	        boolean check = r.next();
			while (check) {
					y.add(r.getString(1));
	                check = r.next();
	            }

		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
    	
    	
    	return y; 
    }

    public static ArrayList<ArrayList<String>> GenQuery3_1(Connection conn, String year) {
        String query =
            "SELECT Y.Ticker, S.Name"
            + " FROM (SELECT Ticker, YEAR(Day) AS Year, MIN(Day) AS Min, MAX(Day) AS Max"
            + " FROM AdjustedPrices P"
            + " GROUP BY Ticker, YEAR(Day)) Y, AdjustedPrices P1, AdjustedPrices P2, Securities S"
            + " WHERE Y.Year=" + year
            + "	AND P1.Day = Y.Min"
            + " AND P2.Day = Y.Max"
            + " AND P1.Ticker = Y.Ticker"
            + " AND P2.Ticker = Y.Ticker"
            + " AND S.Ticker = Y.Ticker"
            + " AND 5 >="
            + " (SELECT COUNT(*)"
            + " FROM (SELECT Ticker, YEAR(Day) AS Year,"
            + " MIN(Day) AS Min, MAX(Day) AS Max"
            + " FROM AdjustedPrices P"
            + " GROUP BY Ticker, YEAR(Day)) as Y1, AdjustedPrices P3, AdjustedPrices P4"
            + " WHERE P3.Day = Y1.Min"
            + " AND P4.Day = Y1.Max"
            + " AND P3.Ticker = Y1.Ticker"
            + " AND P4.Ticker = Y1.Ticker"
            + " AND Y1.Year = Y.Year"
            + " AND (P2.Close - P1.Open) <= (P4.Close - P3.Open))"
            + " ORDER BY Y.Year, Y.Ticker;";

        return execQuery(conn, query, 2);
    }
    
    public static ArrayList<ArrayList<String>> GenQuery3_2(Connection conn, String year) {
        String query =
            "SELECT Y.Ticker, S.Name"
            + " FROM (SELECT Ticker, YEAR(Day) AS Year, MIN(Day) AS Min, MAX(Day) AS Max"
            + " FROM AdjustedPrices P"
            + " GROUP BY Ticker, YEAR(Day)) Y, AdjustedPrices P1, AdjustedPrices P2, Securities S"
            + " WHERE Y.Year=" + year
            + "	AND P1.Day = Y.Min"
            + " AND P2.Day = Y.Max"
            + " AND P1.Ticker = Y.Ticker"
            + " AND P2.Ticker = Y.Ticker"
            + " AND S.Ticker = Y.Ticker"
            + " AND 5 >="
            + " (SELECT COUNT(*)"
            + " FROM (SELECT Ticker, YEAR(Day) AS Year,"
            + " MIN(Day) AS Min, MAX(Day) AS Max"
            + " FROM AdjustedPrices P"
            + " GROUP BY Ticker, YEAR(Day)) as Y1, AdjustedPrices P3, AdjustedPrices P4"
            + " WHERE P3.Day = Y1.Min"
            + " AND P4.Day = Y1.Max"
            + " AND P3.Ticker = Y1.Ticker"
            + " AND P4.Ticker = Y1.Ticker"
            + " AND Y1.Year = Y.Year"
            + " AND (P2.Close - P1.Open) / P1.Open <= (P4.Close - P3.Open) / P3.Open)"
            + " ORDER BY Y.Year, Y.Ticker;";

        return execQuery(conn, query, 2);
    }
    
    public static ArrayList<ArrayList<String>> GenQuery4(Connection conn) {
        String query = 
            "SELECT ticker, name FROM "
            + " (SELECT ticker, name, .5*UPRATIO + .1*STDVOLUME + 1.5*INC2016 as score"
            + " FROM "
            + " ((SELECT ticker, IF((b.r IS NULL ), a.r-1, a.r-b.r) as UPRATIO FROM "
            + " (SELECT ticker, SUM(up), SUM(down), SUM(up)/SUM(down) as r"
            + " FROM (SELECT ticker, IF(close>open, 1, 0) as up, IF(close>open, 0, 1) as down"
            + " FROM AdjustedPrices WHERE YEAR(day)=2016 GROUP BY ticker, day) a"
            + " GROUP BY ticker) a"
            + " LEFT OUTER JOIN "
            + " (SELECT ticker, SUM(up), SUM(down), SUM(up)/SUM(down) as r"
            + " FROM (SELECT ticker, DAY(day), IF(close>open, 1, 0) as up, IF(close>open, 0, 1) as down"
            + " FROM AdjustedPrices WHERE YEAR(day)<2016 GROUP BY ticker, day) a"
            + " GROUP BY ticker) b USING (ticker) "
            + " ) up"
            + " JOIN "
            + " (SELECT ticker, IF(b.stv IS NULL, 0, a.stv-b.stv) as STDVOLUME "
            + " FROM"
            + " (SELECT ticker, SUM(Volume), (SUM(Volume) - a.av) / a.st as stv"
            + " FROM (SELECT STDDEV(v) as st, AVG(v) as av "
            + " FROM (SELECT SUM(Volume) as v "
            + " FROM AdjustedPrices"
            + " WHERE YEAR(day)=2016 "
            + " GROUP BY ticker) x1	"
            + " ) a, AdjustedPrices"
            + " WHERE YEAR(day)=2016"
            + " GROUP BY ticker) a"
            + " LEFT OUTER JOIN "
            + " (SELECT ticker, SUM(Volume), (SUM(Volume) - a.av) / a.st as stv"
            + " FROM (SELECT STDDEV(v) as st, AVG(v) as av "
            + " FROM (SELECT SUM(Volume) as v "
            + " FROM AdjustedPrices"
            + " WHERE YEAR(day)<2016 "
            + " GROUP BY ticker) x1	"
            + " ) a, AdjustedPrices"
            + " WHERE YEAR(day)<2016"
            + " GROUP BY ticker) b "
            + " USING(ticker)) stdv "
            + " USING (ticker)) "
            + " JOIN "
            + " (SELECT Y.Ticker, (P2.close-P1.open) / P2.close as INC2016"
            + " FROM (SELECT Ticker, MIN(Day) AS Min, MAX(Day) AS Max"
            + " FROM AdjustedPrices P"
            + " WHERE YEAR(Day)=2016"
            + " GROUP BY Ticker) as Y, AdjustedPrices P1, AdjustedPrices P2"
            + " WHERE P1.Day = Y.Min"
            + " AND P2.Day = Y.Max"
            + " AND P1.Ticker = Y.Ticker"
            + " AND P2.Ticker = Y.Ticker "
            + " ) "
            + " inc USING (ticker) JOIN Securities Using (ticker)"
            + " ORDER BY score DESC"
            + " LIMIT 10) a"
            + " ;";

        return execQuery(conn, query, 2);
    }

    public static ArrayList<ArrayList<String>> GenQuery5_1(Connection conn) {
        String query =
            "SELECT Sector, SUM(up)/SUM(down)"
            + " FROM (SELECT Sector,"
            + " IF(Close > Open, 1, 0) as Up, IF(Close > Open, 0, 1) as Down"
            + " FROM AdjustedPrices AP, Securities S"
            + " WHERE AP.Ticker = S.Ticker"
            + " AND YEAR(Day) = 2016"
            + " AND Sector != 'Telecommunications Services') X"
            + " GROUP BY Sector"
            + " ORDER BY Sector"
            + " ;";


        return execQuery(conn, query, 2);
    }

    public static ArrayList<ArrayList<String>> GenQuery5_2(Connection conn) {
        String query =
            "SELECT Sector, (SUM(AP2.Close) - SUM(AP1.Open)) / SUM(AP1.Open)"
            + " FROM Securities S,"
            + " (SELECT Ticker, Open"
            + " FROM AdjustedPrices A"
            + " WHERE Day = (SELECT MIN(DAY)"
            + " FROM AdjustedPrices"
            + " WHERE YEAR(Day) = 2016)) AP1,"
            + " (SELECT Ticker, Close"
            + " FROM AdjustedPrices A"
            + " WHERE Day = (SELECT MAX(DAY)"
            + " FROM AdjustedPrices"
            + " WHERE YEAR(Day) = 2016)) AP2"
            + " WHERE AP1.Ticker = AP2.Ticker"
            + " AND S.Ticker = AP1.Ticker"
            + " GROUP BY Sector"
            + " ORDER BY Sector"
            + " ;";

        return execQuery(conn, query, 2);
    }

    public static ArrayList<ArrayList<String>> GenQuery5_3(Connection conn) {
        String query =
            "SELECT Sector, (SUM(Volume) - A.Avg) / A.StDev as Stv"
            + " FROM (SELECT STDDEV(V) as StDev, AVG(V) as Avg"
            + " FROM (SELECT SUM(Volume) as V"
            + " FROM AdjustedPrices AP, Securities S"
            + " WHERE YEAR(Day) = 2016"
            + " AND AP.Ticker = S.Ticker"
            + " GROUP BY Sector) X"
            + " ) A, AdjustedPrices AP, Securities S"
            + " WHERE YEAR(Day) = 2016"
            + " AND AP.Ticker = S.Ticker"
            + " GROUP BY Sector"
            + " ORDER BY Sector"
            + " ;";

        return execQuery(conn, query, 2);
    }

    public static ArrayList<String> sectors(Connection conn){
        String query = "SELECT DISTINCT Sector FROM Securities;";
        ArrayList<String> y = new ArrayList<>();
        Statement s;
                try {
                        s = conn.createStatement();
                ResultSet r = s.executeQuery(query);

                boolean check = r.next();
                        while (check) {
                                        y.add(r.getString(1));
                        check = r.next();
                    }

                } catch (SQLException e) {
                        e.printStackTrace();
                }
        return y;
    }


    public static ArrayList<ArrayList<String>> IndivQuery1(Connection conn, String ticker) {
        String query =
            "SELECT MIN(day), MAX(day) FROM Prices WHERE ticker=?;";
        return tickerQuery(conn, query, 2, ticker, 1);
    }

    public static ArrayList<ArrayList<String>> IndivQuery2(Connection conn, String ticker){
    	String query = 
    			"SELECT year, increase, v, c, a "
    			+ " FROM "
    			+ " (SELECT Y.year, P2.close-P1.open as increase, IF(P2.close>P1.close, 'Inc', 'Dec') as inc"
				+ " FROM (SELECT YEAR(day) as year, MIN(Day) AS Min, MAX(Day) AS Max"
				+ " FROM AdjustedPrices P"
				+ " WHERE Ticker=?"
				+ " GROUP BY year) as Y, AdjustedPrices P1, AdjustedPrices P2"
				+ " WHERE P1.Day = Y.Min"
				+ " AND P2.Day = Y.Max"
				+ " AND P1.Ticker=? and P2.Ticker=?) a"
				+ " JOIN "
				+ " (SELECT YEAR(Day) as year, SUM(volume) as v, AVG(close) as c, AVG(volume) as a"
				+ " FROM AdjustedPrices"
				+ "	WHERE Ticker=? "
				+ " GROUP BY year ) b "
				+ " USING (year);"; 
    	
        return tickerQuery(conn, query, 5, ticker, 4);

    	
    }
    
    public static ArrayList<ArrayList<String>> IndivQuery2_1(Connection conn, String ticker) {
        String query =
            "SELECT YEAR(Day) as year, SUM(volume), AVG(close), AVG(volume)"
            + " FROM AdjustedPrices"
            + " WHERE Ticker=?"
            + " GROUP BY year;";

        return tickerQuery(conn, query, 4, ticker, 1);
    }

    public static ArrayList<ArrayList<String>> IndivQuery2_2(Connection conn, String ticker) {
        String query =
            "SELECT Y.year, P2.close-P1.open as inc2016, IF(P2.close>P1.close, 'Inc', 'Dec') as inc"
            + " FROM (SELECT YEAR(day) as year, MIN(Day) AS Min, MAX(Day) AS Max"
            + " FROM AdjustedPrices P"
            + " WHERE Ticker=?"
            + " GROUP BY year) as Y, AdjustedPrices P1, AdjustedPrices P2"
            + " WHERE P1.Day = Y.Min"
            + " AND P2.Day = Y.Max"
            + " AND P1.Ticker=? and P2.Ticker=?;";

        return tickerQuery(conn, query, 3, ticker, 3);
    }

    public static ArrayList<ArrayList<String>> IndivQuery3_1(Connection conn, String ticker, String year) {
        String query =
            "SELECT MONTHNAME(Day) as m, AVG(close), MAX(High), MIN(Low), AVG(Volume)"
            + " FROM AdjustedPrices"
            + " WHERE Ticker=? and Year(day)="+year
            + " GROUP BY m"
            + " ORDER BY MONTH(DAY);";

        return tickerQuery(conn, query, 5, ticker, 1);
    }

    public static ArrayList<ArrayList<String>> IndivQuery3_2(Connection conn, String ticker) {
        String query =
            "SELECT MONTH(Day) as m, AVG(volume)"
            + " FROM AdjustedPrices"
            + " WHERE Ticker=?"
            + " GROUP BY m;";

        return tickerQuery(conn, query, 2, ticker, 1);
    }

    public static ArrayList<ArrayList<String>> IndivQuery4(Connection conn, String ticker) {
        String query =
            "SELECT year, MONTHNAME(STR_TO_DATE(month, '%m'))"
            + " FROM"
            + " (SELECT year, month, STDC+STDV+UPRATIO as score "
            + " "
            + " FROM "
            + " (SELECT YEAR(Day) as year, MONTH(Day) as month, "
            + " (SUM(Volume) - a.av) / a.st as STDV,  (AVG(Close) - b.av) / b.st as STDC"
            + " FROM AdjustedPrices, ("
            + " SELECT x1.y, STDDEV(v) as st, AVG(v) as av "
            + " FROM (SELECT YEAR(day) as y, MONTH(day) as m, SUM(Volume) "
            + " as v FROM AdjustedPrices WHERE ticker=? GROUP BY y, m) x1"
            + " GROUP BY x1.y"
            + " "
            + " ) a, "
            + " (SELECT x2.y, STDDEV(c) as st, AVG(c) as av "
            + " FROM (SELECT YEAR(day) as y, MONTH(day) as m, AVG(close) "
            + " as c FROM AdjustedPrices WHERE ticker=? GROUP BY y, m) x2"
            + " GROUP BY x2.y"
            + " ) b"
            + " WHERE Ticker=? and a.y=YEAR(Day) and b.y=YEAR(day)"
            + " GROUP BY year, month) a"
            + " "
            + " JOIN "
            + " "
            + " (SELECT year, month, SUM(up), SUM(down), SUM(up)/SUM(down) as UPRATIO"
            + " FROM (SELECT YEAR(day) as year, MONTH(day) as month, DAY(day), IF(close>open, 1, 0) as up, IF(close>open, 0, 1) as down"
            + " FROM AdjustedPrices WHERE ticker=? GROUP BY year, month, DAY(day)) a"
            + " GROUP BY year, month) b"
            + " "
            + " USING (year, month) "
            + " "
            + " "
            + " "
            + " GROUP BY year, month"
            + " ORDER BY year, score DESC) t"
            + " WHERE score >= ALL"
            + " (SELECT STDC+STDV+UPRATIO as score "
            + " "
            + " FROM "
            + " (SELECT YEAR(Day) as year, MONTH(Day) as month, "
            + " (SUM(Volume) - a.av) / a.st as STDV,  (AVG(Close) - b.av) / b.st as STDC"
            + " FROM AdjustedPrices, ("
            + " SELECT x1.y, STDDEV(v) as st, AVG(v) as av "
            + " FROM (SELECT YEAR(day) as y, MONTH(day) as m, SUM(Volume) "
            + " as v FROM AdjustedPrices WHERE ticker=? GROUP BY y, m) x1"
            + " GROUP BY x1.y"
            + " "
            + " ) a, "
            + " (SELECT x2.y, STDDEV(c) as st, AVG(c) as av "
            + " FROM (SELECT YEAR(day) as y, MONTH(day) as m, AVG(close) "
            + " as c FROM AdjustedPrices WHERE ticker=? GROUP BY y, m) x2"
            + " GROUP BY x2.y"
            + " ) b"
            + " WHERE Ticker=? and a.y=YEAR(Day) and b.y=YEAR(day)"
            + " GROUP BY year, month) a"
            + " "
            + " JOIN "
            + " "
            + " (SELECT year, month, SUM(up), SUM(down), SUM(up)/SUM(down) as UPRATIO"
            + " FROM (SELECT YEAR(day) as year, MONTH(day) as month, DAY(day), IF(close>open, 1, 0) as up, IF(close>open, 0, 1) as down"
            + " FROM AdjustedPrices WHERE ticker=? GROUP BY year, month, DAY(day)) a"
            + " GROUP BY year, month) b"
            + " "
            + " USING (year, month) "
            + " "
            + " WHERE t.year=year"
            + " "
            + " GROUP BY year, month"
            + " ORDER BY year, score DESC)"
            + " "
            + " ORDER BY year "
            + " ;";

        return tickerQuery(conn, query, 2, ticker, 8);
    }

    public static String IndivQuery5_1(Connection conn, String ticker, String date) {
        String query =
            "SELECT CASE "
            + "	WHEN h3 > h1 and upratio > 1 and dif1 < dif2"
            + " THEN 'Buy' "
            + " WHEN h1 > h3 and upratio < 1 and dif3>0"
            + " THEN 'Sell'"
            + " ELSE 'Hold' "
            + " END as Decision"
            + " FROM "
            + " (SELECT SUM(close-open) as dif1, MAX(high) as h1 "
            + " FROM AdjustedPrices"
            + " WHERE ticker=? "
            + " and day>=DATE_SUB(?, INTERVAL 30 DAY) and day<?) a"
            + " , "
            + " (SELECT SUM(close-open) as dif2, MAX(high) as h2 "
            + " FROM AdjustedPrices"
            + " WHERE ticker=? "
            + " and day>=DATE_SUB(?, INTERVAL 60 DAY) and day<DATE_SUB(?, INTERVAL 30 DAY)) b"
            + " , "
            + " (SELECT SUM(close-open) as dif3, MAX(high) as h3 "
            + " FROM AdjustedPrices"
            + " WHERE ticker=? "
            + " and day>=DATE_SUB(?, INTERVAL 365 DAY) and day<?) c"
            + " , "
            + " (SELECT SUM(up) as upDays, SUM(down) as downDays, SUM(up)/SUM(down) as UPRATIO"
            + " FROM (SELECT day, IF(close>open, 1, 0) as up, IF(close>open, 0, 1) as down"
            + " FROM AdjustedPrices "
            + "  WHERE ticker=? and day>=DATE_SUB(?, INTERVAL 30 DAY) and day<? "
            + " GROUP BY day) a ) d ; ";
        
	        PreparedStatement s;
			try {
				s = conn.prepareStatement(query);
		    
				s.setString(1, ticker);
				s.setString(2, date);
				s.setString(3, date);
				
				s.setString(4, ticker);
				s.setString(5, date);
				s.setString(6, date);
				
				s.setString(7, ticker);
				s.setString(8, date);
				s.setString(9, date);
				
				s.setString(10, ticker);
				s.setString(11, date);
				s.setString(12, date);

	
				ResultSet r = s.executeQuery();
		    	
		        boolean check = r.next();
				while (check) {
						return r.getString(1);
		            }
	
			} catch (SQLException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
	    	
	    	
	    	return "FUCK"; 
        
        
        
    }
            
    
    public static String IndivQuery6_1(Connection conn, String ticker, String date) {
        String query =
            "SELECT CASE "
            + "	WHEN h3 > h1 and upratio > 1 and dif3>0"
            + " THEN 'Buy' "
            + " WHEN upratio<1 and dif3<0"
            + " THEN 'Sell'"
            + " ELSE 'Hold' "
            + " END as Decision"
            + " FROM "
            + " (SELECT SUM(close-open) as dif1, MAX(high) as h1 "
            + " FROM AdjustedPrices"
            + " WHERE ticker=? "
            + " and day<=DATE_ADD(?, INTERVAL 90 DAY) and day>?) a"
            + " , "
            + " (SELECT SUM(close-open) as dif2, MAX(high) as h2 "
            + " FROM AdjustedPrices"
            + " WHERE ticker=? "
            + " and day<=DATE_ADD(?, INTERVAL 60 DAY) and day>DATE_ADD(?, INTERVAL 30 DAY)) b"
            + " , "
            + " (SELECT SUM(close-open) as dif3, MAX(high) as h3 "
            + " FROM AdjustedPrices"
            + " WHERE ticker=? "
            + " and day<=DATE_ADD(?, INTERVAL 180 DAY) and day>?) c"
            + " , "
            + " (SELECT SUM(up) as upDays, SUM(down) as downDays, SUM(up)/SUM(down) as UPRATIO"
            + " FROM (SELECT day, IF(close>open, 1, 0) as up, IF(close>open, 0, 1) as down"
            + " FROM AdjustedPrices "
            + "  WHERE ticker=? and day<=DATE_ADD(?, INTERVAL 90 DAY) and day<? "
            + " GROUP BY day) a ) d ; ";
        
	        PreparedStatement s;
			try {
				s = conn.prepareStatement(query);
		    
				s.setString(1, ticker);
				s.setString(2, date);
				s.setString(3, date);
				
				s.setString(4, ticker);
				s.setString(5, date);
				s.setString(6, date);
				
				s.setString(7, ticker);
				s.setString(8, date);
				s.setString(9, date);

				s.setString(10, ticker);
				s.setString(11, date);
				s.setString(12, date);
	
				ResultSet r = s.executeQuery();
		    	
		        boolean check = r.next();
				while (check) {
						return r.getString(1);
		            }
	
			} catch (SQLException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
	    	
	    	
	    	return "FUCK"; 
        
        
        
    }
    public static ArrayList<ArrayList<String>> IndivQuery6_2(Connection conn, String ticker, String date) {
        String query =
            "SELECT SUM(close-open)"
            + " FROM AdjustedPrices"
            + " WHERE ticker=?"
            + " and day<=DATE_ADD(?, INTERVAL 180 DAY) and day>DATE_ADD(?, INTERVAL 90 DAY)"
            + " ;";


        return dateQuery(conn, query, 1, ticker, date);
    }

    public static ArrayList<ArrayList<String>> IndivQuery6_3(Connection conn, String ticker, String date) {
        String query =
            "SELECT SUM(close-open)"
            + " FROM AdjustedPrices"
            + " WHERE ticker=?"
            + " and day<=DATE_ADD(?, INTERVAL 180 DAY) and day>?"
            + " ;";

        return dateQuery(conn, query, 1, ticker, date);
    }

    public static ArrayList<ArrayList<String>> IndivQuery6_4(Connection conn, String ticker, String date) {
        String query =
            "SELECT MAX(high)"
            + " FROM AdjustedPrices"
            + " WHERE ticker=?"
            + " and day<=DATE_ADD(?, INTERVAL 90 DAY) and day>?"
            + " ;";

        return dateQuery(conn, query, 1, ticker, date);
    }

    public static ArrayList<ArrayList<String>> IndivQuery6_5(Connection conn, String ticker, String date) {
        String query =
            "SELECT SUM(up) as upDays, SUM(down) as downDays, SUM(up)/SUM(down) as UPRATIO"
            + " FROM (SELECT day, IF(close>open, 1, 0) as up, IF(close>open, 0, 1) as down"
            + " FROM AdjustedPrices"
            + " WHERE ticker=? and day<=DATE_ADD(?, INTERVAL 90 DAY) and day>?"
            + " GROUP BY day) a"
            + " ;";

        return dateQuery(conn, query, 3, ticker, date);
    }
    public static ArrayList<ArrayList<String>> IndivQuery7_1(Connection conn, String ticker, String year) {
        String query =
        		"SELECT P1.ticker, MONTHNAME(STR_TO_DATE(month, '%m')), P2.close-P1.open as inc2016, IF(P2.close>P1.close, 'Inc', 'Dec') as inc, vol"
    		+ " FROM (SELECT MONTH(day) as month, MIN(Day) AS Min, MAX(Day) AS Max, SUM(Volume) as vol"
    		+ " FROM AdjustedPrices P"
    		+ " WHERE Ticker=? and YEAR(day)="+year
    		+ " GROUP BY month) as Y, AdjustedPrices P1, AdjustedPrices P2"
    		+ " WHERE P1.Day = Y.Min"
    		+ " AND P2.Day = Y.Max"
    		+ " AND P1.Ticker=? and P2.Ticker=?"
    		+ " ORDER BY month;";
        
        return tickerQuery(conn, query, 5, ticker, 3);
    }


    public static ArrayList<ArrayList<String>> IndivQuery7_2(Connection conn, String ticker, String year) {
        String query =
            "SELECT ticker, MONTHNAME(STR_TO_DATE(month, '%m')), inc2016, inc, vol FROM"
            + " (SELECT Y.ticker, month, P2.close-P1.open as inc2016, IF(P2.close>P1.close, 'Inc', 'Dec') as inc, vol"
            + " FROM (SELECT ticker, MONTH(day) as month, MIN(Day) AS Min, MAX(Day) AS Max, SUM(Volume) as vol "
            + " FROM AdjustedPrices P"
            + " WHERE YEAR(day)="+year
            + " GROUP BY ticker, month) as Y, AdjustedPrices P1, AdjustedPrices P2"
            + " WHERE P1.Day = Y.Min"
            + " AND P2.Day = Y.Max"
            + " AND P1.Ticker=P2.Ticker and P1.ticker=Y.ticker) a"
            + " JOIN "
            + " (SELECT P1.ticker "
            + " FROM (SELECT MONTH(day) as month, MIN(Day) AS Min, MAX(Day) AS Max "
            + " FROM AdjustedPrices P"
            + "	WHERE  YEAR(day)="+year
            + "	GROUP BY month) as Y, AdjustedPrices P1, AdjustedPrices P2"
            + "	WHERE P1.Day = Y.Min"
            + "	AND P2.Day = Y.Max AND P1.Ticker=P2.Ticker"
            + "	ORDER BY (P2.close-P1.open)/P1.open"
            + "	LIMIT 5 ) b"
            + "	USING(ticker)"
            + "	GROUP BY ticker, month"
            + " ORDER BY ticker, month;";

        return tickerQuery(conn, query, 5, ticker, 0);
    }

    public static ArrayList<ArrayList<String>> IndivQuery8(Connection conn, String ticker, String year) {
        String query =
            "SELECT MONTHNAME(STR_TO_DATE(month, '%m')), P2.close-P1.open as inc2016, IF(P2.close>P1.close, 'Inc', 'Dec') as inc, vol"
            + " FROM (SELECT MONTH(day) as month, MIN(Day) AS Min, MAX(Day) AS Max, SUM(Volume) as vol"
            + " FROM AdjustedPrices P"
            + " WHERE Ticker=? and YEAR(day)=" + year
            + " GROUP BY month) as Y, AdjustedPrices P1, AdjustedPrices P2"
            + " WHERE P1.Day = Y.Min"
            + " AND P2.Day = Y.Max"
            + " AND P1.Ticker=? and P2.Ticker=?"
            + " ORDER BY month;";


        return tickerQuery(conn, query, 4, ticker, 3);
    }
    
    public static String IndivQuery8_1(Connection conn, String ticker1, String ticker2, String year){
    	String query = 
    			"SELECT IF(IF(SUM(a.inc2016)/SUM(b.inc2016)>1, 1, 0) + IF(SUM(a.inc)/SUM(b.inc)>1,1,0) + IF(SUM(a.vol)/SUM(b.vol)>1, 1, 0) > 2, a.ticker, b.ticker)"
    			+ " FROM "
    			+ " (SELECT P1.ticker, month, (P2.close-P1.open)/P1.open as inc2016, IF(P2.close>P1.close, 1, 0) as inc, vol"
    			+ " FROM (SELECT MONTH(day) as month, MIN(Day) AS Min, MAX(Day) AS Max, SUM(Volume) as vol "
    			+ " FROM AdjustedPrices P "
    			+ " WHERE Ticker=? and YEAR(day)=?"
    			+ " GROUP BY month) as Y, AdjustedPrices P1, AdjustedPrices P2"
    			+ " WHERE P1.Day = Y.Min"
    			+ " AND P2.Day = Y.Max"
    			+ " AND P1.Ticker=? and P2.Ticker=?) a,"
    			+ " (SELECT P1.ticker, month, (P2.close-P1.open)/P1.open as inc2016, IF(P2.close>P1.close, 1, 0) as inc, vol"
    			+ " FROM (SELECT MONTH(day) as month, MIN(Day) AS Min, MAX(Day) AS Max, SUM(Volume) as vol"
    			+ " FROM AdjustedPrices P"
    			+ " WHERE Ticker=? and YEAR(day)=?" 
    			+ " GROUP BY month) as Y, AdjustedPrices P1, AdjustedPrices P2"
    			+ " WHERE P1.Day = Y.Min"
    			+ " AND P2.Day = Y.Max"
    			+ " AND P1.Ticker=? and P2.Ticker=?) b;"; 
    	
    	PreparedStatement s;
		try {
			s = conn.prepareStatement(query);
	    
			s.setString(1, ticker1);
			s.setString(2, year);
			s.setString(3, ticker1);
			s.setString(4, ticker1);
			
			s.setString(5, ticker2);
			s.setString(6, year);
			s.setString(7, ticker2);
			s.setString(8, ticker2);

			ResultSet r = s.executeQuery();
	    	
	        boolean check = r.next();
			while (check) {
					return r.getString(1);
	            }

		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
    	
    	
    	return "FUCK"; 
    	
    }

}
