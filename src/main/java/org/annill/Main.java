package org.annill;

import static org.annill.util.FileUtils.deleteAllLogFiles;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;
import java.util.logging.Logger;
import org.annill.model.CustomDate;
import org.annill.model.Transaction;
import org.annill.service.BalanceService;
import org.annill.util.DateFormatUtils;
import org.annill.util.FileUtils;
import org.annill.util.TransactionLogParser;

public class Main {

    private static final String OUTPUT_SUBDIR = "transactions_by_users";
    private static final Logger logger = Logger.getLogger(Main.class.getName());

    public static void main(String[] args) {
        try {
            validateArguments(args);
            Path outputDir = Paths.get(args[1], OUTPUT_SUBDIR);
            prepareOutputDirectory(args[1]);

            List<Path> logFiles = FileUtils.searchFile(args[0], ".log");
            if (logFiles.isEmpty()) {
                throw new FileNotFoundException("Файлов не найдено");
            }

            deleteAllLogFiles(outputDir);

            Map<CustomDate, Transaction> transactions = parseTransactions(logFiles);
            logger.info("Парсили транзакции");
            BalanceService balanceService = processTransactions(transactions);
            logger.info("Обработали транзакции");

            Files.createDirectories(outputDir);
            writeUserTransactionFiles(outputDir, transactions);
            logger.info("Распределили транзакции по файлам");
            writeFinalBalances(outputDir, balanceService);
            logger.info("Добавили окончательный баланс");
            logger.info("Вычисления выполнены успешно");

        } catch (FileNotFoundException e) {
            logger.warning("Файл не найден" + e.getMessage());
        } catch (IOException e) {
            logger.warning("Ошибка ввода-вывода: " + e.getMessage());
        }
    }

    private static void validateArguments(String[] args) {
        if (args.length != 2) {
            throw new IllegalArgumentException(
                """
                    Необходимо указать два аргумента:
                    1. Путь где искать файлы
                    2. Путь куда сохранять результаты"""
            );
        }
    }

    private static void prepareOutputDirectory(String baseOutputPath) throws IOException {

        if (!Files.exists(Paths.get(baseOutputPath))) {
            throw new IllegalArgumentException("Указанный путь не существует: " + baseOutputPath);
        }

    }

    private static Map<CustomDate, Transaction> parseTransactions(List<Path> logFiles) throws IOException {
        Map<CustomDate, Transaction> transactions = new TreeMap<>();

        for (Path file : logFiles) {
            Files.lines(file)
                .map(TransactionLogParser::parseLogLine)
                .forEach(t -> transactions.put(new CustomDate(t.timestamp()), t));
        }

        return transactions;
    }

    private static BalanceService processTransactions(Map<CustomDate, Transaction> transactions) {
        BalanceService balanceService = new BalanceService();
        transactions.values().forEach(balanceService::processTransaction);
        return balanceService;
    }

    private static void writeUserTransactionFiles(Path outputDir, Map<CustomDate, Transaction> transactions)
        throws IOException {

        for (Transaction t : transactions.values()) {
            Path userFilePath = outputDir.resolve(t.user() + ".log");
            FileUtils.writeToFile(userFilePath, t.toString());
        }
    }

    private static void writeFinalBalances(Path outputDir, BalanceService balanceService) throws IOException {
        String timestamp = DateFormatUtils.getFormattedTimestamp(LocalDateTime.now());

        balanceService.getBalances().forEach((user, balance) -> {
            try {
                Path userFilePath = outputDir.resolve(user + ".log");
                String logLine = String.format("[%s] %s final balance %f %n", timestamp, user, balance);

                if (Files.exists(userFilePath)) {

                    Files.write(
                        userFilePath,
                        logLine.getBytes(),
                        StandardOpenOption.APPEND
                    );
                }
            } catch (IOException e) {
                throw new RuntimeException("Не удалось записать баланс для пользователя " + user, e);
            }
        });
    }
}