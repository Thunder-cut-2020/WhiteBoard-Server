package connect;

import thread.ServerThread;

import java.io.*;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.nio.ByteBuffer;
import java.nio.channels.*;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.util.Iterator;
import java.util.Set;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.ThreadPoolExecutor;

public class Server {

    private static Selector selector;
    private static ServerSocketChannel serverSocketChannel;
    private static ByteBuffer byteBuffer;

    public static void main(String[] args) {
        try {
            selector = Selector.open();
            serverSocketChannel = ServerSocketChannel.open();
            serverSocketChannel.bind(new InetSocketAddress("127.0.0.1", 3000));
            serverSocketChannel.configureBlocking(false);
            serverSocketChannel.register(selector, SelectionKey.OP_ACCEPT);
            byteBuffer = ByteBuffer.allocate(256);
            while (true) {
                selector.select();
                Set<SelectionKey> selectedKeys = selector.selectedKeys();
                Iterator<SelectionKey> selectionKeyIterator = selectedKeys.iterator();
                while (selectionKeyIterator.hasNext()) {
                    SelectionKey key = selectionKeyIterator.next();
                    if (key.isAcceptable()) {
                        register(selector,serverSocketChannel);
                    }
                    if (key.isReadable()) {
                        answerWithEcho(byteBuffer,key);
                    }
                    selectionKeyIterator.remove();
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private static void answerWithEcho(ByteBuffer byteBuffer, SelectionKey key) throws IOException {
        SocketChannel client = (SocketChannel) key.channel();
        Charset charset = StandardCharsets.UTF_8;
        client.read(byteBuffer);
        System.out.println(new String(byteBuffer.array(),charset));
        if (new String(byteBuffer.array()).trim().equals("EXIT")) {
            client.close();
            System.out.println("No client");
        }
        byteBuffer.flip();
        byteBuffer = charset.encode("String");
        client.write(byteBuffer);
        byteBuffer.clear();
    }

    private static void register(Selector selector, ServerSocketChannel serverSocketChannel) throws IOException {
        SocketChannel client = serverSocketChannel.accept();
        client.configureBlocking(false);
        client.register(selector,SelectionKey.OP_READ);
        System.out.println("new Client connected "+client.hashCode());
    }
}
