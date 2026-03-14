INSERT IGNORE INTO users (username, password, first_name, last_name, role)
VALUES
('admin', '$2a$10$56//qUtwlTjojvUW5vjX2ufRPV0ZK4XIDKOBdd7N4LCtANOl1A2IW', 'System', 'Admin', 'ADMIN'),
('coach1', '$2a$10$YCFDL85gf.fECMdHiKiRi.KIMpggUujjbiY3RiBdahsadX6gzd.LG', 'John', 'Coach', 'COACH'),
('player1', '$2a$10$aA6ZRYzrYqGLVIPpphv54.O2H.i6.iKFbWjwO3Jfip.8lNpi.MeKa', 'Mike', 'M', 'PLAYER'),
('player2', '$2a$10$aA6ZRYzrYqGLVIPpphv54.O2H.i6.iKFbWjwO3Jfip.8lNpi.MeKa', 'Alex', 'A', 'PLAYER'),
('player3', '$2a$10$aA6ZRYzrYqGLVIPpphv54.O2H.i6.iKFbWjwO3Jfip.8lNpi.MeKa', 'Lucy', 'L', 'PLAYER');

INSERT IGNORE INTO training_session (datetime, type, duration)
VALUES
('2026-03-09 18:00:00', 'MATCH_SIMULATION', 60),
('2026-03-10 18:30:00', 'CONDITIONING', 45),
('2026-03-12 18:00:00', 'TACTICAL', 60),
('2026-03-14 11:00:00', 'RECOVERY', 40),
('2026-03-16 19:00:00', 'MATCH_SIMULATION', 75),
('2026-03-18 18:15:00', 'SPEED', 50);

INSERT IGNORE INTO player_performance
(player_id, session_id, total_distance, distance_per_min, high_intensity_distance, top_speed, effort_rating)
VALUES
(3, 1, 5000, 120.5, 800, 12.2, 7),
(3, 2, 3000, 90.0, 400, 9.5, 7),
(3, 3, 6100, 122.0, 950, 13.1, 8),
(3, 4, 2200, 55.0, 180, 8.2, 5),
(3, 5, 6800, 90.7, 1100, 13.8, 9),
(3, 6, 4100, 74.5, 500, 10.4, 6),

(4, 1, 5200, 125.0, 900, 12.5, 8),
(5, 1, 4800, 118.0, 750, 11.9, 6),
(4, 2, 3200, 95.0, 450, 9.8, 8),
(5, 2, 2900, 88.0, 350, 9.1, 6);
