/**
 * Copyright (c) 2010-2020 Contributors to the openHAB project
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
package org.openhab.binding.synologysurveillancestation.internal.webapi.error;

/**
 * The {@link WebApiAuthErrorCodes} hosts errorCodes for SYNO.API.Auth.
 *
 * @author Nils - Initial contribution
 * @author Pavion - Contribution
 */
public enum WebApiAuthErrorCodes implements ErrorCode {

    UNKNOWN_ERROR(100, "Unknown error."),
    PARAM_NOT_SPECIFIED(101, "The account parameter is not specified."),
    API_DOES_NOT_EXIST(102, "Surveillance Station is not running."),
    METHOD_NOT_EXIST(103, "Method does not exist"),
    API_VERSION_NOT_SUPPORTED(104, "This API version is not supported"),
    INSUFFICIENT_USER_PRIVILEGE(105, "Insufficient user privilege"),
    CONNECT_TIMEOUT(106, "Connection time out"),
    MULTIPLE_LOGIN(107, "Multiple login detected"),
    NEED_MANAGER_RIGHTS(117, "Need manager rights for operation"),
    UNKNOWN_ERROR_119(119, "Unknown API error 119"),
    INVALID_PASSWORD(400, "Invalid password."),
    DISABLED_ACCOUNT(401, "Guest or disabled account."),
    PERMISSION_DENIED(402, "Permission denied."),
    ONE_TIME_PASSWD_NOT_SCECIFIED(403, "One time password not specified."),
    ONE_TIME_PASSWD_AUTH_FAILED(404, "One time password authenticate failed."),
    APPORTAL_INCORRECT(405, "Appportal incorrect."),
    OTP_CODE_ENDFORCED(406, "OTP code enforced."),
    MAX_TRIES(407, "Max Tries (if auto blocking is set to true)."),
    PASSWD_EXP_CAN_NOT_CHANGE(408, "Password Expired Can not Change."),
    PASSWD_EXPIRED(409, "Password Expired."),
    PASSWD_MUST_CHANGE(410, "Password must change (when first time use or after reset password by admin)."),
    ACCOUNT_LOCKED(411, "Account Locked (when account max try exceed)."),
    MISSING_LICENSE(412, "Need to add license."),
    PLATFORM_MAX_REACHED(413, "Reach the maximum of platform."),
    EVENT_NOT_EXIST(414, "Some events not exist."),
    MESSAGE_CONNECT_ERROR(415, "message connect failed"),
    TEST_CONNETCION_ERROR(417, "Test Connection Error."),
    VISUAL_NAME_REPITITION(419, "Visualstation name repetition."),
    TOO_MANY_ITEMS(439, "Too many items selected.");

    private final int code;
    private final String msg;

    WebApiAuthErrorCodes(int code, String msg) {
        this.code = code;
        this.msg = msg;
    }

    @Override
    public int getCode() {
        return this.code;
    }

    @Override
    public String getMsg() {
        return this.msg;
    }

    /**
     *
     * @param code
     * @return
     */
    public static ErrorCode getByCode(int code) {
        return ErrorCode.lookup(WebApiAuthErrorCodes.class, code);
    }

    @Override
    public String toString() {
        return this.name() + " | ErrorCode: " + this.getCode() + " - " + this.getMsg();
    }
}
