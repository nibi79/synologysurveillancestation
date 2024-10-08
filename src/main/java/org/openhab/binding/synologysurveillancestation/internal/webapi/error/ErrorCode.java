/**
 * Copyright (c) 2010-2024 Contributors to the openHAB project
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
package org.openhab.binding.synologysurveillancestation.internal.webapi.error;

/**
 * The {@link ErrorCode} is an interface for error codes
 *
 * @author Nils - Initial contribution
 * @author Pavion - Contribution
 */
public interface ErrorCode {

    /**
     * @return
     */
    public int getCode();

    /**
     * @return
     */
    public String getMsg();

    /**
     *
     * Lookup ApiErrorCode.
     *
     * @param e
     * @param code
     * @return
     */
    static WebApiAuthErrorCodes lookup(int code) {
        for (WebApiAuthErrorCodes w : WebApiAuthErrorCodes.class.getEnumConstants()) {
            if (w.getCode() == code)
                return w;
        }
        return WebApiAuthErrorCodes.NO_ERROR;
    }
}
