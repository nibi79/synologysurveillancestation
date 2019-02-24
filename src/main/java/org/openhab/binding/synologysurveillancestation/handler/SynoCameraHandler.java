/**
 * Copyright (c) 2010-2019 by the respective copyright holders.
 *
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 */
package org.openhab.binding.synologysurveillancestation.handler;

import static org.openhab.binding.synologysurveillancestation.SynoBindingConstants.*;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
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
import org.eclipse.smarthome.core.thing.ThingStatusInfo;
import org.eclipse.smarthome.core.thing.binding.BaseThingHandler;
import org.eclipse.smarthome.core.thing.binding.builder.ThingBuilder;
import org.eclipse.smarthome.core.types.Command;
import org.eclipse.smarthome.core.types.RefreshType;
import org.eclipse.smarthome.core.types.State;
import org.eclipse.smarthome.core.types.StateOption;
import org.openhab.binding.synologysurveillancestation.internal.discovery.SynoDynamicStateDescriptionProvider;
import org.openhab.binding.synologysurveillancestation.internal.thread.SynoApiThread;
import org.openhab.binding.synologysurveillancestation.internal.thread.SynoApiThreadCamera;
import org.openhab.binding.synologysurveillancestation.internal.thread.SynoApiThreadCameraEvent;
import org.openhab.binding.synologysurveillancestation.internal.thread.SynoApiThreadEvent;
import org.openhab.binding.synologysurveillancestation.internal.thread.SynoApiThreadLiveUri;
import org.openhab.binding.synologysurveillancestation.internal.thread.SynoApiThreadSnapshot;
import org.openhab.binding.synologysurveillancestation.internal.webapi.SynoWebApiHandler;
import org.openhab.binding.synologysurveillancestation.internal.webapi.WebApiException;
import org.openhab.binding.synologysurveillancestation.internal.webapi.response.CameraEventResponse;
import org.openhab.binding.synologysurveillancestation.internal.webapi.response.CameraResponse;
import org.openhab.binding.synologysurveillancestation.internal.webapi.response.SimpleResponse;
import org.openhab.binding.synologysurveillancestation.internal.webapi.response.SynoApiResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;

/**
 * The {@link SynoCameraHandler} is responsible for handling commands, which are
 * sent to one of the channels of a camera Thing.
 *
 * @author Nils - Initial contribution
 * @author Pavion - Contribution
 */
@NonNullByDefault
public class SynoCameraHandler extends BaseThingHandler implements SynoHandler {

    private final Logger logger = LoggerFactory.getLogger(SynoCameraHandler.class);
    private String cameraId = "";
    private boolean ptz = false;
    private final Map<String, SynoApiThread<SynoCameraHandler>> threads = new HashMap<>();
    private @Nullable SynoWebApiHandler apiHandler;

    private @Nullable SynoDynamicStateDescriptionProvider stateDescriptionProvider;

    /**
     * Camera handler main constructor
     *
     * @param thing Thing to handle
     * @param ptz   PTZ support?
     */
    public SynoCameraHandler(Thing thing, SynoDynamicStateDescriptionProvider stateDescriptionProvider) {
        super(thing);

        this.stateDescriptionProvider = stateDescriptionProvider;

        int refreshRateSnapshot = 10;
        int refreshRateEvents = 3;
        int refreshRateCameraEvent = 0;

        try {
            refreshRateSnapshot = Integer.parseInt(thing.getConfiguration().get(REFRESH_RATE_SNAPSHOT).toString());
            refreshRateEvents = Integer.parseInt(thing.getConfiguration().get(REFRESH_RATE_EVENTS).toString());
            refreshRateCameraEvent = Integer
                    .parseInt(thing.getConfiguration().get(REFRESH_RATE_CAMERAEVENT).toString());
        } catch (Exception ex) {
            logger.error("Error parsing camera Thing configuration");
        }

        threads.put(SynoApiThread.THREAD_SNAPSHOT, new SynoApiThreadSnapshot(this, refreshRateSnapshot));
        threads.put(SynoApiThread.THREAD_EVENT, new SynoApiThreadEvent(this, refreshRateEvents));
        threads.put(SynoApiThread.THREAD_CAMERA, new SynoApiThreadCamera(this, refreshRateEvents));
        threads.put(SynoApiThread.THREAD_LIVEURI, new SynoApiThreadLiveUri(this, refreshRateEvents));
        threads.put(SynoApiThread.THREAD_CAMERAEVENT, new SynoApiThreadCameraEvent(this, refreshRateCameraEvent));
    }

    @Override
    public void handleCommand(ChannelUID channelUID, Command command) {
        if (apiHandler == null) {
            return;
        }
        try {
            if (command.toString().equals("REFRESH")) {
                switch (channelUID.getId()) {
                    case CHANNEL_SNAPSHOT:
                        threads.get(SynoApiThread.THREAD_SNAPSHOT).runOnce();
                        break;
                    case CHANNEL_EVENT_MOTION:
                    case CHANNEL_EVENT_ALARM:
                    case CHANNEL_EVENT_MANUAL:
                    case CHANNEL_EVENT_EXTERNAL:
                    case CHANNEL_EVENT_ACTIONRULE:
                        updateState(channelUID, OnOffType.OFF);
                        break;
                    case CHANNEL_SNAPSHOT_URI_STATIC:
                        int streamId = Integer.parseInt(this.getThing().getConfiguration().get(STREAM_ID).toString());
                        String uri = apiHandler.getApiCamera().getSnapshotUri(cameraId, streamId);
                        updateState(channelUID, new StringType(uri));
                        break;
                    case CHANNEL_LIVE_URI_RTSP:
                        String rtsp = apiHandler.getApiLiveUri().getLiveUriResponse(cameraId).getRtsp();
                        updateState(channelUID, new StringType(rtsp));
                        break;
                    case CHANNEL_LIVE_URI_MJPEG_HTTP:
                        String mjpeg = apiHandler.getApiLiveUri().getLiveUriResponse(cameraId).getMjpegHttp();
                        updateState(channelUID, new StringType(mjpeg));
                        break;
                }
            } else {
                switch (channelUID.getId()) {
                    case CHANNEL_ENABLE:
                        apiHandler.getApiCamera().toggleCamera(cameraId, command.toString().equals("ON"));
                    case CHANNEL_RECORD:
                        apiHandler.getApiExternalRecording().toggleRecording(cameraId, command.toString().equals("ON"));
                    case CHANNEL_ZOOM:
                    case CHANNEL_MOVE:
                        apiHandler.getApiPTZ().execute(cameraId, channelUID.getId(), command.toString());
                        break;
                    case CHANNEL_MOVEPRESET:
                        apiHandler.getApiPTZ().goPreset(cameraId, command.toString());
                        break;
                    case CHANNEL_RUNPATROL:
                        apiHandler.getApiPTZ().runPatrol(cameraId, command.toString());
                        break;
                    case CHANNEL_MDPARAM_SOURCE:
                        apiHandler.getApiCameraEvent().setSource(cameraId, command.toString());
                        break;
                    case CHANNEL_MDPARAM_SENSIVITY:
                        apiHandler.getApiCameraEvent().setSensitivity(cameraId, Integer.parseInt(command.toString()));
                        break;
                    case CHANNEL_MDPARAM_THRESHOLD:
                        apiHandler.getApiCameraEvent().setThreshold(cameraId, Integer.parseInt(command.toString()));
                        break;
                    case CHANNEL_MDPARAM_OBJECTSIZE:
                        apiHandler.getApiCameraEvent().setObjectSize(cameraId, Integer.parseInt(command.toString()));
                        break;
                    case CHANNEL_MDPARAM_PERCENTAGE:
                        apiHandler.getApiCameraEvent().setPercentage(cameraId, Integer.parseInt(command.toString()));
                        break;
                    case CHANNEL_MDPARAM_SHORTLIVE:
                        apiHandler.getApiCameraEvent().setShortLiveSecond(cameraId,
                                Integer.parseInt(command.toString()));
                        break;
                }
            }

        } catch (

        WebApiException e) {
            logger.error("handle command: {}::{}", getThing().getLabel(), getThing().getUID());
        }

    }

    @Override
    public boolean reconnect(boolean forceLogout) throws WebApiException {
        boolean ret = ((SynoBridgeHandler) getBridge().getHandler()).reconnect(forceLogout);
        if (ret) {
            refreshStatic();
        }
        return ret;
    }

    @Override
    public void initialize() {
        if (getBridge() != null) {

            cameraId = getThing().getUID().getId();

            logger.debug("Initializing SynologySurveillanceStationHandler for cameraId '{}'", cameraId);

            if (getBridge().getStatus() == ThingStatus.ONLINE) {
                apiHandler = ((SynoBridgeHandler) getBridge().getHandler()).getSynoWebApiHandler();

                try {
                    List<String> toExclude = new ArrayList<>();

                    CameraResponse cameraDetails = apiHandler.getApiCamera().getInfo(cameraId);
                    Map<String, Object> properties = cameraDetails.getCameraProperties(cameraId);
                    if (properties.isEmpty()) {
                        updateStatus(ThingStatus.OFFLINE, ThingStatusDetail.OFFLINE.GONE);
                        return;
                    }
                    ptz = properties.getOrDefault(SynoApiResponse.PROP_PTZ, "false").equals("true");

                    if (!ptz) {
                        toExclude.addAll(CHANNEL_PTZ);
                    } else {
                        if (isLinked(CHANNEL_MOVEPRESET)) {
                            updatePresets();
                        }

                        if (isLinked(CHANNEL_RUNPATROL)) {
                            updatePatrols();
                        }
                    }

                    CameraEventResponse cameraEventResponse = apiHandler.getApiCameraEvent().getMDParam(cameraId);
                    if (!cameraEventResponse.isSuccess()) {
                        toExclude.addAll(CHANNEL_MDPARAM);
                    } else {
                        threads.get(SynoApiThread.THREAD_CAMERAEVENT).runOnce();
                    }

                    if (toExclude.size() > 0) {
                        ThingBuilder thingBuilder = editThing();
                        for (String channel : toExclude) {
                            thingBuilder.withoutChannel(new ChannelUID(thing.getUID(), channel));
                        }
                        updateThing(thingBuilder.build());
                    }
                } catch (WebApiException e) {
                    logger.error("initialize camera: id {} - {}::{}", cameraId, getThing().getLabel(),
                            getThing().getUID());
                }

                updateStatus(ThingStatus.ONLINE);
                for (SynoApiThread<SynoCameraHandler> thread : threads.values()) {
                    thread.start();
                }

                refreshStatic();
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

    public void refreshStatic() {
        for (String channelID : STATIC_CHANNELS) {
            if (isLinked(channelID)) {
                ChannelUID channelUID = new ChannelUID(thing.getUID(), channelID);
                handleCommand(channelUID, RefreshType.REFRESH);
            }
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
        int refreshRateCameraEvent = Integer
                .parseInt(thing.getConfiguration().get(REFRESH_RATE_CAMERAEVENT).toString());
        threads.get(SynoApiThread.THREAD_SNAPSHOT).setRefreshRate(refreshRateSnapshot);
        threads.get(SynoApiThread.THREAD_EVENT).setRefreshRate(refreshRateEvents);
        threads.get(SynoApiThread.THREAD_CAMERA).setRefreshRate(refreshRateEvents);
        threads.get(SynoApiThread.THREAD_LIVEURI).setRefreshRate(refreshRateEvents);
        threads.get(SynoApiThread.THREAD_CAMERAEVENT).setRefreshRate(refreshRateCameraEvent);
    }

    @Override
    public void bridgeStatusChanged(ThingStatusInfo bridgeStatusInfo) {
        if (bridgeStatusInfo.getStatus() == ThingStatus.ONLINE) {
            initialize();
        } else if (bridgeStatusInfo.getStatus() == ThingStatus.OFFLINE) {
            dispose();
        }
    }

    @Override
    public void channelLinked(ChannelUID channelUID) {
        handleCommand(channelUID, RefreshType.REFRESH);
    }

    @Override
    public void channelUnlinked(ChannelUID channelUID) {
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
    public void updateState(String channel, State state) {
        super.updateState(channel, state);
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

    /**
     * load and update options for presets
     *
     * @return
     * @throws WebApiException
     */
    public void updatePresets() throws WebApiException {
        SimpleResponse listPresetResponse = apiHandler.getApiPTZ().listPresets(cameraId);

        JsonObject data = listPresetResponse.getData();
        List<StateOption> options = new ArrayList<>();
        if (data != null) {
            JsonArray presets = data.getAsJsonArray("presets");
            if (presets != null) {
                for (JsonElement preset : presets) {
                    JsonObject op = preset.getAsJsonObject();
                    options.add(new StateOption(op.get("id").getAsString(), op.get("name").getAsString()));
                }
            }
        }
        stateDescriptionProvider.setStateOptions(new ChannelUID(getThing().getUID(), CHANNEL_MOVEPRESET), options);
    }

    /**
     * load and update options for patrols
     *
     * @return
     * @throws WebApiException
     */
    public void updatePatrols() throws WebApiException {
        SimpleResponse listPatrolResponse = apiHandler.getApiPTZ().listPatrol(cameraId);

        JsonObject data = listPatrolResponse.getData();
        List<StateOption> options = new ArrayList<>();
        if (data != null) {
            JsonArray patrols = data.getAsJsonArray("patrols");
            if (patrols != null) {
                for (JsonElement patrol : patrols) {
                    JsonObject op = patrol.getAsJsonObject();
                    options.add(new StateOption(op.get("id").getAsString(), op.get("name").getAsString()));
                }
            }
        }
        stateDescriptionProvider.setStateOptions(new ChannelUID(getThing().getUID(), CHANNEL_RUNPATROL), options);
    }

    /**
     * Returns true if this camera supports PTZ
     *
     * @return true/false
     */
    public boolean isPtz() {
        return ptz;
    }

}
