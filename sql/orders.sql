DROP TABLE IF EXISTS flowershop.orders;

CREATE TABLE flowershop.orders(
	id INTEGER AUTO_INCREMENT,
	shopid VARCHAR(32) NOT NULL,
	emailaddress VARCHAR(128) NOT NULL,
	driverphone VARCHAR(32),
	delivered BOOLEAN,
	address VARCHAR(256) NOT NULL,
	latitude FLOAT NOT NULL,
	longitude FLOAT NOT NULL,
	pickedup BOOLEAN,
	PRIMARY KEY(id)
);
