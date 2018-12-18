/**
 * Copyright (c) 2010-2018 by the respective copyright holders.
 *
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
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
    ACCOUNT_LOCKED(411, "Account Locked (when account max try exceed).");

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
