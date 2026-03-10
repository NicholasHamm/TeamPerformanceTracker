INSERT INTO users (id, username, password, first_name, last_name, role)
VALUES
(1, 'admin', '$2a$10$56//qUtwlTjojvUW5vjX2ufRPV0ZK4XIDKOBdd7N4LCtANOl1A2IW', 'System', 'Admin', 'ADMIN'),
(2, 'coach1', '$2a$10$YCFDL85gf.fECMdHiKiRi.KIMpggUujjbiY3RiBdahsadX6gzd.LG', 'John', 'Coach', 'COACH'),
(3, 'player1', '$2a$10$aA6ZRYzrYqGLVIPpphv54.O2H.i6.iKFbWjwO3Jfip.8lNpi.MeKa', 'Mike', 'Player', 'PLAYER');

INSERT INTO training_session (datetime, type, duration)
VALUES ('2026-03-09 18:00:00', 'PITCH', 60);

INSERT INTO training_session_players (session_id, player_id)
VALUES (1, 3);