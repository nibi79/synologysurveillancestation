/**
 * Copyright (c) 2010-2018 by the respective copyright holders.
 *
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 */
package org.openhab.binding.synologysurveillancestation.handler;

import static org.openhab.binding.synologysurveillancestation.SynoBindingConstants.*;

import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;
import java.util.concurrent.ScheduledExecutorService;

import org.eclipse.jdt.annotation.NonNullByDefault;
import org.eclipse.jdt.annotation.Nullable;
import org.eclipse.smarthome.config.core.Configuration;
import org.eclipse.smarthome.core.library.types.OnOffType;
import org.eclipse.smarthome.core.library.types.StringType;
import org.eclipse.smarthome.core.thing.Bridge;
import org.eclipse.smarthome.core.thing.ChannelUID;
import org.eclipse.smarthome.core.thing.Thing;
import org.eclipse.smarthome.core.thing.ThingStatus;
import org.eclipse.smarthome.core.thing.ThingStatusDetail;
import org.eclipse.smarthome.core.thing.binding.BaseThingHandler;
import org.eclipse.smarthome.core.thing.binding.builder.ThingBuilder;
import org.eclipse.smarthome.core.types.Command;
import org.eclipse.smarthome.core.types.RefreshType;
import org.eclipse.smarthome.core.types.State;
import org.openhab.binding.synologysurveillancestation.internal.thread.SynoApiThread;
import org.openhab.binding.synologysurveillancestation.internal.thread.SynoApiThreadCamera;
import org.openhab.binding.synologysurveillancestation.internal.thread.SynoApiThreadEvent;
import org.openhab.binding.synologysurveillancestation.internal.thread.SynoApiThreadSnapshot;
import org.openhab.binding.synologysurveillancestation.internal.webapi.SynoEvent;
import org.openhab.binding.synologysurveillancestation.internal.webapi.SynoWebApiHandler;
import org.openhab.binding.synologysurveillancestation.internal.webapi.WebApiException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * The {@link SynoCameraHandler} is responsible for handling commands, which are
 * sent to one of the channels of a camera Thing.
 *
 * @author Nils
 */
@NonNullByDefault
public class SynoCameraHandler extends BaseThingHandler implements SynoHandler {

    private final Logger logger = LoggerFactory.getLogger(SynoCameraHandler.class);
    private String cameraId = "";
    private boolean isPtz = false;
    private final Map<String, SynoApiThread<SynoCameraHandler>> threads = new HashMap<>();
    private @Nullable SynoWebApiHandler apiHandler;

    /**
     * Camera handler main constructor
     *
     * @param thing Thing to handle
     * @param isPtz PTZ support?
     */
    public SynoCameraHandler(Thing thing, boolean isPtz) {
        super(thing);
        this.isPtz = isPtz;
        int refreshRateSnapshot = 10;
        int refreshRateEvents = 3;
        try {
            refreshRateSnapshot = Integer.parseInt(thing.getConfiguration().get(REFRESH_RATE_SNAPSHOT).toString());
            refreshRateEvents = Integer.parseInt(thing.getConfiguration().get(REFRESH_RATE_EVENTS).toString());
        } catch (Exception ex) {
            logger.error("Error parsing camera Thing configuration");
        }
        threads.put(SynoApiThread.THREAD_SNAPSHOT, new SynoApiThreadSnapshot(this, refreshRateSnapshot));
        threads.put(SynoApiThread.THREAD_EVENT, new SynoApiThreadEvent(this, refreshRateEvents));
        threads.put(SynoApiThread.THREAD_CAMERA, new SynoApiThreadCamera(this, refreshRateEvents));
    }

    @Override
    public void handleCommand(ChannelUID channelUID, Command command) {
        try {
            switch (channelUID.getId()) {
                case CHANNEL_SNAPSHOT:
                    if (command.toString().equals("REFRESH")) {
                        threads.get(SynoApiThread.THREAD_SNAPSHOT).runOnce();
                    }
                    break;
                case CHANNEL_EVENT_MOTION:
                case CHANNEL_EVENT_ALARM:
                case CHANNEL_EVENT_MANUAL:
                    if (command.toString().equals("REFRESH")) {
                        updateState(channelUID, OnOffType.OFF);
                    }
                    break;
                case CHANNEL_ENABLE:
                case CHANNEL_RECORD:
                case CHANNEL_ZOOM:
                case CHANNEL_MOVE:
                    if (apiHandler != null) {
                        apiHandler.execute(cameraId, channelUID.getId(), command.toString());
                    }
                    break;
                case CHANNEL_SNAPSHOT_URI_STATIC:
                    if (apiHandler != null) {
                        int streamId = Integer.parseInt(this.getThing().getConfiguration().get(STREAM_ID).toString());
                        String uri = apiHandler.getSnapshotUri(cameraId, streamId);
                        updateState(channelUID, new StringType(uri));
                    }
                    break;
                case CHANNEL_LIVE_URI_RTSP:
                    if (apiHandler != null) {
                        String uri = apiHandler.getLiveUriResponse(cameraId).getRtsp();
                        updateState(channelUID, new StringType(uri));
                    }
                    break;
                case CHANNEL_LIVE_URI_MJPEG_HTTP:
                    if (apiHandler != null) {
                        String uri = apiHandler.getLiveUriResponse(cameraId).getMjpegHttp();
                        updateState(channelUID, new StringType(uri));
                    }
                    break;
            }
        } catch (WebApiException e) {
            logger.error("handle command: {}::{}", getThing().getLabel(), getThing().getUID());
        }

    }

    @Override
    public void initialize() {
        if (getBridge() != null) {
            cameraId = getThing().getUID().getId();

            logger.debug("Initializing SynologySurveillanceStationHandler for cameraId '{}'", cameraId);

            if (!isPtz) {
                ThingBuilder thingBuilder = editThing();
                thingBuilder.withoutChannel(new ChannelUID(thing.getUID(), CHANNEL_ZOOM));
                thingBuilder.withoutChannel(new ChannelUID(thing.getUID(), CHANNEL_MOVE));
                updateThing(thingBuilder.build());
            }

            if (getBridge().getStatus() == ThingStatus.ONLINE) {
                apiHandler = ((SynoBridgeHandler) getBridge().getHandler()).getSynoWebApiHandler();

                updateStatus(ThingStatus.ONLINE);
                for (SynoApiThread<SynoCameraHandler> thread : threads.values()) {
                    thread.start();
                }

                // Workaround for text configuration, whereby items are linked even before the handler initialization
                for (String channelID : STATIC_CHANNELS) {
                    if (isLinked(channelID)) {
                        ChannelUID channelUID = new ChannelUID(thing.getUID(), channelID);
                        handleCommand(channelUID, RefreshType.REFRESH);
                    }
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

    @Override
    public void dispose() {
        for (SynoApiThread<SynoCameraHandler> thread : threads.values()) {
            thread.stop();
        }
    }

    @Override
    public void handleConfigurationUpdate(Map<String, Object> configurationParameters) {
        Configuration configuration = editConfiguration();
        for (Entry<String, Object> configurationParameter : configurationParameters.entrySet()) {
            configuration.put(configurationParameter.getKey(), configurationParameter.getValue());
        }
        updateConfiguration(configuration);

        int refreshRateSnapshot = Integer.parseInt(configurationParameters.get(REFRESH_RATE_SNAPSHOT).toString());
        int refreshRateEvents = Integer.parseInt(configurationParameters.get(REFRESH_RATE_EVENTS).toString());

        threads.get(SynoApiThread.THREAD_SNAPSHOT).setRefreshRate(refreshRateSnapshot);
        threads.get(SynoApiThread.THREAD_EVENT).setRefreshRate(refreshRateEvents);
        threads.get(SynoApiThread.THREAD_CAMERA).setRefreshRate(refreshRateEvents);
    }

    @Override
    public void channelLinked(ChannelUID channelUID) {
        String id = channelUID.getId();
        switch (id) {
            case CHANNEL_EVENT_MOTION:
                ((SynoApiThreadEvent) threads.get(SynoApiThread.THREAD_EVENT)).getEvents().put(id,
                        new SynoEvent(SynoEvent.EVENT_REASON_MOTION));
                break;
            case CHANNEL_EVENT_ALARM:
                ((SynoApiThreadEvent) threads.get(SynoApiThread.THREAD_EVENT)).getEvents().put(id,
                        new SynoEvent(SynoEvent.EVENT_REASON_ALARM));
                break;
            case CHANNEL_EVENT_MANUAL:
                ((SynoApiThreadEvent) threads.get(SynoApiThread.THREAD_EVENT)).getEvents().put(id,
                        new SynoEvent(SynoEvent.EVENT_REASON_MANUAL));
                break;
        }
        handleCommand(channelUID, RefreshType.REFRESH);
    }

    @Override
    public void channelUnlinked(ChannelUID channelUID) {
        String id = channelUID.getId();
        ((SynoApiThreadEvent) threads.get(SynoApiThread.THREAD_EVENT)).getEvents().remove(id);
    }

    @Override
    public void updateStatus(ThingStatus status, ThingStatusDetail statusDetail, @Nullable String description) {
        super.updateStatus(status, statusDetail, description);
    }

    @Override
    public void updateStatus(ThingStatus status) {
        super.updateStatus(status);
    }

    @Override
    public @Nullable Bridge getBridge() {
        return super.getBridge();
    }

    @Override
    public void updateState(ChannelUID channelUID, State state) {
        super.updateState(channelUID, state);
    }

    @Override
    public boolean isLinked(String channelId) {
        return super.isLinked(channelId);
    }

    /**
     * @return service scheduler of this Thing
     */
    @Override
    public ScheduledExecutorService getScheduler() {
        return scheduler;
    }

    /**
     * @return the cameraId
     */
    public String getCameraId() {
        return cameraId;
    }

    @Override
    public @Nullable SynoWebApiHandler getSynoWebApiHandler() {
        return apiHandler;
    }

}
