/**
 * Copyright (c) 2010-2017 by the respective copyright holders.
 *
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 */
package org.openhab.binding.synologysurveillancestation.internal.discovery;

import static org.openhab.binding.synologysurveillancestation.SynologySurveillanceStationBindingConstants.*;

import java.util.Map;
import java.util.Set;

import org.eclipse.smarthome.config.discovery.AbstractDiscoveryService;
import org.eclipse.smarthome.config.discovery.DiscoveryResult;
import org.eclipse.smarthome.config.discovery.DiscoveryResultBuilder;
import org.eclipse.smarthome.core.thing.ThingStatus;
import org.eclipse.smarthome.core.thing.ThingTypeUID;
import org.eclipse.smarthome.core.thing.ThingUID;
import org.openhab.binding.synologysurveillancestation.SynologySurveillanceStationBindingConstants;
import org.openhab.binding.synologysurveillancestation.handler.SynologySurveillanceStationBridgeHandler;
import org.openhab.binding.synologysurveillancestation.internal.webapi.SynoWebApiHandler;
import org.openhab.binding.synologysurveillancestation.internal.webapi.WebApiException;
import org.openhab.binding.synologysurveillancestation.internal.webapi.response.CameraResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;

/**
 * The {@link CameraDiscoveryService} is a service for discovering your cameras through Synology API
 *
 * @author Nils
 */
public class CameraDiscoveryService extends AbstractDiscoveryService {

    private final Logger logger = LoggerFactory.getLogger(CameraDiscoveryService.class);

    private SynologySurveillanceStationBridgeHandler bridgeHandler = null;

    /**
     * Maximum time to search for devices in seconds.
     */
    private static final int SEARCH_TIME = 20;

    public CameraDiscoveryService() {
        super(SynologySurveillanceStationBindingConstants.SUPPORTED_CAMERA_TYPES, SEARCH_TIME);
    }

    public CameraDiscoveryService(SynologySurveillanceStationBridgeHandler bridgeHandler)
            throws IllegalArgumentException {
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
        return SynologySurveillanceStationBindingConstants.SUPPORTED_THING_TYPES;
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

            CameraResponse response = apiHandler.list();

            if (response.isSuccess()) {

                JsonArray cameras = response.getCameras();

                ThingUID bridgeUID = bridgeHandler.getThing().getUID();

                if (cameras != null) {

                    for (JsonElement camera : cameras) {

                        if (camera.isJsonObject()) {

                            JsonObject cam = camera.getAsJsonObject();

                            String cameraId = cam.get("id").getAsString();

                            CameraResponse cameraDetails = apiHandler.getInfo(cameraId);

                            ThingUID thingUID = createThingUID(bridgeUID, cameraId, cameraDetails);

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

        } catch (WebApiException | NullPointerException e) {
            logger.error("Error in WebApiException", e);
        }
    }

    /**
     * Creates ThingUID by evaluating camera PTZ support.
     *
     * @param bridgeUID
     * @param cameraId
     * @param cameraDetails
     * @return
     */
    private ThingUID createThingUID(ThingUID bridgeUID, String cameraId, CameraResponse cameraDetails) {

        JsonObject cameraDetail = cameraDetails.getCameras().get(0).getAsJsonObject();

        int ptzCap = cameraDetail.get("ptzCap").getAsInt();

        ThingTypeUID thingTypeUID = THING_TYPE_CAMERA;

        // supports PTZ?
        if (ptzCap > 0) {
            thingTypeUID = THING_TYPE_CAMERA_PTZ;
        }

        ThingUID thingUID = new ThingUID(thingTypeUID, bridgeUID, cameraId);

        return thingUID;
    }

    @Override
    protected void startBackgroundDiscovery() {
        startScan();
    }

}
