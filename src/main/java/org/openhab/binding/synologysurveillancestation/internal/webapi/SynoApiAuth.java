package org.openhab.binding.synologysurveillancestation.internal.webapi;

import java.util.ArrayList;
import java.util.List;

import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;
import org.openhab.binding.synologysurveillancestation.internal.Config;
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
 * @author Nils
 *
 */
public class SynoApiAuth extends SynoApiRequest<AuthResponse> {

    // API configuration
    private static final String API_NAME = "SYNO.API.Auth";
    private static final SynoApiConfig apiConfig = new SynoApiConfig(API_NAME, ApiConstants.API_VERSION_06,
            ApiConstants.API_SCRIPT_AUTH);

    /**
     * @param config
     */
    public SynoApiAuth(Config config) {

        super(apiConfig, config, null);
    }

    /**
     * Calls the passed method.
     *
     * @param method
     * @return
     * @throws WebApiException
     */
    private AuthResponse call(String method) throws WebApiException {

        List<NameValuePair> params = new ArrayList<>();

        // API parameters
        params.add(new BasicNameValuePair("account", getConfig().getUsername()));
        params.add(new BasicNameValuePair("passwd", getConfig().getPassword()));

        params.add(new BasicNameValuePair("session", "SurveillanceStation"));
        params.add(new BasicNameValuePair("format", "sid"));

        return callApi(method, params);
    }

    /**
     * Create new login session.
     *
     * @return
     * @throws WebApiException
     */
    public AuthResponse login() throws WebApiException {

        return call(ApiConstants.METHOD_LOGIN);
    }

    /**
     * Destroy current login session.
     *
     * @return
     * @throws WebApiException
     */
    public AuthResponse logout(String sessionID) throws WebApiException {

        return call(ApiConstants.METHOD_LOGOUT);
    }

}
