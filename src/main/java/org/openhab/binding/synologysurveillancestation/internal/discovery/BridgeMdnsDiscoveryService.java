/**
 * Copyright (c) 2010-2020 Contributors to the openHAB project
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
package org.openhab.binding.synologysurveillancestation.internal.discovery;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;

import javax.jmdns.ServiceInfo;

import org.eclipse.jdt.annotation.NonNullByDefault;
import org.eclipse.jdt.annotation.Nullable;
import org.openhab.binding.synologysurveillancestation.SynoBindingConstants;
import org.openhab.core.config.discovery.DiscoveryResult;
import org.openhab.core.config.discovery.DiscoveryResultBuilder;
import org.openhab.core.config.discovery.mdns.MDNSDiscoveryParticipant;
import org.openhab.core.thing.ThingTypeUID;
import org.openhab.core.thing.ThingUID;
import org.osgi.service.component.ComponentContext;
import org.osgi.service.component.annotations.Activate;
import org.osgi.service.component.annotations.Component;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * The {@link BridgeMdnsDiscoveryService} is a class for discovering the DiskStation via mDNS service
 *
 * @author Pavion - Initial contribution
 */
@Component(service = MDNSDiscoveryParticipant.class, immediate = false, configurationPid = "binding.synologysurveillancestation")
@NonNullByDefault
public class BridgeMdnsDiscoveryService implements MDNSDiscoveryParticipant {

    private final Logger logger = LoggerFactory.getLogger(BridgeMdnsDiscoveryService.class);

    @Activate
    public void activate(ComponentContext context) {
    }

    @Override
    public Set<ThingTypeUID> getSupportedThingTypeUIDs() {
        return SynoBindingConstants.SUPPORTED_BRIDGE_TYPES;
    }

    @Override
    public String getServiceType() {
        return "_http._tcp.local.";
    }

    @Override
    @Nullable
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
                    properties.put(SynoBindingConstants.ACCEPT_SSL, false);
                    properties.put(SynoBindingConstants.PROTOCOL, "http");
                    properties.put(SynoBindingConstants.PORT, port);
                    properties.put(SynoBindingConstants.HOST, ip);
                    // properties.put(SynoBindingConstants.USER_NAME, "");
                    // properties.put(SynoBindingConstants.PASSWORD, "");
                    properties.put(SynoBindingConstants.SERIAL, serial.toLowerCase());

                    DiscoveryResult result = DiscoveryResultBuilder.create(uid).withProperties(properties)
                            .withRepresentationProperty(SynoBindingConstants.SERIAL).withLabel(label).build();
                    return result;
                }
            }
        }
        return null;
    }

    @Override
    @Nullable
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
