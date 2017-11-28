package org.openhab.binding.synologysurveillancestation.internal.webapi.response;

import com.google.gson.JsonArray;

public class CameraResponse extends SynoApiResponse {

    /**
     * @param jsonResponse
     */
    public CameraResponse(String jsonResponse) {
        super(jsonResponse);
    }

    public JsonArray getCameras() {
        return getData().getAsJsonArray("cameras");
    }

}
