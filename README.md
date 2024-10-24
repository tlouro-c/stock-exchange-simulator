# Java Trading Simulator

## Introduction

This project marks the final assignment of the Academy+Plus Java module. Having already mastered Java programming and idioms, the focus here is on advanced scenarios involving threads, sockets, and the TCP protocol. You'll be working with a client-server architecture, implementing three independent components that simulate electronic trading: the **Router**, **Broker**, and **Market**.

These components will communicate over the network using a simplified version of the FIX protocol, a standard in financial markets. This project is not about trading algorithms (although you can experiment with them later) but rather about building a robust, performant messaging platform.

## Components Overview

### 1. Router (Server)

The **Router** serves as the core server, facilitating communication between Brokers and Markets. It assigns a random 6-digit ID to each connected client (Broker/Market), which is then used in their requests and responses. The Router maintains a routing table that maps each ID to the respective client socket channel.

#### Key Functions

- **Non-blocking Sockets**: The Router uses non-blocking sockets for improved efficiency and performance.
- **Message Forwarding**: When a new message is received, the Router spawns a new thread to process it. The message undergoes a series of transformations using the **Chain of Responsibility** pattern, which handles the following steps:
  1. **Checksum Validation**: Ensures the message is not corrupted.
  2. **Destination Identification**: Looks up the routing table to find the correct recipient.
  3. **Message Forwarding**: Sends the message to the appropriate client (Broker or Market).

This component doesn't handle business logic—it simply acts as a message dispatcher.

### 2. Market (Client)

The **Market** connects to the Router and receives its unique ID. It manages a database that stores transaction records and stock information. To ensure efficiency in a multi-threaded environment, the Market uses a connection pool to handle JDBC database operations.

#### Key Functions

- **Database Management**: Two tables are created:
  - **Transactions Table**: Logs every completed transaction.
  - **Stocks Table**: Holds information about available instruments and their versions.

  - **Stock Price Initialization**: When the Market starts, it fetches real stock values using the Polygon API. If the API key is not provided in the `.env` file, the Market assigns dummy values to the stocks instead.

- **Stock Price Updates**: Every 5 seconds, the Market updates stock prices with a 1% volatility factor to simulate real market activity. 	This ensures the stock prices remain dynamic, mimicking real-time trading environments.
  
- **Transaction Processing**: The Market uses the **Chain of Responsibility** pattern here as well, processing each message as follows:
  1. **Checksum Validation**: Double-checks message integrity.
  2. **FIX Message Conversion**: Converts the incoming FIX message into a `Transaction` object.
  3. **Stock Verification**: Fetches the requested stock from the database, ensuring thread safety by using a version field to prevent **Dirty Reads**. The version ensures that when a stock is fetched and later updated, no other thread has altered the data in the meantime.
  4. **Order Execution**: If the requested stock is available at the specified price, the order is executed. The stock table is updated, and the transaction is logged in the transactions table.
  5. **Response Generation**: The Market responds to the Broker, either with an "Executed" or "Rejected" message.

The Market's database operations are protected against concurrent access by using row versions, which ensures that stock availability checks and updates are always consistent.

### 3. Broker (Client)

The **Broker** component connects to the Router and handles placing buy and sell orders into the Market. Here, I implemented the **Strategy Pattern** to streamline the process of placing orders, making it easy to extend for different trading algorithms.

#### Key Functions

- **Order Creation**: The Broker uses the **Factory Pattern** to create order instances (either `Buy` or `Sell`). Additionally, the **Builder Pattern** is employed to configure each order, making it flexible and modular.
  
- **Thread Management**: One thread handles I/O operations with the Router, while another thread processes the actual order placement.
  
- **Order Summary**: Upon receiving a response from the Market (either "Executed" or "Rejected"), the Broker prints a summary of the transaction to `STDOUT`, providing immediate feedback.

---

## Project Structure

This project is organized as a **multi-module Maven** build with separate modules for each component: Router, Market, and Broker.

### Setup

Before building the project, you should acquire a Polygon API key. This key is necessary for fetching real stock data for the Market component. If you do not provide a key, the application will use dummy stock values.

1. Get your API key from [Polygon.io](https://polygon.io/).
2. Create a `.env` file in the folder where you will run the programs. This file should contain the following line:

   ```plaintext
   api-key=your_api_key_here
   ```

If you don’t want to use the API, you can leave the `.env` file out, and the system will simulate stock prices with dummy values.

### Building the Project

To build the project and generate runnable JARs for each component, run the following command from the root directory of the project:

```bash
mvn clean package
```

This will create a `jars` directory in the root of the project, which contains the compiled JARs for each component (`router`, `market`, and `broker`).

### Running the Components

1. **Start the Router**: The Router is the central component of the system and must be started first. To run it, use the following command:

   ```bash
   java -jar jars/router.jar
   ```

2. **Start the Market**: After starting the Router, you can start the Market component. The Market will connect to the Router and receive an ID. Use the command:

   ```bash
   java -jar jars/market.jar
   ```

   After the Market starts, it will print a unique ID to the console, which you will need to use for the Broker.

3. **Start the Broker**: Once the Market is running and has provided an ID, you can start the Broker component. The Broker needs the Market's ID to interact with it. To run the Broker, use the following command, replacing `<market_id>` with the actual ID received from the Market:

   ```bash
   java -jar jars/broker.jar <market_id>
   ```

Once all components are running, the system will simulate stock trading between the Broker and the Market via the Router. The Broker will submit buy/sell orders, and the Market will process these transactions based on the stock information.


https://liakh-aliaksandr.medium.com/java-sockets-i-o-blocking-non-blocking-and-asynchronous-fb7f066e4ede
