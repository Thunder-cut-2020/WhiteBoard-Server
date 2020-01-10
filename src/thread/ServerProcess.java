package thread;

import javax.net.SocketFactory;
import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.*;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.util.Iterator;
import java.util.Set;

public class ServerProcess implements Runnable {

    private Selector selector;
    private ServerSocketChannel serverSocketChannel;
    private ByteBuffer byteBuffer;

    @Override
    public void run() {
        try {
            //TODO Study CompletionHandler
            //TODO I think this will be work like Selector
            AsynchronousServerSocketChannel serverSocket2 = AsynchronousServerSocketChannel.open();
            serverSocket2.bind(new InetSocketAddress("127.0.0.1",3000));
            AsynchronousSocketChannel socketChannel = AsynchronousSocketChannel.open();
            socketChannel.connect(new InetSocketAddress("127.0.0.1",3000));
//            CompletionHandler<>
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
                        register(selector, serverSocketChannel);
                    }
                    if (key.isReadable()) {
                        answer(byteBuffer, key);
                    }
                    selectionKeyIterator.remove();
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void answer(ByteBuffer byteBuffer, SelectionKey key) throws IOException {
        SocketChannel client = (SocketChannel) key.channel();
        Charset charset = StandardCharsets.UTF_8;
        client.read(byteBuffer);
        System.out.println(new String(byteBuffer.array(), charset));
        if (new String(byteBuffer.array()).trim().equals("EXIT")) {
            System.out.println(key.channel().toString()+"This channel is closed");
            client.close();
        }
        byteBuffer.flip();
        byteBuffer = charset.encode(key.toString());
        client.write(byteBuffer);
        byteBuffer.clear();
        key.interestOpsOr(SelectionKey.OP_WRITE);
        System.out.println("Is Writable? "+key.isWritable());
        System.out.println("Is Readable? "+key.isReadable());

    }

    private void register(Selector selector, ServerSocketChannel serverSocketChannel) throws IOException {
        SocketChannel client = serverSocketChannel.accept();
        client.configureBlocking(false);
        client.register(selector, SelectionKey.OP_READ);
        System.out.println("new Client connected " + client.hashCode());
    }
}
