package name.lorenzani.andrea.testservers.netty;

import name.lorenzani.andrea.testservers.IClient;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

public class NettyClient implements IClient{

    private final String host;
    private final int port;
    private URL obj;

    public NettyClient(String host, int port){
        this.host = host;
        this.port = port;
        try {
            this.obj = new URL(host + ":" + port);
        } catch (Exception e) {
            obj = null;
            e.printStackTrace();
        }
    }

    // HTTP POST request
    public boolean sendPost(String content) throws Exception {
        HttpURLConnection con = (HttpURLConnection) obj.openConnection();

        //add reuqest header
        con.setRequestMethod("POST");
        con.setRequestProperty("User-Agent", "NettyClient");

        // Send post request
        con.setDoOutput(true);
        DataOutputStream wr = new DataOutputStream(con.getOutputStream());
        wr.writeBytes(content);
        wr.flush();
        wr.close();

        int responseCode = con.getResponseCode();

        BufferedReader in = new BufferedReader(
                new InputStreamReader(con.getInputStream()));
        String inputLine;
        StringBuilder response = new StringBuilder();

        while ((inputLine = in.readLine()) != null) {
            response.append(inputLine);
        }
        in.close();
        return responseCode == 200 && response.toString().equals(content);
    }
}
