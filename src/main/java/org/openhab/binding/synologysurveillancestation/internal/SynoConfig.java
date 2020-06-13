/**
 * Copyright (c) 2010-2020 Contributors to the openHAB project
 *
 * See the NOTICE file(s) distributed with this work for additional
 * information.
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License 2.0 which is available at
 * http://www.eclipse.org/legal/epl-2.0
 *
 * SPDX-License-Identifier: EPL-2.0
 */
package org.openhab.binding.synologysurveillancestation.internal;

import org.eclipse.jdt.annotation.NonNullByDefault;
import org.eclipse.jdt.annotation.Nullable;

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

    @Override
    public boolean equals(@Nullable Object obj) {
        if (obj == null) {
            return false;
        }
        if (!(obj instanceof SynoConfig)) {
            return false;
        }
        SynoConfig cfg = (SynoConfig) obj;
        return cfg.getHost().equals(getHost()) && cfg.getPassword().equals(getPassword())
                && cfg.getProtocol().equals(getProtocol()) && cfg.getPort().equals(getPort())
                && cfg.getUsername().equals(getUsername()) && cfg.getRefreshRateEvents() == refreshRateEvents;
    }
}
