CREATE TABLE IF NOT EXISTS `locations` (
    `id` INTEGER PRIMARY KEY AUTOINCREMENT,
    `location_name` CHAR(64) NOT NULL UNIQUE,
    `latitude` REAL NOT NULL,
    `longitude` REAL NOT NULL
);

CREATE TABLE IF NOT EXISTS `fares` (
    `id` INTEGER PRIMARY KEY AUTOINCREMENT,
    `location_one` INTEGER NOT NULL,
    `location_two` INTEGER NOT NULL,
    `fare` REAL NOT NULL,
    FOREIGN KEY (`location_one`) REFERENCES `locations`(`id`),
    FOREIGN KEY (`location_two`) REFERENCES `locations`(`id`)
);

CREATE UNIQUE INDEX `fare` ON `fares` (`location_one`, `location_two`);

INSERT INTO `locations` (`id`, `location_name`, `latitude`, `longitude`) VALUES (1, 'Cagayan de Oro', 8.511742, 124.623316);
INSERT INTO `locations` (`id`, `location_name`, `latitude`, `longitude`) VALUES (12, 'Opol', 8.519083, 124.577329);
INSERT INTO `locations` (`id`, `location_name`, `latitude`, `longitude`) VALUES (23, 'El Salvador', 8.519083, 124.577329);
INSERT INTO `locations` (`id`, `location_name`, `latitude`, `longitude`) VALUES (34, 'Alubijid', 8.571242, 124.474247);
INSERT INTO `locations` (`id`, `location_name`, `latitude`, `longitude`) VALUES (45, 'Laguindingan', 8.573983, 124.437641);

INSERT INTO `fares` (`location_one`, `location_two`, `fare`) VALUES (1, 12, 15);
INSERT INTO `fares` (`location_one`, `location_two`, `fare`) VALUES (12, 23, 12);
INSERT INTO `fares` (`location_one`, `location_two`, `fare`) VALUES (23, 34, 15);
INSERT INTO `fares` (`location_one`, `location_two`, `fare`) VALUES (34, 45, 10);