package name.lorenzani.andrea.testservers.grpc;

import io.grpc.Server;
import io.grpc.ServerBuilder;
import io.grpc.stub.StreamObserver;

import java.io.IOException;
import java.util.concurrent.TimeUnit;

/** A gRPC server that echoes every {@link MsgRequest} back as a {@link MsgReply}. */
public class GRPCServer implements AutoCloseable {

    private Server server;

    /** Binds to {@code port} (0 picks a free one) and returns the port actually used. */
    public int start(int port) throws IOException {
        server = ServerBuilder.forPort(port).addService(new MsgSenderImpl()).build().start();
        return server.getPort();
    }

    public void awaitTermination() throws InterruptedException {
        server.awaitTermination();
    }

    @Override
    public void close() throws InterruptedException {
        if (server != null) {
            server.shutdown().awaitTermination(5, TimeUnit.SECONDS);
        }
    }

    static class MsgSenderImpl extends MessageSenderGrpc.MessageSenderImplBase {
        @Override
        public void msgTransfer(MsgRequest request, StreamObserver<MsgReply> responseObserver) {
            responseObserver.onNext(MsgReply.newBuilder().setEcho(request.getContent()).build());
            responseObserver.onCompleted();
        }
    }
}
