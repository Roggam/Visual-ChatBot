CREATE TABLE `chatbot`.`user` (
  `id` BIGINT(11) NOT NULL,
  `email` VARCHAR(500) NULL,
  `firstname` VARCHAR(250) NULL,
  `lastname` VARCHAR(250) NULL,
  `password` VARCHAR(45) NULL,
  `active` INT NULL,
  PRIMARY KEY (`id`));


CREATE TABLE `chatbot`.`role` (
  `role_id` INT NOT NULL,
  `role` VARCHAR(250) NULL,
  PRIMARY KEY (`role_id`));

CREATE TABLE `chatbot`.`user_role` (
  `user_id` INT NOT NULL,
  `role_id` INT NOT NULL,
  PRIMARY KEY (`user_id`, `role_id`));

