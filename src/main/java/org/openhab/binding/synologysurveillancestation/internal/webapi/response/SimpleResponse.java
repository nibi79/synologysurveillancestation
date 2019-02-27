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

/**
 * {@link SimpleResponse} is a simplest implementation of an API response
 *
 * @author Nils - Initial contribution
 */
@NonNullByDefault
public class SimpleResponse extends SynoApiResponse {

    /**
     * @param jsonResponse
     */
    public SimpleResponse(String jsonResponse) {
        super(jsonResponse);
    }

}
