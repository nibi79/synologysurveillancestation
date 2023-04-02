/**
 * Copyright (c) 2010-2023 Contributors to the openHAB project
 *
 * See the NOTICE file(s) distributed with this work for additional
 * information.
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License 2.0 which is available at
 * http://www.eclipse.org/legal/epl-2.0
 *
 * SPDX-License-Identifier: EPL-2.0
 */
package org.openhab.binding.synologysurveillancestation.internal.webapi;

import java.util.HashMap;

import org.eclipse.jdt.annotation.NonNullByDefault;
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
@NonNullByDefault
public class SynoWebApiHandler implements SynoWebApi {

    private SynoConfig config;
    private String sessionID = "";

    private final HashMap<Class<?>, SynoApiRequest<?>> api = new HashMap<>();

    /**
     * @param config
     */
    public SynoWebApiHandler(SynoConfig config, HttpClient httpClient) {
        this.config = config;
        api.put(SynoApiAuth.class, new SynoApiAuth(config, httpClient));
        api.put(SynoApiInfo.class, new SynoApiInfo(config, httpClient));
        api.put(SynoApiCamera.class, new SynoApiCamera(config, httpClient));
        api.put(SynoApiEvent.class, new SynoApiEvent(config, httpClient));
        api.put(SynoApiHomeMode.class, new SynoApiHomeMode(config, httpClient));
        api.put(SynoApiExternalRecording.class, new SynoApiExternalRecording(config, httpClient));
        api.put(SynoApiPTZ.class, new SynoApiPTZ(config, httpClient));
        api.put(SynoApiLiveUri.class, new SynoApiLiveUri(config, httpClient));
        api.put(SynoApiExternalEvent.class, new SynoApiExternalEvent(config, httpClient));
        api.put(SynoApiCameraEvent.class, new SynoApiCameraEvent(config, httpClient));
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
        for (SynoApiRequest<?> r : api.values()) {
            r.setConfig(config);
        }
    }

    /**
     * @return
     */
    public void setSessionID(String sessionID) {
        this.sessionID = sessionID;
        for (SynoApiRequest<?> r : api.values()) {
            r.setSessionId(sessionID);
        }
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
        AuthResponse response = getApiAuth().login();
        if (response.isSuccess()) {
            String sid = response.getSid();
            setSessionID(sid);
            return true;
        } else {
            throw new WebApiException(WebApiAuthErrorCodes.getByCode(response.getErrorcode()));
        }
    }

    /*
     * (non-Javadoc)
     *
     * @see org.eclipse.smarthome.binding.synologysurveillancestation.internal.webapi.SynoWebApi#disconnect()
     */
    @Override
    public SimpleResponse disconnect() throws WebApiException {
        SimpleResponse response = getApiAuth().logout(sessionID);
        setSessionID("");

        if (response.isSuccess()) {
            return response;
        } else {
            throw new WebApiException(WebApiAuthErrorCodes.getByCode(response.getErrorcode()));
        }
    }

    @Override
    public boolean isConnected() {
        return (!this.sessionID.isBlank());
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
