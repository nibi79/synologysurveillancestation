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
- Refresh rate for all other **Camera** events and dynamic channels

## Channels

Currently following **Channels** are supported on the **Bridge**:

- Home mode _SWITCH_

Currently following **Channels** are supported on the **Camera**:

- Snapshot _IMAGE_
- Snapshot static URI _STRING_
- Snapshot dynamic URI (refreshes with event refresh rate) _STRING_
- Snapshot static live feed URI (rtsp) _STRING_
- Snapshot static live feed URI (mjpeg over http) _STRING_
- Camera recording _SWITCH_
- Enable camera _SWITCH_
- Zoom _IN/OUT_ (PTZ cameras only)
- Move _UP/DOWN/LEFT/RIGHT/HOME_ (PTZ cameras only)
- Motion event _SWITCH_ (read-only)
- Alarm event _SWITCH_ (read-only)
- Manual event _SWITCH_ (read-only)

## Advanced: Using file based configuration

### .things file ###

```
Bridge synologysurveillancestation:station:diskstation "DiskStation" @ "ServerRoom" [ protocol="http", host="192.168.0.1", port="5000", username="my username", password="my password" ] {
Thing camera CameraID "Camera 1" @ "Outside" [ refresh-rate-events=5, refresh-rate-snapshot=10, snapshot-stream-id=1 ]
}
```
Here the **CameraID** is a numeric ID of your surveillance camera in Surveillance Station (e.g. 1) and snapshot stream ID is the ID of the preferred stream in Surveillance Station (e.g. 1 for 'Stream 1')

### .items file ###

```
Switch Surveillance_HomeMode "Home Mode" {channel="synologysurveillancestation:station:diskstation:homemode"}

Image Surveillance_Snapshot "Snapshot" {channel="synologysurveillancestation:camera:diskstation:1:common#snapshot"}

String Surveillance_Snapshot_Uri_Dynamic "Dynamic snapshot URI" {channel="synologysurveillancestation:camera:diskstation:1:common#snapshot-uri-dynamic"}
String Surveillance_Snapshot_Uri_Static "Static snapshot URI" {channel="synologysurveillancestation:camera:diskstation:1:common#snapshot-uri-static"}
String Surveillance_Snapshot_Live_Uri_Rtsp "Live feed URI (rtsp)" {channel="synologysurveillancestation:camera:diskstation:1:common#live-uri-rtsp"}
String Surveillance_Snapshot_Live_Uri_Mjpeg_Http "Live feed URI (mjpeg over http)" {channel="synologysurveillancestation:camera:diskstation:1:common#live-uri-mjpeg-http"}

Switch Surveillance_Recording "Camera recording" {channel="synologysurveillancestation:camera:diskstation:1:common#record"}
Switch Surveillance_Enabled "Camera enabled" {channel="synologysurveillancestation:camera:diskstation:1:common#enable"}

Switch Surveillance_Event_Motion "Camera motion event" {channel="synologysurveillancestation:camera:diskstation:1:event#motion"}
Switch Surveillance_Event_Alarm "Camera alarm event" {channel="synologysurveillancestation:camera:diskstation:1:event#alarm"}
Switch Surveillance_Event_Manual "Camera manual event" {channel="synologysurveillancestation:camera:diskstation:1:event#manual"}

String Surveillance_Zooming "Camera zooming" {channel="synologysurveillancestation:camera:diskstation:1:ptz#zoom"}
String Surveillance_Moving "Camera moving" {channel="synologysurveillancestation:camera:diskstation:1:ptz#move"}
```
Here `:1` is yet again the numeric ID of your surveillance camera from a previous step.

### .sitemap ###

```
Image item=Surveillance_Snapshot_Uri_Static url="[%s]" refresh=5000
Video item=Surveillance_Snapshot_Live_Uri_Mjpeg_Http url="[%s]" encoding="mjpeg"
```
