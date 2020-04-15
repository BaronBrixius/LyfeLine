CREATE TABLE `events` (
  `EventID` int NOT NULL AUTO_INCREMENT,
  `EventType` tinyint NOT NULL,
  `EventName` nvarchar(100) DEFAULT NULL,
  `EventDescription` nvarchar(5000) DEFAULT NULL,
  `StartYear` bigint NOT NULL,
  `StartMonth` tinyint unsigned NOT NULL,
  `StartDay` tinyint unsigned NOT NULL,
  `StartTime` time DEFAULT NULL,
  `EndYear` bigint DEFAULT NULL,
  `EndMonth` tinyint unsigned DEFAULT NULL,
  `EndDay` tinyint unsigned DEFAULT NULL,
  `EndTime` time DEFAULT NULL,
  PRIMARY KEY (`EventID`),
  UNIQUE KEY `EventID_UNIQUE` (`EventID`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;

CREATE TABLE `groups` (
  `GroupID` int NOT NULL AUTO_INCREMENT,
  `GroupName` nvarchar(100) DEFAULT NULL,
  `GroupDescription` nvarchar(5000) DEFAULT NULL,
  `Scale` tinyint NOT NULL,
  `Public` tinyint(1) DEFAULT '0',
  `FontID` tinyint DEFAULT '1',
  `FontSize` tinyint DEFAULT '12',
  `ThemeID` tinyint DEFAULT '1',
  `StartYear` bigint NOT NULL,
  `StartMonth` tinyint unsigned NOT NULL,
  `StartDay` tinyint unsigned NOT NULL,
  `StartTime` time NOT NULL,
  `EndYear` bigint NOT NULL,
  `EndMonth` tinyint unsigned DEFAULT NULL,
  `EndDay` tinyint unsigned DEFAULT NULL,
  `EndTime` time DEFAULT NULL,
  PRIMARY KEY (`GroupID`),
  UNIQUE KEY `GroupID_UNIQUE` (`GroupID`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;

CREATE TABLE `groupevents` (
  `GroupID` int NOT NULL,
  `EventID` int NOT NULL,
  PRIMARY KEY (`GroupID`,`EventID`),
  KEY `fk_groupevents_events1_idx` (`EventID`),
  CONSTRAINT `fk_groupevents_events1` FOREIGN KEY (`EventID`) REFERENCES `events` (`EventID`),
  CONSTRAINT `fk_groupevents_groups` FOREIGN KEY (`GroupID`) REFERENCES `groups` (`GroupID`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;

CREATE TABLE `users` (
  `UserID` int NOT NULL AUTO_INCREMENT,
  `UserName` nvarchar(100) DEFAULT NULL,
  `UserEmail` nvarchar(100) NOT NULL,
  `Password` nvarchar(90) NOT NULL,
  `Salt` nvarchar(30) NOT NULL,
  `Admin` tinyint DEFAULT '0',
  PRIMARY KEY (`UserID`),
  UNIQUE KEY `UserID_UNIQUE` (`UserID`),
  UNIQUE KEY `UserEmail_UNIQUE` (`UserEmail`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;

-- Code for creating timelines

CREATE TABLE `timelines` (
  `TimelineID` int NOT NULL AUTO_INCREMENT,
  `Scale` varchar(100) CHARACTER SET utf8 COLLATE utf8_general_ci DEFAULT NULL,
  `TimelineName` varchar(100) CHARACTER SET utf8 COLLATE utf8_general_ci DEFAULT NULL,
  `TimelineDescription` varchar(5000) CHARACTER SET utf8 COLLATE utf8_general_ci DEFAULT NULL,
  `Theme` varchar(100) CHARACTER SET utf8 COLLATE utf8_general_ci DEFAULT NULL,
  `StartYear` bigint NOT NULL,
  `StartMonth` tinyint unsigned NOT NULL,
  `StartDay` tinyint unsigned NOT NULL,
  `StartHour` tinyint unsigned NOT NULL,
  `StartMinute` tinyint unsigned NOT NULL,
  `StartSecond` tinyint unsigned NOT NULL,
  `StartMillisecond` smallint unsigned NOT NULL,
  `EndYear` bigint DEFAULT NULL,
  `EndMonth` tinyint unsigned DEFAULT NULL,
  `EndDay` tinyint unsigned DEFAULT NULL,
  `EndHour` tinyint unsigned DEFAULT NULL,
  `EndMinute` tinyint unsigned DEFAULT NULL,
  `EndSecond` tinyint unsigned DEFAULT NULL,
  `EndMillisecond` smallint unsigned DEFAULT NULL,
  `CreatedYear` bigint DEFAULT NULL,
  `CreatedMonth` tinyint unsigned DEFAULT NULL,
  `CreatedDay` tinyint unsigned DEFAULT NULL,
  `CreatedHour` tinyint unsigned DEFAULT NULL,
  `CreatedMinute` tinyint unsigned DEFAULT NULL,
  `CreatedSecond` tinyint unsigned DEFAULT NULL,
  `CreatedMillisecond` smallint unsigned DEFAULT NULL,
  `Private` tinyint(1) DEFAULT '0',
  `TimelineOwner` int,

  PRIMARY KEY (`TimelineID`),
  UNIQUE KEY `TimelineID_UNIQUE` (`TimelineID`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;


CREATE TRIGGER CreatedDateTime BEFORE INSERT ON timelines FOR EACH ROW BEGIN
    if (isnull(new.`CreatedYear`)) then
        set new.`CreatedYear`=YEAR(NOW());
        set new.`CreatedMonth`=MONTH(NOW());
        set new.`CreatedDay`=DAY(NOW());
        set new.`CreatedHour`=HOUR(NOW());
        set new.`CreatedMinute`=MINUTE(NOW());
        set new.`CreatedSecond`=SECOND(NOW());
        set new.`CreatedMillisecond`=CAST(UNIX_TIMESTAMP(CURTIME(3)) % 1 * 1000 AS unsigned);
    end if;
END;


-- This part is for populating timelines table with dummy data

INSERT INTO `timelines`
(`TimelineID`,
`Scale`,
`TimelineName`,
`TimelineDescription`,
`Theme`,
`StartYear`,
`StartMonth`,
`StartDay`,
`StartHour`,
`StartMinute`,
`StartSecond`,
`StartMillisecond`,
`EndYear`,
`EndMonth`,
`EndDay`,
`EndHour`,
`EndMinute`,
`EndSecond`,
`EndMillisecond`,
`CreatedYear`,
`CreatedMonth`,
`CreatedDay`,
`CreatedHour`,
`CreatedMinute`,
`CreatedSecond`,
`CreatedMillisecond`,
`Private`,
`TimelineOwner`)
VALUES
(01 , 12, 'aaa', 'descr0', 'dark', 2000, 5, 20, 4, 43,32,213, 2001, 5, 20, 4, 43,32,213, 2000, 5, 20, 4, 43,32,213, default, 1),
(02 , 2, 'aaabb', 'descr2', 'dark', 2003, 5, 20, 4, 43,32,213, 2005, 5, 20, 4, 43,32,213, 2003, 5, 20, 4, 43,32,213, default,1),
(03 , 4, 'bbbbb', 'descr3', 'light', 2004, 5, 20, 4, 43,32,213, 2006, 5, 20, 4, 43,32,213, 2004, 5, 20, 4, 43,32,213, default,2),
(04 , 17,'bbbcc', 'descr4', 'dark',2007, 5, 20, 4, 43,32,213, 2008, 5, 20, 4, 43,32,213, 2007, 5, 20, 4, 43,32,213, default, 2),
(05 , 11, 'aaacc', 'descr5', 'light', 2008, 5, 20, 4, 43,32,213, 2009, 5, 20, 4, 43,32,213, 2008, 5, 20, 4, 43,32,213, default,3),
(06 , 2, 'cccaaa', 'descr6', 'light', 2009, 5, 20, 4, 43,32,213, 2010, 5, 20, 4, 43,32,213, 2009, 5, 20, 4, 43,32,213, default,4),
(07 , 1, 'aaagra', 'descr7', 'dark', 1990, 5, 20, 4, 43,32,213, 1991, 5, 20, 4, 43,32,213, 1990, 5, 20, 4, 43,32,213, default,1),
(08 , 59, 'tempus', 'descr8', 'mad', 1500, 5, 20, 4, 43,32,213, 1505, 5, 20, 4, 43,32,213, 2000, 5, 20, 4, 46,32,213, default,5),
(09 , 22,'fungi', 'descr9', 'dark', 1550, 5, 20, 4, 43,32,213, 1555, 5, 20, 4, 43,32,213, 1550, 5, 20, 4, 43,32,213, default,6),
(10 , 33, 'container', 'descr10', 'barkingmad', 1600, 5, 20, 4, 43,32,213, 1661, 5, 20, 4, 43,32,213, 2016, 5, 20, 4, 43,32,213, default,7);