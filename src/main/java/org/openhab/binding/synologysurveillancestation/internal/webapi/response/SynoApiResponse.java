package org.openhab.binding.synologysurveillancestation.internal.webapi.response;

import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

/**
 * @author Nils
 *
 */
public abstract class SynoApiResponse {

    public static final String PROP_VENDOR = "vendor";
    public static final String PROP_MODEL = "model";
    public static final String PROP_DEVICETYPE = "deviceType";
    public static final String PROP_HOST = "host";
    public static final String PROP_RESOLUTION = "resolution";
    public static final String PROP_TYPE = "type";
    public static final String PROP_CAMERANUMBER = "cameraNumber";

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
}
