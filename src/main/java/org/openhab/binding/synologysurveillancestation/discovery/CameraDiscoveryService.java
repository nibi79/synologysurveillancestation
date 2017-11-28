package org.openhab.binding.synologysurveillancestation.discovery;

import static org.openhab.binding.synologysurveillancestation.SynologySurveillanceStationBindingConstants.*;

import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Set;

import org.eclipse.smarthome.config.discovery.AbstractDiscoveryService;
import org.eclipse.smarthome.config.discovery.DiscoveryResult;
import org.eclipse.smarthome.config.discovery.DiscoveryResultBuilder;
import org.eclipse.smarthome.core.thing.ThingTypeUID;
import org.eclipse.smarthome.core.thing.ThingUID;
import org.openhab.binding.synologysurveillancestation.SynologySurveillanceStationBindingConstants;
import org.openhab.binding.synologysurveillancestation.handler.SynologySurveillanceStationBridgeHandler;
import org.openhab.binding.synologysurveillancestation.internal.webapi.SynoWebApiHandler;
import org.openhab.binding.synologysurveillancestation.internal.webapi.WebApiException;
import org.openhab.binding.synologysurveillancestation.internal.webapi.response.CameraResponse;
import org.openhab.binding.synologysurveillancestation.internal.webapi.response.SynoApiResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;

public class CameraDiscoveryService extends AbstractDiscoveryService {

    private final Logger logger = LoggerFactory.getLogger(CameraDiscoveryService.class);

    private SynologySurveillanceStationBridgeHandler bridgeHandler = null;

    /**
     * Maximum time to search for devices in seconds.
     */
    private final static int SEARCH_TIME = 20;

    public CameraDiscoveryService(SynologySurveillanceStationBridgeHandler bridgeHandler)
            throws IllegalArgumentException {
        super(SEARCH_TIME);
        this.bridgeHandler = bridgeHandler;
    }

    @Override
    public Set<ThingTypeUID> getSupportedThingTypes() {
        return SynologySurveillanceStationBindingConstants.SUPPORTED_THING_TYPES;
    }

    @Override
    protected void startScan() {
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

                            ThingTypeUID thingTypeUID = new ThingTypeUID(BINDING_ID, "camera");

                            String cameraId = cam.get("id").getAsString();

                            ThingUID thingUID = new ThingUID(thingTypeUID, bridgeUID, cameraId);

                            Map<String, Object> properties = new LinkedHashMap<>();
                            properties.put(DEVICE_ID, cameraId);
                            properties.put(SynoApiResponse.PROP_VENDOR,
                                    cam.get(SynoApiResponse.PROP_VENDOR).getAsString());
                            properties.put(SynoApiResponse.PROP_MODEL,
                                    cam.get(SynoApiResponse.PROP_MODEL).getAsString());
                            properties.put(SynoApiResponse.PROP_DEVICETYPE,
                                    cam.get(SynoApiResponse.PROP_DEVICETYPE).getAsString());
                            properties.put(SynoApiResponse.PROP_HOST, cam.get(SynoApiResponse.PROP_HOST).getAsString());
                            properties.put(SynoApiResponse.PROP_RESOLUTION,
                                    cam.get(SynoApiResponse.PROP_RESOLUTION).getAsString());
                            properties.put(SynoApiResponse.PROP_TYPE, cam.get(SynoApiResponse.PROP_TYPE).getAsString());

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
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
    }

    @Override
    protected void startBackgroundDiscovery() {
        super.startBackgroundDiscovery();
    }
}
