CREATE DATABASE  IF NOT EXISTS `warehousecpp` /*!40100 DEFAULT CHARACTER SET utf8mb3 */ /*!80016 DEFAULT ENCRYPTION='N' */;
USE `warehousecpp`;
-- MySQL dump 10.13  Distrib 8.0.31, for Win64 (x86_64)
--
-- Host: localhost    Database: warehousecpp
-- ------------------------------------------------------
-- Server version	8.0.31

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
-- Table structure for table `companies`
--

DROP TABLE IF EXISTS `companies`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `companies` (
                             `id` int NOT NULL AUTO_INCREMENT,
                             `name` varchar(200) NOT NULL,
                             `email` varchar(100) NOT NULL,
                             `address` varchar(100) NOT NULL,
                             PRIMARY KEY (`id`),
                             UNIQUE KEY `name_UNIQUE` (`name`),
                             UNIQUE KEY `email_UNIQUE` (`email`),
                             UNIQUE KEY `address_UNIQUE` (`address`)
) ENGINE=InnoDB AUTO_INCREMENT=7 DEFAULT CHARSET=utf8mb3;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `companies`
--

LOCK TABLES `companies` WRITE;
/*!40000 ALTER TABLE `companies` DISABLE KEYS */;
INSERT INTO `companies` VALUES (1,'Delicious cookies','cookie_company@gmail.com','Ukraine, Kyiv region, Kyiv, St. Hrushevsky, №55, flat №30'),(2,'Hot iron','metal_company@gmail.com','Ukraine, Dnipro region, Dnipro, St. Budivelna, №12'),(3,'InterCerama','intercerama_company@gmail.com','Ukraine, Lviv region, Lviv, St. Hlynyana, №20'),(4,'ATB','atb_company@gmail.com','Ukraine, Kharkiv region, Kharkiv, St. Naykova, №35'),(5,'BudMaster','budMaster_company@gmail.com','Ukraine, Poltava region, Poltava, St. Sobornosty, №10'),(6,'Epicentr','epicentr_company@gmail.com','Ukraine, Poltava region, Poltava, St. Shevchenka, №56');
/*!40000 ALTER TABLE `companies` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `income_journal`
--

DROP TABLE IF EXISTS `income_journal`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `income_journal` (
                                  `id` int NOT NULL AUTO_INCREMENT,
                                  `document_number` varchar(45) NOT NULL,
                                  `items_id` int NOT NULL,
                                  `companies_id` int NOT NULL,
                                  `date` datetime NOT NULL,
                                  `price` decimal(65,4) NOT NULL,
                                  `amount` int NOT NULL,
                                  PRIMARY KEY (`id`),
                                  UNIQUE KEY `document_number_UNIQUE` (`document_number`),
                                  KEY `fk_income_journal_items1_idx` (`items_id`),
                                  KEY `fk_income_journal_companies1_idx` (`companies_id`),
                                  CONSTRAINT `fk_income_journal_companies1` FOREIGN KEY (`companies_id`) REFERENCES `companies` (`id`),
                                  CONSTRAINT `fk_income_journal_items1` FOREIGN KEY (`items_id`) REFERENCES `items` (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=9 DEFAULT CHARSET=utf8mb3;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `income_journal`
--

LOCK TABLES `income_journal` WRITE;
/*!40000 ALTER TABLE `income_journal` DISABLE KEYS */;
INSERT INTO `income_journal` VALUES (1,'in-345-645-75634',1,1,'2022-12-13 10:36:12',130.2500,12),(2,'in-345-885-99234',2,2,'2022-12-13 10:30:56',230.0000,20),(3,'in-685-115-58634',3,3,'2022-12-13 09:30:12',400.0000,80),(4,'in-570-388-12534',4,3,'2022-12-13 09:30:12',560.0000,45),(5,'in-9990-624-74831',5,2,'2022-12-13 10:31:00',670.0000,23),(6,'in-345-645-75987',1,1,'2022-12-13 11:36:34',107.5000,12),(7,'in-345-645-95687',2,2,'2022-12-13 14:30:32',220.0000,15),(8,'in-978-865-74367',5,2,'2022-12-13 14:30:32',670.0000,10);
/*!40000 ALTER TABLE `income_journal` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `items`
--

DROP TABLE IF EXISTS `items`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `items` (
                         `id` int NOT NULL AUTO_INCREMENT,
                         `vendor` varchar(20) NOT NULL,
                         `name` varchar(100) NOT NULL,
                         `unit_id` int NOT NULL,
                         `weight` decimal(65,4) NOT NULL,
                         `amount` int DEFAULT NULL,
                         `reserve_rate` int NOT NULL,
                         PRIMARY KEY (`id`),
                         UNIQUE KEY `vendor_UNIQUE` (`vendor`),
                         KEY `unit_id` (`unit_id`),
                         CONSTRAINT `items_ibfk_1` FOREIGN KEY (`unit_id`) REFERENCES `units` (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=6 DEFAULT CHARSET=utf8mb3;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `items`
--

LOCK TABLES `items` WRITE;
/*!40000 ALTER TABLE `items` DISABLE KEYS */;
INSERT INTO `items` VALUES (1,'038-123-53-365-234','Cookies with cream',1,1.0000,34,30),(2,'038-153-365-234','Nails',3,0.3500,10,50),(3,'123-365435-2674','Black Tile',4,2.1000,80,120),(4,'9785-75-623-36','White Tile',4,1.6000,45,120),(5,'96-3248-387-907','3-meter aluminium pipe',2,3.3000,8,50);
/*!40000 ALTER TABLE `items` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `outcome_journal`
--

DROP TABLE IF EXISTS `outcome_journal`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `outcome_journal` (
                                   `id` int NOT NULL AUTO_INCREMENT,
                                   `document_number` varchar(45) NOT NULL,
                                   `items_id` int NOT NULL,
                                   `companies_id` int NOT NULL,
                                   `date` datetime NOT NULL,
                                   `price` decimal(65,4) NOT NULL,
                                   `amount` int NOT NULL,
                                   PRIMARY KEY (`id`),
                                   UNIQUE KEY `document_number_UNIQUE` (`document_number`),
                                   KEY `fk_income_journal_items1_idx` (`items_id`),
                                   KEY `fk_income_journal_companies1_idx` (`companies_id`),
                                   CONSTRAINT `fk_income_journal_companies10` FOREIGN KEY (`companies_id`) REFERENCES `companies` (`id`),
                                   CONSTRAINT `fk_income_journal_items10` FOREIGN KEY (`items_id`) REFERENCES `items` (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=5 DEFAULT CHARSET=utf8mb3;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `outcome_journal`
--

LOCK TABLES `outcome_journal` WRITE;
/*!40000 ALTER TABLE `outcome_journal` DISABLE KEYS */;
INSERT INTO `outcome_journal` VALUES (1,'out-598-867-75237',1,4,'2022-12-13 12:30:56',150.5000,20),(2,'out-248-967-75235',2,5,'2022-12-13 16:30:45',260.0000,10),(3,'out-598-8536-75237',2,6,'2022-12-13 18:15:34',250.0000,15),(4,'out-524-127-07867',5,6,'2022-12-13 18:15:34',750.0000,25);
/*!40000 ALTER TABLE `outcome_journal` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `units`
--

DROP TABLE IF EXISTS `units`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `units` (
                         `id` int NOT NULL AUTO_INCREMENT,
                         `unit` varchar(45) NOT NULL,
                         PRIMARY KEY (`id`),
                         UNIQUE KEY `unit_UNIQUE` (`unit`)
) ENGINE=InnoDB AUTO_INCREMENT=5 DEFAULT CHARSET=utf8mb3;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `units`
--

LOCK TABLES `units` WRITE;
/*!40000 ALTER TABLE `units` DISABLE KEYS */;
INSERT INTO `units` VALUES (2,'apiece'),(1,'kg'),(3,'packaging'),(4,'square meter');
/*!40000 ALTER TABLE `units` ENABLE KEYS */;
UNLOCK TABLES;
/*!40103 SET TIME_ZONE=@OLD_TIME_ZONE */;

/*!40101 SET SQL_MODE=@OLD_SQL_MODE */;
/*!40014 SET FOREIGN_KEY_CHECKS=@OLD_FOREIGN_KEY_CHECKS */;
/*!40014 SET UNIQUE_CHECKS=@OLD_UNIQUE_CHECKS */;
/*!40101 SET CHARACTER_SET_CLIENT=@OLD_CHARACTER_SET_CLIENT */;
/*!40101 SET CHARACTER_SET_RESULTS=@OLD_CHARACTER_SET_RESULTS */;
/*!40101 SET COLLATION_CONNECTION=@OLD_COLLATION_CONNECTION */;
/*!40111 SET SQL_NOTES=@OLD_SQL_NOTES */;

-- Dump completed on 2022-12-15 13:55:02
