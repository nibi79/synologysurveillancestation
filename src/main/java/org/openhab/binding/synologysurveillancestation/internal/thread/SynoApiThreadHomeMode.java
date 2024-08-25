/**
 * Copyright (c) 2010-2024 Contributors to the openHAB project
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
package org.openhab.binding.synologysurveillancestation.internal.thread;

import static org.openhab.binding.synologysurveillancestation.SynoBindingConstants.CHANNEL_HOMEMODE;

import org.eclipse.jdt.annotation.NonNullByDefault;
import org.openhab.binding.synologysurveillancestation.handler.SynoBridgeHandler;
import org.openhab.binding.synologysurveillancestation.internal.webapi.WebApiException;
import org.openhab.binding.synologysurveillancestation.internal.webapi.error.WebApiAuthErrorCodes;
import org.openhab.binding.synologysurveillancestation.internal.webapi.response.HomeModeResponse;
import org.openhab.core.library.types.OnOffType;
import org.openhab.core.thing.Channel;

/**
 * Thread for getting Surveillance Station Home Mode state
 *
 * @author Pavion - Initial contribution
 */
@NonNullByDefault
public class SynoApiThreadHomeMode extends SynoApiThread<SynoBridgeHandler> {
    // private final Logger logger = LoggerFactory.getLogger(SynoApiThreadHomeMode.class);

    public SynoApiThreadHomeMode(SynoBridgeHandler handler, int refreshRate) {
        super(SynoApiThread.THREAD_HOMEMODE, handler, refreshRate);
    }

    @Override
    public boolean isNeeded() {
        return (getSynoHandler().isLinked(CHANNEL_HOMEMODE));
    }

    @Override
    public boolean refresh() throws Exception {
        SynoBridgeHandler bridgeHandler = getSynoHandler();
        HomeModeResponse response = bridgeHandler.getSynoWebApiHandler().getApiHomeMode().getHomeModeResponse();
        if (response.isSuccess()) {
            if (getSynoHandler().isLinked(CHANNEL_HOMEMODE)) {
                Channel channel = bridgeHandler.getThing().getChannel(CHANNEL_HOMEMODE);
                getSynoHandler().updateState(channel.getUID(), response.isHomeMode() ? OnOffType.ON : OnOffType.OFF);
            }
            return true;
        } else if (response.getErrorcode() == 119) {
            throw new WebApiException(WebApiAuthErrorCodes.INSUFFICIENT_USER_PRIVILEGE);
        } else {
            return false;
        }
    }
}
