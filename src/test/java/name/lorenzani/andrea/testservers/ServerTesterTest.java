package name.lorenzani.andrea.testservers;

import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.Random;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

class ServerTesterTest {

    private record FakeClient(boolean failEveryOther, int[] calls) implements IClient {
        @Override
        public boolean sendPost(String content) throws Exception {
            int n = calls[0]++;
            if (n == 0) {
                throw new Exception("boom");
            }
            return !failEveryOther || n % 2 == 0;
        }
    }

    @Test
    void countsExceptionsAndMismatchesAsFailures() {
        var client = new FakeClient(true, new int[1]);
        var tester = new ServerTester(List.of(client), new Random(1));

        var stats = tester.measure(client, List.of("a", "b", "c", "d"));

        assertEquals(4, stats.sent());
        assertEquals(1, stats.successes()); // call 0 throws, 1 mismatches, 2 passes, 3 mismatches
        assertEquals(3, stats.failures());
        assertEquals("FakeClient", stats.clientName());
    }

    @Test
    void randomMessagesHaveRequestedLengthAndAreAlphanumeric() {
        var tester = new ServerTester(List.of(), new Random(1));

        String message = tester.randomMessage(500);

        assertEquals(500, message.length());
        assertTrue(message.matches("[A-Za-z0-9]+"));
    }

    @Test
    void truncatesOversizedMessages() {
        String huge = "x".repeat(IClient.MAX_MESSAGE_LENGTH + 10);

        assertEquals(IClient.MAX_MESSAGE_LENGTH, IClient.truncate(huge).length());
        assertEquals("short", IClient.truncate("short"));
    }
}
