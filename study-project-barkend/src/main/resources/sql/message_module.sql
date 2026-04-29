SET NAMES utf8mb4;
SET FOREIGN_KEY_CHECKS = 0;

DROP TABLE IF EXISTS `message_notice`;
CREATE TABLE `message_notice` (
  `id` varchar(64) NOT NULL,
  `create_by` varchar(64) DEFAULT NULL,
  `create_time` datetime DEFAULT NULL,
  `update_by` varchar(64) DEFAULT NULL,
  `update_time` datetime DEFAULT NULL,
  `title` varchar(200) NOT NULL,
  `content` text,
  `level` int DEFAULT 1,
  `target_role` varchar(64) DEFAULT NULL,
  `enabled` tinyint DEFAULT 1,
  PRIMARY KEY (`id`) USING BTREE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

DROP TABLE IF EXISTS `message_notice_read`;
CREATE TABLE `message_notice_read` (
  `id` varchar(64) NOT NULL,
  `message_id` varchar(64) NOT NULL,
  `user_id` varchar(64) NOT NULL,
  `read_time` datetime DEFAULT NULL,
  PRIMARY KEY (`id`) USING BTREE,
  UNIQUE KEY `uk_message_user` (`message_id`,`user_id`) USING BTREE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

INSERT INTO `message_notice` (`id`, `create_by`, `create_time`, `title`, `content`, `level`, `target_role`, `enabled`)
VALUES ('msg_demo_1', 'admin', NOW(), '系统升级通知', '平台将在今晚 23:00 进行维护升级，请提前保存学习进度。', 2, '1,2,3', 1);

SET FOREIGN_KEY_CHECKS = 1;
