package org.openhab.binding.synologysurveillancestation.handler;

import java.util.concurrent.ScheduledExecutorService;

import org.openhab.binding.synologysurveillancestation.internal.webapi.SynoWebApiHandler;

public interface SynoHandler {

    public ScheduledExecutorService getScheduler();

    public SynoWebApiHandler getSynoWebApiHandler();
}
