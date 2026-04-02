-- MySQL dump 10.13  Distrib 8.0.45, for Win64 (x86_64)
--
-- Host: 127.0.0.1    Database: mega
-- ------------------------------------------------------
-- Server version	8.0.45

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
-- Table structure for table `career`
--

DROP TABLE IF EXISTS `career`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `career` (
  `id` int unsigned NOT NULL AUTO_INCREMENT,
  `field_id` int unsigned NOT NULL,
  `avg_salary_no_master` int unsigned DEFAULT NULL,
  `avg_salary_with_master` int unsigned DEFAULT NULL,
  `country_name` varchar(100) NOT NULL,
  PRIMARY KEY (`id`),
  KEY `field_id` (`field_id`),
  CONSTRAINT `career_ibfk_1` FOREIGN KEY (`field_id`) REFERENCES `it_fields` (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=99 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `career`
--

LOCK TABLES `career` WRITE;
/*!40000 ALTER TABLE `career` DISABLE KEYS */;
INSERT INTO `career` VALUES (1,1,18000,24000,'Greece'),(2,2,17000,23000,'Greece'),(3,3,16000,22000,'Greece'),(4,4,20000,26000,'Greece'),(5,5,22000,28000,'Greece'),(6,6,19000,25000,'Greece'),(7,7,17000,23000,'Greece'),(8,1,50000,60000,'Germany'),(9,2,48000,58000,'Germany'),(10,3,47000,57000,'Germany'),(11,4,55000,70000,'Germany'),(12,5,60000,75000,'Germany'),(13,6,52000,65000,'Germany'),(14,7,48000,60000,'Germany'),(15,1,52000,62000,'Netherlands'),(16,2,50000,60000,'Netherlands'),(17,3,48000,58000,'Netherlands'),(18,4,60000,75000,'Netherlands'),(19,5,65000,80000,'Netherlands'),(20,6,55000,70000,'Netherlands'),(21,7,50000,62000,'Netherlands'),(22,1,48000,60000,'United Kingdom'),(23,2,46000,58000,'United Kingdom'),(24,3,45000,57000,'United Kingdom'),(25,4,55000,70000,'United Kingdom'),(26,5,60000,75000,'United Kingdom'),(27,6,50000,65000,'United Kingdom'),(28,7,47000,60000,'United Kingdom'),(29,1,42000,52000,'France'),(30,2,40000,50000,'France'),(31,3,39000,49000,'France'),(32,4,48000,60000,'France'),(33,5,52000,65000,'France'),(34,6,45000,56000,'France'),(35,7,40000,50000,'France'),(36,1,30000,38000,'Italy'),(37,2,28000,36000,'Italy'),(38,3,27000,35000,'Italy'),(39,4,34000,42000,'Italy'),(40,5,36000,45000,'Italy'),(41,6,32000,40000,'Italy'),(42,7,28000,36000,'Italy'),(43,1,32000,40000,'Spain'),(44,2,30000,38000,'Spain'),(45,3,29000,37000,'Spain'),(46,4,36000,45000,'Spain'),(47,5,38000,47000,'Spain'),(48,6,34000,42000,'Spain'),(49,7,30000,38000,'Spain'),(50,1,25000,32000,'Poland'),(51,2,23000,30000,'Poland'),(52,3,22000,29000,'Poland'),(53,4,28000,35000,'Poland'),(54,5,30000,38000,'Poland'),(55,6,26000,33000,'Poland'),(56,7,23000,30000,'Poland'),(57,1,20000,26000,'Romania'),(58,2,19000,25000,'Romania'),(59,3,18000,24000,'Romania'),(60,4,24000,30000,'Romania'),(61,5,26000,32000,'Romania'),(62,6,22000,28000,'Romania'),(63,7,19000,25000,'Romania'),(64,1,22000,28000,'Russia'),(65,2,21000,27000,'Russia'),(66,3,20000,26000,'Russia'),(67,4,26000,32000,'Russia'),(68,5,28000,35000,'Russia'),(69,6,24000,30000,'Russia'),(70,7,21000,27000,'Russia'),(71,1,18000,24000,'Ukraine'),(72,2,17000,23000,'Ukraine'),(73,3,16000,22000,'Ukraine'),(74,4,22000,28000,'Ukraine'),(75,5,24000,30000,'Ukraine'),(76,6,20000,26000,'Ukraine'),(77,7,17000,23000,'Ukraine'),(78,1,48000,60000,'Sweden'),(79,2,46000,58000,'Sweden'),(80,3,45000,57000,'Sweden'),(81,4,55000,70000,'Sweden'),(82,5,60000,75000,'Sweden'),(83,6,50000,65000,'Sweden'),(84,7,47000,60000,'Sweden'),(85,1,55000,70000,'Norway'),(86,2,53000,68000,'Norway'),(87,3,52000,67000,'Norway'),(88,4,62000,80000,'Norway'),(89,5,68000,85000,'Norway'),(90,6,58000,75000,'Norway'),(91,7,53000,68000,'Norway'),(92,1,45000,55000,'Finland'),(93,2,43000,53000,'Finland'),(94,3,42000,52000,'Finland'),(95,4,50000,62000,'Finland'),(96,5,55000,68000,'Finland'),(97,6,47000,58000,'Finland'),(98,7,43000,53000,'Finland');
/*!40000 ALTER TABLE `career` ENABLE KEYS */;
UNLOCK TABLES;
/*!40103 SET TIME_ZONE=@OLD_TIME_ZONE */;

/*!40101 SET SQL_MODE=@OLD_SQL_MODE */;
/*!40014 SET FOREIGN_KEY_CHECKS=@OLD_FOREIGN_KEY_CHECKS */;
/*!40014 SET UNIQUE_CHECKS=@OLD_UNIQUE_CHECKS */;
/*!40101 SET CHARACTER_SET_CLIENT=@OLD_CHARACTER_SET_CLIENT */;
/*!40101 SET CHARACTER_SET_RESULTS=@OLD_CHARACTER_SET_RESULTS */;
/*!40101 SET COLLATION_CONNECTION=@OLD_COLLATION_CONNECTION */;
/*!40111 SET SQL_NOTES=@OLD_SQL_NOTES */;

-- Dump completed on 2026-04-02 23:13:35
