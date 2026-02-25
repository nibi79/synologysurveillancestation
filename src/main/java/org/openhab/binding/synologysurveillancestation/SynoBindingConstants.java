/*
 * Copyright (c) 2010-2026 Contributors to the openHAB project
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
package org.openhab.binding.synologysurveillancestation;

import java.util.Collections;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import org.eclipse.jdt.annotation.NonNullByDefault;
import org.openhab.core.thing.ThingTypeUID;

/**
 * The {@link SynoBindingConstants} class defines common constants, which are
 * used across the whole binding.
 *
 * @author Nils - Initial contribution
 * @author Pavion - Contribution
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
    public static final String ACCEPT_SSL = "acceptSsl";
    public static final String HOST = "host";
    public static final String PORT = "port";
    public static final String USER_NAME = "username";
    public static final String PASSWORD = "password";
    public static final String SERIAL = "serial";
    public static final String SESSION_ID = "sessionID";

    // List of all Bridge Channels
    public static final String CHANNEL_HOMEMODE = "homemode";
    public static final String CHANNEL_EVENT_TRIGGER = "eventtrigger";
    public static final String CHANNEL_SID = "sid";

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

    // List of all PTZ channels
    public static final String CHANNEL_ZOOM = "ptz#zoom";
    public static final String CHANNEL_MOVE = "ptz#move";
    public static final String CHANNEL_MOVEPRESET = "ptz#movepreset";
    public static final String CHANNEL_RUNPATROL = "ptz#runpatrol";
    public static final Set<String> CHANNEL_PTZ = Collections.unmodifiableSet(
            Stream.of(CHANNEL_ZOOM, CHANNEL_MOVE, CHANNEL_MOVEPRESET, CHANNEL_RUNPATROL).collect(Collectors.toSet()));

    // List of all move commands
    public static final String MOVE_COMMAND_EMPTY = "";
    public static final String MOVE_COMMAND_START = "Start";
    public static final String MOVE_COMMAND_STOP = "Stop";

    // List of all event types (as in thing.xml)
    public static final String CHANNEL_EVENT_MOTION = "event#motion";
    public static final String CHANNEL_EVENT_ALARM = "event#alarm";
    public static final String CHANNEL_EVENT_MANUAL = "event#manual";
    public static final String CHANNEL_EVENT_EXTERNAL = "event#external";
    public static final String CHANNEL_EVENT_CONTINUOUS = "event#continuous";
    public static final String CHANNEL_EVENT_ACTIONRULE = "event#actionrule";
    public static final Set<String> CHANNEL_EVENT = Collections.unmodifiableSet(
            Stream.of(CHANNEL_EVENT_MOTION, CHANNEL_EVENT_ALARM, CHANNEL_EVENT_MANUAL, CHANNEL_EVENT_CONTINUOUS,
                    CHANNEL_EVENT_EXTERNAL, CHANNEL_EVENT_ACTIONRULE).collect(Collectors.toSet()));

    // List of all MD parameters
    public static final String CHANNEL_MDPARAM_SOURCE = "md-param#md-param-source";
    public static final String CHANNEL_MDPARAM_SENSITIVITY = "md-param#md-param-sensitivity";
    public static final String CHANNEL_MDPARAM_THRESHOLD = "md-param#md-param-threshold";
    public static final String CHANNEL_MDPARAM_OBJECTSIZE = "md-param#md-param-objectsize";
    public static final String CHANNEL_MDPARAM_PERCENTAGE = "md-param#md-param-percentage";
    public static final String CHANNEL_MDPARAM_SHORTLIVE = "md-param#md-param-shortlive";
    public static final Set<String> CHANNEL_MDPARAM = Collections.unmodifiableSet(Stream
            .of(CHANNEL_MDPARAM_SOURCE, CHANNEL_MDPARAM_SENSITIVITY, CHANNEL_MDPARAM_THRESHOLD,
                    CHANNEL_MDPARAM_OBJECTSIZE, CHANNEL_MDPARAM_PERCENTAGE, CHANNEL_MDPARAM_SHORTLIVE)
            .collect(Collectors.toSet()));
}
