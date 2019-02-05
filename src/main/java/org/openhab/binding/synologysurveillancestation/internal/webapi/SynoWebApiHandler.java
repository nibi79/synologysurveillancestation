/**
 * Copyright (c) 2010-2019 by the respective copyright holders.
 *
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 */
package org.openhab.binding.synologysurveillancestation.internal.webapi;

import static org.openhab.binding.synologysurveillancestation.SynoBindingConstants.*;

import java.io.IOException;
import java.net.URISyntaxException;
import java.util.Map;

import org.eclipse.jetty.client.HttpClient;
import org.openhab.binding.synologysurveillancestation.internal.SynoConfig;
import org.openhab.binding.synologysurveillancestation.internal.webapi.error.WebApiAuthErrorCodes;
import org.openhab.binding.synologysurveillancestation.internal.webapi.request.SynoApiAuth;
import org.openhab.binding.synologysurveillancestation.internal.webapi.request.SynoApiCamera;
import org.openhab.binding.synologysurveillancestation.internal.webapi.request.SynoApiEvent;
import org.openhab.binding.synologysurveillancestation.internal.webapi.request.SynoApiExternalEvent;
import org.openhab.binding.synologysurveillancestation.internal.webapi.request.SynoApiExternalRecording;
import org.openhab.binding.synologysurveillancestation.internal.webapi.request.SynoApiHomeMode;
import org.openhab.binding.synologysurveillancestation.internal.webapi.request.SynoApiInfo;
import org.openhab.binding.synologysurveillancestation.internal.webapi.request.SynoApiLiveUri;
import org.openhab.binding.synologysurveillancestation.internal.webapi.request.SynoApiPTZ;
import org.openhab.binding.synologysurveillancestation.internal.webapi.response.AuthResponse;
import org.openhab.binding.synologysurveillancestation.internal.webapi.response.CameraResponse;
import org.openhab.binding.synologysurveillancestation.internal.webapi.response.EventResponse;
import org.openhab.binding.synologysurveillancestation.internal.webapi.response.HomeModeResponse;
import org.openhab.binding.synologysurveillancestation.internal.webapi.response.InfoResponse;
import org.openhab.binding.synologysurveillancestation.internal.webapi.response.LiveUriResponse;
import org.openhab.binding.synologysurveillancestation.internal.webapi.response.SimpleResponse;

/**
 * The {@link SynoWebApiHandler} is a facade for Synology Surveillance Station Web API.
 *
 * @author Nils - Initial contribution
 * @author Pavion - Contribution
 */
public class SynoWebApiHandler implements SynoWebApi {

    private SynoConfig config = null;
    private String sessionID = null;

    // APIs
    private SynoApiAuth apiAuth = null;
    private SynoApiInfo apiInfo = null;
    private SynoApiCamera apiCamera = null;
    private SynoApiEvent apiEvent = null;
    private SynoApiHomeMode apiHomeMode = null;
    private SynoApiExternalRecording apiExternalRecording = null;
    private SynoApiPTZ apiPTZ = null;
    private SynoApiLiveUri apiLiveUri = null;
    private SynoApiExternalEvent apiExternalEvent = null;
    private final HttpClient httpClient;

    /**
     * @param config
     */
    public SynoWebApiHandler(SynoConfig config, HttpClient httpClient) {
        this.httpClient = httpClient;
        this.config = config;
    }

    /**
     * @return
     */
    public SynoConfig getConfig() {
        return config;
    }

    /**
     * @return
     */
    public String getSessionID() {
        return sessionID;
    }

    /*
     * (non-Javadoc)
     *
     * @see org.openhab.binding.synologysurveillancestation.internal.webapi.SynoWebApi#connect()
     */
    @Override
    public synchronized boolean connect() throws WebApiException {
        apiAuth = new SynoApiAuth(config, httpClient);
        boolean connected = createSession();

        // initialize APIs
        apiInfo = new SynoApiInfo(config, sessionID, httpClient);
        apiCamera = new SynoApiCamera(config, sessionID, httpClient);
        apiEvent = new SynoApiEvent(config, sessionID, httpClient);
        apiExternalRecording = new SynoApiExternalRecording(config, sessionID, httpClient);
        apiPTZ = new SynoApiPTZ(config, sessionID, httpClient);
        apiHomeMode = new SynoApiHomeMode(config, sessionID, httpClient);
        apiLiveUri = new SynoApiLiveUri(config, sessionID, httpClient);
        apiExternalEvent = new SynoApiExternalEvent(config, sessionID, httpClient);

        return connected;
    }

    /**
     * Execute the given method for the passed camera.
     *
     * @param cameraId
     * @param method
     * @param command
     * @throws WebApiException
     */
    public void execute(String cameraId, String method, String command) throws WebApiException {
        switch (method) {
            case CHANNEL_ENABLE:
                switch (command) {
                    case "ON":
                        enable(cameraId);
                        break;
                    case "OFF":
                        disable(cameraId);
                        break;
                }
                break;
            case CHANNEL_RECORD:
                switch (command) {
                    case "ON":
                        startRecording(cameraId);
                        break;
                    case "OFF":
                        stopRecording(cameraId);
                        break;
                }
                break;
            case CHANNEL_ZOOM:
                switch (command) {
                    case "IN":
                        zoomIn(cameraId);
                        break;
                    case "OUT":
                        zoomOut(cameraId);
                        break;
                }
                break;
            case CHANNEL_MOVE:
                switch (command) {
                    case "UP":
                        moveUp(cameraId);
                        break;
                    case "DOWN":
                        moveDown(cameraId);
                        break;
                    case "LEFT":
                        moveLeft(cameraId);
                        break;
                    case "RIGHT":
                        moveRight(cameraId);
                        break;
                    case "HOME":
                        moveHome(cameraId);
                        break;
                }
                break;
            default:
                break;
        }
    }

    /**
     * @return
     * @throws WebApiException
     */
    private boolean createSession() throws WebApiException {
        if (this.sessionID != null) {
            logout();
        }

        AuthResponse response = login();
        if (response == null) {
            throw new WebApiException(WebApiAuthErrorCodes.API_VERSION_NOT_SUPPORTED);
        } else if (response.isSuccess()) {
            sessionID = response.getSid();
            return true;
        } else {
            throw new WebApiException(WebApiAuthErrorCodes.getByCode(response.getErrorcode()));
        }
    }

    /**
     * Check if request was successful. If not WebApiException with API errorcode is thrown.
     *
     * @param response
     * @return
     * @throws WebApiException
     */
    private SimpleResponse handleSimpleResponse(SimpleResponse response) throws WebApiException {
        return handleSimpleResponse(response, false);
    }

    /**
     * Check if request was successful. If not WebApiException with API errorcode is thrown. Can be overridden with
     * allowError
     *
     * @param response
     * @param allowError allow errors to be passed through
     * @return
     * @throws WebApiException
     */
    private SimpleResponse handleSimpleResponse(SimpleResponse response, boolean allowError) throws WebApiException {
        if (response.isSuccess() || allowError) {
            return response;
        } else {
            throw new WebApiException(WebApiAuthErrorCodes.getByCode(response.getErrorcode()));
        }
    }

    /**
     * @return
     * @throws WebApiException
     */
    private AuthResponse login() throws WebApiException {
        return apiAuth.login();
    }

    /*
     * (non-Javadoc)
     *
     * @see org.eclipse.smarthome.binding.synologysurveillancestation.internal.webapi.SynoWebApi#logout()
     */
    @Override
    public SimpleResponse logout() throws WebApiException {
        SimpleResponse response = apiAuth.logout(sessionID);
        this.sessionID = null;

        if (response.isSuccess()) {
            return response;
        } else {
            throw new WebApiException(WebApiAuthErrorCodes.getByCode(response.getErrorcode()));
        }
    }

    /*
     * (non-Javadoc)
     *
     * @see org.eclipse.smarthome.binding.synologysurveillancestation.internal.webapi.SynoWebApi#getSnapshot(java.lang.
     * String)
     */
    @Override
    public byte[] getSnapshot(String cameraId, int timeout, int streamId)
            throws IOException, URISyntaxException, WebApiException {
        if (apiCamera == null) {
            return new byte[0];
        }
        return apiCamera.getSnapshot(cameraId, timeout, streamId);
    }

    /*
     * (non-Javadoc)
     *
     * @see
     * org.eclipse.smarthome.binding.synologysurveillancestation.internal.webapi.SynoWebApi#getSnapshotUri(java.lang.
     * String, java.lang.Integer streamId)
     */
    @Override
    public String getSnapshotUri(String cameraId, int streamId) throws WebApiException {
        if (apiCamera == null) {
            return "";
        }
        return apiCamera.getSnapshotUri(cameraId, streamId);
    }

    /*
     * (non-Javadoc)
     *
     * @see org.eclipse.smarthome.binding.synologysurveillancestation.internal.webapi.SynoWebApi#list()
     */
    @Override
    public CameraResponse list() throws WebApiException {
        CameraResponse response = apiCamera.list();

        if (!response.isSuccess()) {
            throw new WebApiException(WebApiAuthErrorCodes.getByCode(response.getErrorcode()));
        }

        return response;
    }

    /*
     * (non-Javadoc)
     *
     * @see org.openhab.binding.synologysurveillancestation.internal.webapi.SynoWebApi#getInfo(java.lang.String)
     */
    @Override
    public CameraResponse getInfo(String cameraId) throws WebApiException {
        CameraResponse response = apiCamera.getInfo(cameraId);

        if (!response.isSuccess()) {
            throw new WebApiException(WebApiAuthErrorCodes.getByCode(response.getErrorcode()));
        }

        return response;
    }

    /*
     * (non-Javadoc)
     *
     * @see org.openhab.binding.synologysurveillancestation.internal.webapi.SynoWebApi#getInfo()
     */
    @Override
    public InfoResponse getInfo() throws WebApiException {
        InfoResponse response = apiInfo.getInfo();

        if (!response.isSuccess()) {
            throw new WebApiException(WebApiAuthErrorCodes.getByCode(response.getErrorcode()));
        }

        return response;
    }

    /*
     * (non-Javadoc)
     *
     * @see org.openhab.binding.synologysurveillancestation.internal.webapi.SynoWebApi#enable(java.lang.String)
     */
    @Override
    public SimpleResponse enable(String cameraId) throws WebApiException {
        SimpleResponse response = apiCamera.enable(cameraId);
        return handleSimpleResponse(response);
    }

    /*
     * (non-Javadoc)
     *
     * @see org.openhab.binding.synologysurveillancestation.internal.webapi.SynoWebApi#disable(java.lang.String)
     */
    @Override
    public SimpleResponse disable(String cameraId) throws WebApiException {
        SimpleResponse response = apiCamera.disable(cameraId);
        return handleSimpleResponse(response);
    }

    /*
     * (non-Javadoc)
     *
     * @see org.openhab.binding.synologysurveillancestation.internal.webapi.SynoWebApi#startRecording(java.lang.String)
     */
    @Override
    public SimpleResponse startRecording(String cameraId) throws WebApiException {
        SimpleResponse response = apiExternalRecording.startRecording(cameraId);
        return handleSimpleResponse(response);
    }

    /*
     * (non-Javadoc)
     *
     * @see org.openhab.binding.synologysurveillancestation.internal.webapi.SynoWebApi#stopRecording(java.lang.String)
     */
    @Override
    public SimpleResponse stopRecording(String cameraId) throws WebApiException {
        SimpleResponse response = apiExternalRecording.stopRecording(cameraId);
        return handleSimpleResponse(response);

    }

    /*
     * (non-Javadoc)
     *
     * @see org.openhab.binding.synologysurveillancestation.internal.webapi.SynoWebApi#zoomIn(java.lang.String)
     */
    @Override
    public SimpleResponse zoomIn(String cameraId) throws WebApiException {
        SimpleResponse response = apiPTZ.zoomIn(cameraId);
        return handleSimpleResponse(response);
    }

    /*
     * (non-Javadoc)
     *
     * @see org.openhab.binding.synologysurveillancestation.internal.webapi.SynoWebApi#zoomOut(java.lang.String)
     */
    @Override
    public SimpleResponse zoomOut(String cameraId) throws WebApiException {
        SimpleResponse response = apiPTZ.zoomOut(cameraId);
        return handleSimpleResponse(response);
    }

    /*
     * (non-Javadoc)
     *
     * @see org.openhab.binding.synologysurveillancestation.internal.webapi.SynoWebApi#moveUp(java.lang.String)
     */
    @Override
    public SimpleResponse moveUp(String cameraId) throws WebApiException {
        SimpleResponse response = apiPTZ.moveUp(cameraId);
        return handleSimpleResponse(response);
    }

    /*
     * (non-Javadoc)
     *
     * @see org.openhab.binding.synologysurveillancestation.internal.webapi.SynoWebApi#moveDown(java.lang.String)
     */
    @Override
    public SimpleResponse moveDown(String cameraId) throws WebApiException {
        SimpleResponse response = apiPTZ.moveDown(cameraId);

        return handleSimpleResponse(response);
    }

    /*
     * (non-Javadoc)
     *
     * @see org.openhab.binding.synologysurveillancestation.internal.webapi.SynoWebApi#moveLeft(java.lang.String)
     */
    @Override
    public SimpleResponse moveLeft(String cameraId) throws WebApiException {
        SimpleResponse response = apiPTZ.moveLeft(cameraId);
        return handleSimpleResponse(response);
    }

    /*
     * (non-Javadoc)
     *
     * @see org.openhab.binding.synologysurveillancestation.internal.webapi.SynoWebApi#moveRight(java.lang.String)
     */
    @Override
    public SimpleResponse moveRight(String cameraId) throws WebApiException {
        SimpleResponse response = apiPTZ.moveRight(cameraId);
        return handleSimpleResponse(response);
    }

    /*
     * (non-Javadoc)
     *
     * @see org.openhab.binding.synologysurveillancestation.internal.webapi.SynoWebApi#moveHome(java.lang.String)
     */
    @Override
    public SimpleResponse moveHome(String cameraId) throws WebApiException {
        SimpleResponse response = apiPTZ.moveHome(cameraId);
        return handleSimpleResponse(response);
    }

    /*
     * (non-Javadoc)
     *
     * @see org.openhab.binding.synologysurveillancestation.internal.webapi.SynoWebApi#listPresets(java.lang.String)
     */
    @Override
    public SimpleResponse listPresets(String cameraId) throws WebApiException {
        SimpleResponse response = apiPTZ.listPresets(cameraId);
        return handleSimpleResponse(response, true); // allow errors
    }

    /*
     * (non-Javadoc)
     *
     * @see org.openhab.binding.synologysurveillancestation.internal.webapi.SynoWebApi#goPreset(java.lang.String,
     * java.lang.String)
     */
    @Override
    public SimpleResponse goPreset(String cameraId, String presetId) throws WebApiException {
        SimpleResponse response = apiPTZ.goPreset(cameraId, presetId);
        return handleSimpleResponse(response);
    }

    /*
     * (non-Javadoc)
     *
     * @see org.openhab.binding.synologysurveillancestation.internal.webapi.SynoWebApi#listPatrol(java.lang.String)
     */
    @Override
    public SimpleResponse listPatrol(String cameraId) throws WebApiException {
        SimpleResponse response = apiPTZ.listPatrol(cameraId);
        return handleSimpleResponse(response, true); // allow errors
    }

    /*
     * (non-Javadoc)
     *
     * @see org.openhab.binding.synologysurveillancestation.internal.webapi.SynoWebApi#runPatrol(java.lang.String,
     * java.lang.String)
     */
    @Override
    public SimpleResponse runPatrol(String cameraId, String patrolId) throws WebApiException {
        SimpleResponse response = apiPTZ.runPatrol(cameraId, patrolId);
        return handleSimpleResponse(response);
    }

    /*
     * (non-Javadoc)
     *
     * @see org.openhab.binding.synologysurveillancestation.internal.webapi.SynoWebApi#getEvents(java.lang.String)
     */
    @Override
    public EventResponse getEventResponse(String cameraId, long lastEventTime, Map<String, SynoEvent> events)
            throws WebApiException {
        EventResponse response = apiEvent.query(cameraId, lastEventTime, events);

        return response;
    }

    @Override
    public HomeModeResponse getHomeModeResponse() throws WebApiException {
        HomeModeResponse response = apiHomeMode.query();
        return response;
    }

    @Override
    public SimpleResponse setHomeMode(boolean mode) throws WebApiException {
        return apiHomeMode.setHomeMode(mode);
    }

    @Override
    public LiveUriResponse getLiveUriResponse(String cameraId) throws WebApiException {
        LiveUriResponse response = apiLiveUri.getLiveUriResponse(cameraId);
        return response;
    }

    @Override
    public boolean triggerEvent(int event) throws WebApiException {
        return apiExternalEvent.triggerEvent(event);
    }

    @Override
    public boolean isConnected() {
        return (this.sessionID != null);
    }

}
