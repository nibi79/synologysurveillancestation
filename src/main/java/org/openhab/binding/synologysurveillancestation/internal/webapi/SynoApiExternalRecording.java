package org.openhab.binding.synologysurveillancestation.internal.webapi;

import java.util.ArrayList;
import java.util.List;

import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;
import org.openhab.binding.synologysurveillancestation.internal.Config;
import org.openhab.binding.synologysurveillancestation.internal.webapi.response.SimpleResponse;

/**
 * @author Nils
 *
 */
public class SynoApiExternalRecording extends SynoApiRequest<SimpleResponse> {

    // TODO Check version 3 -< invalid parameter?
    private static final String API_VERSION = "2";
    private static final String API_NAME = "SYNO.SurveillanceStation.ExternalRecording";
    private static final String API_SCRIPT = "/webapi/entry.cgi";

    private static final String METHOD_RECORD = "Record";

    /**
     * @param config
     */
    public SynoApiExternalRecording(Config config, String sessionID) {
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

    private SimpleResponse call(String method, String cameraId, String action) throws WebApiException {

        List<NameValuePair> params = new ArrayList<>();

        // API Parameters
        params.add(new BasicNameValuePair("cameraId", cameraId));
        params.add(new BasicNameValuePair("action", action));

        return callApi(method, params);
    }

    public SimpleResponse startRecording(String camerId) throws WebApiException {

        return call(METHOD_RECORD, camerId, "start");
    }

    public SimpleResponse stopRecording(String camerId) throws WebApiException {

        return call(METHOD_RECORD, camerId, "stop");
    }

}
