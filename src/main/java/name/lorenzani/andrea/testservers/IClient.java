package name.lorenzani.andrea.testservers;

/** A client that sends a message to an echo server and checks that the same message comes back. */
public interface IClient extends AutoCloseable {

    /** Messages are truncated to this many characters so they fit the default 4MB limits of both servers. */
    int MAX_MESSAGE_LENGTH = 4_000_000;

    default String getName() {
        return getClass().getSimpleName();
    }

    /** @return true if the server echoed back exactly {@code content} */
    boolean sendPost(String content) throws Exception;

    @Override
    default void close() throws Exception {
    }

    static String truncate(String content) {
        return content.length() > MAX_MESSAGE_LENGTH ? content.substring(0, MAX_MESSAGE_LENGTH) : content;
    }
}
