/**
 * Copyright (c) 2010-2019 by the respective copyright holders.
 *
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 */
package org.openhab.binding.synologysurveillancestation.internal.webapi.response;

import com.google.gson.JsonObject;

/**
 * {@link CameraEventResponseObject} is a JSON extension for response numeric type
 *
 * @author Pavion - Implementation
 */
public class CameraEventResponseObject {

    private boolean camCap = false;
    private boolean ssCap = false;
    private int value;
    private int minValue;
    private int maxValue;

    /**
     * Constructor
     *
     * @param object
     */
    public CameraEventResponseObject(JsonObject object) {
        if (object != null) {
            this.camCap = object.get("camCap").getAsBoolean();
            this.ssCap = object.get("ssCap").getAsBoolean();
            this.value = object.get("value").getAsInt();
            this.minValue = object.get("minValue").getAsInt();
            this.maxValue = object.get("maxValue").getAsInt();
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
