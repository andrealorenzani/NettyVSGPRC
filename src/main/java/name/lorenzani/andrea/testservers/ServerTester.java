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

    private final Map<String, IClient> clients;

    public ServerTester(Map<String, IClient> clients){
        this.clients = clients;
    }

    private void warmup(IClient client){
        for(int i = 0; i < 1000; i++){
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
            System.out.print(".");
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
        clients.entrySet().forEach(nameClient -> {
            warmup(nameClient.getValue());
            System.out.println("*** "+nameClient.getKey()+" ***");
            test(nameClient.getValue(), messages);
        });
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
        System.out.println("STARTING "+nmessages+" TESTS OF "+filePath);
        System.out.println("***********************************");
        clients.entrySet().forEach(nameClient -> {
            warmup(nameClient.getValue());
            System.out.println("*** "+nameClient.getKey()+" ***");
            test(nameClient.getValue(), messages);
        });
    }

    public void startSuite(){
        /*System.out.println("------------------------------------");
        System.out.println("Starting with 2500 small messages");
        System.out.println("------------------------------------");
        testWithDimensions(100, 25);
        System.out.println("------------------------------------");
        System.out.println("Starting with 25000 small messages");
        System.out.println("------------------------------------");
        testWithDimensions(100, 250);
        System.out.println("------------------------------------");
        System.out.println("Starting with 2500 medium messages");
        System.out.println("------------------------------------");
        testWithDimensions(5000, 25);
        System.out.println("------------------------------------");
        System.out.println("Starting with 50000 medium messages");
        System.out.println("------------------------------------");
        testWithDimensions(5000, 500);
        System.out.println("------------------------------------");
        System.out.println("Starting with 100 long messages");
        System.out.println("------------------------------------");
        */
        testWithDimensions(1000000, 10);
        System.out.println("------------------------------------");
        System.out.println("Fuck off, let's send 10 times the bible");
        System.out.println("------------------------------------");
        testWithFile("bible.txt", 1);
    }

}
