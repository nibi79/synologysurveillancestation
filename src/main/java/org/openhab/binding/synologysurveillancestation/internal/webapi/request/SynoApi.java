/**
 * Copyright (c) 2010-2021 Contributors to the openHAB project
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
package org.openhab.binding.synologysurveillancestation.internal.webapi.request;

import org.eclipse.jdt.annotation.NonNullByDefault;

/**
 * The {@link SynoApi} is an interface for Synology API codes
 *
 * @author Nils - Initial contribution
 * @author Pavion - Contribution
 */
@NonNullByDefault
public interface SynoApi {

    // API configuration versions
    public static final String API_VERSION_01 = "1";
    public static final String API_VERSION_02 = "2";
    public static final String API_VERSION_03 = "3";
    public static final String API_VERSION_04 = "4";
    public static final String API_VERSION_05 = "5";
    public static final String API_VERSION_06 = "6";
    public static final String API_VERSION_07 = "7";
    public static final String API_VERSION_08 = "8";
    public static final String API_VERSION_09 = "9";

    // API configuration scripts
    public static final String API_SCRIPT_AUTH = "/webapi/auth.cgi";
    public static final String API_SCRIPT_ENTRY = "/webapi/entry.cgi";
    public static final String API_SCRIPT_QUERY = "/webapi/query.cgi";

    // API methods
    public static final String METHOD_LOGIN = "Login";
    public static final String METHOD_LOGOUT = "Logout";

    public static final String METHOD_LIST = "List";
    public static final String METHOD_GETINFO = "GetInfo";
    public static final String METHOD_ENABLE = "Enable";
    public static final String METHOD_DISABLE = "Disable";
    public static final String METHOD_GETSNAPSHOT = "GetSnapshot";
    public static final String METHOD_LIVEVIEWPATH = "GetLiveViewPath";

    public static final String METHOD_RECORD = "Record";

    public static final String METHOD_ZOOM = "Zoom";
    public static final String METHOD_MOVE = "Move";
    public static final String METHOD_LISTPRESET = "ListPreset";
    public static final String METHOD_GOPRESET = "GoPreset";
    public static final String METHOD_LISTPATROL = "ListPatrol";
    public static final String METHOD_RUNPATROL = "RunPatrol";
    public static final String METHOD_SWITCH = "Switch";
    public static final String METHOD_TRIGGER = "Trigger";

    public static final String METHOD_MOTIONENUM = "MotionEnum";
    public static final String METHOD_MDPARAMSAVE = "MDParamSave";

    public static final int CONNECTION_TIMEOUT = 5000;

    public SynoApiConfig getApiConfig();
}
