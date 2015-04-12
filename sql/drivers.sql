DROP TABLE IF EXISTS drivers;

CREATE TABLE drivers(
	number VARCHAR(32),
	name VARCHAR(32) NOT NULL,
	lastlat FLOAT,
	lastlong FLOAT,
	PRIMARY KEY(number)
);
