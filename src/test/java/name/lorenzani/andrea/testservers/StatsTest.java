package name.lorenzani.andrea.testservers;

import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.stream.LongStream;

import static org.junit.jupiter.api.Assertions.assertEquals;

class StatsTest {

    @Test
    void computesSummaryOfUnsortedTimes() {
        var stats = Stats.of("c", List.of(5L, 1L, 3L), 2);

        assertEquals(3, stats.sent());
        assertEquals(2, stats.successes());
        assertEquals(1, stats.failures());
        assertEquals(1, stats.min());
        assertEquals(5, stats.max());
        assertEquals(9, stats.total());
        assertEquals(3, stats.average());
    }

    @Test
    void computesPercentiles() {
        var times = LongStream.rangeClosed(1, 100).boxed().toList();

        var stats = Stats.of("c", times, 100);

        assertEquals(91, stats.p90());
        assertEquals(100, stats.p99());
    }

    @Test
    void emptyInputDoesNotDivideByZero() {
        var stats = Stats.of("c", List.of(), 0);

        assertEquals(0, stats.average());
        assertEquals(0, stats.p99());
    }
}
