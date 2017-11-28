package org.openhab.binding.synologysurveillancestation.internal.webapi;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.net.URISyntaxException;

import org.openhab.binding.synologysurveillancestation.internal.webapi.response.AuthResponse;
import org.openhab.binding.synologysurveillancestation.internal.webapi.response.CameraResponse;
import org.openhab.binding.synologysurveillancestation.internal.webapi.response.InfoResponse;
import org.openhab.binding.synologysurveillancestation.internal.webapi.response.SimpleResponse;

/**
 *
 * * @author Nils
 *
 */
public interface SynoWebApi {

    /**
     * Establish connection to Surveillance Station Web API
     *
     * @return
     * @throws WebApiException
     */
    public boolean connect() throws WebApiException;

    /**
     *
     * @return
     * @throws WebApiException
     */
    public AuthResponse logout() throws WebApiException;

    // ----------------------------

    /**
     *
     * @return
     * @throws URISyntaxException
     * @throws IOException
     * @throws UnsupportedOperationException
     */
    public ByteArrayOutputStream getSnapshot(String cameraId)
            throws WebApiException, UnsupportedOperationException, IOException, URISyntaxException;

    /**
     *
     * api = SYNO.SurveillanceStation.Camera
     * method = List
     *
     * @return
     * @throws WebApiException
     */
    public CameraResponse list() throws WebApiException;

    /**
     *
     * api = SYNO.SurveillanceStation.Info
     * method = GetInfo
     *
     * @return
     * @throws WebApiException
     */
    public InfoResponse getInfo() throws WebApiException;

    /**
     * @param cameraId
     * @return
     * @throws WebApiException
     */
    public SimpleResponse startRecording(String cameraId) throws WebApiException;

    /**
     * @param cameraId
     * @return
     * @throws WebApiException
     */
    public SimpleResponse stopRecording(String cameraId) throws WebApiException;

    /**
     * @param cameraId
     * @return
     * @throws WebApiException
     */
    public CameraResponse enable(String cameraId) throws WebApiException;

    /**
     * @param cameraId
     * @return
     * @throws WebApiException
     */
    public CameraResponse disable(String cameraId) throws WebApiException;

    /**
     * @param cameraId
     * @return
     * @throws WebApiException
     */
    public SimpleResponse zoomIn(String cameraId) throws WebApiException;

    /**
     * @param cameraId
     * @return
     * @throws WebApiException
     */
    public SimpleResponse zoomOut(String cameraId) throws WebApiException;

    /**
     * @param cameraId
     * @return
     * @throws WebApiException
     */
    public SimpleResponse moveUp(String cameraId) throws WebApiException;

    /**
     * @param cameraId
     * @return
     * @throws WebApiException
     */
    public SimpleResponse moveDown(String cameraId) throws WebApiException;

    /**
     * @param cameraId
     * @return
     * @throws WebApiException
     */
    public SimpleResponse moveLeft(String cameraId) throws WebApiException;

    /**
     * @param cameraId
     * @return
     * @throws WebApiException
     */
    public SimpleResponse moveRight(String cameraId) throws WebApiException;
}
