/**
 * Copyright (c) 2010-2019 by the respective copyright holders.
 *
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 */
package org.openhab.binding.synologysurveillancestation.internal.webapi;

import org.eclipse.jdt.annotation.NonNullByDefault;
import org.openhab.binding.synologysurveillancestation.internal.webapi.error.ErrorCode;

/**
 * The {@link WebApiException} is a class for handling the binding exceptions
 *
 * @author Nils - Initial contribution
 * @author Pavion - Contribution
 */
@NonNullByDefault
public class WebApiException extends Exception {

    private static final long serialVersionUID = 1L;

    private static final int UNKNOWN = 0;

    private final int errorCode;
    private final String errorMsg;

    public WebApiException(int errorCode, String errorMsg, Throwable cause) {
        super(errorMsg, cause);
        this.errorCode = errorCode;
        this.errorMsg = errorMsg;
    }

    public WebApiException(int errorCode, String errorMsg) {
        super(errorMsg);
        this.errorCode = errorCode;
        this.errorMsg = errorMsg;
    }

    public WebApiException(ErrorCode errorCode) {
        super();
        this.errorCode = errorCode.getCode();
        this.errorMsg = errorCode.getMsg();
    }

    public WebApiException(String errorMsg, Throwable cause) {
        super(errorMsg);
        this.errorCode = UNKNOWN;
        this.errorMsg = errorMsg;
    }

    public WebApiException(String errorMsg) {
        super(errorMsg);
        this.errorCode = UNKNOWN;
        this.errorMsg = errorMsg;
    }

    public WebApiException(Throwable cause) {
        super(cause.getMessage(), cause);
        this.errorCode = UNKNOWN;
        this.errorMsg = cause.getMessage();
    }

    public int getErrorCode() {
        return errorCode;
    }

    public String getErrorMsg() {
        return errorMsg;
    }

}
