package result;

import result.thread.ServerUsingCompletionHandler;

public class Server2 {

    public static void main(String[] args) {
        ServerUsingCompletionHandler server = new ServerUsingCompletionHandler();
        Thread test = new Thread(server);
        test.start();
    }
}
