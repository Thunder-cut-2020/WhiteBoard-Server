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
    private List<ClientInformation> clientList;
    private Consumer<ClientInformation> disconnect;

    public CommandProcess(List<ClientInformation> clientList, Consumer<ClientInformation> disconnect) {
        commandMap = new EnumMap<>(CommandType.class);
        commandMap.put(CommandType.KICK, this::kick);
        commandMap.put(CommandType.OP, this::op);
        commandMap.put(CommandType.SET_NAME, this::setName);
        this.clientList = clientList;
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
     * Find Client include given name
     * If don't exist return null
     *
     * @param clientName client's name
     * @return clientinfo that include clientName
     */
    private ClientInformation getDest(String clientName) {
        for (ClientInformation client : clientList) {
            if (client.getName().equals(clientName)) {
                return client;
            }
        }
        return null;
    }

    public Map<CommandType, BiConsumer<ReceivedData, String[]>> getCommandMap() {
        return commandMap;
    }
}
