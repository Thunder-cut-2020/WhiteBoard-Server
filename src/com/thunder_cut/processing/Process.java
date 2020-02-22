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
import com.thunder_cut.socket.SyncServer;

import java.nio.ByteBuffer;
import java.nio.charset.StandardCharsets;
import java.util.List;

/**
 * After Receiving data, work by data type
 */
public class Process {
    private SyncServer server;

    public Process(SyncServer server) {
        this.server = server;
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
        String[] args = command.split(" ");
        CommandProcess commandProcess = new CommandProcess(clientList, server::disconnected);
        try {
            commandProcess.getCommandMap().get(CommandType.acceptable(args[0], data.getSrc().isOp())).accept(data, args);
        } catch (NullPointerException e) {
            sendMessage("Error! Given command do not exist", data.getSrc());
        }
    }

    private void sendMessage(String message, ClientInformation dest) {
        int id = dest.getId();
        SendingData sendingData = new SendingData(id, id, DataType.MSG, message.getBytes());
        server.send(sendingData.generateDataByType(dest), dest);
    }

    /**
     * Generate message or image data and write to all client in client list
     *
     * @param data       this have data, type, src
     * @param clientList client list
     */
    private void broadcast(ReceivedData data, List<ClientInformation> clientList) {
        ClientInformation src = data.getSrc();
        int srcId = src.getId();
        ClientInformation[] array;
        synchronized (clientList) {
            array = clientList.toArray(new ClientInformation[clientList.size()]);
        }
        for (ClientInformation dest : array) {
            SendingData sendingData = new SendingData(srcId, dest.getId(), data.getDataType(), data.getBuffer().array());
            server.send(sendingData.generateDataByType(src), dest);
        }
    }

    public void processWithType(ReceivedData data, List<ClientInformation> clientList) {
        if (data.getDataType().equals(DataType.CMD)) {
            command(data, clientList);
        } else {
            broadcast(data, clientList);
        }
    }
}
