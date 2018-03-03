/**
 * Copyright (c) 2010-2018 by the respective copyright holders.
 *
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 */
package org.openhab.binding.synologysurveillancestation.internal.webapi.response;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

/**
 * {@link SynoApiResponse} is an abstract class for an API response
 *
 * @author Nils
 */
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

    private JsonObject jsonResponse = null;

    /**
     * @param jsonResponse
     */
    public SynoApiResponse(String jsonResponse) {
        JsonObject json = new JsonParser().parse(jsonResponse).getAsJsonObject();
        this.jsonResponse = json;
    }

    /**
     * @return
     */
    public JsonObject getData() {
        return jsonResponse.getAsJsonObject("data");
    }

    /**
     * @return
     */
    public JsonArray getDataAsArray() {
        return jsonResponse.getAsJsonArray("data");
    }

    /**
     * @return
     */
    public boolean isSuccess() {
        return Boolean.valueOf(jsonResponse.get("success").getAsString()).booleanValue();
    }

    /**
     * @return
     */
    public JsonObject getError() {
        return jsonResponse.getAsJsonObject("error");
    }

    /**
     * @return
     */
    public int getErrorcode() {
        if (getError() != null) {
            return getError().get("code").getAsInt();
        } else {
            return 0;
        }
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
