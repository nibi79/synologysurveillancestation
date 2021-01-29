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
package org.openhab.binding.synologysurveillancestation.internal.webapi.response;

import static org.openhab.binding.synologysurveillancestation.SynoBindingConstants.DEVICE_ID;

import java.util.LinkedHashMap;
import java.util.Map;

import org.eclipse.jdt.annotation.NonNullByDefault;
import org.eclipse.jdt.annotation.Nullable;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;

/**
 * {@link CameraResponse} is a response for camera information
 *
 * @author Nils - Initial contribution
 * @author Pavion - Contribution
 */
@NonNullByDefault
public class CameraResponse extends SimpleResponse {

    // bits for PTZ capability
    // 0x001: Pan
    private static final int BIT_PTZ_PAN = 0;
    // 0x002: Tilt
    private static final int BIT_PTZ_TILT = 1;
    // 0x004: Zoom
    private static final int BIT_PTZ_ZOOM = 3;
    // 0x008: Home
    private static final int BIT_PTZ_HOME = 4;
    // 0x010: Abs position
    private static final int BIT_PTZ_ABS = 5;
    // 0x020: Focus
    private static final int BIT_PTZ_FOCUS = 6;
    // 0x040: Auto focus
    private static final int BIT_PTZ_AUTOFOCUS = 7;
    // 0x080: Iris
    private static final int BIT_PTZ_IRIS = 8;
    // 0x100: Ptz speed
    private static final int BIT_PTZ_SPEED = 9;
    // 0x200: Zoom speed
    private static final int BIT_PTZ_ZOOM_SPEED = 10;

    /**
     * @param jsonResponse
     */
    public CameraResponse(String jsonResponse) {
        super(jsonResponse);
    }

    @Nullable
    public JsonArray getCameras() {
        return getData().getAsJsonArray("cameras");
    }

    /**
     * If the camera is enabled
     *
     * @param cameraId
     */
    public boolean isEnabled(String cameraId) {
        for (JsonElement jcamera : getCameras()) {
            if (jcamera.isJsonObject()) {
                JsonObject camera = jcamera.getAsJsonObject();
                if (camera.get("id").getAsString().equals(cameraId)) {
                    return camera.get("enabled").getAsBoolean();
                }
            }
        }
        return false;
    }

    /**
     * If the camera is recording
     *
     * @param cameraId
     */
    public boolean isRecording(String cameraId) {
        for (JsonElement jcamera : getCameras()) {
            if (jcamera.isJsonObject()) {
                JsonObject camera = jcamera.getAsJsonObject();
                if (camera.get("id").getAsString().equals(cameraId)) {
                    return (camera.get("recStatus").getAsInt() > 0);
                }
            }
        }
        return false;
    }

    /**
     * Creates all relevant properties from response as key/value map.
     *
     * @param cameraId
     * @return
     */
    public Map<String, Object> getCameraProperties(String cameraId) {
        JsonArray cameras = this.getCameras().getAsJsonArray();

        for (JsonElement camera : cameras) {
            if (camera.isJsonObject()) {
                JsonObject cam = camera.getAsJsonObject();

                String id = cam.get("id").getAsString();

                if (cameraId.equals(id)) {
                    return createProperties(cam, cameraId);
                }
            }
        }
        return new LinkedHashMap<>();
    }

    /**
     * Creates the thing properties from response details.
     *
     * @param cam
     * @param cameraId
     * @return
     */
    private Map<String, Object> createProperties(JsonObject cam, String cameraId) {
        Map<String, Object> properties = new LinkedHashMap<>();

        properties.put(DEVICE_ID, cameraId);
        properties.put(SynoApiResponse.PROP_VENDOR, cam.get(SynoApiResponse.PROP_VENDOR).getAsString());
        properties.put(SynoApiResponse.PROP_MODEL, cam.get(SynoApiResponse.PROP_MODEL).getAsString());
        properties.put(SynoApiResponse.PROP_DEVICETYPE, cam.get(SynoApiResponse.PROP_DEVICETYPE).getAsString());
        properties.put(SynoApiResponse.PROP_HOST, cam.get(SynoApiResponse.PROP_HOST).getAsString());
        properties.put(SynoApiResponse.PROP_RESOLUTION, cam.get(SynoApiResponse.PROP_RESOLUTION).getAsString());
        properties.put(SynoApiResponse.PROP_TYPE, cam.get(SynoApiResponse.PROP_TYPE).getAsString());

        // check PTZ capabilities
        int ptzCap = cam.get("ptzCap").getAsInt();
        properties.put(SynoApiResponse.PROP_PTZ, (ptzCap > 0) ? "true" : "false");

        if (ptzCap > 0) {
            properties.put(SynoApiResponse.PROP_PTZ_PAN, isBitSetAsString(ptzCap, BIT_PTZ_PAN));
            properties.put(SynoApiResponse.PROP_PTZ_TILT, isBitSetAsString(ptzCap, BIT_PTZ_TILT));
            properties.put(SynoApiResponse.PROP_PTZ_ZOOM, isBitSetAsString(ptzCap, BIT_PTZ_ZOOM));
            properties.put(SynoApiResponse.PROP_PTZ_HOME, isBitSetAsString(ptzCap, BIT_PTZ_HOME));
            properties.put(SynoApiResponse.PROP_PTZ_ABS, isBitSetAsString(ptzCap, BIT_PTZ_ABS));
            properties.put(SynoApiResponse.PROP_PTZ_FOCUS, isBitSetAsString(ptzCap, BIT_PTZ_FOCUS));
            properties.put(SynoApiResponse.PROP_PTZ_AUTOFOCUS, isBitSetAsString(ptzCap, BIT_PTZ_AUTOFOCUS));
            properties.put(SynoApiResponse.PROP_PTZ_IRIS, isBitSetAsString(ptzCap, BIT_PTZ_IRIS));
            properties.put(SynoApiResponse.PROP_PTZ_SPEED, isBitSetAsString(ptzCap, BIT_PTZ_SPEED));
            properties.put(SynoApiResponse.PROP_PTZ_ZOOM_SPEED, isBitSetAsString(ptzCap, BIT_PTZ_ZOOM_SPEED));
        }

        return properties;
    }

    /**
     * @param ptzCap
     * @param bit
     * @return
     */
    private String isBitSetAsString(int hexValue, int bit) {
        return Boolean.toString(isBitSet(String.valueOf(hexValue), bit));
    }
}
