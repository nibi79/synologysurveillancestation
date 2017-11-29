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
    private static final String API_NAME = "SYNO.SurveillanceStation.Info";
    private static final SynoApiConfig apiConfig = new SynoApiConfig(API_NAME, API_VERSION_05, API_SCRIPT_ENTRY);

    /**
     * @param config
     */
    public SynoApiInfo(Config config, String sessionID) {
        super(apiConfig, config, sessionID);
    }

    /**
     * Get Surveillance Station related general information.
     *
     * @return
     * @throws WebApiException
     */
    public InfoResponse getInfo() throws WebApiException {
        return callApi(METHOD_GETINFO);
    }
}
