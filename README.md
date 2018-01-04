# Synology Surveillance Station Binding

This binding connects openHAB with your surveillance cameras running on Synology&copy; DiskStation using Synology Surveillance Station API. This binding should work with any DiskStation capable of running Surveillance Station as well as with any supported camera.  

## Disclaimer ##

This binding is currently under heavy development. Your help and testing would be greatly appreciated but there is no stability or functionality warranty.

## Installation and upgrading ## 

For an installation the latest release should be copied into the /addons folder of your openHAB installation. 
For an upgrade the existing file should be overwritten. On major or structural changes existing things might have to be deleted and recreated, existing channels might be kept. For further information please read release notes of a corresponding release.  

## Supported Things

Currently following Things are supported:
 
- **Bridge** Thing representing the Synology DiskStation / Surveillance Station
- One or many Things for supported **Cameras**

## Discovery

If your openHAB installation is in the same network as your Synology DiskStation, the discovery will automatically find your Surveillance Station. You can now add its **Bridge** Thing, which will first be _OFFLINE_. You should now configure the **Bridge** and enter your Surveillance Station credentials. For security reasons it's always a better practice to create a separate user for this. After entering correct credentials and updating the **Bridge**, automatic discovery for **cameras** will start. If successful, your **cameras** will be found and can be added without further configuration.   

## Configuration

Following options can be set for the **Bridge**:

- Access protocol of the DiskStation (read only / unchangeable with automatic discovery)
- IP of the DiskStation (read only / unchangeable with automatic discovery)  
- Port of the DiskStation (read only / unchangeable with automatic discovery)
- User name for the DiskStation / Surveillance Station
- Password for the DiskStation / Surveillance Station
- Refresh rate for DiskStation events (Home Mode)

Following options can be set for the **Camera**:

- Snapshot refresh rate 
- Refresh rate for all **Camera** events 

## Channels

Currently following **Channels** are supported on the **Bridge**:

- Home mode _SWITCH_

Currently following **Channels** are supported on the **Camera**:

- Snapshot _IMAGE_
- Camera recording _SWITCH_
- Enable camera _SWITCH_
- Zoom _IN/OUT_ (PTZ cameras only)
- Move _UP/DOWN/LEFT/RIGHT/HOME_ (PTZ cameras only)
- Motion event _SWITCH_ (read-only)
- Alarm event _SWITCH_ (read-only)
- Manual event _SWITCH_ (read-only) 