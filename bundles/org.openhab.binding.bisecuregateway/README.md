# bisecuregateway Binding

This binding is designed to manage garage doors from Hörmann which are connected to a BiSecure Gateway.
With this binding the BiSecure gateway and all doors (and other devices) which are configured in the BiSecure Mobile App are automatically discovered. You can get the current position of the garage door and you can open or close it. It should work like any other roller shutter.

We currently do not support any teach in of other devices into the gateway.

## Supported Things

### BiSecure Gateway

This is the basic connection to the BiSecure gateway. You can set the username and password for the user to be used in the configuration of this thing.

Be aware that you should use a different user for each client connected to your BiSecure gateway. So you should not use the same client here as you use in the mobile app.
The default user name is "admin" and password "0000", which are the standard credential if nothing was changed. You should of course already have changed this!

### BiSecure Group

A BiSecure group is any device connected to the BiSecure gateway. Your garage door will appear as a BiSecure group.
The group needs no additional configuration.
Only garage doors (BiSecure Type 1) devices are currently supported.

## Discovery

When calling the auto discovery the gateway is discovered first. You should then correct the username and password for the BiSecure gateway and call the device discovery for the BiSecure binding again. Then all your attached devices should be discovered.

If auto discovery fails (e.g. because of network problems etc) you can add a BiSecure Gateway thing manually and set the gatewayAddress and gatewayId in the configuration properties manually. 

We are currently investigating problem with auto discovery while running under docker.

## Thing Configuration

You should at least configure your username and password for the connection of the binding to your BiSecure Gateway. You can create this user in the mobile app and don't forget to give him rights for all devices needed.

```
username=admin
password=0000
```

If doing manual adding of the gateway (which is normally filled by auto discovery:

```
gatewayAddress=192.168.0.3
gatewayId=5410EC036151
```

If you get often TimeoutExceptions during execution of requests, you can increase the receiveTimeout to a higher value than the default one (2000ms):

```
readTimeout=5000
```

## Channels

Currently only channels for port type ```IMPULS``` are created. 


| channel |      type     | description                   |

|---------|---------------|-------------------------------|

| IMPULS  | Rollershutter | The impulse controlled device |

## Full Example

Coming soon

## Trouble Shooting

For better error investigation please set Loglevel to DEBUG. In Karaf console execute:
``` log:set DEBUG org.openhab.binding.bisecuregateway ```
 
### Binding does not install

If you see only "Start BiSecure Gateway background discovery" and no further logging of the BiSecure binding, then most probably the auto discovery is not working.
If you are running under docker, the auto discovery could just not work here. Workaround is not yet available.

### Auto Discovery of Gateway does not work

Add the gateway thing manually and set gatewayAddress and gatewayId in configuration

#### Auto Discovery of groups (devices) does not work

Check if you supplied correct username and password in the gateway thing configuration.
Check the log file for more details which error occured.
   
