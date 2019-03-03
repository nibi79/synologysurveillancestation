/**
 * Copyright (c) 2010-2019 Contributors to the openHAB project
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
package org.openhab.binding.synologysurveillancestation.internal.webapi.request;

import org.eclipse.jdt.annotation.NonNullByDefault;
import org.eclipse.jetty.client.HttpClient;
import org.openhab.binding.synologysurveillancestation.internal.SynoConfig;
import org.openhab.binding.synologysurveillancestation.internal.webapi.WebApiException;
import org.openhab.binding.synologysurveillancestation.internal.webapi.error.WebApiAuthErrorCodes;
import org.openhab.binding.synologysurveillancestation.internal.webapi.response.InfoResponse;

/**
 * SYNO.SurveillanceStation.Info
 *
 * This API provides a method to acquire Surveillance Station related information such as package version, package UI
 * path, and the total number of camera and installed licenses.
 *
 * Method:
 * - GetInfo
 *
 * @author Nils - Initial contribution
 * @author Pavion - Contribution
 */
@NonNullByDefault
public class SynoApiInfo extends SynoApiRequest<InfoResponse> {

    // API Configuration
    private static final String API_NAME = "SYNO.SurveillanceStation.Info";
    private static final SynoApiConfig API_CONFIG = new SynoApiConfig(API_NAME, API_VERSION_05, API_SCRIPT_ENTRY);

    /**
     * @param config
     */
    public SynoApiInfo(SynoConfig config, HttpClient httpClient) {
        super(API_CONFIG, config, httpClient);
    }

    /**
     * Get Surveillance Station related general information.
     *
     * @return
     * @throws WebApiException
     */
    public InfoResponse getInfo() throws WebApiException {
        InfoResponse response = callApi(METHOD_GETINFO);

        if (!response.isSuccess()) {
            throw new WebApiException(WebApiAuthErrorCodes.getByCode(response.getErrorcode()));
        }

        return response;
    }
}
