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
        byte[] snapshot = cameraHandler.getSynoWebApiHandler().getSnapshot(getSynoHandler().getCameraId(),
                getRefreshRate(), streamId);
        if (snapshot.length < 1000) {
            getSynoHandler().updateState(channel.getUID(), UnDefType.UNDEF);
            return false;
        } else {
            getSynoHandler().updateState(channel.getUID(), new RawType(snapshot, "image/jpeg"));
            return true;
        }
    }

}
