/**
 * Copyright (c) 2010-2019 by the respective copyright holders.
 *
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
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
