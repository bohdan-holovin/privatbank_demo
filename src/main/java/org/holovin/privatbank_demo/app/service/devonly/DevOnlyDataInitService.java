package org.holovin.privatbank_demo.app.service.devonly;

import lombok.RequiredArgsConstructor;
import org.holovin.privatbank_demo.app.service.UserService;
import org.holovin.privatbank_demo.domain.model.*;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.IntStream;

@Service
@RequiredArgsConstructor
public class DevOnlyDataInitService {

    private static final long MIN_ACCOUNT_NUMBER = 1000000000000000L;
    private static final int MAX_TRANSACTION_AMOUNT = 10000;
    private static final int MIN_TRANSACTION_AMOUNT = 100;
    private static final int MIN_ACCOUNT_BALANCE = 1000;
    private static final int MAX_ACCOUNT_BALANCE = 100000;
    private static final int MAX_DAILY_CHANGE = 1000;
    private static final int MAX_DAILY_TRANSACTIONS = 20;
    private static final int MAX_PROCESSING_MINUTES = 60;
    private static final int PHONE_NUMBER_RANGE = 1000000000;
    private static final int BALANCE_HISTORY_DAYS = 30;
    private static final String PHONE_PREFIX = "+380";
    private static final String EMAIL_TEMPLATE = "user%d@test.com";
    private static final String ACCOUNT_NUMBER_TEMPLATE = "%016d";

    private static final List<String> FIRST_NAMES = Arrays.asList(
            "Олександр", "Марія", "Андрій", "Олена", "Дмитро", "Наталія",
            "Володимир", "Світлана", "Сергій", "Тетяна", "Віталій", "Ірина",
            "Максим", "Юлія", "Роман", "Катерина"
    );
    private static final List<String> LAST_NAMES = Arrays.asList(
            "Петренко", "Іваненко", "Коваленко", "Шевченко", "Бондаренко",
            "Мельниченко", "Кравченко", "Ткаченко", "Савченко", "Поліщук",
            "Гриценко", "Лисенко"
    );
    private final UserService userService;
    private final Random random = new Random();

    @Transactional
    public void populateTestData(int userCount, int accountsPerUser, int transactionsPerAccount) {
        if (userCount < 0 || accountsPerUser < 0 || transactionsPerAccount < 0) {
            throw new IllegalArgumentException("All parameters must be non-negative");
        }

        var users = IntStream.range(0, userCount)
                .mapToObj(this::createUserWithAccounts)
                .peek(user -> createAccountsForUser(user, accountsPerUser, transactionsPerAccount))
                .toList();

        userService.saveAll(users);
    }

    private User createUserWithAccounts(int index) {
        var user = createRandomUser(index);
        user.setAccounts(new ArrayList<>());
        return user;
    }

    private void createAccountsForUser(User user, int accountsPerUser, int transactionsPerAccount) {
        var accounts = IntStream.range(0, accountsPerUser)
                .mapToObj(accountIndex -> createAccountWithTransactions(user, accountIndex, transactionsPerAccount))
                .toList();

        user.setAccounts(accounts);
    }

    private Account createAccountWithTransactions(User user, int accountIndex, int transactionsPerAccount) {
        var account = createRandomAccount(user, accountIndex);

        var transactions = createRandomTransactions(account, transactionsPerAccount);
        account.setOutgoingTransactions(transactions);

        var dayBalances = createRandomDayBalances(account, BALANCE_HISTORY_DAYS);
        account.setBalances(dayBalances);

        var currentBalance = createCurrentBalance(account);
        account.setCurrentBalance(currentBalance);

        return account;
    }

    private User createRandomUser(int index) {
        var user = new User();
        user.setFirstName(getRandomElement(FIRST_NAMES));
        user.setLastName(getRandomElement(LAST_NAMES));
        user.setEmail(String.format(EMAIL_TEMPLATE, index + 1));
        user.setPhone(generateRandomPhoneNumber());
        return user;
    }

    private Account createRandomAccount(User user, int index) {
        var account = new Account();
        account.setNumber(generateAccountNumber(index));
        account.setStatus(getRandomAccountStatus());
        account.setUser(user);

        if (account.getStatus() == Account.Status.CLOSED) {
            account.setClosingDate(generateRandomPastDate(365));
        }

        return account;
    }

    private List<Transaction> createRandomTransactions(Account account, int count) {
        return IntStream.range(0, count)
                .mapToObj(i -> createRandomTransaction(account, i))
                .toList();
    }

    private Transaction createRandomTransaction(Account account, int index) {
        var transaction = new Transaction();
        transaction.setUuid(UUID.randomUUID().toString());
        transaction.setFromAccount(account);
        transaction.setAmount(generateRandomAmount(MIN_TRANSACTION_AMOUNT, MAX_TRANSACTION_AMOUNT));

        var status = getRandomTransactionStatus();
        transaction.setStatus(Transaction.Status.COMPLETED);

        var transactionDate = generateRandomPastDateTime(30);
        transaction.setTransactionDate(transactionDate);

        if ("completed".equals(status)) {
            transaction.setProcessedAt(transactionDate.plusMinutes(
                    random.nextInt(MAX_PROCESSING_MINUTES)));
        }

        return transaction;
    }

    private List<AccountDayBalance> createRandomDayBalances(Account account, int days) {
        var balances = new ArrayList<AccountDayBalance>(days);
        var runningBalance = generateRandomAmount(MIN_ACCOUNT_BALANCE, MAX_ACCOUNT_BALANCE);

        for (int i = days - 1; i >= 0; i--) {
            var dayBalance = createDayBalance(account, i, runningBalance);

            var dailyChange = generateRandomDailyChange();
            runningBalance = runningBalance.add(dailyChange);
            dayBalance.setClosingBalance(runningBalance);

            balances.add(dayBalance);
        }

        return balances;
    }

    private AccountDayBalance createDayBalance(Account account, int daysAgo, BigDecimal openingBalance) {
        var dayBalance = new AccountDayBalance();
        dayBalance.setAccount(account);
        dayBalance.setBalanceDate(LocalDate.now().minusDays(daysAgo));
        dayBalance.setOpeningBalance(openingBalance);
        dayBalance.setTotalDebits(generateRandomAmount(0, 5000));
        dayBalance.setTotalCredits(generateRandomAmount(0, 5000));
        dayBalance.setTransactionCount(random.nextInt(MAX_DAILY_TRANSACTIONS));
        return dayBalance;
    }

    private AccountCurrentBalance createCurrentBalance(Account account) {
        var currentBalance = new AccountCurrentBalance();
        currentBalance.setAccount(account);
        currentBalance.setAvailableBalance(generateRandomAmount(MIN_ACCOUNT_BALANCE, MAX_ACCOUNT_BALANCE));
        currentBalance.setModifiedDate(LocalDateTime.now());
        return currentBalance;
    }

    private <T> T getRandomElement(List<T> list) {
        return list.get(random.nextInt(list.size()));
    }

    private String generateRandomPhoneNumber() {
        return String.format("%s%09d", PHONE_PREFIX, random.nextInt(PHONE_NUMBER_RANGE));
    }

    private String generateAccountNumber(int index) {
        long baseNumber = Math.abs(random.nextLong()) % MIN_ACCOUNT_NUMBER;
        return String.format(ACCOUNT_NUMBER_TEMPLATE, baseNumber + index);
    }

    private Account.Status getRandomAccountStatus() {
        return random.nextBoolean() ? Account.Status.ACTIVE : Account.Status.CLOSED;
    }

    private String getRandomTransactionStatus() {
        return random.nextBoolean() ? "completed" : "pending";
    }

    private LocalDate generateRandomPastDate(int maxDaysAgo) {
        return LocalDate.now().minusDays(random.nextInt(maxDaysAgo));
    }

    private LocalDateTime generateRandomPastDateTime(int maxDaysAgo) {
        return LocalDateTime.now().minusDays(random.nextInt(maxDaysAgo));
    }

    private BigDecimal generateRandomAmount(int min, int max) {
        double amount = random.nextDouble() * (max - min) + min;
        return BigDecimal.valueOf(amount).setScale(2, RoundingMode.HALF_UP);
    }

    private BigDecimal generateRandomDailyChange() {
        double change = (random.nextDouble() - 0.5) * MAX_DAILY_CHANGE;
        return BigDecimal.valueOf(change).setScale(2, RoundingMode.HALF_UP);
    }
}
