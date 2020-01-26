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
import org.openhab.binding.synologysurveillancestation.internal.thread.SynoApiThread;

/**
 * {@link SynoApiThread} handles authentication response
 *
 * @author Nils - Initial contribution
 * @author Pavion - Contribution
 */
@NonNullByDefault
public class AuthResponse extends SimpleResponse {

    /**
     * @param jsonResponse
     */
    public AuthResponse(String jsonResponse) {
        super(jsonResponse);
    }

    /**
     * @return Session ID
     */
    public String getSid() {
        return getData().get("sid").getAsString();
    }

}
