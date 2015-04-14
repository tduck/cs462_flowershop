DROP TABLE IF EXISTS flowershop.drivers;

CREATE TABLE flowershop.drivers(
	phone VARCHAR(32),
	name VARCHAR(32) NOT NULL,
	lastlat FLOAT,
	lastlong FLOAT,
	clockedin BOOLEAN,
	available BOOLEAN,
	id VARCHAR(32) NOT NULL,
	PRIMARY KEY(phone)
);
