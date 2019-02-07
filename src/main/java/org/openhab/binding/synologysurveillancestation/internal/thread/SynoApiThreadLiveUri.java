/**
 * Copyright (c) 2010-2019 by the respective copyright holders.
 *
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 */
package org.openhab.binding.synologysurveillancestation.internal.thread;

import static org.openhab.binding.synologysurveillancestation.SynoBindingConstants.*;

import org.eclipse.jdt.annotation.NonNullByDefault;
import org.eclipse.smarthome.core.library.types.StringType;
import org.eclipse.smarthome.core.thing.Channel;
import org.openhab.binding.synologysurveillancestation.handler.SynoCameraHandler;
import org.openhab.binding.synologysurveillancestation.internal.webapi.WebApiException;
import org.openhab.binding.synologysurveillancestation.internal.webapi.response.LiveUriResponse;

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

        LiveUriResponse response = cameraHandler.getSynoWebApiHandler().getLiveUriResponse(cameraId);

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
            throw new WebApiException(105, "Wrong/expired credentials");
        }

        return false;

    }

}
