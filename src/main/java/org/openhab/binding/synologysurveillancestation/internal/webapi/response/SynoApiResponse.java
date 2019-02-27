/**
 * Copyright (c) 2010-2019 by the respective copyright holders.
 *
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 */
package org.openhab.binding.synologysurveillancestation.internal.webapi.response;

import org.eclipse.jdt.annotation.NonNullByDefault;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.google.gson.JsonSyntaxException;

/**
 * {@link SynoApiResponse} is an abstract class for an API response
 *
 * @author Nils - Initial contribution
 * @author Pavion - Contribution
 */
@NonNullByDefault
public abstract class SynoApiResponse {

    public static final String PROP_VENDOR = "vendor";
    public static final String PROP_MODEL = "model";
    public static final String PROP_DEVICETYPE = "deviceType";
    public static final String PROP_HOST = "host";
    public static final String PROP_RESOLUTION = "resolution";
    public static final String PROP_TYPE = "type";
    // public static final String PROP_CAMERANUMBER = "cameraNumber";

    // PTZ capabilities
    public static final String PROP_PTZ = "ptz";
    public static final String PROP_PTZ_PAN = "ptz_pan";
    public static final String PROP_PTZ_TILT = "ptz_tilt";
    public static final String PROP_PTZ_ZOOM = "ptz_zoom";
    public static final String PROP_PTZ_HOME = "ptz_home";
    public static final String PROP_PTZ_ABS = "ptz_abs";
    public static final String PROP_PTZ_FOCUS = "ptz_focus";
    public static final String PROP_PTZ_AUTOFOCUS = "ptz_autofocus";
    public static final String PROP_PTZ_IRIS = "ptz_iris";
    public static final String PROP_PTZ_SPEED = "ptz_speed";
    public static final String PROP_PTZ_ZOOM_SPEED = "ptz_zoom_speed";

    private JsonObject jsonResponse = new JsonParser().parse("{\"data\":{},\"success\":false}").getAsJsonObject();

    public SynoApiResponse() {
    }

    /**
     * @param jsonResponse
     */
    public SynoApiResponse(String jsonResponse) {
        try {
            JsonObject json = new JsonParser().parse(jsonResponse).getAsJsonObject();
            this.jsonResponse = json;
        } catch (JsonSyntaxException e) {
            // keep default value
        }
    }

    /**
     * @return
     */
    public JsonObject getData() {
        JsonObject ret = jsonResponse.getAsJsonObject("data");
        if (ret == null) {
            return new JsonObject();
        }
        return ret;
    }

    /**
     *
     * @return
     */
    public JsonArray getDataAsJsonArray() {
        JsonArray ret = jsonResponse.getAsJsonArray("data");
        if (ret == null) {
            return new JsonArray();
        }
        return ret;
    }

    /**
     * @return
     */
    public boolean isSuccess() {
        if (jsonResponse.has("success")) {
            return jsonResponse.get("success").getAsBoolean();
        }
        return false;
    }

    /**
     * @return
     */
    public int getErrorcode() {
        if (jsonResponse.has("error")) {
            if (jsonResponse.getAsJsonObject("error").has("code")) {
                return jsonResponse.getAsJsonObject("error").get("code").getAsInt();
            }
        }
        return 0;
    }

    @Override
    public String toString() {
        return jsonResponse.toString();
    }

    /**
     * @param hexValue
     * @param bitNumber
     * @return
     */
    protected boolean isBitSet(String hexValue, int bitNumber) {
        int val = Integer.valueOf(hexValue, 16);
        return (val & (1 << bitNumber)) != 0;
    }
}
