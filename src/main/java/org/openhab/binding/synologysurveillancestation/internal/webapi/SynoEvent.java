/**
 * Copyright (c) 2010-2022 Contributors to the openHAB project
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
package org.openhab.binding.synologysurveillancestation.internal.webapi;

import org.eclipse.jdt.annotation.NonNullByDefault;

/**
 * {@link SynoEvent} stores events
 *
 * @author Pavion - Initial contribution
 */
@NonNullByDefault
public class SynoEvent {
    public static final int EVENT_REASON_CONTINUOUS = 1;
    public static final int EVENT_REASON_MOTION = 2;
    public static final int EVENT_REASON_ALARM = 3;
    public static final int EVENT_REASON_CUSTOM = 4;
    public static final int EVENT_REASON_MANUAL = 5;
    public static final int EVENT_REASON_EXTERNAL = 6;
    public static final int EVENT_REASON_ANALYTICS = 7;
    public static final int EVENT_REASON_EDGE = 8;
    public static final int EVENT_REASON_ACTIONRULE = 9;

    private boolean eventCompleted = true;
    private long eventId = -1;
    private final int reason;

    /**
     * Constructor for OH2 side
     *
     * @param reason
     */
    public SynoEvent(int reason) {
        this.reason = reason;
    }

    /**
     * Constructor for API side
     *
     * @param eventCompleted
     * @param eventId
     * @param reason
     */
    public SynoEvent(long eventId, boolean eventCompleted, int reason) {
        this.eventCompleted = eventCompleted;
        this.eventId = eventId;
        this.reason = reason;
    }

    /**
     * @return the eventCompleted
     */
    public boolean isEventCompleted() {
        return eventCompleted;
    }

    /**
     * @param eventCompleted the eventCompleted to set
     */
    public void setEventCompleted(boolean eventCompleted) {
        this.eventCompleted = eventCompleted;
    }

    /**
     * @return the eventId
     */
    public long getEventId() {
        return eventId;
    }

    /**
     * @param eventId the eventId to set
     */
    public void setEventId(long eventId) {
        this.eventId = eventId;
    }

    /**
     * @return the reason
     */
    public int getReason() {
        return reason;
    }
}
