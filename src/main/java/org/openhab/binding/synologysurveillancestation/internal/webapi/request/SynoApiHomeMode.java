/**
 * Copyright (c) 2010-2019 Contributors to the openHAB project
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

import java.util.HashMap;
import java.util.Map;

import org.eclipse.jdt.annotation.NonNullByDefault;
import org.eclipse.jetty.client.HttpClient;
import org.openhab.binding.synologysurveillancestation.internal.SynoConfig;
import org.openhab.binding.synologysurveillancestation.internal.webapi.WebApiException;
import org.openhab.binding.synologysurveillancestation.internal.webapi.response.HomeModeResponse;
import org.openhab.binding.synologysurveillancestation.internal.webapi.response.SimpleResponse;

/**
 * SYNO.SurveillanceStation.SynoApiHomeMode
 *
 * This API provides a method to acquire Surveillance Station Home Mode state
 *
 * @author Pavion - Initial contribution
 */
@NonNullByDefault
public class SynoApiHomeMode extends SynoApiRequest<HomeModeResponse> {
    private static final String API_NAME = "SYNO.SurveillanceStation.HomeMode";
    private static final SynoApiConfig API_CONFIG = new SynoApiConfig(API_NAME, API_VERSION_01, API_SCRIPT_ENTRY);

    /**
     * @param config
     */
    public SynoApiHomeMode(SynoConfig config, HttpClient httpClient) {
        super(API_CONFIG, config, httpClient);
    }

    /**
     * Get API events
     *
     * @return
     * @throws WebApiException
     */
    public HomeModeResponse getHomeModeResponse() {
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
