package org.openhab.binding.synologysurveillancestation.internal.webapi;

import java.util.ArrayList;
import java.util.List;

import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;
import org.openhab.binding.synologysurveillancestation.internal.Config;
import org.openhab.binding.synologysurveillancestation.internal.webapi.response.AuthResponse;

/**
 * @author Nils
 *
 */
public class SynoApiAuth extends SynoApiRequest<AuthResponse> {

    private static final String API_VERSION = "3";
    private static final String API_NAME = "SYNO.API.Auth";
    private static final String API_SCRIPT = "/webapi/auth.cgi";

    /**
     * @param config
     */
    public SynoApiAuth(Config config) {

        super(config, null);
    }

    @Override
    public String getApiVersion() {
        return API_VERSION;
    }

    @Override
    public String getApiName() {
        return API_NAME;
    }

    @Override
    public String getApiScriptPath() {
        return API_SCRIPT;
    }

    private AuthResponse call(String method) throws WebApiException {

        List<NameValuePair> params = new ArrayList<>();

        // API Parameters
        params.add(new BasicNameValuePair("account", getConfig().getUsername()));
        params.add(new BasicNameValuePair("passwd", getConfig().getPassword()));

        params.add(new BasicNameValuePair("session", "SurveillanceStation"));
        params.add(new BasicNameValuePair("format", "sid"));

        return callApi(method, params);
    }

    /**
     * api = SYNO.Api.Auth
     * method = Login
     *
     * http://IP_ADRESS:PORT/webapi/auth.cgi?api=SYNO.API.Auth&method=Login&version=2&account=admin&passwd=123456&session=SurveillanceStation&format=sid
     *
     * @return
     * @throws WebApiException
     */
    public AuthResponse login() throws WebApiException {

        return call("login");
    }

    /**
     * api = SYNO.Api.Auth
     * method = Logout
     *
     * http://IP_ADRESS:PORT/webapi/auth.cgi?api=SYNO.API.Auth&method=Logout&version=2&session=SurveillanceStation&_sid=Jn5dZ9aS95wh2
     *
     * @return
     * @throws WebApiException
     */
    public AuthResponse logout(String sessionID) throws WebApiException {

        return call("logout");
    }

}
