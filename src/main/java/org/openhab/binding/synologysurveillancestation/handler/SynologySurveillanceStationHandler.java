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
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicBoolean;

import org.eclipse.jdt.annotation.NonNullByDefault;
import org.eclipse.jdt.annotation.Nullable;
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
    private @Nullable ScheduledFuture<?> snapshotJob;

    /**
     * Defines a runnable for a discovery
     */
    Runnable runnable = new Runnable() {
        @Override
        public void run() {
            try {
                refreshImage();
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

            String cameraId = getThing().getUID().getId();

            switch (channelUID.getId()) {
                case CHANNEL_IMAGE:
                    if (command.toString().equals("REFRESH")) {
                        refreshImage();
                    }
                    break;
                case CHANNEL_MOTION_DETECTED:
                case CHANNEL_ALARM_DETECTED:
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
        stopRefreshImage();
    }

    @Override
    public void initialize() {

        if (getBridge() != null) {

            SynologySurveillanceStationBridgeHandler bridge = ((SynologySurveillanceStationBridgeHandler) getBridge()
                    .getHandler());
            apiHandler = bridge.getSynoWebApiHandler();

            String cameraId = getThing().getUID().getId();

            logger.debug("Initializing SynologySurveillanceStationHandler for cameraId '{}'", cameraId);

            if (getBridge().getStatus() == ThingStatus.ONLINE) {
                updateStatus(ThingStatus.ONLINE);
                startRefreshImage();

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
    private void stopRefreshImage() {
        if (snapshotJob != null) {
            snapshotJob.cancel(true);
            try {
                Thread.sleep(1000);
            } catch (InterruptedException e) {

            }
        }
    }

    /**
     * Starts the refresh thread with refresh rate of the bridge
     */
    private void startRefreshImage() {
        // TODO: Changing bridge configuration should restart this thread to apply new refresh rate
        if (getBridge() != null) {
            int refresh = Integer.parseInt(
                    getBridge().getConfiguration().get(SynologySurveillanceStationBindingConstants.POLL).toString());
            snapshotJob = scheduler.scheduleAtFixedRate(runnable, 0, refresh, TimeUnit.SECONDS);
        }
    }

    private void refreshImage() {
        if (isLinked(CHANNEL_IMAGE)) {
            if (refreshInProgress.compareAndSet(false, true)) {
                Channel cx = getThing().getChannel(CHANNEL_IMAGE);
                if (logger.isTraceEnabled()) {
                    logger.trace("Will update: {}::{}::{}", getThing().getUID().getId(), cx.getChannelTypeUID().getId(),
                            getThing().getLabel());
                }

                try {
                    String cameraId = getThing().getUID().getId();

                    byte[] snapshot = apiHandler.getSnapshot(cameraId).toByteArray();

                    updateState(cx.getUID(), new RawType(snapshot, "image/jpeg"));

                    if (!thing.getStatus().equals(ThingStatus.ONLINE)) {
                        updateStatus(ThingStatus.ONLINE);
                    }

                } catch (URISyntaxException | IOException | WebApiException e) {
                    logger.error("could not get snapshot: {}", getThing(), e);
                    updateStatus(ThingStatus.OFFLINE, ThingStatusDetail.COMMUNICATION_ERROR,
                            "communication error: " + e.toString());
                }

                refreshInProgress.set(false);
            }
        }
    }
}
