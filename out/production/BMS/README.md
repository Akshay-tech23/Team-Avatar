# Vision Bank of India - Console Banking Application

A lightweight, secure, console-based Banking Management System (BMS) written in Java. The application models realistic banking workflows including secure password hashing, account creation validation, financial transactions, and profile management.

---

## 📂 File Architecture & Execution Flow

When developing and running this Java application, two types of files are involved:

1. **`Main.java` (Source Code)**:
   The human-readable source code file containing the class definitions, transaction logic, security checks, and console interface loop. This is where all source edits are made.
2. **`Main.class` & `Account.class` (Compiled Bytecode)**:
   When compiled, the Java compiler (`javac`) processes the source code into `.class` files containing platform-independent bytecode. The Java Virtual Machine (JVM) reads these bytecode files to run the program on your computer.

### Compilation and Execution Instructions

To compile and run the application from your terminal:

```bash
# Compile the Java source code
javac Main.java

# Run the compiled application
java Main
```

---

## 🛠️ System Architecture & Classes

The project uses Object-Oriented Programming (OOP) design, dividing responsibilities into two distinct classes:

### 1. `Account` Class
Acts as the data structure and blueprint representing a single customer account.

* **Unique Identification**: Generates a unique 12-digit random account number and a unique IFSC code (`VBIN0...`) for every new account.
* **Encapsulation**: Keeps user profile details (`name`, `mobile`, `email`, `place`, `balance`, `pinHash`) securely separated per instance.
* **Transaction History**: Maintains a private rolling array of the last 100 transactions associated with the account.

### 2. `Main` Class
Serves as the application driver and interface controller.

* **State Management**: Hosts the main repository of accounts (`accounts[]`) and tracks the total account count.
* **SHA-256 Password Cryptography**: Hashes all security PINs before storage and verification to ensure no plaintext passwords exist in memory.
* **Interactive Dashboard**: Handles the console loops, accepts input validation criteria, and routes operations to the selected account.

---

## 🚀 Key Features & Functionality

### 🔒 SHA-256 PIN Security
PIN numbers are never stored as plain text. The application uses Java's `MessageDigest` library to hash and verify security PINs using the **SHA-256** algorithm.
* **Sign-up**: The entered 4-digit PIN is hashed and stored in the account object.
* **Operations**: PIN inputs for logins, withdrawals, and transfers are hashed on the fly and compared to the stored hash.

### 🔢 Unique Account & IFSC Code Generation
* **Account Numbers**: Generated dynamically as unique 12-digit values between `1000000001L` and `999999999999L`.
* **IFSC Codes**: Generated using the pattern `VBIN0` + a random digit + a random uppercase letter + a random 2-digit number (e.g., `VBIN07X52`).
* **Uniqueness Checks**: The generator validates all newly created IDs against active records to prevent duplicate collisions.

### 🛡️ Core Banking Operations & Validations
* **Account Signup**: Enforces validations:
  * Minimum initial deposit of **INR 500.00**.
  * Mobile number must be exactly **10 digits**.
  * Email must contain `@` and `.`.
  * Security PIN must be exactly **4 digits**.
* **Deposits & Withdrawals**: Updates user balance and rejects negative transaction values or withdrawals that drop the balance below the minimum limit of INR 500.00.
* **Fund Transfers**: Checks for recipient existence, prevents self-transfers, validates source balance, and updates transaction ledgers on both sender and recipient accounts.
* **Profile Management**: Allows customers to securely update mobile numbers, email addresses, cities, and security PINs.
* **Account Search**: Admin/User enquiry tool to fetch profile summaries securely by validating credentials.
