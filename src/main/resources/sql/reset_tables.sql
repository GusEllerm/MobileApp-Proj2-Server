DROP TABLE IF EXISTS gps_map;
DROP TABLE IF EXISTS fragment;
DROP TABLE IF EXISTS coords;


CREATE TABLE gps_map (
  map_id INTEGER PRIMARY KEY AUTOINCREMENT,
  map_type VARCHAR(32) NOT NULL,
  fragment_amount INT NOT NULL,
  map_title VARCHAR(32) NOT NULL,
  category VARCHAR(32) NOT NULL,
  votes INT DEFAULT 0
);


CREATE TABLE fragment (
  frag_id INTEGER PRIMARY KEY AUTOINCREMENT,
  line_colour VARCHAR(32) NOT NULL,
  map_id INT NOT NULL,
  zoom INT NOT NULL,
  FOREIGN KEY(map_id) REFERENCES gps_map(map_id) ON DELETE CASCADE
);

CREATE TABLE coords (
  coords_id INTEGER PRIMARY KEY,
  sequence INT NOT NULL,
  long DECIMAL (10,7) NOT NULL,
  lat DECIMAL (10, 7) NOT NULL,
  frag_id INT NOT NULL,
  FOREIGN KEY(frag_id) REFERENCES fragment(frag_id) ON DELETE CASCADE
);

