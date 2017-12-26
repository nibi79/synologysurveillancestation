/**
 * Copyright (c) 2010-2017 by the respective copyright holders.
 *
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 */
package org.openhab.binding.synologysurveillancestation.handler;

import static org.openhab.binding.synologysurveillancestation.SynologySurveillanceStationBindingConstants.*;

import java.io.IOException;
import java.net.URISyntaxException;
import java.time.ZonedDateTime;
import java.util.Map;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicBoolean;

import org.eclipse.jdt.annotation.NonNullByDefault;
import org.eclipse.jdt.annotation.Nullable;
import org.eclipse.smarthome.core.library.types.OnOffType;
import org.eclipse.smarthome.core.library.types.RawType;
import org.eclipse.smarthome.core.thing.Channel;
import org.eclipse.smarthome.core.thing.ChannelUID;
import org.eclipse.smarthome.core.thing.Thing;
import org.eclipse.smarthome.core.thing.ThingStatus;
import org.eclipse.smarthome.core.thing.ThingStatusDetail;
import org.eclipse.smarthome.core.thing.binding.BaseThingHandler;
import org.eclipse.smarthome.core.types.Command;
import org.openhab.binding.synologysurveillancestation.SynologySurveillanceStationBindingConstants;
import org.openhab.binding.synologysurveillancestation.internal.webapi.SynoWebApiHandler;
import org.openhab.binding.synologysurveillancestation.internal.webapi.WebApiException;
import org.openhab.binding.synologysurveillancestation.internal.webapi.response.EventResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * The {@link SynologySurveillanceStationHandler} is responsible for handling commands, which are
 * sent to one of the channels.
 *
 * @author Nils
 */
@NonNullByDefault
public class SynologySurveillanceStationHandler extends BaseThingHandler {

    private final Logger logger = LoggerFactory.getLogger(SynologySurveillanceStationHandler.class);
    private final AtomicBoolean refreshInProgress = new AtomicBoolean(false);
    private @Nullable SynoWebApiHandler apiHandler;
    private @Nullable ScheduledFuture<?> refreshJob;
    private String cameraId = "";
    private int refresh = 5;

    private long lastEventTime = 1513758653;
    private long motionId = -1;
    private long alarmId = -1;
    private boolean motionCompleted = true;
    private boolean alarmCompleted = true;

    /**
     * Defines a runnable for a refresh job
     */
    Runnable runnable = new Runnable() {
        @Override
        public void run() {
            try {
                refresh();
            } catch (Exception e) {
                logger.error("error in refresh", e);
            }
        }
    };

    public SynologySurveillanceStationHandler(Thing thing) {
        super(thing);
    }

    @Override
    public boolean isLinked(String channelId) {
        return super.isLinked(channelId);
    }

    @Override
    public void handleCommand(ChannelUID channelUID, Command command) {
        try {
            switch (channelUID.getId()) {
                case CHANNEL_IMAGE:
                    if (command.toString().equals("REFRESH")) {
                        refresh();
                    }
                    break;
                case CHANNEL_MOTION_DETECTED:
                case CHANNEL_ALARM_DETECTED:
                    if (command.toString().equals("REFRESH")) {
                        updateState(channelUID, OnOffType.OFF);
                    }
                    break;
                case CHANNEL_RECORD:
                case CHANNEL_ENABLE:
                case CHANNEL_ZOOM:
                case CHANNEL_MOVE:
                    apiHandler.execute(cameraId, channelUID.getId(), command.toString());
                    break;
            }
        } catch (WebApiException e) {
            logger.error("handle command: {}::{}::{}", getThing().getLabel(), getThing().getUID());
        }

    }

    @Override
    public void dispose() {
        stopRefresh();
    }

    @Override
    public void initialize() {
        System.err.println("Handle Thing initialization");

        if (getBridge() != null) {

            SynologySurveillanceStationBridgeHandler bridge = ((SynologySurveillanceStationBridgeHandler) getBridge()
                    .getHandler());
            apiHandler = bridge.getSynoWebApiHandler();
            cameraId = getThing().getUID().getId();
            refresh = Integer.parseInt(
                    getBridge().getConfiguration().get(SynologySurveillanceStationBindingConstants.POLL).toString());
            lastEventTime = ZonedDateTime.now().minusSeconds(refresh).toEpochSecond();

            logger.debug("Initializing SynologySurveillanceStationHandler for cameraId '{}'", cameraId);

            if (getBridge().getStatus() == ThingStatus.ONLINE) {
                updateStatus(ThingStatus.ONLINE);
                startRefresh();

            } else {
                updateStatus(ThingStatus.OFFLINE, ThingStatusDetail.OFFLINE.BRIDGE_OFFLINE);
            }
        } else {
            updateStatus(ThingStatus.OFFLINE);
        }

        if (logger.isDebugEnabled()) {
            logger.debug("Initialize thing: {}::{}", getThing().getLabel(), getThing().getUID());
        }

    }

    /**
     * Stops the refresh thread
     */
    private void stopRefresh() {
        if (refreshJob != null) {
            refreshJob.cancel(true);
            try {
                Thread.sleep(1000);
            } catch (InterruptedException e) {

            }
        }
    }

    /**
     * Starts the refresh thread with refresh rate of the bridge
     */
    private void startRefresh() {
        if (getBridge() != null) {
            refreshJob = scheduler.scheduleAtFixedRate(runnable, 0, refresh, TimeUnit.SECONDS);
        }
    }

    // TODO: allow manual image refresh without refreshing events?
    private void refresh() {
        if (refreshInProgress.compareAndSet(false, true)) {
            if (isLinked(CHANNEL_IMAGE)) {
                Channel cx = getThing().getChannel(CHANNEL_IMAGE);
                if (logger.isTraceEnabled()) {
                    logger.trace("Will update: {}::{}::{}", getThing().getUID().getId(), cx.getChannelTypeUID().getId(),
                            getThing().getLabel());
                }

                try {
                    byte[] snapshot = apiHandler.getSnapshot(cameraId).toByteArray();
                    updateState(cx.getUID(), new RawType(snapshot, "image/jpeg"));
                    if (!thing.getStatus().equals(ThingStatus.ONLINE)) {
                        updateStatus(ThingStatus.ONLINE);
                    }

                } catch (URISyntaxException | IOException | WebApiException | NullPointerException e) {
                    logger.error("could not get snapshot: {}", getThing(), e);
                    updateStatus(ThingStatus.OFFLINE, ThingStatusDetail.COMMUNICATION_ERROR,
                            "communication error: " + e.toString());
                }

            }

            if (isLinked(CHANNEL_MOTION_DETECTED) || isLinked(CHANNEL_ALARM_DETECTED)) {
                try {
                    EventResponse response = apiHandler.getEventResponse(cameraId, lastEventTime);
                    if (response.isSuccess()) {
                        if (isLinked(CHANNEL_ALARM_DETECTED)) {
                            Channel ca = getThing().getChannel(CHANNEL_ALARM_DETECTED);
                            if (response.isAlarm()) {
                                if (response.getAlarmId() > alarmId) {
                                    alarmId = response.getAlarmId();
                                    alarmCompleted = response.isAlarmCompleted();
                                    updateState(ca.getUID(), OnOffType.ON);
                                    if (alarmCompleted) {
                                        updateState(ca.getUID(), OnOffType.OFF);
                                    }
                                } else if (response.getAlarmId() == alarmId && response.isAlarmCompleted()
                                        && !alarmCompleted) {
                                    alarmCompleted = true;
                                    updateState(ca.getUID(), OnOffType.OFF);
                                }
                            } else {
                                alarmCompleted = true;
                                updateState(ca.getUID(), OnOffType.OFF);
                            }
                        }

                        if (isLinked(CHANNEL_MOTION_DETECTED)) {
                            Channel cm = getThing().getChannel(CHANNEL_MOTION_DETECTED);
                            if (response.isMotion()) {
                                if (response.getMotionId() > motionId) {
                                    motionId = response.getMotionId();
                                    motionCompleted = response.isMotionCompleted();
                                    updateState(cm.getUID(), OnOffType.ON);
                                    if (motionCompleted) {
                                        updateState(cm.getUID(), OnOffType.OFF);
                                    }
                                } else if (response.getMotionId() == motionId && response.isMotionCompleted()
                                        && !motionCompleted) {
                                    motionCompleted = true;
                                    updateState(cm.getUID(), OnOffType.OFF);
                                }
                            } else {
                                motionCompleted = true;
                                updateState(cm.getUID(), OnOffType.OFF);
                            }
                        }

                        lastEventTime = response.getTimestamp();
                        if (!thing.getStatus().equals(ThingStatus.ONLINE)) {
                            updateStatus(ThingStatus.ONLINE);
                        }
                    } else {
                        if (!thing.getStatus().equals(ThingStatus.OFFLINE)) {
                            updateStatus(ThingStatus.OFFLINE, ThingStatusDetail.COMMUNICATION_ERROR);
                        }
                    }

                } catch (WebApiException | NullPointerException e) {
                    logger.error("could not get event: {}", getThing(), e);
                    updateStatus(ThingStatus.OFFLINE, ThingStatusDetail.COMMUNICATION_ERROR,
                            "communication error: " + e.toString());
                }
            }

            refreshInProgress.set(false);
        }
    }

    /**
     * @return the refresh
     */
    public int getRefresh() {
        return refresh;
    }

    /**
     * @param refresh the refresh to set
     */
    public void setRefresh(int refresh) {
        this.refresh = refresh;
        stopRefresh();
        startRefresh();
    }

    public void handleConfigurationUpdate() {
        dispose();
        initialize();
    }

    @Override
    public void handleConfigurationUpdate(Map<String, Object> configurationParameters) {
        super.handleConfigurationUpdate(configurationParameters);
    }

}
