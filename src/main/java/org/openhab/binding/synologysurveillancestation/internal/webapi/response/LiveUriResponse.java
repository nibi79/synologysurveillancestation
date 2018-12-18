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
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;

/**
 * {@link LiveUriResponse} is a response for live URIs
 *
 * @author Pavion - Initial contribution
 */
public class LiveUriResponse extends SimpleResponse {

    /**
     * @param jsonResponse
     */
    public LiveUriResponse(String jsonResponse) {
        super(jsonResponse);
    }

    public JsonArray getUris() {
        return getDataAsArray();
    }

    /**
     * Return rtsp URI
     *
     */
    public String getRtsp() {
        for (JsonElement jUri : getUris()) {
            if (jUri.isJsonObject()) {
                JsonObject uri = jUri.getAsJsonObject();
                return uri.get("rtspPath").getAsString();
            }
        }
        return "";
    }

    /**
     * Return mjpeg over http URI
     *
     */
    public String getMjpegHttp() {
        for (JsonElement jUri : getUris()) {
            if (jUri.isJsonObject()) {
                JsonObject uri = jUri.getAsJsonObject();
                return uri.get("mjpegHttpPath").getAsString();
            }
        }
        return "";
    }
}
