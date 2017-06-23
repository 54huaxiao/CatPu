/**
 * @Author: zx
 * @Date:   2017-05-13
 * @Email:  yangzx8@mail2.sysu.edu.cn
 * @Last modified by:   zx
 * @Last modified time: 2017-05-13
 */

/**
 * 此文件是数据库文件，可安装mysql对其操作
 */

SET SQL_MODE = "NO_AUTO_VALUE_ON_ZERO";
SET time_zone = "+00:00";

--
-- Database: `catpudata`
--

-- --------------------------------------------------------

--
-- 表的结构 `userlist`
--

CREATE TABLE IF NOT EXISTS `userlist` (
  `username` varchar(20) CHARACTER SET utf8 COLLATE utf8_unicode_ci NOT NULL,
  `tel` varchar(16) CHARACTER SET utf8 COLLATE utf8_unicode_ci DEFAULT NULL,
  `email` varchar(60) CHARACTER SET utf8 COLLATE utf8_unicode_ci DEFAULT NULL,
  `password` varchar(40) CHARACTER SET utf8 COLLATE utf8_unicode_ci DEFAULT NULL,
  `Registertime` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP,
  PRIMARY KEY (`username`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

--
-- 转存表中的数据 `userlist`
--

CREATE TABLE IF NOT EXISTS `RunTable` (
	`id` int NOT NULL AUTO_INCREMENT,
	`date` varchar(20) CHARACTER SET utf8 COLLATE utf8_unicode_ci NOT NULL,
	`time` varchar(20) CHARACTER SET utf8 COLLATE utf8_unicode_ci NOT NULL,
	`distance` varchar(20) CHARACTER SET utf8 COLLATE utf8_unicode_ci NOT NULL,
	`username` varchar(20) CHARACTER SET utf8 COLLATE utf8_unicode_ci NOT NULL,
	`_order` int NOT NULL,
	PRIMARY KEY (`id`),
	KEY `username` (`username`),
	CONSTRAINT `username` FOREIGN KEY (`username`) REFERENCES `userlist` (`username`) ON DELETE CASCADE ON UPDATE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

CREATE TABLE IF NOT EXISTS `MapTable` (
	`id` int NOT NULL AUTO_INCREMENT,
	`_order` int NOT NULL,
	`latitude` double NOT NULL,
	`longitude` double NOT NULL,
	PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;
