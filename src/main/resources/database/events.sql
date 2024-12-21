USE `monero_ecwid`;

CREATE EVENT IF NOT EXISTS `expire_status_event`
ON SCHEDULE EVERY 1 MINUTE
DO
  UPDATE `payment_requets`
  SET `status` = 'EXPIRED'
  WHERE `status` = 'UNPAID' AND `created_at` <= NOW() - INTERVAL 15 MINUTE;
