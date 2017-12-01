/**
 * Copyright (c) 2010-2017 by the respective copyright holders.
 *
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 */
package org.openhab.binding.synologysurveillancestation.internal;

import static org.openhab.binding.synologysurveillancestation.SynologySurveillanceStationBindingConstants.*;

import org.eclipse.smarthome.config.core.Configuration;

/**
 * The {@link Config} is class for handling the binding configuration
 *
 * @author Nils
 */
public class Config {
    private String protocol = null;
    private String host = null;
    private String port = null;
    private String username = null;
    private String password = null;
    private int poll = 10000;

    /**
     * Creates a new {@link Config} and set the given protocoll, hostAddress, username, password.
     *
     * @param hostAddress
     * @param username
     * @param password
     * @param sessionID
     */
    public Config(String protocoll, String hostAddress, String port, String username, String password) {
        this.protocol = protocoll;
        this.host = hostAddress;
        this.port = port;
        this.username = username;
        this.password = password;
    }

    public Config(Configuration configuration) {
        this.protocol = configuration.get(PROTOCOL).toString();
        this.host = configuration.get(HOST).toString();
        this.port = configuration.get(PORT).toString();
        this.username = configuration.get(USER_NAME).toString();
        this.password = configuration.get(PASSWORD).toString();
    }

    /**
     * Creates a {@link Config} with default values.
     */
    public Config() {
        // config with default values
    }

    /**
     * Returns the protocol.
     *
     * @return the protocol
     */
    public String getProtocol() {
        return protocol;
    }

    /**
     * Sets the protocol for Surveillance Station.
     *
     * @param the protocol
     */
    public void setProtocoll(String protocol) {
        this.protocol = protocol;
    }

    /**
     * Returns the host name Surveillance Station.
     *
     * @return the host address
     */
    public String getHost() {
        return host;
    }

    /**
     * Sets the host name of the Surveillance Station.
     *
     * @param the hostAddress
     */
    public void setHost(String host) {
        this.host = host;
    }

    /**
     * Returns the port.
     *
     * @return the port
     */
    public String getPort() {
        return port;
    }

    /**
     * Sets the port for Surveillance Station.
     *
     * @param the port
     */
    public void setPort(String port) {
        this.port = port;
    }

    /**
     * Returns the username.
     *
     * @return the username
     */
    public String getUsername() {
        return username;
    }

    /**
     * Sets the username.
     *
     * @param username
     */
    public void setUsername(String username) {
        this.username = username;
    }

    /**
     * Returns the password.
     *
     * @return the password
     */
    public String getPassword() {
        return password;
    }

    /**
     * Sets the password.
     *
     * @param password
     */
    public void setPassword(String password) {
        this.password = password;
    }

    /**
     * Returns the poll.
     *
     * @return poll
     */
    public int getPoll() {
        return poll;
    }

    /**
     * Sets the poll.
     *
     * @param poll
     */
    public void setPoll(int poll) {
        this.poll = poll;
    }

    @Override
    public String toString() {
        return "Config [protocoll=" + protocol + ", host=" + host + ", port=" + port + ", username=" + username
                + ", password=" + "*****" + ", poll=" + poll + "]";
    }

}
