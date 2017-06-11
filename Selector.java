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

    public static ArrayList<ArrayList<String>> execQuery(PreparedStatement statement, int columns) {
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

    public static String GenQuery1_1(Connection conn) {
        String query = 
            "SELECT SUM(Volume)"
            + " FROM Prices"
            + " WHERE YEAR(Day) < 2016;";

        return execQuery(conn, query, 1).get(0).get(0);
    }

    public static String GenQuery1_2(Connection conn) {
        String query =
            "SELECT SUM(Volume)"
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

    public static ArrayList<ArrayList<String>> GenQuery3(Connection conn) {
        String query =
            "SELECT Y.Year, Y.Ticker, S.Name"
            + " FROM (SELECT Ticker, YEAR(Day) AS Year, MIN(Day) AS Min, MAX(Day) AS Max"
            + " FROM Prices P"
            + " GROUP BY Ticker, YEAR(Day)) as Y, Prices P1, Prices P2, Securities S"
            + " WHERE P1.Day = Y.Min"
            + " AND P2.Day = Y.Max"
            + " AND P1.Ticker = Y.Ticker"
            + " AND P2.Ticker = Y.Ticker"
            + " AND S.Ticker = Y.Ticker"
            + " AND 5 >="
            + " (SELECT COUNT(*)"
            + " FROM (SELECT Ticker, YEAR(Day) AS Year,"
            + " MIN(Day) AS Min, MAX(Day) AS Max"
            + " FROM Prices P"
            + " GROUP BY Ticker, YEAR(Day)) as Y1, Prices P3, Prices P4"
            + " WHERE P3.Day = Y1.Min"
            + " AND P4.Day = Y1.Max"
            + " AND P3.Ticker = Y1.Ticker"
            + " AND P4.Ticker = Y1.Ticker"
            + " AND Y1.Year = Y.Year"
            + " AND (P2.Close - P1.Open) <= (P4.Close - P3.Open))"
            + " ORDER BY Y.Year, Y.Ticker;";

        return execQuery(conn, query, 3);
    }

    public static ArrayList<ArrayList<String>> GenQuery4(Connection conn) {
        String query = 
            "SELECT ticker, .5*UPRATIO + .1*STDVOLUME + 1.5*INC2016 as score"
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
            + " inc USING (ticker)"
            + " ORDER BY score DESC"
            + " LIMIT 10"
            + " ;";

        return execQuery(conn, query, 2);
    }

    public static ArrayList<ArrayList<String>> GenQuery5_1(Connection conn) {
        String query =
            "SELECT Sector, SUM(up), SUM(down), SUM(up)/SUM(down)"
            + " FROM (SELECT Sector,"
            + " IF(Close > Open, 1, 0) as Up, IF(Close > Open, 0, 1) as Down"
            + " FROM AdjustedPrices AP, Securities S"
            + " WHERE AP.Ticker = S.Ticker"
            + " AND YEAR(Day) = 2016"
            + " AND Sector != 'Telecommunications Services') X"
            + " GROUP BY Sector"
            + " ;";


        return execQuery(conn, query, 4);
    }

    public static ArrayList<ArrayList<String>> GenQuery5_2(Connection conn) {
        String query =
            "SELECT Sector, SUM(AP2.Close) - SUM(AP1.Open)"
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
            + " ;";

        return execQuery(conn, query, 2);
    }

    public static ArrayList<ArrayList<String>> GenQuery5_3(Connection conn) {
        String query =
            "SELECT Sector, SUM(Volume), (SUM(Volume) - A.Avg) / A.StDev as Stv"
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
            + " ;";

        return execQuery(conn, query, 3);
    }
}
