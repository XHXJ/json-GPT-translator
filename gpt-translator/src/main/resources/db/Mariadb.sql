/*
 Navicat Premium Data Transfer

 Source Server         : 本地nasMariadb
 Source Server Type    : MariaDB
 Source Server Version : 100519 (10.5.19-MariaDB-1:10.5.19+maria~ubu2004)
 Source Host           : 192.168.123.106:33061
 Source Schema         : translate

 Target Server Type    : MariaDB
 Target Server Version : 100519 (10.5.19-MariaDB-1:10.5.19+maria~ubu2004)
 File Encoding         : 65001

 Date: 17/05/2023 16:37:13
*/

SET NAMES utf8mb4;
SET FOREIGN_KEY_CHECKS = 0;

-- ----------------------------
-- Table structure for OPENAI_PROPERTIES
-- ----------------------------
DROP TABLE IF EXISTS `OPENAI_PROPERTIES`;
CREATE TABLE `OPENAI_PROPERTIES`  (
                                      `ID` bigint(20) NOT NULL AUTO_INCREMENT,
                                      `OPENAI_KEY` varchar(64) CHARACTER SET utf8 COLLATE utf8_general_ci NOT NULL COMMENT 'openai的key',
                                      PRIMARY KEY (`ID`) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 15 CHARACTER SET = utf8 COLLATE = utf8_general_ci COMMENT = 'openai配置类' ROW_FORMAT = Dynamic;

-- ----------------------------
-- Table structure for TRANSLATE_FILES
-- ----------------------------
DROP TABLE IF EXISTS `TRANSLATE_FILES`;
CREATE TABLE `TRANSLATE_FILES`  (
                                    `FILE_ID` int(11) NOT NULL AUTO_INCREMENT,
                                    `PROJECT_ID` int(11) NULL DEFAULT NULL COMMENT '项目id',
                                    `FILE_NAME` varchar(255) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL COMMENT '文件名',
                                    PRIMARY KEY (`FILE_ID`) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 434 CHARACTER SET = utf8 COLLATE = utf8_general_ci COMMENT = '文件表' ROW_FORMAT = Dynamic;

-- ----------------------------
-- Table structure for TRANSLATE_PROJECTS
-- ----------------------------
DROP TABLE IF EXISTS `TRANSLATE_PROJECTS`;
CREATE TABLE `TRANSLATE_PROJECTS`  (
                                       `PROJECT_ID` int(11) NOT NULL AUTO_INCREMENT,
                                       `PROJECT_NAME` varchar(255) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL COMMENT '项目名称',
                                       PRIMARY KEY (`PROJECT_ID`) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 6 CHARACTER SET = utf8 COLLATE = utf8_general_ci COMMENT = '项目表' ROW_FORMAT = Dynamic;

-- ----------------------------
-- Table structure for TRANSLATIONS
-- ----------------------------
DROP TABLE IF EXISTS `TRANSLATIONS`;
CREATE TABLE `TRANSLATIONS`  (
                                 `ID` bigint(20) NOT NULL AUTO_INCREMENT,
                                 `PROJECT_ID` int(11) NULL DEFAULT NULL,
                                 `FILE_ID` int(11) NULL DEFAULT NULL,
                                 `ORIGINAL_TEXT` text CHARACTER SET utf8 COLLATE utf8_general_ci NOT NULL COMMENT '原文',
                                 `TRANSLATION_TEXT` text CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL COMMENT '译文',
                                 `SEQUENCE` int(11) NULL DEFAULT NULL COMMENT '句子序列',
                                 PRIMARY KEY (`ID`) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 51759 CHARACTER SET = utf8 COLLATE = utf8_general_ci COMMENT = 'json翻译表' ROW_FORMAT = Dynamic;

SET FOREIGN_KEY_CHECKS = 1;
