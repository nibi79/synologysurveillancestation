/**
 * Copyright (c) 2010-2017 by the respective copyright holders.
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
import org.eclipse.smarthome.core.thing.Channel;
import org.openhab.binding.synologysurveillancestation.handler.SynoCameraHandler;
import org.openhab.binding.synologysurveillancestation.internal.webapi.WebApiException;
import org.openhab.binding.synologysurveillancestation.internal.webapi.response.CameraResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @author Pav
 *
 */
@NonNullByDefault
public class SynoApiThreadCamera extends SynoApiThread {
    private final Logger logger = LoggerFactory.getLogger(SynoApiThreadCamera.class);

    public SynoApiThreadCamera(SynoCameraHandler handler, int refreshRate) {
        super(SynoApiThread.THREAD_CAMERA, handler, refreshRate);
    }

    @Override
    public boolean isNeeded() {
        return (getHandler().isLinked(CHANNEL_ENABLE) || getHandler().isLinked(CHANNEL_RECORD));
    }

    @Override
    public boolean refresh() {
        String cameraId = getHandler().getCameraId();
        try {
            CameraResponse response = getApiHandler().getInfo(cameraId);
            if (response.isSuccess()) {
                if (getHandler().isLinked(CHANNEL_ENABLE)) {
                    Channel channel = getHandler().getThing().getChannel(CHANNEL_ENABLE);
                    getHandler().updateState(channel.getUID(),
                            response.isEnabled(cameraId) ? OnOffType.ON : OnOffType.OFF);
                }
                if (getHandler().isLinked(CHANNEL_RECORD)) {
                    Channel channel = getHandler().getThing().getChannel(CHANNEL_RECORD);
                    getHandler().updateState(channel.getUID(),
                            response.isRecording(cameraId) ? OnOffType.ON : OnOffType.OFF);
                }
                return true;
            } else {
                return false;
            }
        } catch (WebApiException | NullPointerException e) {
            logger.error("Could not get camera info: {}", e);
            return false;
        }
    }

}
