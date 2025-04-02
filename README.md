# Simple plugin that prevents players from joing your server without specific permission.

Simply add the plugin to the server and restart it.
A configuration and log file will be created at the specified path.
```
plugins/VIPAccess/config.yml
```

## **config.yml**


```
# config.yml (default)
enabled: true
required-permission: server.vip
kick-message: '&c&lERROR!
  &7No access!' #example
redirect-server: '' # PLS DONT`T CHANGE, IT`S BROKEN
```

### Commands

_/vipaccess help_ - Show help lable

_/vipaccess reload_ - Reload plugin

_/vipaccess enabled [true/false]_ - Enable/disable plugin

_/vipaccess required-permission [permission]_ - Set permission required to join the server (if <permission> == true, connect the player)

_/vipaccess kick-message [message]_ - Set the message displayed to the player when access is denied

_/vipaccess stats_ - Show statistics (how many players have been disconnected in total)

## logs.yml

```
# example
logs:
  entry-1743443057869: 'time: 2025-03-31T22:44:17.8695352, player: Steve, uuid:
    7f1481b8-d272-35c1-9713-98662c3d01a5, ip: 123.456.789.101'
```
