/**
 * Copyright (c) 2010-2018 by the respective copyright holders.
 *
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 */
package org.openhab.binding.synologysurveillancestation.internal.webapi.request;

import java.io.IOException;
import java.net.URISyntaxException;
import java.util.HashMap;
import java.util.Map;

import org.eclipse.jetty.client.HttpClient;
import org.openhab.binding.synologysurveillancestation.internal.SynoConfig;
import org.openhab.binding.synologysurveillancestation.internal.webapi.WebApiException;
import org.openhab.binding.synologysurveillancestation.internal.webapi.response.LiveUriResponse;

/**
 * SYNO.SurveillanceStation.LiveUri
 *
 * This API provides a set of methods to acquire camera live feed URIs
 *
 *
 * @author Pavion
 *
 */
public class SynoApiLiveUri extends SynoApiRequest<LiveUriResponse> {
    // API configuration
    private static final String API_NAME = "SYNO.SurveillanceStation.Camera";
    private static final SynoApiConfig apiConfig = new SynoApiConfig(API_NAME, API_VERSION_09, API_SCRIPT_ENTRY);

    /**
     * @param config
     */
    public SynoApiLiveUri(SynoConfig config, String sessionID, HttpClient httpClient) {
        super(apiConfig, config, sessionID, httpClient);
    }

    /**
     * Get live URIs of the selected camera's live feed
     *
     * @throws WebApiException
     * @throws IOException
     * @throws UnsupportedOperationException
     * @throws URISyntaxException
     *
     */
    public LiveUriResponse getLiveUriResponse(String cameraId) throws WebApiException {
        Map<String, String> params = new HashMap<>();
        params.put("idList", cameraId);

        return callApi(METHOD_LIVEVIEWPATH, params);
    }

}
