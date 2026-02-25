/*
 * Copyright (c) 2010-2026 Contributors to the openHAB project
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
package org.openhab.binding.synologysurveillancestation.internal;

import static org.openhab.binding.synologysurveillancestation.SynoBindingConstants.*;

import java.util.HashMap;
import java.util.Hashtable;
import java.util.Map;

import org.eclipse.jdt.annotation.Nullable;
import org.eclipse.jetty.client.HttpClient;
import org.eclipse.jetty.util.ssl.SslContextFactory;
import org.openhab.binding.synologysurveillancestation.handler.SynoBridgeHandler;
import org.openhab.binding.synologysurveillancestation.handler.SynoCameraHandler;
import org.openhab.binding.synologysurveillancestation.internal.discovery.CameraDiscoveryService;
import org.openhab.binding.synologysurveillancestation.internal.discovery.SynoDynamicStateDescriptionProvider;
import org.openhab.core.config.core.Configuration;
import org.openhab.core.config.discovery.DiscoveryService;
import org.openhab.core.io.net.http.HttpClientFactory;
import org.openhab.core.thing.Bridge;
import org.openhab.core.thing.Thing;
import org.openhab.core.thing.ThingTypeUID;
import org.openhab.core.thing.ThingUID;
import org.openhab.core.thing.binding.BaseThingHandlerFactory;
import org.openhab.core.thing.binding.ThingHandler;
import org.openhab.core.thing.binding.ThingHandlerFactory;
import org.osgi.framework.ServiceRegistration;
import org.osgi.service.component.ComponentContext;
import org.osgi.service.component.annotations.Activate;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Deactivate;
import org.osgi.service.component.annotations.Reference;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * The {@link SynoHandlerFactory} is responsible for creating things and thing
 * handlers.
 *
 * @author Nils - Initial contribution
 * @author Pavion - Contribution
 */
@Component(service = ThingHandlerFactory.class, configurationPid = "binding.synologysurveillancestation")
// @NonNullByDefault
public class SynoHandlerFactory extends BaseThingHandlerFactory {

    private final Logger logger = LoggerFactory.getLogger(SynoHandlerFactory.class);
    private Map<ThingUID, ServiceRegistration<?>> discoveryServiceRegs = new HashMap<>();
    private HttpClient httpClient;
    private boolean acceptSsl = false;
    private SynoBridgeHandler bridgeHandler = null;

    private SynoDynamicStateDescriptionProvider stateDescriptionProvider;

    @Reference
    protected void setHttpClientFactory(HttpClientFactory httpClientFactory) {
        this.httpClient = httpClientFactory.getCommonHttpClient();
    }

    protected void unsetHttpClientFactory(HttpClientFactory httpClientFactory) {
        if (this.acceptSsl) {
            try {
                this.httpClient.stop();
            } catch (Exception e) {
                logger.error("Couldn't stop trusting HttpServer, sorry");
            }
        }
        this.httpClient = null;
    }

    @Override
    @Activate
    protected void activate(ComponentContext componentContext) {
        super.activate(componentContext);
    }

    @Override
    @Deactivate
    protected void deactivate(ComponentContext componentContext) {
        super.deactivate(componentContext);
    }

    @Override
    public boolean supportsThingType(ThingTypeUID thingTypeUID) {
        return SUPPORTED_THING_TYPES.contains(thingTypeUID);
    }

    @Override
    public @Nullable Thing createThing(ThingTypeUID thingTypeUID, Configuration configuration,
            @Nullable ThingUID thingUID, @Nullable ThingUID bridgeUID) {
        if (SUPPORTED_BRIDGE_TYPES.contains(thingTypeUID)) {
            return super.createThing(thingTypeUID, configuration, thingUID, null);
        } else if (SUPPORTED_THING_TYPES.contains(thingTypeUID)) {
            ThingUID myUID = thingUID;
            if (myUID == null) {
                myUID = new ThingUID(thingTypeUID, "camera");
            }
            return super.createThing(thingTypeUID, configuration, myUID, bridgeUID);
        }
        throw new IllegalArgumentException("The thing type " + thingTypeUID + " is not supported by this binding.");
    }

    @Override
    protected @Nullable ThingHandler createHandler(Thing thing) {
        ThingTypeUID thingTypeUID = thing.getThingTypeUID();

        if (thingTypeUID.equals(THING_TYPE_STATION)) {
            SynoConfig config = thing.getConfiguration().as(SynoConfig.class);
            if (config.isAcceptSsl()) {
                SslContextFactory sslContextFactory = new SslContextFactory(true);
                sslContextFactory.setTrustAll(true);
                sslContextFactory.setEndpointIdentificationAlgorithm(null);
                HttpClient client = new HttpClient(sslContextFactory);
                try {
                    client.start();
                    this.httpClient = client;
                    logger.debug("Trusting HttpServer started");
                    this.acceptSsl = true;
                } catch (Exception e) {
                    logger.error("Trusting HttpServer failed");
                    this.acceptSsl = false;
                }
            }
            bridgeHandler = new SynoBridgeHandler((Bridge) thing, httpClient);
            CameraDiscoveryService discoveryService = new CameraDiscoveryService(bridgeHandler);
            bridgeHandler.setDiscovery(discoveryService);
            this.discoveryServiceRegs.put(thing.getUID(), bundleContext.registerService(
                    DiscoveryService.class.getName(), discoveryService, new Hashtable<String, Object>()));

            return bridgeHandler;

        } else if (thingTypeUID.equals(THING_TYPE_CAMERA) && bridgeHandler != null) {
            return new SynoCameraHandler(thing, stateDescriptionProvider);
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
                bridgeHandler = null;
            }
        }
        super.removeHandler(handler);
    }

    @Reference
    protected void setDynamicStateDescriptionProvider(SynoDynamicStateDescriptionProvider stateDescriptionProvider) {
        this.stateDescriptionProvider = stateDescriptionProvider;
    }

    protected void unsetDynamicStateDescriptionProvider(SynoDynamicStateDescriptionProvider stateDescriptionProvider) {
        this.stateDescriptionProvider = null;
    }
}
