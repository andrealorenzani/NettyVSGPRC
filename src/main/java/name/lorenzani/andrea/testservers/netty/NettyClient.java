package name.lorenzani.andrea.testservers.netty;

import name.lorenzani.andrea.testservers.IClient;

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;

/** Posts the message over HTTP/1.1 and checks the response body is identical. */
public class NettyClient implements IClient {

    private final HttpClient client = HttpClient.newBuilder().version(HttpClient.Version.HTTP_1_1).build();
    private final URI uri;

    public NettyClient(String host, int port) {
        this.uri = URI.create("http://" + host + ":" + port);
    }

    @Override
    public boolean sendPost(String content) throws Exception {
        String message = IClient.truncate(content);
        var request = HttpRequest.newBuilder(uri)
                .header("User-Agent", "NettyClient")
                .POST(HttpRequest.BodyPublishers.ofString(message))
                .build();
        var response = client.send(request, HttpResponse.BodyHandlers.ofString());
        return response.statusCode() == 200 && message.equals(response.body());
    }

    @Override
    public void close() {
        client.close();
    }
}
