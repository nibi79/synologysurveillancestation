# Synology Surveillance Station Binding

This binding connects openHAB with your surveillance cameras running on Synology&copy; DiskStation using Synology Surveillance Station API. This binding should work with any DiskStation capable of running Surveillance Station as well as with any supported camera.

# Table of contents

- [Synology Surveillance Station Binding](#synology-surveillance-station-binding)
- [Table of contents](#table-of-contents)
  - [Disclaimer](#disclaimer)
  - [Installation and upgrade](#installation-and-upgrade)
  - [Supported Things](#supported-things)
  - [Discovery](#discovery)
  - [Configuration](#configuration)
  - [Channels](#channels)
  - [File based configuration](#file-based-configuration)
    - [.things](#things)
    - [.items](#items)
    - [.sitemap](#sitemap)
  - [Transformation](#transformation)
    - [.items](#items-1)
    - [transform/liveuri.js](#transformliveurijs)
  - [Building the plugin](#building-the-plugin)
    - [Using docker](#using-docker)
  - [Developing the plugin](#developing-the-plugin)
  - [Support](#support)
***

## Disclaimer

This binding is currently under development. Your help and testing would be greatly appreciated but there is no stability or functionality warranty.

## Installation and upgrade

For an installation the [latest release](https://github.com/nibi79/synologysurveillancestation/releases) should be copied into the /addons folder of your openHAB installation.
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
- Host/IP of the DiskStation (read only / unchangeable with automatic discovery)
- Port of the DiskStation (read only / unchangeable with automatic discovery)
- User name for the DiskStation / Surveillance Station
- Password for the DiskStation / Surveillance Station
- Refresh rate for DiskStation events (Home Mode)
- (**advanced**) Enable support for self-signed / invalid SSL certificates (binding or openHAB restart required on change)

Following options can be set for the **Camera**:

- Snapshot refresh rate
- Refresh rate for all other **Camera** events and dynamic channels
- Refresh rate for motion detection parameter (defaults to 0 = no autorefresh)

## Channels

Currently following **Channels** are supported on the **Bridge**:

- Home mode _SWITCH_
- External event trigger _NUMBER_ (1 to 10, write-only)
- Current session ID (SID) _STRING_

Currently following **Channels** are supported on the **Camera**:

- Common channels:
     - Snapshot _IMAGE_
     - Camera recording _SWITCH_
     - Enable camera _SWITCH_
- URIs:
     - Snapshot static URI _STRING_
     - Snapshot dynamic URI (refreshes with event refresh rate) _STRING_
     - Snapshot static live feed URI (rtsp) _STRING_
     - Snapshot static live feed URI (mjpeg over http) _STRING_
- PTZ (Pan/Tilt/Zoom) for PTZ cameras only:
     - Zoom _IN/OUT_
     - Move _UP/DOWN/LEFT/RIGHT/HOME_
     - Continuous Move/Zoom with _START_\<COMMAND\>_ and _STOP_\<COMMAND\>_
     - Move to preset
     - Run patrol
- Event channels:
     - Motion event _SWITCH_ (read-only)
     - Alarm event _SWITCH_ (read-only)
     - Manual event _SWITCH_ (read-only)
     - Continuous recording event _SWITCH_ (read-only)
     - External event _SWITCH_ (read-only)
     - Action rule event _SWITCH_ (read-only)
- Motion detection channels (if available):
     - Motion detection source _STRING_ (-1:disable, 0:by camera, 1:by Surveillance Station)
     - Motion detection sensitivity _NUMBER_ (1 to 99)
     - Motion detection threshold _NUMBER_ (1 to 99)
     - Motion detection object size _NUMBER_ (1 to 99)
     - Motion detection percentage _NUMBER_ (1 to 99)
     - Ignore short-lived motion for _NUMBER_ (0 to 10) seconds

## File based configuration

### .things

```
Bridge synologysurveillancestation:station:diskstation "DiskStation" @ "ServerRoom" [ protocol="http", host="192.168.0.1", port="5000", username="my username", password="my password", acceptSsl="false" ] {
Thing camera CameraID "Camera 1" @ "Outside" [ refresh-rate-events=5, refresh-rate-snapshot=10, refresh-rate-md-param=120, snapshot-stream-id=1 ]
}
```
or for a self-signed SSL certificate:
```
Bridge synologysurveillancestation:station:diskstation "DiskStation" @ "ServerRoom" [ protocol="https", host="192.168.0.1", port="5001", username="my username", password="my password", acceptSsl="true" ] {
Thing camera CameraID "Camera 1" @ "Outside" [ refresh-rate-events=5, refresh-rate-snapshot=10, refresh-rate-md-param=120, snapshot-stream-id=1 ]
}
```

Here the **CameraID** is a numeric ID of your surveillance camera in Surveillance Station (e.g. 1) and snapshot stream ID is the ID of the preferred stream in Surveillance Station (e.g. 1 for 'Stream 1')

### .items

```
Switch Surveillance_HomeMode "Home Mode" {channel="synologysurveillancestation:station:diskstation:homemode"}
Number:Dimensionless Surveillance_Event_Trigger "External event trigger" {channel="synologysurveillancestation:station:diskstation:eventtrigger"}
String Surveillance_SID "Current SID" {channel="synologysurveillancestation:station:diskstation:sid"}

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
Switch Surveillance_Event_Continuous "Camera continuous recording event" {channel="synologysurveillancestation:camera:diskstation:1:event#continuous"}
Switch Surveillance_Event_External "Camera external event" {channel="synologysurveillancestation:camera:diskstation:1:event#external"}
Switch Surveillance_Event_ActionRule "Camera action rule event" {channel="synologysurveillancestation:camera:diskstation:1:event#actionrule"}

String Surveillance_Zooming "Camera zooming" {channel="synologysurveillancestation:camera:diskstation:1:ptz#zoom"}
String Surveillance_Moving "Camera moving" {channel="synologysurveillancestation:camera:diskstation:1:ptz#move"}
String Surveillance_Presets "Camera moving to preset" {channel="synologysurveillancestation:camera:diskstation:1:ptz#movepreset"}
String Surveillance_Patrols "Camera run patrol" {channel="synologysurveillancestation:camera:diskstation:1:ptz#runpatrol"}

String Surveillance_MD_Source "Motion detection source" {channel="synologysurveillancestation:camera:diskstation:1:md-param#md-param-source"}
Number:Dimensionless Surveillance_MD_Sensitivity "Motion detection sensitivity" {channel="synologysurveillancestation:camera:diskstation:1:md-param#md-param-sensitivity"}
Number:Dimensionless Surveillance_MD_Threshold "Motion detection threshold" {channel="synologysurveillancestation:camera:diskstation:1:md-param#md-param-threshold"}
Number:Dimensionless Surveillance_MD_Objectsize "Motion detection objectsize" {channel="synologysurveillancestation:camera:diskstation:1:md-param#md-param-objectsize"}
Number:Dimensionless Surveillance_MD_Percentage "Motion detection percentage" {channel="synologysurveillancestation:camera:diskstation:1:md-param#md-param-percentage"}
Number:Dimensionless Surveillance_MD_Shortlive "Ignore short-lived motion" {channel="synologysurveillancestation:camera:diskstation:1:md-param#md-param-shortlive"}
```

Here `:1` is yet again the numeric ID of your surveillance camera from a previous step.

### .sitemap

```
Switch item=Surveillance_Zooming mappings=[IN="IN", OUT="OUT"]

// Some cameras like Reolink do not support simple stepping
Switch item=Surveillance_ContinuousZoomingIn mappings=[START_IN="Start ZoomIn", STOP_IN="Stop ZoomIn"]
Switch item=Surveillance_ContinuousZoomingOut mappings=[START_OUT="Start ZoomOut", STOP_OUT="Stop ZoomOut"]

Switch item=Surveillance_Moving mappings=[UP="UP", DOWN="DOWN", LEFT="LEFT", RIGHT="RIGHT"]
 
Selection item=Surveillance_Presets label="Surveillance_Presets Selection" 
Switch item=Surveillance_Presets label="Surveillance_Presets Mapping" mappings=[preset1="Preset 1",preset2="Preset 2"]

Image item=Surveillance_Snapshot_Uri_Static url="[%s]" refresh=5000
Video item=Surveillance_Snapshot_Live_Uri_Mjpeg_Http url="[%s]" encoding="mjpeg"
```

## Transformation

Existing URIs can also be transformed using JS transformation to build similar URIs. Most requests can be extended or constructed manually using SID (session ID) for authentication by adding `&_sid=your-current-SID` to the query string. Please refer to [Synology Surveillance Station API documentation](https://global.download.synology.com/download/Document/DeveloperGuide/Surveillance_Station_Web_API_v2.8.pdf) for more details.

Example: SID-based stream URI (over http).

### .items

```
String Surveillance_Snapshot_Live_Uri_Static "SID-based URI" {channel="synologysurveillancestation:camera:diskstation:1:common#snapshot-uri-static"[profile="transform:JS", function="liveuri.js"]}
```

### transform/liveuri.js

```
(function(i) {
    return i.replace("entry.cgi", "SurveillanceStation/videoStreaming.cgi").replace(".Camera", ".VideoStream").replace("version=8", "version=1").replace("GetSnapshot", "Stream&format=mjpeg")
})(input)
```

Please note, **[Javascript Transformation](https://www.openhab.org/addons/transformations/javascript/)** add-on must be installed for the transformation to work properly.

## Building the plugin

If you have Java 8 installed simply use the following command to build the plugin

```bash
mvn clean install
```

This will create a `target` folder that contains the plugin `jar` file that can be used in your OH2 setup.

### Using docker

The easiest way to build the plugin without setting up your system with all necessary java components is to use the provided `docker` environment.
In order to build the plugin simply run the following commands. You will find the `jar` files within the same directories as if you would have build the plugin locally.

```bash
docker-compose run build-plugin
```

## Developing the plugin

The simplest way to engage in extending the plugin is to use `Eclipse` by following the OH2 developers guide. A good explanation can be found here
https://github.com/openhab/openhab2-addons

The steps to setup the plugin in your IDE are the following

1. In the eclipse installer only select `openHAB Development` (And make sure to run JAVA 8) The rest will be taken care of by maven dependencies and you don't need those other projects to develop your binding.
   1. If you have Eclipse already installed got to `File -> Import... -> Oomph -> GitHub Projects -> openHAB`
2. Wait till Eclipse finishes startup. No need to do any of: Clean, Build, perform ModelGen launch.
3. In the `pom.xml` of the demo app. (Next to bnd.runapp) remove the dependency `org.openhab.addons.bom.openhab-addons` and add following dependency

```xml
<dependency>
    <groupId>org.openhab.addons.bundles</groupId>
    <artifactId>org.openhab.transform.map</artifactId>
    <version>${project.version}</version>
    <scope>runtime</scope>
</dependency>
```

4. Only import your binding in eclipse by going to `File -> Import... -> Existing Project into Worspace` and select the root folder of this project
5. Add the dependency of your binding to the demo pom.xml (The dependency is what you would have put in the bom/openhab-addons/pom.xml

```xml
<dependency>
    <groupId>org.openhab.addons.bundles</groupId>
    <artifactId>org.openhab.binding.synologysurveillancestation</artifactId>
    <version>${project.version}</version>
    <scope>runtime</scope>
</dependency>
```

6. Open the bnd.runapp. In browse repos you should be able to search your binding.
7. Drag your binding from the browse repos to the Run requirements.
8. Click resolve button (below Run requirements)
9.  Start the bnd.runapp via buttons above Run requirements.
10. This should show logging in console in eclipse.
11. You should be able to browse to http://localhost:8080/paperui/index.html (can take some time)


## Support

If you encounter critical issues with this binding, please consider to:

- create an [issue](https://github.com/nibi79/synologysurveillancestation/issues) on GitHub
- search [community forum](https://community.openhab.org/t/binding-request-synology-surveillance-station/8200) for answers already given
- or make a new post there, if nothing was found

In any case please provide some information about your problem:

- openHAB and binding version
- error description and steps to retrace if applicable
- any related `[WARN]`/`[ERROR]` from openhab.log
- whether it's the binding, bridge, camera or channel related issue

For the sake of documentation please use English language.
