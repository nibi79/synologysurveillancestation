/*
 * Copyright (c) 2010-2026 Contributors to the openHAB project
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

import org.eclipse.jdt.annotation.NonNullByDefault;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;

/**
 * {@link LiveUriResponse} is a response for live URIs
 *
 * @author Pavion - Initial contribution
 */
@NonNullByDefault
public class LiveUriResponse extends SimpleResponse {

    /**
     * @param jsonResponse
     */
    public LiveUriResponse(String jsonResponse) {
        super(jsonResponse);
    }

    public JsonArray getUris() {
        return getDataAsJsonArray();
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
