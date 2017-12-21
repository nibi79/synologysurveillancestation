/**
 * Copyright (c) 2010-2017 by the respective copyright holders.
 *
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 */
package org.openhab.binding.synologysurveillancestation.internal.webapi.request;

import java.util.ArrayList;
import java.util.List;

import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;
import org.openhab.binding.synologysurveillancestation.internal.Config;
import org.openhab.binding.synologysurveillancestation.internal.webapi.WebApiException;
import org.openhab.binding.synologysurveillancestation.internal.webapi.response.EventResponse;

/**
 * SYNO.SurveillanceStation.SynoApiEvent
 *
 * This API provides a method to acquire Surveillance Station event information as displayed in Timeline
 *
 * @author Pavion
 *
 */
public class SynoApiEvent extends SynoApiRequest<EventResponse> {

    // API Configuration
    private static final String API_NAME = "SYNO.SurveillanceStation.Event";
    private static final SynoApiConfig apiConfig = new SynoApiConfig(API_NAME, API_VERSION_05, API_SCRIPT_ENTRY);
    public static final int EVENT_REASON_MOTION = 2;
    public static final int EVENT_REASON_ALARM = 3;

    /**
     * @param config
     */
    public SynoApiEvent(Config config, String sessionID) {
        super(apiConfig, config, sessionID);
    }

    /**
     * Get API information
     *
     * @return
     * @throws WebApiException
     */
    public EventResponse query(String cameraId, long lastEventTime) {
        List<NameValuePair> params = new ArrayList<>();

        params.add(new BasicNameValuePair("cameraIds", cameraId));
        params.add(new BasicNameValuePair("fromTime", String.valueOf(lastEventTime)));
        params.add(new BasicNameValuePair("blIncludeSnapshot", "false"));
        params.add(new BasicNameValuePair("limit", "10"));
        params.add(new BasicNameValuePair("reason", "2,3"));

        try {
            return callApi(METHOD_LIST, params);
        } catch (WebApiException e) {
            return new EventResponse("{\"data\":{},\"success\":false}");
        }
    }
}
