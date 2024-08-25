/**
 * Copyright (c) 2010-2023 Contributors to the openHAB project
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

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import org.eclipse.jdt.annotation.NonNullByDefault;
import org.eclipse.jdt.annotation.Nullable;
import org.openhab.core.thing.Channel;
import org.openhab.core.thing.ChannelUID;
import org.openhab.core.thing.type.DynamicStateDescriptionProvider;
import org.openhab.core.types.StateDescription;
import org.openhab.core.types.StateDescriptionFragmentBuilder;
import org.openhab.core.types.StateOption;
import org.osgi.service.component.ComponentContext;
import org.osgi.service.component.annotations.Activate;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Deactivate;

/**
 * Dynamic channel state description provider.
 * Overrides the state description for the controls, which receive its configuration in the runtime.
 *
 * @author Nils - Initial contribution
 * @author Pavion - Contribution
 */
@NonNullByDefault
@Component(service = { DynamicStateDescriptionProvider.class, SynoDynamicStateDescriptionProvider.class })
public class SynoDynamicStateDescriptionProvider implements DynamicStateDescriptionProvider {

    private final Map<ChannelUID, List<StateOption>> channelOptionsMap = new ConcurrentHashMap<>();

    @Activate
    public void activate(ComponentContext context) {
    }

    public void setStateOptions(ChannelUID channelUID, List<StateOption> options) {
        channelOptionsMap.put(channelUID, options);
    }

    @Override
    public @Nullable StateDescription getStateDescription(Channel channel, @Nullable StateDescription original,
            @Nullable Locale locale) {
        List<StateOption> options = channelOptionsMap.getOrDefault(channel.getUID(), new ArrayList<StateOption>());

        StateDescriptionFragmentBuilder builder = (original == null) ? StateDescriptionFragmentBuilder.create()
                : StateDescriptionFragmentBuilder.create(original);
        return builder.withOptions(options).build().toStateDescription();
    }

    @Deactivate
    public void deactivate() {
        channelOptionsMap.clear();
    }
}
