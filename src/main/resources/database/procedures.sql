USE `monero_ecwid`;

DELIMITER $$

CREATE PROCEDURE `insert_payment_request`(
	IN `_tx_id` VARCHAR(255), 
    IN `_address` VARCHAR(255),
    IN `_address_account_index` INT,
    IN `_address_index` INT,
    IN `_amount_usd` DECIMAL(9, 2),
    IN `_amount_xmr` BIGINT
)
BEGIN
	INSERT INTO `payment_requests`(`tx_id`, `address`, `address_account_index`, `address_index`, `amount_usd`, `amount_xmr`)
    VALUES (`_tx_id`, `_address`, `_address_account_index`, `_address_index`, `_amount_usd`, `_amount_xmr`);
END $$

DELIMITER ;


DELIMITER $$

CREATE PROCEDURE `set_payment_request_status`(IN `_tx_id` VARCHAR(255), IN `_status` VARCHAR(255))
BEGIN
	UPDATE `payment_requests`
    SET `status` = `_status`
    WHERE `tx_id` = `_tx_id`;
END $$

DELIMITER ;
