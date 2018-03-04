/**
 * Copyright (c) 2010-2018 by the respective copyright holders.
 *
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 */
package org.openhab.binding.synologysurveillancestation;

import java.util.Collections;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import org.eclipse.jdt.annotation.NonNullByDefault;
import org.eclipse.smarthome.core.thing.ThingTypeUID;

/**
 * The {@link SynoBindingConstants} class defines common constants, which are
 * used across the whole binding.
 *
 * @author Nils
 */
@NonNullByDefault
public class SynoBindingConstants {

    public static final String BINDING_ID = "synologysurveillancestation";

    public static final String DEVICE_ID = "deviceID";

    // List of all Thing Type UIDs
    public static final ThingTypeUID THING_TYPE_CAMERA = new ThingTypeUID(BINDING_ID, "camera");
    public static final ThingTypeUID THING_TYPE_STATION = new ThingTypeUID(BINDING_ID, "station");

    public static final Set<ThingTypeUID> SUPPORTED_BRIDGE_TYPES = Collections.singleton(THING_TYPE_STATION);
    public static final Set<ThingTypeUID> SUPPORTED_CAMERA_TYPES = Collections.singleton(THING_TYPE_CAMERA);
    public static final Set<ThingTypeUID> SUPPORTED_THING_TYPES = Collections
            .unmodifiableSet(Stream.of(THING_TYPE_CAMERA, THING_TYPE_STATION).collect(Collectors.toSet()));

    /* List of all config properties */
    public static final String PROTOCOL = "protocol";
    public static final String HOST = "host";
    public static final String PORT = "port";
    public static final String USER_NAME = "username";
    public static final String PASSWORD = "password";
    public static final String SESSION_ID = "sessionID";
    public static final String REFRESH_RATE_SNAPSHOT = "refresh-rate-snapshot";
    public static final String REFRESH_RATE_EVENTS = "refresh-rate-events";
    public static final String STREAM_ID = "snapshot-stream-id";

    // List of all Bridge Channels
    public static final String CHANNEL_HOMEMODE = "homemode";

    // List of all Channel ids
    public static final String CHANNEL_SNAPSHOT_URI_DYNAMIC = "common#snapshot-uri-dynamic";
    public static final String CHANNEL_SNAPSHOT_URI_STATIC = "common#snapshot-uri-static";
    public static final String CHANNEL_LIVE_URI_RTSP = "common#live-uri-rtsp";
    public static final String CHANNEL_LIVE_URI_MJPEG_HTTP = "common#live-uri-mjpeg-http";

    public static final Set<String> STATIC_CHANNELS = Collections
            .unmodifiableSet(Stream.of(CHANNEL_SNAPSHOT_URI_STATIC, CHANNEL_LIVE_URI_RTSP, CHANNEL_LIVE_URI_MJPEG_HTTP)
                    .collect(Collectors.toSet()));

    public static final String CHANNEL_SNAPSHOT = "common#snapshot";
    public static final String CHANNEL_RECORD = "common#record";
    public static final String CHANNEL_ENABLE = "common#enable";
    public static final String CHANNEL_ZOOM = "ptz#zoom";
    public static final String CHANNEL_MOVE = "ptz#move";

    // List of all event types (as in thing.xml)
    public static final String CHANNEL_EVENT_MOTION = "event#motion";
    public static final String CHANNEL_EVENT_ALARM = "event#alarm";
    public static final String CHANNEL_EVENT_MANUAL = "event#manual";
}
