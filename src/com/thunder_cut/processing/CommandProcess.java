/*
 * CommandProcess.java
 * Author : Arakene
 * Created Date : 2020-02-14
 */
package com.thunder_cut.processing;

import com.thunder_cut.processing.data.CommandType;
import com.thunder_cut.processing.data.ReceivedData;
import com.thunder_cut.socket.ClientInformation;

import java.util.EnumMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.function.BiConsumer;
import java.util.function.Consumer;

/**
 * This work if data type is command with given data
 */
public class CommandProcess {


    private Map<CommandType, BiConsumer<ReceivedData, String[]>> commandMap;
    private Map<ClientInformation, List<ClientInformation>> clientMap;
    private Consumer<ClientInformation> disconnect;

    public CommandProcess(Map<ClientInformation, List<ClientInformation>> clientMap, Consumer<ClientInformation> disconnect) {
        commandMap = new EnumMap<CommandType, BiConsumer<ReceivedData, String[]>>(CommandType.class);
        commandMap.put(CommandType.KICK, this::kick);
        commandMap.put(CommandType.OP, this::op);
        commandMap.put(CommandType.BLIND, this::blind);
        commandMap.put(CommandType.IGNORE, this::ignore);
        commandMap.put(CommandType.SET_NAME, this::setName);
        this.clientMap = clientMap;
        this.disconnect = disconnect;
    }

    /**
     * Disconnect specific client
     *
     * @param data   data, type, src
     * @param tokens split command
     */
    private void kick(ReceivedData data, String[] tokens) {
        disconnect.accept(getDest(tokens[1]));
    }

    private void op(ReceivedData data, String[] tokens) {
        Objects.requireNonNull(getDest(tokens[1])).setOp(true);
    }

    /**
     * Set client name by given data
     *
     * @param data   data, type, src
     * @param tokens split command
     */
    private void setName(ReceivedData data, String[] tokens) {
        data.getSrc().setName(tokens[1]);
    }

    /**
     * Stop write my data to all client
     * Circuit All keys in clientMap and Remove self
     * If do not exist in list, add self
     *
     * @param data   data, type, src
     * @param tokens split command
     */
    private void blind(ReceivedData data, String[] tokens) {
        for (ClientInformation key : clientMap.keySet()) {
            if (!clientMap.get(key).contains(data.getSrc())) {
                clientMap.get(key).add(data.getSrc());
            } else {
                clientMap.get(key).remove(data.getSrc());
            }
        }
    }

    /**
     * Block send data from client that given data
     * If target exist in list remove target
     * else add target
     *
     * @param data   data, type, src
     * @param tokens split command
     */
    private void ignore(ReceivedData data, String[] tokens) {
        ClientInformation dest = getDest(tokens[1]);
        if (!clientMap.get(data.getSrc()).contains(dest)) {
            clientMap.get(data.getSrc()).add(dest);
        } else {
            clientMap.get(data.getSrc()).remove(dest);
        }
    }

    /**
     * Find Client include given name
     * If don't exist return null
     *
     * @param clientName client's name
     * @return clientinfo that include clientName
     */
    private ClientInformation getDest(String clientName) {
        for (List<ClientInformation> list : clientMap.values()) {
            for (ClientInformation client : list) {
                if (client.getName().equals(clientName)) {
                    return client;
                }
            }
        }
        return null;
    }

    public Map<CommandType, BiConsumer<ReceivedData, String[]>> getCommandMap() {
        return commandMap;
    }

}
