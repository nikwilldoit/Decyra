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
-- Table structure for table `questionnaire_questions`
--

DROP TABLE IF EXISTS `questionnaire_questions`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `questionnaire_questions` (
  `id` bigint unsigned NOT NULL AUTO_INCREMENT,
  `mode_type` enum('erasmus','master','career') DEFAULT NULL,
  `question_id` int unsigned NOT NULL,
  `question` text NOT NULL,
  `is_active` tinyint(1) NOT NULL DEFAULT '1',
  PRIMARY KEY (`id`),
  UNIQUE KEY `uq_mode_question_order` (`mode_type`,`question_id`)
) ENGINE=InnoDB AUTO_INCREMENT=20 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `questionnaire_questions`
--

LOCK TABLES `questionnaire_questions` WRITE;
/*!40000 ALTER TABLE `questionnaire_questions` DISABLE KEYS */;
INSERT INTO `questionnaire_questions` VALUES (1,'master',1,'Σε ποια περιοχή θα ήθελες να σπουδάσεις;',1),(2,'master',2,'Ποια γλώσσα διδασκαλίας προτιμάς;',1),(3,'master',3,'Πόσο σημαντικό είναι για σένα το κόστος σπουδών και διαβίωσης;',1),(4,'master',4,'Πόσο σε ενδιαφέρει το ranking / φήμη του πανεπιστημίου;',1),(5,'master',5,'Τι είδους πρόγραμμα master σε ενδιαφέρει περισσότερο;',1),(6,'master',6,'Θέλεις το πρόγραμμα να συνδέεται με αγορά εργασίας;',1),(7,'master',7,'Ποιος είναι ο βασικός σου στόχος από το master;',1),(8,'erasmus',1,'Σε ποια περιοχή θα ήθελες να πας για Erasmus;',1),(9,'erasmus',2,'Ποια γλώσσα θα ήθελες να χρησιμοποιείς κυρίως;',1),(10,'erasmus',3,'Πόσο σημαντική είναι για σένα η οικονομική υποστήριξη (Erasmus funding);',1),(11,'erasmus',4,'Τι είδους πανεπιστήμιο θα προτιμούσες;',1),(12,'erasmus',5,'Τι είδους πόλη προτιμάς;',1),(13,'erasmus',6,'Πόσο σημαντική είναι για σένα η φοιτητική ζωή;',1),(14,'erasmus',7,'Τι θέλεις να κερδίσεις από το Erasmus;',1),(15,'career',1,'Ποιος τομέας της πληροφορικής σε ενδιαφέρει περισσότερο;',1),(16,'career',2,'Πόσο σημαντικός είναι για σένα ο μισθός;',1),(17,'career',3,'Σκέφτεσαι να κάνεις master;',1),(18,'career',4,'Πού θα ήθελες να εργαστείς;',1),(19,'career',5,'Τι είναι πιο σημαντικό για σένα σε μια καριέρα;',1);
/*!40000 ALTER TABLE `questionnaire_questions` ENABLE KEYS */;
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
