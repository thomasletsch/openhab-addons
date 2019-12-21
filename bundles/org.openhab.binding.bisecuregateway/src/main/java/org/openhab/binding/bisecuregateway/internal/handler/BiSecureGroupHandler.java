/**
 * Copyright (c) 2010-2019 Contributors to the openHAB project
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
package org.openhab.binding.bisecuregateway.internal.handler;

import static org.openhab.binding.bisecuregateway.internal.BiSecureGatewayBindingConstants.*;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.bisdk.sdk.ClientAPI;
import org.bisdk.sdk.Group;
import org.bisdk.sdk.Port;
import org.bisdk.sdk.PortType;
import org.bisdk.sdk.Transition;
import org.eclipse.jdt.annotation.NonNullByDefault;
import org.eclipse.jdt.annotation.Nullable;
import org.eclipse.smarthome.core.library.types.OpenClosedType;
import org.eclipse.smarthome.core.library.types.UpDownType;
import org.eclipse.smarthome.core.thing.Bridge;
import org.eclipse.smarthome.core.thing.Channel;
import org.eclipse.smarthome.core.thing.ChannelUID;
import org.eclipse.smarthome.core.thing.Thing;
import org.eclipse.smarthome.core.thing.ThingStatus;
import org.eclipse.smarthome.core.thing.ThingStatusDetail;
import org.eclipse.smarthome.core.thing.ThingStatusInfo;
import org.eclipse.smarthome.core.thing.ThingTypeUID;
import org.eclipse.smarthome.core.thing.binding.BaseThingHandler;
import org.eclipse.smarthome.core.thing.binding.BridgeHandler;
import org.eclipse.smarthome.core.thing.binding.builder.ChannelBuilder;
import org.eclipse.smarthome.core.thing.binding.builder.ThingBuilder;
import org.eclipse.smarthome.core.thing.type.ChannelTypeUID;
import org.eclipse.smarthome.core.types.Command;
import org.eclipse.smarthome.core.types.RefreshType;
import org.eclipse.smarthome.core.types.UnDefType;
import org.openhab.binding.bisecuregateway.internal.BiSecureGatewayBindingConstants;
import org.openhab.binding.bisecuregateway.internal.BiSecureGatewayHandlerFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * The {@link BiSecureGroupHandler} is responsible for handling commands for BiSecure Groups (Devices), which are
 * sent to one of the channels.
 *
 * @author Thomas Letsch - Initial contribution
 */
@NonNullByDefault
public class BiSecureGroupHandler extends BaseThingHandler {

    private final Logger logger = LoggerFactory.getLogger(BiSecureGroupHandler.class);

    public static final Set<ThingTypeUID> SUPPORTED_THING_TYPES_UIDS = Collections.singleton(GROUP_THING_TYPE);

    private BiSecureGatewayHandlerFactory factory;

    private Map<ChannelUID, Port> ports = new HashMap<ChannelUID, Port>();

    public BiSecureGroupHandler(Thing thing, BiSecureGatewayHandlerFactory factory) {
        super(thing);
        this.factory = factory;
    }

    protected @Nullable BiSecureGatewayHandler getBiSecureGatewayHandler() {
        Bridge bridge = getBridge();
        return bridge != null ? (BiSecureGatewayHandler) bridge.getHandler() : null;
    }

    @Override
    public void initialize() {
        ClientAPI clientAPI = getClientAPI();
        if (clientAPI == null) {
            logger.debug("ClientAPI not yet ready, cannot initialize");
            updateStatus(ThingStatus.OFFLINE, ThingStatusDetail.CONFIGURATION_ERROR,
                    "ClientAPI not yet ready, cannot initialize");
            return;
        }
        String groupId = getThing().getProperties().get(PROPERTY_ID);
        List<Group> groups = clientAPI.getGroups();
        Group group = null;
        for (Group toBeChecked : groups) {
            if (toBeChecked.getId() == Integer.valueOf(groupId)) {
                group = toBeChecked;
            }
        }
        List<Channel> channels = new ArrayList<Channel>();
        group.getPorts().forEach(port -> {
            String portType = PortType.Companion.from(port.getType()).name();
            ChannelTypeUID channelTypeUID = new ChannelTypeUID(getThing().getUID().getAsString() + ":" + portType);
            ChannelUID channelUID = new ChannelUID(getThing().getUID(), PortType.Companion.from(port.getType()).name());
            Channel channel = ChannelBuilder.create(channelUID, BiSecureGatewayBindingConstants.ITEM_TYPE_ROLLERSHUTTER)
                    .withType(channelTypeUID).build();
            channels.add(channel);
            ports.put(channelUID, port);
        });
        ThingBuilder thingBuilder = editThing();
        thingBuilder.withChannels(channels);
        updateThing(thingBuilder.build());
        updateStatus(ThingStatus.ONLINE);
        for (Channel channel : channels) {
            updateChannelState(clientAPI, channel.getUID());
        }
    }

    private void updateChannelState(ClientAPI clientAPI, ChannelUID channelUID) {
        Transition transition = clientAPI.getTransition(ports.get(channelUID));
        if (transition.getHcp().getPositionOpen()) {
            updateState(channelUID, OpenClosedType.OPEN);
        } else if (transition.getHcp().getPositionClose()) {
            updateState(channelUID, OpenClosedType.CLOSED);
        } else {
            logger.info("Could no determine OpenClosedType: " + transition);
        }
    }

    private @Nullable ClientAPI getClientAPI() {
        BridgeHandler bridgeHandler = getBridge().getHandler();
        ClientAPI clientAPI = ((BiSecureGatewayHandler) bridgeHandler).getClientAPI();
        return clientAPI;
    }

    @Override
    public void handleCommand(ChannelUID channelUID, Command command) {
        logger.info("Handling command '{}' for {}", command, channelUID);
        ClientAPI clientAPI = getClientAPI();
        if (clientAPI == null) {
            logger.warn("ClientAPI not yet ready, cannot handle command");
            return;
        }
        if (command instanceof RefreshType) {
            updateChannelState(clientAPI, channelUID);
            return;
        }

        if (getThing().getStatus() != ThingStatus.ONLINE) {
            logger.debug("Gateway is offline, ignoring command {} for channel {}", command, channelUID);
            return;
        }

        if (command instanceof UpDownType) {
            UpDownType upDownType = (UpDownType) command;
            if (upDownType == UpDownType.DOWN) {
                clientAPI.setState(ports.get(channelUID));
            }
        }

        // may need to ask the list if this can be set here?
        updateState(channelUID, UnDefType.UNDEF);

        logger.warn("Command '{}' is not a String type for channel {}", command, channelUID);
        return;
    }

    @Override
    public void bridgeStatusChanged(ThingStatusInfo bridgeStatusInfo) {
    }

    @Override
    public void dispose() {

    }
}
