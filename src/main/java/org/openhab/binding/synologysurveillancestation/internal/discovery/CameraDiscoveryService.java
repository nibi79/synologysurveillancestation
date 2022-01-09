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
package org.openhab.binding.synologysurveillancestation.internal.discovery;

import static org.openhab.binding.synologysurveillancestation.SynoBindingConstants.THING_TYPE_CAMERA;

import java.util.Map;
import java.util.Set;

import org.eclipse.jdt.annotation.NonNullByDefault;
import org.eclipse.jdt.annotation.Nullable;
import org.openhab.binding.synologysurveillancestation.SynoBindingConstants;
import org.openhab.binding.synologysurveillancestation.handler.SynoBridgeHandler;
import org.openhab.binding.synologysurveillancestation.internal.webapi.SynoWebApiHandler;
import org.openhab.binding.synologysurveillancestation.internal.webapi.WebApiException;
import org.openhab.binding.synologysurveillancestation.internal.webapi.error.WebApiAuthErrorCodes;
import org.openhab.binding.synologysurveillancestation.internal.webapi.response.CameraResponse;
import org.openhab.core.config.discovery.AbstractDiscoveryService;
import org.openhab.core.config.discovery.DiscoveryResult;
import org.openhab.core.config.discovery.DiscoveryResultBuilder;
import org.openhab.core.thing.ThingStatus;
import org.openhab.core.thing.ThingTypeUID;
import org.openhab.core.thing.ThingUID;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;

/**
 * The {@link CameraDiscoveryService} is a service for discovering your cameras through Synology API
 *
 * @author Nils - Initial contribution
 * @author Pavion - Contribution
 */
@NonNullByDefault
public class CameraDiscoveryService extends AbstractDiscoveryService {

    private final Logger logger = LoggerFactory.getLogger(CameraDiscoveryService.class);

    @Nullable
    private SynoBridgeHandler bridgeHandler = null;

    /**
     * Maximum time to search for devices in seconds.
     */
    private static final int SEARCH_TIME = 20;

    public CameraDiscoveryService() {
        super(SynoBindingConstants.SUPPORTED_CAMERA_TYPES, SEARCH_TIME);
    }

    public CameraDiscoveryService(SynoBridgeHandler bridgeHandler) throws IllegalArgumentException {
        super(SEARCH_TIME);
        this.bridgeHandler = bridgeHandler;
    }

    /**
     * Public method for triggering camera discovery
     */
    public void discoverCameras() {
        startScan();
    }

    @Override
    public Set<ThingTypeUID> getSupportedThingTypes() {
        return SynoBindingConstants.SUPPORTED_THING_TYPES;
    }

    @Override
    protected void startScan() {
        if (bridgeHandler == null) {
            return;
        }
        // Trigger no scan if offline
        if (bridgeHandler.getThing().getStatus() != ThingStatus.ONLINE) {
            return;
        }

        try {
            SynoWebApiHandler apiHandler = bridgeHandler.getSynoWebApiHandler();

            CameraResponse response = apiHandler.getApiCamera().listCameras();

            if (response.isSuccess()) {
                JsonArray cameras = response.getCameras();

                ThingUID bridgeUID = bridgeHandler.getThing().getUID();

                if (cameras != null) {
                    for (JsonElement camera : cameras) {

                        if (camera.isJsonObject()) {
                            JsonObject cam = camera.getAsJsonObject();

                            String cameraId = cam.get("id").getAsString();

                            CameraResponse cameraDetails = apiHandler.getApiCamera().getInfo(cameraId);

                            ThingUID thingUID = new ThingUID(THING_TYPE_CAMERA, bridgeUID, cameraId);

                            Map<String, Object> properties = cameraDetails.getCameraProperties(cameraId);

                            DiscoveryResult discoveryResult = DiscoveryResultBuilder.create(thingUID)
                                    .withProperties(properties).withBridge(bridgeHandler.getThing().getUID())
                                    .withLabel(cam.get("name").getAsString()).build();

                            thingDiscovered(discoveryResult);

                            logger.debug("Discovered a camera thing with ID '{}'", cameraId);
                        }
                    }
                }
            }

        } catch (WebApiException e) {
            if (e.getCause() instanceof javax.net.ssl.SSLHandshakeException
                    || e.getCause() instanceof java.io.EOFException
                    || e.getCause() instanceof java.util.concurrent.ExecutionException) {
                logger.error("Possible SSL certificate issue, please consider using http or enabling SSL bypass");
            } else if (e.getErrorCode() == 102) {
                logger.error("Discovery Thread; Surveillance Station is disabled or not installed");
            } else if (e.getErrorCode() == WebApiAuthErrorCodes.INSUFFICIENT_USER_PRIVILEGE.getCode()) {
                logger.debug("Discovery Thread; Wrong/expired credentials");
                try {
                    bridgeHandler.reconnect(false);
                } catch (WebApiException ee) {
                    logger.error("Discovery Thread; Attempt to reconnect failed");
                }
            } else {
                logger.error("Discovery Thread; Unexpected error: {} - {}", e.getErrorCode(), e.getMessage());
            }
        } catch (Exception npe) {
            logger.error("Error in WebApiException", npe);
        }
    }

    @Override
    protected void startBackgroundDiscovery() {
        startScan();
    }
}
