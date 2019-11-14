-- MySQL dump 10.13  Distrib 5.7.27, for Linux (x86_64)
--
-- Host: 127.0.0.1    Database: db_test
-- ------------------------------------------------------
-- Server version	5.7.27-0ubuntu0.18.04.1

/*!40101 SET @OLD_CHARACTER_SET_CLIENT=@@CHARACTER_SET_CLIENT */;
/*!40101 SET @OLD_CHARACTER_SET_RESULTS=@@CHARACTER_SET_RESULTS */;
/*!40101 SET @OLD_COLLATION_CONNECTION=@@COLLATION_CONNECTION */;
/*!40101 SET NAMES utf8 */;
/*!40103 SET @OLD_TIME_ZONE=@@TIME_ZONE */;
/*!40103 SET TIME_ZONE='+00:00' */;
/*!40014 SET @OLD_UNIQUE_CHECKS=@@UNIQUE_CHECKS, UNIQUE_CHECKS=0 */;
/*!40014 SET @OLD_FOREIGN_KEY_CHECKS=@@FOREIGN_KEY_CHECKS, FOREIGN_KEY_CHECKS=0 */;
/*!40101 SET @OLD_SQL_MODE=@@SQL_MODE, SQL_MODE='NO_AUTO_VALUE_ON_ZERO' */;
/*!40111 SET @OLD_SQL_NOTES=@@SQL_NOTES, SQL_NOTES=0 */;

--
-- Table structure for table `datafetcher`
--

DROP TABLE IF EXISTS `datafetcher`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `datafetcher` (
  `id` int(11) NOT NULL AUTO_INCREMENT,
  `last_block` bigint(20) NOT NULL,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=34 DEFAULT CHARSET=latin1;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `datafetcher`
--

LOCK TABLES `datafetcher` WRITE;
/*!40000 ALTER TABLE `datafetcher` DISABLE KEYS */;
INSERT INTO `datafetcher` VALUES (33,127);
/*!40000 ALTER TABLE `datafetcher` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `hibernate_sequence`
--

DROP TABLE IF EXISTS `hibernate_sequence`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `hibernate_sequence` (
  `next_val` bigint(20) DEFAULT NULL
) ENGINE=MyISAM DEFAULT CHARSET=latin1;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `hibernate_sequence`
--

LOCK TABLES `hibernate_sequence` WRITE;
/*!40000 ALTER TABLE `hibernate_sequence` DISABLE KEYS */;
/*!40000 ALTER TABLE `hibernate_sequence` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `notif_users`
--

DROP TABLE IF EXISTS `notif_users`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `notif_users` (
  `address` varchar(45) NOT NULL,
  `api_key` varchar(45) DEFAULT NULL,
  PRIMARY KEY (`address`)
) ENGINE=InnoDB DEFAULT CHARSET=latin1;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `notif_users`
--

LOCK TABLES `notif_users` WRITE;
/*!40000 ALTER TABLE `notif_users` DISABLE KEYS */;
INSERT INTO `notif_users` VALUES ('0x0','-UhIGiv8Fyvj6QNCrseNTpeM01YRpi_2'),('0x1','AzE72uIrz-SBJe62lzlBhu8mZR2D99fW');
/*!40000 ALTER TABLE `notif_users` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `notification`
--

DROP TABLE IF EXISTS `notification`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `notification` (
  `id` varchar(45) NOT NULL,
  `to_address` varchar(45) NOT NULL,
  `timestamp` varchar(45) DEFAULT NULL,
  `sended` tinyint(4) DEFAULT '0',
  `data` varchar(500) NOT NULL,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=latin1;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `notification`
--

LOCK TABLES `notification` WRITE;
/*!40000 ALTER TABLE `notification` DISABLE KEYS */;
/*!40000 ALTER TABLE `notification` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `notification_preference`
--

DROP TABLE IF EXISTS `notification_preference`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `notification_preference` (
  `id` int(11) NOT NULL,
  `user_address` varchar(45) NOT NULL,
  `notification_service` varchar(45) NOT NULL,
  `subscription` varchar(45) NOT NULL,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=latin1;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `notification_preference`
--

LOCK TABLES `notification_preference` WRITE;
/*!40000 ALTER TABLE `notification_preference` DISABLE KEYS */;
/*!40000 ALTER TABLE `notification_preference` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `notification_service`
--

DROP TABLE IF EXISTS `notification_service`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `notification_service` (
  `id` int(11) NOT NULL,
  `type` varchar(45) NOT NULL,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=latin1;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `notification_service`
--

LOCK TABLES `notification_service` WRITE;
/*!40000 ALTER TABLE `notification_service` DISABLE KEYS */;
/*!40000 ALTER TABLE `notification_service` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `preloaded_events`
--

DROP TABLE IF EXISTS `preloaded_events`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `preloaded_events` (
  `id` int(11) NOT NULL AUTO_INCREMENT,
  `description` varchar(200) DEFAULT NULL,
  `event` varchar(500) NOT NULL,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=2 DEFAULT CHARSET=latin1;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `preloaded_events`
--

LOCK TABLES `preloaded_events` WRITE;
/*!40000 ALTER TABLE `preloaded_events` DISABLE KEYS */;
INSERT INTO `preloaded_events` VALUES (1,'open channel blab lab la','{\"type\": \"CONTRACT_EVENT\", \"topicParams\":[{\"type\": \"CONTRACT_ADDRESS\", \"value\": \"0x96463f6463771ed9f9d730986501b17127823fd2\" }, {\"type\": \"EVENT_NAME\", \"value\": \"LogSellArticle\" }, { \"type\": \"EVENT_PARAM\", \"value\": \"seller\", \"order\": 0, \"valueType\": \"Address\", \"indexed\": 1 }, { \"type\": \"EVENT_PARAM\",\"value\": \"article\", \"order\": 1, \"valueType\": \"Utf8String\", \"indexed\": 0 }, { \"type\": \"EVENT_PARAM\", \"value\": \"price\", \"order\": 2, \"valueType\": \"Uint256\",\"indexed\": 0 }]}');
/*!40000 ALTER TABLE `preloaded_events` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `raw_data`
--

DROP TABLE IF EXISTS `raw_data`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `raw_data` (
  `id` varchar(45) NOT NULL,
  `type` varchar(45) NOT NULL,
  `data` varchar(500) NOT NULL,
  `processed` tinyint(1) NOT NULL DEFAULT '0',
  `block` bigint(45) NOT NULL,
  `id_topic` int(11) DEFAULT NULL,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=latin1;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `raw_data`
--

LOCK TABLES `raw_data` WRITE;
/*!40000 ALTER TABLE `raw_data` DISABLE KEYS */;
/*!40000 ALTER TABLE `raw_data` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `subscription`
--

DROP TABLE IF EXISTS `subscription`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `subscription` (
  `id` int(11) NOT NULL AUTO_INCREMENT,
  `active_since` date DEFAULT NULL,
  `active` tinyint(4) DEFAULT '0',
  `user_address` varchar(45) NOT NULL,
  `type` int(11) DEFAULT '0',
  `state` varchar(45) NOT NULL,
  `notification_balance` int(11) NOT NULL,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=35 DEFAULT CHARSET=latin1;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `subscription`
--

LOCK TABLES `subscription` WRITE;
/*!40000 ALTER TABLE `subscription` DISABLE KEYS */;
INSERT INTO `subscription` VALUES (34,'2019-11-12',1,'0x0',1,'PENDING_PAYMENT',998);
/*!40000 ALTER TABLE `subscription` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `subscription_type`
--

DROP TABLE IF EXISTS `subscription_type`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `subscription_type` (
  `id` int(11) NOT NULL,
  `notifications` int(11) NOT NULL,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=latin1;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `subscription_type`
--

LOCK TABLES `subscription_type` WRITE;
/*!40000 ALTER TABLE `subscription_type` DISABLE KEYS */;
INSERT INTO `subscription_type` VALUES (0,100),(1,1000);
/*!40000 ALTER TABLE `subscription_type` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `test`
--

DROP TABLE IF EXISTS `test`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `test` (
  `id` varchar(255) NOT NULL,
  `testcol` varchar(255) DEFAULT NULL,
  PRIMARY KEY (`id`)
) ENGINE=MyISAM DEFAULT CHARSET=latin1;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `test`
--

LOCK TABLES `test` WRITE;
/*!40000 ALTER TABLE `test` DISABLE KEYS */;
/*!40000 ALTER TABLE `test` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `topic`
--

DROP TABLE IF EXISTS `topic`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `topic` (
  `id` int(11) NOT NULL AUTO_INCREMENT,
  `type` enum('NEW_BLOCK','NEW_TRANSACTIONS','PENDING_TRANSACTIONS','CONTRACT_EVENT') NOT NULL,
  `hash` varchar(45) NOT NULL,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=50 DEFAULT CHARSET=latin1;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `topic`
--

LOCK TABLES `topic` WRITE;
/*!40000 ALTER TABLE `topic` DISABLE KEYS */;
INSERT INTO `topic` VALUES (49,'CONTRACT_EVENT','-1832301816');
/*!40000 ALTER TABLE `topic` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `topic_params`
--

DROP TABLE IF EXISTS `topic_params`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `topic_params` (
  `id` int(11) NOT NULL AUTO_INCREMENT,
  `id_topic` int(11) NOT NULL,
  `param_type` enum('CONTRACT_ADDRESS','EVENT_NAME','EVENT_PARAM') NOT NULL,
  `value` varchar(45) NOT NULL,
  `param_order` int(11) DEFAULT '0',
  `value_type` varchar(45) DEFAULT 'string',
  `is_indexed` tinyint(4) DEFAULT '0',
  `filter` varchar(45) DEFAULT NULL,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=158 DEFAULT CHARSET=latin1;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `topic_params`
--

LOCK TABLES `topic_params` WRITE;
/*!40000 ALTER TABLE `topic_params` DISABLE KEYS */;
INSERT INTO `topic_params` VALUES (153,49,'CONTRACT_ADDRESS','0x96463f6463771ed9f9d730986501b17127823fd2',0,NULL,0,NULL),(154,49,'EVENT_NAME','LogSellArticle',0,NULL,0,NULL),(155,49,'EVENT_PARAM','seller',0,'Address',1,NULL),(156,49,'EVENT_PARAM','article',1,'Utf8String',0,NULL),(157,49,'EVENT_PARAM','price',2,'Uint256',0,NULL);
/*!40000 ALTER TABLE `topic_params` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `user_topic`
--

DROP TABLE IF EXISTS `user_topic`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `user_topic` (
  `id_topic` int(11) NOT NULL,
  `id_subscription` varchar(45) NOT NULL
) ENGINE=InnoDB DEFAULT CHARSET=latin1;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `user_topic`
--

LOCK TABLES `user_topic` WRITE;
/*!40000 ALTER TABLE `user_topic` DISABLE KEYS */;
INSERT INTO `user_topic` VALUES (49,'34');
/*!40000 ALTER TABLE `user_topic` ENABLE KEYS */;
UNLOCK TABLES;
/*!40103 SET TIME_ZONE=@OLD_TIME_ZONE */;

/*!40101 SET SQL_MODE=@OLD_SQL_MODE */;
/*!40014 SET FOREIGN_KEY_CHECKS=@OLD_FOREIGN_KEY_CHECKS */;
/*!40014 SET UNIQUE_CHECKS=@OLD_UNIQUE_CHECKS */;
/*!40101 SET CHARACTER_SET_CLIENT=@OLD_CHARACTER_SET_CLIENT */;
/*!40101 SET CHARACTER_SET_RESULTS=@OLD_CHARACTER_SET_RESULTS */;
/*!40101 SET COLLATION_CONNECTION=@OLD_COLLATION_CONNECTION */;
/*!40111 SET SQL_NOTES=@OLD_SQL_NOTES */;

-- Dump completed on 2019-11-14 11:23:15
