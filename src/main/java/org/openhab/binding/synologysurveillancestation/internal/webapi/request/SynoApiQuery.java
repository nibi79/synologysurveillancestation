/**
 * Copyright (c) 2010-2017 by the respective copyright holders.
 *
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 */
package org.openhab.binding.synologysurveillancestation.internal.webapi.request;

import java.util.ArrayList;
import java.util.List;

import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;
import org.openhab.binding.synologysurveillancestation.internal.Config;
import org.openhab.binding.synologysurveillancestation.internal.webapi.WebApiException;
import org.openhab.binding.synologysurveillancestation.internal.webapi.response.InfoResponse;
import org.openhab.binding.synologysurveillancestation.internal.webapi.response.SimpleResponse;

/**
 * SYNO.SurveillanceStation.SynoApiQuery
 *
 * This API provides a method to query for the existence of Surveillance Station
 *
 * @author Pavion
 *
 */
public class SynoApiQuery extends SynoApiRequest<InfoResponse> {

    // API Configuration
    private static final String API_NAME = "SYNO.API.Info";
    private static final SynoApiConfig apiConfig = new SynoApiConfig(API_NAME, API_VERSION_01, API_SCRIPT_QUERY);

    /**
     * @param config
     */
    public SynoApiQuery(Config config) {
        super(apiConfig, config, null);
    }

    /**
     * Get API information
     *
     * @return
     * @throws WebApiException
     */
    public SimpleResponse query() {
        List<NameValuePair> params = new ArrayList<>();
        params.add(new BasicNameValuePair("query", "SYNO.SurveillanceStation"));
        try {
            return callApi(METHOD_QUERY, params);
        } catch (WebApiException e) {
            return new SimpleResponse("{\"data\":{},\"success\":false}");
        }
    }
}
