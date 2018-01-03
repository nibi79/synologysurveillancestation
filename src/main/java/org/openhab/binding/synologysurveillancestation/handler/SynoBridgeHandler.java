/**
 * Copyright (c) 2010-2017 by the respective copyright holders.
 *
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 */
package org.openhab.binding.synologysurveillancestation.handler;

import java.util.Map;

import org.eclipse.jdt.annotation.NonNull;
import org.eclipse.smarthome.core.thing.Bridge;
import org.eclipse.smarthome.core.thing.ChannelUID;
import org.eclipse.smarthome.core.thing.ThingStatus;
import org.eclipse.smarthome.core.thing.ThingStatusDetail;
import org.eclipse.smarthome.core.thing.binding.BaseBridgeHandler;
import org.eclipse.smarthome.core.types.Command;
import org.openhab.binding.synologysurveillancestation.internal.SynoConfig;
import org.openhab.binding.synologysurveillancestation.internal.discovery.CameraDiscoveryService;
import org.openhab.binding.synologysurveillancestation.internal.webapi.SynoWebApiHandler;
import org.openhab.binding.synologysurveillancestation.internal.webapi.WebApiException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * The {@link SynoBridgeHandler} is a Bridge handler for camera Things
 *
 * @author Nils
 */
public class SynoBridgeHandler extends BaseBridgeHandler {

    private final Logger logger = LoggerFactory.getLogger(SynoBridgeHandler.class);
    private CameraDiscoveryService discoveryService;
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

    private SynoWebApiHandler apiHandler = null;

    public SynoBridgeHandler(Bridge bridge) {
        super(bridge);
    }

    public SynoWebApiHandler getSynoWebApiHandler() {
        return apiHandler;
    }

    @Override
    public void handleCommand(@NonNull ChannelUID channelUID, @NonNull Command command) {
        // There is nothing to handle in the bridge handler
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
    public void handleConfigurationUpdate(Map<String, Object> configurationParameters) {
        super.handleConfigurationUpdate(configurationParameters);
    }
}
