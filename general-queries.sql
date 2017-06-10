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
SELECT Y.Year, Y.Ticker
FROM (SELECT Ticker, YEAR(Day) AS Year, MIN(Day) AS Min, MAX(Day) AS Max
    FROM Prices P
    GROUP BY Ticker, YEAR(Day)) as Y, Prices P1, Prices P2
WHERE P1.Day = Y.Min
    AND P2.Day = Y.Max
    AND P1.Ticker = Y.Ticker
    AND P2.Ticker = Y.Ticker
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

--Q5
