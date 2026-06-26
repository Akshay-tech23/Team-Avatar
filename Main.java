import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Scanner;

class Account {
    String accountNumber;
    String name;
    String mobile;
    String email;
    String place;
    double balance;
    String pinHash; // Securely stored SHA-256 hash of the PIN
    String ifsc;

    // Static list to store all generated IFSC codes to ensure uniqueness
    static java.util.List<String> ifscList = new java.util.ArrayList<>();

    // Generate unique IFSC code: VBIN0 + random digit + random letter + random
    // number
    private static String generateUniqueIfsc() {
        java.util.Random random = new java.util.Random();
        while (true) {
            int digit = random.nextInt(9);
            char letter = (char) ('A' + random.nextInt(26));
            int num = random.nextInt(10, 100);
            String code = "VBIN0" + digit + letter + num;
            if (!ifscList.contains(code)) {
                ifscList.add(code);
                return code;
            }
        }
    }

    static java.util.List<String> AccList = new java.util.ArrayList<>();

    // Generate unique 12-digit random account number
    private static String generateUniqueAcc() {
        java.util.Random random = new java.util.Random();
        while (true) {
            long digit = random.nextLong(100000000000L, 1000000000000L);

            String code = String.valueOf(digit);
            if (!AccList.contains(code)) {
                AccList.add(code);
                return code;
            }
        }
    }

    // Arrays for tracking transactions
    String[] transactions = new String[100];
    int transactionCount = 0;

    public Account(String accountNumber, String name, String mobile, String email, String place, double balance,
            String pinHash) {
        if (accountNumber == null || accountNumber.isEmpty()) {
            this.accountNumber = generateUniqueAcc();
        } else {
            this.accountNumber = accountNumber;
            if (!AccList.contains(accountNumber)) {
                AccList.add(accountNumber);
            }
        }
        this.name = name;
        this.mobile = mobile;
        this.email = email;
        this.place = place;
        this.balance = balance;
        this.pinHash = pinHash;
        this.ifsc = generateUniqueIfsc();
        addTransaction("INITIAL DEPOSIT: INR " + balance + " (Account opened)");
    }

    public void addTransaction(String entry) {
        if (transactionCount < transactions.length) {
            transactions[transactionCount++] = entry;
        } else {
            // Shift transactions to make room if array is full
            for (int i = 1; i < transactions.length; i++) {
                transactions[i - 1] = transactions[i];
            }
            transactions[transactions.length - 1] = entry;
        }
    }
}

public class Main {
    static Account[] accounts = new Account[100];
    static int accountCount = 0;
    static int nextAccountNumber = 1001;

    // Helper method to hash PIN strings using SHA-256
    public static String hashPin(String pin) {
        try {
            MessageDigest digest = MessageDigest.getInstance("SHA-256");
            byte[] encodedHash = digest.digest(pin.getBytes());
            StringBuilder hexString = new StringBuilder();
            for (byte b : encodedHash) {
                String hex = Integer.toHexString(0xff & b);
                if (hex.length() == 1) {
                    hexString.append('0');
                }
                hexString.append(hex);
            }
            return hexString.toString();
        } catch (NoSuchAlgorithmException e) {
            throw new RuntimeException("SHA-256 hashing algorithm not found", e);
        }
    }

    public static void main(String[] args) {
        // Seed two initial accounts with secure hashed PINs ("1234" -> hash and "5678"
        // -> hash)
        accounts[accountCount++] = new Account("1001", "Amit Sharma", "9876543210", "amit@example.com", "Mumbai",
                5000.0, hashPin("1234"));
        accounts[accountCount++] = new Account("1002", "Priya Patel", "8765432109", "priya@example.com", "Pune",
                10000.0, hashPin("5678"));

        Scanner scanner = new Scanner(System.in);
        while (true) {
            System.out.println("\n==================================================");
            System.out.println("              VISION BANK OF INDIA                ");
            System.out.println("==================================================");
            System.out.println(" 1. Account Signup");
            System.out.println(" 2. Account Login");
            System.out.println(" 3. Search Account");
            System.out.println(" 4. Exit");
            System.out.println("==================================================");
            System.out.print("Please enter your choice (1-4): ");

            int choice = scanner.nextInt();
            scanner.nextLine(); // Consume newline

            if (choice == 1) {
                // ACCOUNT CREATION / SIGNUP
                System.out.println("\n--- ACCOUNT SIGNUP ---");
                System.out.print("Enter Full Name: ");
                String name = scanner.nextLine();
                System.out.print("Enter Mobile Number (10 digits): ");
                String mobile = scanner.nextLine();
                System.out.print("Enter Email Address: ");
                String email = scanner.nextLine();
                System.out.print("Enter Place: ");
                String place = scanner.nextLine();
                System.out.print("Enter Initial Deposit (Min INR 500): ");
                double balance = scanner.nextDouble();
                scanner.nextLine(); // Consume newline
                System.out.print("Create a 4-Digit Security PIN: ");
                String pin = scanner.nextLine();

                // VALIDATIONS
                if (balance < 500) {
                    System.out.println("[ERROR] Initial deposit must be at least INR 500.00!");
                    continue;
                }
                if (mobile.length() != 10) {
                    System.out.println("[ERROR] Mobile number must be exactly 10 digits!");
                    continue;
                }
                if (!email.contains("@") || !email.contains(".")) {
                    System.out.println("[ERROR] Email address is invalid!");
                    continue;
                }
                if (pin.length() != 4) {
                    System.out.println("[ERROR] Security PIN must be exactly 4 digits!");
                    continue;
                }

                // Generate Account Number, hash PIN, and save
                Account newAcc = new Account(null, name, mobile, email, place, balance, hashPin(pin));
                accounts[accountCount++] = newAcc;

                System.out.println("\n[SUCCESS] Account Created Successfully!");
                System.out.println(" Account Number : " + newAcc.accountNumber);
                System.out.println(" IFSC Code      : " + newAcc.ifsc);
            } else if (choice == 2) {
                // ACCOUNT LOGIN
                System.out.print("\nEnter Account Number: ");
                String accNum = scanner.nextLine();
                System.out.print("Enter 4-Digit PIN: ");
                String pin = scanner.nextLine();

                // Look up account in the array and verify PIN using hashed values
                Account loggedIn = null;
                String hashedInput = hashPin(pin);
                for (int i = 0; i < accountCount; i++) {
                    if (accounts[i].accountNumber.equals(accNum) && accounts[i].pinHash.equals(hashedInput)) {
                        loggedIn = accounts[i];
                        break;
                    }
                }

                if (loggedIn == null) {
                    System.out.println("[ERROR] Invalid Account Number or PIN!");
                    continue;
                }

                System.out.println("\n[SUCCESS] Login successful! Welcome, " + loggedIn.name);
                boolean loggedInLoop = true;

                // Dashboard loop
                while (loggedInLoop) {
                    System.out.println("\n==================================================");
                    System.out.println("   DASHBOARD | " + loggedIn.name + " (Acc: " + loggedIn.accountNumber + ")");
                    System.out.println("==================================================");
                    System.out.println(" 1. Deposit");
                    System.out.println(" 2. Withdrawal");
                    System.out.println(" 3. Fund Transfer");
                    System.out.println(" 4. View Profile & Balance");
                    System.out.println(" 5. Update Profile Details");
                    System.out.println(" 6. View Transaction History");
                    System.out.println(" 7. Logout");
                    System.out.println("==================================================");
                    System.out.print("Please enter choice (1-7): ");

                    int userChoice = scanner.nextInt();
                    scanner.nextLine(); // Consume newline

                    if (userChoice == 1) {
                        // DEPOSIT
                        System.out.print("Enter amount to deposit: ");
                        double amt = scanner.nextDouble();
                        scanner.nextLine();
                        if (amt > 0) {
                            loggedIn.balance += amt;
                            loggedIn.addTransaction("DEPOSIT: INR " + amt + " | New Balance: INR " + loggedIn.balance);
                            System.out.println("[SUCCESS] Deposit completed! New Balance: INR " + loggedIn.balance);
                        } else {
                            System.out.println("[ERROR] Invalid amount. Must be greater than zero.");
                        }
                    } else if (userChoice == 2) {
                        // WITHDRAWAL
                        System.out.print("Enter amount to withdraw: ");
                        double amt = scanner.nextDouble();
                        scanner.nextLine();
                        System.out.print("Enter PIN to authorize: ");
                        String verifyPin = scanner.nextLine();

                        if (!loggedIn.pinHash.equals(hashPin(verifyPin))) {
                            System.out.println("[ERROR] Incorrect PIN! Transaction cancelled.");
                        } else if (amt <= 0) {
                            System.out.println("[ERROR] Invalid amount. Must be greater than zero.");
                        } else if (loggedIn.balance - amt < 500) {
                            System.out.println(
                                    "[ERROR] Insufficient balance! Remaining balance must be at least INR 500.00.");
                        } else {
                            loggedIn.balance -= amt;
                            loggedIn.addTransaction(
                                    "WITHDRAWAL: INR " + amt + " | Remaining Balance: INR " + loggedIn.balance);
                            System.out.println(
                                    "[SUCCESS] Withdrawal completed! Remaining Balance: INR " + loggedIn.balance);
                        }
                    } else if (userChoice == 3) {
                        // FUND TRANSFER
                        System.out.print("Enter Recipient Account Number: ");
                        String toAccNum = scanner.nextLine();
                        System.out.print("Enter amount to transfer: ");
                        double amt = scanner.nextDouble();
                        scanner.nextLine();
                        System.out.print("Enter PIN to authorize transfer: ");
                        String verifyPin = scanner.nextLine();

                        if (!loggedIn.pinHash.equals(hashPin(verifyPin))) {
                            System.out.println("[ERROR] Incorrect PIN! Transfer cancelled.");
                            continue;
                        }

                        if (loggedIn.accountNumber.equals(toAccNum)) {
                            System.out.println("[ERROR] Cannot transfer to the same account!");
                            continue;
                        }

                        // Search recipient
                        Account recipient = null;
                        for (int i = 0; i < accountCount; i++) {
                            if (accounts[i].accountNumber.equals(toAccNum)) {
                                recipient = accounts[i];
                                break;
                            }
                        }

                        if (recipient == null) {
                            System.out.println("[ERROR] Recipient account not found!");
                        } else if (amt <= 0) {
                            System.out.println("[ERROR] Invalid amount. Must be greater than zero.");
                        } else if (loggedIn.balance - amt < 500) {
                            System.out
                                    .println("[ERROR] Insufficient balance to maintain minimum balance of INR 500.00.");
                        } else {
                            loggedIn.balance -= amt;
                            recipient.balance += amt;
                            loggedIn.addTransaction("TRANSFER SENT: Sent INR " + amt + " to Account: " + toAccNum
                                    + " | Remaining Balance: INR " + loggedIn.balance);
                            recipient.addTransaction("TRANSFER RECEIVED: Received INR " + amt + " from Account: "
                                    + loggedIn.accountNumber + " | New Balance: INR " + recipient.balance);
                            System.out.println(
                                    "[SUCCESS] Transfer successful! Your New Balance: INR " + loggedIn.balance);
                        }
                    } else if (userChoice == 4) {
                        // DISPLAY DETAILS
                        System.out.println("\n==================================================");
                        System.out.println("                 ACCOUNT PROFILE                  ");
                        System.out.println("==================================================");
                        System.out.println(" Name           : " + loggedIn.name);
                        System.out.println(" Mobile         : " + loggedIn.mobile);
                        System.out.println(" Email          : " + loggedIn.email);
                        System.out.println(" Place          : " + loggedIn.place);
                        System.out.println(" Account Number : " + loggedIn.accountNumber);
                        System.out.println(" Balance        : INR " + loggedIn.balance);
                        System.out.println(" IFSC Code      : " + loggedIn.ifsc);
                        System.out.println("==================================================");
                    } else if (userChoice == 5) {
                        // UPDATE PROFILE DETAILS
                        System.out.println("\n--- UPDATE PROFILE DETAILS ---");
                        System.out.println(" 1. Update Mobile Number");
                        System.out.println(" 2. Update Email Address");
                        System.out.println(" 3. Update Place");
                        System.out.println(" 4. Change Security PIN");
                        System.out.println(" 5. Back to Dashboard");
                        System.out.print("Select field to update: ");
                        int fieldChoice = scanner.nextInt();
                        scanner.nextLine(); // Consume newline

                        if (fieldChoice == 1) {
                            System.out.print("Enter New Mobile Number (10 digits): ");
                            String newMobile = scanner.nextLine();
                            if (newMobile.length() == 10) {
                                loggedIn.mobile = newMobile;
                                System.out.println("[SUCCESS] Mobile number updated successfully!");
                            } else {
                                System.out.println("[ERROR] Invalid mobile number. It must be exactly 10 digits.");
                            }
                        } else if (fieldChoice == 2) {
                            System.out.print("Enter New Email Address: ");
                            String newEmail = scanner.nextLine();
                            if (newEmail.contains("@") && newEmail.contains(".")) {
                                loggedIn.email = newEmail;
                                System.out.println("[SUCCESS] Email address updated successfully!");
                            } else {
                                System.out.println("[ERROR] Invalid email format.");
                            }
                        } else if (fieldChoice == 3) {
                            System.out.print("Enter New Place/City: ");
                            String newPlace = scanner.nextLine();
                            if (newPlace.length() >= 2) {
                                loggedIn.place = newPlace;
                                System.out.println("[SUCCESS] Place/City updated successfully!");
                            } else {
                                System.out.println("[ERROR] Place name must be at least 2 characters.");
                            }
                        } else if (fieldChoice == 4) {
                            System.out.print("Enter Current Security PIN: ");
                            String currentPin = scanner.nextLine();
                            if (loggedIn.pinHash.equals(hashPin(currentPin))) {
                                System.out.print("Enter New 4-Digit Security PIN: ");
                                String newPin = scanner.nextLine();
                                if (newPin.length() == 4) {
                                    loggedIn.pinHash = hashPin(newPin);
                                    System.out.println("[SUCCESS] Security PIN changed successfully!");
                                } else {
                                    System.out.println("[ERROR] Invalid PIN length. It must be 4 digits.");
                                }
                            } else {
                                System.out.println("[ERROR] Verification failed. Incorrect PIN.");
                            }
                        } else {
                            System.out.println("Returning to Dashboard...");
                        }
                    } else if (userChoice == 6) {
                        // VIEW TRANSACTION HISTORY
                        System.out.println("\n==================================================");
                        System.out.println("               TRANSACTION HISTORY                ");
                        System.out.println("==================================================");
                        if (loggedIn.transactionCount == 0) {
                            System.out.println("No transactions recorded yet.");
                        } else {
                            for (int i = 0; i < loggedIn.transactionCount; i++) {
                                System.out.println((i + 1) + ". " + loggedIn.transactions[i]);
                            }
                        }
                        System.out.println("==================================================");
                    } else if (userChoice == 7) {
                        loggedInLoop = false;
                        System.out.println("Logged out successfully.");
                    } else {
                        System.out.println("[ERROR] Invalid choice!");
                    }
                }
            } else if (choice == 3) {
                // SEARCH ACCOUNT
                System.out.println("\n--- SEARCH ACCOUNT DETAILS ---");
                System.out.print("Enter Account Number: ");
                String accNum = scanner.nextLine();
                System.out.print("Enter 4-Digit Security PIN: ");
                String pin = scanner.nextLine();

                Account found = null;
                String hashedInput = hashPin(pin);
                for (int i = 0; i < accountCount; i++) {
                    if (accounts[i].accountNumber.equals(accNum) && accounts[i].pinHash.equals(hashedInput)) {
                        found = accounts[i];
                        break;
                    }
                }

                if (found != null) {
                    System.out.println("\n==================================================");
                    System.out.println("               ACCOUNT ENQUIRY RESULT             ");
                    System.out.println("==================================================");
                    System.out.println(" Name           : " + found.name);
                    System.out.println(" Mobile         : " + found.mobile);
                    System.out.println(" Email          : " + found.email);
                    System.out.println(" Place          : " + found.place);
                    System.out.println(" Account Number : " + found.accountNumber);
                    System.out.println(" Balance        : INR " + found.balance);
                    System.out.println(" IFSC Code      : " + found.ifsc);
                    System.out.println("==================================================");
                } else {
                    System.out.println("[ERROR] Invalid Account Number or PIN. Access Denied.");
                }
            } else if (choice == 4) {
                System.out.println("\nThank you for banking with Vision Bank of India. Goodbye!");
                break;
            } else {
                System.out.println("[ERROR] Invalid choice. Please select between 1 and 4.");
            }
        }
        scanner.close();
    }
}
