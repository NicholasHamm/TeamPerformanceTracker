DROP TABLE IF EXISTS player_performance;
DROP TABLE IF EXISTS training_session;
DROP TABLE IF EXISTS users;

CREATE TABLE IF NOT EXISTS `users` (
  `id` BIGINT NOT NULL AUTO_INCREMENT,
  `username` varchar(50) NOT NULL,
  `password` varchar(72) NOT NULL,
  `first_name` varchar(50) NOT NULL,
  `last_name` varchar(50) NOT NULL,
  `role` varchar(20) NOT NULL,
  PRIMARY KEY (`id`),
  UNIQUE KEY `uk_users_username` (`username`)
);

CREATE TABLE IF NOT EXISTS `training_session` (
  `id` BIGINT NOT NULL AUTO_INCREMENT,
  `datetime` DATETIME NOT NULL,
  `type` varchar(30) NOT NULL,
  `duration` INT NOT NULL,
  PRIMARY KEY (`id`)
);

CREATE TABLE IF NOT EXISTS `player_performance` (
  `id` BIGINT NOT NULL AUTO_INCREMENT,
  `player_id` BIGINT NOT NULL,
  `session_id` BIGINT NOT NULL,
  `total_distance` DOUBLE NOT NULL,
  `distance_per_min` DOUBLE NOT NULL,
  `high_intensity_distance` DOUBLE NOT NULL,
  `top_speed` DOUBLE NOT NULL,
  `effort_rating` INT NOT NULL,
  PRIMARY KEY (`id`),
  CONSTRAINT `uk_player_session` UNIQUE (`player_id`, `session_id`),
  CONSTRAINT `fk_performance_player`
    FOREIGN KEY (`player_id`) REFERENCES `users`(`id`),
  CONSTRAINT `fk_performance_session`
    FOREIGN KEY (`session_id`) REFERENCES `training_session`(`id`)
);