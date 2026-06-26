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

    // Loan attributes
    String loanType = "None"; // None, House, Education, Foreign/Visa
    double loanAmount = 0.0;
    double loanInterestRate = 0.0;
    String loanStatus = "NONE"; // NONE, PENDING, APPROVED, REJECTED

    // Gold Storage
    double goldStored = 0.0; // in grams

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

class Manager {
    String username;
    String pinHash;

    public Manager(String username, String pinHash) {
        this.username = username;
        this.pinHash = pinHash;
    }
}

public class Main {
    static Account[] accounts = new Account[100];
    static int accountCount = 0;
    static int nextAccountNumber = 1001;

    // Banker Managers array
    static Manager[] managers = new Manager[100];
    static int managerCount = 0;

    // Manager/Admin Global Settings
    static double globalInterestRate = 10.0; // Default 10%

    // Vault and Cash Balance Calculators
    public static double getTotalGoldStored() {
        double total = 0;
        for (int i = 0; i < accountCount; i++) {
            total += accounts[i].goldStored;
        }
        return total;
    }

    public static double getTotalCashBalance() {
        double total = 0;
        for (int i = 0; i < accountCount; i++) {
            total += accounts[i].balance;
        }
        return total;
    }

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

    public static void managerSpace(Scanner scanner) {
        System.out.print("\nEnter Manager Username: ");
        String username = scanner.nextLine().trim();
        System.out.print("Enter Manager PIN: ");
        String pin = scanner.nextLine();

        Manager loggedInManager = null;
        String hashedInput = hashPin(pin);
        for (int i = 0; i < managerCount; i++) {
            if (managers[i].username.equalsIgnoreCase(username) && managers[i].pinHash.equals(hashedInput)) {
                loggedInManager = managers[i];
                break;
            }
        }

        if (loggedInManager == null) {
            System.out.println("[ERROR] Invalid Manager Credentials!");
            return;
        }

        System.out.println("\n[SUCCESS] Manager Login successful! Welcome, " + loggedInManager.username);
        boolean managerLoop = true;
        while (managerLoop) {
            System.out.println("\n==================================================");
            System.out.println("            BANK MANAGER DASHBOARD                ");
            System.out.println("==================================================");
            System.out.println(" 1. View All Client Details");
            System.out.println(" 2. Manage Loan Applications");
            System.out.println(" 3. Change Interest Rate (Current: " + globalInterestRate + "%)");
            System.out.println(" 4. Manage Money & Gold Vault");
            System.out.println(" 5. View System-wide Transaction Logs");
            System.out.println(" 6. Logout");
            System.out.println("==================================================");
            System.out.print("Please enter choice (1-6): ");

            int choice = scanner.nextInt();
            scanner.nextLine(); // Consume newline

            if (choice == 1) {
                // View All Client Details
                System.out.println("\n--- CLIENT DETAILS LIST ---");
                if (accountCount == 0) {
                    System.out.println("No accounts registered in the system.");
                } else {
                    for (int i = 0; i < accountCount; i++) {
                        Account acc = accounts[i];
                        System.out.println("--------------------------------------------------");
                        System.out.println("Name: " + acc.name + " | Acc No: " + acc.accountNumber);
                        System.out.println("Mobile: " + acc.mobile + " | Email: " + acc.email + " | Place: " + acc.place);
                        System.out.println("Balance: INR " + acc.balance + " | Gold Stored: " + acc.goldStored + "g");
                        System.out.println("IFSC: " + acc.ifsc);
                        System.out.println("Loan Status: " + acc.loanStatus + " | Loan Type: " + acc.loanType + " | Amount: INR " + acc.loanAmount);
                    }
                    System.out.println("--------------------------------------------------");
                }
            } else if (choice == 2) {
                // Manage Loan Applications
                System.out.println("\n--- PENDING LOAN APPLICATIONS ---");
                java.util.List<Account> pendingLoans = new java.util.ArrayList<>();
                for (int i = 0; i < accountCount; i++) {
                    if ("PENDING".equals(accounts[i].loanStatus)) {
                        pendingLoans.add(accounts[i]);
                    }
                }

                if (pendingLoans.isEmpty()) {
                    System.out.println("No pending loan applications.");
                } else {
                    for (int i = 0; i < pendingLoans.size(); i++) {
                        Account acc = pendingLoans.get(i);
                        System.out.println((i + 1) + ". Name: " + acc.name + " (Acc: " + acc.accountNumber + ") | " + acc.loanType + " Loan | Amount: INR " + acc.loanAmount + " | Interest Rate: " + acc.loanInterestRate + "%");
                    }
                    System.out.print("Select loan to manage (1-" + pendingLoans.size() + ") or 0 to go back: ");
                    int select = scanner.nextInt();
                    scanner.nextLine();
                    if (select > 0 && select <= pendingLoans.size()) {
                        Account targetAcc = pendingLoans.get(select - 1);
                        System.out.print("Approve or Reject? (1. Approve, 2. Reject): ");
                        int decision = scanner.nextInt();
                        scanner.nextLine();
                        if (decision == 1) {
                            targetAcc.loanStatus = "APPROVED";
                            // Add loan amount directly to user's balance
                            targetAcc.balance += targetAcc.loanAmount;
                            targetAcc.addTransaction("LOAN APPROVED: INR " + targetAcc.loanAmount + " (" + targetAcc.loanType + " Loan at " + targetAcc.loanInterestRate + "% interest)");
                            System.out.println("[SUCCESS] Loan application approved. Funds deposited into account.");
                        } else if (decision == 2) {
                            targetAcc.loanStatus = "REJECTED";
                            targetAcc.addTransaction("LOAN REJECTED: " + targetAcc.loanType + " Loan request of INR " + targetAcc.loanAmount);
                            System.out.println("[SUCCESS] Loan application rejected.");
                        } else {
                            System.out.println("[ERROR] Invalid choice.");
                        }
                    }
                }
            } else if (choice == 3) {
                // Change Interest Rate
                System.out.print("\nEnter new global interest rate (in %): ");
                double rate = scanner.nextDouble();
                scanner.nextLine();
                if (rate >= 0) {
                    globalInterestRate = rate;
                    System.out.println("[SUCCESS] Global interest rate set to " + globalInterestRate + "%.");
                } else {
                    System.out.println("[ERROR] Interest rate cannot be negative.");
                }
            } else if (choice == 4) {
                // Manage Money & Gold Vault
                System.out.println("\n--- BANK MONEY & GOLD VAULT SUMMARY ---");
                System.out.println("Total Bank Cash Deposits  : INR " + getTotalCashBalance());
                System.out.println("Total Gold Storage Volume : " + getTotalGoldStored() + " grams");
                System.out.println("Total Registered Clients  : " + accountCount);
            } else if (choice == 5) {
                // View System-wide Transaction Logs
                System.out.println("\n--- SYSTEM-WIDE AUDIT TRANSACTION LOGS ---");
                boolean foundLogs = false;
                for (int i = 0; i < accountCount; i++) {
                    Account acc = accounts[i];
                    if (acc.transactionCount > 0) {
                        System.out.println("\nAccount Number: " + acc.accountNumber + " (" + acc.name + ")");
                        for (int j = 0; j < acc.transactionCount; j++) {
                            System.out.println("  - " + acc.transactions[j]);
                        }
                        foundLogs = true;
                    }
                }
                if (!foundLogs) {
                    System.out.println("No transactions recorded across any accounts.");
                }
            } else if (choice == 6) {
                managerLoop = false;
                System.out.println("Logged out from Manager Space.");
            } else {
                System.out.println("[ERROR] Invalid choice!");
            }
        }
    }

    public static void main(String[] args) {
        // Seed initial Bank Manager account
        managers[managerCount++] = new Manager("admin", hashPin("9999"));

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
            System.out.println(" 4. Bank Manager Login");
            System.out.println(" 5. Bank Manager Signup");
            System.out.println(" 6. Exit");
            System.out.println("==================================================");
            System.out.print("Please enter your choice (1-6): ");

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
                    System.out.println(" 7. Apply / View Loans");
                    System.out.println(" 8. Gold Storage Vault");
                    System.out.println(" 9. Logout");
                    System.out.println("==================================================");
                    System.out.print("Please enter choice (1-9): ");

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
                        System.out.println(" Gold Stored    : " + loggedIn.goldStored + " grams");
                        if (!loggedIn.loanType.equalsIgnoreCase("None")) {
                            System.out.println(" Loan Type      : " + loggedIn.loanType);
                            System.out.println(" Loan Amount    : INR " + loggedIn.loanAmount);
                            System.out.println(" Interest Rate  : " + loggedIn.loanInterestRate + "%");
                            System.out.println(" Loan Status    : " + loggedIn.loanStatus);
                        } else {
                            System.out.println(" Active Loan    : None");
                        }
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
                        // LOAN SERVICE
                        System.out.println("\n--- LOAN SERVICES ---");
                        if ("NONE".equals(loggedIn.loanStatus) || "REJECTED".equals(loggedIn.loanStatus)) {
                            if ("REJECTED".equals(loggedIn.loanStatus)) {
                                System.out.println("[NOTE] Your previous loan request was REJECTED. You may re-apply.");
                            }
                            System.out.println("Available Loan Types:");
                            System.out.println("1. House Loan (Interest Rate: " + globalInterestRate + "%)");
                            System.out.println("2. Education Loan (Interest Rate: " + globalInterestRate + "%)");
                            System.out.println("3. Foreign / Visa Loan (Interest Rate: " + globalInterestRate + "%)");
                            System.out.print("Select loan type (1-3) or 0 to cancel: ");
                            int loanOpt = scanner.nextInt();
                            scanner.nextLine();
                            if (loanOpt >= 1 && loanOpt <= 3) {
                                String type = "";
                                if (loanOpt == 1) type = "House";
                                else if (loanOpt == 2) type = "Education";
                                else if (loanOpt == 3) type = "Foreign/Visa";

                                System.out.print("Enter required loan amount (INR): ");
                                double amount = scanner.nextDouble();
                                scanner.nextLine();
                                if (amount > 0) {
                                    loggedIn.loanType = type;
                                    loggedIn.loanAmount = amount;
                                    loggedIn.loanInterestRate = globalInterestRate;
                                    loggedIn.loanStatus = "PENDING";
                                    loggedIn.addTransaction("LOAN APPLIED: Requested " + type + " Loan of INR " + amount + " at " + globalInterestRate + "% interest");
                                    System.out.println("[SUCCESS] Loan application submitted successfully! Pending Bank Manager's approval.");
                                } else {
                                    System.out.println("[ERROR] Invalid loan amount.");
                                }
                            }
                        } else if ("PENDING".equals(loggedIn.loanStatus)) {
                            System.out.println("You have a PENDING loan application:");
                            System.out.println("Loan Type    : " + loggedIn.loanType);
                            System.out.println("Requested Amt: INR " + loggedIn.loanAmount);
                            System.out.println("Interest Rate: " + loggedIn.loanInterestRate + "%");
                            System.out.println("Status       : PENDING (Awaiting manager approval)");
                        } else if ("APPROVED".equals(loggedIn.loanStatus)) {
                            System.out.println("You have an ACTIVE loan:");
                            System.out.println("Loan Type    : " + loggedIn.loanType);
                            System.out.println("Loan Amount  : INR " + loggedIn.loanAmount);
                            System.out.println("Interest Rate: " + loggedIn.loanInterestRate + "%");
                            System.out.println("Status       : APPROVED (Funds credited to account)");
                        }
                    } else if (userChoice == 8) {
                        // GOLD STORAGE VAULT
                        System.out.println("\n--- GOLD STORAGE VAULT ---");
                        System.out.println("Your Stored Gold: " + loggedIn.goldStored + " grams");
                        System.out.println("1. Deposit Gold");
                        System.out.println("2. Withdraw Gold");
                        System.out.println("3. Back to Dashboard");
                        System.out.print("Select choice (1-3): ");
                        int goldOpt = scanner.nextInt();
                        scanner.nextLine();
                        if (goldOpt == 1) {
                            System.out.print("Enter gold weight to deposit (in grams): ");
                            double weight = scanner.nextDouble();
                            scanner.nextLine();
                            if (weight > 0) {
                                loggedIn.goldStored += weight;
                                loggedIn.addTransaction("GOLD DEPOSITED: " + weight + " grams stored");
                                System.out.println("[SUCCESS] " + weight + " grams of gold deposited in your vault storage.");
                            } else {
                                System.out.println("[ERROR] Invalid weight.");
                            }
                        } else if (goldOpt == 2) {
                            System.out.print("Enter gold weight to withdraw (in grams): ");
                            double weight = scanner.nextDouble();
                            scanner.nextLine();
                            if (weight > 0) {
                                if (loggedIn.goldStored >= weight) {
                                    loggedIn.goldStored -= weight;
                                    loggedIn.addTransaction("GOLD WITHDRAWN: " + weight + " grams retrieved");
                                    System.out.println("[SUCCESS] " + weight + " grams of gold withdrawn from your vault storage.");
                                } else {
                                    System.out.println("[ERROR] Insufficient gold balance stored.");
                                }
                            } else {
                                System.out.println("[ERROR] Invalid weight.");
                            }
                        }
                    } else if (userChoice == 9) {
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
                    System.out.println(" Gold Stored    : " + found.goldStored + " grams");
                    if (!found.loanType.equalsIgnoreCase("None")) {
                        System.out.println(" Loan Type      : " + found.loanType);
                        System.out.println(" Loan Amount    : INR " + found.loanAmount);
                        System.out.println(" Interest Rate  : " + found.loanInterestRate + "%");
                        System.out.println(" Loan Status    : " + found.loanStatus);
                    } else {
                        System.out.println(" Active Loan    : None");
                    }
                    System.out.println("==================================================");
                } else {
                    System.out.println("[ERROR] Invalid Account Number or PIN. Access Denied.");
                }
            } else if (choice == 4) {
                managerSpace(scanner);
            } else if (choice == 5) {
                // MANAGER SIGNUP
                System.out.println("\n--- BANK MANAGER SIGNUP ---");
                System.out.print("Enter Username: ");
                String username = scanner.nextLine().trim();
                if (username.isEmpty()) {
                    System.out.println("[ERROR] Username cannot be empty!");
                    continue;
                }
                // Check if username already exists
                boolean exists = false;
                for (int i = 0; i < managerCount; i++) {
                    if (managers[i].username.equalsIgnoreCase(username)) {
                        exists = true;
                        break;
                    }
                }
                if (exists) {
                    System.out.println("[ERROR] Username already exists!");
                    continue;
                }
                System.out.print("Create a 4-Digit Security PIN: ");
                String pin = scanner.nextLine();
                if (pin.length() != 4) {
                    System.out.println("[ERROR] Security PIN must be exactly 4 digits!");
                    continue;
                }
                managers[managerCount++] = new Manager(username, hashPin(pin));
                System.out.println("[SUCCESS] Bank Manager registered successfully!");
            } else if (choice == 6) {
                System.out.println("\nThank you for banking with Vision Bank of India. Goodbye!");
                break;
            } else {
                System.out.println("[ERROR] Invalid choice. Please select between 1 and 6.");
            }
        }
        scanner.close();
    }
}
