package org.openhab.binding.synologysurveillancestation.internal.webapi;

import java.util.ArrayList;
import java.util.List;

import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;
import org.openhab.binding.synologysurveillancestation.internal.Config;
import org.openhab.binding.synologysurveillancestation.internal.webapi.response.CameraResponse;

/**
 * @author Nils
 *
 */
public class SynoApiCamera extends SynoApiRequest<CameraResponse> {

    private static final String API_VERSION = "8";
    private static final String API_NAME = "SYNO.SurveillanceStation.Camera";
    private static final String API_SCRIPT = "/webapi/entry.cgi";

    /**
     * @param config
     */
    public SynoApiCamera(Config config, String sessionID) {
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

    private CameraResponse call(String method) throws WebApiException {

        return call(method, null);
    }

    private CameraResponse call(String method, String cameraId) throws WebApiException {

        List<NameValuePair> params = new ArrayList<>();

        // API Parameters
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
     * api = SYNO.SurveillanceStation.Camera
     * method = List
     *
     * http://IP_ADRESS:PORT/webapi/entry.cgi?privCamType=3&version="8"&blIncludeDeletedCam=true&streamInfo=true
     * &blPrivilege=false&api="SYNO.SurveillanceStation.Camera"&basic=true&blFromCamList=true&camStm=1&method="List"&_sid=2323
     *
     * @return
     * @throws WebApiException
     */
    public CameraResponse list() throws WebApiException {

        return call("List", null);
    }

    public CameraResponse getInfo() throws WebApiException {

        return call("GetInfo");
    }

    public CameraResponse enable(String cameraId) throws WebApiException {

        return call("Enable", cameraId);
    }

    public CameraResponse disable(String cameraId) throws WebApiException {

        return call("Disable", cameraId);
    }

}
