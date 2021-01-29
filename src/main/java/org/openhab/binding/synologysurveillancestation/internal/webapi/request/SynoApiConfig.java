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
package org.openhab.binding.synologysurveillancestation.internal.webapi.request;

import org.eclipse.jdt.annotation.NonNullByDefault;

/**
 * API configuration parameters
 *
 * @author Nils - Initial contribution
 * @author Pavion - Contribution
 *
 */
@NonNullByDefault
public class SynoApiConfig {

    private final String name;
    private final String version;
    private final String scriptpath;

    /**
     * @param name
     * @param version
     * @param scriptpath
     */
    public SynoApiConfig(String name, String version, String scriptpath) {
        this.name = name;
        this.version = version;
        this.scriptpath = scriptpath;
    }

    /**
     *
     * @return
     */
    public final String getName() {
        return name;
    }

    /**
     * @return
     */
    public final String getVersion() {
        return version;
    }

    /**
     * @return
     */
    public final String getScriptpath() {
        return scriptpath;
    }
}
