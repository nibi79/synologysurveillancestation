package org.openhab.binding.synologysurveillancestation.internal.webapi.error;

import java.util.stream.Stream;

/**
 * Interface for ErrorCodes.
 *
 * @author Nils
 *
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
