/**
 * Copyright (c) 2019-2019 Contributors to the openHAB project
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

import org.eclipse.jdt.annotation.NonNullByDefault;
import org.eclipse.smarthome.core.thing.ThingTypeUID;

/**
 * The {@link BiSecureGatewayBindingConstants} class defines common constants, which are
 * used across the whole binding.
 *
 * @author Thomas Letsch - Initial contribution
 */
@NonNullByDefault
public class BiSecureGatewayBindingConstants {

    private static final String BINDING_ID = "bisecuregateway";

    // List of all Thing Type UIDs
    public static final ThingTypeUID GATEWAY_BRIDGE_TYPE = new ThingTypeUID(BINDING_ID, "bisecuregateway");
    public static final ThingTypeUID GROUP_THING_TYPE = new ThingTypeUID(BINDING_ID, "bisecuregroup");
    public static final ThingTypeUID PORT_THING_TYPE = new ThingTypeUID(BINDING_ID, "bisecureport");

    // List of all Channel ids
}
