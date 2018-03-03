/**
 * Copyright (c) 2010-2018 by the respective copyright holders.
 *
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 */
package org.openhab.binding.synologysurveillancestation.internal.webapi;

import java.io.IOException;
import java.net.URISyntaxException;
import java.util.Map;

import org.openhab.binding.synologysurveillancestation.internal.webapi.response.CameraResponse;
import org.openhab.binding.synologysurveillancestation.internal.webapi.response.EventResponse;
import org.openhab.binding.synologysurveillancestation.internal.webapi.response.HomeModeResponse;
import org.openhab.binding.synologysurveillancestation.internal.webapi.response.InfoResponse;
import org.openhab.binding.synologysurveillancestation.internal.webapi.response.SimpleResponse;

/**
 * The {@link SynoWebApi} is an interface for the Web API
 *
 * @author Nils
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
    public SimpleResponse logout() throws WebApiException;

    // ----------------------------

    /**
     *
     * @return
     * @throws URISyntaxException
     * @throws IOException
     * @throws UnsupportedOperationException
     */
    public byte[] getSnapshot(String cameraId, int timeout, int streamId)
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
     * @param cameraId
     * @return
     * @throws WebApiException
     */
    public CameraResponse getInfo(String cameraId) throws WebApiException;

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
    public SimpleResponse enable(String cameraId) throws WebApiException;

    /**
     * @param cameraId
     * @return
     * @throws WebApiException
     */
    public SimpleResponse disable(String cameraId) throws WebApiException;

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

    /**
     * @param cameraId
     * @return
     * @throws WebApiException
     */
    public SimpleResponse moveHome(String cameraId) throws WebApiException;

    /**
     * @param cameraId ID of the camera
     * @return Response with current events
     * @throws WebApiException
     */
    public EventResponse getEventResponse(String cameraId, long lastEventTime, Map<String, SynoEvent> events)
            throws WebApiException;

    /**
     *
     * @return Home Mode state
     */
    public HomeModeResponse getHomeModeResponse() throws WebApiException;

    /**
     * Turns the Home Mode on/off
     *
     * @param mode on/off
     * @return
     * @throws WebApiException
     */
    public SimpleResponse setHomeMode(boolean mode) throws WebApiException;
}
