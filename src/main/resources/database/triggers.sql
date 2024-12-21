USE `monero_ecwid`;

DELIMITER $$

CREATE TRIGGER `update_amount_deposited`
AFTER INSERT ON `monero_transactions`
FOR EACH ROW
BEGIN
	UPDATE `payment_requests`
    SET `amount_deposited` = `amount_deposited` + NEW.`amount`
    WHERE `tx_id` = NEW.`tx_id`;
    
    UPDATE `payment_requests`
    SET `status` = 'PAID'
    WHERE `tx_id` = NEW.`tx_id` AND `amount_deposited` >= `amount_xmr`;
END $$

DELIMITER ;

DELIMITER $$

CREATE TRIGGER `before_insert_payment_request`
BEFORE INSERT ON `payment_requests`
FOR EACH ROW
BEGIN
	SET NEW.`created_at` = CURRENT_TIMESTAMP;
    SET NEW.`updated_at` = CURRENT_TIMESTAMP;
END $$

DELIMITER ;
