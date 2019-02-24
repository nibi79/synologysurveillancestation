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
import org.eclipse.smarthome.core.library.types.DecimalType;
import org.eclipse.smarthome.core.library.types.StringType;
import org.openhab.binding.synologysurveillancestation.handler.SynoCameraHandler;
import org.openhab.binding.synologysurveillancestation.internal.webapi.response.CameraEventResponse;

/**
 * Thread for getting camera state (enabled, recording)
 *
 * @author Pavion - Initial contribution
 */
@NonNullByDefault
public class SynoApiThreadCameraEvent extends SynoApiThread<SynoCameraHandler> {

    public SynoApiThreadCameraEvent(SynoCameraHandler handler, int refreshRate) {
        super(SynoApiThread.THREAD_CAMERAEVENT, handler, refreshRate);
    }

    @Override
    public boolean isNeeded() {
        for (String channel : CHANNEL_MDPARAM) {
            if (getSynoHandler().isLinked(channel)) {
                return true;
            }
        }
        return false;
    }

    @Override
    public boolean refresh() throws Exception {
        SynoCameraHandler cameraHandler = getSynoHandler();
        String cameraId = cameraHandler.getCameraId();

        CameraEventResponse response = cameraHandler.getSynoWebApiHandler().getApiCameraEvent().getMDParam(cameraId);
        if (!response.isSuccess()) {
            return false;
        }

        cameraHandler.updateState(CHANNEL_MDPARAM_SOURCE, new StringType(response.getSource()));
        cameraHandler.updateState(CHANNEL_MDPARAM_SENSITIVITY, new DecimalType(response.getSensitivity().getValue()));
        cameraHandler.updateState(CHANNEL_MDPARAM_THRESHOLD, new DecimalType(response.getThreshold().getValue()));
        cameraHandler.updateState(CHANNEL_MDPARAM_OBJECTSIZE, new DecimalType(response.getObjectSize().getValue()));
        cameraHandler.updateState(CHANNEL_MDPARAM_PERCENTAGE, new DecimalType(response.getPercentage().getValue()));
        cameraHandler.updateState(CHANNEL_MDPARAM_SHORTLIVE, new DecimalType(response.getShortLiveSecond().getValue()));

        return true;
    }
}
