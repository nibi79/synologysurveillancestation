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
package org.openhab.binding.synologysurveillancestation.internal.webapi.request;

import static org.openhab.binding.synologysurveillancestation.SynoBindingConstants.*;

import java.util.HashMap;
import java.util.Map;

import org.eclipse.jdt.annotation.NonNullByDefault;
import org.eclipse.jetty.client.HttpClient;
import org.openhab.binding.synologysurveillancestation.internal.SynoConfig;
import org.openhab.binding.synologysurveillancestation.internal.webapi.WebApiException;
import org.openhab.binding.synologysurveillancestation.internal.webapi.error.WebApiAuthErrorCodes;
import org.openhab.binding.synologysurveillancestation.internal.webapi.response.SimpleResponse;

/**
 * SYNO.SurveillanceStation.SynoApiPTZ
 *
 * This API provides a set of methods to execute PTZ action, and to acquire PTZ related information such as
 * patrol list or patrol schedule of a camera.
 *
 * Method:
 * - Move
 * - Zoom
 * - ListPreset
 * - GoPreset
 * - ListPatrol
 * - RunPatrol
 * - Focus
 * - Iris
 * - AutoFocus
 * - AbsPtz
 * - Home
 * - AutoPan
 * - ObjTracking
 *
 * @author Nils - Initial contribution
 * @author Pavion - Contribution
 */
@NonNullByDefault
public class SynoApiPTZ extends SynoApiRequest<SimpleResponse> {

    // API configuration
    private static final String API_NAME = "SYNO.SurveillanceStation.PTZ";
    private static final SynoApiConfig API_CONFIG = new SynoApiConfig(API_NAME, API_VERSION_03, API_SCRIPT_ENTRY);

    /**
     * @param config
     */
    public SynoApiPTZ(SynoConfig config, HttpClient httpClient) {
        super(API_CONFIG, config, httpClient);
    }

    /**
     * Execute the given PTZ method for the passed camera.
     *
     * @param cameraId
     * @param method
     * @param command
     * @throws WebApiException
     */
    public void execute(String cameraId, String method, String command) throws WebApiException {
        switch (method) {
            case CHANNEL_ZOOM:
                switch (command) {
                    case "IN":
                        zoomIn(cameraId, MOVE_COMMAND_EMPTY);
                        break;
                    case "OUT":
                        zoomOut(cameraId, MOVE_COMMAND_EMPTY);
                        break;
                    // START
                    case "START_IN":
                        zoomIn(cameraId, MOVE_COMMAND_START);
                        break;
                    case "START_OUT":
                        zoomOut(cameraId, MOVE_COMMAND_START);
                        break;
                    // STOP
                    case "STOP_IN":
                        zoomIn(cameraId, MOVE_COMMAND_STOP);
                        break;
                    case "STOP_OUT":
                        zoomOut(cameraId, MOVE_COMMAND_STOP);
                        break;
                }
                break;
            case CHANNEL_MOVE:
                switch (command) {
                    case "UP":
                        moveUp(cameraId, MOVE_COMMAND_EMPTY);
                        break;
                    case "DOWN":
                        moveDown(cameraId, MOVE_COMMAND_EMPTY);
                        break;
                    case "LEFT":
                        moveLeft(cameraId, MOVE_COMMAND_EMPTY);
                        break;
                    case "RIGHT":
                        moveRight(cameraId, MOVE_COMMAND_EMPTY);
                        break;
                    case "HOME":
                        moveHome(cameraId, MOVE_COMMAND_EMPTY);
                        break;
                    // START
                    case "START_UP":
                        moveUp(cameraId, MOVE_COMMAND_START);
                        break;
                    case "START_DOWN":
                        moveDown(cameraId, MOVE_COMMAND_START);
                        break;
                    case "START_LEFT":
                        moveLeft(cameraId, MOVE_COMMAND_START);
                        break;
                    case "START_RIGHT":
                        moveRight(cameraId, MOVE_COMMAND_START);
                        break;
                    case "START_HOME":
                        moveHome(cameraId, MOVE_COMMAND_START);
                        // STOP
                    case "STOP_UP":
                        moveUp(cameraId, MOVE_COMMAND_STOP);
                        break;
                    case "STOP_DOWN":
                        moveDown(cameraId, MOVE_COMMAND_STOP);
                        break;
                    case "STOP_LEFT":
                        moveLeft(cameraId, MOVE_COMMAND_STOP);
                        break;
                    case "STOP_RIGHT":
                        moveRight(cameraId, MOVE_COMMAND_STOP);
                        break;
                    case "STOP_HOME":
                        moveHome(cameraId, MOVE_COMMAND_STOP);
                }
                break;
            default:
                break;
        }
    }

    /**
     * calls api method 'zoom' with passed control
     *
     * @param cameraId
     * @param control
     * @param moveType
     * @return
     * @throws WebApiException
     */
    private SimpleResponse callZoom(String cameraId, String control, String moveType) throws WebApiException {
        Map<String, String> params = new HashMap<>();

        // API parameters
        params.put("cameraId", cameraId);
        params.put("control", control);
        if (!moveType.equals(MOVE_COMMAND_EMPTY)) {
            Integer version = Integer.parseInt(API_CONFIG.getVersion());
            if (version < 3) {
                throw new WebApiException(WebApiAuthErrorCodes.API_VERSION_NOT_SUPPORTED);
            }
            params.put("moveType", moveType);
        }

        return callApi(METHOD_ZOOM, params);
    }

    /**
     * calls api method 'move' with passed direction and speed
     *
     * @param cameraId
     * @param direction
     * @param speed
     * @param moveType
     * @return
     * @throws WebApiException
     */
    private SimpleResponse callMove(String cameraId, String direction, int speed, String moveType)
            throws WebApiException {
        Map<String, String> params = new HashMap<>();

        // API Parameters
        params.put("cameraId", cameraId);
        params.put("direction", direction);
        params.put("speed", String.valueOf(speed));
        if (!moveType.equals(MOVE_COMMAND_EMPTY)) {
            Integer version = Integer.parseInt(API_CONFIG.getVersion());
            if (version < 3) {
                throw new WebApiException(WebApiAuthErrorCodes.API_VERSION_NOT_SUPPORTED);
            }
            params.put("moveType", moveType);
        }

        return callApi(METHOD_MOVE, params);
    }

    /**
     * Control the PTZ camera to zoom out.
     *
     * @param cameraId
     * @return
     * @throws WebApiException
     */
    public SimpleResponse zoomOut(String cameraId, String moveType) throws WebApiException {
        return callZoom(cameraId, "out", moveType);
    }

    /**
     * Control the PTZ camera to zoom in.
     *
     * @param cameraId
     * @return
     * @throws WebApiException
     */
    public SimpleResponse zoomIn(String cameraId, String moveType) throws WebApiException {
        return callZoom(cameraId, "in", moveType);
    }

    /**
     * Control the PTZ camera to move its lens up.
     *
     * @param cameraId
     * @return
     * @throws WebApiException
     */
    public SimpleResponse moveUp(String cameraId, String moveType) throws WebApiException {
        return callMove(cameraId, "up", 1, moveType);
    }

    /**
     * Control the PTZ camera to move its lens down.
     *
     * @param cameraId
     * @return
     * @throws WebApiException
     */
    public SimpleResponse moveDown(String cameraId, String moveType) throws WebApiException {
        return callMove(cameraId, "down", 1, moveType);
    }

    /**
     * Control the PTZ camera to move its lens left.
     *
     * @param cameraId
     * @return
     * @throws WebApiException
     */
    public SimpleResponse moveLeft(String cameraId, String moveType) throws WebApiException {
        return callMove(cameraId, "left", 1, moveType);
    }

    /**
     * Control the PTZ camera to move its lens right.
     *
     * @param cameraId
     * @return
     * @throws WebApiException
     */
    public SimpleResponse moveRight(String cameraId, String moveType) throws WebApiException {
        return callMove(cameraId, "right", 1, moveType);
    }

    /**
     * Control the PTZ camera to move to preset HOME.
     *
     * @param cameraId
     * @return
     * @throws WebApiException
     */
    public SimpleResponse moveHome(String cameraId, String moveType) throws WebApiException {
        return callMove(cameraId, "home", 1, moveType);
    }

    /**
     * calls api method 'ListPreset' and list all presets for moving.
     *
     * @param cameraId
     * @return
     * @throws WebApiException
     */
    public SimpleResponse listPresets(String cameraId) throws WebApiException {
        Map<String, String> params = new HashMap<>();

        // API Parameters
        params.put("cameraId", cameraId);

        SimpleResponse response = callApi(METHOD_LISTPRESET, params);

        return response;
    }

    /**
     * calls api method 'GoPreset' and move the camera lens to a pre-defined preset position.
     *
     * @param cameraId
     * @param presetId
     * @return
     * @throws WebApiException
     */
    public SimpleResponse goPreset(String cameraId, String presetId) throws WebApiException {
        Map<String, String> params = new HashMap<>();

        // API Parameters
        params.put("cameraId", cameraId);
        params.put("presetId", presetId);
        // params.put("position", ???);
        // params.put("speed", ???);
        // params.put("type", ???);
        // params.put("isPatrol", ???);

        SimpleResponse response = callApi(METHOD_GOPRESET, params);

        return response;
    }

    /**
     * calls api method 'ListPatrol' and list all patrols.
     *
     * @param cameraId
     * @return
     * @throws WebApiException
     */
    public SimpleResponse listPatrol(String cameraId) throws WebApiException {
        Map<String, String> params = new HashMap<>();

        // API Parameters
        params.put("cameraId", cameraId);

        SimpleResponse response = callApi(METHOD_LISTPATROL, params);

        return response;
    }

    /**
     * calls api method 'RunPatrol' and execute the given patrol.
     *
     * @param cameraId
     * @param patrolId
     * @return
     * @throws WebApiException
     */
    public SimpleResponse runPatrol(String cameraId, String patrolId) throws WebApiException {
        Map<String, String> params = new HashMap<>();

        // API Parameters
        params.put("cameraId", cameraId);
        params.put("patrolId", patrolId);

        SimpleResponse response = callApi(METHOD_RUNPATROL, params);

        return response;
    }
}
