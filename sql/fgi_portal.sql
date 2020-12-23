-- phpMyAdmin SQL Dump
-- version 4.2.7.1
-- http://www.phpmyadmin.net
--
-- Host: 127.0.0.1
-- Generation Time: Dec 23, 2020 at 11:14 AM
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
(10, 'udin', 'java desktop', 'idzin', 'not available', '2020-11-25 09:22:48', '2020-11-30 04:43:04'),
(11, 'dddd', 'java desktop', 'idzin', 'not available', '2020-11-25 10:58:08', '2020-11-30 04:37:00'),
(12, 'dddd', 'java desktop', 'idzin', 'not available', '2020-11-25 10:58:18', '2020-11-25 03:58:18'),
(13, 'dddd', 'java web', 'hadir', 'not available', '2020-11-25 11:32:17', '2020-11-25 04:32:17');

-- --------------------------------------------------------

--
-- Table structure for table `data_bill`
--

CREATE TABLE IF NOT EXISTS `data_bill` (
`id` int(4) NOT NULL,
  `amount` double NOT NULL,
  `description` varchar(100) NOT NULL,
  `username` varchar(75) NOT NULL,
  `status` varchar(75) NOT NULL,
  `date_created` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP
) ENGINE=InnoDB  DEFAULT CHARSET=latin1 AUTO_INCREMENT=3 ;

--
-- Dumping data for table `data_bill`
--

INSERT INTO `data_bill` (`id`, `amount`, `description`, `username`, `status`, `date_created`) VALUES
(1, 7500, 'bayaran nov 2020', 'udin', 'pending', '2020-12-23 09:41:01'),
(2, 7500, 'bayaran nov 2020', 'udin', 'unpaid', '2020-12-23 09:33:55');

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
) ENGINE=InnoDB  DEFAULT CHARSET=latin1 AUTO_INCREMENT=6 ;

--
-- Dumping data for table `data_document`
--

INSERT INTO `data_document` (`id`, `title`, `description`, `filename`, `username`, `url`, `date_created`) VALUES
(3, 'Corel+Data', 'Corel+Data', 'bitplay.mp4', 'udin', 'http://fgroupindonesia.com/downloadable/bitplay.mp4', '2020-11-30 17:11:16'),
(4, 'kuliah', 'kuliah desc', 'pencil.png', 'udin', 'http://fgroupindonesia.com/downloadable/pencil.png', '2020-11-30 17:05:55'),
(5, 'Youtube - Modul 1', 'contoh', NULL, 'udin', 'https://www.youtube.com/watch?v=_B5QM1ZkPZk', '2020-12-10 09:00:25');

-- --------------------------------------------------------

--
-- Table structure for table `data_history`
--

CREATE TABLE IF NOT EXISTS `data_history` (
`id` int(4) NOT NULL,
  `username` varchar(75) NOT NULL,
  `description` varchar(200) NOT NULL,
  `date_created` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP
) ENGINE=InnoDB  DEFAULT CHARSET=latin1 AUTO_INCREMENT=52 ;

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
(7, 'ccc', 'waiting', '2020-10-21 14:14:47'),
(12, 'dede', 'add new report bugs [dsdsd]', '2020-12-09 07:35:20'),
(13, 'dede', 'add new payment proof from [Cash]', '2020-12-09 07:35:58'),
(14, 'dede', 'updating user profile', '2020-12-09 07:36:21'),
(15, 'udin', 'reporting bugs [coba caob]', '2020-12-09 07:58:07'),
(16, 'udin', 'uploading payment proof [Transfer Bank]', '2020-12-09 07:59:40'),
(17, 'udin', 'changing schedule [ monday 12:14] to [Wednesday]', '2020-12-09 09:34:59'),
(18, 'udin', 'updating client settings', '2020-12-09 09:58:09'),
(19, 'udin', 'changing schedule [ monday 12:14] to [selasa]', '2020-12-09 10:01:14'),
(20, 'udin', 'reporting bugs [asd]', '2020-12-09 17:54:09'),
(21, 'udin', 'reporting bugs [cccc]', '2020-12-09 17:54:22'),
(22, 'udin', 'reporting bugs [asdasd]', '2020-12-09 17:54:42'),
(23, 'udin', 'logging in successfuly', '2020-12-10 09:00:44'),
(24, 'udin', 'logging in successfuly', '2020-12-10 09:02:11'),
(25, 'udin', 'logging in successfuly', '2020-12-10 09:03:40'),
(26, 'udin', 'logging in successfuly', '2020-12-10 09:04:29'),
(27, 'udin', 'opening youtube [https://www.youtube.com/watch?v=_B5QM1ZkPZk]', '2020-12-10 09:04:33'),
(28, 'udin', 'opening youtube [https://www.youtube.com/watch?v=_B5QM1ZkPZk]', '2020-12-10 09:04:46'),
(29, 'udin', 'opening document [bitplay.mp4]', '2020-12-10 09:05:47'),
(30, 'udin', 'logging in successfuly', '2020-12-10 09:10:17'),
(31, 'udin', 'opening youtube [https://www.youtube.com/watch?v=_B5QM1ZkPZk]', '2020-12-10 09:11:04'),
(32, 'udin', 'logging in successfuly', '2020-12-10 09:15:53'),
(33, 'udin', 'opening youtube [https://www.youtube.com/watch?v=_B5QM1ZkPZk]', '2020-12-10 09:16:28'),
(34, 'udin', 'opening youtube [https://www.youtube.com/watch?v=_B5QM1ZkPZk]', '2020-12-10 09:16:52'),
(35, 'udin', 'logging in successfuly', '2020-12-10 09:22:34'),
(36, 'udin', 'logging in successfuly', '2020-12-10 09:23:29'),
(37, 'udin', 'logging in successfuly', '2020-12-10 09:24:39'),
(38, 'udin', 'logging in successfuly', '2020-12-10 09:43:59'),
(39, 'udin', 'opening youtube [https://www.youtube.com/watch?v=_B5QM1ZkPZk]', '2020-12-10 09:44:39'),
(40, 'udin', 'opening document [pencil.png]', '2020-12-10 09:45:44'),
(41, 'udin', 'opening teamviewer', '2020-12-10 09:46:47'),
(42, 'udin', 'logging in successfuly', '2020-12-10 10:22:46'),
(43, 'udin', 'logging in successfuly', '2020-12-10 10:26:07'),
(44, 'udin', 'logging in successfuly', '2020-12-21 04:48:00'),
(45, 'udin', 'logging in successfuly', '2020-12-22 03:40:22'),
(46, 'udin', 'logging in successfuly', '2020-12-22 03:43:33'),
(47, 'udin', 'logging in successfuly', '2020-12-22 03:44:19'),
(48, 'udin', 'logging in successfuly', '2020-12-22 04:35:14'),
(49, 'udin', 'logging in successfuly', '2020-12-22 04:35:56'),
(50, 'udin', 'logging in successfuly', '2020-12-22 04:38:15'),
(51, 'udin', 'logging in successfuly', '2020-12-22 07:18:14');

-- --------------------------------------------------------

--
-- Table structure for table `data_payment`
--

CREATE TABLE IF NOT EXISTS `data_payment` (
`id` int(4) NOT NULL,
  `username` varchar(75) NOT NULL,
  `amount` double NOT NULL,
  `method` varchar(45) NOT NULL,
  `screenshot` varchar(75) NOT NULL DEFAULT 'not available',
  `date_created` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP
) ENGINE=InnoDB  DEFAULT CHARSET=latin1 AUTO_INCREMENT=16 ;

--
-- Dumping data for table `data_payment`
--

INSERT INTO `data_payment` (`id`, `username`, `amount`, `method`, `screenshot`, `date_created`) VALUES
(3, 'dddd', 500000, 'Cash', 'not available', '2020-11-30 03:42:05'),
(10, 'udin', 100000, 'Transfer Bank', 'not available', '2020-11-30 04:19:19'),
(11, 'udin', 750000, 'Transfer+Bank', '1606763748_youtube.jpg', '2020-11-30 19:15:48'),
(12, 'udin', 900000, 'Transfer Bank', '1607497879_LOGO_RTH.jpg', '2020-12-09 07:11:19'),
(13, 'dede', 900000, 'Cash', 'not available', '2020-12-09 07:35:58'),
(14, 'udin', 500000, 'Transfer Bank', '1607500780_123a.png', '2020-12-09 07:59:40'),
(15, 'udin', 7500, 'Transfer Bank', '1608716461_cash.jpg', '2020-12-23 09:41:01');

-- --------------------------------------------------------

--
-- Table structure for table `data_remote_login`
--

CREATE TABLE IF NOT EXISTS `data_remote_login` (
`id` int(4) NOT NULL,
  `username` varchar(75) DEFAULT NULL,
  `machine_unique` varchar(100) NOT NULL,
  `country` varchar(75) NOT NULL,
  `region` varchar(75) NOT NULL,
  `city` varchar(75) NOT NULL,
  `isp` varchar(75) NOT NULL,
  `isp_as` varchar(75) NOT NULL,
  `ip_address` varchar(25) NOT NULL,
  `status` varchar(25) NOT NULL,
  `date_created` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP
) ENGINE=InnoDB  DEFAULT CHARSET=latin1 AUTO_INCREMENT=6 ;

--
-- Dumping data for table `data_remote_login`
--

INSERT INTO `data_remote_login` (`id`, `username`, `machine_unique`, `country`, `region`, `city`, `isp`, `isp_as`, `ip_address`, `status`, `date_created`) VALUES
(1, NULL, '4B435451-394A-3043-4631-14DAE9AD8243', 'Indonesia', 'West Java', 'Bandung', 'Indonesia Broadband Communications', 'AS55699 PT. Cemerlang Multimedia', '103.247.197.4', 'ready', '2020-12-20 03:12:14'),
(5, NULL, '4B435451-394A-3043-4631-14DAE9AD8243', 'Indonesia', 'West Java', 'Bandung', 'Indonesia Broadband Communications', 'AS55699 PT. Cemerlang Multimedia', '103.247.197.4', 'ready', '2020-12-22 07:36:38');

-- --------------------------------------------------------

--
-- Table structure for table `data_report_bugs`
--

CREATE TABLE IF NOT EXISTS `data_report_bugs` (
`id` int(4) NOT NULL,
  `app_name` varchar(75) NOT NULL,
  `username` varchar(75) NOT NULL,
  `ip_address` varchar(20) NOT NULL,
  `title` varchar(75) NOT NULL,
  `description` varchar(200) NOT NULL,
  `screenshot` varchar(75) NOT NULL DEFAULT 'not available',
  `date_created` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP
) ENGINE=InnoDB  DEFAULT CHARSET=latin1 AUTO_INCREMENT=17 ;

--
-- Dumping data for table `data_report_bugs`
--

INSERT INTO `data_report_bugs` (`id`, `app_name`, `username`, `ip_address`, `title`, `description`, `screenshot`, `date_created`) VALUES
(14, 'portal access', 'udin', '127.0.0.1', 'asd', 'asd', 'not available', '2020-12-09 17:54:09'),
(15, 'portal access', 'udin', '127.0.0.1', 'cccc', 'cccc cccc', 'not available', '2020-12-09 17:54:22'),
(16, 'portal access', 'udin', '127.0.0.1', 'asdasd', 'asdasd asd', '1607536482_drawing.png', '2020-12-09 17:54:42');

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
(10, 'udin', 'monday', '12:14', 'javascript web');

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
) ENGINE=InnoDB  DEFAULT CHARSET=latin1 AUTO_INCREMENT=45 ;

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
(25, 'admin', '3eb80fb7b8a63a23044bfe77ccb6e606a16a1058', '2020-11-25 07:43:19', '2020-11-26 14:11:19'),
(26, 'admin', 'f49ee4aa22c35b983a028e4e277a8e706e2bc647', '2020-11-28 07:24:24', '2020-11-29 14:11:24'),
(27, 'admin', '1036d1305bde10a9beb3d701271fea5d9187a3c1', '2020-11-30 03:19:46', '2020-12-01 10:12:46'),
(28, 'udin', '30956bab90246302df6844f74f920ca632f601a5', '2020-11-30 07:12:09', '2020-12-01 14:12:09'),
(29, 'udin', '3f1f76c8a6005a34688d996e2db5a651a6358431', '2020-12-01 07:40:47', '2020-12-02 14:12:47'),
(30, 'udin', '1b35fbf53b2533b60a377b0a270a92f333a7ffa2', '2020-12-02 09:48:05', '2020-12-03 16:12:05'),
(31, 'udin', '1605b693273f488e905c1b355bd4111d26daa8d9', '2020-12-03 15:30:58', '2020-12-04 22:12:58'),
(32, 'udin', 'bcd93849504bd29780ecef0a0d2240270234017c', '2020-12-06 15:15:51', '2020-12-07 22:12:51'),
(33, 'udin', 'd8f897a9980cb87b088b5916a68f1830c377bca1', '2020-12-08 07:24:16', '2020-12-09 14:12:16'),
(34, 'admin', '630aa0c9c101fbce344937c527d55676fc60814c', '2020-12-09 03:54:53', '2020-12-10 11:20:23'),
(35, 'udin', '1cf9d86d4d6c67d02ed411a5ae15b9b92e236a75', '2020-12-09 07:15:23', '2020-12-10 14:12:23'),
(36, 'dede', '95de77ce50fa4fc423c680f2b06152a2e5149f79', '2020-12-09 07:27:58', '2020-12-10 14:12:58'),
(37, 'admin', '39417f91a0d6e4801cade05f7cb88c18735bc89f', '2020-12-10 08:55:17', '2020-12-11 15:12:17'),
(38, 'udin', 'c5ba427050c0dd9d8c45ff48d7f99f102d0b6574', '2020-12-10 09:00:42', '2020-12-11 16:12:42'),
(39, 'admin', '69d355856e533c80f4cc317fd5b19ead99e558f4', '2020-12-15 03:51:00', '2020-12-16 10:12:00'),
(40, 'admin', 'd9e8043bf74c795694866f61fe2f2b7b529319b4', '2020-12-16 04:52:20', '2020-12-17 11:12:20'),
(41, 'admin', 'b3000f2de104d044d0b62a5590ccc51ca0d4be72', '2020-12-17 04:18:49', '2020-12-18 11:12:49'),
(42, 'admin', 'a24b70c4de297ef2ec6f43c3121a59a81c551eef', '2020-12-18 16:43:54', '2020-12-19 23:12:54'),
(44, 'udin', 'a7bff0d547956f416db368fcfb820969d2a1c9fa', '2020-12-23 09:30:05', '2020-12-24 10:59:17');

-- --------------------------------------------------------

--
-- Table structure for table `data_tools`
--

CREATE TABLE IF NOT EXISTS `data_tools` (
`id` int(4) NOT NULL,
  `app_name` varchar(75) NOT NULL,
  `app_ver` varchar(25) NOT NULL
) ENGINE=InnoDB  DEFAULT CHARSET=latin1 AUTO_INCREMENT=2 ;

--
-- Dumping data for table `data_tools`
--

INSERT INTO `data_tools` (`id`, `app_name`, `app_ver`) VALUES
(1, 'teamviewer', '15.12.4.0');

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
  `tmv_id` varchar(25) NOT NULL DEFAULT 'not available',
  `tmv_pass` varchar(25) NOT NULL DEFAULT 'not available',
  `date_created` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP
) ENGINE=InnoDB  DEFAULT CHARSET=latin1 AUTO_INCREMENT=39 ;

--
-- Dumping data for table `data_user`
--

INSERT INTO `data_user` (`id`, `username`, `pass`, `email`, `address`, `propic`, `mobile`, `tmv_id`, `tmv_pass`, `date_created`) VALUES
(1, 'admin', 'admin', 'sya@yqhoo.com', 'bdg jakartaaa\nluar syaaaa', '1608179927_images.jpeg', 'not available', 'not available', 'not available\r\n\r\n', '2020-12-17 04:38:47'),
(37, 'dede', 'dede', 'dddd@ymail.com', 'hehehe', 'default.png', '123-123', '432996191', 'sosoais', '2020-12-09 07:36:21'),
(38, 'udin', '123', 'udin@udin.com', 'jakarta singapur', 'default.png', '1231', '432996191', 'test', '2020-11-30 15:33:32');

--
-- Indexes for dumped tables
--

--
-- Indexes for table `data_attendance`
--
ALTER TABLE `data_attendance`
 ADD PRIMARY KEY (`id`);

--
-- Indexes for table `data_bill`
--
ALTER TABLE `data_bill`
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
-- Indexes for table `data_remote_login`
--
ALTER TABLE `data_remote_login`
 ADD PRIMARY KEY (`id`);

--
-- Indexes for table `data_report_bugs`
--
ALTER TABLE `data_report_bugs`
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
-- Indexes for table `data_tools`
--
ALTER TABLE `data_tools`
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
-- AUTO_INCREMENT for table `data_bill`
--
ALTER TABLE `data_bill`
MODIFY `id` int(4) NOT NULL AUTO_INCREMENT,AUTO_INCREMENT=3;
--
-- AUTO_INCREMENT for table `data_class_room`
--
ALTER TABLE `data_class_room`
MODIFY `id` int(4) NOT NULL AUTO_INCREMENT,AUTO_INCREMENT=5;
--
-- AUTO_INCREMENT for table `data_document`
--
ALTER TABLE `data_document`
MODIFY `id` int(4) NOT NULL AUTO_INCREMENT,AUTO_INCREMENT=6;
--
-- AUTO_INCREMENT for table `data_history`
--
ALTER TABLE `data_history`
MODIFY `id` int(4) NOT NULL AUTO_INCREMENT,AUTO_INCREMENT=52;
--
-- AUTO_INCREMENT for table `data_payment`
--
ALTER TABLE `data_payment`
MODIFY `id` int(4) NOT NULL AUTO_INCREMENT,AUTO_INCREMENT=16;
--
-- AUTO_INCREMENT for table `data_remote_login`
--
ALTER TABLE `data_remote_login`
MODIFY `id` int(4) NOT NULL AUTO_INCREMENT,AUTO_INCREMENT=6;
--
-- AUTO_INCREMENT for table `data_report_bugs`
--
ALTER TABLE `data_report_bugs`
MODIFY `id` int(4) NOT NULL AUTO_INCREMENT,AUTO_INCREMENT=17;
--
-- AUTO_INCREMENT for table `data_schedule`
--
ALTER TABLE `data_schedule`
MODIFY `id` int(4) NOT NULL AUTO_INCREMENT,AUTO_INCREMENT=11;
--
-- AUTO_INCREMENT for table `data_token`
--
ALTER TABLE `data_token`
MODIFY `id` int(4) NOT NULL AUTO_INCREMENT,AUTO_INCREMENT=45;
--
-- AUTO_INCREMENT for table `data_tools`
--
ALTER TABLE `data_tools`
MODIFY `id` int(4) NOT NULL AUTO_INCREMENT,AUTO_INCREMENT=2;
--
-- AUTO_INCREMENT for table `data_user`
--
ALTER TABLE `data_user`
MODIFY `id` int(4) NOT NULL AUTO_INCREMENT,AUTO_INCREMENT=39;
/*!40101 SET CHARACTER_SET_CLIENT=@OLD_CHARACTER_SET_CLIENT */;
/*!40101 SET CHARACTER_SET_RESULTS=@OLD_CHARACTER_SET_RESULTS */;
/*!40101 SET COLLATION_CONNECTION=@OLD_COLLATION_CONNECTION */;
