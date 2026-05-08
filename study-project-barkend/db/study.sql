/*
 Navicat Premium Dump SQL

 Source Server         : localhost
 Source Server Type    : MySQL
 Source Server Version : 50726 (5.7.26)
 Source Host           : localhost:3306
 Source Schema         : study

 Target Server Type    : MySQL
 Target Server Version : 50726 (5.7.26)
 File Encoding         : 65001

 Date: 09/05/2026 00:31:58
*/

SET NAMES utf8mb4;
SET FOREIGN_KEY_CHECKS = 0;

-- ----------------------------
-- Table structure for cloud_computing_course
-- ----------------------------
DROP TABLE IF EXISTS `cloud_computing_course`;
CREATE TABLE `cloud_computing_course`  (
  `id` varchar(36) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL,
  `create_by` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '创建人',
  `create_time` datetime NULL DEFAULT NULL COMMENT '创建日期',
  `update_by` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '更新人',
  `update_time` datetime NULL DEFAULT NULL COMMENT '更新日期',
  `course_name` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL COMMENT '课程名称',
  `course_type_id` varchar(32) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '课程分类名称',
  `course_tag` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '课程标签',
  `teacher_id` varchar(36) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL COMMENT '关联负责教师',
  `course_hours` int(11) NOT NULL COMMENT '总课时（h）',
  `course_status` int(11) NOT NULL COMMENT '课程状态',
  PRIMARY KEY (`id`) USING BTREE
) ENGINE = InnoDB CHARACTER SET = utf8mb4 COLLATE = utf8mb4_general_ci ROW_FORMAT = DYNAMIC;

-- ----------------------------
-- Records of cloud_computing_course
-- ----------------------------
INSERT INTO `cloud_computing_course` VALUES ('COURSE003', 'admin', '2026-01-15 09:00:00', 'admin', '2026-05-08 13:36:30', 'Python程序设计', 'TYP002', '必修,编程,入门', '2052697568052920322', 48, 1);
INSERT INTO `cloud_computing_course` VALUES ('COURSE004', 'admin', '2026-01-20 09:00:00', 'admin', '2026-05-08 13:36:33', 'Java高级开发', 'TYP002', '必修,面向对象,企业级', '2052697568052920322', 64, 1);
INSERT INTO `cloud_computing_course` VALUES ('COURSE005', 'admin', '2026-02-01 09:00:00', NULL, NULL, '数据库原理与应用', 'TYP003', '必修,SQL,MySQL', '2052697568052920322', 40, 1);
INSERT INTO `cloud_computing_course` VALUES ('COURSE006', 'admin', '2026-02-10 09:00:00', 'admin', '2026-05-08 13:36:36', '云计算概论', 'TYP001', '必修,基础,IaaS', '2052697568052920322', 32, 1);
INSERT INTO `cloud_computing_course` VALUES ('COURSE007', 'admin', '2026-03-01 09:00:00', 'admin', '2026-05-08 18:33:19', 'Docker与Kubernetes', 'TYP003', '选修,容器化,DevOps', '2052697568052920322', 24, 0);
INSERT INTO `cloud_computing_course` VALUES ('COURSE008', 'admin', '2026-03-05 09:00:00', NULL, NULL, '机器学习实战', 'TYP004', '选修,AI,TensorFlow', '2052697568052920322', 56, 1);
INSERT INTO `cloud_computing_course` VALUES ('COURSE009', 'admin', '2026-03-10 09:00:00', NULL, NULL, '软件项目管理', 'TYP005', '必修,敏捷,Scrum', '2052697568052920322', 32, 1);
INSERT INTO `cloud_computing_course` VALUES ('COURSE010', 'admin', '2026-03-15 09:00:00', NULL, NULL, '毕业设计指导(云原生)', 'TYP006', '必修,综合,答辩', '2052697568052920322', 20, 1);
INSERT INTO `cloud_computing_course` VALUES ('COURSE011', 'admin', '2026-04-01 09:00:00', NULL, NULL, 'C++程序设计', 'TYP002', '必修,面向对象,系统级', '2052697568052920322', 48, 1);
INSERT INTO `cloud_computing_course` VALUES ('COURSE012', 'admin', '2026-04-02 09:00:00', 'admin', '2026-05-08 18:33:02', 'Go语言并发编程', 'TYP002', '选修,高并发,微服务', '2052697568052920322', 32, 0);
INSERT INTO `cloud_computing_course` VALUES ('COURSE013', 'admin', '2026-04-03 09:00:00', NULL, NULL, 'JavaScript高级应用', 'TYP002', '必修,全栈,异步编程', '2052697568052920322', 40, 1);
INSERT INTO `cloud_computing_course` VALUES ('COURSE014', 'admin', '2026-04-04 09:00:00', NULL, NULL, 'Vue3实战项目', 'TYP007', '必修,组件化,前后端分离', '2052697568052920322', 56, 1);
INSERT INTO `cloud_computing_course` VALUES ('COURSE015', 'admin', '2026-04-05 09:00:00', NULL, NULL, 'React入门与进阶', 'TYP007', '选修,JSX,状态管理', '2052697568052920322', 48, 1);
INSERT INTO `cloud_computing_course` VALUES ('COURSE016', 'admin', '2026-04-06 09:00:00', NULL, NULL, '移动端适配与跨平台开发', 'TYP007', '选修,uniapp,Flutter', '2052697568052920322', 36, 1);
INSERT INTO `cloud_computing_course` VALUES ('COURSE017', 'admin', '2026-04-07 09:00:00', NULL, NULL, 'Linux服务器运维', 'TYP003', '必修,Shell,监控', '2052697568052920322', 50, 1);
INSERT INTO `cloud_computing_course` VALUES ('COURSE018', 'admin', '2026-04-08 09:00:00', NULL, NULL, '自动化运维（Ansible）', 'TYP003', '选修,CMDB,持续交付', '2052697568052920322', 24, 1);
INSERT INTO `cloud_computing_course` VALUES ('COURSE019', 'admin', '2026-04-09 09:00:00', NULL, NULL, '数据挖掘与清洗', 'TYP004', '必修,Pandas,ETL', '2052697568052920322', 48, 1);
INSERT INTO `cloud_computing_course` VALUES ('COURSE020', 'admin', '2026-04-10 09:00:00', NULL, NULL, '深度学习（PyTorch）', 'TYP004', '选修,CNN,RNN', '2052697568052920322', 72, 1);
INSERT INTO `cloud_computing_course` VALUES ('COURSE021', 'admin', '2026-04-11 09:00:00', NULL, NULL, '大数据平台（Hadoop+Spark）', 'TYP004', '选修,分布式,MapReduce', '2052697568052920322', 60, 1);
INSERT INTO `cloud_computing_course` VALUES ('COURSE022', 'admin', '2026-04-12 09:00:00', NULL, NULL, '网络安全基础', 'TYP008', '必修,渗透测试,防护', '2052697568052920322', 40, 1);
INSERT INTO `cloud_computing_course` VALUES ('COURSE023', 'admin', '2026-04-13 09:00:00', NULL, NULL, '密码学与身份认证', 'TYP008', '选修,加密,数字签名', '2052697568052920322', 32, 1);
INSERT INTO `cloud_computing_course` VALUES ('COURSE024', 'admin', '2026-04-14 09:00:00', NULL, NULL, 'IT项目管理案例实战', 'TYP005', '必修,甘特图,Risk', '2052697568052920322', 28, 1);
INSERT INTO `cloud_computing_course` VALUES ('COURSE025', 'admin', '2026-04-15 09:00:00', NULL, NULL, '技术文档写作', 'TYP009', '必修,Markdown,API文档', '2052697568052920322', 16, 1);
INSERT INTO `cloud_computing_course` VALUES ('COURSE026', 'admin', '2026-04-16 09:00:00', NULL, NULL, '演讲与汇报技巧', 'TYP009', '选修,PPT,逻辑呈现', '2052697568052920322', 12, 1);
INSERT INTO `cloud_computing_course` VALUES ('COURSE027', 'admin', '2026-04-17 09:00:00', NULL, NULL, '电商系统实训（Java+Vue）', 'TYP006', '必修,全栈,企业项目', '2052697568052920322', 80, 1);
INSERT INTO `cloud_computing_course` VALUES ('COURSE028', 'admin', '2026-04-18 09:00:00', NULL, NULL, '自动驾驶模拟实训', 'TYP006', '选修,Python,仿真', '2052697568052920322', 64, 1);
INSERT INTO `cloud_computing_course` VALUES ('COURSE029', 'admin', '2026-04-19 09:00:00', NULL, NULL, '微服务架构实战', 'TYP006', '必修,SpringCloud,Dubbo', '2052697568052920322', 52, 1);
INSERT INTO `cloud_computing_course` VALUES ('COURSE030', 'admin', '2026-04-20 09:00:00', NULL, NULL, '离散数学', 'TYP001', '必修,逻辑,图论', '2052697568052920322', 44, 1);

-- ----------------------------
-- Table structure for cloud_computing_course_resource
-- ----------------------------
DROP TABLE IF EXISTS `cloud_computing_course_resource`;
CREATE TABLE `cloud_computing_course_resource`  (
  `id` varchar(36) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL,
  `create_by` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '创建人',
  `create_time` datetime NULL DEFAULT NULL COMMENT '创建日期',
  `update_by` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '更新人',
  `update_time` datetime NULL DEFAULT NULL COMMENT '更新日期',
  `resource_type` int(11) NOT NULL COMMENT '学习内容类型',
  `course_id` varchar(32) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL COMMENT '课程名称',
  `resource_name` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL COMMENT '资源名称',
  `resource_url` varchar(500) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL COMMENT '资源存储地址',
  `resource_sort` int(11) NOT NULL COMMENT '资源排序',
  PRIMARY KEY (`id`) USING BTREE
) ENGINE = InnoDB CHARACTER SET = utf8mb4 COLLATE = utf8mb4_general_ci ROW_FORMAT = DYNAMIC;

-- ----------------------------
-- Records of cloud_computing_course_resource
-- ----------------------------
INSERT INTO `cloud_computing_course_resource` VALUES ('RES_ADMIN_1', 'teacher', '2026-04-07 10:00:00', NULL, NULL, 1, 'COURSE017', 'Linux性能监控', 'video/linux_perf.mp4', 1);
INSERT INTO `cloud_computing_course_resource` VALUES ('RES_ADMIN_2', 'teacher', '2026-04-09 10:00:00', NULL, NULL, 2, 'COURSE017', '防火墙配置文档', 'document/linux_firewall.docx', 2);
INSERT INTO `cloud_computing_course_resource` VALUES ('RES_ADMIN_3', 'teacher', '2026-04-11 10:00:00', NULL, NULL, 3, 'COURSE017', 'LVS负载均衡课件', 'courseware/linux_lvs.pptx', 3);
INSERT INTO `cloud_computing_course_resource` VALUES ('RES_ANSIBLE_1', 'teacher', '2026-04-08 10:00:00', NULL, NULL, 1, 'COURSE018', 'Ansible Playbook实战', 'video/ansible_playbook.mp4', 1);
INSERT INTO `cloud_computing_course_resource` VALUES ('RES_ANSIBLE_2', 'teacher', '2026-04-10 10:00:00', NULL, NULL, 2, 'COURSE018', 'Ansible Roles详解', 'document/ansible_roles.docx', 2);
INSERT INTO `cloud_computing_course_resource` VALUES ('RES_ANSIBLE_3', 'teacher', '2026-04-12 10:00:00', NULL, NULL, 3, 'COURSE018', '自动化部署案例课件', 'courseware/ansible_case.pptx', 3);
INSERT INTO `cloud_computing_course_resource` VALUES ('RES_AUTO_1', 'teacher', '2026-04-18 10:00:00', NULL, NULL, 1, 'COURSE028', '计算机视觉基础', 'video/auto_cv.mp4', 1);
INSERT INTO `cloud_computing_course_resource` VALUES ('RES_AUTO_2', 'teacher', '2026-04-20 10:00:00', NULL, NULL, 2, 'COURSE028', '传感器数据处理文档', 'document/auto_sensor.docx', 2);
INSERT INTO `cloud_computing_course_resource` VALUES ('RES_AUTO_3', 'teacher', '2026-04-22 10:00:00', NULL, NULL, 3, 'COURSE028', '路径规划算法课件', 'courseware/auto_path.pptx', 3);
INSERT INTO `cloud_computing_course_resource` VALUES ('RES_BIGDATA_1', 'teacher', '2026-04-11 10:00:00', NULL, NULL, 1, 'COURSE021', 'HDFS架构详解', 'video/hdfs_arch.mp4', 1);
INSERT INTO `cloud_computing_course_resource` VALUES ('RES_BIGDATA_2', 'teacher', '2026-04-13 10:00:00', NULL, NULL, 2, 'COURSE021', 'MapReduce编程', 'document/mapreduce_program.docx', 2);
INSERT INTO `cloud_computing_course_resource` VALUES ('RES_BIGDATA_3', 'teacher', '2026-04-15 10:00:00', NULL, NULL, 3, 'COURSE021', 'Spark SQL课件', 'courseware/spark_sql.pptx', 3);
INSERT INTO `cloud_computing_course_resource` VALUES ('RES_CLOUD_1', 'teacher', '2026-02-11 09:00:00', NULL, NULL, 1, 'COURSE006', '云计算三大服务模式', 'video/cloud_services.mp4', 1);
INSERT INTO `cloud_computing_course_resource` VALUES ('RES_CLOUD_2', 'teacher', '2026-02-13 09:00:00', NULL, NULL, 2, 'COURSE006', '虚拟化技术概述', 'document/cloud_virtual.docx', 2);
INSERT INTO `cloud_computing_course_resource` VALUES ('RES_CLOUD_3', 'teacher', '2026-02-15 09:00:00', NULL, NULL, 3, 'COURSE006', 'OpenStack入门课件', 'courseware/cloud_openstack.pptx', 3);
INSERT INTO `cloud_computing_course_resource` VALUES ('RES_CPP_1', 'teacher', '2026-04-01 10:00:00', NULL, NULL, 1, 'COURSE011', 'C++指针与引用', 'video/cpp_pointer.mp4', 1);
INSERT INTO `cloud_computing_course_resource` VALUES ('RES_CPP_2', 'teacher', '2026-04-03 10:00:00', NULL, NULL, 2, 'COURSE011', 'STL常用容器', 'document/cpp_stl.docx', 2);
INSERT INTO `cloud_computing_course_resource` VALUES ('RES_CPP_3', 'teacher', '2026-04-05 10:00:00', NULL, NULL, 3, 'COURSE011', '多态与虚函数课件', 'courseware/cpp_polymorphism.pptx', 3);
INSERT INTO `cloud_computing_course_resource` VALUES ('RES_CRYPTO_1', 'teacher', '2026-04-13 10:00:00', NULL, NULL, 1, 'COURSE023', '对称加密与非对称加密', 'video/crypto_symmetric.mp4', 1);
INSERT INTO `cloud_computing_course_resource` VALUES ('RES_CRYPTO_2', 'teacher', '2026-04-15 10:00:00', NULL, NULL, 2, 'COURSE023', '数字证书与PKI', 'document/crypto_pki.docx', 2);
INSERT INTO `cloud_computing_course_resource` VALUES ('RES_CRYPTO_3', 'teacher', '2026-04-17 10:00:00', NULL, NULL, 3, 'COURSE023', 'OAuth2.0与JWT课件', 'courseware/crypto_oauth.pptx', 3);
INSERT INTO `cloud_computing_course_resource` VALUES ('RES_DB_1', 'teacher', '2026-02-02 09:00:00', NULL, NULL, 1, 'COURSE005', 'ER模型与关系代数', 'video/db_ermodel.mp4', 1);
INSERT INTO `cloud_computing_course_resource` VALUES ('RES_DB_2', 'teacher', '2026-02-04 09:00:00', NULL, NULL, 2, 'COURSE005', 'SQL高级查询文档', 'document/db_sql_advanced.docx', 2);
INSERT INTO `cloud_computing_course_resource` VALUES ('RES_DB_3', 'teacher', '2026-02-06 09:00:00', NULL, NULL, 3, 'COURSE005', '索引与事务课件', 'courseware/db_index.pptx', 3);
INSERT INTO `cloud_computing_course_resource` VALUES ('RES_DB_4', 'teacher', '2026-02-08 09:00:00', NULL, NULL, 4, 'COURSE005', 'MySQL调优手册', 'other/db_tuning.html', 4);
INSERT INTO `cloud_computing_course_resource` VALUES ('RES_DL_1', 'teacher', '2026-04-10 10:00:00', NULL, NULL, 1, 'COURSE020', 'PyTorch基础张量', 'video/pytorch_tensor.mp4', 1);
INSERT INTO `cloud_computing_course_resource` VALUES ('RES_DL_2', 'teacher', '2026-04-12 10:00:00', NULL, NULL, 2, 'COURSE020', 'CNN图像分类实战', 'document/pytorch_cnn.docx', 2);
INSERT INTO `cloud_computing_course_resource` VALUES ('RES_DL_3', 'teacher', '2026-04-14 10:00:00', NULL, NULL, 3, 'COURSE020', 'RNN序列模型课件', 'courseware/pytorch_rnn.pptx', 3);
INSERT INTO `cloud_computing_course_resource` VALUES ('RES_DL_4', 'teacher', '2026-04-16 10:00:00', NULL, NULL, 1, 'COURSE020', '模型训练与GPU加速', 'video/pytorch_gpu.mp4', 4);
INSERT INTO `cloud_computing_course_resource` VALUES ('RES_DMATH_1', 'teacher', '2026-04-20 10:00:00', NULL, NULL, 1, 'COURSE030', '集合论与关系', 'video/dmath_set.mp4', 1);
INSERT INTO `cloud_computing_course_resource` VALUES ('RES_DMATH_2', 'teacher', '2026-04-22 10:00:00', NULL, NULL, 2, 'COURSE030', '图论基础', 'document/dmath_graph.docx', 2);
INSERT INTO `cloud_computing_course_resource` VALUES ('RES_DMATH_3', 'teacher', '2026-04-24 10:00:00', NULL, NULL, 3, 'COURSE030', '逻辑与证明课件', 'courseware/dmath_logic.pptx', 3);
INSERT INTO `cloud_computing_course_resource` VALUES ('RES_DM_1', 'teacher', '2026-04-09 10:00:00', NULL, NULL, 1, 'COURSE019', 'Pandas数据清洗', 'video/dm_pandas.mp4', 1);
INSERT INTO `cloud_computing_course_resource` VALUES ('RES_DM_2', 'teacher', '2026-04-11 10:00:00', NULL, NULL, 2, 'COURSE019', '特征工程方法', 'document/dm_feature.docx', 2);
INSERT INTO `cloud_computing_course_resource` VALUES ('RES_DM_3', 'teacher', '2026-04-13 10:00:00', NULL, NULL, 3, 'COURSE019', 'ETL工具介绍课件', 'courseware/dm_etl.pptx', 3);
INSERT INTO `cloud_computing_course_resource` VALUES ('RES_DOCKER_1', 'teacher', '2026-03-02 09:00:00', NULL, NULL, 1, 'COURSE007', 'Dockerfile最佳实践', 'video/docker_dockerfile.mp4', 1);
INSERT INTO `cloud_computing_course_resource` VALUES ('RES_DOCKER_2', 'teacher', '2026-03-04 09:00:00', NULL, NULL, 2, 'COURSE007', 'K8s核心概念', 'document/k8s_concepts.docx', 2);
INSERT INTO `cloud_computing_course_resource` VALUES ('RES_DOCKER_3', 'teacher', '2026-03-06 09:00:00', NULL, NULL, 3, 'COURSE007', 'Helm Chart模板课件', 'courseware/k8s_helm.pptx', 3);
INSERT INTO `cloud_computing_course_resource` VALUES ('RES_DOCKER_4', 'teacher', '2026-03-08 09:00:00', NULL, NULL, 1, 'COURSE007', '集群监控与日志', 'video/k8s_monitoring.mp4', 4);
INSERT INTO `cloud_computing_course_resource` VALUES ('RES_DOC_1', 'teacher', '2026-04-15 10:00:00', NULL, NULL, 1, 'COURSE025', '优秀的API文档示例', 'video/doc_api.mp4', 1);
INSERT INTO `cloud_computing_course_resource` VALUES ('RES_DOC_2', 'teacher', '2026-04-17 10:00:00', NULL, NULL, 2, 'COURSE025', 'Markdown语法指南', 'document/doc_markdown.docx', 2);
INSERT INTO `cloud_computing_course_resource` VALUES ('RES_DOC_3', 'teacher', '2026-04-19 10:00:00', NULL, NULL, 3, 'COURSE025', '技术写作风格课件', 'courseware/doc_style.pptx', 3);
INSERT INTO `cloud_computing_course_resource` VALUES ('RES_ECOMM_1', 'teacher', '2026-04-17 10:00:00', NULL, NULL, 1, 'COURSE027', '电商系统需求分析', 'video/ecommerce_req.mp4', 1);
INSERT INTO `cloud_computing_course_resource` VALUES ('RES_ECOMM_2', 'teacher', '2026-04-19 10:00:00', NULL, NULL, 2, 'COURSE027', '购物车模块设计文档', 'document/ecommerce_cart.docx', 2);
INSERT INTO `cloud_computing_course_resource` VALUES ('RES_ECOMM_3', 'teacher', '2026-04-21 10:00:00', NULL, NULL, 3, 'COURSE027', '支付系统课件', 'courseware/ecommerce_pay.pptx', 3);
INSERT INTO `cloud_computing_course_resource` VALUES ('RES_ECOMM_4', 'teacher', '2026-04-23 10:00:00', NULL, NULL, 1, 'COURSE027', '订单与库存管理', 'video/ecommerce_order.mp4', 4);
INSERT INTO `cloud_computing_course_resource` VALUES ('RES_GO_1', 'teacher', '2026-04-02 10:00:00', NULL, NULL, 1, 'COURSE012', 'goroutine与channel', 'video/go_channel.mp4', 1);
INSERT INTO `cloud_computing_course_resource` VALUES ('RES_GO_2', 'teacher', '2026-04-04 10:00:00', NULL, NULL, 2, 'COURSE012', 'Go并发模式', 'document/go_concurrency_pattern.docx', 2);
INSERT INTO `cloud_computing_course_resource` VALUES ('RES_GO_3', 'teacher', '2026-04-06 10:00:00', NULL, NULL, 3, 'COURSE012', '性能调优课件', 'courseware/go_perf.pptx', 3);
INSERT INTO `cloud_computing_course_resource` VALUES ('RES_ITPM_1', 'teacher', '2026-04-14 10:00:00', NULL, NULL, 1, 'COURSE024', '项目计划制定', 'video/itpm_planning.mp4', 1);
INSERT INTO `cloud_computing_course_resource` VALUES ('RES_ITPM_2', 'teacher', '2026-04-16 10:00:00', NULL, NULL, 2, 'COURSE024', '风险登记册模板', 'document/itpm_risk.docx', 2);
INSERT INTO `cloud_computing_course_resource` VALUES ('RES_ITPM_3', 'teacher', '2026-04-18 10:00:00', NULL, NULL, 3, 'COURSE024', '项目复盘案例课件', 'courseware/itpm_retro.pptx', 3);
INSERT INTO `cloud_computing_course_resource` VALUES ('RES_JAVA_1', 'teacher', '2026-01-21 09:00:00', 'teacher', '2026-05-08 22:33:50', 1, 'COURSE004', 'Java内存模型解析', 'temp/【java】jvm内存模型全面解析_2052758762809921537.mp4', 1);
INSERT INTO `cloud_computing_course_resource` VALUES ('RES_JAVA_2', 'teacher', '2026-01-23 09:00:00', 'teacher', '2026-05-08 22:40:22', 2, 'COURSE004', '多线程编程指南', 'temp/多线程编程指南_2_2052760518767874049.pdf', 2);
INSERT INTO `cloud_computing_course_resource` VALUES ('RES_JAVA_3', 'teacher', '2026-01-25 09:00:00', 'teacher', '2026-05-08 22:41:26', 3, 'COURSE004', 'JVM调优课件', 'temp/JVM体系结构与GC调优_2052760718907478017_2052760787664703490.pptx', 3);
INSERT INTO `cloud_computing_course_resource` VALUES ('RES_JAVA_4', 'teacher', '2026-01-27 09:00:00', 'teacher', '2026-05-08 22:40:08', 1, 'COURSE004', 'SpringBoot实战视频', 'temp/java学不会？10分钟springboot入门到精通！程序员邪修之：springboot直通车_2052760462849413122.mp4', 4);
INSERT INTO `cloud_computing_course_resource` VALUES ('RES_JS_1', 'teacher', '2026-04-03 10:00:00', NULL, NULL, 1, 'COURSE013', 'ES6+新特性', 'video/js_es6plus.mp4', 1);
INSERT INTO `cloud_computing_course_resource` VALUES ('RES_JS_2', 'teacher', '2026-04-05 10:00:00', NULL, NULL, 2, 'COURSE013', '异步编程进阶', 'document/js_async_advanced.docx', 2);
INSERT INTO `cloud_computing_course_resource` VALUES ('RES_JS_3', 'teacher', '2026-04-07 10:00:00', NULL, NULL, 3, 'COURSE013', '前端工程化课件', 'courseware/js_engineering.pptx', 3);
INSERT INTO `cloud_computing_course_resource` VALUES ('RES_LINUX_1', 'teacher', '2026-05-07 08:00:00', NULL, NULL, 1, '2052292863269015554', 'Linux命令行入门', 'video/linux_cli.mp4', 1);
INSERT INTO `cloud_computing_course_resource` VALUES ('RES_LINUX_2', 'teacher', '2026-05-07 08:10:00', NULL, NULL, 2, '2052292863269015554', 'Linux文件权限详解', 'document/linux_permission.docx', 2);
INSERT INTO `cloud_computing_course_resource` VALUES ('RES_LINUX_3', 'teacher', '2026-05-07 08:20:00', NULL, NULL, 3, '2052292863269015554', '网络配置课件', 'courseware/linux_network.pptx', 3);
INSERT INTO `cloud_computing_course_resource` VALUES ('RES_LINUX_4', 'teacher', '2026-05-07 08:30:00', NULL, NULL, 4, '2052292863269015554', 'Shell脚本参考手册', 'other/shell_cheatsheet.html', 4);
INSERT INTO `cloud_computing_course_resource` VALUES ('RES_ML_1', 'teacher', '2026-03-06 09:00:00', NULL, NULL, 1, 'COURSE008', '线性回归梯度下降', 'video/ml_linear.mp4', 1);
INSERT INTO `cloud_computing_course_resource` VALUES ('RES_ML_2', 'teacher', '2026-03-08 09:00:00', NULL, NULL, 2, 'COURSE008', '决策树与随机森林', 'document/ml_decision_tree.docx', 2);
INSERT INTO `cloud_computing_course_resource` VALUES ('RES_ML_3', 'teacher', '2026-03-10 09:00:00', NULL, NULL, 3, 'COURSE008', 'TensorFlow实践课件', 'courseware/ml_tf.pptx', 3);
INSERT INTO `cloud_computing_course_resource` VALUES ('RES_ML_4', 'teacher', '2026-03-12 09:00:00', NULL, NULL, 1, 'COURSE008', '模型评估与调参', 'video/ml_evaluation.mp4', 4);
INSERT INTO `cloud_computing_course_resource` VALUES ('RES_MOBILE_1', 'teacher', '2026-04-06 10:00:00', NULL, NULL, 1, 'COURSE016', 'Flutter Widget体系', 'video/flutter_widgets.mp4', 1);
INSERT INTO `cloud_computing_course_resource` VALUES ('RES_MOBILE_2', 'teacher', '2026-04-08 10:00:00', NULL, NULL, 2, 'COURSE016', 'uniapp快速上手指南', 'document/uni_app_guide.docx', 2);
INSERT INTO `cloud_computing_course_resource` VALUES ('RES_MOBILE_3', 'teacher', '2026-04-10 10:00:00', NULL, NULL, 3, 'COURSE016', '多端适配策略课件', 'courseware/mobile_adaptation.pptx', 3);
INSERT INTO `cloud_computing_course_resource` VALUES ('RES_MSA_1', 'teacher', '2026-04-19 10:00:00', NULL, NULL, 1, 'COURSE029', '服务拆分原则', 'video/msa_split.mp4', 1);
INSERT INTO `cloud_computing_course_resource` VALUES ('RES_MSA_2', 'teacher', '2026-04-21 10:00:00', NULL, NULL, 2, 'COURSE029', 'Spring Cloud组件详解', 'document/msa_springcloud.docx', 2);
INSERT INTO `cloud_computing_course_resource` VALUES ('RES_MSA_3', 'teacher', '2026-04-23 10:00:00', NULL, NULL, 3, 'COURSE029', '服务网格Istio课件', 'courseware/msa_istio.pptx', 3);
INSERT INTO `cloud_computing_course_resource` VALUES ('RES_PM_1', 'teacher', '2026-03-11 09:00:00', NULL, NULL, 1, 'COURSE009', '敏捷开发流程', 'video/pm_agile.mp4', 1);
INSERT INTO `cloud_computing_course_resource` VALUES ('RES_PM_2', 'teacher', '2026-03-13 09:00:00', NULL, NULL, 2, 'COURSE009', '用户故事编写指南', 'document/pm_userstory.docx', 2);
INSERT INTO `cloud_computing_course_resource` VALUES ('RES_PM_3', 'teacher', '2026-03-15 09:00:00', NULL, NULL, 3, 'COURSE009', 'Jira使用教程课件', 'courseware/pm_jira.pptx', 3);
INSERT INTO `cloud_computing_course_resource` VALUES ('RES_PY_1', 'teacher', '2026-01-16 10:00:00', NULL, NULL, 1, 'COURSE003', 'Python环境搭建与基础', 'video/python_setup.mp4', 1);
INSERT INTO `cloud_computing_course_resource` VALUES ('RES_PY_2', 'teacher', '2026-01-18 10:00:00', NULL, NULL, 2, 'COURSE003', 'Python数据类型详解', 'document/python_data_types.docx', 2);
INSERT INTO `cloud_computing_course_resource` VALUES ('RES_PY_3', 'teacher', '2026-01-20 10:00:00', NULL, NULL, 3, 'COURSE003', '函数与模块课件', 'courseware/python_func.pptx', 3);
INSERT INTO `cloud_computing_course_resource` VALUES ('RES_PY_4', 'teacher', '2026-01-22 10:00:00', NULL, NULL, 1, 'COURSE003', '面向对象编程实例', 'video/python_oop.mp4', 4);
INSERT INTO `cloud_computing_course_resource` VALUES ('RES_REACT_1', 'teacher', '2026-04-05 10:00:00', NULL, NULL, 1, 'COURSE015', 'React Hooks深入', 'video/react_hooks_deep.mp4', 1);
INSERT INTO `cloud_computing_course_resource` VALUES ('RES_REACT_2', 'teacher', '2026-04-07 10:00:00', NULL, NULL, 2, 'COURSE015', 'React性能优化', 'document/react_perf.docx', 2);
INSERT INTO `cloud_computing_course_resource` VALUES ('RES_REACT_3', 'teacher', '2026-04-09 10:00:00', NULL, NULL, 3, 'COURSE015', 'Next.js服务端渲染课件', 'courseware/react_nextjs.pptx', 3);
INSERT INTO `cloud_computing_course_resource` VALUES ('RES_SEC_1', 'teacher', '2026-04-12 10:00:00', NULL, NULL, 1, 'COURSE022', '渗透测试入门', 'video/security_penetration.mp4', 1);
INSERT INTO `cloud_computing_course_resource` VALUES ('RES_SEC_2', 'teacher', '2026-04-14 10:00:00', NULL, NULL, 2, 'COURSE022', 'OWASP Top 10', 'document/security_owasp.docx', 2);
INSERT INTO `cloud_computing_course_resource` VALUES ('RES_SEC_3', 'teacher', '2026-04-16 10:00:00', NULL, NULL, 3, 'COURSE022', '安全防御体系课件', 'courseware/security_defense.pptx', 3);
INSERT INTO `cloud_computing_course_resource` VALUES ('RES_SOFT_TEST_1', 'teacher', '2026-05-07 08:00:00', NULL, NULL, 1, '2052292234744172546', '测试理论概述', 'video/testing_theory.mp4', 1);
INSERT INTO `cloud_computing_course_resource` VALUES ('RES_SOFT_TEST_2', 'teacher', '2026-05-07 08:10:00', NULL, NULL, 2, '2052292234744172546', '单元测试实践文档', 'document/unit_test_guide.docx', 2);
INSERT INTO `cloud_computing_course_resource` VALUES ('RES_SOFT_TEST_3', 'teacher', '2026-05-07 08:20:00', NULL, NULL, 3, '2052292234744172546', '测试用例设计课件', 'courseware/test_case.pptx', 3);
INSERT INTO `cloud_computing_course_resource` VALUES ('RES_SOFT_TEST_4', 'teacher', '2026-05-07 08:30:00', NULL, NULL, 4, '2052292234744172546', '自动化测试链接', 'other/auto_test_tools.html', 4);
INSERT INTO `cloud_computing_course_resource` VALUES ('RES_SPEECH_1', 'teacher', '2026-04-16 10:00:00', NULL, NULL, 1, 'COURSE026', 'PPT设计原则', 'video/speech_ppt.mp4', 1);
INSERT INTO `cloud_computing_course_resource` VALUES ('RES_SPEECH_2', 'teacher', '2026-04-18 10:00:00', NULL, NULL, 2, 'COURSE026', '演讲结构模板', 'document/speech_structure.docx', 2);
INSERT INTO `cloud_computing_course_resource` VALUES ('RES_THESIS_1', 'teacher', '2026-03-16 09:00:00', NULL, NULL, 2, 'COURSE010', '毕业论文模板', 'document/thesis_template.docx', 1);
INSERT INTO `cloud_computing_course_resource` VALUES ('RES_THESIS_2', 'teacher', '2026-03-18 09:00:00', NULL, NULL, 1, 'COURSE010', '开题报告指导视频', 'video/thesis_proposal.mp4', 2);
INSERT INTO `cloud_computing_course_resource` VALUES ('RES_VUE_1', 'teacher', '2026-04-04 10:00:00', NULL, NULL, 1, 'COURSE014', 'Vue3响应式原理', 'video/vue3_reactivity.mp4', 1);
INSERT INTO `cloud_computing_course_resource` VALUES ('RES_VUE_2', 'teacher', '2026-04-06 10:00:00', NULL, NULL, 2, 'COURSE014', '项目架构设计文档', 'document/vue3_project_arch.docx', 2);
INSERT INTO `cloud_computing_course_resource` VALUES ('RES_VUE_3', 'teacher', '2026-04-08 10:00:00', NULL, NULL, 3, 'COURSE014', '状态管理Pinia课件', 'courseware/vue3_pinia.pptx', 3);
INSERT INTO `cloud_computing_course_resource` VALUES ('RES_VUE_4', 'teacher', '2026-04-10 10:00:00', NULL, NULL, 1, 'COURSE014', '实战项目部署', 'video/vue3_deploy.mp4', 4);

-- ----------------------------
-- Table structure for cloud_computing_course_type
-- ----------------------------
DROP TABLE IF EXISTS `cloud_computing_course_type`;
CREATE TABLE `cloud_computing_course_type`  (
  `id` varchar(36) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL,
  `create_by` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '创建人',
  `create_time` datetime NULL DEFAULT NULL COMMENT '创建日期',
  `update_by` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '更新人',
  `update_time` datetime NULL DEFAULT NULL COMMENT '更新日期',
  `course_type_name` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '课程分类名称',
  PRIMARY KEY (`id`) USING BTREE
) ENGINE = InnoDB CHARACTER SET = utf8mb4 COLLATE = utf8mb4_general_ci ROW_FORMAT = DYNAMIC;

-- ----------------------------
-- Records of cloud_computing_course_type
-- ----------------------------
INSERT INTO `cloud_computing_course_type` VALUES ('TYP001', 'admin', '2026-01-01 10:00:00', NULL, NULL, '基础理论');
INSERT INTO `cloud_computing_course_type` VALUES ('TYP002', 'admin', '2026-01-01 10:00:00', NULL, NULL, '开发与编程');
INSERT INTO `cloud_computing_course_type` VALUES ('TYP003', 'admin', '2026-01-01 10:00:00', NULL, NULL, '系统与运维');
INSERT INTO `cloud_computing_course_type` VALUES ('TYP004', 'admin', '2026-01-01 10:00:00', NULL, NULL, '数据与人工智能');
INSERT INTO `cloud_computing_course_type` VALUES ('TYP005', 'admin', '2026-01-01 10:00:00', NULL, NULL, '项目管理');
INSERT INTO `cloud_computing_course_type` VALUES ('TYP006', 'admin', '2026-01-01 10:00:00', NULL, NULL, '实训与项目');
INSERT INTO `cloud_computing_course_type` VALUES ('TYP007', 'admin', '2026-01-01 10:00:00', NULL, NULL, '前端开发');
INSERT INTO `cloud_computing_course_type` VALUES ('TYP008', 'admin', '2026-01-01 10:00:00', NULL, NULL, '信息安全');
INSERT INTO `cloud_computing_course_type` VALUES ('TYP009', 'admin', '2026-01-01 10:00:00', NULL, NULL, '职场软技能');

-- ----------------------------
-- Table structure for cloud_computing_student_learning_record
-- ----------------------------
DROP TABLE IF EXISTS `cloud_computing_student_learning_record`;
CREATE TABLE `cloud_computing_student_learning_record`  (
  `id` varchar(36) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL,
  `create_by` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '创建人',
  `update_by` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '更新人',
  `update_time` datetime NULL DEFAULT NULL COMMENT '更新日期',
  `course_id` varchar(32) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL COMMENT '课程名称',
  `user_id` varchar(32) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL COMMENT '学生名称',
  `content_id` varchar(32) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL COMMENT '学习资源名称',
  `learning_status` varchar(32) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL COMMENT '学习状态',
  `learning_time` int(11) NOT NULL COMMENT '累计学习时长(秒)',
  `last_learn_time` date NULL DEFAULT NULL COMMENT '最后一次学习时间',
  `create_time` datetime NOT NULL COMMENT '记录创建日期',
  PRIMARY KEY (`id`) USING BTREE
) ENGINE = InnoDB CHARACTER SET = utf8mb4 COLLATE = utf8mb4_general_ci ROW_FORMAT = DYNAMIC;

-- ----------------------------
-- Records of cloud_computing_student_learning_record
-- ----------------------------
INSERT INTO `cloud_computing_student_learning_record` VALUES ('2052765810754785282', NULL, NULL, NULL, 'COURSE004', '2052697664752599042', 'RES_JAVA_1', '0', 0, '2026-05-08', '2026-05-08 15:01:22');
INSERT INTO `cloud_computing_student_learning_record` VALUES ('2052769095842729985', NULL, 'admin', '2026-05-08 23:14:26', 'COURSE004', '1', 'RES_JAVA_2', '1', 0, '2026-05-08', '2026-05-08 15:14:25');
INSERT INTO `cloud_computing_student_learning_record` VALUES ('2052769099047178242', NULL, NULL, NULL, 'COURSE004', '1', 'RES_JAVA_3', '0', 0, '2026-05-08', '2026-05-08 15:14:26');

-- ----------------------------
-- Table structure for group_post
-- ----------------------------
DROP TABLE IF EXISTS `group_post`;
CREATE TABLE `group_post`  (
  `id` varchar(64) CHARACTER SET utf8 COLLATE utf8_unicode_ci NOT NULL,
  `group_id` varchar(64) CHARACTER SET utf8 COLLATE utf8_unicode_ci NOT NULL,
  `user_id` varchar(64) CHARACTER SET utf8 COLLATE utf8_unicode_ci NULL DEFAULT NULL,
  `username` varchar(128) CHARACTER SET utf8 COLLATE utf8_unicode_ci NULL DEFAULT NULL,
  `title` varchar(255) CHARACTER SET utf8 COLLATE utf8_unicode_ci NULL DEFAULT NULL,
  `content` text CHARACTER SET utf8 COLLATE utf8_unicode_ci NULL,
  `comment_count` int(11) NULL DEFAULT 0,
  `create_time` datetime NULL DEFAULT CURRENT_TIMESTAMP,
  PRIMARY KEY (`id`) USING BTREE
) ENGINE = MyISAM CHARACTER SET = utf8 COLLATE = utf8_unicode_ci ROW_FORMAT = DYNAMIC;

-- ----------------------------
-- Records of group_post
-- ----------------------------
INSERT INTO `group_post` VALUES ('POST_SOFT_2', '', '2052289849938415618', 'xs1', '自动化测试框架选型求助', 'Selenium、Cypress、Playwright哪个更适合我们项目？求指导。', 0, '2026-05-07 10:00:00');
INSERT INTO `group_post` VALUES ('POST_PY_1', 'GRP001', '1', 'admin', 'Python学习路线分享', '从基础语法到Flask/Django再到数据分析，附学习资源链接。', 3, '2026-05-01 09:30:00');
INSERT INTO `group_post` VALUES ('POST_PY_2', 'GRP001', '2052289567506567170', 'user', '遇到一个诡异的闭包问题', 'for循环中lambda的延迟绑定，如何正确捕获变量？', 1, '2026-05-02 10:30:00');
INSERT INTO `group_post` VALUES ('POST_PY_3', 'GRP001', '2052289849938415618', 'xs1', '第一周作业打卡', '已经完成环境搭建和第一个脚本，未来可期！', 2, '2026-05-03 11:30:00');
INSERT INTO `group_post` VALUES ('POST_JAVA_1', 'GRP002', '2052289567506567170', 'user', 'Spring Boot 3.0新特性总结', 'GraalVM原生支持、Observability、虚拟线程等，值得升级。', 2, '2026-05-02 10:30:00');
INSERT INTO `group_post` VALUES ('POST_JAVA_2', 'GRP002', '1', 'admin', 'JVM调优实战案例', '一次线上Full GC频繁的排查过程，分享工具和思路。', 1, '2026-05-03 11:00:00');
INSERT INTO `group_post` VALUES ('POST_K8S_1', 'GRP003', '2052289849938415618', 'xs1', 'K8s网络模型初探', 'CNI、Calico、Flannel选型对比。', 0, '2026-05-03 11:30:00');
INSERT INTO `group_post` VALUES ('POST_K8S_2', 'GRP003', '1', 'admin', '如何在K8s中优雅处理滚动更新', 'maxSurge、maxUnavailable的最佳实践。', 2, '2026-05-04 12:00:00');
INSERT INTO `group_post` VALUES ('POST_MATH_1', 'GRP004', '1', 'admin', '离散数学期末复习重点', '图论、代数结构、逻辑推理必考题型总结。', 3, '2026-05-04 12:30:00');
INSERT INTO `group_post` VALUES ('POST_MATH_2', 'GRP004', '2052289567506567170', 'user', '有一起刷题的朋友吗？', '每天打卡一道证明题，互相监督。', 1, '2026-05-05 13:00:00');
INSERT INTO `group_post` VALUES ('POST_AI_1', 'GRP005', '2052289567506567170', 'user', 'Transformer论文精读笔记', 'Attention Is All You Need 核心思想与代码实现。', 2, '2026-05-05 13:30:00');
INSERT INTO `group_post` VALUES ('POST_AI_2', 'GRP005', '1', 'admin', 'Kaggle竞赛入门指南', '如何快速上手房价预测、泰坦尼克等新手赛。', 0, '2026-05-06 14:00:00');
INSERT INTO `group_post` VALUES ('POST_FE_1', 'GRP006', '2052289849938415618', 'xs1', 'Vue3与React Hooks对比', '从设计哲学到实际编码体验，一点见解。', 1, '2026-05-06 14:30:00');
INSERT INTO `group_post` VALUES ('POST_FE_2', 'GRP006', '1', 'admin', '前端性能优化清单', '图片懒加载、代码分割、CDN缓存……持续更新。', 0, '2026-05-07 15:00:00');
INSERT INTO `group_post` VALUES ('POST_PMP_1', 'GRP007', '1', 'admin', 'PMP新版考试大纲解读', '敏捷内容占比增加，备考策略需调整。', 1, '2026-05-07 15:30:00');
INSERT INTO `group_post` VALUES ('POST_PMP_2', 'GRP007', '2052289567506567170', 'user', '项目管理软件推荐', 'Jira、Notion、Trello、Asana对比。', 0, '2026-05-08 09:00:00');
INSERT INTO `group_post` VALUES ('POST_SOFT_1', '', '1', 'admin', '测试用例评审注意事项', '大家在写测试用例的时候，边界值分析和等价类划分是基础，但容易遗漏场景。分享一份检查清单。', 0, '2026-05-07 09:00:00');

-- ----------------------------
-- Table structure for message_notice
-- ----------------------------
DROP TABLE IF EXISTS `message_notice`;
CREATE TABLE `message_notice`  (
  `id` varchar(64) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL,
  `create_by` varchar(64) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL,
  `create_time` datetime NULL DEFAULT NULL,
  `update_by` varchar(64) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL,
  `update_time` datetime NULL DEFAULT NULL,
  `title` varchar(200) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL,
  `content` text CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL,
  `level` int(11) NULL DEFAULT 1,
  `target_role` varchar(64) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL,
  `enabled` tinyint(4) NULL DEFAULT 1,
  PRIMARY KEY (`id`) USING BTREE
) ENGINE = InnoDB CHARACTER SET = utf8mb4 COLLATE = utf8mb4_general_ci ROW_FORMAT = DYNAMIC;

-- ----------------------------
-- Records of message_notice
-- ----------------------------
INSERT INTO `message_notice` VALUES ('MSG001', 'admin', '2026-05-01 08:00:00', 'admin', '2026-05-09 00:08:43', '系统升级通知', '平台将于5月5日凌晨2:00-5:00进行维护升级，届时无法访问，请提前做好安排。', 1, 'all,1,2,3', 1);
INSERT INTO `message_notice` VALUES ('MSG002', 'admin', '2026-05-06 09:00:00', 'admin', '2026-05-09 00:08:51', '新课程上线通知', '【机器学习实战】和【Go语言并发编程】已上线，欢迎选课！', 2, 'student,2,3', 1);
INSERT INTO `message_notice` VALUES ('MSG003', 'admin', '2026-05-07 10:00:00', 'admin', '2026-05-09 00:08:38', '教师备课提醒', '请各位教师及时完成下周课程资源的更新和维护。', 2, 'teacher,2,1,3', 1);
INSERT INTO `message_notice` VALUES ('MSG004', 'admin', '2026-05-08 11:00:00', 'admin', '2026-05-09 00:08:32', '作业截止提醒', '本周五17:00前请提交Python课后作业，逾期系统自动关闭入口。', 1, 'student,2,3', 1);
INSERT INTO `message_notice` VALUES ('MSG005', 'admin', '2026-05-08 12:00:00', 'admin', '2026-05-09 00:08:25', '学习小组活动通知', '【Python互助小组】将于本周六晚8点举办线上答疑会，欢迎参加。', 3, 'all,2,3,1', 1);
INSERT INTO `message_notice` VALUES ('MSG006', '2052289567506567170', '2026-05-08 13:00:00', 'admin', '2026-05-09 00:08:16', '课程评价邀请', '您正在学习的《Kubernetes实战》课程已完结，请前往评价，帮助课程改进。', 2, 'student,3,2', 1);

-- ----------------------------
-- Table structure for message_notice_read
-- ----------------------------
DROP TABLE IF EXISTS `message_notice_read`;
CREATE TABLE `message_notice_read`  (
  `id` varchar(64) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL,
  `message_id` varchar(64) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL,
  `user_id` varchar(64) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL,
  `read_time` datetime NULL DEFAULT NULL,
  PRIMARY KEY (`id`) USING BTREE,
  UNIQUE INDEX `uk_message_user`(`message_id`, `user_id`) USING BTREE
) ENGINE = InnoDB CHARACTER SET = utf8mb4 COLLATE = utf8mb4_general_ci ROW_FORMAT = DYNAMIC;

-- ----------------------------
-- Records of message_notice_read
-- ----------------------------
INSERT INTO `message_notice_read` VALUES ('1', '123', '123', '2026-05-06 17:56:19');
INSERT INTO `message_notice_read` VALUES ('2051983953461174274', '2051983914886160385', '2051983770350444545', '2026-05-06 19:14:33');
INSERT INTO `message_notice_read` VALUES ('2052265869319868417', '2051983914886160385', '1', '2026-05-07 05:54:47');
INSERT INTO `message_notice_read` VALUES ('2052295582893477890', '2052295566963511297', '1', '2026-05-07 07:52:51');
INSERT INTO `message_notice_read` VALUES ('2052295600459223041', '2052295566963511297', '2052289567506567170', '2026-05-07 07:52:56');
INSERT INTO `message_notice_read` VALUES ('2052300222917775362', '2052295566963511297', '2052289849938415618', '2026-05-07 08:11:18');
INSERT INTO `message_notice_read` VALUES ('READ001', 'MSG001', '1', '2026-05-01 09:00:00');
INSERT INTO `message_notice_read` VALUES ('READ002', 'MSG001', '2052289567506567170', '2026-05-01 09:05:00');
INSERT INTO `message_notice_read` VALUES ('READ003', 'MSG002', '1', '2026-05-06 10:00:00');
INSERT INTO `message_notice_read` VALUES ('READ004', 'MSG002', '2052289567506567170', '2026-05-06 10:30:00');
INSERT INTO `message_notice_read` VALUES ('READ005', 'MSG003', '2052289567506567170', '2026-05-07 11:00:00');
INSERT INTO `message_notice_read` VALUES ('READ006', 'MSG004', '2052289849938415618', '2026-05-08 11:30:00');

-- ----------------------------
-- Table structure for persistent_logins
-- ----------------------------
DROP TABLE IF EXISTS `persistent_logins`;
CREATE TABLE `persistent_logins`  (
  `username` varchar(64) CHARACTER SET utf8 COLLATE utf8_unicode_ci NOT NULL,
  `series` varchar(64) CHARACTER SET utf8 COLLATE utf8_unicode_ci NOT NULL,
  `token` varchar(64) CHARACTER SET utf8 COLLATE utf8_unicode_ci NOT NULL,
  `last_used` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  PRIMARY KEY (`series`) USING BTREE
) ENGINE = MyISAM CHARACTER SET = utf8 COLLATE = utf8_unicode_ci ROW_FORMAT = DYNAMIC;

-- ----------------------------
-- Records of persistent_logins
-- ----------------------------
INSERT INTO `persistent_logins` VALUES ('user', 'SERIES_USER_001', 'TOKEN_USER_001_DEF', '2026-05-08 13:30:00');
INSERT INTO `persistent_logins` VALUES ('xs1', 'SERIES_XS1_001', 'TOKEN_XS1_001_GHI', '2026-05-08 12:00:00');

-- ----------------------------
-- Table structure for post_comment
-- ----------------------------
DROP TABLE IF EXISTS `post_comment`;
CREATE TABLE `post_comment`  (
  `id` varchar(64) CHARACTER SET utf8 COLLATE utf8_unicode_ci NOT NULL,
  `post_id` varchar(64) CHARACTER SET utf8 COLLATE utf8_unicode_ci NOT NULL,
  `user_id` varchar(64) CHARACTER SET utf8 COLLATE utf8_unicode_ci NULL DEFAULT NULL,
  `username` varchar(128) CHARACTER SET utf8 COLLATE utf8_unicode_ci NULL DEFAULT NULL,
  `content` text CHARACTER SET utf8 COLLATE utf8_unicode_ci NULL,
  `create_time` datetime NULL DEFAULT CURRENT_TIMESTAMP,
  PRIMARY KEY (`id`) USING BTREE
) ENGINE = MyISAM CHARACTER SET = utf8 COLLATE = utf8_unicode_ci ROW_FORMAT = DYNAMIC;

-- ----------------------------
-- Records of post_comment
-- ----------------------------
INSERT INTO `post_comment` VALUES ('2049363658350997505', '2049363642311979010', '1', 'admin', '123', '2026-04-29 13:42:26');
INSERT INTO `post_comment` VALUES ('2049363687740485633', '2049363675258236930', '1', 'admin', '123', '2026-04-29 13:42:33');
INSERT INTO `post_comment` VALUES ('2049363692006092802', '2049363675258236930', '1', 'admin', '123', '2026-04-29 13:42:34');
INSERT INTO `post_comment` VALUES ('2049363698675036161', '2049363675258236930', '1', 'admin', '123', '2026-04-29 13:42:36');
INSERT INTO `post_comment` VALUES ('2049414600643522561', '2049363675258236930', '1', 'admin', '123', '2026-04-29 17:04:52');
INSERT INTO `post_comment` VALUES ('2050228104132100098', '2050228081101176833', '1', 'admin', '测试', '2026-05-01 14:57:26');
INSERT INTO `post_comment` VALUES ('2050228764609150977', '2050228081101176833', '1', 'admin', '暗杀看似简单', '2026-05-01 15:00:04');
INSERT INTO `post_comment` VALUES ('2050229071334408193', '2050228902689832961', '1', 'admin', '1231313', '2026-05-01 15:01:17');
INSERT INTO `post_comment` VALUES ('2050230249682173953', '2050229979090845697', '1', 'admin', '@！@@@@dasjlkhfkladsjkfhklasdjhfklajsd【alskdhfklasjdhflkjashdf', '2026-05-01 15:05:58');
INSERT INTO `post_comment` VALUES ('2052296235539763202', '2052296098864173058', '2052289567506567170', 'ls1', '非常好的问题', '2026-05-07 07:55:27');
INSERT INTO `post_comment` VALUES ('2052296269446516738', '2052296098864173058', '2052289567506567170', 'ls1', '这样一个软件测试问题阿贾克斯的回复看见拉萨的回复卡死的回复贷记卡是否可见哈师大快放假了哈圣诞快乐爱神的箭客户发考虑实际到货付款了就爱上发生口角倒海翻江卡明黄色的官方就卡省的卡拉省的风口浪尖哈桑打开考砸了手机号看了就杀了科技大厦好看了就啊圣诞快乐就哈萨克来得及发货卡拉技术大会可拉斯基和对方可拉斯基和刻录机撒地方就按时打卡记录发生看见了东方航空拉萨酱豆腐后来卡就是 会发生空间的恢复了卡死的回复卢卡斯的话抠脚大汉风口浪尖安徽的疯狂练级啊士大夫抠脚大汉分厘卡士大夫刻录机阿斯顿分厘卡士大夫卢卡斯的积分和看拉萨酱豆腐卢卡斯京东方和可垃圾啊收到回复可垃圾啊是的风口浪尖哈桑的风口浪尖啊士大夫艰苦黑暗时代快来解放这样一个软件测试问题阿贾克斯的回复看见拉萨的回复卡死的回复贷记卡是否可见哈师大快放假了哈圣诞快乐爱神的箭客户发考虑实际到货付款了就爱上发生口角倒海翻江卡明黄色的官方就卡省的卡拉省的风口浪尖哈桑打开考砸了手机号看了就杀了科技大厦好看了就啊圣诞快乐就哈萨克来得及发货卡拉技术大会可拉斯基和对方可拉斯基和刻录机撒地方就按时打卡记录发生看见了东方航空拉萨酱豆腐后来卡就是 会发生空间的恢复了卡死的回复卢卡斯的话抠脚大汉风口浪尖安徽的疯狂练级啊士大夫抠脚大汉分厘卡士大夫刻录机阿斯顿分厘卡士大夫卢卡斯的积分和看拉萨酱豆腐卢卡斯京东方和可垃圾啊收到回复可垃圾啊是的风口浪尖哈桑的风口浪尖啊士大夫艰苦黑暗时代快来解放这样一个软件测试问题阿贾克斯的回复看见拉萨的回复卡死的回复贷记卡是否可见哈师大快放假了哈圣诞快乐爱神的箭客户发考虑实际到货付款了就爱上发生口角倒海翻江卡明黄色的官方就卡省的卡拉省的风口浪尖哈桑打开考砸了手机号看了就杀了科技大厦好看了就啊圣诞快乐就哈萨克来得及发货卡拉技术大会可拉斯基和对方可拉斯基和刻录机撒地方就按时打卡记录发生看见了东方航空拉萨酱豆腐后来卡就是 会发生空间的恢复了卡死的回复卢卡斯的话抠脚大汉风口浪尖安徽的疯狂练级啊士大夫抠脚大汉分厘卡士大夫刻录机阿斯顿分厘卡士大夫卢卡斯的积分和看拉萨酱豆腐卢卡斯京东方和可垃圾啊收到回复可垃圾啊是的风口浪尖哈桑的风口浪尖啊士大夫艰苦黑暗时代快来解放这样一个软件测试问题阿贾克斯的回复看见拉萨的回复卡死的回复贷记卡是否可见哈师大快放假了哈圣诞快乐爱神的箭客户发考虑实际到货付款了就爱上发生口角倒海翻江卡明黄色的官方就卡省的卡拉省的风口浪尖哈桑打开考砸了手机号看了就杀了科技大厦好看了就啊圣诞快乐就哈萨克来得及发货卡拉技术大会可拉斯基和对方可拉斯基和刻录机撒地方就按时打卡记录发生看见了东方航空拉萨酱豆腐后来卡就是 会发生空间的恢复了卡死的回复卢卡斯的话抠脚大汉风口浪尖安徽的疯狂练级啊士大夫抠脚大汉分厘卡士大夫刻录机阿斯顿分厘卡士大夫卢卡斯的积分和看拉萨酱豆腐卢卡斯京东方和可垃圾啊收到回复可垃圾啊是的风口浪尖哈桑的风口浪尖啊士大夫艰苦黑暗时代快来解放这样一个软件测试问题阿贾克斯的回复看见拉萨的回复卡死的回复贷记卡是否可见哈师大快放假了哈圣诞快乐爱神的箭客户发考虑实际到货付款了就爱上发生口角倒海翻江卡明黄色的官方就卡省的卡拉省的风口浪尖哈桑打开考砸了手机号看了就杀了科技大厦好看了就啊圣诞快乐就哈萨克来得及发货卡拉技术大会可拉斯基和对方可拉斯基和刻录机撒地方就按时打卡记录发生看见了东方航空拉萨酱豆腐后来卡就是 会发生空间的恢复了卡死的回复卢卡斯的话抠脚大汉风口浪尖安徽的疯狂练级啊士大夫抠脚大汉分厘卡士大夫刻录机阿斯顿分厘卡士大夫卢卡斯的积分和看拉萨酱豆腐卢卡斯京东方和可垃圾啊收到回复可垃圾啊是的风口浪尖哈桑的风口浪尖啊士大夫艰苦黑暗时代快来解放这样一个软件测试问题阿贾克斯的回复看见拉萨的回复卡死的回复贷记卡是否可见哈师大快放假了哈圣诞快乐爱神的箭客户发考虑实际到货付款了就爱上发生口角倒海翻江卡明黄色的官方就卡省的卡拉省的风口浪尖哈桑打开考砸了手机号看了就杀了科技大厦好看了就啊圣诞快乐就哈萨克来得及发货卡拉技术大会可拉斯基和对方可拉斯基和刻录机撒地方就按时打卡记录发生看见了东方航空拉萨酱豆腐后来卡就是 会发生空间的恢复了卡死的回复卢卡斯的话抠脚大汉风口浪尖安徽的疯狂练级啊士大夫抠脚大汉分厘卡士大夫刻录机阿斯顿分厘卡士大夫卢卡斯的积分和看拉萨酱豆腐卢卡斯京东方和可垃圾啊收到回复可垃圾啊是的风口浪尖哈桑的风口浪尖啊士大夫艰苦黑暗时代快来解放这样一个软件测试问题阿贾克斯的回复看见拉萨的回复卡死的回复贷记卡是否可见哈师大快放假了哈圣诞快乐爱神的箭客户发考虑实际到货付款了就爱上发生口角倒海翻江卡明黄色的官方就卡省的卡拉省的风口浪尖哈桑打开考砸了手机号看了就杀了科技大厦好看了就啊圣诞快乐就哈萨克来得及发货卡拉技术大会可拉斯基和对方可拉斯基和刻录机撒地方就按时打卡记录发生看见了东方航空拉萨酱豆腐后来卡就是 会发生空间的恢复了卡死的回复卢卡斯的话抠脚大汉风口浪尖安徽的疯狂练级啊士大夫抠脚大汉分厘卡士大夫刻录机阿斯顿分厘卡士大夫卢卡斯的积分和看拉萨酱豆腐卢卡斯京东方和可垃圾啊收到回复可垃圾啊是的风口浪尖哈桑的风口浪尖啊士大夫艰苦黑暗时代快来解放这样一个软件测试问题阿贾克斯的回复看见拉萨的回复卡死的回复贷记卡是否可见哈师大快放假了哈圣诞快乐爱神的箭客户发考虑实际到货付款了就爱上发生口角倒海翻江卡明黄色的官方就卡省的卡拉省的风口浪尖哈桑打开考砸了手机号看了就杀了科技大厦好看了就啊圣诞快乐就哈萨克来得及发货卡拉技术大会可拉斯基和对方可拉斯基和刻录机撒地方就按时打卡记录发生看见了东方航空拉萨酱豆腐后来卡就是 会发生空间的恢复了卡死的回复卢卡斯的话抠脚大汉风口浪尖安徽的疯狂练级啊士大夫抠脚大汉分厘卡士大夫刻录机阿斯顿分厘卡士大夫卢卡斯的积分和看拉萨酱豆腐卢卡斯京东方和可垃圾啊收到回复可垃圾啊是的风口浪尖哈桑的风口浪尖啊士大夫艰苦黑暗时代快来解放这样一个软件测试问题阿贾克斯的回复看见拉萨的回复卡死的回复贷记卡是否可见哈师大快放假了哈圣诞快乐爱神的箭客户发考虑实际到货付款了就爱上发生口角倒海翻江卡明黄色的官方就卡省的卡拉省的风口浪尖哈桑打开考砸了手机号看了就杀了科技大厦好看了就啊圣诞快乐就哈萨克来得及发货卡拉技术大会可拉斯基和对方可拉斯基和刻录机撒地方就按时打卡记录发生看见了东方航空拉萨酱豆腐后来卡就是 会发生空间的恢复了卡死的回复卢卡斯的话抠脚大汉风口浪尖安徽的疯狂练级啊士大夫抠脚大汉分厘卡士大夫刻录机阿斯顿分厘卡士大夫卢卡斯的积分和看拉萨酱豆腐卢卡斯京东方和可垃圾啊收到回复可垃圾啊是的风口浪尖哈桑的风口浪尖啊士大夫艰苦黑暗时代快来解放这样一个软件测试问题阿贾克斯的回复看见拉萨的回复卡死的回复贷记卡是否可见哈师大快放假了哈圣诞快乐爱神的箭客户发考虑实际到货付款了就爱上发生口角倒海翻江卡明黄色的官方就卡省的卡拉省的风口浪尖哈桑打开考砸了手机号看了就杀了科技大厦好看了就啊圣诞快乐就哈萨克来得及发货卡拉技术大会可拉斯基和对方可拉斯基和刻录机撒地方就按时打卡记录发生看见了东方航空拉萨酱豆腐后来卡就是 会发生空间的恢复了卡死的回复卢卡斯的话抠脚大汉风口浪尖安徽的疯狂练级啊士大夫抠脚大汉分厘卡士大夫刻录机阿斯顿分厘卡士大夫卢卡斯的积分和看拉萨酱豆腐卢卡斯京东方和可垃圾啊收到回复可垃圾啊是的风口浪尖哈桑的风口浪尖啊士大夫艰苦黑暗时代快来解放这样一个软件测试问题阿贾克斯的回复看见拉萨的回复卡死的回复贷记卡是否可见哈师大快放假了哈圣诞快乐爱神的箭客户发考虑实际到货付款了就爱上发生口角倒海翻江卡明黄色的官方就卡省的卡拉省的风口浪尖哈桑打开考砸了手机号看了就杀了科技大厦好看了就啊圣诞快乐就哈萨克来得及发货卡拉技术大会可拉斯基和对方可拉斯基和刻录机撒地方就按时打卡记录发生看见了东方航空拉萨酱豆腐后来卡就是 会发生空间的恢复了卡死的回复卢卡斯的话抠脚大汉风口浪尖安徽的疯狂练级啊士大夫抠脚大汉分厘卡士大夫刻录机阿斯顿分厘卡士大夫卢卡斯的积分和看拉萨酱豆腐卢卡斯京东方和可垃圾啊收到回复可垃圾啊是的风口浪尖哈桑的风口浪尖啊士大夫艰苦黑暗时代快来解放这样一个软件测试问题阿贾克斯的回复看见拉萨的回复卡死的回复贷记卡是否可见哈师大快放假了哈圣诞快乐爱神的箭客户发考虑实际到货付款了就爱上发生口角倒海翻江卡明黄色的官方就卡省的卡拉省的风口浪尖哈桑打开考砸了手机号看了就杀了科技大厦好看了就啊圣诞快乐就哈萨克来得及发货卡拉技术大会可拉斯基和对方可拉斯基和刻录机撒地方就按时打卡记录发生看见了东方航空拉萨酱豆腐后来卡就是 会发生空间的恢复了卡死的回复卢卡斯的话抠脚大汉风口浪尖安徽的疯狂练级啊士大夫抠脚大汉分厘卡士大夫刻录机阿斯顿分厘卡士大夫卢卡斯的积分和看拉萨酱豆腐卢卡斯京东方和可垃圾啊收到回复可垃圾啊是的风口浪尖哈桑的风口浪尖啊士大夫艰苦黑暗时代快来解放这样一个软件测试问题阿贾克斯的回复看见拉萨的回复卡死的回复贷记卡是否可见哈师大快放假了哈圣诞快乐爱神的箭客户发考虑实际到货付款了就爱上发生口角倒海翻江卡明黄色的官方就卡省的卡拉省的风口浪尖哈桑打开考砸了手机号看了就杀了科技大厦好看了就啊圣诞快乐就哈萨克来得及发货卡拉技术大会可拉斯基和对方可拉斯基和刻录机撒地方就按时打卡记录发生看见了东方航空拉萨酱豆腐后来卡就是 会发生空间的恢复了卡死的回复卢卡斯的话抠脚大汉风口浪尖安徽的疯狂练级啊士大夫抠脚大汉分厘卡士大夫刻录机阿斯顿分厘卡士大夫卢卡斯的积分和看拉萨酱豆腐卢卡斯京东方和可垃圾啊收到回复可垃圾啊是的风口浪尖哈桑的风口浪尖啊士大夫艰苦黑暗时代快来解放这样一个软件测试问题阿贾克斯的回复看见拉萨的回复卡死的回复贷记卡是否可见哈师大快放假了哈圣诞快乐爱神的箭客户发考虑实际到货付款了就爱上发生口角倒海翻江卡明黄色的官方就卡省的卡拉省的风口浪尖哈桑打开考砸了手机号看了就杀了科技大厦好看了就啊圣诞快乐就哈萨克来得及发货卡拉技术大会可拉斯基和对方可拉斯基和刻录机撒地方就按时打卡记录发生看见了东方航空拉萨酱豆腐后来卡就是 会发生空间的恢复了卡死的回复卢卡斯的话抠脚大汉风口浪尖安徽的疯狂练级啊士大夫抠脚大汉分厘卡士大夫刻录机阿斯顿分厘卡士大夫卢卡斯的积分和看拉萨酱豆腐卢卡斯京东方和可垃圾啊收到回复可垃圾啊是的风口浪尖哈桑的风口浪尖啊士大夫艰苦黑暗时代快来解放这样一个软件测试问题阿贾克斯的回复看见拉萨的回复卡死的回复贷记卡是否可见哈师大快放假了哈圣诞快乐爱神的箭客户发考虑实际到货付款了就爱上发生口角倒海翻江卡明黄色的官方就卡省的卡拉省的风口浪尖哈桑打开考砸了手机号看了就杀了科技大厦好看了就啊圣诞快乐就哈萨克来得及发货卡拉技术大会可拉斯基和对方可拉斯基和刻录机撒地方就按时打卡记录发生看见了东方航空拉萨酱豆腐后来卡就是 会发生空间的恢复了卡死的回复卢卡斯的话抠脚大汉风口浪尖安徽的疯狂练级啊士大夫抠脚大汉分厘卡士大夫刻录机阿斯顿分厘卡士大夫卢卡斯的积分和看拉萨酱豆腐卢卡斯京东方和可垃圾啊收到回复可垃圾啊是的风口浪尖哈桑的风口浪尖啊士大夫艰苦黑暗时代快来解放这样一个软件测试问题阿贾克斯的回复看见拉萨的回复卡死的回复贷记卡是否可见哈师大快放假了哈圣诞快乐爱神的箭客户发考虑实际到货付款了就爱上发生口角倒海翻江卡明黄色的官方就卡省的卡拉省的风口浪尖哈桑打开考砸了手机号看了就杀了科技大厦好看了就啊圣诞快乐就哈萨克来得及发货卡拉技术大会可拉斯基和对方可拉斯基和刻录机撒地方就按时打卡记录发生看见了东方航空拉萨酱豆腐后来卡就是 会发生空间的恢复了卡死的回复卢卡斯的话抠脚大汉风口浪尖安徽的疯狂练级啊士大夫抠脚大汉分厘卡士大夫刻录机阿斯顿分厘卡士大夫卢卡斯的积分和看拉萨酱豆腐卢卡斯京东方和可垃圾啊收到回复可垃圾啊是的风口浪尖哈桑的风口浪尖啊士大夫艰苦黑暗时代快来解放', '2026-05-07 07:55:35');
INSERT INTO `post_comment` VALUES ('2052298100998410241', '2052298048389255169', '2052289567506567170', '老师1', '还可以', '2026-05-07 08:02:52');
INSERT INTO `post_comment` VALUES ('2052298115632336897', '2052298048389255169', '2052289567506567170', '老师1', '还哦行', '2026-05-07 08:02:55');
INSERT INTO `post_comment` VALUES ('2052298336911233026', '2052298048389255169', '2052289849938415618', 'xs1', '我觉得还不错', '2026-05-07 08:03:48');
INSERT INTO `post_comment` VALUES ('COMM_PY_1_1', 'POST_PY_1', '2052289567506567170', 'user', '非常感谢分享，尤其是Django部分对我很有帮助。', '2026-05-01 10:00:00');
INSERT INTO `post_comment` VALUES ('COMM_PY_1_2', 'POST_PY_1', '2052289849938415618', 'xs1', '请问有推荐的练手项目吗？', '2026-05-01 10:30:00');
INSERT INTO `post_comment` VALUES ('COMM_PY_1_3', 'POST_PY_1', '1', 'admin', '可以做一个个人博客，或者爬虫练手。', '2026-05-01 11:00:00');
INSERT INTO `post_comment` VALUES ('COMM_PY_2_1', 'POST_PY_2', '1', 'admin', '可以用默认参数或者闭包内立即执行函数解决。', '2026-05-02 11:00:00');
INSERT INTO `post_comment` VALUES ('COMM_JAVA_1_1', 'POST_JAVA_1', '1', 'admin', '虚拟线程确实很香，但要注意搭配使用。', '2026-05-02 11:30:00');
INSERT INTO `post_comment` VALUES ('COMM_JAVA_1_2', 'POST_JAVA_1', '2052289849938415618', 'xs1', '我们项目还在用2.7，得推动升级了。', '2026-05-02 12:00:00');
INSERT INTO `post_comment` VALUES ('COMM_K8S_2_1', 'POST_K8S_2', '2052289567506567170', 'user', '还有readiness probe也很重要。', '2026-05-04 12:30:00');
INSERT INTO `post_comment` VALUES ('COMM_K8S_2_2', 'POST_K8S_2', '2052289849938415618', 'xs1', '谢谢，这就去实验。', '2026-05-04 13:00:00');
INSERT INTO `post_comment` VALUES ('COMM_MATH_1_1', 'POST_MATH_1', '2052289567506567170', 'user', '图论部分最好再补充一下最短路径算法，经常考。', '2026-05-04 13:30:00');
INSERT INTO `post_comment` VALUES ('COMM_MATH_1_2', 'POST_MATH_1', '2052289849938415618', 'xs1', '收藏了，考试前拿出来看。', '2026-05-04 14:00:00');
INSERT INTO `post_comment` VALUES ('COMM_MATH_1_3', 'POST_MATH_1', '1', 'admin', '已更新，谢谢提醒。', '2026-05-04 14:30:00');
INSERT INTO `post_comment` VALUES ('COMM_AI_1_1', 'POST_AI_1', '1', 'admin', '代码实现用的是PyTorch还是TensorFlow？', '2026-05-05 14:00:00');
INSERT INTO `post_comment` VALUES ('COMM_AI_1_2', 'POST_AI_1', '2052289567506567170', 'user', '用的PyTorch，更灵活一些。', '2026-05-05 14:30:00');
INSERT INTO `post_comment` VALUES ('COMM_FE_1_1', 'POST_FE_1', '1', 'admin', '写得不错，React的useEffect 和 Vue的watch 确实有区别。', '2026-05-06 15:00:00');
INSERT INTO `post_comment` VALUES ('COMM_PMP_1_1', 'POST_PMP_1', '2052289567506567170', 'user', '备考资料有推荐吗？', '2026-05-07 16:00:00');

-- ----------------------------
-- Table structure for resource_review
-- ----------------------------
DROP TABLE IF EXISTS `resource_review`;
CREATE TABLE `resource_review`  (
  `id` varchar(64) CHARACTER SET utf8 COLLATE utf8_unicode_ci NOT NULL,
  `resource_id` varchar(64) CHARACTER SET utf8 COLLATE utf8_unicode_ci NULL DEFAULT NULL,
  `course_id` varchar(64) CHARACTER SET utf8 COLLATE utf8_unicode_ci NOT NULL,
  `user_id` varchar(64) CHARACTER SET utf8 COLLATE utf8_unicode_ci NULL DEFAULT NULL,
  `username` varchar(128) CHARACTER SET utf8 COLLATE utf8_unicode_ci NULL DEFAULT NULL,
  `rating` int(11) NULL DEFAULT NULL,
  `content` text CHARACTER SET utf8 COLLATE utf8_unicode_ci NULL,
  `likes` int(11) NULL DEFAULT 0,
  `create_time` datetime NULL DEFAULT CURRENT_TIMESTAMP,
  PRIMARY KEY (`id`) USING BTREE
) ENGINE = MyISAM CHARACTER SET = utf8 COLLATE = utf8_unicode_ci ROW_FORMAT = DYNAMIC;

-- ----------------------------
-- Records of resource_review
-- ----------------------------
INSERT INTO `resource_review` VALUES ('REV_PY_1', 'RES_PY_1', 'COURSE003', '1', 'admin', 5, 'Python入门视频讲得非常清晰，适合小白。', 3, '2026-05-08 10:00:00');
INSERT INTO `resource_review` VALUES ('REV_PY_2', 'RES_PY_2', 'COURSE003', '2052289567506567170', 'user', 4, '文档整理得很详细，但缺少实际项目案例。', 1, '2026-05-08 11:00:00');
INSERT INTO `resource_review` VALUES ('REV_JAVA_1', 'RES_JAVA_1', 'COURSE004', '2052289849938415618', 'xs1', 5, 'JVM内存模型讲得深入浅出，点赞。', 5, '2026-05-08 12:00:00');
INSERT INTO `resource_review` VALUES ('REV_DOCKER_1', 'RES_DOCKER_1', 'COURSE007', '1', 'admin', 4, 'Dockerfile最佳实践很实用，但缺少多阶段构建的例子。', 2, '2026-05-08 13:00:00');
INSERT INTO `resource_review` VALUES ('REV_ML_1', 'RES_ML_1', 'COURSE008', '2052289567506567170', 'user', 5, '梯度下降的数学推导和代码实现结合得很好。', 4, '2026-05-08 14:00:00');
INSERT INTO `resource_review` VALUES ('REV_VUE_1', 'RES_VUE_1', 'COURSE014', '2052289849938415618', 'xs1', 3, '响应式原理讲得有点快，建议配合图解慢一点。', 1, '2026-05-08 15:00:00');
INSERT INTO `resource_review` VALUES ('REV_GO_1', 'RES_GO_1', 'COURSE012', '1', 'admin', 5, 'goroutine例子非常生动，一看就懂。', 6, '2026-05-08 16:00:00');

-- ----------------------------
-- Table structure for study_group
-- ----------------------------
DROP TABLE IF EXISTS `study_group`;
CREATE TABLE `study_group`  (
  `id` varchar(64) CHARACTER SET utf8 COLLATE utf8_unicode_ci NOT NULL,
  `name` varchar(255) CHARACTER SET utf8 COLLATE utf8_unicode_ci NOT NULL,
  `description` text CHARACTER SET utf8 COLLATE utf8_unicode_ci NULL,
  `owner_id` varchar(64) CHARACTER SET utf8 COLLATE utf8_unicode_ci NULL DEFAULT NULL,
  `owner_name` varchar(128) CHARACTER SET utf8 COLLATE utf8_unicode_ci NULL DEFAULT NULL,
  `create_time` datetime NULL DEFAULT CURRENT_TIMESTAMP,
  PRIMARY KEY (`id`) USING BTREE
) ENGINE = MyISAM CHARACTER SET = utf8 COLLATE = utf8_unicode_ci ROW_FORMAT = DYNAMIC;

-- ----------------------------
-- Records of study_group
-- ----------------------------
INSERT INTO `study_group` VALUES ('GRP001', 'Python入门互助小组', '一起从零开始学习Python，交流作业和项目', '1', 'admin', '2026-05-01 09:00:00');
INSERT INTO `study_group` VALUES ('GRP002', 'Java进阶讨论组', '面向年薪百万的Java开发者交流群', '2052289567506567170', 'user', '2026-05-02 10:00:00');
INSERT INTO `study_group` VALUES ('GRP003', 'Kubernetes实战群', '容器编排、云原生爱好者集结地', '2052289849938415618', 'xs1', '2026-05-03 11:00:00');
INSERT INTO `study_group` VALUES ('GRP004', '考研数学攻坚团', '离散数学、高数、线代复习打卡', '1', 'admin', '2026-05-04 12:00:00');
INSERT INTO `study_group` VALUES ('GRP005', 'AI与深度学习研究组', '论文阅读、模型复现、Kaggle竞赛', '2052289567506567170', 'user', '2026-05-05 13:00:00');
INSERT INTO `study_group` VALUES ('GRP006', '前端技术交流圈', 'Vue/React/小程序爱好者', '2052289849938415618', 'xs1', '2026-05-06 14:00:00');
INSERT INTO `study_group` VALUES ('GRP007', '项目管理PMP备考群', '一起冲刺PMP认证', '1', 'admin', '2026-05-07 15:00:00');

-- ----------------------------
-- Table structure for study_group_member
-- ----------------------------
DROP TABLE IF EXISTS `study_group_member`;
CREATE TABLE `study_group_member`  (
  `id` varchar(64) CHARACTER SET utf8 COLLATE utf8_unicode_ci NOT NULL,
  `group_id` varchar(64) CHARACTER SET utf8 COLLATE utf8_unicode_ci NOT NULL,
  `user_id` varchar(64) CHARACTER SET utf8 COLLATE utf8_unicode_ci NOT NULL,
  `username` varchar(128) CHARACTER SET utf8 COLLATE utf8_unicode_ci NULL DEFAULT NULL,
  `create_time` datetime NULL DEFAULT CURRENT_TIMESTAMP,
  PRIMARY KEY (`id`) USING BTREE,
  UNIQUE INDEX `uk_group_user`(`group_id`, `user_id`) USING BTREE
) ENGINE = MyISAM CHARACTER SET = utf8 COLLATE = utf8_unicode_ci ROW_FORMAT = DYNAMIC;

-- ----------------------------
-- Records of study_group_member
-- ----------------------------
INSERT INTO `study_group_member` VALUES ('MEMBER_06', 'GRP003', '2052289849938415618', 'xs1', '2026-05-03 11:00:00');
INSERT INTO `study_group_member` VALUES ('MEMBER_05', 'GRP002', '1', 'admin', '2026-05-02 10:05:00');
INSERT INTO `study_group_member` VALUES ('MEMBER_04', 'GRP002', '2052289567506567170', 'user', '2026-05-02 10:00:00');
INSERT INTO `study_group_member` VALUES ('MEMBER_03', 'GRP001', '2052289849938415618', 'xs1', '2026-05-01 09:10:00');
INSERT INTO `study_group_member` VALUES ('MEMBER_02', 'GRP001', '2052289567506567170', 'user', '2026-05-01 09:05:00');
INSERT INTO `study_group_member` VALUES ('MEMBER_01', 'GRP001', '1', 'admin', '2026-05-01 09:00:00');
INSERT INTO `study_group_member` VALUES ('MEMBER_PLUS_2', '2052295801857118210', '1', 'admin', '2026-05-07 08:00:01');
INSERT INTO `study_group_member` VALUES ('MEMBER_PLUS_1', '2052295801857118210', '2052289567506567170', 'user', '2026-05-07 08:00:00');
INSERT INTO `study_group_member` VALUES ('MEMBER_07', 'GRP003', '1', 'admin', '2026-05-03 11:05:00');
INSERT INTO `study_group_member` VALUES ('MEMBER_08', 'GRP004', '1', 'admin', '2026-05-04 12:00:00');
INSERT INTO `study_group_member` VALUES ('MEMBER_09', 'GRP004', '2052289567506567170', 'user', '2026-05-04 12:05:00');
INSERT INTO `study_group_member` VALUES ('MEMBER_10', 'GRP004', '2052289849938415618', 'xs1', '2026-05-04 12:10:00');
INSERT INTO `study_group_member` VALUES ('MEMBER_11', 'GRP005', '2052289567506567170', 'user', '2026-05-05 13:00:00');
INSERT INTO `study_group_member` VALUES ('MEMBER_12', 'GRP005', '1', 'admin', '2026-05-05 13:05:00');
INSERT INTO `study_group_member` VALUES ('MEMBER_13', 'GRP006', '2052289849938415618', 'xs1', '2026-05-06 14:00:00');
INSERT INTO `study_group_member` VALUES ('MEMBER_14', 'GRP006', '1', 'admin', '2026-05-06 14:05:00');
INSERT INTO `study_group_member` VALUES ('MEMBER_15', 'GRP007', '1', 'admin', '2026-05-07 15:00:00');
INSERT INTO `study_group_member` VALUES ('MEMBER_16', 'GRP007', '2052289567506567170', 'user', '2026-05-07 15:05:00');

-- ----------------------------
-- Table structure for sys_account
-- ----------------------------
DROP TABLE IF EXISTS `sys_account`;
CREATE TABLE `sys_account`  (
  `id` varchar(36) CHARACTER SET utf8 COLLATE utf8_unicode_ci NOT NULL,
  `email` varchar(100) CHARACTER SET utf8 COLLATE utf8_unicode_ci NULL DEFAULT NULL COMMENT '邮箱地址',
  `username` varchar(50) CHARACTER SET utf8 COLLATE utf8_unicode_ci NULL DEFAULT NULL COMMENT '用户名',
  `password` varchar(100) CHARACTER SET utf8 COLLATE utf8_unicode_ci NULL DEFAULT NULL COMMENT '密码',
  `role` varchar(36) CHARACTER SET utf8 COLLATE utf8_unicode_ci NULL DEFAULT NULL COMMENT '角色',
  PRIMARY KEY (`id`) USING BTREE,
  UNIQUE INDEX `unique_name`(`username`) USING BTREE,
  UNIQUE INDEX `unique_email`(`email`) USING BTREE
) ENGINE = InnoDB CHARACTER SET = utf8 COLLATE = utf8_unicode_ci ROW_FORMAT = DYNAMIC;

-- ----------------------------
-- Records of sys_account
-- ----------------------------
INSERT INTO `sys_account` VALUES ('1', '', 'admin', '$2a$10$0AdDkPuCxkyAccD1xyQH2.EzeF3YRGUXgdHrhbC8u1Kl6g1kfXEty', '1');
INSERT INTO `sys_account` VALUES ('2052697568052920322', '123@qq.com', 'teacher', '$2a$10$8foulT58TQ1IGZ40WOayk.BWzRpI/yFQd2GKw3E8amelvrHBTY0TO', '2');
INSERT INTO `sys_account` VALUES ('2052697664752599042', '1234@qq.com', 'student', '$2a$10$4VW4lcwlylgZu/xeS8y9uu4FoQLfP84vHpX/HMAsy8FrYGzE3HLcO', '3');

-- ----------------------------
-- Table structure for sys_role_route
-- ----------------------------
DROP TABLE IF EXISTS `sys_role_route`;
CREATE TABLE `sys_role_route`  (
  `id` varchar(36) CHARACTER SET utf8 COLLATE utf8_general_ci NOT NULL,
  `role_id` varchar(36) CHARACTER SET utf8 COLLATE utf8_general_ci NOT NULL COMMENT '角色ID',
  `route_id` varchar(36) CHARACTER SET utf8 COLLATE utf8_general_ci NOT NULL COMMENT '路由ID',
  PRIMARY KEY (`id`) USING BTREE
) ENGINE = InnoDB CHARACTER SET = utf8 COLLATE = utf8_general_ci ROW_FORMAT = DYNAMIC;

-- ----------------------------
-- Records of sys_role_route
-- ----------------------------
INSERT INTO `sys_role_route` VALUES ('2052758344046415873', '1', '13');
INSERT INTO `sys_role_route` VALUES ('2052758344109330433', '1', '131');
INSERT INTO `sys_role_route` VALUES ('2052758344109330434', '1', '130');
INSERT INTO `sys_role_route` VALUES ('2052758344109330435', '1', '132');
INSERT INTO `sys_role_route` VALUES ('2052758344109330436', '1', '133');
INSERT INTO `sys_role_route` VALUES ('2052758344109330437', '1', '11');
INSERT INTO `sys_role_route` VALUES ('2052758344109330438', '1', '111');
INSERT INTO `sys_role_route` VALUES ('2052758344109330439', '1', '110');
INSERT INTO `sys_role_route` VALUES ('2052758344109330440', '1', '119');
INSERT INTO `sys_role_route` VALUES ('2052758344109330441', '1', '112');
INSERT INTO `sys_role_route` VALUES ('2052758344109330442', '1', '113');
INSERT INTO `sys_role_route` VALUES ('2052758344109330443', '1', '114');
INSERT INTO `sys_role_route` VALUES ('2052758344109330444', '1', '115');
INSERT INTO `sys_role_route` VALUES ('2052758344109330445', '1', '116');
INSERT INTO `sys_role_route` VALUES ('2052758344109330446', '1', '117');
INSERT INTO `sys_role_route` VALUES ('2052758344109330447', '1', '118');
INSERT INTO `sys_role_route` VALUES ('2052758344109330448', '1', '12');
INSERT INTO `sys_role_route` VALUES ('2052758344109330449', '1', '120');
INSERT INTO `sys_role_route` VALUES ('2052758344109330450', '1', '121');
INSERT INTO `sys_role_route` VALUES ('2052758344109330451', '1', '14');
INSERT INTO `sys_role_route` VALUES ('2052758344109330452', '1', '141');
INSERT INTO `sys_role_route` VALUES ('2052758344109330453', '1', '142');
INSERT INTO `sys_role_route` VALUES ('2052758344109330454', '1', '143');
INSERT INTO `sys_role_route` VALUES ('2052758344109330455', '1', '140');
INSERT INTO `sys_role_route` VALUES ('2052758344109330456', '1', '15');
INSERT INTO `sys_role_route` VALUES ('2052758344109330457', '1', '150');
INSERT INTO `sys_role_route` VALUES ('2052758344109330458', '1', '151');
INSERT INTO `sys_role_route` VALUES ('2052758344109330459', '1', '152');
INSERT INTO `sys_role_route` VALUES ('2052758344109330460', '1', '155');
INSERT INTO `sys_role_route` VALUES ('2052758344109330461', '1', '153');
INSERT INTO `sys_role_route` VALUES ('2052758344109330462', '1', '154');
INSERT INTO `sys_role_route` VALUES ('2052758344109330463', '1', '156');
INSERT INTO `sys_role_route` VALUES ('2052758344109330464', '1', '16');
INSERT INTO `sys_role_route` VALUES ('2052758344109330465', '1', '160');
INSERT INTO `sys_role_route` VALUES ('2052758344109330466', '1', '161');
INSERT INTO `sys_role_route` VALUES ('2052758475990831106', '2', '13');
INSERT INTO `sys_role_route` VALUES ('2052758475990831107', '2', '131');
INSERT INTO `sys_role_route` VALUES ('2052758475990831108', '2', '130');
INSERT INTO `sys_role_route` VALUES ('2052758475990831109', '2', '132');
INSERT INTO `sys_role_route` VALUES ('2052758475990831110', '2', '133');
INSERT INTO `sys_role_route` VALUES ('2052758475990831111', '2', '12');
INSERT INTO `sys_role_route` VALUES ('2052758475990831112', '2', '120');
INSERT INTO `sys_role_route` VALUES ('2052758475990831113', '2', '121');
INSERT INTO `sys_role_route` VALUES ('2052758475990831114', '2', '142');
INSERT INTO `sys_role_route` VALUES ('2052758475990831115', '2', '143');
INSERT INTO `sys_role_route` VALUES ('2052758475990831116', '2', '150');
INSERT INTO `sys_role_route` VALUES ('2052758475990831117', '2', '151');
INSERT INTO `sys_role_route` VALUES ('2052758475990831118', '2', '152');
INSERT INTO `sys_role_route` VALUES ('2052758475990831119', '2', '155');
INSERT INTO `sys_role_route` VALUES ('2052758475990831120', '2', '161');
INSERT INTO `sys_role_route` VALUES ('2052758475990831121', '2', '14');
INSERT INTO `sys_role_route` VALUES ('2052758475990831122', '2', '15');
INSERT INTO `sys_role_route` VALUES ('2052758475990831123', '2', '16');
INSERT INTO `sys_role_route` VALUES ('2052758563131691009', '3', '13');
INSERT INTO `sys_role_route` VALUES ('2052758563131691010', '3', '131');
INSERT INTO `sys_role_route` VALUES ('2052758563131691011', '3', '130');
INSERT INTO `sys_role_route` VALUES ('2052758563131691012', '3', '132');
INSERT INTO `sys_role_route` VALUES ('2052758563131691013', '3', '133');
INSERT INTO `sys_role_route` VALUES ('2052758563131691014', '3', '142');
INSERT INTO `sys_role_route` VALUES ('2052758563131691015', '3', '143');
INSERT INTO `sys_role_route` VALUES ('2052758563131691016', '3', '150');
INSERT INTO `sys_role_route` VALUES ('2052758563131691017', '3', '151');
INSERT INTO `sys_role_route` VALUES ('2052758563131691018', '3', '152');
INSERT INTO `sys_role_route` VALUES ('2052758563131691019', '3', '160');
INSERT INTO `sys_role_route` VALUES ('2052758563131691020', '3', '14');
INSERT INTO `sys_role_route` VALUES ('2052758563131691021', '3', '15');
INSERT INTO `sys_role_route` VALUES ('2052758563131691022', '3', '16');

-- ----------------------------
-- Table structure for sys_roles
-- ----------------------------
DROP TABLE IF EXISTS `sys_roles`;
CREATE TABLE `sys_roles`  (
  `id` varchar(36) CHARACTER SET utf8 COLLATE utf8_unicode_ci NOT NULL,
  `name` varchar(50) CHARACTER SET utf8 COLLATE utf8_unicode_ci NULL DEFAULT NULL COMMENT '角色名称',
  `description` varchar(200) CHARACTER SET utf8 COLLATE utf8_unicode_ci NULL DEFAULT NULL COMMENT '角色描述',
  PRIMARY KEY (`id`) USING BTREE
) ENGINE = MyISAM AUTO_INCREMENT = 4 CHARACTER SET = utf8 COLLATE = utf8_unicode_ci ROW_FORMAT = DYNAMIC;

-- ----------------------------
-- Records of sys_roles
-- ----------------------------
INSERT INTO `sys_roles` VALUES ('1', 'admin', '系统管理员');
INSERT INTO `sys_roles` VALUES ('2', 'teacher', '教师');
INSERT INTO `sys_roles` VALUES ('3', 'student', '学生');

-- ----------------------------
-- Table structure for sys_routes
-- ----------------------------
DROP TABLE IF EXISTS `sys_routes`;
CREATE TABLE `sys_routes`  (
  `id` varchar(36) CHARACTER SET utf8 COLLATE utf8_unicode_ci NOT NULL,
  `title` varchar(50) CHARACTER SET utf8 COLLATE utf8_unicode_ci NULL DEFAULT NULL COMMENT '菜单名称/显示名称',
  `path` varchar(100) CHARACTER SET utf8 COLLATE utf8_unicode_ci NULL DEFAULT NULL COMMENT '	路由路径',
  `parent_id` varchar(36) CHARACTER SET utf8 COLLATE utf8_unicode_ci NULL DEFAULT NULL COMMENT '父菜单ID，顶级为 0',
  `level` varchar(255) CHARACTER SET utf8 COLLATE utf8_unicode_ci NULL DEFAULT NULL COMMENT '菜单层级（1:一级，2:二级）',
  `remark` varchar(200) CHARACTER SET utf8 COLLATE utf8_unicode_ci NULL DEFAULT NULL COMMENT '备注说明',
  `sort` varchar(10) CHARACTER SET utf8 COLLATE utf8_unicode_ci NULL DEFAULT NULL COMMENT '排序号',
  `menu_visible` int(11) NULL DEFAULT 1 COMMENT '是否在菜单中显示（1:显示，0:隐藏）',
  PRIMARY KEY (`id`) USING BTREE
) ENGINE = MyISAM AUTO_INCREMENT = 130 CHARACTER SET = utf8 COLLATE = utf8_unicode_ci ROW_FORMAT = DYNAMIC;

-- ----------------------------
-- Records of sys_routes
-- ----------------------------
INSERT INTO `sys_routes` VALUES ('16', '/index', '/index', '0', '1', '数据统计', '6', 1);
INSERT INTO `sys_routes` VALUES ('160', '/StudentStatsView', '/index/StudentStatsView', '16', '2', '学生统计', '2', 1);
INSERT INTO `sys_routes` VALUES ('155', '/PublicDiscussion', '/community/PublicDiscussion', '15', '2', '公共讨论区', '2', 1);
INSERT INTO `sys_routes` VALUES ('154', '/PostManage', '/community/PostManage', '15', '2', '公共讨论管理', '3', 1);
INSERT INTO `sys_routes` VALUES ('153', '/GroupManage', '/community/GroupManage', '15', '2', '学习小组管理', '3', 1);
INSERT INTO `sys_routes` VALUES ('152', '/CommentList', '/community/components/CommentList', '15', '2', '回复', '2', 0);
INSERT INTO `sys_routes` VALUES ('150', '/GroupDetail', '/community/GroupDetail', '15', '2', '小组', '2', 0);
INSERT INTO `sys_routes` VALUES ('151', '/GroupList', '/community/GroupList', '15', '2', '学习小组', '2', 1);
INSERT INTO `sys_routes` VALUES ('15', '/community', '/community', '0', '1', '学习社区', '5', 1);
INSERT INTO `sys_routes` VALUES ('142', '/PersonalInfo', '/sys/PersonalInfo', '14', '2', '个人信息', '2', 1);
INSERT INTO `sys_routes` VALUES ('143', '/MessageCenter', '/sys/MessageCenter', '14', '2', '消息中心', '2', 1);
INSERT INTO `sys_routes` VALUES ('141', '/UserList', '/sys/UserList', '14', '2', '用户管理', '2', 1);
INSERT INTO `sys_routes` VALUES ('14', '/sys', '/sys', '0', '1', '系统管理', '4', 1);
INSERT INTO `sys_routes` VALUES ('140', '/RoleList', '/sys/RoleList', '14', '2', '角色管理', '5', 1);
INSERT INTO `sys_routes` VALUES ('133', '/ResourceReviewList', '/study/components/ResourceReviewList', '13', '2', '评分表', '2', 0);
INSERT INTO `sys_routes` VALUES ('132', '/ResourceReviewForm', '/study/components/ResourceReviewForm', '13', '2', '评分附表', '2', 0);
INSERT INTO `sys_routes` VALUES ('131', '/LearningCenter', '/study/LearningCenter', '13', '2', '学习中心', '1', 1);
INSERT INTO `sys_routes` VALUES ('130', '/CourseDetail', '/study/CourseDetail', '13', '2', '课程详情页', '2', 0);
INSERT INTO `sys_routes` VALUES ('121', '/EditorPreparationCenter', '/study/EditorPreparationCenter', '12', '2', '编辑备课中心', '2', 0);
INSERT INTO `sys_routes` VALUES ('13', '/zh', '/zh', '0', '1', '智慧学习', '1', 1);
INSERT INTO `sys_routes` VALUES ('12', '/jx', '/jx', '0', '1', '教学管理', '3', 1);
INSERT INTO `sys_routes` VALUES ('120', '/PreparationCenter', '/study/PreparationCenter', '12', '2', '备课中心', '1', 1);
INSERT INTO `sys_routes` VALUES ('119', '/ReviewManage', '/study/ReviewManage', '11', '2', '课程评价管理', '2', 1);
INSERT INTO `sys_routes` VALUES ('118', '/CourseTypeModal', '/study/modules/CourseTypeModal', '11', '2', '课程类型弹窗', '5', 0);
INSERT INTO `sys_routes` VALUES ('117', '/CourseResourceModal', '/study/modules/CourseResourceModal', '11', '2', '课程资源弹窗', '5', 0);
INSERT INTO `sys_routes` VALUES ('115', '/CourseForm', '/study/modules/CourseForm', '11', '2', '课程表单', '5', 0);
INSERT INTO `sys_routes` VALUES ('116', '/CourseModal', '/study/modules/CourseModal', '11', '2', '课程弹窗', '5', 0);
INSERT INTO `sys_routes` VALUES ('114', '/CategoryCreateForm', '/study/modules/CategoryCreateForm', '11', '2', '创建分类表单', '5', 0);
INSERT INTO `sys_routes` VALUES ('113', '/StudentLearningRecordList', '/study/StudentLearningRecordList', '11', '2', '查看学生详细学习记录', '4', 1);
INSERT INTO `sys_routes` VALUES ('112', '/CourseResourceList', '/study/CourseResourceList', '11', '2', '学习资源', '3', 1);
INSERT INTO `sys_routes` VALUES ('111', '/CourseTypeList', '/study/CourseTypeList', '11', '2', '课程分类', '1', 1);
INSERT INTO `sys_routes` VALUES ('110', '/CourseList', '/study/CourseList', '11', '2', '课程管理', '2', 1);
INSERT INTO `sys_routes` VALUES ('11', '/kc', '/kc', '0', '1', '课程管理', '2', 1);
INSERT INTO `sys_routes` VALUES ('161', '/TeacherStatsView', '/index/TeacherStatsView', '16', '2', '教师统计', '3', 1);
INSERT INTO `sys_routes` VALUES ('156', '/GroupMemberManage', '/community/GroupMemberManage', '15', '2', '学习小组成员管理', '3', 1);

SET FOREIGN_KEY_CHECKS = 1;
