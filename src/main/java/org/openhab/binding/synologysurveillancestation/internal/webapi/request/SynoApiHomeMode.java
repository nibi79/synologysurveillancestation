/**
 * Copyright (c) 2010-2017 by the respective copyright holders.
 *
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 */
package org.openhab.binding.synologysurveillancestation.internal.webapi.request;

import java.util.HashMap;
import java.util.Map;

import org.openhab.binding.synologysurveillancestation.internal.SynoConfig;
import org.openhab.binding.synologysurveillancestation.internal.webapi.WebApiException;
import org.openhab.binding.synologysurveillancestation.internal.webapi.response.HomeModeResponse;
import org.openhab.binding.synologysurveillancestation.internal.webapi.response.SimpleResponse;

/**
 * SYNO.SurveillanceStation.SynoApiHomeMode
 *
 * This API provides a method to acquire Surveillance Station Home Mode state
 *
 * @author Pavion
 *
 */
public class SynoApiHomeMode extends SynoApiRequest<HomeModeResponse> {
    private static final String API_NAME = "SYNO.SurveillanceStation.HomeMode";
    private static final SynoApiConfig apiConfig = new SynoApiConfig(API_NAME, API_VERSION_01, API_SCRIPT_ENTRY);

    /**
     * @param config
     */
    public SynoApiHomeMode(SynoConfig config, String sessionID) {
        super(apiConfig, config, sessionID);
    }

    /**
     * Get API events
     *
     * @return
     * @throws WebApiException
     */
    public HomeModeResponse query() {
        try {
            Map<String, String> params = new HashMap<>();
            return callApi(METHOD_GETINFO, params);
        } catch (WebApiException e) {
            return new HomeModeResponse("{\"data\":{},\"success\":false}");
        }
    }

    /**
     *
     * @param mode
     * @return
     * @throws WebApiException
     */
    public SimpleResponse setHomeMode(boolean mode) throws WebApiException {
        Map<String, String> params = new HashMap<>();
        params.put("on", mode ? "true" : "false");
        return callApi(METHOD_SWITCH, params);
    }
}
