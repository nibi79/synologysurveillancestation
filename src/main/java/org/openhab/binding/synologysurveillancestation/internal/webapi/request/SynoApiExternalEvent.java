/*
 * Copyright (c) 2010-2026 Contributors to the openHAB project
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
import org.openhab.binding.synologysurveillancestation.internal.webapi.response.CameraResponse;

/**
 * SYNO.SurveillanceStation.ExternalEvent
 *
 * External event related WebAPI. The method “Trigger” is implemented.
 *
 * @author Pavion - Initial Contribution
 *
 */
@NonNullByDefault
public class SynoApiExternalEvent extends SynoApiRequest<CameraResponse> {
    // private final Logger logger = LoggerFactory.getLogger(SynoApiExternalEvent.class);

    // API configuration
    private static final String API_NAME = "SYNO.SurveillanceStation.ExternalEvent";
    private static final SynoApiConfig API_CONFIG = new SynoApiConfig(API_NAME, API_VERSION_01, API_SCRIPT_ENTRY);

    /**
     * @param config
     */
    public SynoApiExternalEvent(SynoConfig config, HttpClient httpClient) {
        super(API_CONFIG, config, httpClient);
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
