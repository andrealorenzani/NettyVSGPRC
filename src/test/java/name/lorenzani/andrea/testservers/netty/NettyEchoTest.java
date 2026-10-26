package name.lorenzani.andrea.testservers.netty;

import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertTrue;

class NettyEchoTest {

    private static NettyServer server;
    private static NettyClient client;

    @BeforeAll
    static void startServer() throws Exception {
        server = new NettyServer();
        client = new NettyClient("localhost", server.start(0));
    }

    @AfterAll
    static void stopServer() {
        client.close();
        server.close();
    }

    @Test
    void echoesSmallMessage() throws Exception {
        assertTrue(client.sendPost("hello"));
    }

    @Test
    void echoesEmptyMessage() throws Exception {
        assertTrue(client.sendPost(""));
    }

    @Test
    void echoesMultibyteMessage() throws Exception {
        assertTrue(client.sendPost("già è così — 日本語"));
    }

    @Test
    void echoesLargeMessage() throws Exception {
        assertTrue(client.sendPost("abcdefghij".repeat(300_000)));
    }
}
