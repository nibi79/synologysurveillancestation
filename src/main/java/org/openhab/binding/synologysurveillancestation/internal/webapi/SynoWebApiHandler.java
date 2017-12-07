/**
 * Copyright (c) 2010-2017 by the respective copyright holders.
 *
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 */
package org.openhab.binding.synologysurveillancestation.internal.webapi;

import static org.openhab.binding.synologysurveillancestation.SynologySurveillanceStationBindingConstants.*;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.net.URISyntaxException;

import org.openhab.binding.synologysurveillancestation.internal.Config;
import org.openhab.binding.synologysurveillancestation.internal.webapi.error.WebApiAuthErrorCodes;
import org.openhab.binding.synologysurveillancestation.internal.webapi.request.SynoApiAuth;
import org.openhab.binding.synologysurveillancestation.internal.webapi.request.SynoApiCamera;
import org.openhab.binding.synologysurveillancestation.internal.webapi.request.SynoApiExternalRecording;
import org.openhab.binding.synologysurveillancestation.internal.webapi.request.SynoApiInfo;
import org.openhab.binding.synologysurveillancestation.internal.webapi.request.SynoApiPTZ;
import org.openhab.binding.synologysurveillancestation.internal.webapi.response.AuthResponse;
import org.openhab.binding.synologysurveillancestation.internal.webapi.response.CameraResponse;
import org.openhab.binding.synologysurveillancestation.internal.webapi.response.InfoResponse;
import org.openhab.binding.synologysurveillancestation.internal.webapi.response.SimpleResponse;

/**
 * The {@link SynoWebApiHandler} is a facade for Synology Surveillance Station Web API.
 *
 * @author Nils
 */
public class SynoWebApiHandler implements SynoWebApi {

    private Config config = null;
    private String sessionID = null;

    // APIs
    private SynoApiAuth apiAuth = null;
    private SynoApiInfo apiInfo = null;
    // private SynoApiCameraGetSnapshot apiCameraGetSnapshot = null;
    private SynoApiCamera apiCamera = null;
    private SynoApiExternalRecording apiExternalRecording = null;
    private SynoApiPTZ apiPTZ = null;

    /**
     * @param config
     */
    public SynoWebApiHandler(Config config) {
        this.config = config;
    }

    /**
     * @return
     */
    public Config getConfig() {
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
    public boolean connect() throws WebApiException {

        apiAuth = new SynoApiAuth(config);

        boolean connected = createSession();

        // initialize APIs
        apiInfo = new SynoApiInfo(config, sessionID);
        apiCamera = new SynoApiCamera(config, sessionID);
        apiExternalRecording = new SynoApiExternalRecording(config, sessionID);
        apiPTZ = new SynoApiPTZ(config, sessionID);

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

        AuthResponse response = login();

        if (response.isSuccess()) {

            sessionID = response.getSid();

            return true;

        } else {

            throw new WebApiException(WebApiAuthErrorCodes.getByCode(response.getErrorcode()));
        }

    }

    /**
     * Check if request was succesull. If not WebApiException with API errocode is thrown.
     *
     * @param response
     * @return
     * @throws WebApiException
     */
    private SimpleResponse handleSimpleResponse(SimpleResponse response) throws WebApiException {

        if (response.isSuccess()) {

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
    public ByteArrayOutputStream getSnapshot(String cameraId) throws IOException, URISyntaxException, WebApiException {

        return apiCamera.getSnapshot(cameraId);
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

}
