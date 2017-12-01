/**
 * Copyright (c) 2010-2017 by the respective copyright holders.
 *
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 */
package org.openhab.binding.synologysurveillancestation.internal.webapi.request;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

import org.apache.commons.io.IOUtils;
import org.apache.http.HttpEntity;
import org.apache.http.NameValuePair;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.protocol.HttpClientContext;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.message.BasicNameValuePair;
import org.openhab.binding.synologysurveillancestation.internal.Config;
import org.openhab.binding.synologysurveillancestation.internal.webapi.WebApiException;
import org.openhab.binding.synologysurveillancestation.internal.webapi.response.CameraResponse;

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
 * @author Nils
 *
 */
public class SynoApiCamera extends SynoApiRequest<CameraResponse> {

    // API configuration
    private static final String API_NAME = "SYNO.SurveillanceStation.Camera";
    private static final SynoApiConfig apiConfig = new SynoApiConfig(API_NAME, API_VERSION_08, API_SCRIPT_ENTRY);

    /**
     * @param config
     */
    public SynoApiCamera(Config config, String sessionID) {
        super(apiConfig, config, sessionID);
    }

    /**
     * Calls the passed method for all cameras.
     *
     * @param method
     * @return
     * @throws WebApiException
     */
    private CameraResponse call(String method) throws WebApiException {

        return call(method, null);
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

        List<NameValuePair> params = new ArrayList<>();

        // API parameters
        params.add(new BasicNameValuePair("blFromCamList", API_TRUE));
        params.add(new BasicNameValuePair("privCamType", API_TRUE));
        params.add(new BasicNameValuePair("blIncludeDeletedCam", API_FALSE));
        params.add(new BasicNameValuePair("basic", API_TRUE));
        params.add(new BasicNameValuePair("streamInfo", API_TRUE));
        params.add(new BasicNameValuePair("blPrivilege", API_FALSE));
        params.add(new BasicNameValuePair("camStm", "1"));

        if (cameraId != null) {
            params.add(new BasicNameValuePair("cameraIds", cameraId));
        }

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
    public ByteArrayOutputStream getSnapshot(String cameraId) throws IOException, URISyntaxException, WebApiException {

        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        CloseableHttpResponse response = null;

        try {

            List<NameValuePair> params = new ArrayList<>();

            // API parameters
            params.add(new BasicNameValuePair("cameraId", cameraId));
            // 0 - High quality, 1 - Balanced, 2 - Low bandwidth
            params.add(new BasicNameValuePair("profileType", "1"));

            URL url = getWebApiUrl(METHOD_GETSNAPSHOT, params);
            URI uri = url.toURI();

            CloseableHttpClient httpclient = HttpClients.createDefault();
            HttpClientContext context = HttpClientContext.create();
            HttpGet httpget = new HttpGet(uri);

            response = httpclient.execute(httpget, context);

            HttpEntity entity = response.getEntity();
            IOUtils.copy(entity.getContent(), baos);
            entity.getContent();

            return baos;

        } finally {
            if (response != null) {

                response.close();
            }

        }

    }

    /**
     * Get the list of all cameras.
     *
     * @return
     * @throws WebApiException
     */
    public CameraResponse list() throws WebApiException {

        return call(METHOD_LIST);
    }

    /**
     * Get specific camera settings.
     *
     * @param cameraId
     * @return
     * @throws WebApiException
     */
    public CameraResponse getInfo(String cameraId) throws WebApiException {

        return call(METHOD_GETINFO, cameraId);
    }

    /**
     * Enable camera.
     *
     * @param cameraId
     * @return
     * @throws WebApiException
     */
    public CameraResponse enable(String cameraId) throws WebApiException {

        List<NameValuePair> params = new ArrayList<>();
        params.add(new BasicNameValuePair("cameraIds", cameraId));

        return callApi(METHOD_ENABLE, params);
    }

    /**
     * Disable camera.
     *
     * @param cameraId
     * @return
     * @throws WebApiException
     */
    public CameraResponse disable(String cameraId) throws WebApiException {

        List<NameValuePair> params = new ArrayList<>();
        params.add(new BasicNameValuePair("cameraIds", cameraId));

        return callApi(METHOD_DISABLE, params);
    }

}
