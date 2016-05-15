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
  ticketId  VARCHAR(100),
  fareId    INTEGER,
  amount    DECIMAL(9,4)  NOT NULL,
  createdAt TIMESTAMP     DEFAULT NOW(),
  type      VARCHAR(20)   NOT NULL,

  PRIMARY KEY (id),
  UNIQUE KEY  (ticketId),
  FOREIGN KEY (userId) REFERENCES users(id)
) ENGINE=InnoDB;

DROP TABLE IF EXISTS locations;
CREATE TABLE IF NOT EXISTS locations (
    id            INTEGER AUTO_INCREMENT,
    locationName  VARCHAR(64) NOT NULL UNIQUE,
    latitude      REAL NOT NULL,
    longitude     REAL NOT NULL,

    PRIMARY KEY (id)
) ENGINE=InnoDB;

DROP TABLE IF EXISTS fares;
CREATE TABLE IF NOT EXISTS fares (
    id          INTEGER AUTO_INCREMENT,
    locationOne INTEGER NOT NULL,
    locationTwo INTEGER NOT NULL,
    fare        REAL NOT NULL,

    PRIMARY KEY (id),
    FOREIGN KEY (locationOne) REFERENCES locations(id),
    FOREIGN KEY (locationTwo) REFERENCES locations(id)
) ENGINE=InnoDB;

CREATE UNIQUE INDEX fare ON fares (locationOne, locationTwo);

INSERT INTO users (id, email, hash) VALUES ('sgFdFFgd', 'roger.madjos@gmail.com', '');
INSERT INTO transactions (id, userId, ticketId, amount, type) VALUES ('18e7b2a8-6f97-435d-91f6-8729ad059881', 'sgFdFFgd', '558559a1-ee9b-4992-9108-50bf89578abf', 1000, 'topup');
INSERT INTO locations (id, locationName, latitude, longitude) VALUES (1, 'Cagayan de Oro', 8.511742, 124.623316);
INSERT INTO locations (id, locationName, latitude, longitude) VALUES (12, 'Opol', 8.519083, 124.577329);
INSERT INTO locations (id, locationName, latitude, longitude) VALUES (23, 'El Salvador', 8.519083, 124.577329);
INSERT INTO locations (id, locationName, latitude, longitude) VALUES (34, 'Alubijid', 8.571242, 124.474247);
INSERT INTO locations (id, locationName, latitude, longitude) VALUES (45, 'Laguindingan', 8.573983, 124.437641);
INSERT INTO fares (id, locationOne, locationTwo, fare) VALUES (1, 1, 12, 15);
INSERT INTO fares (id, locationOne, locationTwo, fare) VALUES (2, 1, 23, 27);
INSERT INTO fares (id, locationOne, locationTwo, fare) VALUES (3, 1, 34, 42);
INSERT INTO fares (id, locationOne, locationTwo, fare) VALUES (4, 1, 45, 52);
INSERT INTO fares (id, locationOne, locationTwo, fare) VALUES (5, 12, 23, 12);
INSERT INTO fares (id, locationOne, locationTwo, fare) VALUES (6, 12, 34, 27);
INSERT INTO fares (id, locationOne, locationTwo, fare) VALUES (7, 12, 45, 37);
INSERT INTO fares (id, locationOne, locationTwo, fare) VALUES (8, 23, 34, 15);
INSERT INTO fares (id, locationOne, locationTwo, fare) VALUES (9, 23, 45, 25);
INSERT INTO fares (id, locationOne, locationTwo, fare) VALUES (10, 34, 45, 10);

SET FOREIGN_KEY_CHECKS = 1;
