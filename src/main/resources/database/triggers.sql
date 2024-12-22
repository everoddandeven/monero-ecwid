USE `monero_ecwid`;

DELIMITER $$

CREATE TRIGGER `update_amount_deposited`
AFTER INSERT ON `monero_transactions`
FOR EACH ROW
BEGIN

	DECLARE `amount_dep` BIGINT DEFAULT 0;
	DECLARE `new_sum` BIGINT DEFAULT 0;
    
    SELECT `amount_deposited` INTO `amount_dep`
    FROM `payment_requests`
    WHERE `tx_id` = NEW.`tx_id`;
    
    SET `new_sum` = `amount_dep` + NEW.`amount`;
    
	UPDATE `payment_requests`
    SET `amount_deposited` = `new_sum`
    WHERE `tx_id` = NEW.`tx_id`;
    
    UPDATE `payment_requests`
    SET `status` = 'PAID', `ecwid_api_updated` = FALSE
    WHERE `amount_deposited` > `amount_xmr` AND `status` = 'UNPAID';

END $$

DELIMITER ;

DELIMITER $$

CREATE TRIGGER `before_insert_payment_request`
BEFORE INSERT ON `payment_requests`
FOR EACH ROW
BEGIN
	SET NEW.`created_at` = CURRENT_TIMESTAMP, NEW.`updated_at` = CURRENT_TIMESTAMP;
END $$

DELIMITER ;

