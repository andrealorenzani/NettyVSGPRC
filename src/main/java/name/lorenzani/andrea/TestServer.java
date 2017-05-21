package name.lorenzani.andrea;

import name.lorenzani.andrea.testservers.netty.NettyServer;

/**
 * Hello world!
 *
 */
public class TestServer
{
    public static void main( String[] args )
    {
        System.out.println( "Starting Netty..." );
        try {
            NettyServer.main(args);
        } catch(Exception e){
            e.printStackTrace();
        }
    }
}
