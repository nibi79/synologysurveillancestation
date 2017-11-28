package org.openhab.binding.synologysurveillancestation.internal.webapi;

import org.openhab.binding.synologysurveillancestation.internal.Config;
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
 * @author Nils
 *
 */
public class SynoApiInfo extends SynoApiRequest<InfoResponse> {

    // API Configuration
    private static final String API_VERSION = "5";
    private static final String API_NAME = "SYNO.SurveillanceStation.Info";
    private static final String API_SCRIPT = "/webapi/entry.cgi";
    private static final SynoApiConfig apiConfig = new SynoApiConfig(API_NAME, API_VERSION, API_SCRIPT);

    /**
     * @param config
     */
    public SynoApiInfo(Config config, String sessionID) {
        super(apiConfig, config, sessionID);
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
