/***
*   Copyright 2017 Andrea Lorenzani
*
*   Licensed under the Apache License, Version 2.0 (the "License");
*   you may not use this file except in compliance with the License.
*   You may obtain a copy of the License at
*
*       http://www.apache.org/licenses/LICENSE-2.0
*
*   Unless required by applicable law or agreed to in writing, software
*   distributed under the License is distributed on an "AS IS" BASIS,
*   WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
*   See the License for the specific language governing permissions and
*   limitations under the License.
*
***/

package name.lorenzani.andrea;

import com.google.common.collect.ImmutableMap;
import name.lorenzani.andrea.testservers.IClient;
import name.lorenzani.andrea.testservers.ServerTester;
import name.lorenzani.andrea.testservers.grpc.GRPCClient;
import name.lorenzani.andrea.testservers.grpc.GRPCServer;
import name.lorenzani.andrea.testservers.netty.NettyClient;
import name.lorenzani.andrea.testservers.netty.NettyServer;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.function.Predicate;
import java.util.stream.Collectors;

public class TestServer {
    public static void main(String[] args) {
        // java -jar <jar> both runs both the server and the tests on the same machine
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
        if (args.length > 0) {
            try {
                Thread.sleep(500);
            } catch (InterruptedException e) {
            }
            List<String> arg = Arrays.asList(args).stream().collect(Collectors.toList());
            arg.remove("both");
            String remoteaddr = (arg.size() > 0)? arg.get(0) : "localhost";

            ServerTester tester = new ServerTester(Arrays.asList(new NettyClient("http://"+remoteaddr, 9999),
                    new GRPCClient(remoteaddr, 9998)));
            tester.startSuite();
        }
    }
}
