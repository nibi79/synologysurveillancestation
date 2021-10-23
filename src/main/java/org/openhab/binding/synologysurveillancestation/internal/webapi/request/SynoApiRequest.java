/**
 * Copyright (c) 2010-2020 Contributors to the openHAB project
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
import java.io.UnsupportedEncodingException;
import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.ParameterizedType;
import java.net.MalformedURLException;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeoutException;

import org.eclipse.jdt.annotation.NonNullByDefault;
import org.eclipse.jetty.client.HttpClient;
import org.eclipse.jetty.client.api.ContentResponse;
import org.eclipse.jetty.client.api.Request;
import org.eclipse.jetty.util.URIUtil;
import org.openhab.binding.synologysurveillancestation.internal.SynoConfig;
import org.openhab.binding.synologysurveillancestation.internal.webapi.WebApiException;
import org.openhab.binding.synologysurveillancestation.internal.webapi.response.SynoApiResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * API request
 *
 * @param <T>
 *
 * @author Nils - Initial contribution
 * @author Pavion - Contribution
 */
@NonNullByDefault
public abstract class SynoApiRequest<T extends SynoApiResponse> implements SynoApi {
    private final Logger logger = LoggerFactory.getLogger(SynoApiRequest.class);

    protected static final String API_TRUE = Boolean.TRUE.toString();
    protected static final String API_FALSE = Boolean.FALSE.toString();

    private final SynoApiConfig apiConfig;
    private final HttpClient httpClient;
    private SynoConfig config;
    private String sessionId = "";

    final Class<T> typeParameterClass;

    /**
     * @param apiConfig
     * @param config
     * @param sessionId
     */
    @SuppressWarnings({ "unchecked", "rawtypes" })
    public SynoApiRequest(SynoApiConfig apiConfig, SynoConfig config, HttpClient httpClient) {
        super();

        this.typeParameterClass = ((Class) ((ParameterizedType) getClass().getGenericSuperclass())
                .getActualTypeArguments()[0]);

        this.httpClient = httpClient;
        this.apiConfig = apiConfig;
        this.config = config;
    }

    /*
     * (non-Javadoc)
     *
     * @see org.openhab.binding.synologysurveillancestation.internal.webapi.SynoApi#getApiConfig()
     */
    @Override
    public SynoApiConfig getApiConfig() {
        return apiConfig;
    }

    /**
     * @return
     */
    protected SynoConfig getConfig() {
        return config;
    }

    /**
     * @return
     */
    public void setConfig(SynoConfig config) {
        this.config = config;
    }

    /**
     *
     * @param sessionId
     */
    public void setSessionId(String sessionId) {
        this.sessionId = sessionId;
    }

    /**
     * @return
     */
    protected String getSessionId() {
        return sessionId;
    }

    /**
     * Creates an URIBuilder to build the url.
     *
     * @return
     */
    protected URI getWebApiUrlBuilder() throws URISyntaxException {
        StringBuilder sb = URIUtil.newURIBuilder(getConfig().getProtocol(), getConfig().getHost(),
                Integer.parseInt(getConfig().getPort()));
        URI uri = new URI(sb.toString());
        uri = URIUtil.addPath(uri, apiConfig.getScriptpath());
        return uri;
    }

    /**
     * Calls the method.
     *
     * @param method
     * @return
     * @throws WebApiException
     */
    protected T callApi(String method) throws WebApiException {
        return callApi(method, new HashMap<>());
    }

    /**
     * Calls the method with the passed parameters.
     *
     * @param method
     * @param params
     * @return
     * @throws WebApiException
     */
    protected T callApi(String method, Map<String, String> params) throws WebApiException {
        Request request = getWebApiUrl(method, params);
        return callWebApi(request);
    }

    /**
     * Builds the url for api.
     *
     * @param method
     * @param params
     * @return
     * @throws MalformedURLException
     * @throws URISyntaxException
     */
    protected Request getWebApiUrl(String method, Map<String, String> params) throws WebApiException {
        try {
            URI uri = getWebApiUrlBuilder();

            Request request = httpClient.newRequest(uri);

            // API data
            request.param("api", apiConfig.getName());
            request.param("version", apiConfig.getVersion());

            // API method
            request.param("method", method);

            // API session
            request.param("_sid", getSessionId());

            if (!params.isEmpty()) {
                for (String key : params.keySet()) {
                    request.param(key, params.get(key));
                }
            }

            return request;

        } catch (URISyntaxException | UnsupportedOperationException e) {
            throw new WebApiException(e);
        }
    }

    /**
     * E
     *
     * @param apiurl
     * @return
     * @throws WebApiException
     * @throws URISyntaxException
     * @throws UnsupportedOperationException
     * @throws IOException
     */
    protected synchronized T callWebApi(Request request) throws WebApiException {
        try {
            if (logger.isDebugEnabled()) {
                logger.debug("URI: {}", request.getURI().toString());
            }
            ContentResponse response = request.send();

            if (response.getStatus() == 200) {
                byte[] rawResponse = response.getContent();
                String encoding = response.getEncoding().replaceAll("\"", "").trim();
                String result = new String(rawResponse, encoding);

                if (result.length() > 0) {
                    if (result.contains("\"success\":true")) {
                        logger.debug("RESPONSE: {}", result);
                    } else {
                        logger.error("RESPONSE: {}", result);
                    }

                }

                Constructor<T> ctor = typeParameterClass.getConstructor(String.class);

                T vo = ctor.newInstance(new Object[] { result });

                return vo;

            } else {
                throw new WebApiException("Error calling Surveillance Station WebApi!");
            }

        } catch (InstantiationException | IllegalAccessException | IllegalArgumentException | InvocationTargetException
                | NoSuchMethodException | SecurityException | ExecutionException | TimeoutException
                | InterruptedException e) {
            throw new WebApiException(e);
        } catch (UnsupportedEncodingException ee) {
            throw new WebApiException(ee);
        }
    }
}
