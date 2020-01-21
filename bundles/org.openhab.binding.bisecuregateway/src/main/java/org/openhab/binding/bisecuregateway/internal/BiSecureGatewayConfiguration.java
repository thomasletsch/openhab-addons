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

/**
 * The {@link BiSecureGatewayConfiguration} class contains fields mapping thing configuration parameters.
 *
 * @author Thomas Letsch - Initial contribution
 */
public class BiSecureGatewayConfiguration {

    /**
     * Username
     */
    public String userName;

    /**
     * Password
     */
    public String password;

    /**
     * Gateway IP Address. Not needed if the gateway is auto discovered!
     */
    public String gatewayAddress;
}
