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

import static org.openhab.binding.synologysurveillancestation.SynoBindingConstants.CHANNEL_SNAPSHOT;

import org.eclipse.jdt.annotation.NonNullByDefault;
import org.openhab.binding.synologysurveillancestation.handler.SynoCameraHandler;
import org.openhab.binding.synologysurveillancestation.internal.SynoCameraConfig;
import org.openhab.core.library.types.RawType;
import org.openhab.core.thing.Channel;
import org.openhab.core.thing.Thing;
import org.openhab.core.types.UnDefType;

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
        SynoCameraConfig config = thing.getConfiguration().as(SynoCameraConfig.class);
        byte[] snapshot = cameraHandler.getSynoWebApiHandler().getApiCamera()
                .getSnapshot(getSynoHandler().getCameraId(), getRefreshRate(), config.getSnapshotStreamId());
        if (snapshot.length < 1000) {
            getSynoHandler().updateState(channel.getUID(), UnDefType.UNDEF);
            return (snapshot.length == 2);
        } else {
            getSynoHandler().updateState(channel.getUID(), new RawType(snapshot, "image/jpeg"));
            return true;
        }
    }
}
