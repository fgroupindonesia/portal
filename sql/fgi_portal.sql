-- phpMyAdmin SQL Dump
-- version 4.2.7.1
-- http://www.phpmyadmin.net
--
-- Host: 127.0.0.1
-- Generation Time: Nov 25, 2020 at 11:53 AM
-- Server version: 5.5.39
-- PHP Version: 5.4.31

SET SQL_MODE = "NO_AUTO_VALUE_ON_ZERO";
SET time_zone = "+00:00";


/*!40101 SET @OLD_CHARACTER_SET_CLIENT=@@CHARACTER_SET_CLIENT */;
/*!40101 SET @OLD_CHARACTER_SET_RESULTS=@@CHARACTER_SET_RESULTS */;
/*!40101 SET @OLD_COLLATION_CONNECTION=@@COLLATION_CONNECTION */;
/*!40101 SET NAMES utf8 */;

--
-- Database: `fgi_portal`
--

-- --------------------------------------------------------

--
-- Table structure for table `data_attendance`
--

CREATE TABLE IF NOT EXISTS `data_attendance` (
`id` int(4) NOT NULL,
  `username` varchar(75) NOT NULL,
  `class_registered` varchar(75) NOT NULL,
  `status` varchar(75) NOT NULL,
  `signature` varchar(75) NOT NULL DEFAULT 'not available',
  `date_created` datetime NOT NULL,
  `date_modified` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP
) ENGINE=InnoDB  DEFAULT CHARSET=latin1 AUTO_INCREMENT=14 ;

--
-- Dumping data for table `data_attendance`
--

INSERT INTO `data_attendance` (`id`, `username`, `class_registered`, `status`, `signature`, `date_created`, `date_modified`) VALUES
(7, 'udin', 'java+web', 'hadir', '1606274767_sign0x.png', '2020-11-24 16:24:52', '2020-11-25 03:26:07'),
(9, 'udin', 'java+web', 'hadir', 'sign01.png', '2020-11-25 09:20:26', '2020-11-25 03:34:37'),
(10, 'udin', 'java desktop', 'idzin', '1606276114_coloringpen.png', '2020-11-25 09:22:48', '2020-11-25 03:48:34'),
(11, 'dddd', 'java desktop', 'idzin', '1606276688_coloringpen.png', '2020-11-25 10:58:08', '2020-11-25 03:58:08'),
(12, 'dddd', 'java desktop', 'idzin', 'not available', '2020-11-25 10:58:18', '2020-11-25 03:58:18'),
(13, 'dddd', 'java web', 'hadir', 'not available', '2020-11-25 11:32:17', '2020-11-25 04:32:17');

-- --------------------------------------------------------

--
-- Table structure for table `data_class_room`
--

CREATE TABLE IF NOT EXISTS `data_class_room` (
`id` int(4) NOT NULL,
  `instructor_id` int(4) NOT NULL,
  `name` varchar(75) NOT NULL,
  `description` varchar(75) NOT NULL,
  `date_created` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP
) ENGINE=InnoDB  DEFAULT CHARSET=latin1 AUTO_INCREMENT=5 ;

--
-- Dumping data for table `data_class_room`
--

INSERT INTO `data_class_room` (`id`, `instructor_id`, `name`, `description`, `date_created`) VALUES
(1, 1, 'java web', 'about java web', '2020-11-22 04:02:04'),
(2, 1, 'java desktop', 'about java desktop', '2020-11-22 04:02:04'),
(3, 1, 'javascript web', 'about javascript', '2020-11-22 04:02:25'),
(4, 1, 'c++', 'about c++ basic', '2020-11-22 04:02:25');

-- --------------------------------------------------------

--
-- Table structure for table `data_document`
--

CREATE TABLE IF NOT EXISTS `data_document` (
`id` int(4) NOT NULL,
  `title` varchar(75) NOT NULL,
  `description` varchar(200) NOT NULL,
  `filename` varchar(75) DEFAULT NULL,
  `username` varchar(75) NOT NULL,
  `url` varchar(200) NOT NULL,
  `date_created` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP
) ENGINE=InnoDB  DEFAULT CHARSET=latin1 AUTO_INCREMENT=5 ;

--
-- Dumping data for table `data_document`
--

INSERT INTO `data_document` (`id`, `title`, `description`, `filename`, `username`, `url`, `date_created`) VALUES
(3, 'Corel+Data', 'Corel+Data', '1605830368_CorelDRAWTechnicalSuiteX7.txt', 'asdsad', 'asd', '2020-11-19 23:59:28'),
(4, 'kuliah', 'contoh+priority', '1606286025_PRIORITY.xlsx', 'dddd', '', '2020-11-25 06:33:45');

-- --------------------------------------------------------

--
-- Table structure for table `data_history`
--

CREATE TABLE IF NOT EXISTS `data_history` (
`id` int(4) NOT NULL,
  `username` varchar(75) NOT NULL,
  `description` varchar(200) NOT NULL,
  `date_created` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP
) ENGINE=InnoDB  DEFAULT CHARSET=latin1 AUTO_INCREMENT=8 ;

--
-- Dumping data for table `data_history`
--

INSERT INTO `data_history` (`id`, `username`, `description`, `date_created`) VALUES
(1, 'asd', 'deleting product', '2020-10-21 14:13:53'),
(2, 'asd', 'paying 500 to atm bank', '2020-10-21 14:13:53'),
(3, 'asd', 'deleting charis', '2020-10-21 14:14:21'),
(4, 'asd', 'moving up freespace', '2020-10-21 14:14:21'),
(5, 'asd', 'copying document', '2020-10-21 14:14:35'),
(6, 'asd', 'deleting document locally', '2020-10-21 14:14:35'),
(7, 'ccc', 'waiting', '2020-10-21 14:14:47');

-- --------------------------------------------------------

--
-- Table structure for table `data_payment`
--

CREATE TABLE IF NOT EXISTS `data_payment` (
`id` int(4) NOT NULL,
  `username` varchar(75) NOT NULL,
  `amount` double NOT NULL,
  `method` varchar(45) NOT NULL,
  `screenshot` varchar(75) NOT NULL,
  `date_created` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP
) ENGINE=InnoDB  DEFAULT CHARSET=latin1 AUTO_INCREMENT=4 ;

--
-- Dumping data for table `data_payment`
--

INSERT INTO `data_payment` (`id`, `username`, `amount`, `method`, `screenshot`, `date_created`) VALUES
(1, 'asd', 450000, 'atm', 'asd_trf.jpg', '2020-10-21 08:36:48'),
(2, 'ccc', 500000, 'atm', 'ccc_atm.jpg', '2020-10-21 08:37:25'),
(3, 'asd', 650000, 'cash', 'asd_cash.jpg', '2020-10-21 08:37:25');

-- --------------------------------------------------------

--
-- Table structure for table `data_schedule`
--

CREATE TABLE IF NOT EXISTS `data_schedule` (
`id` int(4) NOT NULL,
  `username` varchar(75) NOT NULL,
  `day_schedule` varchar(12) NOT NULL,
  `time_schedule` varchar(12) NOT NULL,
  `class_registered` varchar(75) NOT NULL
) ENGINE=InnoDB  DEFAULT CHARSET=latin1 AUTO_INCREMENT=11 ;

--
-- Dumping data for table `data_schedule`
--

INSERT INTO `data_schedule` (`id`, `username`, `day_schedule`, `time_schedule`, `class_registered`) VALUES
(1, 'dddd', 'friday', '10:00', 'java web'),
(10, 'udin', 'monday', '12:00', 'javascript web');

-- --------------------------------------------------------

--
-- Table structure for table `data_token`
--

CREATE TABLE IF NOT EXISTS `data_token` (
`id` int(4) NOT NULL,
  `username` varchar(75) NOT NULL,
  `token` varchar(75) NOT NULL,
  `created_date` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  `expired_date` datetime NOT NULL
) ENGINE=InnoDB  DEFAULT CHARSET=latin1 AUTO_INCREMENT=26 ;

--
-- Dumping data for table `data_token`
--

INSERT INTO `data_token` (`id`, `username`, `token`, `created_date`, `expired_date`) VALUES
(2, 'a', 'b006c6156fc237f875d0235a4432295ada3e0fe2', '2020-09-23 10:09:44', '2020-09-24 17:09:44'),
(15, 'admin', 'cbdc3989249d676171b413e5182c3cae1ec7abd2', '2020-10-05 09:16:46', '2020-10-06 16:10:46'),
(16, 'asd', 'b6abe724c048a1bf628f9b8da4cb0c281e70c3cd', '2020-11-03 09:29:24', '2020-11-04 16:11:24'),
(17, 'asd', 'cfafca845dfdf818fc475e9c9251fb792317d53e', '2020-11-08 08:49:12', '2020-11-09 15:11:12'),
(18, 'asd', '01fed453c030cff07d347592c19ff3a017274434', '2020-11-16 17:39:09', '2020-11-18 00:11:09'),
(19, 'admin', '748b893551949b720db7396aa3e6f4aba1e8540a', '2020-11-16 18:43:54', '2020-11-18 01:11:54'),
(20, 'admin', '576526db4776504c4f44a43a0933f9f9eeaa9b00', '2020-11-18 07:41:40', '2020-11-19 14:11:40'),
(21, 'dddd', '6a389c090d4d94cabd72845fbc922a742245435e', '2020-11-19 02:45:35', '2020-11-20 09:11:35'),
(22, 'admin', 'a5322041aca232360577b8303ff09a107bf94449', '2020-11-19 13:31:26', '2020-11-20 20:11:26'),
(23, 'admin', '29b18f6017729b34804d073dcd2d49618a5386cc', '2020-11-22 03:50:57', '2020-11-23 10:11:57'),
(24, 'admin', '4cf5d8f290cfb698a1cab2cde3c96bf1574f030f', '2020-11-24 07:49:56', '2020-11-25 14:11:56'),
(25, 'admin', '3eb80fb7b8a63a23044bfe77ccb6e606a16a1058', '2020-11-25 07:43:19', '2020-11-26 14:11:19');

-- --------------------------------------------------------

--
-- Table structure for table `data_user`
--

CREATE TABLE IF NOT EXISTS `data_user` (
`id` int(4) NOT NULL,
  `username` varchar(75) NOT NULL,
  `pass` varchar(75) NOT NULL,
  `email` varchar(75) NOT NULL,
  `address` varchar(200) NOT NULL,
  `propic` varchar(75) NOT NULL DEFAULT 'default.png',
  `mobile` varchar(75) NOT NULL DEFAULT 'not available',
  `date_created` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP
) ENGINE=InnoDB  DEFAULT CHARSET=latin1 AUTO_INCREMENT=39 ;

--
-- Dumping data for table `data_user`
--

INSERT INTO `data_user` (`id`, `username`, `pass`, `email`, `address`, `propic`, `mobile`, `date_created`) VALUES
(1, 'admin', 'admin', 'home@home.com', 'bdg', 'default.png', 'not available', '2020-10-22 08:25:09'),
(37, 'dddd', 'ddd', 'dddd@ymail.com', 'jakarta ku kan berpetualang', '1605693156_handbrake.png', '123-123', '2020-11-25 07:00:27'),
(38, 'udin', 'udin123 baru pass', 'udin@udin.com', 'jakarta', '1606041059_pencil.png', '09128', '2020-11-25 07:44:51');

--
-- Indexes for dumped tables
--

--
-- Indexes for table `data_attendance`
--
ALTER TABLE `data_attendance`
 ADD PRIMARY KEY (`id`);

--
-- Indexes for table `data_class_room`
--
ALTER TABLE `data_class_room`
 ADD PRIMARY KEY (`id`);

--
-- Indexes for table `data_document`
--
ALTER TABLE `data_document`
 ADD PRIMARY KEY (`id`);

--
-- Indexes for table `data_history`
--
ALTER TABLE `data_history`
 ADD PRIMARY KEY (`id`);

--
-- Indexes for table `data_payment`
--
ALTER TABLE `data_payment`
 ADD PRIMARY KEY (`id`);

--
-- Indexes for table `data_schedule`
--
ALTER TABLE `data_schedule`
 ADD PRIMARY KEY (`id`);

--
-- Indexes for table `data_token`
--
ALTER TABLE `data_token`
 ADD PRIMARY KEY (`id`);

--
-- Indexes for table `data_user`
--
ALTER TABLE `data_user`
 ADD PRIMARY KEY (`id`);

--
-- AUTO_INCREMENT for dumped tables
--

--
-- AUTO_INCREMENT for table `data_attendance`
--
ALTER TABLE `data_attendance`
MODIFY `id` int(4) NOT NULL AUTO_INCREMENT,AUTO_INCREMENT=14;
--
-- AUTO_INCREMENT for table `data_class_room`
--
ALTER TABLE `data_class_room`
MODIFY `id` int(4) NOT NULL AUTO_INCREMENT,AUTO_INCREMENT=5;
--
-- AUTO_INCREMENT for table `data_document`
--
ALTER TABLE `data_document`
MODIFY `id` int(4) NOT NULL AUTO_INCREMENT,AUTO_INCREMENT=5;
--
-- AUTO_INCREMENT for table `data_history`
--
ALTER TABLE `data_history`
MODIFY `id` int(4) NOT NULL AUTO_INCREMENT,AUTO_INCREMENT=8;
--
-- AUTO_INCREMENT for table `data_payment`
--
ALTER TABLE `data_payment`
MODIFY `id` int(4) NOT NULL AUTO_INCREMENT,AUTO_INCREMENT=4;
--
-- AUTO_INCREMENT for table `data_schedule`
--
ALTER TABLE `data_schedule`
MODIFY `id` int(4) NOT NULL AUTO_INCREMENT,AUTO_INCREMENT=11;
--
-- AUTO_INCREMENT for table `data_token`
--
ALTER TABLE `data_token`
MODIFY `id` int(4) NOT NULL AUTO_INCREMENT,AUTO_INCREMENT=26;
--
-- AUTO_INCREMENT for table `data_user`
--
ALTER TABLE `data_user`
MODIFY `id` int(4) NOT NULL AUTO_INCREMENT,AUTO_INCREMENT=39;
/*!40101 SET CHARACTER_SET_CLIENT=@OLD_CHARACTER_SET_CLIENT */;
/*!40101 SET CHARACTER_SET_RESULTS=@OLD_CHARACTER_SET_RESULTS */;
/*!40101 SET COLLATION_CONNECTION=@OLD_COLLATION_CONNECTION */;
