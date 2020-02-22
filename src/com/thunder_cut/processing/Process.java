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
import java.util.Map;
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
     * @param data      this have data, type, src
     * @param clientMap client list
     */
    private void command(ReceivedData data, Map<ClientInformation, List<ClientInformation>> clientMap) {
        ByteBuffer buffer = data.getBuffer();
        buffer.flip();
        String command = new String(buffer.array(), StandardCharsets.UTF_8);
        String[] commandToken = new String[3];
        StringTokenizer stringTokenizer = new StringTokenizer(command, " ");
        int index = 0;
        while (stringTokenizer.hasMoreTokens()) {
            commandToken[index++] = stringTokenizer.nextToken();
        }
        CommandProcess commandProcess = new CommandProcess(clientMap, disconnect);
        try {
            commandProcess.getCommandMap().get(CommandType.acceptable(commandToken[0], data.getSrc().isOp())).accept(data, commandToken);
        } catch (NullPointerException e) {
            errorMessage(data.getSrc(), clientMap);
        }
    }

    private void errorMessage(ClientInformation src, Map<ClientInformation, List<ClientInformation>> clientMap) {
        String errorMessage = "Error! Given command do not exist";
        int srcIndex = 0;
        for (int destIndex = 0; destIndex < clientMap.get(src).size(); destIndex++) {
            ClientInformation destination = clientMap.get(src).get(destIndex);
            if (src == destination) {
                srcIndex = destIndex;
                SendingData sendingData = new SendingData(srcIndex, destIndex, DataType.MSG, errorMessage.getBytes());
                write(src, destination, sendingData);
                return;
            }
        }
    }

    /**
     * Generate message or image data and write to all client in client list
     *
     * @param data      this have data, type, src
     * @param clientMap client list
     */
    private void notCommand(ReceivedData data, Map<ClientInformation, List<ClientInformation>> clientMap) {
        for (int index = 0; index < clientMap.get(data.getSrc()).size(); index++) {
            ClientInformation destination = clientMap.get(data.getSrc()).get(index);
            int[] IDs = findID(data.getSrc(), destination, clientMap);
            SendingData sendingData = new SendingData(IDs[0], IDs[1], data.getDataType(), data.getBuffer().array());
            write(data.getSrc(), destination, sendingData);
        }
    }


    private int[] findID(ClientInformation src, ClientInformation dest, Map<ClientInformation, List<ClientInformation>> clientMap) {
        ClientInformation[] clientArr = clientMap.keySet().toArray(ClientInformation[]::new);
        int[] IDs = new int[2];
        for (int index = 0; index < clientArr.length; index++) {
            if (src == clientArr[index]) {
                IDs[0] = index;
            }
            if (dest == clientArr[index]) {
                IDs[1] = index;
            }
        }
        return IDs;
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

    public void processWithType(ReceivedData data, Map<ClientInformation, List<ClientInformation>> clientMap) {
        if (data.getDataType().equals(DataType.CMD)) {
            command(data, clientMap);
        } else {
            notCommand(data, clientMap);
        }
    }
}
