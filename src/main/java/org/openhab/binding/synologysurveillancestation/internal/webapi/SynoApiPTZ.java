package org.openhab.binding.synologysurveillancestation.internal.webapi;

import java.util.ArrayList;
import java.util.List;

import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;
import org.openhab.binding.synologysurveillancestation.internal.Config;
import org.openhab.binding.synologysurveillancestation.internal.webapi.response.SimpleResponse;

/**
 * From functional spec:
 *
 * This API provides a set of methods to execute PTZ action, and to acquire PTZ related information such as
 * patrol list or patrol schedule of a camera.
 *
 * @author Nils
 *
 */
public class SynoApiPTZ extends SynoApiRequest<SimpleResponse> {

    // API configuration
    // TODO Check version 3 -< invalid parameter?
    private static final String API_VERSION = "3";
    private static final String API_NAME = "SYNO.SurveillanceStation.PTZ";
    private static final String API_SCRIPT = "/webapi/entry.cgi";
    private static final SynoApiConfig apiConfig = new SynoApiConfig(API_NAME, API_VERSION, API_SCRIPT);

    // API methods
    private static final String METHOD_ZOOM = "Zoom";
    private static final String METHOD_MOVE = "Move";

    /**
     * @param config
     */
    public SynoApiPTZ(Config config, String sessionID) {
        super(apiConfig, config, sessionID);
    }

    /**
     * calls api method 'zoom' with passed control
     *
     * @param cameraId
     * @param control
     * @return
     * @throws WebApiException
     */
    private SimpleResponse callZoom(String cameraId, String control) throws WebApiException {

        List<NameValuePair> params = new ArrayList<>();

        // API parameters
        params.add(new BasicNameValuePair("cameraId", cameraId));
        params.add(new BasicNameValuePair("control", control));
        params.add(new BasicNameValuePair("moveType", "Start"));

        return callApi(METHOD_ZOOM, params);
    }

    /**
     * calls api method 'move' with passed direction and speed
     *
     * @param cameraId
     * @param direction
     * @param speed
     * @return
     * @throws WebApiException
     */
    private SimpleResponse callMove(String cameraId, String direction, int speed) throws WebApiException {

        List<NameValuePair> params = new ArrayList<>();

        // API Parameters
        params.add(new BasicNameValuePair("cameraId", cameraId));
        params.add(new BasicNameValuePair("direction", direction));
        params.add(new BasicNameValuePair("speed", String.valueOf(speed)));
        params.add(new BasicNameValuePair("moveType", "Start"));

        return callApi(METHOD_MOVE, params);
    }

    /**
     * Control the PTZ camera to zoom out.
     *
     * @param camerId
     * @return
     * @throws WebApiException
     */
    public SimpleResponse zoomOut(String camerId) throws WebApiException {

        return callZoom(camerId, "out");
    }

    /**
     * Control the PTZ camera to zoom in.
     *
     * @param camerId
     * @return
     * @throws WebApiException
     */
    public SimpleResponse zoomIn(String camerId) throws WebApiException {

        return callZoom(camerId, "in");
    }

    /**
     * Control the PTZ camera to move its lens up.
     *
     * @param camerId
     * @return
     * @throws WebApiException
     */
    public SimpleResponse moveUp(String camerId) throws WebApiException {

        return callMove(camerId, "up", 1);
    }

    /**
     * Control the PTZ camera to move its lens down.
     *
     * @param camerId
     * @return
     * @throws WebApiException
     */
    public SimpleResponse moveDown(String camerId) throws WebApiException {

        return callMove(camerId, "down", 1);
    }

    /**
     * Control the PTZ camera to move its lens left.
     *
     * @param camerId
     * @return
     * @throws WebApiException
     */
    public SimpleResponse moveLeft(String camerId) throws WebApiException {

        return callMove(camerId, "left", 1);
    }

    /**
     * Control the PTZ camera to move its lens right.
     *
     * @param camerId
     * @return
     * @throws WebApiException
     */
    public SimpleResponse moveRight(String camerId) throws WebApiException {

        return callMove(camerId, "right", 1);
    }

}
