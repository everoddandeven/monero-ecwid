USE `monero_ecwid`;

DROP EVENT IF EXISTS `payment_request_expiration_event`;

CREATE EVENT IF NOT EXISTS `payment_request_expiration_event`
ON SCHEDULE EVERY 1 MINUTE
DO
  UPDATE `payment_requests`
  SET `status` = 'EXPIRED', `ecwid_api_updated` = FALSE
  WHERE `status` = 'UNPAID' AND `amount_deposited` = 0 AND `created_at` <= NOW() - INTERVAL 15 MINUTE;
