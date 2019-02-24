/**
 * Copyright (c) 2010-2019 by the respective copyright holders.
 *
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 */
package org.openhab.binding.synologysurveillancestation.internal.webapi;

import java.util.HashMap;

import org.eclipse.jetty.client.HttpClient;
import org.openhab.binding.synologysurveillancestation.internal.SynoConfig;
import org.openhab.binding.synologysurveillancestation.internal.webapi.error.WebApiAuthErrorCodes;
import org.openhab.binding.synologysurveillancestation.internal.webapi.request.SynoApiAuth;
import org.openhab.binding.synologysurveillancestation.internal.webapi.request.SynoApiCamera;
import org.openhab.binding.synologysurveillancestation.internal.webapi.request.SynoApiCameraEvent;
import org.openhab.binding.synologysurveillancestation.internal.webapi.request.SynoApiEvent;
import org.openhab.binding.synologysurveillancestation.internal.webapi.request.SynoApiExternalEvent;
import org.openhab.binding.synologysurveillancestation.internal.webapi.request.SynoApiExternalRecording;
import org.openhab.binding.synologysurveillancestation.internal.webapi.request.SynoApiHomeMode;
import org.openhab.binding.synologysurveillancestation.internal.webapi.request.SynoApiInfo;
import org.openhab.binding.synologysurveillancestation.internal.webapi.request.SynoApiLiveUri;
import org.openhab.binding.synologysurveillancestation.internal.webapi.request.SynoApiPTZ;
import org.openhab.binding.synologysurveillancestation.internal.webapi.request.SynoApiRequest;
import org.openhab.binding.synologysurveillancestation.internal.webapi.response.AuthResponse;
import org.openhab.binding.synologysurveillancestation.internal.webapi.response.SimpleResponse;

/**
 * The {@link SynoWebApiHandler} is a facade for Synology Surveillance Station Web API.
 *
 * @author Nils - Initial contribution
 * @author Pavion - Contribution
 */
// @NonNullByDefault
public class SynoWebApiHandler implements SynoWebApi {

    private SynoConfig config;
    private String sessionID = "";
    private final HttpClient httpClient;

    private final HashMap<Class<?>, SynoApiRequest<?>> api = new HashMap<>();

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
    public void setConfig(SynoConfig config) {
        this.config = config;
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
    public boolean connect(boolean forceLogout) throws WebApiException {
        api.put(SynoApiAuth.class, new SynoApiAuth(config, httpClient));
        if (forceLogout && !sessionID.equals("")) {
            logout();
        }
        boolean connected = createSession();

        // initialize APIs

        api.put(SynoApiInfo.class, new SynoApiInfo(config, sessionID, httpClient));
        api.put(SynoApiCamera.class, new SynoApiCamera(config, sessionID, httpClient));
        api.put(SynoApiEvent.class, new SynoApiEvent(config, sessionID, httpClient));
        api.put(SynoApiHomeMode.class, new SynoApiHomeMode(config, sessionID, httpClient));
        api.put(SynoApiExternalRecording.class, new SynoApiExternalRecording(config, sessionID, httpClient));
        api.put(SynoApiPTZ.class, new SynoApiPTZ(config, sessionID, httpClient));
        api.put(SynoApiLiveUri.class, new SynoApiLiveUri(config, sessionID, httpClient));
        api.put(SynoApiExternalEvent.class, new SynoApiExternalEvent(config, sessionID, httpClient));
        api.put(SynoApiCameraEvent.class, new SynoApiCameraEvent(config, sessionID, httpClient));

        return connected;
    }

    /**
     * @return
     * @throws WebApiException
     */
    private boolean createSession() throws WebApiException {

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
     * @return
     * @throws WebApiException
     */
    private AuthResponse login() throws WebApiException {
        return getApiAuth().login();
    }

    /*
     * (non-Javadoc)
     *
     * @see org.eclipse.smarthome.binding.synologysurveillancestation.internal.webapi.SynoWebApi#logout()
     */
    @Override
    public SimpleResponse logout() throws WebApiException {
        SimpleResponse response = getApiAuth().logout(sessionID);
        this.sessionID = "";

        if (response.isSuccess()) {
            return response;
        } else {
            throw new WebApiException(WebApiAuthErrorCodes.getByCode(response.getErrorcode()));
        }
    }

    @Override
    public boolean isConnected() {
        return (!this.sessionID.equals(""));
    }

    /**
     * @return the apiCameraEvent
     */
    public SynoApiCameraEvent getApiCameraEvent() {
        return getApi(SynoApiCameraEvent.class);
    }

    /**
     * @return the apiPTZ
     */
    public SynoApiPTZ getApiPTZ() {
        return getApi(SynoApiPTZ.class);
    }

    /**
     * @return the apiCamera
     */
    public SynoApiCamera getApiCamera() {
        return getApi(SynoApiCamera.class);
    }

    /**
     * @return the apiExternalRecording
     */
    public SynoApiExternalRecording getApiExternalRecording() {
        return getApi(SynoApiExternalRecording.class);
    }

    /**
     * @return the apiInfo
     */
    public SynoApiInfo getApiInfo() {
        return getApi(SynoApiInfo.class);
    }

    /**
     * @return the apiEvent
     */
    public SynoApiEvent getApiEvent() {
        return getApi(SynoApiEvent.class);
    }

    /**
     * @return the apiHomeMode
     */
    public SynoApiHomeMode getApiHomeMode() {
        return getApi(SynoApiHomeMode.class);
    }

    /**
     * @return the apiLiveUri
     */
    public SynoApiLiveUri getApiLiveUri() {
        return getApi(SynoApiLiveUri.class);
    }

    /**
     * @return the apiExternalEvent
     */
    public SynoApiExternalEvent getApiExternalEvent() {
        return getApi(SynoApiExternalEvent.class);
    }

    /**
     * @return the apiLiveUri
     */
    public SynoApiAuth getApiAuth() {
        return getApi(SynoApiAuth.class);
    }

    /**
     * Generic getter
     *
     * @param cl
     * @return
     */
    @SuppressWarnings("unchecked")
    private <T extends SynoApiRequest<?>> T getApi(Class<T> cl) {
        return (T) api.get(cl);
    }
}
