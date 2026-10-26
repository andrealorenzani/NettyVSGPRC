package name.lorenzani.andrea.testservers.grpc;

import io.grpc.ManagedChannel;
import io.grpc.ManagedChannelBuilder;
import name.lorenzani.andrea.testservers.IClient;

import java.util.concurrent.TimeUnit;

/** Sends the message through gRPC and checks the reply is identical. */
public class GRPCClient implements IClient {

    private final ManagedChannel channel;
    private final MessageSenderGrpc.MessageSenderBlockingStub blockingStub;

    public GRPCClient(String host, int port) {
        // Plaintext on purpose: the benchmark should not pay for TLS handshakes.
        channel = ManagedChannelBuilder.forAddress(host, port).usePlaintext().build();
        blockingStub = MessageSenderGrpc.newBlockingStub(channel);
    }

    @Override
    public boolean sendPost(String content) {
        String message = IClient.truncate(content);
        var reply = blockingStub.msgTransfer(MsgRequest.newBuilder().setContent(message).build());
        return message.equals(reply.getEcho());
    }

    @Override
    public void close() throws InterruptedException {
        channel.shutdown().awaitTermination(5, TimeUnit.SECONDS);
    }
}
