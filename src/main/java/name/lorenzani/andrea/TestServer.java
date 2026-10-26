package name.lorenzani.andrea;

import name.lorenzani.andrea.testservers.IClient;
import name.lorenzani.andrea.testservers.ServerTester;
import name.lorenzani.andrea.testservers.grpc.GRPCClient;
import name.lorenzani.andrea.testservers.grpc.GRPCServer;
import name.lorenzani.andrea.testservers.netty.NettyClient;
import name.lorenzani.andrea.testservers.netty.NettyServer;

import java.nio.file.Path;
import java.util.List;

/**
 * Usage: {@code java -jar test-webservers.jar [both|server|client] [host]}
 *
 * <ul>
 *   <li>{@code both} (default): start both servers and benchmark them on localhost</li>
 *   <li>{@code server}: only start the servers</li>
 *   <li>{@code client <host>}: only run the benchmark against the servers on {@code host}</li>
 * </ul>
 */
public final class TestServer {

    static final int NETTY_PORT = 9999;
    static final int GRPC_PORT = 9998;

    private TestServer() {
    }

    public static void main(String[] args) throws Exception {
        String mode = args.length > 0 ? args[0] : "both";
        String host = args.length > 1 ? args[1] : "localhost";
        switch (mode) {
            case "both" -> {
                try (var netty = new NettyServer(); var grpc = new GRPCServer()) {
                    netty.start(NETTY_PORT);
                    grpc.start(GRPC_PORT);
                    runBenchmark("localhost");
                }
            }
            case "server" -> {
                try (var netty = new NettyServer(); var grpc = new GRPCServer()) {
                    System.out.println("Netty listening on " + netty.start(NETTY_PORT));
                    System.out.println("gRPC listening on " + grpc.start(GRPC_PORT));
                    grpc.awaitTermination();
                }
            }
            case "client" -> runBenchmark(host);
            default -> {
                System.err.println("Usage: [both|server|client] [host]");
                System.exit(1);
            }
        }
    }

    private static void runBenchmark(String host) throws Exception {
        try (var netty = new NettyClient(host, NETTY_PORT); var grpc = new GRPCClient(host, GRPC_PORT)) {
            new ServerTester(List.<IClient>of(netty, grpc)).startSuite(Path.of("bible.txt"));
        }
    }
}
