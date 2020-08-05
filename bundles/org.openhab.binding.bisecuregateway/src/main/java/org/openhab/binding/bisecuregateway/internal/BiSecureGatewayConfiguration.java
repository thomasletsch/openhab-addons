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
     * Standard delay between two get transition calls (=state updates)
     */
    private static int DEFAULT_POLLING_INTERVAL_SECONDS = 30;

    /**
     * Intensive delay between two get transition calls (=state updates). Used when door is opening / closing
     */
    private static int DEFAULT_ACTIVE_POLLING_INTERVAL_SECONDS = 5;

    private static int DEFAULT_ACTIVE_POLLING_TIMEOUT_SECONDS = 300;

    private static boolean DEFAULT_ACTIVE_POLLING_DURING_OPENED = false;

    private static int DEFAULT_READ_TIMEOUT_MILLI_SECONDS = 5000;

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

    /**
     * Gateway ID. Not needed if the gateway is auto discovered!
     * The gateway id is the MAC address of the gateway without ":" separation chars
     */
    public String gatewayId;

    /**
     * This is the timeout in ms for reading answer from the gateway.
     * The default is 2000ms (2s) which should be ok for most installations. Not required.
     */
    public Integer readTimeout;

    /**
     * This is the interval in seconds for polling the status of the bisecure device.
     * Each time the bisecure gateway will send a wireless request to the device to query the state.
     * The default is 30s. Not required.
     */
    public Integer pollingInterval;

    /**
     * This is the interval in seconds for polling the status of the bisecure device while it is working (open or
     * closing).
     * Each time the bisecure gateway will send a wireless request to the device to query the state
     * The default is 5s. Not required.
     */
    public Integer activePollingInterval;

    /**
     * This is the time in seconds after which the active polling (see activePollingInterval) is stopped.
     * Should be more how long your door needs to open or close.
     * The default is 300s. Not required.
     */
    public Integer activePollingTimeout;

    /**
     * Should we stay in active polling mode while the door is open?
     * The default is false. Not required.
     */
    public Boolean activePollingDuringOpened;

    public Integer getReadTimeout() {
        return readTimeout == null ? DEFAULT_READ_TIMEOUT_MILLI_SECONDS : readTimeout;
    }

    public Integer getPollingInterval() {
        return pollingInterval == null ? DEFAULT_POLLING_INTERVAL_SECONDS : pollingInterval;
    }

    public Integer getActivePollingInterval() {
        return activePollingInterval == null ? DEFAULT_ACTIVE_POLLING_INTERVAL_SECONDS : activePollingInterval;
    }

    public Integer getActivePollingTimeout() {
        return activePollingTimeout == null ? DEFAULT_ACTIVE_POLLING_TIMEOUT_SECONDS : activePollingTimeout;
    }

    public Boolean getActivePollingDuringOpened() {
        return activePollingDuringOpened == null ? DEFAULT_ACTIVE_POLLING_DURING_OPENED : activePollingDuringOpened;
    }
}
