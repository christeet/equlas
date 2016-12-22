drop schema `equals1DB`;
CREATE DATABASE IF NOT EXISTS equals1DB;
USE equals1DB;

DROP TABLE IF EXISTS `Person`;
CREATE TABLE `Person` (
  `id` int unsigned NOT NULL AUTO_INCREMENT,
  `lastName` varchar(50) NOT NULL,
  `firstName` varchar(50) NOT NULL,
  `dateOfBirth` date,
  `placeOfOrigin` varchar(50),
  `sex` char(1),
  `userName` varchar(20),
  `password` int,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=1 DEFAULT CHARSET=utf8;

DROP TABLE IF EXISTS `Module`;
CREATE TABLE `Module` (
  `id` int unsigned NOT NULL AUTO_INCREMENT,
  `name` varchar(50) NOT NULL,
  `shortName` varchar(20) NOT NULL,
  `startDate` date NOT NULL,
  `endDate` date NOT NULL,
  `headId` int unsigned NOT NULL,
  `assistantId` int unsigned NOT NULL,
  PRIMARY KEY (`id`),
  CONSTRAINT `Module_FK_headId` FOREIGN KEY (`headId`) REFERENCES `Person` (`id`),
  CONSTRAINT `Module_FK_assistantId` FOREIGN KEY (`assistantId`) REFERENCES `Person` (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=2 DEFAULT CHARSET=utf8;

DROP TABLE IF EXISTS `Course`;
CREATE TABLE `Course` (
  `id` int unsigned NOT NULL AUTO_INCREMENT,
  `name` varchar(50) NOT NULL,
  `shortName` varchar(20) NOT NULL,
  `moduleId` int unsigned NOT NULL,
  `professorId` int unsigned NOT NULL,
  `weight` float NOT NULL,
  PRIMARY KEY (`id`),
  CONSTRAINT `Course_FK_moduleId` FOREIGN KEY (`moduleId`) REFERENCES `Module` (`id`),
  CONSTRAINT `Course_FK_professorId` FOREIGN KEY (`professorId`) REFERENCES `Person` (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=3 DEFAULT CHARSET=utf8;

DROP TABLE IF EXISTS `Registration`;
CREATE TABLE `Registration` (
  `studentId` int unsigned NOT NULL,
  `moduleId` int unsigned NOT NULL,
  PRIMARY KEY `Registration_PK` (`studentId`,`moduleId`),
  CONSTRAINT `Registration_FK_studentId` FOREIGN KEY (`studentId`) REFERENCES `Person` (`id`),
  CONSTRAINT `Registration_FK_moduleId` FOREIGN KEY (`moduleId`) REFERENCES `Module` (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

DROP TABLE IF EXISTS `Rating`;
CREATE TABLE `Rating` (
  `studentId` int unsigned NOT NULL,
  `courseId` int unsigned NOT NULL,
  `successRate` int NOT NULL,
  `version` int NOT NULL,
  PRIMARY KEY `Rating_PK` (`studentId`,`courseId`),
  CONSTRAINT `Rating_FK_studentId` FOREIGN KEY (`studentId`) REFERENCES `Person` (`id`),
  CONSTRAINT `Rating_FK_courseId` FOREIGN KEY (`courseId`) REFERENCES `Course` (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;
