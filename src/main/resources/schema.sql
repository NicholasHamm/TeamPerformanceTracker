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
  `duration` BIGINT NOT NULL,
  PRIMARY KEY (`id`)
  CONSTRAINT `uk_training_session_datetime_type` UNIQUE (`datetime`, `type`)
);

CREATE TABLE IF NOT EXISTS `training_session_players` (
  `session_id` BIGINT NOT NULL,
  `player_id` BIGINT NOT NULL,
  PRIMARY KEY (`session_id`, `player_id`),
  CONSTRAINT `fk_tsp_session`
    FOREIGN KEY (`session_id`) REFERENCES `training_session` (`id`),
  CONSTRAINT `fk_tsp_user`
    FOREIGN KEY (`player_id`) REFERENCES `users` (`id`)
);