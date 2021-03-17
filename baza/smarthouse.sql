CREATE DATABASE  IF NOT EXISTS `smarthouse` /*!40100 DEFAULT CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci */ /*!80016 DEFAULT ENCRYPTION='N' */;
USE `smarthouse`;
-- MySQL dump 10.13  Distrib 8.0.22, for Win64 (x86_64)
--
-- Host: localhost    Database: smarthouse
-- ------------------------------------------------------
-- Server version	8.0.22

/*!40101 SET @OLD_CHARACTER_SET_CLIENT=@@CHARACTER_SET_CLIENT */;
/*!40101 SET @OLD_CHARACTER_SET_RESULTS=@@CHARACTER_SET_RESULTS */;
/*!40101 SET @OLD_COLLATION_CONNECTION=@@COLLATION_CONNECTION */;
/*!50503 SET NAMES utf8 */;
/*!40103 SET @OLD_TIME_ZONE=@@TIME_ZONE */;
/*!40103 SET TIME_ZONE='+00:00' */;
/*!40014 SET @OLD_UNIQUE_CHECKS=@@UNIQUE_CHECKS, UNIQUE_CHECKS=0 */;
/*!40014 SET @OLD_FOREIGN_KEY_CHECKS=@@FOREIGN_KEY_CHECKS, FOREIGN_KEY_CHECKS=0 */;
/*!40101 SET @OLD_SQL_MODE=@@SQL_MODE, SQL_MODE='NO_AUTO_VALUE_ON_ZERO' */;
/*!40111 SET @OLD_SQL_NOTES=@@SQL_NOTES, SQL_NOTES=0 */;

--
-- Table structure for table `alarm`
--

DROP TABLE IF EXISTS `alarm`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `alarm` (
  `IdAl` int NOT NULL AUTO_INCREMENT,
  `Time` datetime NOT NULL,
  `Period` int DEFAULT NULL,
  `Active` tinyint NOT NULL,
  `IdUsr` int NOT NULL,
  PRIMARY KEY (`IdAl`),
  KEY `FK_alarm_IdUsr_idx` (`IdUsr`),
  CONSTRAINT `FK_alarm_IdUsr` FOREIGN KEY (`IdUsr`) REFERENCES `user` (`IdUsr`) ON DELETE CASCADE
) ENGINE=InnoDB AUTO_INCREMENT=38 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `alarm`
--

LOCK TABLES `alarm` WRITE;
/*!40000 ALTER TABLE `alarm` DISABLE KEYS */;
INSERT INTO `alarm` VALUES (1,'2021-02-13 14:15:46',0,1,3),(2,'2021-02-12 23:13:57',0,0,5),(3,'2021-02-21 18:50:52',1,1,3),(5,'2021-02-09 18:59:15',3,0,4),(6,'2021-02-08 19:09:37',2,0,5),(8,'2021-02-07 12:04:48',0,0,5),(9,'2021-02-14 20:57:46',0,1,4),(10,'2021-02-14 20:59:04',0,0,4),(11,'2021-02-22 21:02:24',5,1,4),(12,'2021-02-11 19:00:00',0,0,3),(13,'2021-02-11 20:18:00',0,1,3),(20,'2021-02-20 12:32:40',0,1,6),(21,'2021-02-15 12:34:24',2,0,6),(22,'2021-02-20 12:38:55',0,1,6),(23,'2021-02-13 12:49:20',0,0,6),(24,'2021-02-23 12:51:28',5,1,6),(25,'2021-02-13 18:54:00',0,0,4),(26,'2021-02-20 20:40:08',0,1,4),(27,'2021-02-13 17:00:00',0,0,4),(28,'2021-02-21 11:03:25',0,1,6),(29,'2021-02-21 11:05:15',7,0,6),(34,'2021-02-14 11:58:54',0,0,6),(35,'2021-02-14 12:01:30',0,1,6),(36,'2021-02-27 18:16:58',0,1,10),(37,'2021-02-25 18:18:40',5,1,10);
/*!40000 ALTER TABLE `alarm` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `event`
--

DROP TABLE IF EXISTS `event`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `event` (
  `IdEv` int NOT NULL AUTO_INCREMENT,
  `Name` varchar(45) NOT NULL,
  `Start` datetime NOT NULL,
  `Duration` int NOT NULL,
  `IdLoc` int NOT NULL,
  `IdUsr` int NOT NULL,
  PRIMARY KEY (`IdEv`),
  KEY `FK_event_IdLoc_idx` (`IdLoc`),
  KEY `FK_event_IdUsr_idx` (`IdUsr`),
  CONSTRAINT `FK_event_IdLoc` FOREIGN KEY (`IdLoc`) REFERENCES `location` (`IdLoc`) ON DELETE CASCADE,
  CONSTRAINT `FK_event_IdUsr` FOREIGN KEY (`IdUsr`) REFERENCES `user` (`IdUsr`) ON DELETE CASCADE
) ENGINE=InnoDB AUTO_INCREMENT=41 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `event`
--

LOCK TABLES `event` WRITE;
/*!40000 ALTER TABLE `event` DISABLE KEYS */;
INSERT INTO `event` VALUES (3,'Basketball','2021-02-15 16:00:00',120,5,3),(7,'Friends','2021-02-15 20:00:00',180,2,3),(9,'Tennis','2021-02-10 14:40:00',90,2,3),(22,'Watching TV','2021-02-10 13:00:00',60,1,3),(24,'Running','2021-02-12 07:00:00',45,1,3),(25,'Swimming','2021-02-15 18:15:00',30,1,3),(28,'Walking','2021-02-20 13:00:00',90,1,3),(29,'Laughing','2021-02-20 16:00:00',60,2,3),(31,'Test','2021-02-11 19:00:00',60,2,3),(33,'Test2','2021-02-11 20:40:00',120,5,3),(34,'Study','2021-02-13 17:00:00',90,2,4),(36,'Football','2021-02-13 19:30:00',45,8,4),(37,'Friends','2021-02-13 22:00:00',180,1,4);
/*!40000 ALTER TABLE `event` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `location`
--

DROP TABLE IF EXISTS `location`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `location` (
  `IdLoc` int NOT NULL AUTO_INCREMENT,
  `Name` varchar(45) NOT NULL,
  `Longitude` double NOT NULL,
  `Latitude` double NOT NULL,
  PRIMARY KEY (`IdLoc`)
) ENGINE=InnoDB AUTO_INCREMENT=14 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `location`
--

LOCK TABLES `location` WRITE;
/*!40000 ALTER TABLE `location` DISABLE KEYS */;
INSERT INTO `location` VALUES (1,'Vladimira Popovica 46',20.42745978282128,44.80469718746254),(2,'Kosovska 27',20.279786255840676,44.79606317386422),(4,'Bele Bartoka 48a',20.45239981760338,44.876901519966815),(5,'Cara Dusana 105',20.39401323861414,44.85230733194561),(8,'Koste Nadja 31',20.536707916320008,44.793967471405345),(9,'Kneza Sime Markovica',20.196700160344765,44.65710419238607),(13,'Milana Tankosica 31',20.4841070399427,44.8113125524606);
/*!40000 ALTER TABLE `location` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `songplayed`
--

DROP TABLE IF EXISTS `songplayed`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `songplayed` (
  `IdUsr` int NOT NULL,
  `Song` varchar(45) NOT NULL,
  PRIMARY KEY (`IdUsr`,`Song`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `songplayed`
--

LOCK TABLES `songplayed` WRITE;
/*!40000 ALTER TABLE `songplayed` DISABLE KEYS */;
INSERT INTO `songplayed` VALUES (3,'Drake - God\'s Plan'),(3,'Kendrick Lamar - DNA'),(3,'Lil Uzi Vert - XO Tour Llif3'),(4,'Katy Perry - ROAR'),(4,'The Weeknd - Blinding Lights'),(4,'Wiz Khalifa - See You Again'),(5,'Kendrick Lamar - Swimming Pools'),(6,'21 Savage - A lot'),(6,'21 Savage - Bank Account'),(6,'Aitch - Taste'),(6,'Ed Sheeran - Shape of You'),(6,'Post Malone - Better Now'),(6,'Rihanna - Diamonds'),(6,'Shakira - Waka Waka'),(6,'Stormzy - Vossi Bop'),(6,'Travis Scott - Goosebumps'),(6,'Travis Scott - Highest in the Room'),(6,'Travis Scott - Sicko Mode'),(10,'21 Savage - A lot'),(10,'21 Savage - Bank Account'),(10,'Desiigner - Panda'),(10,'The Weeknd - Starboy');
/*!40000 ALTER TABLE `songplayed` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `user`
--

DROP TABLE IF EXISTS `user`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `user` (
  `IdUsr` int NOT NULL AUTO_INCREMENT,
  `Name` varchar(45) NOT NULL,
  `Username` varchar(45) NOT NULL,
  `Password` varchar(45) NOT NULL,
  `AlarmSongName` varchar(45) NOT NULL,
  `AlarmSongUri` tinytext NOT NULL,
  `IdLoc` int NOT NULL,
  PRIMARY KEY (`IdUsr`),
  UNIQUE KEY `Username_UNIQUE` (`Username`),
  KEY `FK_user_IdLoc_idx` (`IdLoc`),
  CONSTRAINT `FK_user_IdLoc` FOREIGN KEY (`IdLoc`) REFERENCES `location` (`IdLoc`) ON DELETE CASCADE
) ENGINE=InnoDB AUTO_INCREMENT=11 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `user`
--

LOCK TABLES `user` WRITE;
/*!40000 ALTER TABLE `user` DISABLE KEYS */;
INSERT INTO `user` VALUES (3,'Andrej Jokic','andrejjokic','andrej123','Kendrick Lamar - DNA','https://www.youtube.com/watch?v=NLZRYQMLDW4&ab_channel=KendrickLamarVEVO',1),(4,'Ana Milicevic','anamilicevic','ana123','The Weeknd - Blinding Lights','https://www.youtube.com/watch?v=fHI8X4OXluQ&ab_channel=TheWeekndVEVO',2),(5,'Nikola Krstic','nikolakrstic','nikola123','Kendrick Lamar - Swimming Pools','https://www.youtube.com/watch?v=B5YNiCfWC3A&ab_channel=KendrickLamarVEVO',4),(6,'Milos Tasic','milostasic','milos123','Post Malone - Better Now','https://www.youtube.com/watch?v=UYwF-jdcVjY&ab_channel=PostMaloneVEVO',8),(10,'Jovan Mitrasinovic','jovanmitrasinovic','jovan123','21 Savage - A lot','https://www.youtube.com/watch?v=DmWWqogr_r8&ab_channel=21SavageVEVO',13);
/*!40000 ALTER TABLE `user` ENABLE KEYS */;
UNLOCK TABLES;
/*!40103 SET TIME_ZONE=@OLD_TIME_ZONE */;

/*!40101 SET SQL_MODE=@OLD_SQL_MODE */;
/*!40014 SET FOREIGN_KEY_CHECKS=@OLD_FOREIGN_KEY_CHECKS */;
/*!40014 SET UNIQUE_CHECKS=@OLD_UNIQUE_CHECKS */;
/*!40101 SET CHARACTER_SET_CLIENT=@OLD_CHARACTER_SET_CLIENT */;
/*!40101 SET CHARACTER_SET_RESULTS=@OLD_CHARACTER_SET_RESULTS */;
/*!40101 SET COLLATION_CONNECTION=@OLD_COLLATION_CONNECTION */;
/*!40111 SET SQL_NOTES=@OLD_SQL_NOTES */;

-- Dump completed on 2021-02-24 11:35:21
