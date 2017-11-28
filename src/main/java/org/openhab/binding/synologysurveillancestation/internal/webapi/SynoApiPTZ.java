package org.openhab.binding.synologysurveillancestation.internal.webapi;

import java.util.ArrayList;
import java.util.List;

import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;
import org.openhab.binding.synologysurveillancestation.internal.Config;
import org.openhab.binding.synologysurveillancestation.internal.webapi.response.SimpleResponse;

/**
 * GET /webapi/entry.cgi?api=SYNO.SurveillanceStation.PTZ&method=Zoom&version=1&cameraId=10&control=in&moveType=Start
 *
 * @author Nils
 *
 */
public class SynoApiPTZ extends SynoApiRequest<SimpleResponse> {

    // TODO Check version 3 -< invalid parameter?
    private static final String API_VERSION = "3";
    private static final String API_NAME = "SYNO.SurveillanceStation.PTZ";
    private static final String API_SCRIPT = "/webapi/entry.cgi";

    private static final String METHOD_ZOOM = "Zoom";
    private static final String METHOD_MOVE = "Move";

    /**
     * @param config
     */
    public SynoApiPTZ(Config config, String sessionID) {
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

    private SimpleResponse callZoom(String method, String cameraId, String control) throws WebApiException {

        List<NameValuePair> params = new ArrayList<>();

        // API Parameters
        params.add(new BasicNameValuePair("cameraId", cameraId));
        params.add(new BasicNameValuePair("control", control));
        params.add(new BasicNameValuePair("moveType", "Start"));

        return callApi(method, params);
    }

    private SimpleResponse callMove(String method, String cameraId, String direction, int speed)
            throws WebApiException {

        List<NameValuePair> params = new ArrayList<>();

        // API Parameters
        params.add(new BasicNameValuePair("cameraId", cameraId));
        params.add(new BasicNameValuePair("direction", direction));
        params.add(new BasicNameValuePair("speed", String.valueOf(speed)));
        params.add(new BasicNameValuePair("moveType", "Start"));

        return callApi(method, params);
    }

    public SimpleResponse zoomOut(String camerId) throws WebApiException {

        return callZoom(METHOD_ZOOM, camerId, "out");
    }

    public SimpleResponse zoomIn(String camerId) throws WebApiException {

        return callZoom(METHOD_ZOOM, camerId, "in");
    }

    public SimpleResponse moveUp(String camerId) throws WebApiException {

        return callMove(METHOD_MOVE, camerId, "up", 1);
    }

    public SimpleResponse moveDown(String camerId) throws WebApiException {

        return callMove(METHOD_MOVE, camerId, "down", 1);
    }

    public SimpleResponse moveLeft(String camerId) throws WebApiException {

        return callMove(METHOD_MOVE, camerId, "left", 1);
    }

    public SimpleResponse moveRight(String camerId) throws WebApiException {

        return callMove(METHOD_MOVE, camerId, "right", 1);
    }

}
