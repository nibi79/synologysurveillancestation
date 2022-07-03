/**
 * Copyright (c) 2010-2022 Contributors to the openHAB project
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
import org.openhab.binding.synologysurveillancestation.internal.SynoCameraConfig;
import org.openhab.binding.synologysurveillancestation.internal.webapi.WebApiException;
import org.openhab.binding.synologysurveillancestation.internal.webapi.error.WebApiAuthErrorCodes;
import org.openhab.binding.synologysurveillancestation.internal.webapi.response.CameraResponse;
import org.openhab.core.library.types.OnOffType;
import org.openhab.core.library.types.StringType;
import org.openhab.core.thing.Channel;

/**
 * Thread for getting camera state (enabled, recording)
 *
 * @author Pavion - Initial contribution
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
            SynoCameraConfig config = cameraHandler.getThing().getConfiguration().as(SynoCameraConfig.class);
            String path = cameraHandler.getSynoWebApiHandler().getApiCamera().getSnapshotUri(cameraId,
                    config.getSnapshotStreamId());
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
            CameraResponse response = cameraHandler.getSynoWebApiHandler().getApiCamera().getInfo(cameraId);
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

            } else if (response.getErrorcode() == 105) {
                throw new WebApiException(WebApiAuthErrorCodes.INSUFFICIENT_USER_PRIVILEGE);
            } else {
                ret &= false;
            }
        }

        return ret;
    }
}
