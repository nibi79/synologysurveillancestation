/**
 * Copyright (c) 2010-2018 by the respective copyright holders.
 *
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 */
package org.openhab.binding.synologysurveillancestation.internal.webapi.error;

import java.util.stream.Stream;

/**
 * The {@link ErrorCode} is an interface for error codes
 *
 * @author Nils
 */
public interface ErrorCode {

    /**
     * @return
     */
    public int getCode();

    /**
     * @return
     */
    public String getMsg();

    /**
     *
     * Lookup ApiErrorCode.
     *
     * @param e
     * @param code
     * @return
     */
    static <E extends Enum<E> & ErrorCode> E lookup(Class<E> e, int code) {

        // TODO orElse null?
        return Stream.of(e.getEnumConstants()).filter(x -> x.getCode() == code).findAny().orElse(null);

    }
}
