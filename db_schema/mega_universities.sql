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
-- Table structure for table `universities`
--

DROP TABLE IF EXISTS `universities`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `universities` (
  `id` bigint unsigned NOT NULL AUTO_INCREMENT,
  `name` varchar(255) COLLATE utf8mb4_unicode_ci NOT NULL,
  `city` varchar(100) COLLATE utf8mb4_unicode_ci DEFAULT NULL,
  `country` varchar(100) COLLATE utf8mb4_unicode_ci DEFAULT NULL,
  `ranking` int DEFAULT NULL,
  `website_url` varchar(500) COLLATE utf8mb4_unicode_ci DEFAULT NULL,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=76 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `universities`
--

LOCK TABLES `universities` WRITE;
/*!40000 ALTER TABLE `universities` DISABLE KEYS */;
INSERT INTO `universities` VALUES (1,'National and Kapodistrian University of Athens - Dept. of Informatics','Athens','Greece',301,'https://www.di.uoa.gr'),(2,'Athens University of Economics and Business - Dept. of Informatics','Athens','Greece',401,'https://www.aueb.gr'),(3,'Aristotle University of Thessaloniki - Dept. of Informatics','Thessaloniki','Greece',401,'https://www.csd.auth.gr'),(4,'Technical University of Munich - Informatics','Munich','Germany',50,'https://www.in.tum.de'),(5,'TU Delft - Computer Science','Delft','Netherlands',60,'https://www.tudelft.nl'),(6,'KU Leuven - Computer Science','Leuven','Belgium',70,'https://www.kuleuven.be'),(7,'Universität Stuttgart - Computer Science','Stuttgart','Germany',251,'https://www.uni-stuttgart.de'),(8,'Uppsala University - Dept. of Information Technology','Uppsala','Sweden',120,'https://www.it.uu.se'),(9,'Sorbonne University - Computer Science','Paris','France',50,'https://www.sorbonne-universite.fr'),(10,'École Polytechnique - Computer Science','Palaiseau','France',40,'https://www.polytechnique.edu'),(11,'Université Paris-Saclay - Informatics','Paris','France',60,'https://www.universite-paris-saclay.fr'),(12,'Universidad Politécnica de Madrid - Informatics','Madrid','Spain',100,'https://www.upm.es'),(13,'University of Barcelona - Computer Science','Barcelona','Spain',110,'https://www.ub.edu'),(14,'Autonomous University of Madrid - Computer Science','Madrid','Spain',120,'https://www.uam.es'),(15,'Sapienza University of Rome - Computer Science','Rome','Italy',90,'https://www.uniroma1.it'),(16,'Politecnico di Milano - Computer Engineering','Milan','Italy',80,'https://www.polimi.it'),(17,'University of Bologna - Computer Science','Bologna','Italy',95,'https://www.unibo.it'),(18,'University of Warsaw - Computer Science','Warsaw','Poland',150,'https://www.uw.edu.pl'),(19,'Warsaw University of Technology - CS','Warsaw','Poland',160,'https://www.pw.edu.pl'),(20,'Jagiellonian University - Informatics','Krakow','Poland',170,'https://www.uj.edu.pl'),(21,'KTH Royal Institute of Technology - CS','Stockholm','Sweden',70,'https://www.kth.se'),(22,'Lund University - Computer Science','Lund','Sweden',85,'https://www.lunduniversity.lu.se'),(23,'University of Oslo - Informatics','Oslo','Norway',120,'https://www.uio.no'),(24,'NTNU - Computer Science','Trondheim','Norway',130,'https://www.ntnu.edu'),(25,'University of Bergen - Informatics','Bergen','Norway',200,'https://www.uib.no'),(26,'University of Helsinki - Computer Science','Helsinki','Finland',110,'https://www.helsinki.fi'),(27,'Aalto University - Computer Science','Espoo','Finland',90,'https://www.aalto.fi'),(28,'University of Turku - IT','Turku','Finland',180,'https://www.utu.fi'),(29,'University of Bucharest - Computer Science','Bucharest','Romania',200,'https://www.unibuc.ro'),(30,'Politehnica University of Bucharest','Bucharest','Romania',210,'https://upb.ro'),(31,'Babes-Bolyai University - CS','Cluj-Napoca','Romania',220,'https://www.ubbcluj.ro'),(32,'Taras Shevchenko National University - CS','Kyiv','Ukraine',250,'https://www.univ.kiev.ua'),(33,'Kyiv Polytechnic Institute - CS','Kyiv','Ukraine',240,'https://kpi.ua'),(34,'Lviv Polytechnic - Computer Science','Lviv','Ukraine',260,'https://lpnu.ua'),(35,'University of Oxford - Computer Science','Oxford','United Kingdom',5,'https://www.cs.ox.ac.uk'),(36,'University of Cambridge - Computer Science','Cambridge','United Kingdom',4,'https://www.cl.cam.ac.uk'),(37,'Imperial College London - Computing','London','United Kingdom',10,'https://www.imperial.ac.uk'),(38,'Lomonosov Moscow State University - CS','Moscow','Russia',80,'https://www.msu.ru'),(39,'Saint Petersburg State University - CS','Saint Petersburg','Russia',90,'https://spbu.ru'),(40,'MIPT - Computer Science','Moscow','Russia',70,'https://mipt.ru'),(41,'RWTH Aachen University - Computer Science','Aachen','Germany',100,'https://www.rwth-aachen.de'),(42,'University of Amsterdam - Computer Science','Amsterdam','Netherlands',55,'https://www.uva.nl'),(43,'Eindhoven University of Technology - CS','Eindhoven','Netherlands',75,'https://www.tue.nl'),(44,'Ghent University - Computer Science','Ghent','Belgium',90,'https://www.ugent.be'),(45,'Université catholique de Louvain - CS','Louvain-la-Neuve','Belgium',110,'https://www.uclouvain.be'),(46,'National Technical University of Athens - Electrical and Computer Engineering','Athens','Greece',150,'https://www.ece.ntua.gr'),(47,'University of Patras - Computer Engineering and Informatics','Patras','Greece',501,'https://www.ceid.upatras.gr'),(48,'University of Crete - Computer Science','Heraklion','Greece',401,'https://www.csd.uoc.gr'),(49,'University of Ioannina - Computer Science and Engineering','Ioannina','Greece',601,'https://www.cse.uoi.gr'),(50,'University of Macedonia - Applied Informatics','Thessaloniki','Greece',801,'https://www.uom.gr/dai'),(51,'University of Thessaly - Electrical and Computer Engineering','Volos','Greece',801,'https://www.e-ce.uth.gr'),(52,'Technical University of Crete - Electrical and Computer Engineering','Chania','Greece',801,'https://www.ece.tuc.gr'),(53,'Democritus University of Thrace - Electrical and Computer Engineering','Xanthi','Greece',901,'https://www.ee.duth.gr'),(54,'University of the Aegean - Information and Communication Systems Engineering','Karlovasi','Greece',1001,'https://www.icsd.aegean.gr'),(55,'Harokopio University - Informatics and Telematics','Athens','Greece',801,'https://www.dit.hua.gr'),(56,'University of Peloponnese - Informatics and Telecommunications','Tripoli','Greece',1001,'https://dit.uop.gr'),(57,'University of West Attica - Informatics and Computer Engineering','Athens','Greece',1001,'https://ice.uniwa.gr'),(58,'Ionian University - Informatics','Corfu','Greece',1201,'https://di.ionio.gr'),(59,'International Hellenic University - Computer Science','Kavala','Greece',1201,'https://www.cs.ihu.gr'),(60,'ETH Zurich - Computer Science','Zurich','Switzerland',9,'https://inf.ethz.ch'),(61,'EPFL - Computer and Communication Sciences','Lausanne','Switzerland',14,'https://www.epfl.ch/schools/ic'),(62,'TU Wien - Informatics','Vienna','Austria',80,'https://informatik.tuwien.ac.at'),(63,'University of Vienna - Computer Science','Vienna','Austria',150,'https://informatik.univie.ac.at'),(64,'University of Copenhagen - Computer Science','Copenhagen','Denmark',100,'https://di.ku.dk'),(65,'Technical University of Denmark (DTU) - Compute','Kongens Lyngby','Denmark',120,'https://www.compute.dtu.dk'),(66,'Trinity College Dublin - Computer Science','Dublin','Ireland',100,'https://www.scss.tcd.ie'),(67,'University College Dublin - Computer Science','Dublin','Ireland',150,'https://www.ucd.ie/cs'),(68,'Instituto Superior Técnico - Computer Science and Engineering','Lisbon','Portugal',150,'https://tecnico.ulisboa.pt'),(69,'University of Porto - Informatics Engineering','Porto','Portugal',200,'https://sigarra.up.pt/feup'),(70,'Charles University - Computer Science','Prague','Czech Republic',200,'https://www.mff.cuni.cz'),(71,'Czech Technical University in Prague - IT','Prague','Czech Republic',250,'https://fit.cvut.cz'),(72,'University of Edinburgh - Informatics','Edinburgh','United Kingdom',20,'https://www.ed.ac.uk/informatics'),(73,'University College London (UCL) - Computer Science','London','United Kingdom',18,'https://www.ucl.ac.uk/computer-science'),(74,'LMU Munich - Institute of Informatics','Munich','Germany',60,'https://www.ifi.lmu.de'),(75,'TU Berlin - Electrical Engineering and Computer Science','Berlin','Germany',70,'https://www.eecs.tu-berlin.de');
/*!40000 ALTER TABLE `universities` ENABLE KEYS */;
UNLOCK TABLES;
/*!40103 SET TIME_ZONE=@OLD_TIME_ZONE */;

/*!40101 SET SQL_MODE=@OLD_SQL_MODE */;
/*!40014 SET FOREIGN_KEY_CHECKS=@OLD_FOREIGN_KEY_CHECKS */;
/*!40014 SET UNIQUE_CHECKS=@OLD_UNIQUE_CHECKS */;
/*!40101 SET CHARACTER_SET_CLIENT=@OLD_CHARACTER_SET_CLIENT */;
/*!40101 SET CHARACTER_SET_RESULTS=@OLD_CHARACTER_SET_RESULTS */;
/*!40101 SET COLLATION_CONNECTION=@OLD_COLLATION_CONNECTION */;
/*!40111 SET SQL_NOTES=@OLD_SQL_NOTES */;

-- Dump completed on 2026-04-14 23:19:43
