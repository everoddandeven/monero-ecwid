USE `monero_ecwid`;

DELIMITER $$

CREATE FUNCTION `payment_request_exists`(`_tx_id` VARCHAR(255))
RETURNS INT
READS SQL DATA
BEGIN
	DECLARE `_count` INT DEFAULT 0;
    SELECT COUNT(*) INTO `_count` FROM `payment_requests` WHERE `tx_id` = `_tx_id`;
    
    IF `_count` > 0 THEN 
		RETURN 1;
    END IF;
    
    RETURN 0;
END $$

DELIMITER ;

DELIMITER $$

CREATE FUNCTION `is_address_used`(`_address` VARCHAR(255))
RETURNS INT
READS SQL DATA
BEGIN
	DECLARE `_count` INT DEFAULT 0;
    SELECT COUNT(*) INTO `_count` FROM `used_addresses` WHERE `address` = `_address`;
    
    IF `_count` > 0 THEN 
		RETURN 1;
    END IF;
    
    RETURN 0;
END $$

DELIMITER ;