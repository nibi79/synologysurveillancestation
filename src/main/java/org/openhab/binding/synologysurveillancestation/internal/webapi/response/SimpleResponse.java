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
