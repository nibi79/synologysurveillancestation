/**
 * Copyright (c) 2010-2018 by the respective copyright holders.
 *
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 */
package org.openhab.binding.synologysurveillancestation.handler;

import static org.openhab.binding.synologysurveillancestation.SynoBindingConstants.*;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ScheduledExecutorService;

import org.eclipse.jdt.annotation.NonNull;
import org.eclipse.jdt.annotation.NonNullByDefault;
import org.eclipse.jdt.annotation.Nullable;
import org.eclipse.smarthome.core.thing.Bridge;
import org.eclipse.smarthome.core.thing.ChannelUID;
import org.eclipse.smarthome.core.thing.ThingStatus;
import org.eclipse.smarthome.core.thing.ThingStatusDetail;
import org.eclipse.smarthome.core.thing.binding.BaseBridgeHandler;
import org.eclipse.smarthome.core.types.Command;
import org.eclipse.smarthome.core.types.State;
import org.openhab.binding.synologysurveillancestation.internal.SynoConfig;
import org.openhab.binding.synologysurveillancestation.internal.discovery.CameraDiscoveryService;
import org.openhab.binding.synologysurveillancestation.internal.thread.SynoApiThread;
import org.openhab.binding.synologysurveillancestation.internal.thread.SynoApiThreadHomeMode;
import org.openhab.binding.synologysurveillancestation.internal.webapi.SynoWebApiHandler;
import org.openhab.binding.synologysurveillancestation.internal.webapi.WebApiException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * The {@link SynoBridgeHandler} is a Bridge handler for the Synology Surveillance Station
 *
 * @author Nils
 */
@NonNullByDefault
public class SynoBridgeHandler extends BaseBridgeHandler implements SynoHandler {

    private final Logger logger = LoggerFactory.getLogger(SynoBridgeHandler.class);
    private @Nullable CameraDiscoveryService discoveryService;
    private @Nullable SynoWebApiHandler apiHandler = null;
    private final Map<String, SynoApiThread> threads = new HashMap<>();

    /**
     * Defines a runnable for a discovery
     */
    Runnable runnable = new Runnable() {
        @Override
        public void run() {
            if (discoveryService != null) {
                discoveryService.discoverCameras();
            }
        }
    };

    public SynoBridgeHandler(Bridge bridge) {
        super(bridge);
        int refreshRateEvents = 3;
        try {
            refreshRateEvents = Integer.parseInt(thing.getConfiguration().get(REFRESH_RATE_EVENTS).toString());
        } catch (Exception ex) {
            logger.error("Error parsing Bridge configuration");
        }
        threads.put(SynoApiThread.THREAD_HOMEMODE, new SynoApiThreadHomeMode(this, refreshRateEvents));
    }

    @Override
    public @Nullable SynoWebApiHandler getSynoWebApiHandler() {
        return apiHandler;
    }

    @Override
    public void handleCommand(@NonNull ChannelUID channelUID, @NonNull Command command) {
        try {
            switch (channelUID.getId()) {
                case CHANNEL_HOMEMODE:
                    if (command.toString().equals("REFRESH")) {
                        threads.get(SynoApiThread.THREAD_HOMEMODE).runOnce();
                    } else if (apiHandler != null) {
                        boolean state = command.toString().equals("ON");
                        apiHandler.setHomeMode(state);
                    }
                    break;
            }
        } catch (Exception e) {
            logger.error("handle command: {}::{}", getThing().getLabel(), getThing().getUID());
        }
    }

    public void setDiscovery(CameraDiscoveryService discoveryService) {
        this.discoveryService = discoveryService;
    }

    @Override
    public void initialize() {
        try {
            if (logger.isDebugEnabled()) {
                logger.debug("Initialize thing: {}::{}", getThing().getLabel(), getThing().getUID());
            }

            SynoConfig config = getConfigAs(SynoConfig.class);
            apiHandler = new SynoWebApiHandler(config);
            apiHandler.connect();

            // InfoResponse infoResponse = apiHandler.getInfo();
            // getThing().setProperty(SynoApiResponse.PROP_CAMERANUMBER,
            // infoResponse.getData().get(SynoApiResponse.PROP_CAMERANUMBER).getAsString());
            // TODO if needed add other infos

            for (SynoApiThread thread : threads.values()) {
                thread.start();
            }

            updateStatus(ThingStatus.ONLINE);

            // Trigger discovery of cameras
            scheduler.submit(runnable);

        } catch (WebApiException e) {
            if (e.getCause() instanceof java.util.concurrent.TimeoutException) {
                updateStatus(ThingStatus.OFFLINE, ThingStatusDetail.COMMUNICATION_ERROR, "Connection timeout");
            } else if (e.getErrorCode() == 400) {
                updateStatus(ThingStatus.OFFLINE, ThingStatusDetail.CONFIGURATION_ERROR,
                        "Please add or check your credentials");
            } else {
                updateStatus(ThingStatus.OFFLINE, ThingStatusDetail.COMMUNICATION_ERROR,
                        "Errorcode: " + e.getErrorCode());
            }
        }

    }

    @Override
    public void dispose() {
        for (SynoApiThread thread : threads.values()) {
            thread.stop();
        }
        try {
            apiHandler.logout();
        } catch (Exception ex) {
        }
    }

    @Override
    public void handleConfigurationUpdate(Map<String, Object> configurationParameters) {
        super.handleConfigurationUpdate(configurationParameters);
        int refreshRateEvents = Integer.parseInt(configurationParameters.get(REFRESH_RATE_EVENTS).toString());
        threads.get(SynoApiThread.THREAD_HOMEMODE).setRefreshRate(refreshRateEvents);
    }

    @Override
    public boolean isLinked(String channelId) {
        return super.isLinked(channelId);
    }

    @Override
    public void updateStatus(ThingStatus status, ThingStatusDetail statusDetail, @Nullable String description) {
        super.updateStatus(status, statusDetail, description);
    }

    @Override
    public void updateStatus(ThingStatus status) {
        super.updateStatus(status);
    }

    @Override
    public void updateState(ChannelUID channelUID, State state) {
        super.updateState(channelUID, state);
    }

    /**
     * @return service scheduler of this Thing
     */
    @Override
    public ScheduledExecutorService getScheduler() {
        return scheduler;
    }
}
