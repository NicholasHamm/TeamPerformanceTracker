INSERT INTO users (id, username, password, first_name, last_name, role)
VALUES
(1, 'admin', '$2a$10$56//qUtwlTjojvUW5vjX2ufRPV0ZK4XIDKOBdd7N4LCtANOl1A2IW', 'System', 'Admin', 'ADMIN'),
(2, 'coach1', '$2a$10$YCFDL85gf.fECMdHiKiRi.KIMpggUujjbiY3RiBdahsadX6gzd.LG', 'John', 'Coach', 'COACH'),
(3, 'player1', '$2a$10$aA6ZRYzrYqGLVIPpphv54.O2H.i6.iKFbWjwO3Jfip.8lNpi.MeKa', 'Mike', 'Player', 'PLAYER');

INSERT INTO training_session (datetime, type, duration)
VALUES
('2026-03-09 18:00:00', 'RECOVERY', 50),
('2026-03-10 18:00:00', 'MATCH_SIMULATION', 50);

INSERT IGNORE INTO player_performance
(player_id, session_id, total_distance, distance_per_min, high_intensity_distance, top_speed, effort_rating)
VALUES
(3, 2, 5000, 100, 800, 12.2, 7);