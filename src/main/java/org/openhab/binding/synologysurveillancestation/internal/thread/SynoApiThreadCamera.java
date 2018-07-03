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
public class SynoApiThreadCamera extends SynoApiThread<SynoCameraHandler> {
    // private final Logger logger = LoggerFactory.getLogger(SynoApiThreadCamera.class);

    public SynoApiThreadCamera(SynoCameraHandler handler, int refreshRate) {
        super(SynoApiThread.THREAD_CAMERA, handler, refreshRate);
    }

    @Override
    public boolean isNeeded() {
        boolean ret = getSynoHandler().isLinked(CHANNEL_ENABLE) || getSynoHandler().isLinked(CHANNEL_RECORD)
                || getSynoHandler().isLinked(CHANNEL_SNAPSHOT_URI_DYNAMIC);
        if (getSynoHandler().isPtz()) {
            ret = ret || getSynoHandler().isLinked(CHANNEL_MOVEPRESET) || getSynoHandler().isLinked(CHANNEL_RUNPATROL);
        }
        return ret;
    }

    @Override
    public boolean refresh() throws Exception {

        boolean ret = true;

        SynoCameraHandler cameraHandler = getSynoHandler();
        String cameraId = cameraHandler.getCameraId();

        if (cameraHandler.isLinked(CHANNEL_SNAPSHOT_URI_DYNAMIC)) {

            Channel channel = cameraHandler.getThing().getChannel(CHANNEL_SNAPSHOT_URI_DYNAMIC);
            Thing thing = cameraHandler.getThing();
            int streamId = Integer.parseInt(thing.getConfiguration().get(STREAM_ID).toString());

            String path = cameraHandler.getSynoWebApiHandler().getSnapshotUri(cameraId, streamId);
            path += "&timestamp=" + String.valueOf(System.currentTimeMillis());
            cameraHandler.updateState(channel.getUID(), new StringType(path));
        }

        if (cameraHandler.isPtz()) {
            if (cameraHandler.isLinked(CHANNEL_MOVEPRESET)) {
                cameraHandler.updatePresets();
            }
            if (cameraHandler.isLinked(CHANNEL_RUNPATROL)) {
                cameraHandler.updatePatrols();
            }
        }

        if (cameraHandler.isLinked(CHANNEL_ENABLE) || cameraHandler.isLinked(CHANNEL_RECORD)) {
            CameraResponse response = cameraHandler.getSynoWebApiHandler().getInfo(cameraId);
            if (response.isSuccess()) {
                if (cameraHandler.isLinked(CHANNEL_ENABLE)) {
                    Channel channel = cameraHandler.getThing().getChannel(CHANNEL_ENABLE);
                    cameraHandler.updateState(channel.getUID(),
                            response.isEnabled(cameraId) ? OnOffType.ON : OnOffType.OFF);
                }
                if (cameraHandler.isLinked(CHANNEL_RECORD)) {
                    Channel channel = cameraHandler.getThing().getChannel(CHANNEL_RECORD);
                    cameraHandler.updateState(channel.getUID(),
                            response.isRecording(cameraId) ? OnOffType.ON : OnOffType.OFF);
                }

                ret &= true;

            } else {

                ret &= false;
            }
        }

        return ret;

    }

}
