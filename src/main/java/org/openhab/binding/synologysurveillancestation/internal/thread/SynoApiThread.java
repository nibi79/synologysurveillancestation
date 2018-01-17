/**
 * Copyright (c) 2010-2018 by the respective copyright holders.
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
import org.eclipse.smarthome.core.thing.Bridge;
import org.eclipse.smarthome.core.thing.ThingStatus;
import org.eclipse.smarthome.core.thing.ThingStatusDetail;
import org.eclipse.smarthome.core.thing.binding.BaseThingHandler;
import org.openhab.binding.synologysurveillancestation.handler.SynoBridgeHandler;
import org.openhab.binding.synologysurveillancestation.handler.SynoCameraHandler;
import org.openhab.binding.synologysurveillancestation.internal.webapi.SynoWebApiHandler;
import org.openhab.binding.synologysurveillancestation.internal.webapi.WebApiException;
import org.openhab.binding.synologysurveillancestation.internal.webapi.request.SynoApi;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * The {@link SynoApiThread} is an abstract class for thread management (events, snapshot and so on)
 *
 * @author Pavion
 */
@NonNullByDefault
public abstract class SynoApiThread {
    private final Logger logger = LoggerFactory.getLogger(SynoApiThread.class);

    /**
     * Thread types. Each thread has its refresh rate and polls the Station for events
     */
    public static final String THREAD_SNAPSHOT = "Snapshot";
    public static final String THREAD_EVENT = "Event";
    public static final String THREAD_CAMERA = "Camera";
    public static final String THREAD_HOMEMODE = "HomeMode";

    private final AtomicBoolean refreshInProgress = new AtomicBoolean(false);
    private @Nullable ScheduledFuture<?> future;
    private int refreshRate; // Refresh rate in seconds
    private final BaseThingHandler handler; // Bridge or Camera Thing handler
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
     * @param threadId ID of this thread for logging purposes
     * @param refreshRate Refresh rate of this thread in seconds
     * @param handler Camera or bridge handler
     */
    public SynoApiThread(String name, BaseThingHandler handler, int refreshRate) {
        this.name = name;
        this.handler = handler;
        this.refreshRate = refreshRate;
        this.deviceId = handler.getThing().getProperties().getOrDefault("deviceID", "Bridge");
    }

    /**
     * Starts the refresh job
     */
    public void start() {
        if (refreshRate > 0) {
            ScheduledExecutorService scheduler = null;
            if (handler instanceof SynoCameraHandler) {
                scheduler = ((SynoCameraHandler) handler).getScheduler();
            } else if (handler instanceof SynoBridgeHandler) {
                scheduler = ((SynoBridgeHandler) handler).getScheduler();
            }
            if (scheduler != null) {
                future = scheduler.scheduleAtFixedRate(runnable, 0, refreshRate, TimeUnit.SECONDS);
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
        if (getApiHandler() == null) {
            logger.error("DeviceId: {}; Thread: {}; Handler not (yet) initialized", deviceId, name);
        } else if (isNeeded()) {
            boolean success = false;
            try {
                success = refresh();
            } catch (WebApiException e) {
                if (e.getCause() instanceof java.util.concurrent.TimeoutException) {
                    logger.error("DeviceId: {}; Thread: {}; Connection timeout ({} ms)", deviceId, name,
                            SynoApi.API_CONNECTION_TIMEOUT);
                } else {
                    logger.error("DeviceId: {}; Thread: {}; Handler gone offline", deviceId, name);
                }
            } catch (Exception e) {
                logger.error("DeviceId: {}; Thread: {}; Critical error:\n {}", deviceId, name, e);
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
        if (success && !handler.getThing().getStatus().equals(ThingStatus.ONLINE)) {
            if (handler instanceof SynoCameraHandler) {
                ((SynoCameraHandler) handler).updateStatus(ThingStatus.ONLINE);
            } else if (handler instanceof SynoBridgeHandler) {
                ((SynoBridgeHandler) handler).updateStatus(ThingStatus.ONLINE);
            }
        } else if (!success && handler.getThing().getStatus().equals(ThingStatus.ONLINE)) {
            if (handler instanceof SynoCameraHandler) {
                ((SynoCameraHandler) handler).updateStatus(ThingStatus.OFFLINE, ThingStatusDetail.COMMUNICATION_ERROR,
                        "Thread " + name);
            } else if (handler instanceof SynoBridgeHandler) {
                ((SynoBridgeHandler) handler).updateStatus(ThingStatus.OFFLINE, ThingStatusDetail.COMMUNICATION_ERROR,
                        "Thread " + name);
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
    public SynoCameraHandler getAsCameraHandler() {
        return (SynoCameraHandler) handler;
    }

    /**
     * @return the SynoBridgeHandler
     */
    public SynoBridgeHandler getAsBridgeHandler() {
        return (SynoBridgeHandler) handler;
    }

    /**
     * @return the API handler
     */
    public @Nullable SynoWebApiHandler getApiHandler() {
        if (handler instanceof SynoCameraHandler) {
            Bridge bridge = ((SynoCameraHandler) handler).getBridge();
            if (bridge != null) {
                SynoBridgeHandler bridgeHandler = ((SynoBridgeHandler) bridge.getHandler());
                if (bridgeHandler != null) {
                    return bridgeHandler.getSynoWebApiHandler();
                }
            }
        } else if (handler instanceof SynoBridgeHandler) {
            return ((SynoBridgeHandler) handler).getSynoWebApiHandler();
        }
        return null;
    }

    /**
     * @return if thread has to be run
     */
    public abstract boolean isNeeded();

}
