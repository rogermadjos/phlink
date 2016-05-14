SET FOREIGN_KEY_CHECKS = 0;
DROP TABLE IF EXISTS users;
CREATE TABLE IF NOT EXISTS users (
  id        VARCHAR(8)    NOT NULL,
  email     VARCHAR(100)  NOT NULL,
  hash      VARCHAR(255)  NOT NULL,

  PRIMARY KEY (id)
) ENGINE=InnoDB;

-- Table that is used to liquidate the balance of the user
DROP TABLE IF EXISTS transactions;
CREATE TABLE IF NOT EXISTS transactions (
  id        VARCHAR(100)  NOT NULL,
  userId    VARCHAR(8)    NOT NULL,
  amount    DECIMAL(9,4)  NOT NULL,
  createdAt TIMESTAMP     DEFAULT NOW(),

  PRIMARY KEY (id),
  FOREIGN KEY (userId) REFERENCES users(id)
) ENGINE=InnoDB;

DROP TABLE IF EXISTS tickets;
CREATE TABLE IF NOT EXISTS tickets (
  id            VARCHAR(100)  NOT NULL,
  transactionId VARCHAR(100)  NOT NULL,

  PRIMARY KEY (id),
  FOREIGN KEY (transactionId) REFERENCES transactions(id)
) ENGINE=InnoDB;

DROP TABLE IF EXISTS ticketActivities;
CREATE TABLE IF NOT EXISTS ticketActivities (
  id            INT(11)      UNSIGNED NOT NULL AUTO_INCREMENT,
  ticketId      VARCHAR(100) NOT NULL,
  latitude      FLOAT(10,6),
  longitude     FLOAT(10,6),
  state         VARCHAR(25),
  amount        DECIMAL(9,4),

  PRIMARY KEY (id),
  FOREIGN KEY (ticketId) REFERENCES tickets(id)
) ENGINE=InnoDB;

SET FOREIGN_KEY_CHECKS = 1;
