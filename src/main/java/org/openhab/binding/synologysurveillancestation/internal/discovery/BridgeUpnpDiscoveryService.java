/**
 * Copyright (c) 2010-2017 by the respective copyright holders.
 *
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 */
package org.openhab.binding.synologysurveillancestation.internal.discovery;

import java.net.URI;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

import org.eclipse.jdt.annotation.NonNullByDefault;
import org.eclipse.jdt.annotation.Nullable;
import org.eclipse.smarthome.config.discovery.DiscoveryResult;
import org.eclipse.smarthome.config.discovery.DiscoveryResultBuilder;
import org.eclipse.smarthome.config.discovery.upnp.UpnpDiscoveryParticipant;
import org.eclipse.smarthome.core.thing.ThingTypeUID;
import org.eclipse.smarthome.core.thing.ThingUID;
import org.jupnp.model.meta.DeviceDetails;
import org.jupnp.model.meta.ManufacturerDetails;
import org.jupnp.model.meta.ModelDetails;
import org.jupnp.model.meta.RemoteDevice;
import org.openhab.binding.synologysurveillancestation.SynologySurveillanceStationBindingConstants;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @author Pav
 *
 */
@NonNullByDefault
public class BridgeUpnpDiscoveryService implements UpnpDiscoveryParticipant {

    private final Logger logger = LoggerFactory.getLogger(BridgeUpnpDiscoveryService.class);

    @Override
    public Set<ThingTypeUID> getSupportedThingTypeUIDs() {
        return SynologySurveillanceStationBindingConstants.SUPPORTED_BRIDGE_TYPES;
    }

    @Override
    public @Nullable DiscoveryResult createResult(RemoteDevice device) {
        ThingUID uid = getThingUID(device);
        if (uid != null) {

            DeviceDetails details = device.getDetails();
            URI uri = details.getPresentationURI();

            Map<String, Object> properties = new HashMap<>();
            properties.put(SynologySurveillanceStationBindingConstants.PROTOCOL, uri.getScheme());
            properties.put(SynologySurveillanceStationBindingConstants.PORT, uri.getPort());
            properties.put(SynologySurveillanceStationBindingConstants.HOST, uri.getHost());

            DiscoveryResult result = DiscoveryResultBuilder.create(uid).withProperties(properties)
                    .withRepresentationProperty(details.getSerialNumber()).withLabel(details.getFriendlyName()).build();
            return result;
        }
        return null;
    }

    @Override
    public @Nullable ThingUID getThingUID(RemoteDevice device) {
        DeviceDetails details = device.getDetails();

        if (details != null) {
            ManufacturerDetails manufacturerDetails = details.getManufacturerDetails();
            ModelDetails modelDetails = details.getModelDetails();
            if (manufacturerDetails != null && modelDetails != null) {

                String description = modelDetails.getModelDescription();
                String vendor = manufacturerDetails.getManufacturer();
                if (vendor.startsWith("Synology") && description.contains("NAS")) {
                    return new ThingUID(SynologySurveillanceStationBindingConstants.THING_TYPE_STATION,
                            details.getSerialNumber());
                }
            }
        }
        return null;
    }

}
