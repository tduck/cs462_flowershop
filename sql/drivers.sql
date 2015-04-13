DROP TABLE IF EXISTS flowershop.drivers;

CREATE TABLE flowershop.drivers(
	number VARCHAR(32),
	name VARCHAR(32) NOT NULL,
	lastlat FLOAT,
	lastlong FLOAT,
	PRIMARY KEY(number)
);
