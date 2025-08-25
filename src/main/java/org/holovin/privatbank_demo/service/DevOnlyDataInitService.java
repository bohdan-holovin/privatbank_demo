package org.holovin.privatbank_demo.service;

import lombok.RequiredArgsConstructor;
import org.holovin.privatbank_demo.entity.*;
import org.holovin.privatbank_demo.repository.UserRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class DevOnlyDataInitService {

    private final UserRepository userRepository;
    private final Random random = new Random();

    private final String[] FIRST_NAMES = {
            "Олександр", "Марія", "Андрій", "Олена", "Дмитро", "Наталія", "Володимир", "Світлана",
            "Сергій", "Тетяна", "Віталій", "Ірина", "Максим", "Юлія", "Роман", "Катерина"
    };

    private final String[] LAST_NAMES = {
            "Петренко", "Іваненко", "Коваленко", "Шевченко", "Бондаренко", "Мельниченко",
            "Кравченко", "Ткаченко", "Савченко", "Поліщук", "Гриценко", "Лисенко"
    };

    @Transactional
    public int populateTestData(int userCount, int accountsPerUser, int transactionsPerAccount) {

        var users = new ArrayList<User>();

        for (int i = 0; i < userCount; i++) {
            var user = createRandomUser(i);

            var accounts = new ArrayList<Account>();
            for (int j = 0; j < accountsPerUser; j++) {
                var account = createRandomAccount(user, j);
                accounts.add(account);

                var transactions = createRandomTransactions(account, transactionsPerAccount);
                account.setOutgoingTransactions(transactions);

                var dayBalances = createRandomDayBalances(account, 30);
                account.setBalances(dayBalances);

                var currentBalance = createCurrentBalance(account);
                account.setAccountCurrentBalance(currentBalance);
            }

            user.setAccounts(accounts);
            users.add(user);
        }

        userRepository.saveAll(users);
        return users.size();
    }

    private User createRandomUser(int index) {
        var user = new User();
        user.setFirstName(FIRST_NAMES[random.nextInt(FIRST_NAMES.length)]);
        user.setLastName(LAST_NAMES[random.nextInt(LAST_NAMES.length)]);
        user.setEmail(String.format("user%d@test.com", index + 1));
        user.setPhone(String.format("+380%09d", random.nextInt(1000000000)));
        return user;
    }

    private Account createRandomAccount(User user, int index) {
        var account = new Account();
        account.setNumber(String.format("%016d", random.nextLong(1000000000000000L) + index));
        account.setStatus(random.nextBoolean() ? Account.Status.ACTIVE : Account.Status.CLOSED);
        account.setUser(user);

        if (account.getStatus() == Account.Status.CLOSED) {
            account.setClosingDate(LocalDate.now().minusDays(random.nextInt(365)));
        }

        return account;
    }

    private List<Transaction> createRandomTransactions(Account account, int count) {
        var transactions = new ArrayList<Transaction>();

        for (int i = 0; i < count; i++) {
            Transaction transaction = new Transaction();
            transaction.setUuid(UUID.randomUUID().toString());
            transaction.setFromAccount(account);
            transaction.setAmount(BigDecimal.valueOf(random.nextDouble() * 10000 + 100).setScale(2, RoundingMode.HALF_UP));
            transaction.setStatus(random.nextBoolean() ? "completed" : "pending");
            transaction.setTransactionDate(LocalDateTime.now().minusDays(random.nextInt(30)));
            transaction.setDescription("Тестова транзакція №" + (i + 1));

            if (transaction.getStatus().equals("completed")) {
                transaction.setProcessedAt(transaction.getTransactionDate().plusMinutes(random.nextInt(60)));
            }

            transactions.add(transaction);
        }

        return transactions;
    }

    private List<AccountDayBalance> createRandomDayBalances(Account account, int days) {
        var balances = new ArrayList<AccountDayBalance>();
        var currentBalance = BigDecimal.valueOf(random.nextDouble() * 100000 + 1000);

        for (int i = days - 1; i >= 0; i--) {
            AccountDayBalance dayBalance = new AccountDayBalance();
            dayBalance.setAccount(account);
            dayBalance.setBalanceDate(LocalDate.now().minusDays(i));
            dayBalance.setOpeningBalance(currentBalance);

            BigDecimal dailyChange = BigDecimal.valueOf((random.nextDouble() - 0.5) * 1000);
            currentBalance = currentBalance.add(dailyChange);

            dayBalance.setClosingBalance(currentBalance);
            dayBalance.setTotalDebits(BigDecimal.valueOf(random.nextDouble() * 5000).setScale(2, RoundingMode.HALF_UP));
            dayBalance.setTotalCredits(BigDecimal.valueOf(random.nextDouble() * 5000).setScale(2, RoundingMode.HALF_UP));
            dayBalance.setTransactionCount(random.nextInt(20));

            balances.add(dayBalance);
        }

        return balances;
    }

    private AccountCurrentBalance createCurrentBalance(Account account) {
        var currentBalance = new AccountCurrentBalance();
        currentBalance.setAccount(account);
        currentBalance.setAvailableBalance(BigDecimal.valueOf(random.nextDouble() * 100000 + 1000).setScale(2, RoundingMode.HALF_UP));
        currentBalance.setModifiedDate(LocalDateTime.now());
        return currentBalance;
    }
}
