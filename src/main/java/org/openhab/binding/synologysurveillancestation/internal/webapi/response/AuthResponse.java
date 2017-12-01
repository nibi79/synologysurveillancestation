package org.openhab.binding.synologysurveillancestation.internal.webapi.response;

public class AuthResponse extends SimpleResponse {

    /**
     * @param jsonResponse
     */
    public AuthResponse(String jsonResponse) {
        super(jsonResponse);
    }

    /**
     * @return
     */
    public String getSid() {
        return getData().get("sid").getAsString();
    }

}
