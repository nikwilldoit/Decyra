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
  `field_name` varchar(100) DEFAULT NULL,
  `avg_salary_no_master` int unsigned DEFAULT NULL,
  `avg_salary_with_master` int unsigned DEFAULT NULL,
  `country_name` varchar(100) NOT NULL,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=99 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `career`
--

LOCK TABLES `career` WRITE;
/*!40000 ALTER TABLE `career` DISABLE KEYS */;
INSERT INTO `career` VALUES (1,'Software Engineering',18000,24000,'Greece'),(2,'Web Development',17000,23000,'Greece'),(3,'Mobile Development',16000,22000,'Greece'),(4,'Data Science',20000,26000,'Greece'),(5,'Artificial Intelligence',22000,28000,'Greece'),(6,'Cybersecurity',19000,25000,'Greece'),(7,'Game Development',17000,23000,'Greece'),(8,'Software Engineering',50000,60000,'Germany'),(9,'Web Development',48000,58000,'Germany'),(10,'Mobile Development',47000,57000,'Germany'),(11,'Data Science',55000,70000,'Germany'),(12,'Artificial Intelligence',60000,75000,'Germany'),(13,'Cybersecurity',52000,65000,'Germany'),(14,'Game Development',48000,60000,'Germany'),(15,'Software Engineering',52000,62000,'Netherlands'),(16,'Web Development',50000,60000,'Netherlands'),(17,'Mobile Development',48000,58000,'Netherlands'),(18,'Data Science',60000,75000,'Netherlands'),(19,'Artificial Intelligence',65000,80000,'Netherlands'),(20,'Cybersecurity',55000,70000,'Netherlands'),(21,'Game Development',50000,62000,'Netherlands'),(22,'Software Engineering',48000,60000,'United Kingdom'),(23,'Web Development',46000,58000,'United Kingdom'),(24,'Mobile Development',45000,57000,'United Kingdom'),(25,'Data Science',55000,70000,'United Kingdom'),(26,'Artificial Intelligence',60000,75000,'United Kingdom'),(27,'Cybersecurity',50000,65000,'United Kingdom'),(28,'Game Development',47000,60000,'United Kingdom'),(29,'Software Engineering',42000,52000,'France'),(30,'Web Development',40000,50000,'France'),(31,'Mobile Development',39000,49000,'France'),(32,'Data Science',48000,60000,'France'),(33,'Artificial Intelligence',52000,65000,'France'),(34,'Cybersecurity',45000,56000,'France'),(35,'Game Development',40000,50000,'France'),(36,'Software Engineering',30000,38000,'Italy'),(37,'Web Development',28000,36000,'Italy'),(38,'Mobile Development',27000,35000,'Italy'),(39,'Data Science',34000,42000,'Italy'),(40,'Artificial Intelligence',36000,45000,'Italy'),(41,'Cybersecurity',32000,40000,'Italy'),(42,'Game Development',28000,36000,'Italy'),(43,'Software Engineering',32000,40000,'Spain'),(44,'Web Development',30000,38000,'Spain'),(45,'Mobile Development',29000,37000,'Spain'),(46,'Data Science',36000,45000,'Spain'),(47,'Artificial Intelligence',38000,47000,'Spain'),(48,'Cybersecurity',34000,42000,'Spain'),(49,'Game Development',30000,38000,'Spain'),(50,'Software Engineering',25000,32000,'Poland'),(51,'Web Development',23000,30000,'Poland'),(52,'Mobile Development',22000,29000,'Poland'),(53,'Data Science',28000,35000,'Poland'),(54,'Artificial Intelligence',30000,38000,'Poland'),(55,'Cybersecurity',26000,33000,'Poland'),(56,'Game Development',23000,30000,'Poland'),(57,'Software Engineering',20000,26000,'Romania'),(58,'Web Development',19000,25000,'Romania'),(59,'Mobile Development',18000,24000,'Romania'),(60,'Data Science',24000,30000,'Romania'),(61,'Artificial Intelligence',26000,32000,'Romania'),(62,'Cybersecurity',22000,28000,'Romania'),(63,'Game Development',19000,25000,'Romania'),(64,'Software Engineering',22000,28000,'Russia'),(65,'Web Development',21000,27000,'Russia'),(66,'Mobile Development',20000,26000,'Russia'),(67,'Data Science',26000,32000,'Russia'),(68,'Artificial Intelligence',28000,35000,'Russia'),(69,'Cybersecurity',24000,30000,'Russia'),(70,'Game Development',21000,27000,'Russia'),(71,'Software Engineering',18000,24000,'Ukraine'),(72,'Web Development',17000,23000,'Ukraine'),(73,'Mobile Development',16000,22000,'Ukraine'),(74,'Data Science',22000,28000,'Ukraine'),(75,'Artificial Intelligence',24000,30000,'Ukraine'),(76,'Cybersecurity',20000,26000,'Ukraine'),(77,'Game Development',17000,23000,'Ukraine'),(78,'Software Engineering',48000,60000,'Sweden'),(79,'Web Development',46000,58000,'Sweden'),(80,'Mobile Development',45000,57000,'Sweden'),(81,'Data Science',55000,70000,'Sweden'),(82,'Artificial Intelligence',60000,75000,'Sweden'),(83,'Cybersecurity',50000,65000,'Sweden'),(84,'Game Development',47000,60000,'Sweden'),(85,'Software Engineering',55000,70000,'Norway'),(86,'Web Development',53000,68000,'Norway'),(87,'Mobile Development',52000,67000,'Norway'),(88,'Data Science',62000,80000,'Norway'),(89,'Artificial Intelligence',68000,85000,'Norway'),(90,'Cybersecurity',58000,75000,'Norway'),(91,'Game Development',53000,68000,'Norway'),(92,'Software Engineering',45000,55000,'Finland'),(93,'Web Development',43000,53000,'Finland'),(94,'Mobile Development',42000,52000,'Finland'),(95,'Data Science',50000,62000,'Finland'),(96,'Artificial Intelligence',55000,68000,'Finland'),(97,'Cybersecurity',47000,58000,'Finland'),(98,'Game Development',43000,53000,'Finland');
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

-- Dump completed on 2026-04-14 23:19:44
