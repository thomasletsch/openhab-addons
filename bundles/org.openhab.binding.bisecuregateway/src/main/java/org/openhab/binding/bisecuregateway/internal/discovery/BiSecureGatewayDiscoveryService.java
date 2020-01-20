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

import java.util.Set;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.TimeUnit;

import org.bisdk.sdk.Discovery;
import org.bisdk.sdk.DiscoveryData;
import org.eclipse.jdt.annotation.NonNullByDefault;
import org.eclipse.jdt.annotation.Nullable;
import org.eclipse.smarthome.config.discovery.AbstractDiscoveryService;
import org.eclipse.smarthome.config.discovery.DiscoveryResultBuilder;
import org.eclipse.smarthome.config.discovery.DiscoveryService;
import org.eclipse.smarthome.core.thing.ThingTypeUID;
import org.eclipse.smarthome.core.thing.ThingUID;
import org.openhab.binding.bisecuregateway.internal.BiSecureGatewayBindingConstants;
import org.openhab.binding.bisecuregateway.internal.BiSecureGatewayHandlerFactory;
import org.osgi.service.component.annotations.Component;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * The {@link BiSecureGatewayDiscoveryService} class discovers BiSecure Gateways and adds the results to the inbox.
 *
 * @author Thomas Letsch - Initial contribution
 */
@NonNullByDefault
@Component(service = DiscoveryService.class, immediate = true, configurationPid = "discovery.bisecuregateway")
public class BiSecureGatewayDiscoveryService extends AbstractDiscoveryService {

    private final Logger logger = LoggerFactory.getLogger(BiSecureGatewayDiscoveryService.class);

    private static final int TIMEOUT = 15;
    private boolean running;

    private @Nullable CompletableFuture<DiscoveryData> discoveryFuture;

    public BiSecureGatewayDiscoveryService() {
        super(BiSecureGatewayHandlerFactory.SUPPORTED_THING_TYPES_UIDS, TIMEOUT, true);
    }

    @Override
    public Set<ThingTypeUID> getSupportedThingTypes() {
        return BiSecureGatewayHandlerFactory.SUPPORTED_THING_TYPES_UIDS;
    }

    @Override
    public void startScan() {
        logger.debug("StartScan called");
        startDiscovery();
    }

    @Override
    protected void startBackgroundDiscovery() {
        logger.debug("Start BiSecure Gateway background discovery");
        if (discoveryFuture == null || discoveryFuture.isCancelled()) {
            startDiscovery();
        }
    }

    @Override
    protected void stopBackgroundDiscovery() {
        logger.debug("Stop BiSecure Gateway background discovery");
        stopDiscovery();
    }

    /**
     * Starts discovery for BiSecure gateways
     */
    private synchronized void startDiscovery() {
        if (running) {
            return;
        }

        try {
            Discovery discovery = new Discovery();
            discoveryFuture = discovery.startServer();
            running = true;
            scheduler.schedule(this::stopDiscovery, TIMEOUT, TimeUnit.SECONDS);
            discovery.sendDiscoveryRequest();
            DiscoveryData discoveryData = discoveryFuture.get(TIMEOUT, TimeUnit.SECONDS);
            String name = discoveryData.getMac().replace(":", "-");
            ThingUID uid = new ThingUID(BiSecureGatewayBindingConstants.GATEWAY_BRIDGE_TYPE, name);
            // @formatter:off
            thingDiscovered(DiscoveryResultBuilder.create(uid)
                    .withLabel("BiSecure Gateway")
                    .withProperty(PROPERTY_MAC, discoveryData.getMac())
                    .withProperty(PROPERTY_GATEWAY_ID, discoveryData.getGatewayId())
                    .withProperty(PROPERTY_SOURCE_ADDRESS, discoveryData.getSourceAddress().toString())
                    .withProperty(PROPERTY_SOFTWARE_VERSION, discoveryData.getSwVersion())
                    .withProperty(PROPERTY_HARDWARE_VERSION, discoveryData.getHwVersion())
                    .withProperty(PROPERTY_PROTOCOL, discoveryData.getProtocol())
                    .build());
         // @formatter:on
            running = false;
        } catch (Exception e) {
            logger.error("Could not start BiSecure Gateway discovery server ", e);
        }
    }

    /**
     * Stops discovery of BiSecure gateways
     */
    private synchronized void stopDiscovery() {
        if (discoveryFuture != null && !discoveryFuture.isCancelled()) {
            discoveryFuture.cancel(true);
        }
        running = false;
    }

}
