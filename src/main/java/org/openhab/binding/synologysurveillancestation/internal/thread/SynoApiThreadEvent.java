/**
 * Copyright (c) 2010-2017 by the respective copyright holders.
 *
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 */
package org.openhab.binding.synologysurveillancestation.internal.thread;

import java.time.ZonedDateTime;
import java.util.HashMap;
import java.util.Map;

import org.eclipse.jdt.annotation.NonNullByDefault;
import org.eclipse.smarthome.core.library.types.OnOffType;
import org.eclipse.smarthome.core.thing.Channel;
import org.eclipse.smarthome.core.thing.Thing;
import org.openhab.binding.synologysurveillancestation.handler.SynoCameraHandler;
import org.openhab.binding.synologysurveillancestation.internal.webapi.SynoEvent;
import org.openhab.binding.synologysurveillancestation.internal.webapi.WebApiException;
import org.openhab.binding.synologysurveillancestation.internal.webapi.response.EventResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @author Pavion
 *
 */
@NonNullByDefault
public class SynoApiThreadEvent extends SynoApiThread {
    private final Logger logger = LoggerFactory.getLogger(SynoApiThreadEvent.class);

    private long lastEventTime;
    private Map<String, SynoEvent> events = new HashMap<>();

    public SynoApiThreadEvent(SynoCameraHandler handler, int refreshRate) {
        super(SynoApiThread.THREAD_EVENT, handler, refreshRate);
        lastEventTime = ZonedDateTime.now().minusSeconds(refreshRate * 2).toEpochSecond();
    }

    @Override
    public boolean isNeeded() {
        return (!events.isEmpty());
    }

    @Override
    public boolean refresh() {
        Thing thing = getAsCameraHandler().getThing();
        try {
            EventResponse response = getApiHandler().getEventResponse(getAsCameraHandler().getCameraId(), lastEventTime,
                    events);
            if (response.isSuccess()) {
                for (String eventType : events.keySet()) {
                    SynoEvent event = events.get(eventType);
                    Channel channel = getAsCameraHandler().getThing().getChannel(eventType);
                    if (response.hasEvent(event.getReason())) {
                        SynoEvent responseEvent = response.getEvent(event.getReason());
                        if (responseEvent.getEventId() != event.getEventId()) {
                            event.setEventId(responseEvent.getEventId());
                            event.setEventCompleted(responseEvent.isEventCompleted());
                            getAsCameraHandler().updateState(channel.getUID(), OnOffType.ON);
                            if (responseEvent.isEventCompleted()) {
                                getAsCameraHandler().updateState(channel.getUID(), OnOffType.OFF);
                            }
                        } else if (responseEvent.getEventId() == event.getEventId() && responseEvent.isEventCompleted()
                                && !event.isEventCompleted()) {
                            event.setEventCompleted(true);
                            getAsCameraHandler().updateState(channel.getUID(), OnOffType.OFF);
                        }
                    } else {
                        event.setEventCompleted(true);
                        getAsCameraHandler().updateState(channel.getUID(), OnOffType.OFF);
                    }
                }

                lastEventTime = response.getTimestamp();
                return true;
            } else {
                return false;
            }

        } catch (WebApiException | NullPointerException e) {
            logger.error("could not get event {}: {}", thing, e);
            return false;
        }
    }

    /**
     * @return the events
     */
    public Map<String, SynoEvent> getEvents() {
        return events;
    }

}
