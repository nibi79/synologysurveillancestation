package org.openhab.binding.synologysurveillancestation.internal.webapi;

import java.util.ArrayList;
import java.util.List;

import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;
import org.openhab.binding.synologysurveillancestation.internal.Config;
import org.openhab.binding.synologysurveillancestation.internal.webapi.response.CameraResponse;

/**
 * SYNO.SurveillanceStation.Camera
 *
 * This API provides a set of methods to acquire camera-related information and to enable/disable cameras.
 *
 * Method:
 * - Save
 * - List
 * - GetInfo
 * - ListGroup
 * - GetSnapshot
 * - Enable
 * - Disable
 * - GetCapabilityByCamId
 * - MigrationEnum
 * - Migrate
 * - CountByCategory
 * - RecountEventSize
 * - SaveOptimizeParam
 * - GetOccupiedSize
 * - CheckCamValid
 * - MigrationCancel
 * - Delete
 * - GetLiveViewPath
 *
 * @author Nils
 *
 */
public class SynoApiCamera extends SynoApiRequest<CameraResponse> {

    // API configuration
    private static final String API_VERSION = "8";
    private static final String API_NAME = "SYNO.SurveillanceStation.Camera";
    private static final String API_SCRIPT = "/webapi/entry.cgi";
    private static final SynoApiConfig apiConfig = new SynoApiConfig(API_NAME, API_VERSION, API_SCRIPT);

    /**
     * @param config
     */
    public SynoApiCamera(Config config, String sessionID) {
        super(apiConfig, config, sessionID);
    }

    /**
     * Calls the passed method for all cameras.
     *
     * @param method
     * @return
     * @throws WebApiException
     */
    private CameraResponse call(String method) throws WebApiException {

        return call(method, null);
    }

    /**
     * Calls the passed method.
     *
     * @param method
     * @param cameraId
     * @return
     * @throws WebApiException
     */
    private CameraResponse call(String method, String cameraId) throws WebApiException {

        List<NameValuePair> params = new ArrayList<>();

        // API parameters
        params.add(new BasicNameValuePair("blFromCamList", API_TRUE));
        params.add(new BasicNameValuePair("privCamType", API_TRUE));
        params.add(new BasicNameValuePair("blIncludeDeletedCam", API_FALSE));
        params.add(new BasicNameValuePair("basic", API_TRUE));
        params.add(new BasicNameValuePair("streamInfo", API_TRUE));
        params.add(new BasicNameValuePair("blPrivilege", API_FALSE));
        params.add(new BasicNameValuePair("camStm", "1"));

        if (cameraId != null) {
            params.add(new BasicNameValuePair("cameraIds", cameraId));
        }

        return callApi(method, params);
    }

    /**
     * Get the list of all cameras.
     *
     * @return
     * @throws WebApiException
     */
    public CameraResponse list() throws WebApiException {

        return call("List");
    }

    /**
     * Get specific camera settings.
     *
     * @param cameraId
     * @return
     * @throws WebApiException
     */
    public CameraResponse getInfo(String cameraId) throws WebApiException {

        return call("GetInfo", cameraId);
    }

    /**
     * Enable camera.
     *
     * @param cameraId
     * @return
     * @throws WebApiException
     */
    public CameraResponse enable(String cameraId) throws WebApiException {

        return call("Enable", cameraId);
    }

    /**
     * Disable camera.
     *
     * @param cameraId
     * @return
     * @throws WebApiException
     */
    public CameraResponse disable(String cameraId) throws WebApiException {

        return call("Disable", cameraId);
    }

}
