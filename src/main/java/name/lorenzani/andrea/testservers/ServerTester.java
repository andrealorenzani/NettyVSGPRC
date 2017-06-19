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

package name.lorenzani.andrea.testservers;

import org.apache.commons.lang.RandomStringUtils;

import java.io.BufferedReader;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class ServerTester {

    private final List<IClient> clients;

    public ServerTester(List<IClient> clients){
        this.clients = clients;
    }

    private void warmup(IClient client){
        /***
         * This should warmup the JVM, meaning that it triggers every optimization
         * on the code ever. I don't think it is necessary, but someone complained...
         */
        for(int i = 0; i < 100; i++){
            String warmUpStr = RandomStringUtils.random(20);
            try{
                client.sendPost(warmUpStr);
            } catch(Exception e){}
        }
    }

    private void test(IClient client, List<String> msgs) {
        long success = 0;
        long failure = 0;
        long max = 0;
        long min = Long.MAX_VALUE;
        long total = 0;
        List<Long> times = new ArrayList<>(msgs.size());
        for (String msg: msgs) {
            boolean res;
            long start = System.currentTimeMillis();
            try{
                res = client.sendPost(msg);
            } catch(Exception e){ res = false; }
            long stop = System.currentTimeMillis();
            if(res) success++;
            else failure++;
            long time = stop-start;
            if(time > max) max = time;
            if(time < min) min = time;
            total = total + time;
            times.add(stop-start);
        }
        times.sort(Long::compareTo);
        System.out.println();
        System.out.println("===============================");
        System.out.println("Client name: "+client.getName());
        System.out.println("Msg sents: "+msgs.size());
        System.out.println("Successes: "+success);
        System.out.println("Failures: "+failure);
        System.out.println("Minimum time spent: "+min);
        System.out.println("Maximum time spent: "+max);
        System.out.println("Total time spent: "+total);
        System.out.println("Average time spent: "+(total/msgs.size())); // yes yes, div by zero...
        System.out.println("90-perc: "+times.get((msgs.size()*90)/100));
        System.out.println("99-perc: "+times.get((msgs.size()*99)/100));
        System.out.println("===============================");
    }

    private void testWithDimensions(int lenght, int nmessages){
        List<String> messages = new ArrayList<>(nmessages);
        for(int i=0; i<nmessages; i++) {
            messages.add(RandomStringUtils.random(lenght, true, true));
        }
        System.out.println("***********************************");
        System.out.println("STARTING "+nmessages+" TESTS OF SIZE "+lenght);
        System.out.println("***********************************");
        startTest(messages);
    }

    private void testWithFile(String filePath, int nmessages){
        List<String> messages = new ArrayList<>(nmessages);
        String singleMsg = null;
            try {
                StringBuilder fileStr = new StringBuilder();
                BufferedReader content = Files.newBufferedReader(Paths.get(".", filePath));
                content.lines().forEach(fileStr::append);
                singleMsg = fileStr.toString();
            }
            catch(IOException e){
                System.out.println("Exception reading file "+filePath);
                e.printStackTrace();
                singleMsg = filePath;
            }
        for(int i=0; i<nmessages; i++) {
                messages.add(singleMsg);
        }
        System.out.println("***********************************");
        System.out.println("STARTING "+nmessages+" TESTS OF "+filePath+" (size "+messages.get(0).length()+")");
        System.out.println("***********************************");
        startTest(messages);
    }

    private void startTest(List<String> messages) {
        clients.forEach(nameClient -> {
            warmup(nameClient);
            System.out.println("*** "+nameClient.getName()+" ***");
            test(nameClient, messages);
        });
    }

    private void startSuite(int length, int nmsg) {
        System.out.println("------------------------------------");
        System.out.println("Starting with "+nmsg+" messages of "+length+" characters");
        System.out.println("------------------------------------");
        testWithDimensions(length, nmsg);
    }

    public void startSuite(){
        startSuite(100, 2500);
        startSuite(100, 25000);
        startSuite(5000, 2500);
        startSuite(5000, 50000);
        startSuite(100000, 100);
        System.out.println("------------------------------------");
        System.out.println("Send 10 times the bible");
        System.out.println("------------------------------------");
        testWithFile("bible.txt", 10);
    }

}
