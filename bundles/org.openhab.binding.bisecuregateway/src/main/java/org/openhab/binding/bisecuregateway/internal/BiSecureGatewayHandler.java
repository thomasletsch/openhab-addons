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
package org.openhab.binding.bisecuregateway.internal;

import static org.openhab.binding.bisecuregateway.internal.BiSecureGatewayBindingConstants.*;

import java.net.InetAddress;
import java.net.UnknownHostException;

import org.bisdk.sdk.Client;
import org.bisdk.sdk.ClientAPI;
import org.eclipse.jdt.annotation.NonNullByDefault;
import org.eclipse.jdt.annotation.Nullable;
import org.eclipse.smarthome.core.thing.ChannelUID;
import org.eclipse.smarthome.core.thing.Thing;
import org.eclipse.smarthome.core.thing.ThingStatus;
import org.eclipse.smarthome.core.thing.ThingStatusDetail;
import org.eclipse.smarthome.core.thing.binding.BaseThingHandler;
import org.eclipse.smarthome.core.thing.binding.BridgeHandler;
import org.eclipse.smarthome.core.thing.binding.ThingHandler;
import org.eclipse.smarthome.core.types.Command;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * The {@link BiSecureGatewayHandler} is responsible for handling commands, which are
 * sent to one of the channels.
 *
 * @author Thomas Letsch - Initial contribution
 */
@NonNullByDefault
public class BiSecureGatewayHandler extends BaseThingHandler implements BridgeHandler {

    private static final String DEFAULT_TOKEN = "00000000";

    private static final int DEFAULT_PORT = 4000;

    private final Logger logger = LoggerFactory.getLogger(BiSecureGatewayHandler.class);

    private @Nullable BiSecureGatewayConfiguration config;
    private @Nullable ClientAPI clientAPI;

    public BiSecureGatewayHandler(Thing thing) {
        super(thing);
    }

    @Override
    public void handleCommand(ChannelUID channelUID, Command command) {
        logger.warn("Handle command not implemented!");
    }

    @Override
    public void initialize() {
        logger.debug("Start initializing!");
        config = getConfigAs(BiSecureGatewayConfiguration.class);
        updateStatus(ThingStatus.UNKNOWN, ThingStatusDetail.BRIDGE_UNINITIALIZED, "Connecting to gateway");

        Client client;
        try {
            InetAddress inetAddress = InetAddress
                    .getByName(thing.getProperties().get(PROPERTY_SOURCE_ADDRESS).replace("/", ""));
            client = new Client(inetAddress, "000000000000", thing.getProperties().get(PROPERTY_GATEWAY_ID),
                    DEFAULT_TOKEN, DEFAULT_PORT);
            clientAPI = new ClientAPI(client);
        } catch (UnknownHostException e) {
            updateStatus(ThingStatus.OFFLINE, ThingStatusDetail.CONFIGURATION_ERROR, e.getMessage());
            return;
        }
        thing.setProperty(PROPERTY_NAME, clientAPI.getName());
        logger.info("Logging in with username " + config.userName);
        clientAPI.login(config.userName, config.password);
        updateStatus(ThingStatus.ONLINE);
    }

    public @Nullable ClientAPI getClientAPI() {
        return clientAPI;
    }

    @Override
    public void childHandlerInitialized(ThingHandler childHandler, Thing childThing) {
        logger.warn("childHandlerInitialized not implemented!");
    }

    @Override
    public void childHandlerDisposed(ThingHandler childHandler, Thing childThing) {
        logger.warn("childHandlerDisposed not implemented!");
    }
}
