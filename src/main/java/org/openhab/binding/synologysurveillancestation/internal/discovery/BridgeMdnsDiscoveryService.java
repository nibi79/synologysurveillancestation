/**
 * Copyright (c) 2010-2018 by the respective copyright holders.
 *
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 */
package org.openhab.binding.synologysurveillancestation.internal.discovery;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;

import javax.jmdns.ServiceInfo;

import org.eclipse.smarthome.config.discovery.DiscoveryResult;
import org.eclipse.smarthome.config.discovery.DiscoveryResultBuilder;
import org.eclipse.smarthome.config.discovery.mdns.MDNSDiscoveryParticipant;
import org.eclipse.smarthome.core.thing.ThingTypeUID;
import org.eclipse.smarthome.core.thing.ThingUID;
import org.openhab.binding.synologysurveillancestation.SynoBindingConstants;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * The {@link BridgeMdnsDiscoveryService} is a class for discovering the DiskStation via mDNS service
 *
 * @author Pavion
 */
public class BridgeMdnsDiscoveryService implements MDNSDiscoveryParticipant {

    private final Logger logger = LoggerFactory.getLogger(BridgeMdnsDiscoveryService.class);

    @Override
    public Set<ThingTypeUID> getSupportedThingTypeUIDs() {
        return SynoBindingConstants.SUPPORTED_BRIDGE_TYPES;
    }

    @Override
    public String getServiceType() {
        return "_http._tcp.local.";
    }

    @Override
    public DiscoveryResult createResult(ServiceInfo service) {
        ThingUID uid = getThingUID(service);
        if (uid != null) {
            if (service.getHostAddresses() != null && service.getHostAddresses().length > 0
                    && !service.getHostAddresses()[0].isEmpty()) {

                String name = service.getName();
                String ip = service.getHostAddresses()[0];
                String model = service.getPropertyString("model");
                String serial = service.getPropertyString("serial");
                String port = service.getPropertyString("admin_port");

                if (name != null && ip != null && model != null && serial != null && port != null) {
                    String label = String.format("%s (%s)", name, model);
                    Map<String, Object> properties = new HashMap<>();
                    properties.put(SynoBindingConstants.PROTOCOL, "http");
                    properties.put(SynoBindingConstants.PORT, port);
                    properties.put(SynoBindingConstants.HOST, ip);

                    DiscoveryResult result = DiscoveryResultBuilder.create(uid).withProperties(properties)
                            .withRepresentationProperty(serial.toLowerCase()).withLabel(label).build();
                    return result;
                }
            }
        }
        return null;
    }

    @Override
    public ThingUID getThingUID(ServiceInfo service) {
        String vendor = service.getPropertyString("vendor");
        String serial = service.getPropertyString("serial");
        if (vendor != null && serial != null) {
            if (vendor.startsWith("Synology")) {
                return new ThingUID(SynoBindingConstants.THING_TYPE_STATION, serial.toLowerCase());
            }

        }
        return null;
    }

}
