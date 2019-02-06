/**
 * Copyright (c) 2010-2019 by the respective copyright holders.
 *
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 */
package org.openhab.binding.synologysurveillancestation.internal.thread;

import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicBoolean;

import org.eclipse.jdt.annotation.NonNullByDefault;
import org.eclipse.jdt.annotation.Nullable;
import org.eclipse.smarthome.core.thing.ThingStatus;
import org.eclipse.smarthome.core.thing.ThingStatusDetail;
import org.eclipse.smarthome.core.thing.binding.BaseThingHandler;
import org.openhab.binding.synologysurveillancestation.handler.SynoBridgeHandler;
import org.openhab.binding.synologysurveillancestation.handler.SynoCameraHandler;
import org.openhab.binding.synologysurveillancestation.handler.SynoHandler;
import org.openhab.binding.synologysurveillancestation.internal.webapi.WebApiException;
import org.openhab.binding.synologysurveillancestation.internal.webapi.error.WebApiAuthErrorCodes;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * The {@link SynoApiThread} is an abstract class for thread management (events, snapshot and so on)
 *
 * @author Pavion - Initial contribution
 */
@NonNullByDefault
public abstract class SynoApiThread<T extends BaseThingHandler & SynoHandler> {
    private final Logger logger = LoggerFactory.getLogger(SynoApiThread.class);

    /**
     * Thread types. Each thread has its refresh rate and polls the Station for events
     */
    public static final String THREAD_SNAPSHOT = "Snapshot";
    public static final String THREAD_EVENT = "Event";
    public static final String THREAD_CAMERA = "Camera";
    public static final String THREAD_HOMEMODE = "HomeMode";
    public static final String THREAD_LIVEURI = "LiveUri";

    private final AtomicBoolean refreshInProgress = new AtomicBoolean(false);
    private @Nullable ScheduledFuture<?> future;
    private int refreshRate; // Refresh rate in seconds
    private final T synoHandler; // Bridge or Camera Thing handler
    private final String name; // Thread name / type
    private final String deviceId; // Thread name / type

    /**
     * Defines a runnable for a refresh job
     */
    private Runnable runnable = new Runnable() {
        @Override
        public void run() {
            try {
                if (refreshInProgress.compareAndSet(false, true)) {
                    runOnce();
                    refreshInProgress.set(false);
                }
            } catch (IllegalStateException e) {
                logger.debug("Thread {}: Refreshing Thing failed, handler might be OFFLINE", name);
            } catch (Exception e) {
                logger.error("Thread {}: Unknown error", name, e);
            }
        }
    };

    /**
     * Main constructor
     *
     * @param threadId    ID of this thread for logging purposes
     * @param refreshRate Refresh rate of this thread in seconds
     * @param handler     Camera or bridge handler
     */
    public SynoApiThread(String name, T synoHandler, int refreshRate) {
        this.name = name;
        this.synoHandler = synoHandler;
        this.refreshRate = refreshRate;
        this.deviceId = synoHandler.getThing().getProperties().getOrDefault("deviceID", "Bridge");
    }

    /**
     * Starts the refresh job
     */
    public void start() {
        if (refreshRate > 0) {
            ScheduledExecutorService scheduler = synoHandler.getScheduler();

            if (scheduler != null) {
                if (this.name == THREAD_SNAPSHOT) {
                    future = scheduler.scheduleAtFixedRate(runnable, 0, refreshRate, TimeUnit.SECONDS);
                } else {
                    future = scheduler.scheduleWithFixedDelay(runnable, 0, refreshRate, TimeUnit.SECONDS);
                }
            }
        }
    }

    /**
     * Stops the refresh job
     */
    public void stop() {
        if (future != null) {
            future.cancel(false);
            try {
                Thread.sleep(1000);
            } catch (InterruptedException e) {
            }
        }
    }

    /**
     * Abstract dummy for a refresh function
     */
    public abstract boolean refresh() throws Exception;

    /**
     * Run the runnable just once (for manual refresh)
     */
    public void runOnce() {
        if (getSynoHandler().getSynoWebApiHandler() == null) {
            logger.error("DeviceId: {}; Thread: {}; Handler not (yet) initialized", deviceId, name);
        } else if (isNeeded()) {
            boolean success = false;
            try {
                success = refresh();
            } catch (WebApiException e) {
                if (e.getCause() instanceof java.util.concurrent.TimeoutException) {
                    logger.debug(
                            "DeviceId: {}; {} API timeout, consider to increase refresh rate ({} s) if seen frequently",
                            deviceId, name, refreshRate);
                    success = true;
                } else if (e.getErrorCode() == WebApiAuthErrorCodes.INSUFFICIENT_USER_PRIVILEGE.getCode()
                        || e.getErrorCode() == WebApiAuthErrorCodes.UNKNOWN_ERROR_119.getCode()) {
                    logger.debug("DeviceId: {}; Thread: {}; SID expired, trying to reconnect", deviceId, name);
                    try {
                        getSynoHandler().reconnect(true);
                    } catch (WebApiException ee) {
                        logger.error("DeviceId: {}; Thread: {}; Attempt to reconnect failed", deviceId, name);
                    }
                } else {
                    logger.error("DeviceId: {}; Thread: {}; Handler gone offline", deviceId, name);
                }
            } catch (Exception e) {
                logger.error("DeviceId: {}; Thread: {}; Critical error:\n", deviceId, name, e);
            }

            updateStatus(success);
        }
    }

    /**
     * Update handler status on runnable feedback
     *
     * @param success if runnable was successful
     */
    private void updateStatus(boolean success) {
        if (success && !synoHandler.getThing().getStatus().equals(ThingStatus.ONLINE)) {
            if (synoHandler instanceof SynoCameraHandler) {
                ((SynoCameraHandler) synoHandler).updateStatus(ThingStatus.ONLINE);
            } else if (synoHandler instanceof SynoBridgeHandler) {
                ((SynoBridgeHandler) synoHandler).updateStatus(ThingStatus.ONLINE);
            }
        } else if (!success && synoHandler.getThing().getStatus().equals(ThingStatus.ONLINE)) {
            if (synoHandler instanceof SynoCameraHandler) {
                ((SynoCameraHandler) synoHandler).updateStatus(ThingStatus.OFFLINE,
                        ThingStatusDetail.COMMUNICATION_ERROR, "Thread " + name);
            } else if (synoHandler instanceof SynoBridgeHandler) {
                ((SynoBridgeHandler) synoHandler).updateStatus(ThingStatus.OFFLINE,
                        ThingStatusDetail.COMMUNICATION_ERROR, "Thread " + name);
            }
        }

    }

    /**
     * @return the refreshRate
     */
    public int getRefreshRate() {
        return refreshRate;
    }

    /**
     * @param refreshRate The refreshRate to be set
     */
    public void setRefreshRate(int refreshRate) {
        if (this.refreshRate != refreshRate) {
            this.refreshRate = refreshRate;
            stop();
            start();
        }
    }

    /**
     * @return the SynoCameraHandler
     */
    public T getSynoHandler() {
        return synoHandler;
    }

    /**
     * @return if thread has to be run
     */
    public abstract boolean isNeeded();

}
