/**
 * Copyright (c) 2010-2018 by the respective copyright holders.
 *
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 */
package org.openhab.binding.synologysurveillancestation.internal.webapi.request;

import org.eclipse.jetty.client.HttpClient;
import org.openhab.binding.synologysurveillancestation.internal.SynoConfig;
import org.openhab.binding.synologysurveillancestation.internal.webapi.WebApiException;
import org.openhab.binding.synologysurveillancestation.internal.webapi.response.InfoResponse;

/**
 * SYNO.SurveillanceStation.Info
 *
 * This API provides a method to acquire Surveillance Station related information such as package version, package UI
 * path, and the total number of camera and installed licenses.
 *
 * Method:
 * - GetInfo
 *
 * @author Nils - Initial contribution
 * @author Pavion - Contribution
 */
public class SynoApiInfo extends SynoApiRequest<InfoResponse> {

    // API Configuration
    private static final String API_NAME = "SYNO.SurveillanceStation.Info";
    private static final SynoApiConfig API_CONFIG = new SynoApiConfig(API_NAME, API_VERSION_05, API_SCRIPT_ENTRY);

    /**
     * @param config
     */
    public SynoApiInfo(SynoConfig config, String sessionID, HttpClient httpClient) {
        super(API_CONFIG, config, sessionID, httpClient);
    }

    /**
     * Get Surveillance Station related general information.
     *
     * @return
     * @throws WebApiException
     */
    public InfoResponse getInfo() throws WebApiException {
        return callApi(METHOD_GETINFO);
    }
}
