DROP TABLE IF EXISTS flowershop.orders;

CREATE TABLE flowershop.orders(
	id INTEGER AUTO_INCREMENT,
	shopid VARCHAR(32) NOT NULL,
	driverphone VARCHAR(32),
	latitude FLOAT NOT NULL,
	longitude FLOAT NOT NULL,
	PRIMARY KEY(id)
);
