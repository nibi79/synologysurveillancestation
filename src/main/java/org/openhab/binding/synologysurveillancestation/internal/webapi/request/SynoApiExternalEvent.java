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

import org.eclipse.jetty.client.HttpClient;
import org.openhab.binding.synologysurveillancestation.internal.SynoConfig;
import org.openhab.binding.synologysurveillancestation.internal.webapi.WebApiException;
import org.openhab.binding.synologysurveillancestation.internal.webapi.response.CameraResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * SYNO.SurveillanceStation.ExternalEvent
 *
 * External event related WebAPI. The method “Trigger” is implemented.
 *
 * @author Pavion - Initial Contribution
 *
 */
public class SynoApiExternalEvent extends SynoApiRequest<CameraResponse> {
    private final Logger logger = LoggerFactory.getLogger(SynoApiExternalEvent.class);

    // API configuration
    private static final String API_NAME = "SYNO.SurveillanceStation.ExternalEvent";
    private static final SynoApiConfig API_CONFIG = new SynoApiConfig(API_NAME, API_VERSION_01, API_SCRIPT_ENTRY);

    /**
     * @param config
     */
    public SynoApiExternalEvent(SynoConfig config, String sessionID, HttpClient httpClient) {
        super(API_CONFIG, config, sessionID, httpClient);
    }

    /**
     * Triggers the external event 1 to 10
     *
     * @param event External event to be triggered (1 to 10)
     * @return
     * @throws WebApiException
     */
    public boolean triggerEvent(int event) throws WebApiException {
        Map<String, String> params = new HashMap<>();

        // API parameters
        params.put("eventId", String.valueOf(event));
        return callApi(METHOD_TRIGGER, params).isSuccess();
    }

}
