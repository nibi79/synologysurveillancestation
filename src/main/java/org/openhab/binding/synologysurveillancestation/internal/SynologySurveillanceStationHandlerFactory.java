/**
 * Copyright (c) 2010-2017 by the respective copyright holders.
 *
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 */
package org.openhab.binding.synologysurveillancestation.internal;

import static org.openhab.binding.synologysurveillancestation.SynologySurveillanceStationBindingConstants.*;

import java.util.HashMap;
import java.util.Hashtable;
import java.util.Map;

import org.eclipse.jdt.annotation.NonNullByDefault;
import org.eclipse.jdt.annotation.Nullable;
import org.eclipse.smarthome.config.discovery.DiscoveryService;
import org.eclipse.smarthome.core.thing.Bridge;
import org.eclipse.smarthome.core.thing.Thing;
import org.eclipse.smarthome.core.thing.ThingTypeUID;
import org.eclipse.smarthome.core.thing.ThingUID;
import org.eclipse.smarthome.core.thing.binding.BaseThingHandlerFactory;
import org.eclipse.smarthome.core.thing.binding.ThingHandler;
import org.eclipse.smarthome.core.thing.binding.ThingHandlerFactory;
import org.openhab.binding.synologysurveillancestation.handler.SynologySurveillanceStationBridgeHandler;
import org.openhab.binding.synologysurveillancestation.handler.SynologySurveillanceStationHandler;
import org.openhab.binding.synologysurveillancestation.internal.discovery.CameraDiscoveryService;
import org.osgi.framework.ServiceRegistration;
import org.osgi.service.component.ComponentContext;
import org.osgi.service.component.annotations.Component;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * The {@link SynologySurveillanceStationHandlerFactory} is responsible for creating things and thing
 * handlers.
 *
 * @author Nils - Initial contribution
 */
@Component(service = ThingHandlerFactory.class, immediate = true, configurationPid = "binding.synologysurveillancestation")
@NonNullByDefault
public class SynologySurveillanceStationHandlerFactory extends BaseThingHandlerFactory {

    private Map<ThingUID, ServiceRegistration<?>> discoveryServiceRegs = new HashMap<>();

    private final Logger logger = LoggerFactory.getLogger(SynologySurveillanceStationHandlerFactory.class);

    @Override
    protected void activate(ComponentContext componentContext) {
        super.activate(componentContext);
    }

    @Override
    protected void deactivate(ComponentContext componentContext) {
        super.deactivate(componentContext);
    }

    @Override
    public boolean supportsThingType(ThingTypeUID thingTypeUID) {
        return SUPPORTED_THING_TYPES.contains(thingTypeUID);
    }

    @Override
    protected @Nullable ThingHandler createHandler(Thing thing) {
        ThingTypeUID thingTypeUID = thing.getThingTypeUID();

        if (thingTypeUID.equals(THING_TYPE_STATION)) {
            SynologySurveillanceStationBridgeHandler bridgeHandler = new SynologySurveillanceStationBridgeHandler(
                    (Bridge) thing);
            CameraDiscoveryService discoveryService = new CameraDiscoveryService(bridgeHandler);
            bridgeHandler.setDiscovery(discoveryService);
            this.discoveryServiceRegs.put(thing.getUID(), bundleContext.registerService(
                    DiscoveryService.class.getName(), discoveryService, new Hashtable<String, Object>()));

            return bridgeHandler;
        } else if (thingTypeUID.equals(THING_TYPE_CAMERA)) {
            return new SynologySurveillanceStationHandler(thing);
        }
        if (thingTypeUID.equals(THING_TYPE_CAMERA_PTZ)) {
            return new SynologySurveillanceStationHandler(thing);
        }

        return null;
    }

    @Override
    protected void removeHandler(ThingHandler handler) {
        if (handler.getThing().getThingTypeUID().equals(THING_TYPE_STATION)) {
            ServiceRegistration<?> serviceReg = this.discoveryServiceRegs.get(handler.getThing().getUID());
            if (serviceReg != null) {
                serviceReg.unregister();
                discoveryServiceRegs.remove(handler.getThing().getUID());
            }
        }
        super.removeHandler(handler);
    }

}
