/**
 * Copyright (c) 2010-2019 by the respective copyright holders.
 *
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 */
package org.openhab.binding.synologysurveillancestation.internal.webapi.request;

import java.util.HashMap;
import java.util.Map;

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
public class SynoApiAuth extends SynoApiRequest<AuthResponse> {

    // API configuration
    private static final String API_NAME = "SYNO.API.Auth";
    private static final SynoApiConfig API_CONFIG = new SynoApiConfig(API_NAME, API_VERSION_06, API_SCRIPT_AUTH);

    /**
     * @param config
     */
    public SynoApiAuth(SynoConfig config, HttpClient httpClient) {
        super(API_CONFIG, config, null, httpClient);
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
    public AuthResponse logout(String sessionID) throws WebApiException {
        return call(METHOD_LOGOUT);
    }

}
