/**
 * Copyright (c) 2010-2020 Contributors to the openHAB project
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

import com.google.gson.JsonElement;

/**
 * {@link InfoResponse} provides information about current camera setup
 *
 * @author Nils - Initial contribution
 * @author Pavion - Contribution
 */
@NonNullByDefault
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
