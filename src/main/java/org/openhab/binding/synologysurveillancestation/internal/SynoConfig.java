/**
 * Copyright (c) 2010-2019 by the respective copyright holders.
 *
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 */
package org.openhab.binding.synologysurveillancestation.internal;

import org.eclipse.jdt.annotation.NonNullByDefault;

/**
 * The {@link SynoConfig} is class for handling the binding configuration
 *
 * @author Nils - Initial contribution
 * @author Pavion - Contribution
 */
@NonNullByDefault
public class SynoConfig {
    private String protocol = "http";
    private String host = "";
    private String port = "5000";
    private String username = "";
    private String password = "";
    private int refreshRateEvents = 5;

    /**
     * Returns the protocol.
     *
     * @return the protocol
     */
    public String getProtocol() {
        return protocol;
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
     * Returns the port.
     *
     * @return the port
     */
    public String getPort() {
        return port;
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
     * Returns the password.
     *
     * @return the password
     */
    public String getPassword() {
        return password;
    }

    /**
     * @return the refreshRateEvents
     */
    public int getRefreshRateEvents() {
        return refreshRateEvents;
    }

    @Override
    public String toString() {
        return "Config [protocol=" + protocol + ", host=" + host + ", port=" + port + ", username=" + username
                + ", password=" + "********, refreshRateEvents=" + String.valueOf(refreshRateEvents) + "]";
    }
}
