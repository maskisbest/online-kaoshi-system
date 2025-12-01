-- 优化版数据库结构（可选执行）。请先备份现有数据，再按需执行。
-- 目标：字符集统一、主键/唯一键/外键补充、索引优化、字段约束更明确。

-- 统一字符集
ALTER DATABASE `examination` CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;

-- admin
ALTER TABLE `admin`
    CONVERT TO CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci,
    MODIFY `adminName`      VARCHAR(40),
    MODIFY `sex`            VARCHAR(2),
    MODIFY `tel`            VARCHAR(20),
    MODIFY `email`          VARCHAR(80),
    MODIFY `pwd`            VARCHAR(255) NOT NULL,
    MODIFY `cardId`         VARCHAR(18),
    MODIFY `role`           CHAR(1) NOT NULL DEFAULT '0',
    ADD UNIQUE KEY `uk_admin_card` (`cardId`),
    ADD UNIQUE KEY `uk_admin_email` (`email`),
    ADD UNIQUE KEY `uk_admin_tel` (`tel`),
    ADD CONSTRAINT `ck_admin_role` CHECK (`role` IN ('0','1','2'));

-- teacher
ALTER TABLE `teacher`
    CONVERT TO CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci,
    MODIFY `teacherName` VARCHAR(40),
    MODIFY `tel`         VARCHAR(20),
    MODIFY `email`       VARCHAR(80),
    MODIFY `pwd`         VARCHAR(255) NOT NULL,
    MODIFY `cardId`      VARCHAR(18),
    MODIFY `role`        CHAR(1) NOT NULL DEFAULT '1',
    ADD UNIQUE KEY `uk_teacher_card` (`cardId`),
    ADD UNIQUE KEY `uk_teacher_email` (`email`),
    ADD UNIQUE KEY `uk_teacher_tel` (`tel`),
    ADD CONSTRAINT `ck_teacher_role` CHECK (`role` IN ('0','1','2'));

-- student
ALTER TABLE `student`
    CONVERT TO CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci,
    MODIFY `studentName` VARCHAR(40),
    MODIFY `grade`       VARCHAR(10),
    MODIFY `major`       VARCHAR(40),
    MODIFY `clazz`       VARCHAR(20),
    MODIFY `institute`   VARCHAR(60),
    MODIFY `tel`         VARCHAR(20),
    MODIFY `email`       VARCHAR(80),
    MODIFY `pwd`         VARCHAR(255) NOT NULL,
    MODIFY `cardId`      VARCHAR(18),
    MODIFY `sex`         VARCHAR(2),
    MODIFY `role`        CHAR(1) NOT NULL DEFAULT '2',
    ADD UNIQUE KEY `uk_student_card` (`cardId`),
    ADD UNIQUE KEY `uk_student_email` (`email`),
    ADD UNIQUE KEY `uk_student_tel` (`tel`),
    ADD CONSTRAINT `ck_student_role` CHECK (`role` IN ('0','1','2'));

-- 试卷与考试
ALTER TABLE `exam_manage`
    CONVERT TO CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci,
    MODIFY `description` VARCHAR(120),
    MODIFY `source`      VARCHAR(60),
    MODIFY `paperId`     INT NOT NULL,
    MODIFY `examDate`    VARCHAR(16), -- 如需严格日期可改 DATE，并调整代码的类型转换
    MODIFY `totalTime`   INT NOT NULL DEFAULT 60,
    MODIFY `grade`       VARCHAR(20),
    MODIFY `term`        VARCHAR(20),
    MODIFY `major`       VARCHAR(40),
    MODIFY `institute`   VARCHAR(60),
    MODIFY `totalScore`  INT NOT NULL DEFAULT 100,
    MODIFY `type`        VARCHAR(60),
    MODIFY `tips`        VARCHAR(255),
    ADD UNIQUE KEY `uk_exam_paper` (`paperId`);

-- 试卷明细
ALTER TABLE `paper_manage`
    CONVERT TO CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci,
    ADD COLUMN `id` BIGINT NOT NULL AUTO_INCREMENT PRIMARY KEY FIRST,
    MODIFY `paperId`      INT NOT NULL,
    MODIFY `questionType` INT NOT NULL,
    MODIFY `questionId`   INT NOT NULL,
    ADD UNIQUE KEY `uk_paper_question` (`paperId`, `questionType`, `questionId`),
    ADD KEY `idx_paper` (`paperId`);

-- 成绩
ALTER TABLE `score`
    CONVERT TO CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci,
    MODIFY `examCode`  INT NOT NULL,
    MODIFY `studentId` INT NOT NULL,
    MODIFY `subject`   VARCHAR(60) NOT NULL,
    MODIFY `ptScore`   INT DEFAULT 0,
    MODIFY `etScore`   INT DEFAULT 0,
    MODIFY `score`     INT DEFAULT 0,
    MODIFY `answerDate` VARCHAR(16),
    ADD UNIQUE KEY `uk_score_exam_student` (`examCode`,`studentId`),
    ADD KEY `idx_score_student` (`studentId`),
    ADD CONSTRAINT `fk_score_exam` FOREIGN KEY (`examCode`) REFERENCES `exam_manage` (`examCode`) ON DELETE CASCADE,
    ADD CONSTRAINT `fk_score_student` FOREIGN KEY (`studentId`) REFERENCES `student` (`studentId`) ON DELETE CASCADE;

-- 题库
ALTER TABLE `multi_question`  CONVERT TO CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;
ALTER TABLE `fill_question`   CONVERT TO CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;
ALTER TABLE `judge_question`  CONVERT TO CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;

-- 消息、反馈
ALTER TABLE `message` CONVERT TO CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;
ALTER TABLE `replay`  CONVERT TO CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;

-- 若需要保留原始密码明文，请在应用层改为哈希存储；以上长度已为哈希字段预留。
