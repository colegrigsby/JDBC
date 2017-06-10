-- Q1.1
SELECT SUM(Volume)
FROM Prices
WHERE YEAR(Day) < 2016;

-- Q1.2
SELECT SUM(Volume)
FROM Prices
WHERE YEAR(Day) < 2017;

-- Q1.3
SELECT COUNT(*)
FROM
    (SELECT Ticker, Close
    FROM AdjustedPrices A
    WHERE Day = (SELECT MAX(DAY)
                FROM AdjustedPrices
                WHERE YEAR(Day) = 2015)
    GROUP BY Ticker, Close) AP1,
    (SELECT Ticker, Close
    FROM AdjustedPrices A
    WHERE Day = (SELECT MAX(DAY)
                FROM AdjustedPrices
                WHERE YEAR(Day) = 2016)
    GROUP BY Ticker, Close) AP2
WHERE AP1.Ticker = AP2.Ticker
    AND AP1.Close < AP2.Close
;

-- Q1.4
SELECT COUNT(*)
FROM
    (SELECT Ticker, Close
    FROM AdjustedPrices A
    WHERE Day = (SELECT MAX(DAY)
                FROM AdjustedPrices
                WHERE YEAR(Day) = 2015)
    GROUP BY Ticker, Close) AP1,
    (SELECT Ticker, Close
    FROM AdjustedPrices A
    WHERE Day = (SELECT MAX(DAY)
                FROM AdjustedPrices
                WHERE YEAR(Day) = 2016)
    GROUP BY Ticker, Close) AP2
WHERE AP1.Ticker = AP2.Ticker
    AND AP1.Close > AP2.Close
;

-- Q2
SELECT S.Ticker, S.Name
FROM (SELECT S.Ticker, S.Name, SUM(Volume) AS Vol
        FROM Prices P JOIN Securities S ON P.Ticker = S.Ticker
        WHERE YEAR(Day) = 2016
        GROUP BY S.Ticker, S.Name) S
WHERE 10 >=
        (SELECT COUNT(*)
            FROM (SELECT SUM(Volume) AS Vol
                    FROM Prices P JOIN Securities S ON P.Ticker = S.Ticker
                    WHERE YEAR(Day) = 2016
                    GROUP BY S.Ticker) S1
            WHERE S.Vol <= S1.Vol)
ORDER BY Vol
;

-- Q3
SELECT Y.Year, Y.Ticker, S.Name
FROM (SELECT Ticker, YEAR(Day) AS Year, MIN(Day) AS Min, MAX(Day) AS Max
    FROM Prices P
    GROUP BY Ticker, YEAR(Day)) as Y, Prices P1, Prices P2, Securities S
WHERE P1.Day = Y.Min
    AND P2.Day = Y.Max
    AND P1.Ticker = Y.Ticker
    AND P2.Ticker = Y.Ticker
    AND S.Ticker = Y.Ticker
    AND 5 >= 
        (SELECT COUNT(*)
            FROM (SELECT Ticker, YEAR(Day) AS Year,
                        MIN(Day) AS Min, MAX(Day) AS Max
                    FROM Prices P
                    GROUP BY Ticker, YEAR(Day)) as Y1, Prices P3, Prices P4
            WHERE P3.Day = Y1.Min
                AND P4.Day = Y1.Max
                AND P3.Ticker = Y1.Ticker
                AND P4.Ticker = Y1.Ticker
                AND Y1.Year = Y.Year
                AND (P2.Close - P1.Open) <= (P4.Close - P3.Open))
ORDER BY Y.Year, Y.Ticker;

-- Q4

-- Q5.1
SELECT Sector, SUM(up), SUM(down), SUM(up)/SUM(down)
FROM (SELECT Sector,
            IF(Close > Open, 1, 0) as Up, IF(Close > Open, 0, 1) as Down
        FROM AdjustedPrices AP, Securities S
        WHERE AP.Ticker = S.Ticker
            AND YEAR(Day) = 2016
            AND Sector != 'Telecommunications Services') X
GROUP BY Sector
;

-- Q5.2
SELECT Sector, SUM(AP2.Close) - SUM(AP1.Open)
FROM Securities S,
    (SELECT Ticker, Open
    FROM AdjustedPrices A
    WHERE Day = (SELECT MIN(DAY)
                FROM AdjustedPrices
                WHERE YEAR(Day) = 2016)) AP1,
    (SELECT Ticker, Close
    FROM AdjustedPrices A
    WHERE Day = (SELECT MAX(DAY)
                FROM AdjustedPrices
                WHERE YEAR(Day) = 2016)) AP2
WHERE AP1.Ticker = AP2.Ticker
    AND S.Ticker = AP1.Ticker
GROUP BY Sector
;

-- Q5.3
SELECT Sector, SUM(Volume), (SUM(Volume) - A.Avg) / A.StDev as Stv
    FROM (SELECT STDDEV(V) as StDev, AVG(V) as Avg
            FROM (SELECT SUM(Volume) as V
                    FROM AdjustedPrices AP, Securities S
                    WHERE YEAR(Day) = 2016
                        AND AP.Ticker = S.Ticker
                    GROUP BY Sector) X
            ) A, AdjustedPrices AP, Securities S
    WHERE YEAR(Day) = 2016
        AND AP.Ticker = S.Ticker
    GROUP BY Sector
;

