/**
 * Copyright (c) 2010-2018 by the respective copyright holders.
 *
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 */
package org.openhab.binding.synologysurveillancestation.internal.webapi.request;

import java.io.IOException;
import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.ParameterizedType;
import java.net.MalformedURLException;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.Map;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

import org.eclipse.jetty.client.HttpClient;
import org.eclipse.jetty.client.api.ContentResponse;
import org.eclipse.jetty.client.api.Request;
import org.eclipse.jetty.util.URIUtil;
import org.eclipse.jetty.util.ssl.SslContextFactory;
import org.openhab.binding.synologysurveillancestation.internal.SynoConfig;
import org.openhab.binding.synologysurveillancestation.internal.webapi.WebApiException;
import org.openhab.binding.synologysurveillancestation.internal.webapi.response.SynoApiResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * API request
 *
 * @author Nils
 *
 * @param <T>
 */
public abstract class SynoApiRequest<T extends SynoApiResponse> implements SynoApi {
    private final Logger logger = LoggerFactory.getLogger(SynoApiRequest.class);

    protected static final String API_TRUE = Boolean.TRUE.toString();
    protected static final String API_FALSE = Boolean.FALSE.toString();
    private SynoApiConfig apiConfig = null;
    private HttpClient httpClient;

    final Class<T> typeParameterClass;

    private SynoConfig config = null;
    private String sessionId = null;

    /**
     * @param apiConfig
     * @param config
     * @param sessionId
     */
    @SuppressWarnings({ "unchecked", "rawtypes" })
    public SynoApiRequest(SynoApiConfig apiConfig, SynoConfig config, String sessionId) {

        super();

        this.typeParameterClass = ((Class) ((ParameterizedType) getClass().getGenericSuperclass())
                .getActualTypeArguments()[0]);

        this.apiConfig = apiConfig;
        this.config = config;
        this.sessionId = sessionId;

        SslContextFactory sslContextFactory = new SslContextFactory();
        httpClient = new HttpClient(sslContextFactory);
        httpClient.setConnectTimeout(3000);
        try {
            httpClient.start();
        } catch (Exception e) {
            logger.debug("Error starting HTTP client");
        }

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
        return callApi(method, null);
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

            Request request = httpClient.newRequest(uri).timeout(3000, TimeUnit.MILLISECONDS);

            // API data
            request.param("api", apiConfig.getName());
            request.param("version", apiConfig.getVersion());

            // API method
            request.param("method", method);

            // API session
            request.param("_sid", getSessionId());

            if (params != null) {
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

            // System.err.println(request.getURI());
            ContentResponse response = request.send();

            if (response.getStatus() == 200) {

                String result = response.getContentAsString();
                if (result.length() > 0) {
                    logger.debug("RESPONSE: {}", result);
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
        }

    }

}
