package org.openhab.binding.bisecuregateway.internal;

import org.bisdk.sdk.LoggerAdapter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class BiSdkLoggerAdapter implements LoggerAdapter {

    private final Logger logger = LoggerFactory.getLogger(BiSdkLoggerAdapter.class);

    static public void initLogger() {
        org.bisdk.sdk.Logger.Companion.setLoggerAdapter(new BiSdkLoggerAdapter());
    }

    @Override
    public void debug(String msg) {
        logger.debug(msg);
    }

    @Override
    public void info(String msg) {
        logger.info(msg);
    }

    @Override
    public void warn(String msg) {
        logger.warn(msg);
    }

}
