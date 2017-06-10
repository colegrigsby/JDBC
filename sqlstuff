-- GENERAL 


-- Q4 

-- up ratio 
SELECT ticker, a.r, b.r, a.r-b.r FROM 

(SELECT ticker, SUM(up), SUM(down), SUM(up)/SUM(down) as r
FROM (SELECT ticker, IF(close>open, 1, 0) as up, IF(close>open, 0, 1) as down
	FROM AdjustedPrices WHERE YEAR(day)=2016 GROUP BY ticker, day) a
GROUP BY ticker) a
LEFT OUTER JOIN 
(SELECT ticker, SUM(up), SUM(down), SUM(up)/SUM(down) as r
FROM (SELECT ticker, DAY(day), IF(close>open, 1, 0) as up, IF(close>open, 0, 1) as down
	FROM AdjustedPrices WHERE YEAR(day)<2016 GROUP BY ticker, day) a
GROUP BY ticker) b USING (ticker) 
;

-- standardized volume 2016 - previous 

SELECT ticker, a.stv, b.stv, a.stv-b.stv
FROM
(SELECT ticker, SUM(Volume), (SUM(Volume) - a.av) / a.st as stv
FROM (SELECT STDDEV(v) as st, AVG(v) as av 
        FROM (SELECT SUM(Volume) as v 
            FROM AdjustedPrices
            WHERE YEAR(day)=2016 
            GROUP BY ticker) x1	
      ) a, AdjustedPrices
WHERE YEAR(day)=2016
GROUP BY ticker) a

LEFT OUTER JOIN 
	
(SELECT ticker, SUM(Volume), (SUM(Volume) - a.av) / a.st as stv
FROM (SELECT STDDEV(v) as st, AVG(v) as av 
        FROM (SELECT SUM(Volume) as v 
            FROM AdjustedPrices
            WHERE YEAR(day)<2016 
            GROUP BY ticker) x1	
      ) a, AdjustedPrices
WHERE YEAR(day)<2016
GROUP BY ticker) b USING(ticker) 
;


-- semi-standardized price change over the year 2016

SELECT Y.Ticker, (P2.close-P1.open) / P2.close as inc2016
    FROM (SELECT Ticker, MIN(Day) AS Min, MAX(Day) AS Max
        FROM AdjustedPrices P
        WHERE YEAR(Day)=2016
        GROUP BY Ticker) as Y, AdjustedPrices P1, AdjustedPrices P2
    WHERE P1.Day = Y.Min
        AND P2.Day = Y.Max
        AND P1.Ticker = Y.Ticker
        AND P2.Ticker = Y.Ticker 



-- price increasing differences between the two years 
SELECT Ticker, inc2016, inc2015, inc2016-inc2015 FROM 

(SELECT Y.Ticker, P2.close-P1.open as inc2016
FROM (SELECT Ticker, MIN(Day) AS Min, MAX(Day) AS Max
    FROM AdjustedPrices P
    WHERE YEAR(Day)=2016
    GROUP BY Ticker) as Y, AdjustedPrices P1, AdjustedPrices P2
WHERE P1.Day = Y.Min
    AND P2.Day = Y.Max
    AND P1.Ticker = Y.Ticker
    AND P2.Ticker = Y.Ticker) a
 
 
   
LEFT OUTER JOIN 

(SELECT Y.Ticker, P2.close-P1.open as inc2015
FROM (SELECT Ticker, MIN(Day) AS Min, MAX(Day) AS Max
    FROM AdjustedPrices P
    WHERE YEAR(Day)=2015
    GROUP BY Ticker) as Y, AdjustedPrices P1, AdjustedPrices P2
WHERE P1.Day = Y.Min
    AND P2.Day = Y.Max
    AND P1.Ticker = Y.Ticker 
    AND P2.Ticker = Y.Ticker) b USING(Ticker) 
    
;


-- add ratios and change in price to get a score 


-- IND 




-- Q1 

SELECT MIN(day), MAX(day) FROM Prices WHERE ticker='GOOG';

-- Q2 
SELECT YEAR(Day) as year, SUM(volume), AVG(close), AVG(volume) 
FROM AdjustedPrices 
WHERE Ticker='GOOG' 

GROUP BY year ;


SELECT Y.year, P2.close-P1.open as inc2016, IF(P2.close>P1.close, "Inc", "dec") as inc
FROM (SELECT YEAR(day) as year, MIN(Day) AS Min, MAX(Day) AS Max
    FROM AdjustedPrices P
    WHERE Ticker='GOOG'
    GROUP BY year) as Y, AdjustedPrices P1, AdjustedPrices P2
	WHERE P1.Day = Y.Min
    AND P2.Day = Y.Max
	AND P1.Ticker='GOOG' and P2.Ticker='GOOG'
  
    
    
-- TODO get the "increase/decrease in prices year-over-year" 

-- Q3 

-- TODO last full YEAR for which data exists 

SELECT AVG(close), IF(MIN(close)<MIN(open), MIN(close), MIN(open)),
	IF(MAX(open)>MAX(close), MAX(open), MAX(close))
FROM AdjustedPrices 
WHERE Ticker='GOOG' and Year(day)=2016 
; 


SELECT MONTH(Day) as m, AVG(volume) 
FROM AdjustedPrices 
WHERE Ticker='GOOG' 
GROUP BY m; 


-- Q4 

-- use values and determine 

SELECT YEAR(Day) as year, MONTH(Day) as month, SUM(Volume), 
	(SUM(Volume) - a.av) / a.st , AVG(close), (AVG(Close) - b.av) / b.st
FROM AdjustedPrices, (
SELECT x1.y, STDDEV(v) as st, AVG(v) as av 
	FROM (SELECT YEAR(day) as y, MONTH(day) as m, SUM(Volume) 
	as v FROM AdjustedPrices WHERE ticker='GOOG' GROUP BY y, m) x1
	GROUP BY x1.y
	
) a, 
(SELECT x2.y, STDDEV(c) as st, AVG(c) as av 
	FROM (SELECT YEAR(day) as y, MONTH(day) as m, AVG(close) 
	as c FROM AdjustedPrices WHERE ticker='GOOG' GROUP BY y, m) x2
	GROUP BY x2.y
) b
WHERE Ticker='GOOG' and a.y=YEAR(Day) and b.y=YEAR(day)
GROUP BY year, month;
	
	

SELECT y, m, SUM(up), SUM(down), SUM(up)/SUM(down)
FROM (SELECT YEAR(day) as y, MONTH(day) as m, DAY(day), IF(close>open, 1, 0) as up, IF(close>open, 0, 1) as down
	FROM AdjustedPrices WHERE ticker='GOOG' GROUP BY y, m, DAY(day)) a
GROUP BY y, m
;

-- from the above two queries, create a score by summing the standardized values and ratios 

