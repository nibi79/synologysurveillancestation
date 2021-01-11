/**
 * Copyright (c) 2010-2020 Contributors to the openHAB project
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
package org.openhab.binding.synologysurveillancestation.handler;

import static org.openhab.binding.synologysurveillancestation.SynoBindingConstants.*;

import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.atomic.AtomicBoolean;

import org.eclipse.jdt.annotation.NonNullByDefault;
import org.eclipse.jdt.annotation.Nullable;
import org.eclipse.jetty.client.HttpClient;
import org.openhab.binding.synologysurveillancestation.internal.SynoConfig;
import org.openhab.binding.synologysurveillancestation.internal.discovery.CameraDiscoveryService;
import org.openhab.binding.synologysurveillancestation.internal.thread.SynoApiThread;
import org.openhab.binding.synologysurveillancestation.internal.thread.SynoApiThreadHomeMode;
import org.openhab.binding.synologysurveillancestation.internal.webapi.SynoWebApiHandler;
import org.openhab.binding.synologysurveillancestation.internal.webapi.WebApiException;
import org.openhab.core.config.core.Configuration;
import org.openhab.core.library.types.DecimalType;
import org.openhab.core.library.types.StringType;
import org.openhab.core.thing.Bridge;
import org.openhab.core.thing.ChannelUID;
import org.openhab.core.thing.ThingStatus;
import org.openhab.core.thing.ThingStatusDetail;
import org.openhab.core.thing.binding.BaseBridgeHandler;
import org.openhab.core.types.Command;
import org.openhab.core.types.RefreshType;
import org.openhab.core.types.State;
import org.openhab.core.types.UnDefType;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * The {@link SynoBridgeHandler} is a Bridge handler for the Synology Surveillance Station
 *
 * @author Nils - Initial contribution
 * @author Pavion - Contribution
 */
@NonNullByDefault
public class SynoBridgeHandler extends BaseBridgeHandler implements SynoHandler {

    private final Logger logger = LoggerFactory.getLogger(SynoBridgeHandler.class);
    private @Nullable CameraDiscoveryService discoveryService;
    private final SynoWebApiHandler apiHandler;
    private final Map<String, SynoApiThread<SynoBridgeHandler>> threads = new HashMap<>();
    private int refreshRateEvents = 3;
    private final AtomicBoolean refreshInProgress = new AtomicBoolean(false);
    private SynoConfig config = new SynoConfig();

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

    public SynoBridgeHandler(Bridge bridge, HttpClient httpClient) {
        super(bridge);
        try {
            this.refreshRateEvents = Integer.parseInt(bridge.getConfiguration().get(REFRESH_RATE_EVENTS).toString());
        } catch (Exception ex) {
            logger.error("Error parsing Bridge configuration");
        }
        config = getConfigAs(SynoConfig.class);

        apiHandler = new SynoWebApiHandler(config, httpClient);
        threads.put(SynoApiThread.THREAD_HOMEMODE, new SynoApiThreadHomeMode(this, refreshRateEvents));
        try {
            reconnect(false);
        } catch (WebApiException e) {
        }
    }

    @Override
    @Nullable
    public SynoWebApiHandler getSynoWebApiHandler() {
        return apiHandler;
    }

    @Override
    public void handleCommand(ChannelUID channelUID, Command command) {
        try {
            switch (channelUID.getId()) {
                case CHANNEL_HOMEMODE:
                    if (command.toString().equals("REFRESH")) {
                        threads.get(SynoApiThread.THREAD_HOMEMODE).runOnce();
                    } else {
                        boolean state = command.toString().equals("ON");
                        apiHandler.getApiHomeMode().setHomeMode(state);
                    }
                    break;
                case CHANNEL_EVENT_TRIGGER:
                    if (command.toString().equals("REFRESH")) {
                        updateState(channelUID, UnDefType.UNDEF);
                    } else {
                        int event = Integer.parseInt(command.toString());
                        boolean ret = false;
                        if (event >= 1 && event <= 10) {
                            ret = apiHandler.getApiExternalEvent().triggerEvent(event);
                        }
                        updateState(channelUID, ret ? new DecimalType(0) : UnDefType.UNDEF);
                    }
                    break;
                case CHANNEL_SID:
                    if (command.toString().equals("REFRESH")) {
                        updateState(channelUID, new StringType(apiHandler.getSessionID()));
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
    public boolean reconnect(boolean forceLogout) throws WebApiException {
        if (refreshInProgress.compareAndSet(false, true)) {
            boolean ret = false;
            try {
                ret = apiHandler.connect(forceLogout);
                try {
                    Thread.sleep(1000);
                } catch (InterruptedException e) {
                }
                refreshInProgress.set(false);
            } catch (WebApiException e) {
                refreshInProgress.set(false);
                throw e;
            }
            if (ret && getThing().getStatus() == ThingStatus.ONLINE) {
                handleCommand(new ChannelUID(getThing().getUID(), CHANNEL_SID), RefreshType.REFRESH);
            }
            return ret;
        } else {
            logger.debug("Reconnect already in progress...");
            return false;
        }
    }

    @Override
    public void initialize() {
        try {
            if (logger.isDebugEnabled()) {
                logger.debug("Initialize thing: {}::{}", getThing().getLabel(), getThing().getUID());
            }

            if (!getConfigAs(SynoConfig.class).equals(config)) {
                config = getConfigAs(SynoConfig.class);
                apiHandler.setConfig(config);
                reconnect(false);
            }

            // if needed add other infos
            // InfoResponse infoResponse = apiHandler.getInfo();
            // getThing().setProperty(SynoApiResponse.PROP_CAMERANUMBER,
            // infoResponse.getData().get(SynoApiResponse.PROP_CAMERANUMBER).getAsString());

            for (SynoApiThread<SynoBridgeHandler> thread : threads.values()) {
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
        for (SynoApiThread<SynoBridgeHandler> thread : threads.values()) {
            thread.stop();
        }
    }

    @Override
    public void handleConfigurationUpdate(Map<String, Object> configurationParameters) {
        boolean refreshOnly = true;
        Configuration currentConfig = getConfig();
        for (Entry<String, Object> entry : configurationParameters.entrySet()) {
            if (!currentConfig.containsKey(entry.getKey())) {
                refreshOnly = false;
                break;
            } else if (!currentConfig.get(entry.getKey()).equals(entry.getValue())
                    && !entry.getKey().equals(REFRESH_RATE_EVENTS)) {
                refreshOnly = false;
                break;
            }
        }
        if (!refreshOnly) {
            super.handleConfigurationUpdate(configurationParameters);
        }
        int newRefreshRateEvents = Integer.parseInt(configurationParameters.get(REFRESH_RATE_EVENTS).toString());
        if (newRefreshRateEvents != this.refreshRateEvents) {
            this.refreshRateEvents = newRefreshRateEvents;
            threads.get(SynoApiThread.THREAD_HOMEMODE).setRefreshRate(this.refreshRateEvents);
        }
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
