/**
 * Copyright (c) 2010-2017 by the respective copyright holders.
 *
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 */
package org.openhab.binding.synologysurveillancestation.handler;

import static org.openhab.binding.synologysurveillancestation.SynologySurveillanceStationBindingConstants.CHANNEL_IMAGE;

import java.io.IOException;
import java.lang.ref.WeakReference;
import java.net.URISyntaxException;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.atomic.AtomicBoolean;

import org.eclipse.smarthome.core.library.types.RawType;
import org.eclipse.smarthome.core.thing.Channel;
import org.eclipse.smarthome.core.thing.ChannelUID;
import org.eclipse.smarthome.core.thing.Thing;
import org.eclipse.smarthome.core.thing.ThingStatus;
import org.eclipse.smarthome.core.thing.ThingStatusDetail;
import org.eclipse.smarthome.core.thing.binding.BaseThingHandler;
import org.eclipse.smarthome.core.types.Command;
import org.openhab.binding.synologysurveillancestation.internal.webapi.SynoWebApiHandler;
import org.openhab.binding.synologysurveillancestation.internal.webapi.WebApiException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * The {@link SynologySurveillanceStationHandler} is responsible for handling commands, which are
 * sent to one of the channels.
 *
 * @author Nils - Initial contribution
 */
// @NonNullByDefault
public class SynologySurveillanceStationHandler extends BaseThingHandler {

    private final Logger logger = LoggerFactory.getLogger(SynologySurveillanceStationHandler.class);

    private final AtomicBoolean refreshInProgress = new AtomicBoolean(false);
    private final AtomicBoolean initialized = new AtomicBoolean(false);
    private final ExecutorService serviceCached = Executors.newCachedThreadPool();

    public SynologySurveillanceStationHandler(Thing thing) {
        super(thing);
    }

    @Override
    public void handleCommand(ChannelUID channelUID, Command command) {

        try {

            String cameraId = getThing().getUID().getId();

            SynologySurveillanceStationBridgeHandler bridge = ((SynologySurveillanceStationBridgeHandler) getBridge()
                    .getHandler());

            SynoWebApiHandler apiHandler = bridge.getSynoWebApiHandler();

            switch (channelUID.getId()) {
                case CHANNEL_IMAGE:

                    if (command.toString().equals("REFRESH")) {
                        refreshData();
                    }

                default:
                    apiHandler.execute(cameraId, channelUID.getId(), command.toString());
            }
        } catch (WebApiException e) {
            logger.error("handle command: {}::{}::{}", getThing().getLabel(), getThing().getUID());
        }

    }

    @Override
    public void initialize() {

        updateStatus(ThingStatus.UNKNOWN);

        if (getBridge() != null) {

            String cameraId = getThing().getUID().getId();

            logger.debug("Initializing SynologySurveillanceStationHandler for Camerid '{}'", cameraId);

            if (getBridge().getStatus() == ThingStatus.ONLINE) {
                updateStatus(ThingStatus.ONLINE);

                if (initialized.compareAndSet(false, true)) {
                    WeakReference<SynologySurveillanceStationHandler> weakReference = new WeakReference<>(this);
                    serviceCached.submit(new Callable<Void>() {
                        @Override
                        public Void call() throws Exception {
                            while (weakReference.get() != null) {
                                try {
                                    refreshData();
                                } catch (Exception e) {
                                    logger.error("error in refresh", e);
                                }
                                // TODO POLL time
                                Thread.sleep(Math.max(10, 5000));
                            }
                            return null;
                        }
                    });
                }

            } else {
                updateStatus(ThingStatus.OFFLINE, ThingStatusDetail.OFFLINE.BRIDGE_OFFLINE);
            }
        } else {
            updateStatus(ThingStatus.OFFLINE);
        }

        if (logger.isDebugEnabled()) {
            logger.debug("Initialize thing: {}::{}", getThing().getLabel(), getThing().getUID());
        }

    }

    private void refreshData() {
        if (refreshInProgress.compareAndSet(false, true)) {
            try {
                for (Channel cx : getThing().getChannels()) {
                    if (cx.getAcceptedItemType().equals("Image")) {
                        if (logger.isTraceEnabled()) {
                            logger.trace("Will update: {}::{}::{}", getThing().getUID().getId(),
                                    cx.getChannelTypeUID().getId(), getThing().getLabel());
                        }

                        try {

                            String cameraId = getThing().getUID().getId();

                            SynologySurveillanceStationBridgeHandler bridge = ((SynologySurveillanceStationBridgeHandler) getBridge()
                                    .getHandler());
                            SynoWebApiHandler apiHandler = bridge.getSynoWebApiHandler();

                            byte[] snapshot = apiHandler.getSnapshot(cameraId).toByteArray();

                            updateState(cx.getUID(), new RawType(snapshot, "image/jpeg"));

                            updateStatus(ThingStatus.ONLINE);

                        } catch (URISyntaxException | IOException | WebApiException e) {
                            logger.error("could not get snapshot: {}", getThing(), e);
                            updateStatus(ThingStatus.OFFLINE, ThingStatusDetail.COMMUNICATION_ERROR,
                                    "communication error: " + e.toString());
                        }
                    }
                }
            } finally {
                refreshInProgress.set(false);
            }
        }
    }
}
