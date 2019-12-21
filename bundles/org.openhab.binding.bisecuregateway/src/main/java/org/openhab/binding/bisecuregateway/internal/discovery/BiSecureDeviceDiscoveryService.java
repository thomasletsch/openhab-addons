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
package org.openhab.binding.bisecuregateway.internal.discovery;

import static org.openhab.binding.bisecuregateway.internal.BiSecureGatewayBindingConstants.*;

import java.util.List;

import org.bisdk.sdk.Group;
import org.eclipse.jdt.annotation.NonNullByDefault;
import org.eclipse.smarthome.config.discovery.AbstractDiscoveryService;
import org.eclipse.smarthome.config.discovery.DiscoveryResultBuilder;
import org.eclipse.smarthome.core.thing.ThingStatus;
import org.eclipse.smarthome.core.thing.ThingUID;
import org.openhab.binding.bisecuregateway.internal.BiSecureGatewayHandlerFactory;
import org.openhab.binding.bisecuregateway.internal.handler.BiSecureGatewayHandler;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * The {@link BiSecureDeviceDiscoveryService} class discovers BiSecure Devices connected to a BiSecure Gateway
 *
 * @author Thomas Letsch - Initial contribution
 */
@NonNullByDefault
public class BiSecureDeviceDiscoveryService extends AbstractDiscoveryService {

    private static final int TIMEOUT = 5;

    private final Logger logger = LoggerFactory.getLogger(BiSecureDeviceDiscoveryService.class);
    private final BiSecureGatewayHandler bridge;

    public BiSecureDeviceDiscoveryService(BiSecureGatewayHandler bridge) {
        super(BiSecureGatewayHandlerFactory.SUPPORTED_THING_TYPES_UIDS, TIMEOUT, true);
        logger.debug("BiSecureDeviceDiscoveryService {}", bridge);
        this.bridge = bridge;
    }

    @Override
    protected void startScan() {
        discoverDevices();
    }

    @Override
    protected void startBackgroundDiscovery() {
        discoverDevices();
    }

    @Override
    protected void deactivate() {
        super.deactivate();
    }

    /**
     * Discovers devices connected to a hub
     */
    private void discoverDevices() {
        if (bridge.getThing().getStatus() != ThingStatus.ONLINE) {
            logger.debug("Harmony Hub not online, scanning postponed");
            return;
        }
        if (bridge.getClientAPI() == null) {
            logger.debug("ClientAPI not yet ready, scanning postponed");
            return;
        }
        logger.debug("getting devices on {}", bridge.getThing().getUID().getId());
        List<Group> groups = bridge.getClientAPI().getGroups();
        groups.forEach(group -> {
            addDiscoveryResults(group);
        });
    }

    private void addDiscoveryResults(Group group) {
        String name = group.getName();
        int id = group.getId();
        ThingUID bridgeUID = bridge.getThing().getUID();
        ThingUID thingUID = new ThingUID(GROUP_THING_TYPE, bridgeUID, id + "");
        // @formatter:off
        thingDiscovered(DiscoveryResultBuilder.create(thingUID)
                .withLabel(name)
                .withBridge(bridgeUID)
                .withProperty(PROPERTY_ID, id + "")
                .withProperty(PROPERTY_NAME, name)
                .build());
         // @formatter:on
    }
}
