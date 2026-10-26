package name.lorenzani.andrea.testservers;

import java.util.List;

/** Summary of the round-trip times (in milliseconds) measured for one client. */
public record Stats(String clientName, int sent, long successes, long failures,
                    long min, long max, long total, long p90, long p99) {

    public static Stats of(String clientName, List<Long> times, long successes) {
        if (times.isEmpty()) {
            return new Stats(clientName, 0, 0, 0, 0, 0, 0, 0, 0);
        }
        var sorted = times.stream().sorted().toList();
        long total = sorted.stream().mapToLong(Long::longValue).sum();
        return new Stats(clientName, sorted.size(), successes, sorted.size() - successes,
                sorted.getFirst(), sorted.getLast(), total,
                percentile(sorted, 90), percentile(sorted, 99));
    }

    static long percentile(List<Long> sorted, int percentile) {
        return sorted.get(Math.min(sorted.size() - 1, sorted.size() * percentile / 100));
    }

    public long average() {
        return sent == 0 ? 0 : total / sent;
    }

    @Override
    public String toString() {
        return """
                ===============================
                Client name: %s
                Msg sents: %d
                Successes: %d
                Failures: %d
                Minimum time spent: %d
                Maximum time spent: %d
                Total time spent: %d
                Average time spent: %d
                90-perc: %d
                99-perc: %d
                ===============================""".formatted(clientName, sent, successes, failures,
                min, max, total, average(), p90, p99);
    }
}
