/**
 * Copyright (c) 2010-2019 by the respective copyright holders.
 *
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 */
package org.openhab.binding.synologysurveillancestation.internal.webapi;

import org.openhab.binding.synologysurveillancestation.internal.webapi.response.SimpleResponse;

/**
 * The {@link SynoWebApi} is an interface for the Web API
 *
 * @author Nils - Initial contribution
 * @author Pavion - Contribution
 */
public interface SynoWebApi {
    /**
     * Establish connection to Surveillance Station Web API
     *
     * @return
     * @throws WebApiException
     */
    public boolean connect(boolean forceLogout) throws WebApiException;

    /**
     *
     * @return
     * @throws WebApiException
     */
    public SimpleResponse disconnect() throws WebApiException;

    // ----------------------------

    /**
     * Returns true if connected and sid != null
     *
     * @return
     */
    public boolean isConnected();

}
