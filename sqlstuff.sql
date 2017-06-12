-- GENERAL 


-- Q4 

-- up ratio 


SELECT Ticker, Name FROM 
(SELECT ticker, name, .5*UPRATIO + .1*STDVOLUME + 1.5*INC2016 as score

FROM 
    ((SELECT ticker, IF((b.r IS NULL ), a.r-1, a.r-b.r) as UPRATIO FROM 
        (SELECT ticker, SUM(up), SUM(down), SUM(up)/SUM(down) as r
        FROM (SELECT ticker, IF(close>open, 1, 0) as up, IF(close>open, 0, 1) as down
            FROM AdjustedPrices WHERE YEAR(day)=2016 GROUP BY ticker, day) a
        GROUP BY ticker) a
        LEFT OUTER JOIN 
        (SELECT ticker, SUM(up), SUM(down), SUM(up)/SUM(down) as r
        FROM (SELECT ticker, DAY(day), IF(close>open, 1, 0) as up, IF(close>open, 0, 1) as down
            FROM AdjustedPrices WHERE YEAR(day)<2016 GROUP BY ticker, day) a
        GROUP BY ticker) b USING (ticker) 
    ) up

JOIN 

    (SELECT ticker, IF(b.stv IS NULL, 0, a.stv-b.stv) as STDVOLUME 
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
            GROUP BY ticker) b 
        USING(ticker)) stdv 

USING (ticker)) 

JOIN 

        (SELECT Y.Ticker, (P2.close-P1.open) / P2.close as INC2016
            FROM (SELECT Ticker, MIN(Day) AS Min, MAX(Day) AS Max
                FROM AdjustedPrices P
                WHERE YEAR(Day)=2016
                GROUP BY Ticker) as Y, AdjustedPrices P1, AdjustedPrices P2
            WHERE P1.Day = Y.Min
                AND P2.Day = Y.Max
                AND P1.Ticker = Y.Ticker
                AND P2.Ticker = Y.Ticker 
        ) 

    inc USING (ticker)
JOIN 
    Securities USING(ticker) 
ORDER BY score DESC
LIMIT 10) a
;


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


SELECT year, increase, v, c, a 
FROM 
(SELECT Y.year, P2.close-P1.open as increase, IF(P2.close>P1.close, "Inc", "dec") as inc
FROM (SELECT YEAR(day) as year, MIN(Day) AS Min, MAX(Day) AS Max
    FROM AdjustedPrices P
    WHERE Ticker='GOOG'
    GROUP BY year) as Y, AdjustedPrices P1, AdjustedPrices P2
	WHERE P1.Day = Y.Min
    AND P2.Day = Y.Max
	AND P1.Ticker='GOOG' and P2.Ticker='GOOG') a
JOIN 
    (SELECT YEAR(Day) as year, SUM(volume) as v, AVG(close) as c, AVG(volume) as a
    FROM AdjustedPrices 
    WHERE Ticker='GOOG' 

    GROUP BY year ) b
USING (year);
  
    
    
-- TODO get the "increase/decrease in prices year-over-year" 

-- Q3 

-- TODO last full YEAR for which data exists 

SELECT AVG(close), IF(MIN(close)<MIN(open), MIN(close), MIN(open)),
	IF(MAX(open)>MAX(close), MAX(open), MAX(close))
FROM AdjustedPrices 
WHERE Ticker='GOOG' and Year(day)=2016 
; 

SELECT MONTH(Day) as m, AVG(close), MAX(High), MIN(Low), AVG(Volume)
FROM AdjustedPrices
WHERE Ticker='GOOG' and Year(day)=2016
GROUP BY m;


SELECT MONTH(Day) as m, AVG(volume) 
FROM AdjustedPrices 
WHERE Ticker='GOOG' 
GROUP BY m; 


-- Q4 

-- use values and determine 

SELECT year, MONTHNAME(STR_TO_DATE(month, '%m')), month
FROM
    (SELECT year, month, STDC+STDV+UPRATIO as score 

    FROM 
        (SELECT YEAR(Day) as year, MONTH(Day) as month, 
            (SUM(Volume) - a.av) / a.st as STDV,  (AVG(Close) - b.av) / b.st as STDC
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
        GROUP BY year, month) a

    JOIN 

        (SELECT year, month, SUM(up), SUM(down), SUM(up)/SUM(down) as UPRATIO
        FROM (SELECT YEAR(day) as year, MONTH(day) as month, DAY(day), IF(close>open, 1, 0) as up, IF(close>open, 0, 1) as down
            FROM AdjustedPrices WHERE ticker='GOOG' GROUP BY year, month, DAY(day)) a
        GROUP BY year, month) b

    USING (year, month) 



    GROUP BY year, month
    ORDER BY year, score DESC) t
WHERE score >= ALL
        (SELECT STDC+STDV+UPRATIO as score 

    FROM 
        (SELECT YEAR(Day) as year, MONTH(Day) as month, 
            (SUM(Volume) - a.av) / a.st as STDV,  (AVG(Close) - b.av) / b.st as STDC
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
        GROUP BY year, month) a

    JOIN 

        (SELECT year, month, SUM(up), SUM(down), SUM(up)/SUM(down) as UPRATIO
        FROM (SELECT YEAR(day) as year, MONTH(day) as month, DAY(day), IF(close>open, 1, 0) as up, IF(close>open, 0, 1) as down
            FROM AdjustedPrices WHERE ticker='GOOG' GROUP BY year, month, DAY(day)) a
        GROUP BY year, month) b

    USING (year, month) 

    WHERE t.year=year

    GROUP BY year, month
    ORDER BY year, score DESC)

ORDER BY year 
;


-- Q5 


-- up ratio 

-- if up ratio is steady up (>1.2) and relatively low price, buy 
    -- positive trend in past month increases 
-- else if medium price, hold  
    -- also hold if med-high and >1.4 
    -- also had + 2month ago increase and close to + last month increase 
-- else if ~1 and high price, then sell 
    -- high price increase in past 2month but not month 

SELECT SUM(up) as upDays, SUM(down) as downDays, SUM(up)/SUM(down) as UPRATIO
        FROM (SELECT day, IF(close>open, 1, 0) as up, IF(close>open, 0, 1) as down
            FROM AdjustedPrices 
            WHERE ticker='GOOG' and day>=DATE_SUB('2015-01-01', INTERVAL 30 DAY) and day<'2015-01-01'
            GROUP BY day) a
;

-- price increase 

-- last month increase 
SELECT SUM(close-open)
FROM AdjustedPrices
WHERE ticker='GOOG' 
    and day>=DATE_SUB('2015-01-01', INTERVAL 30 DAY) and day<'2015-01-01'
;

-- 2 month ago increase 
SELECT SUM(close-open)
FROM AdjustedPrices
WHERE ticker='GOOG' 
    and day>=DATE_SUB('2015-01-01', INTERVAL 60 DAY) and day<DATE_SUB('2015-01-01', INTERVAL 30 DAY)
;


-- last month high 
SELECT MAX(high)
FROM AdjustedPrices
WHERE ticker='GOOG' 
    and day>=DATE_SUB('2015-01-01', INTERVAL 30 DAY) and day<'2015-01-01'
;

-- 2 months ago high 
SELECT MAX(high) 
FROM AdjustedPrices
WHERE ticker='GOOG' 
    and day>=DATE_SUB('2015-01-01', INTERVAL 60 DAY) and day<DATE_SUB('2015-01-01', INTERVAL 30 DAY)
;

-- past year high 
SELECT MAX(high)
FROM AdjustedPrices
WHERE ticker='GOOG' 
    and day>=DATE_SUB('2015-01-01', INTERVAL 365 DAY) and day<'2015-01-01'
;


-- Q6 


-- increase for the next months 
SELECT SUM(close-open)
FROM AdjustedPrices
WHERE ticker='GOOG' 
    and day<=DATE_ADD('2015-01-01', INTERVAL 90 DAY) and day>'2015-01-01'
;

-- 90-180 days  increase 
SELECT SUM(close-open)
FROM AdjustedPrices
WHERE ticker='GOOG' 
    and day<=DATE_ADD('2015-01-01', INTERVAL 180 DAY) and day>DATE_ADD('2015-01-01', INTERVAL 90 DAY)
; 


SELECT SUM(close-open)
FROM AdjustedPrices
WHERE ticker='GOOG' 
    and day<=DATE_ADD('2015-01-01', INTERVAL 180 DAY) and day>'2015-01-01'
; 

-- maximums 

SELECT MAX(high)
FROM AdjustedPrices
WHERE ticker='GOOG' 
    and day<=DATE_ADD('2015-01-01', INTERVAL 90 DAY) and day>'2015-01-01'
;


-- volatility 

SELECT SUM(up) as upDays, SUM(down) as downDays, SUM(up)/SUM(down) as UPRATIO
        FROM (SELECT day, IF(close>open, 1, 0) as up, IF(close>open, 0, 1) as down
            FROM AdjustedPrices 
            WHERE ticker='GOOG' and day<=DATE_ADD('2015-01-01', INTERVAL 90 DAY) and day>'2015-01-01'
            GROUP BY day) a
;


-- Q7 

-- todo last full year of data if nec. 

-- our stock 
SELECT * FROM 
    (SELECT P1.ticker, month, P2.close-P1.open as inc2016, IF(P2.close>P1.close, "Inc", "dec") as inc, vol
        FROM (SELECT MONTH(day) as month, MIN(Day) AS Min, MAX(Day) AS Max, SUM(Volume) as vol
            FROM AdjustedPrices P
            WHERE Ticker='GOOG' and YEAR(day)=2016
            GROUP BY month) as Y, AdjustedPrices P1, AdjustedPrices P2
        WHERE P1.Day = Y.Min
        AND P2.Day = Y.Max
        AND P1.Ticker='GOOG' and P2.Ticker='GOOG') a
	JOIN 
    (SELECT ticker, month, inc2016, vol FROM 
        (SELECT Y.ticker, month, P2.close-P1.open as inc2016, IF(P2.close>P1.close, "Inc", "dec") as inc, vol
            FROM (SELECT ticker, MONTH(day) as month, MIN(Day) AS Min, MAX(Day) AS Max, SUM(Volume) as vol
                FROM AdjustedPrices P
                WHERE YEAR(day)=2016
                GROUP BY ticker, month) as Y, AdjustedPrices P1, AdjustedPrices P2
            WHERE P1.Day = Y.Min
            AND P2.Day = Y.Max
            AND P1.Ticker=P2.Ticker and P1.ticker=Y.ticker) a
    JOIN 
        (SELECT P1.ticker 
            FROM (SELECT MONTH(day) as month, MIN(Day) AS Min, MAX(Day) AS Max
                FROM AdjustedPrices P
                WHERE  YEAR(day)=2016
                GROUP BY month) as Y, AdjustedPrices P1, AdjustedPrices P2
            WHERE P1.Day = Y.Min
            AND P2.Day = Y.Max AND P1.Ticker=P2.Ticker
            ORDER BY (P2.close-P1.open)/P1.open
            LIMIT 5 ) b
    USING(ticker)
    GROUP BY ticker, month) b
USING (month) 
;



-- Q8 

-- better trading stock compared to : 

SELECT P1.ticker, month, P2.close-P1.open as inc2016, IF(P2.close>P1.close, "Inc", "dec") as inc, vol
        FROM (SELECT MONTH(day) as month, MIN(Day) AS Min, MAX(Day) AS Max, SUM(Volume) as vol
            FROM AdjustedPrices P
            WHERE Ticker='GOOG' and YEAR(day)=2016
            GROUP BY month) as Y, AdjustedPrices P1, AdjustedPrices P2
        WHERE P1.Day = Y.Min
        AND P2.Day = Y.Max
        AND P1.Ticker='GOOG' and P2.Ticker='GOOG';

SELECT P1.ticker, month, P2.close-P1.open as inc2016, IF(P2.close>P1.close, "Inc", "dec") as inc, vol
        FROM (SELECT MONTH(day) as month, MIN(Day) AS Min, MAX(Day) AS Max, SUM(Volume) as vol
            FROM AdjustedPrices P
            WHERE Ticker='UAL' and YEAR(day)=2016
            GROUP BY month) as Y, AdjustedPrices P1, AdjustedPrices P2
        WHERE P1.Day = Y.Min
        AND P2.Day = Y.Max
        AND P1.Ticker='UAL' and P2.Ticker='UAL';
        

-- last full year of data 

SELECT year 
FROM (SELECT YEAR(day) as year, MIN(Day) AS Min, MAX(Day) AS Max
      FROM AdjustedPrices P
      WHERE Ticker='FTV'
      GROUP BY year) a
WHERE MONTH(Min)=1 and MONTH(Max)=12
ORDER BY year DESC
LIMIT 1; 

   

-- get last full year that the two stocks were traded together 
SELECT year
FROM (SELECT YEAR(day) as year, MIN(Day) AS Min, MAX(Day) AS Max
FROM AdjustedPrices P
WHERE Ticker='GOOG'
GROUP BY year) a
JOIN 
(SELECT YEAR(day) as year, MIN(Day) AS Min, MAX(Day) AS Max
FROM AdjustedPrices P
WHERE Ticker='UAL'
GROUP BY year) b
USING (year, Min, Max) 
WHERE MONTH(Min)=1 and MONTH(Max)=12
ORDER BY year DESC
LIMIT 1; 



