/*
 * Processing.java
 * Author : Arakene
 * Created Date : 2020-02-14
 */
package com.thunder_cut.processing;

import com.thunder_cut.processing.data.CommandType;
import com.thunder_cut.processing.data.DataType;
import com.thunder_cut.processing.data.ReceivedData;
import com.thunder_cut.processing.data.SendingData;
import com.thunder_cut.socket.ClientInformation;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.StringTokenizer;
import java.util.function.Consumer;

/**
 * After Receiving data, work by data type
 */
public class Process {
    private Consumer<ClientInformation> disconnect;

    public Process(Consumer<ClientInformation> disconnect) {
        this.disconnect = disconnect;
    }

    /**
     * This work if type is command
     * Split command and work with given command in command process
     *
     * @param data       this have data, type, src
     * @param clientList client list
     */
    private void command(ReceivedData data, List<ClientInformation> clientList) {
        ByteBuffer buffer = data.getBuffer();
        buffer.flip();
        String command = new String(buffer.array(), StandardCharsets.UTF_8);
        String[] commandToken = new String[3];
        StringTokenizer stringTokenizer = new StringTokenizer(command, " ");
        int index = 0;
        while (stringTokenizer.hasMoreTokens()) {
            commandToken[index++] = stringTokenizer.nextToken();
        }
        CommandProcess commandProcess = new CommandProcess(clientList, disconnect);
        try {
            commandProcess.getCommandMap().get(CommandType.acceptable(commandToken[0], data.getSrc().isOp())).accept(data, commandToken);
        } catch (NullPointerException e) {
            errorMessage(data.getSrc(), clientList);
        }
    }

    private void errorMessage(ClientInformation src, List<ClientInformation> clientList) {
        String errorMessage = "Error! Given command do not exist";
        int id = src.getId();
        SendingData sendingData = new SendingData(id, id, DataType.MSG, errorMessage.getBytes());
        write(src, src, sendingData);
    }

    /**
     * Generate message or image data and write to all client in client list
     *
     * @param data       this have data, type, src
     * @param clientList client list
     */
    private void notCommand(ReceivedData data, List<ClientInformation> clientList) {
        ClientInformation src = data.getSrc();
        int srcId = src.getId();
        for (ClientInformation dest : clientList) {
            SendingData sendingData = new SendingData(srcId, dest.getId(), data.getDataType(), data.getBuffer().array());
            write(src, dest, sendingData);
        }
    }

    /**
     * Write to client with given data
     *
     * @param dest client who received data
     * @param data data for write
     */
    private synchronized void write(ClientInformation src, ClientInformation dest, SendingData data) {
        try {
            dest.getClient().write(data.generateDataByType(src));
        } catch (IOException | NullPointerException e) {
            disconnect.accept(dest);
        }
    }

    public void processWithType(ReceivedData data, List<ClientInformation> clientList) {
        if (data.getDataType().equals(DataType.CMD)) {
            command(data, clientList);
        } else {
            notCommand(data, clientList);
        }
    }
}
