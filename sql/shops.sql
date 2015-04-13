DROP TABLE IF EXISTS flowershop.shops;

CREATE TABLE flowershop.shops (
	id VARCHAR(32),
	name VARCHAR(32),
	latitude FLOAT,
	longitude FLOAT,
	PRIMARY KEY(id)
);
