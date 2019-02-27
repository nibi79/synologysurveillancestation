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
