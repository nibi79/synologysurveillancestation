/**
 * Copyright (c) 2010-2021 Contributors to the openHAB project
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

import com.google.gson.JsonObject;

/**
 * {@link CameraEventResponseObject} is a JSON extension for response numeric type
 *
 * @author Pavion - Initial contribution
 */
@NonNullByDefault
public class CameraEventResponseObject {

    private boolean camCap = false;
    private boolean ssCap = false;
    private int value = 0;
    private int minValue = 0;
    private int maxValue = 99;

    /**
     * Constructor
     *
     * @param object
     */
    public CameraEventResponseObject(JsonObject object) {
        this.camCap = object.get("camCap").getAsBoolean();
        this.ssCap = object.get("ssCap").getAsBoolean();
        this.value = object.get("value").getAsInt();
        try {
            this.minValue = object.get("minValue").getAsInt();
            this.maxValue = object.get("maxValue").getAsInt();
        } catch (Exception ex) {
            // Keep default values
        }
    }

    /**
     * @return the camCap
     */
    public boolean isCamCap() {
        return camCap;
    }

    /**
     * @param camCap the camCap to set
     */
    public void setCamCap(boolean camCap) {
        this.camCap = camCap;
    }

    /**
     * @return the ssCap
     */
    public boolean isSsCap() {
        return ssCap;
    }

    /**
     * @param ssCap the ssCap to set
     */
    public void setSsCap(boolean ssCap) {
        this.ssCap = ssCap;
    }

    /**
     * @return the value
     */
    public int getValue() {
        return value;
    }

    /**
     * @param value the value to set
     */
    public void setValue(int value) {
        this.value = value;
    }

    /**
     * @return the minValue
     */
    public int getMinValue() {
        return minValue;
    }

    /**
     * @param minValue the minValue to set
     */
    public void setMinValue(int minValue) {
        this.minValue = minValue;
    }

    /**
     * @return the maxValue
     */
    public int getMaxValue() {
        return maxValue;
    }

    /**
     * @param maxValue the maxValue to set
     */
    public void setMaxValue(int maxValue) {
        this.maxValue = maxValue;
    }
}
