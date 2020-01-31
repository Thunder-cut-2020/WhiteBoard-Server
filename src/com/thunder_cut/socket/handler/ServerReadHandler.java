/*
 * ServerReadHandler.java
 * Author : Arakene
 * Created Date : 2020-01-15
 */
package com.thunder_cut.socket.handler;

import com.thunder_cut.socket.Attachment;

import java.nio.ByteBuffer;
import java.nio.channels.CompletionHandler;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.util.Vector;
import java.util.function.Consumer;

/**
 * This class will work After read method
 * Read data from client and send to all client in clientGroup
 */

public class ServerReadHandler {

    private CompletionHandler<Integer, Attachment> readHandler;
    private Consumer<ByteBuffer> sendToAllClient;

    public ServerReadHandler(Consumer<ByteBuffer> sendToAllClient) {
        this.sendToAllClient = sendToAllClient;
        readHandler = new CompletionHandler<Integer, Attachment>() {
            /**
             * If read method success completed will start
             * @param result Statement
             * @param attachment Client Information
             */
            @Override
            public void completed(Integer result, Attachment attachment) {
                attachment.getBuffer().flip();
                sendToAllClient.accept(attachment.getBuffer());
                attachment.getClient().read(attachment.getBuffer(),attachment,this);
            }

            @Override
            public void failed(Throwable exc, Attachment attachment) {
                Vector<?> clientGroup = attachment.getClientGroup();
                if(clientGroup.size() > 0){
                    clientGroup.remove(attachment);
                }
                else{
                    System.out.println("Server Read Error");
                }
            }
        };
    }

    public CompletionHandler<Integer, Attachment> getReadHandler() {
        return readHandler;
    }
}
