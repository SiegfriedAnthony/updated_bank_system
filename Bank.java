import java.io.File;
import java.io.FileNotFoundException;
import java.io.PrintWriter;
import java.time.LocalDate;
import java.time.Period;
import java.util.ArrayList;
import java.util.InputMismatchException;
import java.util.Scanner;

/**
 * A class for representing a bank. A bank has a list of accounts, which can be
 * plain bank accounts, checking accounts, or CD accounts.
 */
public class Bank {
    /**
     * The list of accounts in this bank.
     */
    private ArrayList<BankAccount> accounts;

    /**
     * Creates the ArrayList. Then reads the file line by line, passing each line to the
     * processLine method for processing. If any exception occurs during the processing of a
     * line, prints the exception and moves on to the next line.
     * @param file The file from which to read the accounts
     */
    public Bank(File file) {
        accounts = new ArrayList<>();

        try (Scanner input = new Scanner(file)) {
            while (input.hasNext()) {
                try {
                    String account = input.nextLine();
                    processLine(account);
                } catch (Exception e) {
                    System.out.println(e);
                }
            }
        } catch (FileNotFoundException e) {
            System.out.println(e);
        }
    }



    /**
     * Processes one line of input. Divides the line up into its parts, creates an account of
     * the appropriate type containing the information from the line, and adds the account
     * to the list of accounts.
     * <p>
     * The line is expected to be made up of the following five (or six) comma-separated values:
     * <ul>
     *  <li>The type of account: "BankAccount" or "CDAccount" or "CheckingAccount"</li>
     *  <li>The account number</li>
     *  <li>The balance as a {@code double}</li>
     *  <li>The date the account was opened, in the form year-month-dayOfMonth (for example, 2020-08-23)</li>
     *  <li>The name of the account holder</li>
     * </ul>
     * At the end the line, if it's a CD account, there should be an integer representing
     * the length of the term in months; if it's a checking account there should instead be
     * the overdraft limit as a {@code double}.
     * <p>



        MonetaryValue balance = new MonetaryValue(Double.parseDouble(lines[2]));
        LocalDate dateOpened = LocalDate.parse(lines[3]);

        String[] names = lines[4].split(" ");
        Name name;
        if (names.length == 2) {
            String firstName = names[0];
            String lastName = names[1];
            name = new Name(firstName, lastName);
        } if (names.length == 3) {
            String firstName = names[0];
            String middleName = names[1];
            String lastName = names[2];
            name = new Name(firstName, middleName, lastName);
        } else {
            throw new InvalidNameException(lines[4]);
        }

        String typeAccount = lines[0];
        BankAccount account = null;

        switch(typeAccount) {
            case "BankAccount":
                account = new BankAccount(accountNumber, balance, dateOpened, name);
                break;

            case "CheckingAccount":
                MonetaryValue overDraftLimit = new MonetaryValue(Double.parseDouble(lines[5]));
                account = new CheckingAccount(accountNumber, balance, dateOpened, name, overDraftLimit);
                break;

            case "CDAccount":
                Period term = Period.ofMonths(Integer.parseInt(lines[5]));
                account = new CDAccount(accountNumber, balance, dateOpened, name, term);
        }
        accounts.add(account);
    }

    /**
     * Adds the specified account to the bank.
     * @param account The account to add
     * @throws DuplicateAccountNumberException If the bank already has an account with
     *         the same account number as the specified account
     */

    private void processLine(String line)
            throws DuplicateAccountNumberException, InvalidAccountNumberException,
                InvalidNameException, InputMismatchException {
            String[] lines = line.split(",");
            String accountNumber = lines[1];

            if (accounts.contains(accountNumber)) {
                throw new DuplicateAccountNumberException(accountNumber);
            }
        }


    public void addAccount(BankAccount account) throws DuplicateAccountNumberException {
        if (accounts.contains(account.getAccountNumber())) {
            throw new DuplicateAccountNumberException(account.getAccountNumber());
        } else {
            accounts.add(account);
        }
    }

    private BankAccount getAccount(String accountNumber) {
        for (BankAccount account : accounts) {
            if (account.getAccountNumber().equals(accountNumber)) {
                return account;
            }
        }
        return null;
    }

    /**
     * Deposits the specified amount of money into the account with the specified
     * account number. The method does this by calling the deposit method of BankAccount.
     * @param accountNumber The account number of the account we are interested in
     * @param amount The amount to deposit
     * @throws NegativeMonetaryValueException If the amount to deposit is negative
     * @throws NonexistentAccountException If there is no account in the bank with the
     *         specified account number
     */
    public void deposit(String accountNumber, MonetaryValue amount)
            throws NegativeMonetaryValueException, NonexistentAccountException {
        BankAccount account = getAccount(accountNumber);

        if (account == null) {
            throw new NonexistentAccountException(accountNumber);
        }

        account.deposit(amount);
    }


    /**
     * Withdraws the specified amount of money from the account with the specified
     * account number. The method does this by calling a withdraw method of BankAccount or
     * one of its subclasses.
     * @param accountNumber The account number of the account we are interested in
     * @param amount The amount to withdraw
     * @throws NegativeMonetaryValueException If the amount to withdraw is negative
     * @throws NonexistentAccountException If there is no account in the bank with the
     *         specified account number
     * @throws InsufficientFundsException If there are insufficient funds in the account (the
     *         precise meaning of this depends on whether the account is a checking account or
     *         some other type of account)
     * @throws WithdrawalDuringTermException If the account is a CD account and it is still
     *         during the term
     */
    public void withdraw(String accountNumber, MonetaryValue amount)
            throws NegativeMonetaryValueException, NonexistentAccountException,
            InsufficientFundsException, WithdrawalDuringTermException {
        BankAccount account = getAccount(accountNumber);

        if (account == null) {
            throw new NonexistentAccountException(accountNumber);
        }

        account.withdraw(amount);
    }



    /**
     * Returns the balance of the account with the specified account number.
     * @param accountNumber The account number of the account we are interested in
     * @return The account's balance
     * @throws NonexistentAccountException If there is no account in the bank with
     *         the specified account number
     */
    public MonetaryValue getBalance(String accountNumber) throws NonexistentAccountException {
        BankAccount account = getAccount(accountNumber);

        if (account == null) {
            throw new NonexistentAccountException(accountNumber);
        } else {
            return account.getBalance();
        }
    }


    /**
     * Returns a string representation of this bank. Each account is put on its own line.
     * @return A string representation of this bank
     */
    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        for (BankAccount account : accounts) {
            sb.append(account + "\n");
        }
        return sb.toString();
    }


    /**
     * Prints the bank to a file in comma-separated value format.
     * Each account is printed on its own line.
     * @param filename The name of the file to print to
     */
    public void printToFile(String filename) {
        try (PrintWriter pw = new PrintWriter(filename)) {
            pw.print(toString());
        } catch (FileNotFoundException e) {
            System.out.println(e);
        }
    }
}