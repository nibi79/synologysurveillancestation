/**
 * Copyright (c) 2010-2019 Contributors to the openHAB project
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
import org.eclipse.smarthome.core.library.types.RawType;
import org.eclipse.smarthome.core.thing.Channel;
import org.eclipse.smarthome.core.thing.Thing;
import org.eclipse.smarthome.core.types.UnDefType;
import org.openhab.binding.synologysurveillancestation.handler.SynoCameraHandler;

/**
 * Thread for getting snapshots
 *
 * @author Pavion - Initial contribution
 */
@NonNullByDefault
public class SynoApiThreadSnapshot extends SynoApiThread<SynoCameraHandler> {
    // private final Logger logger = LoggerFactory.getLogger(SynoApiThreadSnapshot.class);

    public SynoApiThreadSnapshot(SynoCameraHandler handler, int refreshRate) {
        super(SynoApiThread.THREAD_SNAPSHOT, handler, refreshRate);
    }

    @Override
    public boolean isNeeded() {
        return (getSynoHandler().isLinked(CHANNEL_SNAPSHOT));
    }

    @Override
    public boolean refresh() throws Exception {
        SynoCameraHandler cameraHandler = getSynoHandler();

        Channel channel = cameraHandler.getThing().getChannel(CHANNEL_SNAPSHOT);
        Thing thing = cameraHandler.getThing();

        int streamId = Integer.parseInt(thing.getConfiguration().get(STREAM_ID).toString());
        byte[] snapshot = cameraHandler.getSynoWebApiHandler().getApiCamera()
                .getSnapshot(getSynoHandler().getCameraId(), getRefreshRate(), streamId);
        if (snapshot.length < 1000) {
            getSynoHandler().updateState(channel.getUID(), UnDefType.UNDEF);
            return false;
        } else {
            getSynoHandler().updateState(channel.getUID(), new RawType(snapshot, "image/jpeg"));
            return true;
        }
    }

}
