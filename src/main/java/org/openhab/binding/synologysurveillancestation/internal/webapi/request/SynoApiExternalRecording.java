/**
 * Copyright (c) 2010-2019 by the respective copyright holders.
 *
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 */
package org.openhab.binding.synologysurveillancestation.internal.webapi.request;

import java.util.HashMap;
import java.util.Map;

import org.eclipse.jdt.annotation.NonNullByDefault;
import org.eclipse.jetty.client.HttpClient;
import org.openhab.binding.synologysurveillancestation.internal.SynoConfig;
import org.openhab.binding.synologysurveillancestation.internal.webapi.WebApiException;
import org.openhab.binding.synologysurveillancestation.internal.webapi.response.SimpleResponse;

/**
 * SYNO.SurveillanceStation.ExternalRecording
 *
 * This API provides methods to start or stop external recording of a camera.
 *
 * Method:
 * - Record
 *
 * @author Nils - Initial contribution
 * @author Pavion - Contribution
 */
@NonNullByDefault
public class SynoApiExternalRecording extends SynoApiRequest<SimpleResponse> {

    // API configuration
    private static final String API_NAME = "SYNO.SurveillanceStation.ExternalRecording";
    private static final SynoApiConfig API_CONFIG = new SynoApiConfig(API_NAME, API_VERSION_02, API_SCRIPT_ENTRY);

    /**
     * @param config
     */
    public SynoApiExternalRecording(SynoConfig config, HttpClient httpClient) {
        super(API_CONFIG, config, httpClient);
    }

    /**
     * @param method
     * @param cameraId
     * @param action
     * @return
     * @throws WebApiException
     */
    private SimpleResponse call(String method, String cameraId, String action) throws WebApiException {
        Map<String, String> params = new HashMap<>();

        // API parameters
        params.put("cameraId", cameraId);
        params.put("action", action);

        return callApi(method, params);
    }

    /**
     * Toggle external recording of a camera.
     *
     * @param cameraId
     * @return
     * @throws WebApiException
     */
    public SimpleResponse toggleRecording(String cameraId, boolean on) throws WebApiException {
        return call(METHOD_RECORD, cameraId, on ? "start" : "stop");
    }

}
