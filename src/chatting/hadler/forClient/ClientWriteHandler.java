package chatting.hadler.forClient;

import result.Attachment;

import java.nio.channels.CompletionHandler;

public class ClientWriteHandler implements CompletionHandler<Integer, Attachment> {

    @Override
    public void completed(Integer result, Attachment attachment) {

    }

    @Override
    public void failed(Throwable exc, Attachment attachment) {

    }
}
