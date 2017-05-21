package name.lorenzani.andrea.testservers;

public interface IClient {
    default String getName() {
        return this.getClass().getSimpleName();
    }
    boolean sendPost(String content) throws Exception;
}