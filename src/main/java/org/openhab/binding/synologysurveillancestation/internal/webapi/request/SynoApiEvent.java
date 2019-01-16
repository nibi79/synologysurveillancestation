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
import java.util.StringJoiner;

import org.eclipse.jetty.client.HttpClient;
import org.openhab.binding.synologysurveillancestation.internal.SynoConfig;
import org.openhab.binding.synologysurveillancestation.internal.webapi.SynoEvent;
import org.openhab.binding.synologysurveillancestation.internal.webapi.WebApiException;
import org.openhab.binding.synologysurveillancestation.internal.webapi.response.EventResponse;

/**
 * SYNO.SurveillanceStation.SynoApiEvent
 *
 * This API provides a method to acquire Surveillance Station event information as displayed in Timeline
 *
 * @author Pavion - Initial contribution
 */
public class SynoApiEvent extends SynoApiRequest<EventResponse> {

    // API Configuration
    private static final String API_NAME = "SYNO.SurveillanceStation.Event";
    private static final SynoApiConfig API_CONFIG = new SynoApiConfig(API_NAME, API_VERSION_05, API_SCRIPT_ENTRY);

    /**
     * @param config
     */
    public SynoApiEvent(SynoConfig config, String sessionID, HttpClient httpClient) {
        super(API_CONFIG, config, sessionID, httpClient);
    }

    /**
     * Get API events
     *
     * @return
     * @throws WebApiException
     */
    public EventResponse query(String cameraId, long lastEventTime, Map<String, SynoEvent> events) {
        Map<String, String> params = new HashMap<>();

        params.put("cameraIds", cameraId);
        params.put("fromTime", String.valueOf(lastEventTime));
        params.put("blIncludeSnapshot", API_FALSE);
        params.put("limit", "25");

        StringJoiner reasons = new StringJoiner(",");
        for (SynoEvent event : events.values()) {
            reasons.add(String.valueOf(event.getReason()));
        }
        params.put("reason", reasons.toString());

        try {
            return callApi(METHOD_LIST, params);
        } catch (WebApiException e) {
            return new EventResponse("{\"data\":{},\"success\":false}");
        }
    }
}
