package thread;

import connect.Client1;

import java.nio.channels.Selector;
import java.nio.channels.ServerSocketChannel;
import java.util.Vector;

public interface ServerInterface {
    ServerSocketChannel serverSocket = null;
    Vector<Client1> connections = new Vector<>();
    Selector selector = null;
}
