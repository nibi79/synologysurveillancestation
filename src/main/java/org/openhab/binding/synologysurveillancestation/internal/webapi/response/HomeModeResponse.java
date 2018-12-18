/**
 * Copyright (c) 2010-2018 by the respective copyright holders.
 *
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 */
package org.openhab.binding.synologysurveillancestation.internal.webapi.response;

import org.eclipse.jdt.annotation.NonNullByDefault;

/**
 * {@link HomeModeResponse} provides response for Home Mode
 *
 * @author Pavion - Initial contribution
 */
@NonNullByDefault
public class HomeModeResponse extends SimpleResponse {
    /**
     * @param jsonResponse
     */
    public HomeModeResponse(String jsonResponse) {
        super(jsonResponse);
    }

    /**
     * @return
     */
    public boolean isHomeMode() {
        return getData().get("on").getAsBoolean();
    }

    public int getReason() {
        return getData().get("reason").getAsInt();
    }

}
