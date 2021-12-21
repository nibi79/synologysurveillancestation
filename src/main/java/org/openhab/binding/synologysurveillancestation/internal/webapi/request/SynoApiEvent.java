/**
 * Copyright (c) 2010-2021 Contributors to the openHAB project
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
import java.util.StringJoiner;

import org.eclipse.jdt.annotation.NonNullByDefault;
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
@NonNullByDefault
public class SynoApiEvent extends SynoApiRequest<EventResponse> {

    // API Configuration
    private static final String API_NAME = "SYNO.SurveillanceStation.Event";
    private static final SynoApiConfig API_CONFIG = new SynoApiConfig(API_NAME, API_VERSION_05, API_SCRIPT_ENTRY);

    /**
     * @param config
     */
    public SynoApiEvent(SynoConfig config, HttpClient httpClient) {
        super(API_CONFIG, config, httpClient);
    }

    /**
     * Get API events
     *
     * @return
     * @throws WebApiException
     */
    public EventResponse getEventResponse(String cameraId, long lastEventTime, Map<String, SynoEvent> events) {
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
