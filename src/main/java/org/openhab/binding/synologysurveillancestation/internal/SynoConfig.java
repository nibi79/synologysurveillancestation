/**
 * Copyright (c) 2010-2019 by the respective copyright holders.
 *
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 */
package org.openhab.binding.synologysurveillancestation.internal;

import static org.openhab.binding.synologysurveillancestation.SynoBindingConstants.*;

import org.eclipse.smarthome.config.core.Configuration;

/**
 * The {@link SynoConfig} is class for handling the binding configuration
 *
 * @author Nils - Initial contribution
 * @author Pavion - Contribution
 */
public class SynoConfig {
    private String protocol = null;
    private String host = null;
    private String port = null;
    private String username = null;
    private String password = null;

    /**
     * Creates a new {@link SynoConfig} and set the given values
     *
     * @param protocoll   Protocol of the DiskStation (http/https)
     * @param hostAddress IP or host address of the DiskStation
     * @param port        Port of the DiskStation
     * @param username    User name with sufficient rights to run Surveillance Station
     * @param password    User password
     */
    public SynoConfig(String protocoll, String hostAddress, String port, String username, String password) {
        this.protocol = protocoll;
        this.host = hostAddress;
        this.port = port;
        this.username = username;
        this.password = password;
    }

    /**
     * Creates a new {@link SynoConfig} from a configuration object
     *
     * @param configuration
     */
    public SynoConfig(Configuration configuration) {
        this.protocol = configuration.get(PROTOCOL).toString();
        this.host = configuration.get(HOST).toString();
        this.port = configuration.get(PORT).toString();
        this.username = configuration.get(USER_NAME).toString();
        this.password = configuration.get(PASSWORD).toString();
    }

    /**
     * Creates a {@link SynoConfig} with default values.
     */
    public SynoConfig() {
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

    @Override
    public String toString() {
        return "Config [protocoll=" + protocol + ", host=" + host + ", port=" + port + ", username=" + username
                + ", password=" + "********]";
    }

}
