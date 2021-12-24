DROP DATABASE IF EXISTS text_board;
CREATE DATABASE text_board;
USE text_board;

CREATE TABLE article (
    id INT(10) UNSIGNED NOT NULL AUTO_INCREMENT,
    PRIMARY KEY(id),
    regDate DATETIME NOT NULL,
    updateDate DATETIME NOT NULL,
    title CHAR(100) NOT NULL,
    `body` TEXT NOT NULL
);

DESC article;
SELECT * FROM article;

CREATE TABLE `member` (
    id INT(10) UNSIGNED NOT NULL AUTO_INCREMENT,
    PRIMARY KEY(id),
    regDate DATETIME NOT NULL,
    updateDate DATETIME NOT NULL,
    loginId CHAR(100) NOT NULL,
    loginPw CHAR(100) NOT NULL,
    `name` CHAR(100) NOT NULL
);

DESC article;
SELECT * FROM `member`;

ALTER TABLE article ADD COLUMN memberId INT(10) UNSIGNED NOT NULL AFTER updateDate;
DESC article;

SELECT *, m.name AS extra_writer FROM article AS a
LEFT JOIN `member` AS m
ON a.memberId = m.id
ORDER BY a.id DESC;

ALTER TABLE article ADD COLUMN hit INT(10) UNSIGNED NOT NULL AFTER `body`;

INSERT INTO article
SET regDate = NOW(),
updateDate = NOW(),
memberId = 1,
title = '제목1',
`body` = '내용1',
hit = 20;

INSERT INTO article
SET regDate = NOW(),
updateDate = NOW(),
memberId = 1,
title = '제목2',
`body` = '내용2',
hit = 20;

INSERT INTO article
SET regDate = NOW(),
updateDate = NOW(),
memberId = 1,
title = '제목3',
`body` = '내용3',
hit = 20;

INSERT INTO article
SET regDate = NOW(),
updateDate = NOW(),
memberId = 1,
title = '제목4',
`body` = '내용4',
hit = 20;

INSERT INTO article
SET regDate = NOW(),
updateDate = NOW(),
memberId = 1,
title = '제목5',
`body` = '내용5',
hit = 20;

DESC article;

DROP TABLE `like`;

CREATE TABLE `like` (
    id INT(10) UNSIGNED NOT NULL AUTO_INCREMENT,
    PRIMARY KEY(id),
    regDate DATETIME NOT NULL,
    updateDate DATETIME NOT NULL,
    articleId INT(10) UNSIGNED NOT NULL,
    memberId INT(10) UNSIGNED NOT NULL,
    likeType TINYINT(1) NOT NULL
);

DESC `like`;
SELECT * FROM `like`;

# likeCheck
SELECT
CASE WHEN COUNT(*) != 0 THEN likeType ELSE 0
END AS isRecommend
FROM `like`
WHERE articleId = 6 AND memberId = 1;

# getLikeVal
# 데이터가 없다면 0
# 데이터가 있다면 해당 데이터 반환
SELECT COUNT(*)
FROM `like`
WHERE articleId = 3 AND likeType = 2;

SELECT * FROM article;

CREATE TABLE `comment` (
    id INT(10) UNSIGNED NOT NULL AUTO_INCREMENT,
    PRIMARY KEY(id),
    regDate DATETIME NOT NULL,
    updateDate DATETIME NOT NULL,
    articleId INT(10) UNSIGNED NOT NULL,
    memberId INT(10) UNSIGNED NOT NULL,
    title CHAR(100) NOT NULL,
    `body` CHAR(100) NOT NULL
);

DESC `comment`;

SELECT c.*, m.name AS extra_writer
FROM `comment` AS c
INNER JOIN `member` AS m
ON c.memberId = m.id
WHERE articleId = 7;

SELECT *
FROM `comment`;

SELECT COUNT(*)
FROM `comment`
WHERE id = 1;

SELECT *
FROM `comment`
WHERE id = 1;

SELECT *
FROM `member`;
