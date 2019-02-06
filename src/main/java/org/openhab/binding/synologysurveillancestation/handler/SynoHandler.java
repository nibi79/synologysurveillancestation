/**
 * Copyright (c) 2010-2019 by the respective copyright holders.
 *
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 */
package org.openhab.binding.synologysurveillancestation.handler;

import java.util.concurrent.ScheduledExecutorService;

import org.openhab.binding.synologysurveillancestation.internal.webapi.SynoWebApiHandler;
import org.openhab.binding.synologysurveillancestation.internal.webapi.WebApiException;

/**
 * The {@link SynoHandler} is a generic handler
 *
 * @author Nils - Initial contribution
 * @author Pavion - Contribution
 */
public interface SynoHandler {

    public ScheduledExecutorService getScheduler();

    public SynoWebApiHandler getSynoWebApiHandler();

    public boolean reconnect(boolean forceLogout) throws WebApiException;
}
