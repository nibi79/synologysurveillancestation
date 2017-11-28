package org.openhab.binding.synologysurveillancestation.internal.webapi;

import org.openhab.binding.synologysurveillancestation.internal.Config;
import org.openhab.binding.synologysurveillancestation.internal.webapi.response.InfoResponse;

/**
 * @author Nils
 *
 */
public class SynoApiInfo extends SynoApiRequest<InfoResponse> {

    private static final String API_VERSION = "5";
    private static final String API_NAME = "SYNO.SurveillanceStation.Info";
    private static final String API_SCRIPT = "/webapi/entry.cgi";

    /**
     * @param config
     */
    public SynoApiInfo(Config config, String sessionID) {
        super(config, sessionID);
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

    public InfoResponse call(String method) throws WebApiException {

        return callApi(method);
    }

    /**
     * api = SYNO.SurveillanceStation.Camera
     * method = List
     *
     * http://IP_ADRESS:PORT/webapi/info.cgi?api=SYNO.SurveillanceStation.Info&method=GetInfo&version=1
     *
     * @return
     * @throws WebApiException
     */
    public InfoResponse getInfo() throws WebApiException {
        return call("GetInfo");
    }
}
