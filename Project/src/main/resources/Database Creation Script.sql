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
  `Start` varchar(30) GENERATED ALWAYS AS (concat(abs(`StartYear`),_utf8mb4'-',lpad(`StartMonth`,2,0),_utf8mb4'-',lpad(`StartDay`,2,0),_utf8mb4' ',if((`StartYear` > 0),_utf8mb4'AD',_utf8mb4'BC'))) VIRTUAL,
  `End` varchar(30) GENERATED ALWAYS AS (concat(abs(`EndYear`),_utf8mb4'-',lpad(`EndMonth`,2,0),_utf8mb4'-',lpad(`EndDay`,2,0),_utf8mb4' ',if((`EndYear` > 0),_utf8mb4'AD',_utf8mb4'BC'))) VIRTUAL,
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
  `Start` varchar(30) GENERATED ALWAYS AS (concat(abs(`StartYear`),_utf8mb4'-',lpad(`StartMonth`,2,0),_utf8mb4'-',lpad(`StartDay`,2,0),_utf8mb4' ',if((`StartYear` > 0),_utf8mb4'AD',_utf8mb4'BC'))) VIRTUAL,
  `End` varchar(30) GENERATED ALWAYS AS (concat(abs(`EndYear`),_utf8mb4'-',lpad(`EndMonth`,2,0),_utf8mb4'-',lpad(`EndDay`,2,0),_utf8mb4' ',if((`EndYear` > 0),_utf8mb4'AD',_utf8mb4'BC'))) VIRTUAL,
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
