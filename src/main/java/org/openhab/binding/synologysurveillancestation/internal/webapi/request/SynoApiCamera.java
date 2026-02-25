/*
 * Copyright (c) 2010-2026 Contributors to the openHAB project
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

import java.io.IOException;
import java.net.URISyntaxException;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

import org.eclipse.jdt.annotation.NonNullByDefault;
import org.eclipse.jetty.client.HttpClient;
import org.eclipse.jetty.client.api.ContentResponse;
import org.eclipse.jetty.client.api.Request;
import org.openhab.binding.synologysurveillancestation.internal.SynoConfig;
import org.openhab.binding.synologysurveillancestation.internal.webapi.WebApiException;
import org.openhab.binding.synologysurveillancestation.internal.webapi.error.WebApiAuthErrorCodes;
import org.openhab.binding.synologysurveillancestation.internal.webapi.response.CameraResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * SYNO.SurveillanceStation.Camera
 *
 * This API provides a set of methods to acquire camera-related information and to enable/disable cameras.
 *
 * Method:
 * - Save
 * - List
 * - GetInfo
 * - ListGroup
 * - GetSnapshot
 * - Enable
 * - Disable
 * - GetCapabilityByCamId
 * - MigrationEnum
 * - Migrate
 * - CountByCategory
 * - RecountEventSize
 * - SaveOptimizeParam
 * - GetOccupiedSize
 * - CheckCamValid
 * - MigrationCancel
 * - Delete
 * - GetLiveViewPath
 *
 * @author Nils - Initial contribution
 * @author Pavion - Contribution
 *
 */
@NonNullByDefault
public class SynoApiCamera extends SynoApiRequest<CameraResponse> {
    private final Logger logger = LoggerFactory.getLogger(SynoApiCamera.class);

    // API configuration
    private static final String API_NAME = "SYNO.SurveillanceStation.Camera";
    private static final SynoApiConfig API_CONFIG = new SynoApiConfig(API_NAME, API_VERSION_08, API_SCRIPT_ENTRY);

    /**
     * @param config
     */
    public SynoApiCamera(SynoConfig config, HttpClient httpClient) {
        super(API_CONFIG, config, httpClient);
    }

    /**
     * Calls the passed method for all cameras.
     *
     * @param method
     * @return
     * @throws WebApiException
     */
    private CameraResponse call(String method) throws WebApiException {
        return call(method, "");
    }

    /**
     * Calls the passed method.
     *
     * @param method
     * @param cameraId
     * @return
     * @throws WebApiException
     */
    private CameraResponse call(String method, String cameraId) throws WebApiException {
        Map<String, String> params = new HashMap<>();

        // API parameters
        params.put("blFromCamList", API_TRUE);
        params.put("privCamType", API_TRUE);
        params.put("blIncludeDeletedCam", API_FALSE);
        params.put("basic", API_TRUE);
        params.put("streamInfo", API_TRUE);
        params.put("blPrivilege", API_FALSE);
        params.put("cameraIds", cameraId);

        return callApi(method, params);
    }

    /**
     * Get the up-to-date snapshot of the selected camera in JPEG format.
     *
     * @throws WebApiException
     * @throws IOException
     * @throws UnsupportedOperationException
     * @throws URISyntaxException
     *
     */
    public byte[] getSnapshot(String cameraId, int timeout, int streamId)
            throws IOException, URISyntaxException, WebApiException {
        try {
            Map<String, String> params = new HashMap<>();

            // API parameters
            params.put("cameraId", cameraId);
            params.put("camStm", String.valueOf(streamId));

            Request request = getWebApiUrl(METHOD_GETSNAPSHOT, params);

            long responseTime = System.currentTimeMillis();

            ContentResponse response = request.timeout(timeout, TimeUnit.SECONDS).send();

            responseTime = System.currentTimeMillis() - responseTime;
            byte[] ret = new byte[0];
            if (response.getStatus() == 200) {
                ret = response.getContent();
                if (ret.length < 200) {
                    String error = new String(ret);
                    if (error.contains("\"success\":false")) {
                        if (error.contains("{\"code\":400}")) {
                            logger.debug("Device: {}, API response time: {} ms, execution failed", cameraId,
                                    responseTime);
                            return new byte[0];
                        } else if (error.contains("{\"code\":401}")) {
                            logger.debug("Device: {}, API response time: {} ms, parameter invalid", cameraId,
                                    responseTime);
                            return new byte[0];
                        } else if (error.contains("{\"code\":402}")) {
                            logger.trace("Device: {}, API response time: {} ms, camera disabled", cameraId,
                                    responseTime);
                            return new byte[2];
                        } else if (error.contains("{\"code\":407}")) {
                            logger.debug("Device: {}, API response time: {} ms, CMS closed", cameraId, responseTime);
                            return new byte[0];
                        } else {
                            logger.trace("Device: {}, API response time: {} ms, unexpected response: {}", cameraId,
                                    responseTime, error);
                            throw new WebApiException(WebApiAuthErrorCodes.INSUFFICIENT_USER_PRIVILEGE);
                        }
                    }
                }
            }
            logger.trace("Device: {}, API response time: {} ms, stream id: {}", cameraId, responseTime, streamId);
            return ret;
        } catch (IllegalArgumentException | SecurityException | ExecutionException | TimeoutException
                | InterruptedException e) {
            throw new WebApiException(e);
        }
    }

    /**
     * Get snapshot URI of the selected camera
     *
     * @throws WebApiException
     * @throws IOException
     * @throws UnsupportedOperationException
     * @throws URISyntaxException
     *
     */
    public String getSnapshotUri(String cameraId, int streamId) throws WebApiException {
        try {
            Map<String, String> params = new HashMap<>();

            // API parameters
            params.put("cameraId", cameraId);
            params.put("camStm", String.valueOf(streamId));

            Request request = getWebApiUrl(METHOD_GETSNAPSHOT, params);
            return request.getURI().toString();
        } catch (Exception e) {
            throw new WebApiException(e);
        }
    }

    /**
     * Get the list of all cameras.
     *
     * @return
     * @throws WebApiException
     */
    public CameraResponse listCameras() throws WebApiException {
        CameraResponse response = call(METHOD_LIST);

        if (!response.isSuccess()) {
            throw new WebApiException(WebApiAuthErrorCodes.getByCode(response.getErrorcode()));
        }

        return response;
    }

    /**
     * Get specific camera settings.
     *
     * @return
     * @throws WebApiException
     */
    public CameraResponse getInfo(String cameraId) throws WebApiException {
        CameraResponse response = call(METHOD_GETINFO, cameraId);

        if (!response.isSuccess()) {
            throw new WebApiException(WebApiAuthErrorCodes.getByCode(response.getErrorcode()));
        }

        return response;
    }

    /**
     * Toggle camera.
     *
     * @param cameraId
     * @return
     * @throws WebApiException
     */
    public CameraResponse toggleCamera(String cameraId, boolean on) throws WebApiException {
        Map<String, String> params = new HashMap<>();
        params.put("cameraIds", cameraId);

        return callApi(on ? METHOD_ENABLE : METHOD_DISABLE, params);
    }
}
