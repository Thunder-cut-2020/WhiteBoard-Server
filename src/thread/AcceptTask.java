package thread;

import connect.Client1;

import java.io.IOException;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.nio.channels.ServerSocketChannel;
import java.util.Iterator;
import java.util.Set;
import java.util.Vector;

public class AcceptTask implements Runnable{

    private ServerSocketChannel serverSocket;
    private Selector selector;
    private Vector<Client1> connections;

    public AcceptTask(ServerSocketChannel serverSocket, Selector selector, Vector<Client1> connections){
        this.serverSocket = serverSocket;
        this.selector = selector;
        this.connections = connections;
    }
    @Override
    public void run() {
        while(true){
            try {
                while(true){
                    selector.select();
                    Set<SelectionKey> selectedKeys = selector.selectedKeys();
                    Iterator<SelectionKey> selectionKeyIterator = selectedKeys.iterator();
                    while (selectionKeyIterator.hasNext()) {
                        SelectionKey key = selectionKeyIterator.next();
                        if (key.isAcceptable()) {
                            //TODO 굳이 이렇게 나눠야 할까?
                        }
                        selectionKeyIterator.remove();
                    }
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }
}
