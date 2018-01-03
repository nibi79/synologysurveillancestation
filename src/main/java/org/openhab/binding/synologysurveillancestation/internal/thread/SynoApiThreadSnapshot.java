/**
 * Copyright (c) 2010-2017 by the respective copyright holders.
 *
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 */
package org.openhab.binding.synologysurveillancestation.internal.thread;

import static org.openhab.binding.synologysurveillancestation.SynoBindingConstants.CHANNEL_SNAPSHOT;

import java.io.IOException;
import java.net.URISyntaxException;

import org.eclipse.jdt.annotation.NonNullByDefault;
import org.eclipse.smarthome.core.library.types.RawType;
import org.eclipse.smarthome.core.thing.Channel;
import org.eclipse.smarthome.core.thing.Thing;
import org.eclipse.smarthome.core.types.UnDefType;
import org.openhab.binding.synologysurveillancestation.handler.SynoCameraHandler;
import org.openhab.binding.synologysurveillancestation.internal.webapi.WebApiException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @author Pav
 *
 */
@NonNullByDefault
public class SynoApiThreadSnapshot extends SynoApiThread {
    private final Logger logger = LoggerFactory.getLogger(SynoApiThreadSnapshot.class);

    public SynoApiThreadSnapshot(SynoCameraHandler handler, int refreshRate) {
        super(SynoApiThread.THREAD_SNAPSHOT, handler, refreshRate);
    }

    @Override
    public boolean isNeeded() {
        return (getHandler().isLinked(CHANNEL_SNAPSHOT));
    }

    @Override
    public boolean refresh() {
        Channel channel = getHandler().getThing().getChannel(CHANNEL_SNAPSHOT);
        Thing thing = getHandler().getThing();

        logger.trace("Will update: {}::{}::{}", thing.getUID().getId(), channel.getChannelTypeUID().getId(),
                thing.getLabel());

        try {
            byte[] snapshot = getApiHandler().getSnapshot(getHandler().getCameraId());
            if (snapshot.length < 1000) {
                getHandler().updateState(channel.getUID(), UnDefType.UNDEF);
            } else {
                getHandler().updateState(channel.getUID(), new RawType(snapshot, "image/jpeg"));
            }
            return true;
        } catch (WebApiException e) {
            logger.error("Snapshot: Handler gone offline");
            return false;
        } catch (URISyntaxException | IOException | NullPointerException e) {
            logger.error("could not get snapshot: {}", thing, e);
            return false;
        }
    }

}
