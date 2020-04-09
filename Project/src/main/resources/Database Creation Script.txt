CREATE TABLE `events` (
  `EventID` int NOT NULL AUTO_INCREMENT,
  `EventType` tinyint NOT NULL,
  `EventName` varchar(100) CHARACTER SET utf8 COLLATE utf8_general_ci DEFAULT NULL,
  `EventDescription` varchar(5000) CHARACTER SET utf8 COLLATE utf8_general_ci DEFAULT NULL,
  `StartYear` bigint NOT NULL,
  `StartMonth` tinyint unsigned NOT NULL,
  `StartDay` tinyint unsigned NOT NULL,
  `StartTime` time NOT NULL,
  `EndYear` bigint DEFAULT NULL,
  `EndMonth` tinyint unsigned DEFAULT NULL,
  `EndDay` tinyint unsigned DEFAULT NULL,
  `EndTime` time DEFAULT NULL,
  `Start` varchar(30) GENERATED ALWAYS AS (concat(abs(`StartYear`),_utf8mb4'-',lpad(`StartMonth`,2,0),_utf8mb4'-',lpad(`StartDay`,2,0),_utf8mb4' ',if((`StartYear` > 0),_utf8mb4'AD',_utf8mb4'BC'))) VIRTUAL,
  `End` varchar(30) GENERATED ALWAYS AS (concat(abs(`EndYear`),_utf8mb4'-',lpad(`EndMonth`,2,0),_utf8mb4'-',lpad(`EndDay`,2,0),_utf8mb4' ',if((`EndYear` > 0),_utf8mb4'AD',_utf8mb4'BC'))) VIRTUAL,
  PRIMARY KEY (`EventID`),
  UNIQUE KEY `EventID_UNIQUE` (`EventID`)
) ENGINE=InnoDB AUTO_INCREMENT=3 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;

CREATE TABLE `groups` (
  `GroupID` int NOT NULL AUTO_INCREMENT,
  `GroupName` varchar(100) CHARACTER SET utf8 COLLATE utf8_general_ci DEFAULT NULL,
  `GroupDescription` varchar(5000) CHARACTER SET utf8 COLLATE utf8_general_ci DEFAULT NULL,
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
) ENGINE=InnoDB AUTO_INCREMENT=3 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;

CREATE TABLE `groupevents` (
  `GroupID` int NOT NULL,
  `EventID` int NOT NULL,
  PRIMARY KEY (`GroupID`,`EventID`),
  KEY `fk_groupevents_events1_idx` (`EventID`),
  CONSTRAINT `fk_groupevents_events1` FOREIGN KEY (`EventID`) REFERENCES `events` (`EventID`),
  CONSTRAINT `fk_groupevents_groups` FOREIGN KEY (`GroupID`) REFERENCES `groups` (`GroupID`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;