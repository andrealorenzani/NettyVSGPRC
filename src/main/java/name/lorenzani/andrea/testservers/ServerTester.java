package name.lorenzani.andrea.testservers;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.security.SecureRandom;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.stream.Stream;

/** Runs the same series of echo round trips against every client and prints the timings. */
public class ServerTester {

    private static final int WARMUP_MESSAGES = 100;
    private static final String ALPHANUMERIC =
            "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789";

    private final List<IClient> clients;
    private final Random random;

    public ServerTester(List<IClient> clients) {
        this(clients, new SecureRandom());
    }

    public ServerTester(List<IClient> clients, Random random) {
        this.clients = clients;
        this.random = random;
    }

    /** Sends every message to the client and measures how long each round trip takes. */
    public Stats measure(IClient client, List<String> messages) {
        var times = new ArrayList<Long>(messages.size());
        long successes = 0;
        for (String message : messages) {
            boolean ok;
            long start = System.nanoTime();
            try {
                ok = client.sendPost(message);
            } catch (Exception e) {
                ok = false;
            }
            times.add((System.nanoTime() - start) / 1_000_000);
            if (ok) {
                successes++;
            }
        }
        return Stats.of(client.getName(), times, successes);
    }

    public String randomMessage(int length) {
        var sb = new StringBuilder(length);
        for (int i = 0; i < length; i++) {
            sb.append(ALPHANUMERIC.charAt(random.nextInt(ALPHANUMERIC.length())));
        }
        return sb.toString();
    }

    private void warmup(IClient client) {
        measure(client, Stream.generate(() -> randomMessage(20)).limit(WARMUP_MESSAGES).toList());
    }

    private void run(List<String> messages) {
        for (IClient client : clients) {
            warmup(client);
            System.out.println("*** " + client.getName() + " ***");
            System.out.println(measure(client, messages));
        }
    }

    private void runWithDimensions(int length, int count) {
        System.out.printf("------------------------------------%n"
                + "Starting with %d messages of %d characters%n"
                + "------------------------------------%n", count, length);
        run(Stream.generate(() -> randomMessage(length)).limit(count).toList());
    }

    private void runWithFile(Path file, int count) throws IOException {
        String message = IClient.truncate(String.join("", Files.readAllLines(file)));
        System.out.printf("------------------------------------%n"
                + "Send %d times %s (size %d)%n"
                + "------------------------------------%n", count, file, message.length());
        run(Stream.generate(() -> message).limit(count).toList());
    }

    public void startSuite(Path bibleFile) throws IOException {
        runWithDimensions(100, 2500);
        runWithDimensions(100, 25000);
        runWithDimensions(5000, 2500);
        runWithDimensions(5000, 50000);
        runWithDimensions(100000, 100);
        if (Files.exists(bibleFile)) {
            runWithFile(bibleFile, 10);
        } else {
            System.out.println("Skipping the bible test: " + bibleFile + " not found");
        }
    }
}
