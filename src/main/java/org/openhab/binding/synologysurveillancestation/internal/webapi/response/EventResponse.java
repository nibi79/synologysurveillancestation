/**
 * Copyright (c) 2010-2017 by the respective copyright holders.
 *
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 */
package org.openhab.binding.synologysurveillancestation.internal.webapi.response;

import org.eclipse.jdt.annotation.NonNullByDefault;
import org.openhab.binding.synologysurveillancestation.internal.webapi.request.SynoApiEvent;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;

/**
 * @author Pavion
 *
 */
@NonNullByDefault
public class EventResponse extends SimpleResponse {
    private final Logger logger = LoggerFactory.getLogger(EventResponse.class);

    private long timestamp = 0;

    private boolean motion = false;
    private long motionId = -1;
    private boolean motionCompleted = true;

    private boolean alarm = false;
    private long alarmId = -1;
    private boolean alarmCompleted = true;

    /**
     * @param jsonResponse
     */
    public EventResponse(String jsonResponse) {
        super(jsonResponse);

        JsonArray events = getData().getAsJsonArray("events");

        timestamp = getData().getAsJsonObject().get("timestamp").getAsLong();

        for (JsonElement event : events) {
            if (event.isJsonObject()) {
                JsonObject cam = event.getAsJsonObject();
                int reason = cam.get("reason").getAsInt();
                if (reason == SynoApiEvent.EVENT_REASON_ALARM && !alarm) {
                    alarm = true;
                    alarmId = cam.get("eventId").getAsLong();
                    alarmCompleted = cam.get("is_complete").getAsBoolean();
                } else if (reason == SynoApiEvent.EVENT_REASON_MOTION && !motion) {
                    motion = true;
                    motionId = cam.get("eventId").getAsLong();
                    motionCompleted = cam.get("is_complete").getAsBoolean();
                }
            }
        }

    }

    /**
     * @return the timestamp
     */
    public long getTimestamp() {
        return timestamp;
    }

    /**
     * @return the motionId
     */
    public long getMotionId() {
        return motionId;
    }

    /**
     * @return the alarmId
     */
    public long getAlarmId() {
        return alarmId;
    }

    /**
     * @return the motionCompleted
     */
    public boolean isMotionCompleted() {
        return motionCompleted;
    }

    /**
     * @return the alarmCompleted
     */
    public boolean isAlarmCompleted() {
        return alarmCompleted;
    }

    /**
     * @return the motion
     */
    public boolean isMotion() {
        return motion;
    }

    /**
     * @return the alarm
     */
    public boolean isAlarm() {
        return alarm;
    }

}
