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
-- Table structure for table `questionnaire_answers`
--

DROP TABLE IF EXISTS `questionnaire_answers`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `questionnaire_answers` (
  `id` bigint unsigned NOT NULL AUTO_INCREMENT,
  `mode_type` enum('erasmus','master','career') COLLATE utf8mb4_unicode_ci DEFAULT NULL,
  `question_id` bigint unsigned NOT NULL,
  `answer1` varchar(255) COLLATE utf8mb4_unicode_ci NOT NULL,
  `answer2` varchar(255) COLLATE utf8mb4_unicode_ci NOT NULL,
  `answer3` varchar(255) COLLATE utf8mb4_unicode_ci NOT NULL,
  `answer4` varchar(255) COLLATE utf8mb4_unicode_ci NOT NULL,
  PRIMARY KEY (`id`),
  KEY `question_id` (`question_id`),
  CONSTRAINT `questionnaire_answers_ibfk_1` FOREIGN KEY (`question_id`) REFERENCES `questionnaire_questions` (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=20 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `questionnaire_answers`
--

LOCK TABLES `questionnaire_answers` WRITE;
/*!40000 ALTER TABLE `questionnaire_answers` DISABLE KEYS */;
INSERT INTO `questionnaire_answers` VALUES (1,'master',1,'Ευρώπη','Αμερική','Ασία','Δεν έχω προτίμηση'),(2,'master',2,'Αγγλικά','Ελληνικά','Άλλη Ευρωπαϊκή γλώσσα (π.χ. Γερμανικά, Γαλλικά)','Δεν με απασχολεί'),(3,'master',3,'Πολύ σημαντικό (ψάχνω οικονομικές επιλογές)','Μέτρια σημαντικό','Δεν με απασχολεί ιδιαίτερα','Θέλω κορυφαίο πρόγραμμα ανεξαρτήτως κόστους'),(4,'master',4,'Πάρα πολύ (θέλω top ranked)','Αρκετά','Λίγο','Καθόλου'),(5,'master',5,'Πολύ εξειδικευμένο (specialized)','Γενικό (broad field)','Επαγγελματικό / πρακτικό (industry-focused)','Ερευνητικό (research-oriented)'),(6,'master',6,'Ναι, με internships / συνεργασίες','Ναι, αλλά όχι απαραίτητα','Όχι, με ενδιαφέρει η ακαδημαϊκή γνώση','Δεν έχω άποψη'),(7,'master',7,'Εύρεση εργασίας','Ακαδημαϊκή καριέρα (PhD)','Αλλαγή καριέρας','Ανάπτυξη δεξιοτήτων'),(8,'erasmus',1,'Βόρεια Ευρώπη','Νότια Ευρώπη','Κεντρική Ευρώπη','Δεν έχω προτίμηση'),(9,'erasmus',2,'Αγγλικά','Τοπική γλώσσα της χώρας','Συνδυασμός Αγγλικών και τοπικής','Δεν με απασχολεί'),(10,'erasmus',3,'Πολύ σημαντική (χρειάζομαι υψηλή επιδότηση)','Μέτρια σημαντική','Λίγο σημαντική','Καθόλου σημαντική'),(11,'erasmus',4,'Πολύ υψηλού κύρους (top ranked)','Καλό αλλά όχι απαραίτητα top','Δεν με ενδιαφέρει το ranking','Θέλω εύκολα μαθήματα'),(12,'erasmus',5,'Μεγάλη πόλη (πολλές δραστηριότητες)','Μεσαία πόλη','Μικρή/ήσυχη πόλη','Δεν έχω προτίμηση'),(13,'erasmus',6,'Πάρα πολύ (πάρτι, social life)','Αρκετά','Λίγο','Καθόλου'),(14,'erasmus',7,'Διασκέδαση & εμπειρίες','Ακαδημαϊκή εξέλιξη','Ταξίδια & νέες κουλτούρες','Διεθνές δίκτυο / γνωριμίες'),(15,'career',1,'Software Development','Data / AI','Cybersecurity / Networks','Δεν είμαι σίγουρος'),(16,'career',2,'Πολύ σημαντικός (top priority)','Αρκετά σημαντικός','Μέτρια σημαντικός','Δεν είναι προτεραιότητα'),(17,'career',3,'Ναι σίγουρα','Πιθανότατα ναι','Μάλλον όχι','Όχι'),(18,'career',4,'Ελλάδα','Ευρώπη','Εξωτερικό (παγκόσμια)','Remote / δεν έχει σημασία'),(19,'career',5,'Υψηλός μισθός','Work-life balance','Δημιουργικότητα','Σταθερότητα');
/*!40000 ALTER TABLE `questionnaire_answers` ENABLE KEYS */;
UNLOCK TABLES;
/*!40103 SET TIME_ZONE=@OLD_TIME_ZONE */;

/*!40101 SET SQL_MODE=@OLD_SQL_MODE */;
/*!40014 SET FOREIGN_KEY_CHECKS=@OLD_FOREIGN_KEY_CHECKS */;
/*!40014 SET UNIQUE_CHECKS=@OLD_UNIQUE_CHECKS */;
/*!40101 SET CHARACTER_SET_CLIENT=@OLD_CHARACTER_SET_CLIENT */;
/*!40101 SET CHARACTER_SET_RESULTS=@OLD_CHARACTER_SET_RESULTS */;
/*!40101 SET COLLATION_CONNECTION=@OLD_COLLATION_CONNECTION */;
/*!40111 SET SQL_NOTES=@OLD_SQL_NOTES */;

-- Dump completed on 2026-04-14 23:19:45
