package name.lorenzani.andrea.testservers.grpc;

import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertTrue;

class GRPCEchoTest {

    private static GRPCServer server;
    private static GRPCClient client;

    @BeforeAll
    static void startServer() throws Exception {
        server = new GRPCServer();
        client = new GRPCClient("localhost", server.start(0));
    }

    @AfterAll
    static void stopServer() throws Exception {
        client.close();
        server.close();
    }

    @Test
    void echoesSmallMessage() {
        assertTrue(client.sendPost("hello"));
    }

    @Test
    void echoesEmptyMessage() {
        assertTrue(client.sendPost(""));
    }

    @Test
    void echoesMultibyteMessage() {
        assertTrue(client.sendPost("già è così — 日本語"));
    }

    @Test
    void echoesLargeMessage() {
        assertTrue(client.sendPost("abcdefghij".repeat(300_000)));
    }
}
