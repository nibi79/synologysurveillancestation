/**
 * Copyright (c) 2010-2018 by the respective copyright holders.
 *
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 */
package org.openhab.binding.synologysurveillancestation.internal.webapi.response;

import java.util.HashMap;
import java.util.Map;

import org.eclipse.jdt.annotation.NonNullByDefault;
import org.openhab.binding.synologysurveillancestation.internal.webapi.SynoEvent;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;

/**
 * {@link EventResponse} is a response with current events
 *
 * @author Pavion - Initial contribution
 */
@NonNullByDefault
public class EventResponse extends SimpleResponse {
    private static final int EVENT_POLL_OVERHEAD = 30;

    private Map<Integer, SynoEvent> synoEvents = new HashMap<>();
    private long timestamp = 0;

    /**
     * Constructs SynoEvents from JSON string.
     *
     * @param jsonResponse
     */
    public EventResponse(String jsonResponse) {
        super(jsonResponse);
        if (isSuccess()) {
            JsonArray events = getData().getAsJsonArray("events");
            timestamp = getData().getAsJsonObject().get("timestamp").getAsLong() - EVENT_POLL_OVERHEAD;
            for (JsonElement event : events) {
                if (event.isJsonObject()) {
                    JsonObject cam = event.getAsJsonObject();
                    int reason = cam.get("reason").getAsInt();
                    if (!hasEvent(reason)) {
                        long starttime = cam.get("startTime").getAsLong();
                        long eventId = cam.get("eventId").getAsLong();
                        boolean eventCompleted = cam.get("is_complete").getAsBoolean();
                        synoEvents.put(reason, new SynoEvent(eventId, eventCompleted, reason));
                        if (!eventCompleted && starttime < timestamp) {
                            timestamp = starttime;
                        }
                    }
                }
            }
        }
    }

    /**
     * @return if the event with specified reason exists
     */
    public boolean hasEvent(int eventReason) {
        return synoEvents.containsKey(eventReason);
    }

    /**
     * @return the event with specified reason
     */
    public SynoEvent getEvent(int eventReason) {
        return synoEvents.get(eventReason);
    }

    /**
     * @return the timestamp
     */
    public long getTimestamp() {
        return timestamp;
    }

}
