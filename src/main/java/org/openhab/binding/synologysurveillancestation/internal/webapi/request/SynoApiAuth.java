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
package org.openhab.binding.synologysurveillancestation.internal.webapi.request;

import java.util.HashMap;
import java.util.Map;

import org.eclipse.jdt.annotation.NonNullByDefault;
import org.eclipse.jetty.client.HttpClient;
import org.openhab.binding.synologysurveillancestation.internal.SynoConfig;
import org.openhab.binding.synologysurveillancestation.internal.webapi.WebApiException;
import org.openhab.binding.synologysurveillancestation.internal.webapi.response.AuthResponse;

/**
 *
 * SYNO.API.Auth
 *
 * API used to perform session login and logout.
 *
 * Method:
 * - Login
 * - Logout
 *
 * @author Nils - Initial contribution
 * @author Pavion - Contribution
 *
 */
@NonNullByDefault
public class SynoApiAuth extends SynoApiRequest<AuthResponse> {

    // API configuration
    private static final String API_NAME = "SYNO.API.Auth";
    private static final SynoApiConfig API_CONFIG = new SynoApiConfig(API_NAME, API_VERSION_06, API_SCRIPT_AUTH);

    /**
     * @param config
     */
    public SynoApiAuth(SynoConfig config, HttpClient httpClient) {
        super(API_CONFIG, config, httpClient);
    }

    /**
     * Calls the passed method.
     *
     * @param method
     * @return
     * @throws WebApiException
     */
    private AuthResponse call(String method) throws WebApiException {
        Map<String, String> params = new HashMap<>();

        // API parameters
        params.put("account", getConfig().getUsername());
        params.put("passwd", getConfig().getPassword());

        params.put("session", "SurveillanceStation");
        params.put("format", "sid");

        if (getConfig().getUsername().equals("")) {
            throw new WebApiException(100, "Empty credentials");
        }
        return callApi(method, params);
    }

    /**
     * Create new login session.
     *
     * @return
     * @throws WebApiException
     */
    public AuthResponse login() throws WebApiException {
        return call(METHOD_LOGIN);
    }

    /**
     * Destroy current login session.
     *
     * @return
     * @throws WebApiException
     */
    public AuthResponse logout(String sessionId) throws WebApiException {
        Map<String, String> params = new HashMap<>();

        // API parameters
        params.put("session", "SurveillanceStation");
        params.put("_sid", sessionId);

        return callApi(METHOD_LOGOUT, params);
    }
}
