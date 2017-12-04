# Synology Surveillance Station Binding

This binding connects openHAB with your surveillance cameras running on Synology&copy; DiskStation using Synology Surveillance Station API. This binding should work with any DiskStation capable of running Surveillance Station as well as with any supported camera.  

## Supported Things

Currently following Things are supported: 
- Bridge Thing for the Synology Surveillance Station
- One or many Things for supported cameras   

## Discovery

If your openHAB installation is in the same network (192.x.x.x or 10.x.x.x) as your Synology DiskStation, the discovery will automatically find your Surveillance Station. You can now add the Bridge Thing, which will have _OFFLINE (NOT CONFIGURED)_ state. You should now configure this new Thing and enter your Surveillance Station credentials. For security reasons it's always a better practice to create a separate user for this. After entering correct credentials and updating the Bridge, automatic discovery for the cameras will start. If successful, your cameras will be found and you can add them without further configuration.   

## Binding Configuration

_Not yet implemented._

## Thing Configuration

_Not yet implemented._

## Channels

_Not yet implemented._

## Full Example

_Not yet implemented._
