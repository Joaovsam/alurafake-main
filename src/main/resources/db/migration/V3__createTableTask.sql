CREATE TABLE task (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    statement VARCHAR(255),
    `order` INT,
    course_id BIGINT,
    CONSTRAINT fk_task_course FOREIGN KEY (course_id) REFERENCES course(id)
);

CREATE TABLE single_choice_task (
    id BIGINT PRIMARY KEY,
    CONSTRAINT fk_single_task FOREIGN KEY (id) REFERENCES task(id)
);

CREATE TABLE multiple_choice_task (
    id BIGINT PRIMARY KEY,
    CONSTRAINT fk_multi_task FOREIGN KEY (id) REFERENCES task(id)
);

CREATE TABLE single_choice_task_options (
    single_choice_task_id BIGINT NOT NULL,
    `option` VARCHAR(255),
    is_correct BOOLEAN,
    CONSTRAINT fk_single_options FOREIGN KEY (single_choice_task_id) REFERENCES single_choice_task(id)
);

CREATE TABLE multiple_choice_task_options (
    multiple_choice_task_id BIGINT NOT NULL,
    `option` VARCHAR(255),
    is_correct BOOLEAN,
    CONSTRAINT fk_multi_options FOREIGN KEY (multiple_choice_task_id) REFERENCES multiple_choice_task(id)
);