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
  `Scale` nvarchar(100) DEFAULT NULL,
  `TimelineName` nvarchar(100) DEFAULT NULL,
  `TimelineDescription` nvarchar(5000) DEFAULT NULL,
  `Theme` nvarchar(100) DEFAULT NULL,
  `StartDate` datetime DEFAULT NULL,
  `Enddate` datetime DEFAULT NULL,
  `DateCreated` timestamp  DEFAULT CURRENT_TIMESTAMP,
  `Private` boolean DEFAULT false,
  `TimelineOwner` nvarchar(100) DEFAULT NULL,
  PRIMARY KEY (`TimelineID`), 
  UNIQUE KEY `TimelineID_UNIQUE` (`TimelineID`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;

-- This part is for populating timelines table with dummy data

INSERT INTO `project`.`timelines`
(`TimelineID`,
`Scale`,
`TimelineName`,
`TimelineDescription`,
`Theme`,
`StartDate`,
`Enddate`,
`DateCreated`,
`Private`,
`TimelineOwner`)
VALUES
(01 , 12, 'aaa', 'descr0', 'dark', default, default, default, default, '1'),
(02 , 2, 'aaabb', 'descr2', 'dark', default, default, default, default,'1'),
(03 , 4, 'bbbbb', 'descr3', 'light', default, default, default, default,'2'),
(04 , 17,'bbbcc', 'descr4', 'dark',default, default, default, default, '2'),
(05 , 11, 'aaacc', 'descr5', 'light', default, default, default, default,'3'),
(06 , 2, 'cccaaa', 'descr6', 'light', default, default, default, default,'4'),
(07 , 1, 'aaagra', 'descr7', 'dark', default, default, default, default,'1'),
(08 , 59, 'tempus', 'descr8', 'mad', default, default, default, default,'5'),
(09 , 22,'fungi', 'descr9', 'dark', default, default, default, default,'6'),
(10 , 33, 'container', 'descr10', 'barkingmad', default, default, default, default,'7');

