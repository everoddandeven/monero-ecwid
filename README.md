# Monero ECWID

[![License][license-badge]](LICENSE.md)
[![XMR Donated](https://img.shields.io/badge/donated-0_XMR-blue?logo=monero)](https://github.com/everoddandeven/monero-ecwid?tab=readme-ov-file#monero)

This project provides a payment integration solution for Ecwid stores using Monero as a payment method. It leverages the [monero-java API](https://github.com/woodser/monero-java) for seamless integration, ensuring secure and private payments.

## Features

- **Monero Payment Gateway**: Integrate Monero as a payment method in your Ecwid store.
- **Monero RPC API**: Uses Monero's Node RPC API for transaction management.
- **Easy Setup**: Simplified integration process for Ecwid store owners.
- **Secure Transactions**: Monero ensures privacy and security for both merchants and customers.
- **Real-Time Payment Status**: Get immediate updates on payment status using the Node RPC API.

## Prerequisites

Before you begin, make sure you have the following installed:

- **Java** (v17 or later)
- **Monero Node**: A running instance of Monero node for managing transactions. You can found an already running instance at [monero.fail](https://monero.fail)
- **Ecwid Store**: An active Ecwid store with API access enabled

## Installation

1. **Clone the repository**:

   ```bash
   git clone https://github.com/everoddandeven/monero-ecwid.git
   cd monero-ecwid
   ```

2. **Install dependencies**

   ```bash
   sudo apt update && sudo apt install build-essential cmake pkg-config libssl-dev libzmq3-dev libunbound-dev libsodium-dev libunwind8-dev liblzma-dev libreadline6-dev libexpat1-dev libpgm-dev qttools5-dev-tools libhidapi-dev libusb-1.0-0-dev libprotobuf-dev protobuf-compiler libudev-dev libboost-chrono-dev libboost-date-time-dev libboost-filesystem-dev libboost-locale-dev libboost-program-options-dev libboost-regex-dev libboost-serialization-dev libboost-system-dev libboost-thread-dev python3 ccache doxygen graphviz nettle-dev libevent-dev
   ```

3. **Install MySQL Server dependency**

   ```bash
   sudo apt update && sudo apt install mysql-server
   ```

4. **MySQL secure installation**

   ```bash
   sudo mysql_secure_installation
   ```
5. **Edit mysqld configuration at `/etc/mysql/mysql.conf.d/mysqld.cnf`**
   
   ```bash
   [mysqld]
   # Add this line
   log-bin-trust-function-creators = 1
   ```

6. **Create MySQL User**

   ```bash
   sudo mysql -u root -p
   ```

   ```bash
   CREATE USER 'monero_ecwid' IDENITIFIED BY 'devpassword';
   GRANT ALL PRIVILEGES ON monero_ecwid.* TO 'monero_ecwid'@'localhost';
   FLUSH PRIVILEGES;
   ```

8. **Build server application**

   ```bash
   mvn clean package
   ```

9. **Start server application**

   ```bash
   java -jar target/monero-ecwid-0.0.1-SNAPSHOT.jar
   ```

## Configuration
You must provide a valid configuration before running server


   ```bash
   # moneroecwid.conf example
   db-host=localhost
   db-port=3306
   db-username=monero_ecwid
   db-password=devpassword
   required-confirmations=1
   client-secret=custom_app_client_secret
   wallet-address=9xjCAvNQaYYDLc5UsxQPZtP8nNDUJnuhiacmMaE3zzTBetYcLusyCtD5kuQNNGo3TVCEUFKjd7yjeE3rCjPahy3RQGa39aJ
   wallet-view-key=9ce15a203d7e31aa930e55e4bf18e65509fb73ba316528182b77b079bb997b0d
   wallet-server-uri=http://node2.monerodevs.org:28089
   wallet-password=supersecretpassword123
   wallet-net-type=testnet
   ```

   ```bash
   java -Dconfig-file=/path/to/moneroecwid.conf -jar monero-ecwid-0.0.1-SNAPSHOT.jar
   ```

## ECWID Integration

1. Apply for a [Ecwid Paid Plan](https://www.ecwid.com/pricing) 
2. Create a new [Custom App](https://my.ecwid.com/cp/#develop-apps) for your store
3. Copy your Custom App `Client Secret` and paste it in your configuration file `client-secret=custom_app_client_secret` 
4. Write to Ecwid support and ask to change the `Payment URL` with your payment gateway `https://yourservername.com/v1/monero/ecwid`, custom app will be enabled within two days
5. Enjoy Monero Ecwid payments!

## Usage

1. **Create Payment Request:** The integration will handle creating a Monero payment request when a customer proceeds to checkout in your Ecwid store.
2. **Handle Payment:** Once the payment is initiated, the integration will monitor the transaction and provide real-time updates to both the merchant and the customer.
3. **Payment Confirmation:** Once the payment is confirmed, the order status in Ecwid will be updated, and the customer will be redirected to the confirmation page.
4. **Error Handling:** If there are any issues with the payment (e.g., payment not received or canceled), appropriate error messages will be displayed to the customer.

## Example Flow

1. A customer creates a new cart in the store and then selects Monero as the payment method at checkout.
2. The payment gateway server generates a new Monero invoice.
3. The customer pays the invoice to complete the transaction.
4. Once the transaction is confirmed, the Ecwid store's order status is updated to "PAID".
5. The customer is redirected to a confirmation page.
6. If the invoice is not paid within 15 minutes, it will expire and the order will be cancelled.

## Donating

Please consider donating to support the development of this project.

### Monero

<p align="center">
 <img src="xmr_qrcode.png" width="115" height="115" alt="xmrQrCode"/><br>
 <code>87qmeEtvQvuELasb4ybjV4iE17KF14SKTPvhgzAwrD5k1vWZUTqsWM52pbyHy8Wb97jCkq4pm5hLKaw39pEnvUKPPf3GFJA</code>
</p>

[license-badge]: https://img.shields.io/badge/license-MIT-blue.svg

