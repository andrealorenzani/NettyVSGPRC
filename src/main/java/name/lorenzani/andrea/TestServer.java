package name.lorenzani.andrea;

import com.google.common.collect.ImmutableMap;
import name.lorenzani.andrea.testservers.ServerTester;
import name.lorenzani.andrea.testservers.grpc.GRPCClient;
import name.lorenzani.andrea.testservers.grpc.GRPCServer;
import name.lorenzani.andrea.testservers.netty.NettyClient;
import name.lorenzani.andrea.testservers.netty.NettyServer;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class TestServer {
    public static void main(String[] args) {
        if (args.length == 0 || args[0].equals("both")) {
            ExecutorService executor = Executors.newFixedThreadPool(12);
            executor.submit(() -> {
                String threadName = Thread.currentThread().getName();
                System.out.println("Starting Netty on thread " + threadName + "...");
                try {
                    NettyServer.main(args);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            });
            executor.submit(() -> {
                String threadName = Thread.currentThread().getName();
                System.out.println("Starting GRPC on thread " + threadName + "...");
                try {
                    GRPCServer.main(args);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            });
        }
        if (args.length > 0 || args[0].equals("both")) {
            try {
                Thread.sleep(500);
            } catch (InterruptedException e) {
            }

            ServerTester tester = new ServerTester(ImmutableMap.of("Netty", new NettyClient("http://localhost", 9999),
                    "GRPC", new GRPCClient("localhost", 9998)));
            tester.startSuite();
        }
    }
}
