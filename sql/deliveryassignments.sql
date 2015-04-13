DROP TABLE IF EXISTS flowershop.deliveries;

CREATE TABLE flowershop.deliveries(
	id INTEGER AUTO_INCREMENT,
	orderid INTEGER NOT NULL,
	accepted BOOLEAN NOT NULL,
	driverphone VARCHAR(32),
	PRIMARY KEY(id),
	FOREIGN KEY(orderid) REFERENCES flowershop.orders(id),
	FOREIGN KEY(driverphone) REFERENCES flowershop.drivers(phone)
);
