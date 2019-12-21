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

import java.util.Arrays;
import java.util.HashSet;
import java.util.Hashtable;
import java.util.Set;

import org.eclipse.jdt.annotation.NonNullByDefault;
import org.eclipse.jdt.annotation.Nullable;
import org.eclipse.smarthome.config.discovery.DiscoveryService;
import org.eclipse.smarthome.core.thing.Thing;
import org.eclipse.smarthome.core.thing.ThingTypeUID;
import org.eclipse.smarthome.core.thing.binding.BaseThingHandlerFactory;
import org.eclipse.smarthome.core.thing.binding.ThingHandler;
import org.eclipse.smarthome.core.thing.binding.ThingHandlerFactory;
import org.openhab.binding.bisecuregateway.internal.discovery.BiSecureDeviceDiscoveryService;
import org.openhab.binding.bisecuregateway.internal.handler.BiSecureGatewayHandler;
import org.openhab.binding.bisecuregateway.internal.handler.BiSecureGroupHandler;
import org.osgi.service.component.annotations.Component;

/**
 * The {@link BiSecureGatewayHandlerFactory} is responsible for creating things and thing
 * handlers.
 *
 * @author Thomas Letsch - Initial contribution
 */
@NonNullByDefault
@Component(configurationPid = "binding.bisecuregateway", service = ThingHandlerFactory.class)
public class BiSecureGatewayHandlerFactory extends BaseThingHandlerFactory {

    public static final Set<ThingTypeUID> SUPPORTED_THING_TYPES_UIDS = new HashSet<ThingTypeUID>(
            Arrays.asList(GATEWAY_BRIDGE_TYPE, GROUP_THING_TYPE, PORT_THING_TYPE));

    @Override
    public boolean supportsThingType(ThingTypeUID thingTypeUID) {
        return SUPPORTED_THING_TYPES_UIDS.contains(thingTypeUID);
    }

    @Override
    protected @Nullable ThingHandler createHandler(Thing thing) {
        ThingTypeUID thingTypeUID = thing.getThingTypeUID();

        if (GATEWAY_BRIDGE_TYPE.equals(thingTypeUID)) {
            BiSecureGatewayHandler biSecureGatewayHandler = new BiSecureGatewayHandler(thing);
            BiSecureDeviceDiscoveryService discoveryService = new BiSecureDeviceDiscoveryService(
                    biSecureGatewayHandler);
            bundleContext.registerService(DiscoveryService.class.getName(), discoveryService, new Hashtable<>());
            return biSecureGatewayHandler;
        }
        if (GROUP_THING_TYPE.equals(thingTypeUID)) {
            BiSecureGroupHandler groupHandler = new BiSecureGroupHandler(thing, this);
            return groupHandler;
        }
        return null;
    }
}
