-- --------------------------------------------------------
-- Host:                         127.0.0.1
-- Server version:               8.0.40 - MySQL Community Server - GPL
-- Server OS:                    Win64
-- HeidiSQL Version:             12.6.0.6765
-- --------------------------------------------------------

/*!40101 SET @OLD_CHARACTER_SET_CLIENT=@@CHARACTER_SET_CLIENT */;
/*!40101 SET NAMES utf8 */;
/*!50503 SET NAMES utf8mb4 */;
/*!40103 SET @OLD_TIME_ZONE=@@TIME_ZONE */;
/*!40103 SET TIME_ZONE='+00:00' */;
/*!40014 SET @OLD_FOREIGN_KEY_CHECKS=@@FOREIGN_KEY_CHECKS, FOREIGN_KEY_CHECKS=0 */;
/*!40101 SET @OLD_SQL_MODE=@@SQL_MODE, SQL_MODE='NO_AUTO_VALUE_ON_ZERO' */;
/*!40111 SET @OLD_SQL_NOTES=@@SQL_NOTES, SQL_NOTES=0 */;


-- Dumping database structure for smartcity360
CREATE DATABASE IF NOT EXISTS `smartcity360` /*!40100 DEFAULT CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci */ /*!80016 DEFAULT ENCRYPTION='N' */;
USE `smartcity360`;

-- Dumping structure for table smartcity360.activity_log
CREATE TABLE IF NOT EXISTS `activity_log` (
  `id` bigint NOT NULL AUTO_INCREMENT,
  `activity_type` varchar(32) COLLATE utf8mb4_unicode_ci NOT NULL,
  `icon` varchar(8) COLLATE utf8mb4_unicode_ci DEFAULT NULL,
  `message` text COLLATE utf8mb4_unicode_ci NOT NULL,
  `severity` enum('info','success','warning','error') COLLATE utf8mb4_unicode_ci DEFAULT 'info',
  `related_table` varchar(64) COLLATE utf8mb4_unicode_ci DEFAULT NULL,
  `related_id` bigint DEFAULT NULL,
  `created_at` timestamp NULL DEFAULT CURRENT_TIMESTAMP,
  PRIMARY KEY (`id`),
  KEY `idx_created` (`created_at`)
) ENGINE=InnoDB AUTO_INCREMENT=66 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- Dumping data for table smartcity360.activity_log: ~56 rows (approximately)
INSERT INTO `activity_log` (`id`, `activity_type`, `icon`, `message`, `severity`, `related_table`, `related_id`, `created_at`) VALUES
	(1, 'stream', '🔒', 'Meter SL-0421 stream encrypted & transmitted', 'success', NULL, NULL, '2026-11-02 11:28:37'),
	(2, 'energy', '⚡', 'North District energy usage reported — 1,247 kWh', 'success', NULL, NULL, '2026-11-02 11:28:37'),
	(3, 'lighting', '💡', 'Streetlight SL-N-102 restored to normal operation', 'success', NULL, NULL, '2026-11-02 11:28:37'),
	(4, 'policy', '📜', 'Policy rule "noise_limit" evaluated — passed', 'info', NULL, NULL, '2026-11-02 11:28:37'),
	(12, 'complaint', '📋', 'New complaint C-8243 (Street Lighting) routed to Supervisor', 'info', 'complaints', 1, '2026-11-03 16:24:52'),
	(13, 'complaint', '⚡', 'Complaint C-8243 escalated from Supervisor to City Manager', 'warning', 'complaints', 1, '2026-11-03 16:25:05'),
	(14, 'complaint', '⚡', 'Complaint C-8243 escalated from City Manager to Emergency Director', 'warning', 'complaints', 1, '2026-11-03 16:26:49'),
	(15, 'complaint', '📋', 'New complaint C-7137 (Noise Pollution) routed to Local Staff', 'info', 'complaints', 2, '2026-11-03 16:31:00'),
	(16, 'ZONE_COMMAND', 'GRID', 'Manual shutdown tested via ZoneDAO', 'warning', 'city_components', 2, '2026-11-06 09:30:44'),
	(17, 'ZONE_SHUTDOWN', 'GRID', 'Grid shutdown test for North District', 'warning', 'city_components', 2, '2026-11-06 09:43:39'),
	(18, 'ZONE_RESTORE', 'GRID', 'Grid restored test for North District', 'success', 'city_components', 2, '2026-11-06 09:43:39'),
	(19, 'ZONE_SHUTDOWN', 'GRID', 'Grid shutdown executed for Metro City and descendant nodes.', 'warning', 'city_components', 1, '2026-11-06 09:44:38'),
	(20, 'ZONE_QUERY', 'GRID', 'Telemetry queried for Metro City', 'info', 'city_components', 1, '2026-11-06 09:45:29'),
	(21, 'ZONE_RESTORE', 'GRID', 'Power restored for Metro City and descendant nodes.', 'success', 'city_components', 1, '2026-11-06 09:45:43'),
	(22, 'ZONE_EMERGENCY', 'GRID', 'Emergency isolation triggered for Metro City and descendant nodes.', 'error', 'city_components', 1, '2026-11-06 09:45:52'),
	(23, 'ZONE_SHUTDOWN', 'GRID', 'Grid shutdown executed for Metro City and descendant nodes.', 'warning', 'city_components', 1, '2026-11-06 09:46:09'),
	(24, 'ZONE_SHUTDOWN', 'GRID', 'Grid shutdown executed for Metro City and descendant nodes.', 'warning', 'city_components', 1, '2026-11-06 09:46:22'),
	(25, 'ZONE_RESTORE', 'GRID', 'Power restored for Metro City and descendant nodes.', 'success', 'city_components', 1, '2026-11-06 09:46:29'),
	(26, 'ZONE_SHUTDOWN', 'GRID', 'Grid shutdown executed for Metro City and descendant nodes.', 'warning', 'city_components', 1, '2026-11-06 09:46:33'),
	(27, 'ZONE_SHUTDOWN', 'GRID', 'Grid shutdown executed for Metro City and descendant nodes.', 'warning', 'city_components', 1, '2026-11-06 09:47:01'),
	(28, 'ZONE_RESTORE', 'GRID', 'Power restored for Metro City and descendant nodes.', 'success', 'city_components', 1, '2026-11-06 09:47:34'),
	(29, 'ZONE_CREATE', 'GRID', 'Added new building: Innovation Lab Block C', 'info', 'city_components', NULL, '2026-11-06 18:42:04'),
	(30, 'ZONE_SHUTDOWN', 'GRID', 'Grid shutdown executed for Metro City and descendant nodes.', 'warning', 'city_components', 1, '2026-11-07 12:55:52'),
	(31, 'ZONE_SHUTDOWN', 'GRID', 'Grid shutdown executed for Metro City and descendant nodes.', 'warning', 'city_components', 1, '2026-11-07 12:56:26'),
	(32, 'complaint', '', 'New complaint C-7854 (Street Lighting) routed to City Manager', 'info', 'complaints', 3, '2026-11-07 13:14:10'),
	(33, 'ZONE_SHUTDOWN', 'GRID', 'Shutdown verified for North District', 'warning', 'city_components', 2, '2026-11-08 05:25:32'),
	(34, 'ZONE_SHUTDOWN', 'GRID', 'Grid shutdown executed for Building N-101 and descendant nodes.', 'warning', 'city_components', 8, '2026-11-08 05:38:20'),
	(35, 'ZONE_RESTORE', 'GRID', 'Power restored for Building N-101 and descendant nodes.', 'success', 'city_components', 8, '2026-11-08 05:38:21'),
	(36, 'ZONE_RESTORE', 'GRID', 'Power restored for North District and descendant nodes.', 'success', 'city_components', 2, '2026-11-08 05:38:35'),
	(37, 'ZONE_RESTORE', 'GRID', 'Power restored for Metro City and descendant nodes.', 'success', 'city_components', 1, '2026-11-08 05:38:49'),
	(38, 'ZONE_EMERGENCY', 'GRID', 'Emergency isolation triggered for Metro City and descendant nodes.', 'error', 'city_components', 1, '2026-11-08 05:38:51'),
	(39, 'ZONE_RESTORE', 'GRID', 'Power restored for Metro City and descendant nodes.', 'success', 'city_components', 1, '2026-11-08 05:38:56'),
	(40, 'ZONE_QUERY', 'GRID', 'Telemetry queried for Metro City', 'info', 'city_components', 1, '2026-11-08 05:38:57'),
	(41, 'ZONE_SHUTDOWN', 'GRID', 'Grid shutdown executed for Green Street and descendant nodes.', 'warning', 'city_components', 4, '2026-11-08 06:18:55'),
	(42, 'ZONE_EMERGENCY', 'GRID', 'Emergency isolation triggered for Green Street and descendant nodes.', 'error', 'city_components', 4, '2026-11-08 07:42:55'),
	(43, 'ZONE_SHUTDOWN', 'GRID', 'Grid shutdown executed for Green Street and descendant nodes.', 'warning', 'city_components', 4, '2026-11-08 07:42:57'),
	(44, 'complaint', '', 'New complaint C-7046 (Public Safety) routed to City Manager', 'info', 'complaints', 4, '2026-11-08 18:53:15'),
	(45, 'complaint', '', 'New complaint C-6981 (Street Lighting) routed to Supervisor', 'info', 'complaints', 5, '2026-11-08 18:53:25'),
	(46, 'ZONE_SHUTDOWN', 'GRID', 'Grid shutdown executed for Oak Street and descendant nodes.', 'warning', 'city_components', 5, '2026-11-12 01:05:11'),
	(47, 'ZONE_SHUTDOWN', 'GRID', 'Grid shutdown executed for Building N-201 and descendant nodes.', 'warning', 'city_components', 11, '2026-11-12 01:05:20'),
	(48, 'ZONE_RESTORE', 'GRID', 'Power restored for Building N-201 and descendant nodes.', 'success', 'city_components', 11, '2026-11-12 01:05:21'),
	(49, 'ZONE_RESTORE', 'GRID', 'Power restored for Oak Street and descendant nodes.', 'success', 'city_components', 5, '2026-11-12 01:05:25'),
	(50, 'ZONE_EMERGENCY', 'GRID', 'Emergency isolation triggered for South District and descendant nodes.', 'error', 'city_components', 3, '2026-11-12 01:05:58'),
	(51, 'ZONE_RESTORE', 'GRID', 'Power restored for South District and descendant nodes.', 'success', 'city_components', 3, '2026-11-12 01:06:05'),
	(52, 'complaint', '', 'New complaint C-8761 (Street Lighting) routed to Supervisor', 'info', 'complaints', 6, '2026-11-12 01:07:31'),
	(53, 'complaint', '', 'Complaint C-8761 escalated from Supervisor to City Manager', 'warning', 'complaints', 6, '2026-11-12 01:08:38'),
	(54, 'complaint', '', 'Complaint C-6981 escalated from Supervisor to City Manager', 'warning', 'complaints', 5, '2026-11-12 01:09:41'),
	(55, 'complaint', '', 'Complaint C-6981 escalated from City Manager to Emergency Director', 'warning', 'complaints', 5, '2026-11-12 01:09:43'),
	(56, 'ZONE_SHUTDOWN', 'GRID', 'Grid shutdown executed for Oak Street and descendant nodes.', 'warning', 'city_components', 5, '2026-11-12 01:59:21'),
	(57, 'ZONE_RESTORE', 'GRID', 'Power restored for Oak Street and descendant nodes.', 'success', 'city_components', 5, '2026-11-12 01:59:28'),
	(58, 'ZONE_SHUTDOWN', 'GRID', 'Grid shutdown executed for South District and descendant nodes.', 'warning', 'city_components', 3, '2026-11-12 01:59:39'),
	(59, 'ZONE_RESTORE', 'GRID', 'Power restored for South District and descendant nodes.', 'success', 'city_components', 3, '2026-11-12 01:59:52'),
	(60, 'ZONE_SHUTDOWN', 'GRID', 'Grid shutdown executed for North District and descendant nodes.', 'warning', 'city_components', 2, '2026-11-12 02:25:53'),
	(61, 'ZONE_RESTORE', 'GRID', 'Power restored for North District and descendant nodes.', 'success', 'city_components', 2, '2026-11-12 02:26:00'),
	(62, 'complaint', '', 'New complaint C-8357 (Street Lighting) routed to Local Staff', 'info', 'complaints', 7, '2026-11-12 02:27:28'),
	(63, 'complaint', '', 'Complaint C-8357 escalated from Local Staff to Supervisor', 'warning', 'complaints', 7, '2026-11-12 02:27:50'),
	(64, 'complaint', '', 'Complaint C-8357 escalated from Supervisor to City Manager', 'warning', 'complaints', 7, '2026-11-12 02:28:09'),
	(65, 'complaint', '', 'Complaint C-8357 escalated from City Manager to Emergency Director', 'warning', 'complaints', 7, '2026-11-12 02:28:23');

-- Dumping structure for table smartcity360.announcements
CREATE TABLE IF NOT EXISTS `announcements` (
  `id` bigint NOT NULL AUTO_INCREMENT,
  `icon` varchar(8) COLLATE utf8mb4_unicode_ci DEFAULT NULL,
  `title` varchar(128) COLLATE utf8mb4_unicode_ci NOT NULL,
  `body` text COLLATE utf8mb4_unicode_ci NOT NULL,
  `published_at` timestamp NULL DEFAULT CURRENT_TIMESTAMP,
  `expires_at` timestamp NULL DEFAULT NULL,
  `active` tinyint(1) DEFAULT '1',
  PRIMARY KEY (`id`),
  KEY `idx_active` (`active`,`published_at`)
) ENGINE=InnoDB AUTO_INCREMENT=5 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- Dumping data for table smartcity360.announcements: ~3 rows (approximately)
INSERT INTO `announcements` (`id`, `icon`, `title`, `body`, `published_at`, `expires_at`, `active`) VALUES
	(1, '📢', 'Scheduled Maintenance', 'Power grid maintenance on Hill Street on June 15, 02:00–04:00.', '2026-11-02 11:28:37', NULL, 1),
	(2, '🎉', 'Community Cleanup Day', 'Join us Saturday at Green Street park, 9 AM. Refreshments provided!', '2026-11-02 11:28:37', NULL, 1),
	(3, '🛣️', 'Road Repair Update', 'Oak Street repairs complete. Thank you for your patience!', '2026-11-02 11:28:37', NULL, 1);

-- Dumping structure for table smartcity360.city_components
CREATE TABLE IF NOT EXISTS `city_components` (
  `id` bigint NOT NULL AUTO_INCREMENT,
  `parent_id` bigint DEFAULT NULL,
  `component_type` enum('city','district','street','building') COLLATE utf8mb4_unicode_ci NOT NULL,
  `name` varchar(255) COLLATE utf8mb4_unicode_ci NOT NULL,
  `address` varchar(500) COLLATE utf8mb4_unicode_ci DEFAULT NULL,
  `lat` decimal(10,8) DEFAULT NULL,
  `lng` decimal(11,8) DEFAULT NULL,
  `status` enum('active','inactive','emergency') COLLATE utf8mb4_unicode_ci DEFAULT 'active',
  `created_at` timestamp NULL DEFAULT CURRENT_TIMESTAMP,
  PRIMARY KEY (`id`),
  KEY `idx_parent` (`parent_id`),
  KEY `idx_type` (`component_type`),
  CONSTRAINT `city_components_ibfk_1` FOREIGN KEY (`parent_id`) REFERENCES `city_components` (`id`) ON DELETE CASCADE
) ENGINE=InnoDB AUTO_INCREMENT=19 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- Dumping data for table smartcity360.city_components: ~17 rows (approximately)
INSERT INTO `city_components` (`id`, `parent_id`, `component_type`, `name`, `address`, `lat`, `lng`, `status`, `created_at`) VALUES
	(1, NULL, 'city', 'Metro City', NULL, 6.92710000, 79.86120000, 'active', '2026-11-02 11:28:37'),
	(2, 1, 'district', 'North District', NULL, 6.95000000, 79.87000000, 'active', '2026-11-02 11:28:37'),
	(3, 1, 'district', 'South District', NULL, 6.90000000, 79.85000000, 'active', '2026-11-02 11:28:37'),
	(4, 2, 'street', 'Green Street', NULL, 6.95200000, 79.87200000, 'active', '2026-11-02 11:28:37'),
	(5, 2, 'street', 'Oak Street', NULL, 6.94800000, 79.86800000, 'active', '2026-11-02 11:28:37'),
	(6, 3, 'street', 'River Street', NULL, 6.90200000, 79.85200000, 'active', '2026-11-02 11:28:37'),
	(7, 3, 'street', 'Hill Street', NULL, 6.89800000, 79.84800000, 'active', '2026-11-02 11:28:37'),
	(8, 4, 'building', 'Building N-101', NULL, 6.95210000, 79.87210000, 'active', '2026-11-02 11:28:37'),
	(9, 4, 'building', 'Building N-102', NULL, 6.95220000, 79.87220000, 'active', '2026-11-02 11:28:37'),
	(10, 4, 'building', 'Building N-103', NULL, 6.95230000, 79.87230000, 'active', '2026-11-02 11:28:37'),
	(11, 5, 'building', 'Building N-201', NULL, 6.94810000, 79.86810000, 'active', '2026-11-02 11:28:37'),
	(12, 5, 'building', 'Building N-202', NULL, 6.94820000, 79.86820000, 'active', '2026-11-02 11:28:37'),
	(13, 6, 'building', 'Building S-301', NULL, 6.90210000, 79.85210000, 'active', '2026-11-02 11:28:37'),
	(14, 6, 'building', 'Building S-302', NULL, 6.90220000, 79.85220000, 'active', '2026-11-02 11:28:37'),
	(15, 7, 'building', 'Building S-401', NULL, 6.89810000, 79.84810000, 'active', '2026-11-02 11:28:37'),
	(16, 7, 'building', 'Building S-402', NULL, 6.89820000, 79.84820000, 'active', '2026-11-02 11:28:37'),
	(17, 7, 'building', 'Building S-403', NULL, 6.89830000, 79.84830000, 'active', '2026-11-02 11:28:37');

-- Dumping structure for table smartcity360.complaints
CREATE TABLE IF NOT EXISTS `complaints` (
  `id` bigint NOT NULL AUTO_INCREMENT,
  `complaint_code` varchar(32) COLLATE utf8mb4_unicode_ci NOT NULL,
  `user_id` bigint NOT NULL,
  `category` varchar(64) COLLATE utf8mb4_unicode_ci NOT NULL,
  `description` text COLLATE utf8mb4_unicode_ci NOT NULL,
  `location` varchar(255) COLLATE utf8mb4_unicode_ci DEFAULT NULL,
  `severity` int NOT NULL,
  `status` enum('pending','escalated','handled','closed') COLLATE utf8mb4_unicode_ci DEFAULT 'pending',
  `assigned_handler_id` bigint DEFAULT NULL,
  `related_light_id` bigint DEFAULT NULL,
  `submitted_at` timestamp NULL DEFAULT CURRENT_TIMESTAMP,
  `handled_at` timestamp NULL DEFAULT NULL,
  PRIMARY KEY (`id`),
  UNIQUE KEY `complaint_code` (`complaint_code`),
  KEY `assigned_handler_id` (`assigned_handler_id`),
  KEY `related_light_id` (`related_light_id`),
  KEY `idx_status` (`status`),
  KEY `idx_user` (`user_id`),
  KEY `idx_submitted` (`submitted_at`),
  CONSTRAINT `complaints_ibfk_1` FOREIGN KEY (`user_id`) REFERENCES `users` (`id`),
  CONSTRAINT `complaints_ibfk_2` FOREIGN KEY (`assigned_handler_id`) REFERENCES `complaint_handlers` (`id`),
  CONSTRAINT `complaints_ibfk_3` FOREIGN KEY (`related_light_id`) REFERENCES `streetlights` (`id`) ON DELETE SET NULL,
  CONSTRAINT `complaints_chk_1` CHECK ((`severity` between 1 and 10))
) ENGINE=InnoDB AUTO_INCREMENT=8 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- Dumping data for table smartcity360.complaints: ~7 rows (approximately)
INSERT INTO `complaints` (`id`, `complaint_code`, `user_id`, `category`, `description`, `location`, `severity`, `status`, `assigned_handler_id`, `related_light_id`, `submitted_at`, `handled_at`) VALUES
	(1, 'C-8243', 1, 'Street Lighting', 'Streetlight issue:', '', 4, 'handled', 4, NULL, '2026-11-03 16:24:52', '2026-11-03 16:27:00'),
	(2, 'C-7137', 1, 'Noise Pollution', 'cbxb', '', 1, 'handled', 1, NULL, '2026-11-03 16:31:00', '2026-11-03 16:31:09'),
	(3, 'C-7854', 1, 'Street Lighting', 'error', '', 7, 'pending', 3, NULL, '2026-11-07 13:14:10', NULL),
	(4, 'C-7046', 1, 'Public Safety', 'Power outage:', '', 7, 'pending', 3, NULL, '2026-11-08 18:53:15', NULL),
	(5, 'C-6981', 1, 'Street Lighting', 'Streetlight issue:', '', 4, 'escalated', 4, NULL, '2026-11-08 18:53:25', NULL),
	(6, 'C-8761', 1, 'Street Lighting', 'lights not working', '', 4, 'handled', 3, NULL, '2026-11-12 01:07:31', '2026-11-12 01:08:57'),
	(7, 'C-8357', 1, 'Street Lighting', 'lights off', '', 3, 'handled', 4, NULL, '2026-11-12 02:27:28', '2026-11-12 02:28:40');

-- Dumping structure for table smartcity360.complaint_handlers
CREATE TABLE IF NOT EXISTS `complaint_handlers` (
  `id` bigint NOT NULL AUTO_INCREMENT,
  `name` varchar(64) COLLATE utf8mb4_unicode_ci NOT NULL,
  `role` varchar(64) COLLATE utf8mb4_unicode_ci NOT NULL,
  `severity_min` int NOT NULL,
  `severity_max` int NOT NULL,
  `chain_order` int NOT NULL,
  `user_id` bigint DEFAULT NULL,
  PRIMARY KEY (`id`),
  KEY `user_id` (`user_id`),
  KEY `idx_order` (`chain_order`),
  CONSTRAINT `complaint_handlers_ibfk_1` FOREIGN KEY (`user_id`) REFERENCES `users` (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=5 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- Dumping data for table smartcity360.complaint_handlers: ~4 rows (approximately)
INSERT INTO `complaint_handlers` (`id`, `name`, `role`, `severity_min`, `severity_max`, `chain_order`, `user_id`) VALUES
	(1, 'Local Staff', 'Front-line officer', 1, 3, 1, 3),
	(2, 'Supervisor', 'District supervisor', 4, 6, 2, 4),
	(3, 'City Manager', 'City executive', 7, 8, 3, 5),
	(4, 'Emergency Director', 'Crisis response', 9, 10, 4, 6);

-- Dumping structure for table smartcity360.complaint_routing_log
CREATE TABLE IF NOT EXISTS `complaint_routing_log` (
  `id` bigint NOT NULL AUTO_INCREMENT,
  `complaint_id` bigint NOT NULL,
  `handler_id` bigint NOT NULL,
  `action` enum('received','passed','handled') COLLATE utf8mb4_unicode_ci NOT NULL,
  `notes` varchar(255) COLLATE utf8mb4_unicode_ci DEFAULT NULL,
  `logged_at` timestamp NULL DEFAULT CURRENT_TIMESTAMP,
  PRIMARY KEY (`id`),
  KEY `handler_id` (`handler_id`),
  KEY `idx_complaint` (`complaint_id`),
  CONSTRAINT `complaint_routing_log_ibfk_1` FOREIGN KEY (`complaint_id`) REFERENCES `complaints` (`id`) ON DELETE CASCADE,
  CONSTRAINT `complaint_routing_log_ibfk_2` FOREIGN KEY (`handler_id`) REFERENCES `complaint_handlers` (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=20 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- Dumping data for table smartcity360.complaint_routing_log: ~18 rows (approximately)
INSERT INTO `complaint_routing_log` (`id`, `complaint_id`, `handler_id`, `action`, `notes`, `logged_at`) VALUES
	(1, 1, 2, 'received', 'Initial assignment to Supervisor (Severity 4)', '2026-11-03 16:24:52'),
	(2, 1, 2, 'passed', 'Escalated to City Manager', '2026-11-03 16:25:05'),
	(3, 1, 3, 'passed', 'Escalated to Emergency Director', '2026-11-03 16:26:49'),
	(4, 1, 4, 'handled', 'Status updated to handled', '2026-11-03 16:27:00'),
	(5, 2, 1, 'received', 'Initial assignment to Local Staff (Severity 1)', '2026-11-03 16:31:00'),
	(6, 2, 1, 'handled', 'Status updated to handled', '2026-11-03 16:31:09'),
	(7, 3, 3, 'received', 'Initial assignment to City Manager (Severity 7)', '2026-11-07 13:14:10'),
	(8, 4, 3, 'received', 'Initial assignment to City Manager (Severity 7)', '2026-11-08 18:53:15'),
	(9, 5, 2, 'received', 'Initial assignment to Supervisor (Severity 4)', '2026-11-08 18:53:25'),
	(10, 6, 2, 'received', 'Initial assignment to Supervisor (Severity 4)', '2026-11-12 01:07:31'),
	(11, 6, 2, 'passed', 'Escalated to City Manager', '2026-11-12 01:08:38'),
	(12, 6, 3, 'handled', 'Status updated to handled', '2026-11-12 01:08:57'),
	(13, 5, 2, 'passed', 'Escalated to City Manager: Manual Escalation', '2026-11-12 01:09:41'),
	(14, 5, 3, 'passed', 'Escalated to Emergency Director: Manual Escalation', '2026-11-12 01:09:43'),
	(15, 7, 1, 'received', 'Initial assignment to Local Staff (Severity 3)', '2026-11-12 02:27:28'),
	(16, 7, 1, 'passed', 'Escalated to Supervisor', '2026-11-12 02:27:50'),
	(17, 7, 2, 'passed', 'Escalated to City Manager', '2026-11-12 02:28:09'),
	(18, 7, 3, 'passed', 'Escalated to Emergency Director', '2026-11-12 02:28:23'),
	(19, 7, 4, 'handled', 'Status updated to handled', '2026-11-12 02:28:40');

-- Dumping structure for table smartcity360.meters
CREATE TABLE IF NOT EXISTS `meters` (
  `id` bigint NOT NULL AUTO_INCREMENT,
  `meter_code` varchar(32) COLLATE utf8mb4_unicode_ci NOT NULL,
  `building_id` bigint NOT NULL,
  `lat` decimal(10,8) DEFAULT NULL,
  `lng` decimal(11,8) DEFAULT NULL,
  `installed_at` date DEFAULT NULL,
  PRIMARY KEY (`id`),
  UNIQUE KEY `meter_code` (`meter_code`),
  KEY `building_id` (`building_id`),
  CONSTRAINT `meters_ibfk_1` FOREIGN KEY (`building_id`) REFERENCES `city_components` (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=3 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- Dumping data for table smartcity360.meters: ~2 rows (approximately)
INSERT INTO `meters` (`id`, `meter_code`, `building_id`, `lat`, `lng`, `installed_at`) VALUES
	(1, 'METER-0142', 8, 6.95210000, 79.87210000, '2024-03-15'),
	(2, 'METER-0143', 9, 6.95220000, 79.87220000, '2024-03-15');

-- Dumping structure for table smartcity360.meter_readings
CREATE TABLE IF NOT EXISTS `meter_readings` (
  `id` bigint NOT NULL AUTO_INCREMENT,
  `meter_id` bigint NOT NULL,
  `raw_data` text COLLATE utf8mb4_unicode_ci NOT NULL,
  `processed_data` text COLLATE utf8mb4_unicode_ci,
  `applied_layers` varchar(128) COLLATE utf8mb4_unicode_ci DEFAULT NULL,
  `kwh` decimal(12,3) DEFAULT NULL,
  `voltage` decimal(6,2) DEFAULT NULL,
  `amperage` decimal(6,2) DEFAULT NULL,
  `reading_ts` timestamp NOT NULL,
  `processed_ts` timestamp NULL DEFAULT CURRENT_TIMESTAMP,
  PRIMARY KEY (`id`),
  KEY `idx_meter` (`meter_id`),
  KEY `idx_ts` (`reading_ts`),
  CONSTRAINT `meter_readings_ibfk_1` FOREIGN KEY (`meter_id`) REFERENCES `meters` (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=2 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- Dumping data for table smartcity360.meter_readings: ~0 rows (approximately)
INSERT INTO `meter_readings` (`id`, `meter_id`, `raw_data`, `processed_data`, `applied_layers`, `kwh`, `voltage`, `amperage`, `reading_ts`, `processed_ts`) VALUES
	(1, 1, 'METER-0142|kWh=12873.44|VOLT=230.1|AMP=12.7|TS=2025-06-11T09:42:00Z', 'AUTH[4A2C1B7]MTIw...', 'encryption,authentication,compression', 12873.440, 230.10, 12.70, '2025-06-11 04:12:00', '2026-11-02 11:28:37');

-- Dumping structure for table smartcity360.policies
CREATE TABLE IF NOT EXISTS `policies` (
  `id` bigint NOT NULL AUTO_INCREMENT,
  `policy_code` varchar(64) COLLATE utf8mb4_unicode_ci NOT NULL,
  `name` varchar(128) COLLATE utf8mb4_unicode_ci NOT NULL,
  `description` varchar(500) COLLATE utf8mb4_unicode_ci DEFAULT NULL,
  `dsl_text` text COLLATE utf8mb4_unicode_ci NOT NULL,
  `parsed_ast` json DEFAULT NULL,
  `category` varchar(64) COLLATE utf8mb4_unicode_ci DEFAULT NULL,
  `active` tinyint(1) DEFAULT '1',
  `created_by` bigint DEFAULT NULL,
  `created_at` timestamp NULL DEFAULT CURRENT_TIMESTAMP,
  PRIMARY KEY (`id`),
  UNIQUE KEY `policy_code` (`policy_code`),
  KEY `created_by` (`created_by`),
  CONSTRAINT `policies_ibfk_1` FOREIGN KEY (`created_by`) REFERENCES `users` (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=6 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- Dumping data for table smartcity360.policies: ~3 rows (approximately)
INSERT INTO `policies` (`id`, `policy_code`, `name`, `description`, `dsl_text`, `parsed_ast`, `category`, `active`, `created_by`, `created_at`) VALUES
	(1, 'noise_limit', 'Residential Noise Limit', 'Applies to residential and commercial zones', '(noise > 55 AND zone = "residential") OR (noise > 70 AND zone = "commercial")', NULL, 'noise', 1, 7, '2026-11-02 11:28:37'),
	(2, 'parking_hours', 'Parking Hours Restriction', 'Enforced during business hours', 'hour >= 8 AND hour <= 18', NULL, 'parking', 1, 7, '2026-11-02 11:28:37'),
	(3, 'night_restriction', 'Night Noise Restriction', 'No construction noise at night in residential', 'NOT (hour >= 22 AND zone = "residential")', NULL, 'noise', 1, 7, '2026-11-02 11:28:37');

-- Dumping structure for table smartcity360.policy_evaluations
CREATE TABLE IF NOT EXISTS `policy_evaluations` (
  `id` bigint NOT NULL AUTO_INCREMENT,
  `policy_id` bigint NOT NULL,
  `context_json` json NOT NULL,
  `result` tinyint(1) NOT NULL,
  `evaluated_by` bigint DEFAULT NULL,
  `evaluated_at` timestamp NULL DEFAULT CURRENT_TIMESTAMP,
  PRIMARY KEY (`id`),
  KEY `evaluated_by` (`evaluated_by`),
  KEY `idx_policy` (`policy_id`),
  KEY `idx_eval_ts` (`evaluated_at`),
  CONSTRAINT `policy_evaluations_ibfk_1` FOREIGN KEY (`policy_id`) REFERENCES `policies` (`id`),
  CONSTRAINT `policy_evaluations_ibfk_2` FOREIGN KEY (`evaluated_by`) REFERENCES `users` (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=120 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- Dumping data for table smartcity360.policy_evaluations: ~109 rows (approximately)
INSERT INTO `policy_evaluations` (`id`, `policy_id`, `context_json`, `result`, `evaluated_by`, `evaluated_at`) VALUES
	(4, 1, '{"zone": "residential", "noise": 48}', 1, NULL, '2026-11-08 06:24:11'),
	(5, 1, '{"hour": 14, "zone": "commercial"}', 1, NULL, '2026-11-08 06:24:12'),
	(6, 1, '{"pm25": 42, "zone": "industrial", "wind_speed": 8}', 1, NULL, '2026-11-08 06:24:15'),
	(7, 1, '{"temp": 31, "power_grid": "peak"}', 1, NULL, '2026-11-08 06:24:16'),
	(8, 1, '{"temp": 31, "power_grid": "peak"}', 1, NULL, '2026-11-08 06:24:31'),
	(9, 1, '{"zone": "residential", "noise": 48}', 1, NULL, '2026-11-08 06:25:33'),
	(10, 1, '{"temp": 31, "power_grid": "peak"}', 1, NULL, '2026-11-08 06:25:36'),
	(11, 1, '{"temp": 31, "power_grid": "peak"}', 0, NULL, '2026-11-08 06:25:45'),
	(12, 1, '{"temp": 31, "power_grid": "peak"}', 1, NULL, '2026-11-08 06:25:48'),
	(13, 1, '{"temp": 31, "power_grid": "peak"}', 0, NULL, '2026-11-08 06:25:48'),
	(14, 1, '{"temp": 31, "power_grid": "peak"}', 1, NULL, '2026-11-08 06:25:58'),
	(15, 1, '{"temp": 31, "power_grid": "peak"}', 1, NULL, '2026-11-08 06:26:07'),
	(16, 1, '{"temp": 31, "power_grid": "peak"}', 1, NULL, '2026-11-08 06:26:08'),
	(17, 1, '{"temp": 31, "power_grid": "peak"}', 1, NULL, '2026-11-08 06:26:08'),
	(18, 1, '{"temp": 31, "power_grid": "peak"}', 1, NULL, '2026-11-08 06:26:08'),
	(19, 1, '{"temp": 31, "power_grid": "peak"}', 1, NULL, '2026-11-08 06:26:11'),
	(20, 1, '{"temp": 31, "power_grid": "peak"}', 1, NULL, '2026-11-08 06:26:11'),
	(21, 1, '{"temp": 31, "power_grid": "peak"}', 1, NULL, '2026-11-08 06:26:12'),
	(22, 1, '{"temp": 31, "power_grid": "peak"}', 1, NULL, '2026-11-08 06:26:12'),
	(23, 1, '{"temp": 31, "power_grid": "peak"}', 1, NULL, '2026-11-08 06:26:13'),
	(24, 1, '{"temp": 31, "power_grid": "peak"}', 1, NULL, '2026-11-08 06:26:14'),
	(25, 1, '{"pm25": 42, "zone": "industrial", "wind_speed": 8}', 1, NULL, '2026-11-08 06:26:15'),
	(26, 1, '{"pm25": 42, "zone": "industrial", "wind_speed": 8}', 1, NULL, '2026-11-08 06:26:15'),
	(27, 1, '{"pm25": 42, "zone": "industrial", "wind_speed": 8}', 1, NULL, '2026-11-08 06:26:24'),
	(28, 1, '{"pm25": 42, "zone": "industrial", "wind_speed": 8}', 0, NULL, '2026-11-08 06:26:24'),
	(29, 1, '{"pm25": 42, "zone": "industrial", "wind_speed": 8}', 0, NULL, '2026-11-08 06:27:10'),
	(30, 1, '{"pm25": 42, "zone": "industrial", "wind_speed": 8}', 0, NULL, '2026-11-08 06:27:14'),
	(31, 1, '{"pm25": 42, "zone": "industrial", "wind_speed": 8}', 0, NULL, '2026-11-08 06:27:14'),
	(32, 1, '{"pm25": 42, "zone": "industrial", "wind_speed": 8}', 0, NULL, '2026-11-08 06:27:15'),
	(33, 1, '{"zone": "residential", "noise": 48}', 1, NULL, '2026-11-08 06:54:13'),
	(34, 1, '{"zone": "residential", "noise": 48}', 1, NULL, '2026-11-08 06:54:15'),
	(35, 1, '{"hour": 14, "zone": "commercial"}', 1, NULL, '2026-11-08 06:54:16'),
	(36, 1, '{"pm25": 42, "zone": "industrial", "wind_speed": 8}', 1, NULL, '2026-11-08 06:54:17'),
	(37, 1, '{"pm25": 42, "zone": "industrial", "wind_speed": 8}', 1, NULL, '2026-11-08 06:54:22'),
	(38, 1, '{"pm25": 42, "zone": "industrial", "wind_speed": 8}', 0, NULL, '2026-11-08 06:54:22'),
	(39, 1, '{"pm25": 42, "zone": "industrial", "wind_speed": 8}', 1, NULL, '2026-11-08 06:54:32'),
	(40, 1, '{"pm25": 42, "zone": "industrial", "wind_speed": 8}', 1, NULL, '2026-11-08 06:54:33'),
	(41, 1, '{"hour": 14, "zone": "residential", "noise": 62}', 1, NULL, '2026-11-08 07:42:35'),
	(42, 1, '{"hour": 14, "zone": "residential"}', 1, NULL, '2026-11-08 07:43:06'),
	(43, 1, '{"zone": "residential", "noise": 48}', 1, NULL, '2026-11-08 07:43:08'),
	(44, 1, '{"pm25": 42, "zone": "industrial", "wind_speed": 8}', 1, NULL, '2026-11-08 07:43:13'),
	(45, 1, '{"temp": 31, "power_grid": "peak"}', 1, NULL, '2026-11-08 07:43:28'),
	(46, 1, '{"hour": 14, "zone": "residential", "noise": 62}', 1, NULL, '2026-11-08 07:43:34'),
	(47, 1, '{"hour": 14, "zone": "residential", "noise": 62}', 1, NULL, '2026-11-08 08:09:25'),
	(48, 1, '{"temp": 31, "power_grid": "peak"}', 1, NULL, '2026-11-08 08:09:33'),
	(49, 1, '{"pm25": 42, "zone": "industrial", "wind_speed": 8}', 1, NULL, '2026-11-08 08:09:34'),
	(50, 1, '{"hour": 14, "zone": "residential"}', 1, NULL, '2026-11-08 08:09:35'),
	(51, 1, '{"hour": 14, "zone": "commercial"}', 1, NULL, '2026-11-08 08:09:35'),
	(52, 1, '{"hour": 14, "zone": "residential", "noise": 62}', 1, NULL, '2026-11-08 08:46:42'),
	(53, 1, '{"hour": 14, "zone": "residential", "noise": 62}', 1, NULL, '2026-11-08 08:48:59'),
	(54, 1, '{"pm25": 42, "zone": "industrial", "wind_speed": 8}', 1, NULL, '2026-11-08 08:49:07'),
	(55, 1, '{"hour": 14, "zone": "residential", "noise": 62}', 1, NULL, '2026-11-08 08:54:59'),
	(56, 1, '{"hour": 14, "zone": "residential", "noise": 62}', 1, NULL, '2026-11-08 14:07:36'),
	(57, 1, '{"hour": 14, "zone": "residential", "noise": 62}', 1, NULL, '2026-11-08 14:14:36'),
	(58, 1, '{"hour": 14, "zone": "residential", "noise": 62}', 1, NULL, '2026-11-08 14:43:29'),
	(59, 1, '{"hour": 14, "zone": "residential", "noise": 62}', 1, NULL, '2026-11-08 15:11:26'),
	(60, 1, '{"hour": 14, "zone": "residential", "noise": 62}', 1, NULL, '2026-11-08 15:11:42'),
	(61, 1, '{"hour": 14, "zone": "residential", "noise": 62}', 1, NULL, '2026-11-08 15:11:48'),
	(62, 1, '{"hour": 14, "zone": "residential", "noise": 62}', 1, NULL, '2026-11-08 15:11:53'),
	(63, 1, '{"hour": 14, "zone": "residential", "noise": 62}', 1, NULL, '2026-11-08 15:12:26'),
	(64, 1, '{"hour": 14, "zone": "residential", "noise": 62}', 1, NULL, '2026-11-08 15:19:56'),
	(65, 1, '{"hour": 14, "zone": "residential", "noise": 62}', 1, NULL, '2026-11-08 15:19:59'),
	(66, 1, '{"hour": 14, "zone": "residential", "noise": 62}', 1, NULL, '2026-11-08 15:20:02'),
	(67, 1, '{"hour": 14, "zone": "residential", "noise": 62}', 1, NULL, '2026-11-08 15:20:06'),
	(68, 1, '{"hour": 14, "zone": "residential", "noise": 62}', 1, NULL, '2026-11-08 15:30:55'),
	(69, 1, '{"hour": 14, "zone": "residential", "noise": 62}', 1, NULL, '2026-11-08 15:31:16'),
	(70, 1, '{"hour": 14, "zone": "residential", "noise": 62}', 1, NULL, '2026-11-08 17:08:21'),
	(71, 1, '{"hour": 14, "zone": "residential", "noise": 62}', 1, NULL, '2026-11-08 17:10:14'),
	(72, 1, '{"hour": 14, "zone": "residential", "noise": 62}', 1, NULL, '2026-11-08 17:36:30'),
	(73, 1, '{"hour": 14, "zone": "residential", "noise": 62}', 1, NULL, '2026-11-08 17:45:14'),
	(74, 1, '{"hour": 14, "zone": "residential", "noise": 62}', 1, NULL, '2026-11-08 17:51:11'),
	(75, 1, '{"temp": 31, "power_grid": "peak"}', 1, NULL, '2026-11-08 18:00:01'),
	(76, 1, '{"hour": 14, "zone": "commercial"}', 1, NULL, '2026-11-08 18:00:02'),
	(77, 1, '{"zone": "residential", "noise": 48}', 1, NULL, '2026-11-08 18:00:17'),
	(78, 1, '{"zone": "residential", "noise": 48}', 0, NULL, '2026-11-08 18:00:38'),
	(79, 1, '{"zone": "residential", "noise": 48}', 0, NULL, '2026-11-08 18:00:40'),
	(80, 1, '{"zone": "residential", "noise": 48}', 0, NULL, '2026-11-08 18:00:43'),
	(81, 1, '{"zone": "residential", "noise": 48}', 0, NULL, '2026-11-08 18:00:44'),
	(82, 1, '{"hour": 14, "zone": "residential", "noise": 62}', 1, NULL, '2026-11-08 18:42:39'),
	(83, 1, '{"hour": 14, "zone": "commercial"}', 1, NULL, '2026-11-08 18:42:49'),
	(84, 1, '{"hour": 14, "zone": "residential"}', 1, NULL, '2026-11-08 18:42:50'),
	(85, 1, '{"temp": 31, "power_grid": "peak"}', 1, NULL, '2026-11-08 18:42:52'),
	(86, 1, '{"pm25": 42, "zone": "industrial", "wind_speed": 8}', 1, NULL, '2026-11-08 18:42:53'),
	(87, 1, '{"hour": 14, "zone": "residential", "noise": 62}', 1, NULL, '2026-11-08 19:11:20'),
	(88, 1, '{"hour": 14, "zone": "residential", "noise": 62}', 1, NULL, '2026-11-09 03:18:59'),
	(89, 1, '{"hour": 14, "zone": "residential", "noise": 62}', 1, NULL, '2026-11-09 18:13:58'),
	(90, 1, '{"hour": 14, "zone": "residential", "noise": 62}', 1, NULL, '2026-11-11 16:42:58'),
	(91, 1, '{"hour": 14, "zone": "residential", "noise": 62}', 1, NULL, '2026-11-11 16:46:48'),
	(92, 1, '{"hour": 14, "zone": "residential", "noise": 62}', 1, NULL, '2026-11-12 01:09:23'),
	(93, 1, '{"hour": 14, "zone": "commercial"}', 1, NULL, '2026-11-12 01:21:10'),
	(94, 1, '{"hour": 14, "zone": "commercial"}', 1, NULL, '2026-11-12 01:21:20'),
	(95, 1, '{"hour": 14, "zone": "commercial"}', 0, NULL, '2026-11-12 01:21:20'),
	(96, 1, '{"pm25": 42, "zone": "industrial", "wind_speed": 8}', 1, NULL, '2026-11-12 01:22:49'),
	(97, 1, '{"hour": 14, "zone": "residential", "noise": 62}', 1, NULL, '2026-11-12 01:33:37'),
	(98, 1, '{"pm25": 42, "zone": "industrial", "wind_speed": 8}', 1, NULL, '2026-11-12 02:05:49'),
	(99, 1, '{"zone": "residential", "noise": 48}', 1, NULL, '2026-11-12 02:05:49'),
	(100, 1, '{"pm25": 42, "zone": "industrial", "wind_speed": 8}', 1, NULL, '2026-11-12 02:05:50'),
	(101, 1, '{"temp": 31, "power_grid": "peak"}', 1, NULL, '2026-11-12 02:05:51'),
	(102, 1, '{"hour": 14, "zone": "commercial"}', 1, NULL, '2026-11-12 02:05:51'),
	(103, 1, '{"hour": 14, "zone": "residential"}', 1, NULL, '2026-11-12 02:05:51'),
	(104, 1, '{"temp": 31, "power_grid": "peak"}', 1, NULL, '2026-11-12 02:05:53'),
	(105, 1, '{"pm25": 42, "zone": "industrial", "wind_speed": 8}', 1, NULL, '2026-11-12 02:06:56'),
	(106, 1, '{"temp": 31, "power_grid": "peak"}', 1, NULL, '2026-11-12 02:06:57'),
	(107, 1, '{"temp": 31, "power_grid": "peak"}', 1, NULL, '2026-11-12 02:07:03'),
	(108, 1, '{"temp": 31, "power_grid": "peak"}', 0, NULL, '2026-11-12 02:07:03'),
	(109, 1, '{"hour": 14, "zone": "residential", "noise": 62}', 1, NULL, '2026-11-12 02:15:23'),
	(110, 1, '{"hour": 14, "zone": "residential", "noise": 62}', 1, NULL, '2026-11-12 02:29:26'),
	(111, 1, '{"zone": "residential", "noise": 48}', 1, NULL, '2026-11-12 02:33:09'),
	(112, 1, '{"zone": "residential", "noise": 48}', 1, NULL, '2026-11-12 02:33:38'),
	(113, 1, '{"temp": 31, "power_grid": "peak"}', 1, NULL, '2026-11-12 02:33:39'),
	(114, 1, '{"hour": 14, "zone": "commercial"}', 1, NULL, '2026-11-12 02:33:39'),
	(115, 1, '{"hour": 14, "zone": "residential"}', 1, NULL, '2026-11-12 02:33:41'),
	(116, 1, '{"temp": 31, "power_grid": "peak"}', 1, NULL, '2026-11-12 02:33:42'),
	(117, 1, '{"temp": 31, "power_grid": "peak"}', 1, NULL, '2026-11-12 02:34:11'),
	(118, 1, '{"temp": 31, "power_grid": "peak"}', 0, NULL, '2026-11-12 02:34:12'),
	(119, 1, '{"hour": 14, "zone": "residential", "noise": 62}', 1, NULL, '2026-11-14 07:51:49');

-- Dumping structure for table smartcity360.streetlights
CREATE TABLE IF NOT EXISTS `streetlights` (
  `id` bigint NOT NULL AUTO_INCREMENT,
  `light_code` varchar(32) COLLATE utf8mb4_unicode_ci NOT NULL,
  `type_id` bigint NOT NULL,
  `building_id` bigint DEFAULT NULL,
  `lat` decimal(10,8) NOT NULL,
  `lng` decimal(11,8) NOT NULL,
  `is_on` tinyint(1) DEFAULT '1',
  `reported_issue` tinyint(1) DEFAULT '0',
  `last_status_change` timestamp NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  PRIMARY KEY (`id`),
  UNIQUE KEY `light_code` (`light_code`),
  KEY `building_id` (`building_id`),
  KEY `idx_type` (`type_id`),
  KEY `idx_on` (`is_on`),
  KEY `idx_issue` (`reported_issue`),
  CONSTRAINT `streetlights_ibfk_1` FOREIGN KEY (`type_id`) REFERENCES `streetlight_types` (`id`),
  CONSTRAINT `streetlights_ibfk_2` FOREIGN KEY (`building_id`) REFERENCES `city_components` (`id`) ON DELETE SET NULL
) ENGINE=InnoDB AUTO_INCREMENT=12 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- Dumping data for table smartcity360.streetlights: ~10 rows (approximately)
INSERT INTO `streetlights` (`id`, `light_code`, `type_id`, `building_id`, `lat`, `lng`, `is_on`, `reported_issue`, `last_status_change`) VALUES
	(1, 'SL-N-101', 1, 8, 6.95210000, 79.87210000, 1, 0, '2026-11-02 11:28:37'),
	(2, 'SL-N-102', 1, 9, 6.95220000, 79.87220000, 1, 0, '2026-11-02 11:28:37'),
	(3, 'SL-N-103', 1, 10, 6.95230000, 79.87230000, 1, 0, '2026-11-02 11:28:37'),
	(4, 'SL-N-201', 1, 11, 6.94810000, 79.86810000, 0, 1, '2026-11-02 11:28:37'),
	(5, 'SL-N-202', 2, 12, 6.94820000, 79.86820000, 1, 0, '2026-11-02 11:28:37'),
	(6, 'SL-S-301', 1, 13, 6.90210000, 79.85210000, 1, 0, '2026-11-02 11:28:37'),
	(7, 'SL-S-302', 1, 14, 6.90220000, 79.85220000, 1, 0, '2026-11-02 11:28:37'),
	(8, 'SL-S-401', 2, 15, 6.89810000, 79.84810000, 1, 0, '2026-11-02 11:28:37'),
	(9, 'SL-S-402', 2, 16, 6.89820000, 79.84820000, 0, 0, '2026-11-02 11:28:37'),
	(10, 'SL-S-403', 3, 17, 6.89830000, 79.84830000, 1, 0, '2026-11-02 11:28:37');

-- Dumping structure for table smartcity360.streetlight_types
CREATE TABLE IF NOT EXISTS `streetlight_types` (
  `id` bigint NOT NULL AUTO_INCREMENT,
  `type_key` varchar(64) COLLATE utf8mb4_unicode_ci NOT NULL,
  `firmware` varchar(32) COLLATE utf8mb4_unicode_ci NOT NULL,
  `color_temp_k` int NOT NULL,
  `wattage` int NOT NULL,
  `lumens` int NOT NULL,
  `protocol` varchar(32) COLLATE utf8mb4_unicode_ci NOT NULL,
  `created_at` timestamp NULL DEFAULT CURRENT_TIMESTAMP,
  PRIMARY KEY (`id`),
  UNIQUE KEY `type_key` (`type_key`)
) ENGINE=InnoDB AUTO_INCREMENT=4 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- Dumping data for table smartcity360.streetlight_types: ~3 rows (approximately)
INSERT INTO `streetlight_types` (`id`, `type_key`, `firmware`, `color_temp_k`, `wattage`, `lumens`, `protocol`, `created_at`) VALUES
	(1, 'eco-v241-4000k', 'v2.4.1-eco', 4000, 120, 12000, 'LoRaWAN', '2026-11-02 11:28:37'),
	(2, 'eco-v241-3000k', 'v2.4.1-eco', 3000, 100, 10000, 'LoRaWAN', '2026-11-02 11:28:37'),
	(3, 'led-60w', 'v2.4.1', 5000, 60, 6000, 'Zigbee', '2026-11-02 11:28:37');

-- Dumping structure for table smartcity360.system_status
CREATE TABLE IF NOT EXISTS `system_status` (
  `id` bigint NOT NULL AUTO_INCREMENT,
  `service_name` varchar(64) COLLATE utf8mb4_unicode_ci NOT NULL,
  `status` enum('operational','degraded','down') COLLATE utf8mb4_unicode_ci DEFAULT 'operational',
  `metric_value` varchar(64) COLLATE utf8mb4_unicode_ci DEFAULT NULL,
  `last_check` timestamp NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  PRIMARY KEY (`id`),
  UNIQUE KEY `service_name` (`service_name`)
) ENGINE=InnoDB AUTO_INCREMENT=7 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- Dumping data for table smartcity360.system_status: ~6 rows (approximately)
INSERT INTO `system_status` (`id`, `service_name`, `status`, `metric_value`, `last_check`) VALUES
	(1, 'Data Streams', 'operational', 'secure', '2026-11-02 11:28:37'),
	(2, 'Policy Engine', 'operational', 'running', '2026-11-02 11:28:37'),
	(3, 'Zone Network', 'operational', 'synced', '2026-11-02 11:28:37'),
	(4, 'Complaint Router', 'operational', 'active', '2026-11-02 11:28:37'),
	(5, 'Lighting Grid', 'operational', 'online', '2026-11-02 11:28:37'),
	(6, 'Memory Usage', 'operational', '24%', '2026-11-02 11:28:37');

-- Dumping structure for table smartcity360.users
CREATE TABLE IF NOT EXISTS `users` (
  `id` bigint NOT NULL AUTO_INCREMENT,
  `username` varchar(64) COLLATE utf8mb4_unicode_ci NOT NULL,
  `password_hash` varchar(255) COLLATE utf8mb4_unicode_ci NOT NULL,
  `full_name` varchar(128) COLLATE utf8mb4_unicode_ci NOT NULL,
  `email` varchar(128) COLLATE utf8mb4_unicode_ci DEFAULT NULL,
  `phone` varchar(20) COLLATE utf8mb4_unicode_ci DEFAULT NULL,
  `address` varchar(255) COLLATE utf8mb4_unicode_ci DEFAULT NULL,
  `role` enum('citizen','operator','supervisor','city_manager','emergency_director','admin') COLLATE utf8mb4_unicode_ci NOT NULL DEFAULT 'citizen',
  `active` tinyint(1) DEFAULT '1',
  `last_login` timestamp NULL DEFAULT NULL,
  `created_at` timestamp NULL DEFAULT CURRENT_TIMESTAMP,
  PRIMARY KEY (`id`),
  UNIQUE KEY `username` (`username`),
  UNIQUE KEY `email` (`email`),
  KEY `idx_role` (`role`),
  KEY `idx_email` (`email`)
) ENGINE=InnoDB AUTO_INCREMENT=8 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- Dumping data for table smartcity360.users: ~7 rows (approximately)
INSERT INTO `users` (`id`, `username`, `password_hash`, `full_name`, `email`, `phone`, `address`, `role`, `active`, `last_login`, `created_at`) VALUES
	(1, 'citizen.john', 'x', 'John Perera', 'john@example.com', '+94771234567', 'N-101, Green St', 'citizen', 1, NULL, '2026-11-02 11:28:37'),
	(2, 'citizen.mary', 'x', 'Mary Silva', 'mary@example.com', '+94772345678', 'N-201, Oak St', 'citizen', 1, NULL, '2026-11-02 11:28:37'),
	(3, 'op.metro', 'x', 'Metro Operator', 'op@metro.gov', '+94112345678', 'City Hall', 'operator', 1, NULL, '2026-11-02 11:28:37'),
	(4, 'sup.north', 'x', 'North Supervisor', 'sup@metro.gov', '+94112345679', 'North Office', 'supervisor', 1, NULL, '2026-11-02 11:28:37'),
	(5, 'mgr.city', 'x', 'City Manager', 'mgr@metro.gov', '+94112345680', 'City Hall', 'city_manager', 1, NULL, '2026-11-02 11:28:37'),
	(6, 'dir.emerg', 'x', 'Emergency Director', 'dir@metro.gov', '+94112345681', 'Emergency HQ', 'emergency_director', 1, NULL, '2026-11-02 11:28:37'),
	(7, 'admin', 'x', 'System Admin', 'admin@metro.gov', '+94112345682', 'City Hall', 'admin', 1, NULL, '2026-11-02 11:28:37');

/*!40103 SET TIME_ZONE=IFNULL(@OLD_TIME_ZONE, 'system') */;
/*!40101 SET SQL_MODE=IFNULL(@OLD_SQL_MODE, '') */;
/*!40014 SET FOREIGN_KEY_CHECKS=IFNULL(@OLD_FOREIGN_KEY_CHECKS, 1) */;
/*!40101 SET CHARACTER_SET_CLIENT=@OLD_CHARACTER_SET_CLIENT */;
/*!40111 SET SQL_NOTES=IFNULL(@OLD_SQL_NOTES, 1) */;
