package org.annill;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

import java.util.TreeMap;
import org.annill.model.CustomDate;
import org.annill.model.Transaction;
import org.annill.service.BalanceService;
import org.annill.util.FileUtils;
import org.annill.util.TransactionLogParser;


public class Main {

    private static final String PATH = "transactions_by_users/";

    public static void main(String[] args) throws IOException {
        if (Files.exists(Paths.get(PATH))) {
            return;
        }
        List<Path> files = FileUtils.searchFile("dir", ".log");
        Map<CustomDate, Transaction> transactions = new TreeMap<>();

        for (Path file : files) {
            Files.lines(file)
                .map(TransactionLogParser::parseLogLine)
                .forEach(t -> transactions.put(new CustomDate(t.timestamp()), t));
        }

        BalanceService balanceService = new BalanceService();
        transactions.values().forEach(balanceService::processTransaction);

        for (Transaction t : transactions.values()) {
            Path userFilePath = Path.of(PATH + t.user() + ".log");
            FileUtils.writeToFile(userFilePath, t.toString());
        }

        balanceService.getBalances().forEach((user, balance) -> {
            Path userFilePath = Path.of(PATH + user + ".log");
            String logLine = String.format(
                "[%s] %s withdrew %.2f",
                LocalDateTime.now().format(Transaction.LOG_FORMATTER),
                user,
                balance
            );
            try {
                File file = new File(String.valueOf(userFilePath));
                if(file.exists()){
                    Files.write(
                        userFilePath,
                        logLine.getBytes(),
                        StandardOpenOption.APPEND
                    );
                }

            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        });

    }


}
