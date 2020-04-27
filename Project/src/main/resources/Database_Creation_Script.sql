CREATE TABLE `events`
(
    `EventID`            int               NOT NULL AUTO_INCREMENT,
    `EventOwner`         int                DEFAULT NULL,
    `EventImage`         nvarchar(5000)    DEFAULT NULL,
    `EventType`          nvarchar(100)    DEFAULT NULL,
    `EventName`          nvarchar(100)     DEFAULT NULL,
    `EventDescription`   nvarchar(5000)    DEFAULT NULL,
    `StartYear`          bigint            default NULL,
    `StartMonth`         tinyint unsigned  default NULL,
    `StartDay`           tinyint unsigned  default NULL,
    `StartHour`          tinyint unsigned  default NULL,
    `StartMinute`        tinyint unsigned  default NULL,
    `StartSecond`        tinyint unsigned  default NULL,
    `StartMillisecond`   smallint unsigned default NULL,
    `EndYear`            bigint            DEFAULT NULL,
    `EndMonth`           tinyint unsigned  DEFAULT NULL,
    `EndDay`             tinyint unsigned  DEFAULT NULL,
    `EndHour`            tinyint unsigned  DEFAULT NULL,
    `EndMinute`          tinyint unsigned  DEFAULT NULL,
    `EndSecond`          tinyint unsigned  DEFAULT NULL,
    `EndMillisecond`     smallint unsigned DEFAULT NULL,
    `CreatedYear`        bigint            DEFAULT NULL,
    `CreatedMonth`       tinyint unsigned  DEFAULT NULL,
    `CreatedDay`         tinyint unsigned  DEFAULT NULL,
    `CreatedHour`        tinyint unsigned  DEFAULT NULL,
    `CreatedMinute`      tinyint unsigned  DEFAULT NULL,
    `CreatedSecond`      tinyint unsigned  DEFAULT NULL,
    `CreatedMillisecond` smallint unsigned DEFAULT NULL,
    PRIMARY KEY (`EventID`),
    UNIQUE KEY `EventID_UNIQUE` (`EventID`)
) ENGINE = InnoDB
  DEFAULT CHARSET = utf8mb4
  COLLATE = utf8mb4_general_ci;


CREATE TRIGGER CreatedDateTimeEvents
    BEFORE INSERT
    ON events
    FOR EACH ROW
BEGIN
    if (isnull(new.`CreatedYear`)) then
        set new.`CreatedYear` = YEAR(NOW());
        set new.`CreatedMonth` = MONTH(NOW());
        set new.`CreatedDay` = DAY(NOW());
        set new.`CreatedHour` = HOUR(NOW());
        set new.`CreatedMinute` = MINUTE(NOW());
        set new.`CreatedSecond` = SECOND(NOW());
        set new.`CreatedMillisecond` = CAST(UNIX_TIMESTAMP(CURTIME(3)) % 1 * 1000 AS unsigned);
    end if;
END;


CREATE TRIGGER EndDate
    BEFORE INSERT
    ON events
    FOR EACH ROW
BEGIN
    if (isnull(new.`EndYear`)) then
        set new.`EndYear` = new.StartYear;
        set new.`EndMonth` = new.StartMonth;
        set new.`EndDay` = new.StartDay;
        set new.`EndHour` = new.StartHour;
        set new.`EndMinute` = new.StartMinute;
        set new.`EndSecond` = new.StartSecond;
        set new.`EndMillisecond` = new.StartMillisecond;
    end if;
END;


-- Lookup table for the scale column of timeline table


CREATE TABLE `scale_lookup`
(
    `ID`   int          NOT NULL AUTO_INCREMENT,
    `unit` nvarchar(20) NOT NULL,
    PRIMARY KEY (`ID`)
)
    ENGINE = InnoDB
    AUTO_INCREMENT = 9
    DEFAULT CHARSET = utf8mb4
    COLLATE = utf8mb4_general_ci;


INSERT INTO `scale_lookup`
(`ID`,
 `unit`)
VALUES (1, 'Seconds'),
       (2, 'Minutes'),
       (3, 'Hours'),
       (4, 'Days'),
       (5, 'Weeks'),
       (6, 'Months'),
       (7, 'Years'),
       (8, 'Decades');




CREATE TABLE `Images`
(
    `ImageID`  int NOT NULL AUTO_INCREMENT,
    `ImageURL` varchar(255) DEFAULT NULL,
    PRIMARY KEY (`ImageID`),
    UNIQUE KEY `ImageID_UNIQUE` (`ImageID`)
) ENGINE = InnoDB
  DEFAULT CHARSET = utf8mb4
  COLLATE = utf8mb4_general_ci;


INSERT INTO `Images`
(`ImageID`,
 `ImageURL`)
VALUES (1, 'image1.png'),
       (2, 'image2.jpg'),
       (3, 'image3.png'),
       (4, 'image4.png'),
       (5, 'image5.png');


CREATE TABLE `groups`
(
    `GroupID`          int              NOT NULL AUTO_INCREMENT,
    `GroupName`        nvarchar(100)    DEFAULT NULL,
    `GroupDescription` nvarchar(5000)   DEFAULT NULL,
    `Scale`            tinyint          NOT NULL,
    `Public`           tinyint(1)       DEFAULT '0',
    `FontID`           tinyint          DEFAULT '1',
    `FontSize`         tinyint          DEFAULT '12',
    `ThemeID`          tinyint          DEFAULT '1',
    `StartYear`        bigint           NOT NULL,
    `StartMonth`       tinyint unsigned NOT NULL,
    `StartDay`         tinyint unsigned NOT NULL,
    `StartTime`        time             NOT NULL,
    `EndYear`          bigint           NOT NULL,
    `EndMonth`         tinyint unsigned DEFAULT NULL,
    `EndDay`           tinyint unsigned DEFAULT NULL,
    `EndTime`          time             DEFAULT NULL,
    PRIMARY KEY (`GroupID`),
    UNIQUE KEY `GroupID_UNIQUE` (`GroupID`)
) ENGINE = InnoDB
  DEFAULT CHARSET = utf8mb4
  COLLATE = utf8mb4_general_ci;


CREATE TABLE `groupevents`
(
    `GroupID` int NOT NULL,
    `EventID` int NOT NULL,
    PRIMARY KEY (`GroupID`, `EventID`),
    KEY `fk_groupevents_events1_idx` (`EventID`),
    CONSTRAINT `fk_groupevents_events1` FOREIGN KEY (`EventID`) REFERENCES `events` (`EventID`),
    CONSTRAINT `fk_groupevents_groups` FOREIGN KEY (`GroupID`) REFERENCES `groups` (`GroupID`)
) ENGINE = InnoDB
  DEFAULT CHARSET = utf8mb4
  COLLATE = utf8mb4_general_ci;


CREATE TABLE `users`
(
    `UserID`    int           NOT NULL AUTO_INCREMENT,
    `UserName`  nvarchar(100) DEFAULT NULL,
    `UserEmail` nvarchar(100) NOT NULL,
    `Password`  nvarchar(90)  NOT NULL,
    `Salt`      nvarchar(30)  NOT NULL,
    `Admin`     tinyint       DEFAULT '0',
    PRIMARY KEY (`UserID`),
    UNIQUE KEY `UserID_UNIQUE` (`UserID`),
    UNIQUE KEY `UserEmail_UNIQUE` (`UserEmail`)
) ENGINE = InnoDB
  DEFAULT CHARSET = utf8mb4
  COLLATE = utf8mb4_general_ci;


-- Code for creating timelines
CREATE TABLE `timelines`
(
    `TimelineID`          int               NOT NULL AUTO_INCREMENT,
    `Scale`               int               DEFAULT NULL,
    `TimelineName`        nvarchar(100)     DEFAULT NULL,
    `TimelineDescription` nvarchar(5000)    DEFAULT NULL,
    `Theme`               nvarchar(100)     DEFAULT NULL,
    `StartYear`           bigint            NOT NULL,
    `StartMonth`          tinyint unsigned  NOT NULL,
    `StartDay`            tinyint unsigned  NOT NULL,
    `StartHour`           tinyint unsigned  NOT NULL,
    `StartMinute`         tinyint unsigned  NOT NULL,
    `StartSecond`         tinyint unsigned  NOT NULL,
    `StartMillisecond`    smallint unsigned NOT NULL,
    `EndYear`             bigint            DEFAULT NULL,
    `EndMonth`            tinyint unsigned  DEFAULT NULL,
    `EndDay`              tinyint unsigned  DEFAULT NULL,
    `EndHour`             tinyint unsigned  DEFAULT NULL,
    `EndMinute`           tinyint unsigned  DEFAULT NULL,
    `EndSecond`           tinyint unsigned  DEFAULT NULL,
    `EndMillisecond`      smallint unsigned DEFAULT NULL,
    `CreatedYear`         bigint            DEFAULT NULL,
    `CreatedMonth`        tinyint unsigned  DEFAULT NULL,
    `CreatedDay`          tinyint unsigned  DEFAULT NULL,
    `CreatedHour`         tinyint unsigned  DEFAULT NULL,
    `CreatedMinute`       tinyint unsigned  DEFAULT NULL,
    `CreatedSecond`       tinyint unsigned  DEFAULT NULL,
    `CreatedMillisecond`  smallint unsigned DEFAULT NULL,
    `Private`             boolean           DEFAULT true,
    `TimelineOwner`       int,
    PRIMARY KEY (`TimelineID`),
    UNIQUE KEY `TimelineID_UNIQUE` (`TimelineID`)
) ENGINE = InnoDB
  DEFAULT CHARSET = utf8mb4
  COLLATE = utf8mb4_general_ci;


CREATE TABLE timelineevents
(
    TimelineID int NOT NULL,
    EventID    int NOT NULL,
    CONSTRAINT pK_timelinesevent PRIMARY KEY (eventID, timelineID),
    CONSTRAINT fk_timelineevents_events1
        FOREIGN KEY (EventID)
            REFERENCES events (EventID)
            ON DELETE CASCADE,
    CONSTRAINT fk_timelineevents_timelines
        FOREIGN KEY (TimelineID)
            REFERENCES timelines (TimelineID)
            ON DELETE CASCADE
);


CREATE TRIGGER CreatedDateTime
    BEFORE INSERT
    ON timelines
    FOR EACH ROW
BEGIN
    if (isnull(new.`CreatedYear`)) then
        set new.`CreatedYear` = YEAR(NOW());
        set new.`CreatedMonth` = MONTH(NOW());
        set new.`CreatedDay` = DAY(NOW());
        set new.`CreatedHour` = HOUR(NOW());
        set new.`CreatedMinute` = MINUTE(NOW());
        set new.`CreatedSecond` = SECOND(NOW());
        set new.`CreatedMillisecond` = CAST(UNIX_TIMESTAMP(CURTIME(3)) % 1 * 1000 AS unsigned);
    end if;
END;


-- This part is for populating tables with dummy data


INSERT INTO `users`
    (`UserID`, `UserName`, `UserEmail`, `Password`, `Salt`, `Admin`)
VALUES ('1', 'Ben', 'Ben@gmail.com',
        'FPUpkk14h2EWAX9J7q18Ue6QJ/VSrs5ulnaw/Tggo23smYvqcLKihIUARNQcxUpDSGXOGBsGo4gjKTikDfrpxw==',
        'hXEFj6Yy9hanXVOUyACANrUi1eZs4f', '1'),
       ('2', 'Max', 'Max@gmail.com',
        'bXKyPFQD//MW1XtOlVrgEDvEXIm9xzT+z4wBrMKR7DTHeETUPFlYpcuvanM/I2dPZSa5fQEnKc4E2D6ZD7sOiA==',
        'Q48XUaFIG4LITasAYZzSNUHskubTw5', '0'),
       ('3', 'Dillon', 'Dillon@gmail.com',
        'a62/l7UcPfNOvlPLDwhy1AYocxnvmFQImNFQDbb+encq/FoV7OOYykxivMleq95EZw88wkG0H46XPS3kW6u3ag==',
        'xerSaGrjebBz0IET7x7vJr1ra9w0RX', '0'),
       ('4', 'Firas', 'Firas@gmail.com',
        'QLwdPiI5hPNyRBUD0tmbNaIZTelEAunhYBEl6RDLvhI3YDpUn73IiE8tg2zo1jVoCw3QM46b2SGkLS3QabSexg==',
        'FVauWBLlMJidPWyN4wCn3tzC0YvQLv', '0'),
       ('5', 'Lasse', 'Lasse@gmail.com',
        'oqi8KO+E3QCUKYbjX0CECxRunPEdZIY0rgcTVeo91UA7QPNH3VM0rMxkRtzDHPQDDvUQXlEz/4zhDlg36vl8+A==',
        'psh4bByUWvaP3UacHHU9VLEcm53Zfg', '0'),
       ('6', 'Haraldur', 'Haraldur@gmail.com',
        'uT6n+/VL6V5yN9BDM7JgBvca0MbO2t3E8LnBwPSDpknUFBkGt89yEHeS20QNTYPX+O7InMuft5mxg4KLkkLoKA==',
        'X9Upxcdkg0JSgzXbTuDqW0XBXXXdgS', '0'),
       ('7', 'Vytautas', 'Vytautas@gmail.com',
        '08faHiezce1RlZ22ntPWdX/tQZ5214qiIntsmKWNZ73prBi2rR45Y1UPWnyhN7fRfLCL/2jiRjw+ZtUR6Mwu7Q==',
        'Z5bCam0vfNQF9HjZd1VGJeu6WTgL4l', '0'),
       ('8', 'Timothy', 'Timothy@gmail.com',
        'FUPZbpwU9RaPyIyh4hbiJGJJIlaEtm/zJ4owm1hfZ1d4Q7/JwEP0M2bRLA3SgxTRo1i+Ci0VUiofLyJEqkItng==',
        'HqJsDL08iV2tRAJuRojFGJTY0bpvy4', '0'),
       ('9', 'Matas', 'Matas@gmail.com',
        'GyESVgT0doFrIJmTXRRhjEzGl4MoUc/hbZsVLRhipE8AUGZy4kV7sFVej40bGnhmyZYv98S6jhjdIcy9RLhheQ==',
        '9yaA4kzAWPYVPRhKog9fpCLeEm43Br', '0'),
       ('10', 'Lorenz', 'Lorenz@gmail.com',
        'PYHAmdNfo+zK/kjvaRq6fON76Iymyb5FPkt2vich++tkPxugd76WPIF+PXjh9iWYhZUiJLlW7O883vj1O6gaug==',
        'n15sLcjfgleZFTvBQDvi1MhFDJ3R7u', '0'),
       ('11', 'Chris', 'Chris@gmail.com',
        'ErFKIFDX5jx7+rFJDpMUpZOTHgCJVUZl6fPsdRZt4m96HpMZknbot/1pr1ns/xChn0V0wrXFbvcs7vTHNZxu+A==',
        '1cQoymwLl05zLbl2q36uWviIB8ffcS', '0'),
       ('12', 'Jan', 'Jan@gmail.com',
        'J4zKZyfXIRQinm4jw/i1+3WXaVrTX9Wl0YhJNUHprUBE0ogUfEojn3gfOo2jBxqb9gtUxEhnhIGxpOfwerI+fQ==',
        'CEc1AAkRdz7BguqKQL4e4wrw7A3j6L', '0'),
       ('13', 'Hans Ove', 'Hans@math.biz',
        'tPmbHxe4qtzP8AaCpQJs/Hjr8RW3xDUGx+kk75AENDVY7Kkz85jJ/H1KICOH9TOsZPg4e/4ldTM9WzajCOJQiw==',
        '8IzHZXvKP9hwwIr5EflEvhLYdo2AVY', '0')
;



INSERT INTO `timelines`
(`TimelineID`,
 `Scale`, `TimelineName`, `TimelineDescription`, `Theme`, `StartYear`, `StartMonth`, `StartDay`, `StartHour`,
 `StartMinute`, `StartSecond`, `StartMillisecond`, `EndYear`, `EndMonth`, `EndDay`, `EndHour`, `EndMinute`, `EndSecond`,
 `EndMillisecond`, `CreatedYear`, `CreatedMonth`, `CreatedDay`, `CreatedHour`, `CreatedMinute`, `CreatedSecond`,
 `CreatedMillisecond`, `Private`, `TimelineOwner`)
VALUES (01, 1, 'Fall of Rome', 'Out with a wimper, not a bang', 'dark', 350, 5, 20, 4, 43, 32, 213, 2001, 5, 20, 4,
        43, 32, 213, 2000, 5, 20, 4, 43,
        32, 213, default, 1),
       (02, 2, 'New Timeline', '', 'dark', 2020, 5, 20, 4, 43, 32, 213, 2005, 5, 20, 4, 43, 32, 213, 2003, 5, 20, 4,
        43, 32, 213, default, 1),
       (03, 4, 'Hound of Baskervilles', 'Investigation of an attempted murder', 'light', 1902, 5, 20, 4, 43, 32, 213,
        2006, 5, 20, 4, 43, 32, 213, 2003, 5, 20, 3,
        43, 32, 213, default, 2),
       (04, 5, 'Dr. Strangelove', 'A dark comedy on nuclear war', 'dark', 1987, 5, 20, 4, 43, 32, 213, 2008, 5, 20, 4,
        43, 32, 213, 2007, 5, 20, 4, 43,
        32, 213, default, 2),
       (05, 6, 'Incredibly, Wastefully Long Timeline Name', '', 'light', 2020, 5, 20, 4, 43, 32, 213, 2009, 5, 20, 4,
        43, 32, 213, 2008, 5, 20, 4,
        43, 32, 213, default, 3),
       (06, 7, 'Bronze Age Collapse', 'When civilization reset', 'light', -13000, 5, 20, 4, 43, 32, 213, 2010, 5, 20, 4,
        43, 32, 213, 2009, 5, 20, 4,
        43, 32, 213, default, 4),
       (07, 8, 'Life of Bacillus', 'Life and times of a bacterium', 'mad', 2020, 5, 20, 4, 43, 32, 213, 1505, 5, 20, 4,
        43, 32, 213, 2000, 5, 20, 4, 46,
        32, 213, default, 5),
       (08, 5, 'Decay of Ununoctium', 'Radioactive decay - a study', 'dark', 2020, 5, 20, 4, 43, 32, 213, 1555, 5, 20,
        4, 43, 32, 213, 1550, 5, 20, 4, 43,
        32, 213, default, 6)
;


INSERT INTO `events` (`EventOwner`, `EventType`, `EventName`, `EventDescription`, `StartYear`, `StartMonth`,
                      `StartDay`, `StartHour`, `StartMinute`, `StartSecond`, `StartMillisecond`, `EndYear`, `EndMonth`, `EndDay`,
                      `EndHour`, `EndMinute`, `EndSecond`, `EndMillisecond`)
VALUES ('1', '1', 'Crossing the Rubicon', 'Julius Caesar''s crossing the Rubicon river in January 49 BC precipitated
        the Roman Civil War, which ultimately led to Caesar becoming dictator and the rise of the imperial era of Rome.
        Caesar had been appointed to a governorship over a region that ranged from southern Gaul to Illyricum (but not Italy).
        As his term of governorship ended, the Roman Senate ordered Caesar to disband his army and return to Rome. He was
        explicitly ordered not to bring his army across the Rubicon river, which was at that time a northern boundary of Italy.
        In January of 49 BC, Caesar brought the 13th legion across the river, which the Roman government considered
        insurrection, treason, and a declaration of war on the Roman Senate. According to some authors, he is said to have
        uttered the phrase "alea iacta est"—the die is cast—as his army marched through the shallow river.'
        , '49', '1', '13', '17', '25', '40', '20', '30', '10', '25', '22', '50', '45','40'),
        ('1', '1', 'Great Roman Civil War', 'The Great Roman Civil War (49–45 BC), also known as Caesar''s Civil War, was
        one of the last politico-military conflicts in the Roman Republic before the establishment of the Roman Empire.
        It began as a series of political and military confrontations, between Julius Caesar (100–44 BC), his political supporters
        (broadly known as Populares), and his legions, against the Optimates (or Boni), the politically conservative and socially
        traditionalist faction of the Roman Senate, who were supported by Pompey (106–48 BC) and his legions.[1]',
        '49', '5', '5', '5', '10', '10', '10', '45', '10', '25', '22', '50', '45','40'),
        ('1', '1', 'Marcus Tullius Cicero', 'Marcus Tullius Cicero[a] (/ˈsɪsəroʊ/ SISS-ə-roh, Latin:
        [ˈmaːrkʊs ˈtʊllɪ.ʊs ˈkɪkɛroː]; 3 January 106 BC – 7 December 43 BC) was a Roman statesman, lawyer and Academic
        Skeptic philosopher[3] who wrote extensively on rhetoric, orations, philosophy, and politics, and is considered one of
        Rome''s greatest orators and prose stylists.[4][5] A leading political figure in the final years of the Roman Republic,
        Cicero vainly tried to uphold the republican system''s integrity during the instability that led to the establishment of
        the Roman Empire.[6] He came from a wealthy municipal family of the Roman equestrian order, and served as consul in the
        year 63 BC.', '106', '8', '8', '9', '20', '20', '25', '43', '10', '30', '22', '50', '45','40');


INSERT INTO `timelineevents` (`TimelineID`, `EventID`)
VALUES ('1', '1'),
       ('1', '2'),
       ('1', '3'),
       ('2', '2'),
       ('3', '2'),
       ('4', '1'),
       ('5', '1'),
       ('6', '1'),
       ('7', '1'),
       ('8', '1');

