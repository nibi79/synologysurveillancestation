/**
 * Copyright (c) 2010-2018 by the respective copyright holders.
 *
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 */
package org.openhab.binding.synologysurveillancestation.internal.thread;

import static org.openhab.binding.synologysurveillancestation.SynoBindingConstants.*;

import org.eclipse.jdt.annotation.NonNullByDefault;
import org.eclipse.smarthome.core.library.types.OnOffType;
import org.eclipse.smarthome.core.library.types.StringType;
import org.eclipse.smarthome.core.thing.Channel;
import org.eclipse.smarthome.core.thing.Thing;
import org.openhab.binding.synologysurveillancestation.handler.SynoCameraHandler;
import org.openhab.binding.synologysurveillancestation.internal.webapi.response.CameraResponse;

/**
 * Thread for getting camera state (enabled, recording)
 *
 * @author Pavion
 */
@NonNullByDefault
public class SynoApiThreadCamera extends SynoApiThread {
    // private final Logger logger = LoggerFactory.getLogger(SynoApiThreadCamera.class);

    public SynoApiThreadCamera(SynoCameraHandler handler, int refreshRate) {
        super(SynoApiThread.THREAD_CAMERA, handler, refreshRate);
    }

    @Override
    public boolean isNeeded() {
        return (getAsCameraHandler().isLinked(CHANNEL_ENABLE) || getAsCameraHandler().isLinked(CHANNEL_RECORD)
                || getAsCameraHandler().isLinked(CHANNEL_SNAPSHOT_URI_DYNAMIC));
    }

    @Override
    public boolean refresh() throws Exception {
        String cameraId = getAsCameraHandler().getCameraId();

        if (getAsCameraHandler().isLinked(CHANNEL_SNAPSHOT_URI_DYNAMIC)) {

            Channel channel = getAsCameraHandler().getThing().getChannel(CHANNEL_SNAPSHOT_URI_DYNAMIC);
            Thing thing = getAsCameraHandler().getThing();
            int streamId = Integer.parseInt(thing.getConfiguration().get(STREAM_ID).toString());

            String path = getApiHandler().getSnapshotUri(cameraId, streamId);
            path += "&timestamp=" + String.valueOf(System.currentTimeMillis());
            getAsCameraHandler().updateState(channel.getUID(), new StringType(path));
        }
        if (getAsCameraHandler().isLinked(CHANNEL_ENABLE) || getAsCameraHandler().isLinked(CHANNEL_RECORD)) {
            CameraResponse response = getApiHandler().getInfo(cameraId);
            if (response.isSuccess()) {
                if (getAsCameraHandler().isLinked(CHANNEL_ENABLE)) {
                    Channel channel = getAsCameraHandler().getThing().getChannel(CHANNEL_ENABLE);
                    getAsCameraHandler().updateState(channel.getUID(),
                            response.isEnabled(cameraId) ? OnOffType.ON : OnOffType.OFF);
                }
                if (getAsCameraHandler().isLinked(CHANNEL_RECORD)) {
                    Channel channel = getAsCameraHandler().getThing().getChannel(CHANNEL_RECORD);
                    getAsCameraHandler().updateState(channel.getUID(),
                            response.isRecording(cameraId) ? OnOffType.ON : OnOffType.OFF);
                }
                return true;
            } else {
                return false;
            }
        }
        return true;

    }

}
