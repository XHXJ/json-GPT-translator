CREATE TABLE OPENAI_PROPERTIES
(
    ID  BIGINT AUTO_INCREMENT,
    `OPENAI_KEY` VARCHAR(64) NOT NULL,
    PRIMARY KEY (ID)
);

ALTER TABLE OPENAI_PROPERTIES COMMENT 'openai配置类';

ALTER TABLE OPENAI_PROPERTIES
    MODIFY COLUMN `OPENAI_KEY` VARCHAR(64) NOT NULL COMMENT 'openai的key';

CREATE TABLE TRANSLATE_FILES
(
    FILE_ID    INTEGER AUTO_INCREMENT,
    PROJECT_ID INTEGER,
    FILE_NAME  VARCHAR(255),
    PRIMARY KEY (FILE_ID)
);

ALTER TABLE TRANSLATE_FILES COMMENT '文件表';

ALTER TABLE TRANSLATE_FILES
    MODIFY COLUMN PROJECT_ID INTEGER COMMENT '项目id';

ALTER TABLE TRANSLATE_FILES
    MODIFY COLUMN FILE_NAME VARCHAR(255) COMMENT '文件名';

CREATE TABLE TRANSLATE_PROJECTS
(
    PROJECT_ID   INTEGER AUTO_INCREMENT,
    PROJECT_NAME VARCHAR(255),
    PRIMARY KEY (PROJECT_ID)
);

ALTER TABLE TRANSLATE_PROJECTS COMMENT '项目表';

ALTER TABLE TRANSLATE_PROJECTS
    MODIFY COLUMN PROJECT_NAME VARCHAR(255) COMMENT '项目名称';

CREATE TABLE `TRANSLATIONS` (
    `ID` bigint(20) NOT NULL AUTO_INCREMENT,
    `PROJECT_ID` int(11) DEFAULT NULL,
    `FILE_ID` int(11) DEFAULT NULL,
    `ORIGINAL_TEXT` text NOT NULL COMMENT '原文',
    `TRANSLATION_TEXT` text DEFAULT NULL COMMENT '译文',
    `SEQUENCE` int(11) DEFAULT NULL COMMENT '句子序列',
    PRIMARY KEY (`ID`)
    ) ENGINE=InnoDB AUTO_INCREMENT=0 DEFAULT CHARSET=utf8 COLLATE=utf8_general_ci COMMENT='json翻译表';

ALTER TABLE TRANSLATIONS COMMENT 'json翻译表';

ALTER TABLE TRANSLATIONS
    MODIFY COLUMN ORIGINAL_TEXT VARCHAR(8000) NOT NULL COMMENT '原文';

ALTER TABLE TRANSLATIONS
    MODIFY COLUMN TRANSLATION_TEXT VARCHAR(8000) COMMENT '译文';

ALTER TABLE TRANSLATIONS
    MODIFY COLUMN SEQUENCE INTEGER COMMENT '句子序列';
