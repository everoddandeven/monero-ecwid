USE `monero_ecwid`;

CREATE VIEW `paid_requests` AS
	SELECT * FROM `payment_requests` WHERE `status` = 'PAID';

CREATE VIEW `unpaid_requests` AS
	SELECT * FROM `payment_requests` WHERE `status` = 'UNPAID';

CREATE VIEW `partially_paid_requests` AS
	SELECT * FROM `unpaid_requests` WHERE `amount_deposited` > 0;

CREATE VIEW `cancelled_requests` AS
	SELECT * FROM `payment_requests` WHERE `status` = 'CANCELLED';

CREATE VIEW `refunded_requests` AS
	SELECT * FROM `payment_requests` WHERE `status` = 'REFUNDED';

CREATE VIEW `expired_requests` AS
	SELECT * FROM `payment_requests` WHERE `status` = 'EXPIRED';

CREATE VIEW `used_addresses` AS
	SELECT `address`, `address_account_index`, `address_index`
    FROM `payment_requests`;
