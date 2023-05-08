create table OPENAI_PROPERTIES
(
    ID  BIGINT identity
        primary key,
    OPENAI_KEY VARCHAR(64) not null
);

comment on table OPENAI_PROPERTIES is 'openai配置类';

comment on column OPENAI_PROPERTIES.OPENAI_KEY is 'openai的key';

create table TRANSLATE_FILES
(
    FILE_ID    INTEGER identity
        primary key,
    PROJECT_ID INTEGER,
    FILE_NAME  VARCHAR(255)
);

comment on table TRANSLATE_FILES is '文件表';

comment on column TRANSLATE_FILES.PROJECT_ID is '项目id';

comment on column TRANSLATE_FILES.FILE_NAME is '文件名';

create table TRANSLATE_PROJECTS
(
    PROJECT_ID   INTEGER identity
        primary key,
    PROJECT_NAME VARCHAR(255)
);

comment on table TRANSLATE_PROJECTS is '项目表';

comment on column TRANSLATE_PROJECTS.PROJECT_NAME is '项目名称';

create table TRANSLATIONS
(
    ID               BIGINT identity
        primary key,
    PROJECT_ID       INTEGER,
    FILE_ID          INTEGER,
    ORIGINAL_TEXT    VARCHAR(8000) not null,
    TRANSLATION_TEXT VARCHAR(8000),
    SEQUENCE         INTEGER
);

comment on table TRANSLATIONS is 'json翻译表';

comment on column TRANSLATIONS.ORIGINAL_TEXT is '原文';

comment on column TRANSLATIONS.TRANSLATION_TEXT is '译文';

comment on column TRANSLATIONS.SEQUENCE is '句子序列';

