package org.openhab.binding.synologysurveillancestation.internal.webapi;

import java.util.ArrayList;
import java.util.List;

import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;
import org.openhab.binding.synologysurveillancestation.internal.Config;
import org.openhab.binding.synologysurveillancestation.internal.webapi.response.SimpleResponse;

/**
 * SYNO.SurveillanceStation.ExternalRecording
 *
 * This API provides methods to start or stop external recording of a camera.
 *
 * Method:
 * - Record
 *
 * @author Nils
 *
 */
public class SynoApiExternalRecording extends SynoApiRequest<SimpleResponse> {

    // API configuration
    // TODO Check version 3 -< invalid parameter?
    private static final String API_NAME = "SYNO.SurveillanceStation.ExternalRecording";
    private static final SynoApiConfig apiConfig = new SynoApiConfig(API_NAME, API_VERSION_02, API_SCRIPT_ENTRY);

    /**
     * @param config
     */
    public SynoApiExternalRecording(Config config, String sessionID) {
        super(apiConfig, config, sessionID);
    }

    /**
     * @param method
     * @param cameraId
     * @param action
     * @return
     * @throws WebApiException
     */
    private SimpleResponse call(String method, String cameraId, String action) throws WebApiException {

        List<NameValuePair> params = new ArrayList<>();

        // API parameters
        params.add(new BasicNameValuePair("cameraId", cameraId));
        params.add(new BasicNameValuePair("action", action));

        return callApi(method, params);
    }

    /**
     * Start external recording of a camera.
     *
     * @param camerId
     * @return
     * @throws WebApiException
     */
    public SimpleResponse startRecording(String camerId) throws WebApiException {

        return call(METHOD_RECORD, camerId, "start");
    }

    /**
     * Stop external recording of a camera.
     *
     * @param camerId
     * @return
     * @throws WebApiException
     */
    public SimpleResponse stopRecording(String camerId) throws WebApiException {

        return call(METHOD_RECORD, camerId, "stop");
    }

}
