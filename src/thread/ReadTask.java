package thread;

import connect.Client1;

import java.io.IOException;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.nio.channels.ServerSocketChannel;
import java.util.Iterator;
import java.util.Set;
import java.util.Vector;

public class ReadTask implements Runnable {

    private ServerSocketChannel serverSocket;
    private Selector selector;
    private Vector<Client1> connections;

    public ReadTask(ServerSocketChannel serverSocket, Selector selector, Vector<Client1> connections){
        this.serverSocket = serverSocket;
        this.selector = selector;
        this.connections = connections;
    }

    @Override
    public void run() {
        try {
            selector.select();
            Set<SelectionKey> selectionKeys = selector.selectedKeys();
            for (SelectionKey key : selectionKeys) {
                if (key.isReadable()) {

                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
