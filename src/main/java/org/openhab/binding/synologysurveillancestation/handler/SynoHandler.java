/**
 * Copyright (c) 2010-2018 by the respective copyright holders.
 *
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 */
package org.openhab.binding.synologysurveillancestation.handler;

import java.util.concurrent.ScheduledExecutorService;

import org.openhab.binding.synologysurveillancestation.internal.webapi.SynoWebApiHandler;

/**
 * The {@link SynoHandler} is a generic handler
 *
 * @author Nils
 */
public interface SynoHandler {

    public ScheduledExecutorService getScheduler();

    public SynoWebApiHandler getSynoWebApiHandler();
}
