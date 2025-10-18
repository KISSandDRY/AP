# **Deposit Management System**

A console-based Java application for managing bank deposits and accounts. Built with a layered architecture, it supports data persistence, dynamic configuration, and a pipe-enabled command-line interface.

### **Features**

- **Layered Architecture**: Separation of concerns between the console (UI), service (business logic), and repository (data access) layers.

- **Financial Accuracy**: Uses java.math.BigDecimal for all monetary calculations to prevent floating-point errors.

- **Expressive Domain Model**: Employs records and custom value objects (Money, PercentageRate, TermPeriod) for a type-safe and readable codebase.

- **CLI**:

* **Argument Parsing**: A universal argument parser handles flags (--verbose), options (--file \<path>), and positional arguments.

* **Command Pipelines**: Chain commands together just like in a Unix shell (e.g., search ... | sort ... | print).

* **Help System**: Automatic, detailed help for commands (help \<command\_name>).

- **Dynamic Configuration**:

* Configure the application via a properties file, environment variables, or command-line arguments.

* Clear priority: CLI arguments > Environment Variables > app.properties file.

- **Data Persistence**: Save and load application state (deposits and accounts) to and from the file system.

- **NBU Integration**: Simulates real-time updates of interest and tax rates from a central source (National Bank of Ukraine).


### **Project Structure**

The project is organized into a multi-module structure:

- **app/**: Contains the main application entry point (App.java) and handles startup configuration.

- **console/**: The entire command-line interface, including:

* commands/: All user-facing commands, separated by context (deposit, account, system).

* util/: Reusable utilities like the ArgParser, printers.

- **deposit/**: The core domain and business logic of the application.

* domain/: The central business objects (Deposit, DepositAccount) and value objects (Money, Range).

* service/: The DepositService which orchestrates all business rules.

* repository/: Data access layer for saving and loading data.

* interest/: The strategy pattern implementation for different interest calculation methods.

* integration/: The NBU rate update simulator.

* exception/: Custom, specific exceptions for better error handling.


### **Getting Started**

#### **Prerequisites**

- Java JDK 17 or higher

- Apache Maven


#### **Build**

1. Clone the repository.

2. Navigate to the project root directory in your terminal.

3. Run the Maven package command. This will compile the code and create a single, executable JAR file in the target/ directory.\
   mvn clean package


#### **Run**

Execute the application using the generated JAR file.

`java -jar target/Deps.jar`

You can also use startup flags:

- `java -jar target/Deps.jar --help` (Shows startup options)

- `java -jar target/Deps.jar --autoload` (Loads data from default files on start)


### **Configuration**

The application can be configured in three ways (from highest to lowest priority):

1. **Command-Line Arguments**:\
   `java -jar target/Deps.jar --db.deposits.file=my_deposits.dat`

2. **Environment Variables**:
   ```bash
   export DB_ACCOUNTS_FILE="my_accounts.dat"
   java -jar target/Deps.jar
   ```

3. **app.properties File**: Place this file in src/main/resources.\
   \# Default file for deposit products\
   `db.deposits.file=deposits.ser`
   \# Default file for customer accounts\
   `db.accounts.file=accounts.ser`
   \# NBU update interval in milliseconds\
   `nbu.update.interval.ms=5000`


### **Usage Examples**

The application provides a rich command-line interface. Here are a few examples:

- **List all deposits and accounts:**\
  `> list`

- **Add a new deposit product (interactive):**\
  `> add-deposit`

- **Search for deposits with a rate > 5% and print the results:**\
  `> search rate > 0.05 | print`

- **List all accounts, sort them by amount (descending), and print:**\
  `> list --accounts | sort amount dsc | print`

- **Get detailed help for a specific command:**\
  `> help search`
