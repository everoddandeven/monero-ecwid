DROP DATABASE IF EXISTS `monero_ecwid`;
CREATE DATABASE `monero_ecwid`;
USE `monero_ecwid`;

CREATE TABLE `wallet_config` (
	`id` VARCHAR(255) NOT NULL,
    `address` VARCHAR(255) NOT NULL,
    `view_key` VARCHAR(255) NOT NULL,
    `confirmations_required` BIGINT NOT NULL DEFAULT 10,    
    PRIMARY KEY (`id`)
);

CREATE TABLE `payment_requests` (
	`tx_id` VARCHAR(255) NOT NULL,
    `store_id` INT NOT NULL,
    `store_token` TEXT NOT NULL,
	`address` VARCHAR(255) NOT NULL,
	`address_account_index` INT NOT NULL,
	`address_index` INT NOT NULL,
    `amount_usd` DECIMAL(9, 2) NOT NULL,
    `amount_xmr` BIGINT NOT NULL,
    `amount_deposited` BIGINT NOT NULL DEFAULT 0,
    `return_url` TEXT NOT NULL,
    `created_at` TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    `updated_at` TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    `status` ENUM ( 'PAID', 'UNPAID', 'CANCELLED', 'REFUNDED', 'EXPIRED' ) NOT NULL DEFAULT 'UNPAID',
    PRIMARY KEY (`tx_id`)
);

CREATE TABLE `monero_transactions` (
	`tx_hash` VARCHAR(255),
    `height` BIGINT NOT NULL,
	`tx_id` VARCHAR(255),
    `amount` BIGINT NOT NULL CHECK (`amount` > 0),
    PRIMARY KEY (`tx_hash`),
    FOREIGN KEY (`tx_id`) REFERENCES `payment_requests`(`tx_id`)
);


INSERT INTO `wallet_config`(`id`, `address`, `view_key`, `confirmations_required`) VALUES 
('prod', '', '', 10),
('dev', '', '', 10);
