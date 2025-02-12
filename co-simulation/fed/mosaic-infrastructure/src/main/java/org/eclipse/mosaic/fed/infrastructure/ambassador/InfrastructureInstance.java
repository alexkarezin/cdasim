/*
 * Copyright (C) 2023 LEIDOS.
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not
 * use this file except in compliance with the License. You may obtain a copy of
 * the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS, WITHOUT
 * WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the
 * License for the specific language governing permissions and limitations under
 * the License.
 */

package org.eclipse.mosaic.fed.infrastructure.ambassador;

import org.eclipse.mosaic.lib.geo.CartesianPoint;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;

/**
 * InfrastructureInstance class represents a physical instance of an
 * infrastructure node in the simulated environment.
 * It contains information about the infrastructure node such as its ID,
 * location, target address, and ports.
 */
public class InfrastructureInstance {

    private String infrastructureId;
    private InetAddress targetAddress;
    private int rxMessagePort;
    private int timeSyncPort;
    private CartesianPoint location = null;
    private DatagramSocket rxMsgsSocket = null;

    /**
     * Constructor for InfrastructureInstance
     * 
     * @param infrastructureId the ID of the infrastructure node
     * @param targetAddress    the target IP address of the infrastructure node
     * @param rxMessagePort    the receive message port of the infrastructure node
     * @param timeSyncPort     the time synchronization port of the infrastructure
     *                         node
     * @param location         the location of the infrastructure node in the
     *                         simulated environment
     */
    public InfrastructureInstance(String infrastructureId, InetAddress targetAddress,
            int rxMessagePort, int timeSyncPort, CartesianPoint location) {
        this.infrastructureId = infrastructureId;
        this.targetAddress = targetAddress;
        this.rxMessagePort = rxMessagePort;
        this.timeSyncPort = timeSyncPort;
        this.location = location;
    }

    /**
     * Returns the target IP address of the infrastructure node
     * 
     * @return InetAddress the target IP address of the infrastructure node
     */
    public InetAddress getTargetAddress() {
        return targetAddress;
    }

    /**
     * Sets the target IP address of the infrastructure node
     * 
     * @param targetAddress the target IP address to set
     */
    public void setTargetAddress(InetAddress targetAddress) {
        this.targetAddress = targetAddress;
    }

    /**
     * Returns the location of the infrastructure node in the simulated environment
     * 
     * @return CartesianPoint the location of the infrastructure node
     */
    public CartesianPoint getLocation() {
        return this.location;
    }

    /**
     * Sets the location of the infrastructure node in the simulated environment
     * 
     * @param location the location to set
     */
    public void setLocation(CartesianPoint location) {
        this.location = location;
    }

    /**
     * Returns the ID of the infrastructure node
     * 
     * @return String the ID of the infrastructure node
     */
    public String getInfrastructureId() {
        return infrastructureId;
    }

    /**
     * Sets the ID of the infrastructure node
     * 
     * @param infrastructureId the ID to set
     */
    public void setInfrastructureId(String infrastructureId) {
        this.infrastructureId = infrastructureId;
    }

    /**
     * Returns the receive message port of the infrastructure node
     * 
     * @return int the receive message port of the infrastructure node
     */
    public int getRxMessagePort() {
        return rxMessagePort;
    }

    /**
     * Sets the receive message port of the infrastructure node
     * 
     * @param rxMessagePort the port to set
     */
    public void setRxMessagePort(int rxMessagePort) {
        this.rxMessagePort = rxMessagePort;
    }

    /**
     * Returns the time synchronization port of the infrastructure node
     * 
     * @return int the time synchronization port of the infrastructure node
     */
    public int getTimeSyncPort() {
        return timeSyncPort;
    }

    /**
     * Sets the time synchronization port of the infrastructure node
     * 
     * @param timeSyncPort the port to set
     */
    public void setTimeSyncPort(int timeSyncPort) {
        this.timeSyncPort = timeSyncPort;
    }

    /**
     * Creates a DatagramSocket object and binds it to this infrastructure
     * instance's receive message port
     * 
     * @throws IOException if there is an issue with the underlying socket object or
     *                     methods
     */
    public void bind() throws IOException {
        rxMsgsSocket = new DatagramSocket();
    }

    /**
     * Sends the data to the Infrastructure Device communications interface
     * configured at construction time.
     * 
     * @param data The binary data to transmit
     * @throws IOException If there is an issue with the underlying socket object or
     *                     methods
     */
    public void sendMsgs(byte[] data) throws IOException {
        if (rxMsgsSocket == null) {
            throw new IllegalStateException("Attempted to send data before opening socket");
        }
        DatagramPacket packet = new DatagramPacket(data, data.length, targetAddress, rxMessagePort);
        rxMsgsSocket.send(packet);

    }

    /**
     * Sends time sync data to the Infrastructure Device communications interface configured at construction time.
     * @param data The binary data to transmit
     * @throws IOException If there is an issue with the underlying socket object or methods
     */
    public void sendTimeSyncMsgs(byte[] data) throws IOException {
        if (rxMsgsSocket == null) {
            throw new IllegalStateException("Attempted to send data before opening socket");
        }

        DatagramPacket packet = new DatagramPacket(data, data.length, targetAddress, timeSyncPort);
        rxMsgsSocket.send(packet);

    }
}
