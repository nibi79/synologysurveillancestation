package org.openhab.binding.synologysurveillancestation.internal.webapi.response;

import com.google.gson.JsonElement;

public class InfoResponse extends SimpleResponse {

    /**
     * @param jsonResponse
     */
    public InfoResponse(String jsonResponse) {
        super(jsonResponse);
    }

    public JsonElement getCameras() {
        return getData().get("cameras");
    }

}
