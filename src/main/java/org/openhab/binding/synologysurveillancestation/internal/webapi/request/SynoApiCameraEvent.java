/**
 * Copyright (c) 2010-2019 Contributors to the openHAB project
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
package org.openhab.binding.synologysurveillancestation.internal.webapi.request;

import java.util.HashMap;
import java.util.Map;

import org.eclipse.jdt.annotation.NonNullByDefault;
import org.eclipse.jetty.client.HttpClient;
import org.openhab.binding.synologysurveillancestation.internal.SynoConfig;
import org.openhab.binding.synologysurveillancestation.internal.webapi.WebApiException;
import org.openhab.binding.synologysurveillancestation.internal.webapi.response.CameraEventResponse;
import org.openhab.binding.synologysurveillancestation.internal.webapi.response.SimpleResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * SYNO.SurveillanceStation.Camera.Event
 *
 * Event Detection related WebAPI. e.g. Enumerate detection parameters or long polling for alarm status or save
 * detection parameters.
 *
 * @author Pavion - Contribution
 *
 */
@NonNullByDefault
public class SynoApiCameraEvent extends SynoApiRequest<CameraEventResponse> {
    private final Logger logger = LoggerFactory.getLogger(SynoApiCameraEvent.class);

    // API configuration
    private static final String API_NAME = "SYNO.SurveillanceStation.Camera.Event";
    private static final SynoApiConfig API_CONFIG = new SynoApiConfig(API_NAME, API_VERSION_01, API_SCRIPT_ENTRY);

    /**
     * @param config
     */
    public SynoApiCameraEvent(SynoConfig config, HttpClient httpClient) {
        super(API_CONFIG, config, httpClient);
    }

    /**
     * Get motion detection parameter
     *
     * @param cameraId
     * @return
     * @throws WebApiException
     */
    public CameraEventResponse getMDParam(String cameraId) throws WebApiException {
        Map<String, String> params = new HashMap<>();
        params.put("camId", cameraId);
        return callApi(METHOD_MOTIONENUM, params);
    }

    public SimpleResponse setSource(String cameraId, String source) throws WebApiException {
        if (!(source.equals("-1") || source.equals("0") || source.equals("1"))) {
            return new SimpleResponse("{\"data\":{},\"success\":false}");
        }
        Map<String, String> params = new HashMap<>();
        params.put("camId", cameraId);
        params.put("keep", "true");
        params.put("source", source);
        return callApi(METHOD_MDPARAMSAVE, params);
    }

    // Warning, absolute values are used
    public SimpleResponse setSensitivity(String cameraId, int val) throws WebApiException {
        if (val < 1 || val > 99) {
            return new SimpleResponse("{\"data\":{},\"success\":false}");
        }
        Map<String, String> params = new HashMap<>();
        params.put("camId", cameraId);
        params.put("keep", "true");
        params.put("sensitivity", String.valueOf(val));
        return callApi(METHOD_MDPARAMSAVE, params);
    }

    public SimpleResponse setThreshold(String cameraId, int val) throws WebApiException {
        if (val < 1 || val > 99) {
            return new SimpleResponse("{\"data\":{},\"success\":false}");
        }
        Map<String, String> params = new HashMap<>();
        params.put("camId", cameraId);
        params.put("keep", "true");
        params.put("threshold", String.valueOf(val));
        return callApi(METHOD_MDPARAMSAVE, params);
    }

    public SimpleResponse setObjectSize(String cameraId, int val) throws WebApiException {
        if (val < 1 || val > 99) {
            return new SimpleResponse("{\"data\":{},\"success\":false}");
        }
        Map<String, String> params = new HashMap<>();
        params.put("camId", cameraId);
        params.put("keep", "true");
        params.put("objectSize", String.valueOf(val));
        return callApi(METHOD_MDPARAMSAVE, params);
    }

    public SimpleResponse setPercentage(String cameraId, int val) throws WebApiException {
        if (val < 1 || val > 99) {
            return new SimpleResponse("{\"data\":{},\"success\":false}");
        }
        Map<String, String> params = new HashMap<>();
        params.put("camId", cameraId);
        params.put("keep", "true");
        params.put("percentage", String.valueOf(val));
        return callApi(METHOD_MDPARAMSAVE, params);
    }

    public SimpleResponse setShortLiveSecond(String cameraId, int val) throws WebApiException {
        if (val < 0 || val > 10) {
            return new SimpleResponse("{\"data\":{},\"success\":false}");
        }
        Map<String, String> params = new HashMap<>();
        params.put("camId", cameraId);
        params.put("keep", "true");
        params.put("shortLiveSecond", String.valueOf(val));
        return callApi(METHOD_MDPARAMSAVE, params);
    }
}
