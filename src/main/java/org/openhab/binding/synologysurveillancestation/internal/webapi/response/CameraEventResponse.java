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

import com.google.gson.JsonObject;

/**
 * {@link CameraEventResponse} is a response for camera information
 *
 * @author Pavion - Initial contribution
 */
@NonNullByDefault
public class CameraEventResponse extends SimpleResponse {

    /**
     * @param jsonResponse
     */
    public CameraEventResponse(String jsonResponse) {
        super(jsonResponse);
    }

    /**
     * Returns motion detection parameter as Json Object
     *
     * @return Motion detection parameter as Json Object
     */
    private JsonObject getMDParam() {
        return getData().getAsJsonObject("MDParam");
    }

    /**
     * What is it?
     *
     * @return
     */
    private JsonObject getPDParam() {
        return getData().getAsJsonObject("PDParam");
    }

    /**
     * Get MD object size
     *
     * @return
     */
    public CameraEventResponseObject getObjectSize() {
        return new CameraEventResponseObject(getMDParam().getAsJsonObject("objectSize"));
    }

    /**
     * Get MD sensitivity
     *
     * @return
     */
    public CameraEventResponseObject getSensitivity() {
        return new CameraEventResponseObject(getMDParam().getAsJsonObject("sensitivity"));
    }

    /**
     * Get MD threshold
     *
     * @return
     */
    public CameraEventResponseObject getThreshold() {
        return new CameraEventResponseObject(getMDParam().getAsJsonObject("threshold"));
    }

    /**
     * Get MD history
     *
     * @return
     */
    public CameraEventResponseObject getHistory() {
        return new CameraEventResponseObject(getMDParam().getAsJsonObject("history"));
    }

    /**
     * Get MD shortLiveSecond
     *
     * @return
     */
    public CameraEventResponseObject getShortLiveSecond() {
        return new CameraEventResponseObject(getMDParam().getAsJsonObject("shortLiveSecond"));
    }

    /**
     * Get MD percentage
     *
     * @return
     */
    public CameraEventResponseObject getPercentage() {
        return new CameraEventResponseObject(getMDParam().getAsJsonObject("percentage"));
    }

    /**
     * Get MD object size
     *
     * @return
     */
    public String getSource() {
        return getMDParam().get("source").getAsString();
    }

    /**
     * Get MD keep parameter
     *
     * @return
     */
    public boolean getKeep() {
        return getMDParam().get("keep").getAsBoolean();
    }
}
