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
package org.openhab.binding.synologysurveillancestation.internal.thread;

import static org.openhab.binding.synologysurveillancestation.SynoBindingConstants.*;

import org.eclipse.jdt.annotation.NonNullByDefault;
import org.openhab.binding.synologysurveillancestation.handler.SynoCameraHandler;
import org.openhab.binding.synologysurveillancestation.internal.webapi.WebApiException;
import org.openhab.binding.synologysurveillancestation.internal.webapi.error.WebApiAuthErrorCodes;
import org.openhab.binding.synologysurveillancestation.internal.webapi.response.LiveUriResponse;
import org.openhab.core.library.types.StringType;
import org.openhab.core.thing.Channel;

/**
 * Thread for refreshing live URIs (RTSP or MJPEG over HTTP)
 *
 * @author Pavion - Initial contribution
 */
@NonNullByDefault
public class SynoApiThreadLiveUri extends SynoApiThread<SynoCameraHandler> {
    // private final Logger logger = LoggerFactory.getLogger(SynoApiThreadCamera.class);

    public SynoApiThreadLiveUri(SynoCameraHandler handler, int refreshRate) {
        super(SynoApiThread.THREAD_LIVEURI, handler, refreshRate);
    }

    @Override
    public boolean isNeeded() {
        return (getSynoHandler().isLinked(CHANNEL_LIVE_URI_RTSP)
                || getSynoHandler().isLinked(CHANNEL_LIVE_URI_MJPEG_HTTP));
    }

    @Override
    public boolean refresh() throws Exception {
        SynoCameraHandler cameraHandler = getSynoHandler();
        String cameraId = cameraHandler.getCameraId();

        LiveUriResponse response = cameraHandler.getSynoWebApiHandler().getApiLiveUri().getLiveUriResponse(cameraId);

        if (response.isSuccess()) {
            if (cameraHandler.isLinked(CHANNEL_LIVE_URI_RTSP)) {
                Channel channel = cameraHandler.getThing().getChannel(CHANNEL_LIVE_URI_RTSP);
                String uri = response.getRtsp();
                cameraHandler.updateState(channel.getUID(), new StringType(uri));
            }

            if (cameraHandler.isLinked(CHANNEL_LIVE_URI_MJPEG_HTTP)) {
                Channel channel = cameraHandler.getThing().getChannel(CHANNEL_LIVE_URI_MJPEG_HTTP);
                String uri = response.getMjpegHttp();
                cameraHandler.updateState(channel.getUID(), new StringType(uri));
            }

            return true;
        } else if (response.getErrorcode() == 105) {
            throw new WebApiException(WebApiAuthErrorCodes.INSUFFICIENT_USER_PRIVILEGE);
        }

        return false;
    }
}
