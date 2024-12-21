# Monero ECWID

[![License][license-badge]](LICENSE.md)
[![XMR Donated](https://img.shields.io/badge/donated-0_XMR-blue?logo=monero)](https://github.com/everoddandeven/monero-ecwid?tab=readme-ov-file#monero)

This project provides a payment integration solution for Ecwid stores using Monero as a payment method. It leverages the [monero-java API](https://github.com/woodser/monero-java) for seamless integration, ensuring secure and private payments.

## Features

- **Monero Payment Gateway**: Integrate Monero as a payment method in your Ecwid store.
- **Light Wallet Server (LWS)**: Uses Monero's Node RPC API for transaction management.
- **Easy Setup**: Simplified integration process for Ecwid store owners.
- **Secure Transactions**: Monero ensures privacy and security for both merchants and customers.
- **Real-Time Payment Status**: Get immediate updates on payment status using the LWS API.

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

4. **Create MySQL User**

   ```bash
   CREATE USER 'monero_ecwid' IDENITIFIED BY 'devpassword';
   GRANT ALL PRIVILEGES ON monero_ecwid.* TO 'monero_ecwid'@'localhost';
   FLUSH PRIVILEGES;
   ```

5. **Build server application**

   ```bash
   mvn clean package
   ```

6. **Start server application**

   ```bash
   java -jar target/server-0.0.1-SNAPSHOT.jar
   ```

## Usage

1. Create Payment Request: The integration will handle creating a Monero payment request when a customer proceeds to checkout in your Ecwid store.
2. Handle Payment: Once the payment is initiated, the integration will monitor the transaction and provide real-time updates to both the merchant and the customer.
3. Payment Confirmation: Once the payment is confirmed, the order status in Ecwid will be updated, and the customer will be redirected to the confirmation page.
4. Error Handling: If there are any issues with the payment (e.g., payment not received or canceled), appropriate error messages will be displayed to the customer.

## Example Flow

1. A customer selects Monero as the payment method at checkout.
2. The server generates a Monero invoice using the monero-java API.
3. The customer pays the invoice to complete the transaction.
4. Once the transaction is confirmed, the Ecwid store's order status is updated to "PAID".
5. The customer is redirected to a confirmation page.

## Donating

Please consider donating to support the development of this project.

### Monero

<p align="center">
 <img src="xmr_qrcode.png" width="115" height="115" alt="xmrQrCode"/><br>
 <code>87qmeEtvQvuELasb4ybjV4iE17KF14SKTPvhgzAwrD5k1vWZUTqsWM52pbyHy8Wb97jCkq4pm5hLKaw39pEnvUKPPf3GFJA</code>
</p>

[license-badge]: https://img.shields.io/badge/license-MIT-blue.svg

