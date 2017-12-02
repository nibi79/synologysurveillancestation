/**
 *
 */
package org.openhab.binding.synologysurveillancestation.internal.discovery;

import java.io.IOException;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.net.NetworkInterface;
import java.net.Socket;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.TimeUnit;

import org.eclipse.smarthome.config.discovery.AbstractDiscoveryService;
import org.eclipse.smarthome.config.discovery.DiscoveryResult;
import org.eclipse.smarthome.config.discovery.DiscoveryResultBuilder;
import org.eclipse.smarthome.config.discovery.DiscoveryService;
import org.eclipse.smarthome.config.discovery.DiscoveryServiceCallback;
import org.eclipse.smarthome.config.discovery.ExtendedDiscoveryService;
import org.eclipse.smarthome.core.thing.ThingUID;
import org.openhab.binding.synologysurveillancestation.SynologySurveillanceStationBindingConstants;
import org.openhab.binding.synologysurveillancestation.internal.Config;
import org.openhab.binding.synologysurveillancestation.internal.webapi.request.SynoApiQuery;
import org.openhab.binding.synologysurveillancestation.internal.webapi.response.SimpleResponse;
import org.osgi.service.component.annotations.Component;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @author Pav
 *
 */
@Component(service = DiscoveryService.class, immediate = true, configurationPid = "discovery.synologysurveillancestation")
public class BridgeDiscoveryService extends AbstractDiscoveryService implements ExtendedDiscoveryService {

    public static final int DISCOVERY_TIMEOUT = 5;
    private final Logger logger = LoggerFactory.getLogger(BridgeDiscoveryService.class);
    ScheduledFuture<?> discoveryJob;
    private DiscoveryServiceCallback discoveryServiceCallback;

    public BridgeDiscoveryService() {
        super(SynologySurveillanceStationBindingConstants.SUPPORTED_BRIDGE_TYPES, DISCOVERY_TIMEOUT);
    }

    /**
     * Defines a runnable for a discovery
     */
    Runnable runnable = new Runnable() {
        @Override
        public void run() {
            String subnet = getSubnet();
            if (subnet != null) {

                Config config = new Config();
                config.setPort("5000");
                config.setProtocoll("http");

                // Polling all potential IPs of this subnet
                for (int ip = 1; ip < 255; ip++) {
                    String currentIp = subnet + String.valueOf(ip);

                    //logger.debug("Polling {} ", currentIp);

                    if (pingHost(currentIp, 5000, 200)) {

                        config.setHost(currentIp);
                        SynoApiQuery apiQuery = new SynoApiQuery(config);

                        try {
                            SimpleResponse si = apiQuery.query();
                            if (si.isSuccess()) {
                                // Try to register a new thing
                                String thingId = "ds-" + getHostName(currentIp);

                                ThingUID thingUID = new ThingUID(
                                        SynologySurveillanceStationBindingConstants.THING_TYPE_STATION, thingId);

                                if (discoveryServiceCallback.getExistingThing(thingUID) != null) {
                                    logger.debug("Thing " + thingUID.toString() + " already exists");
                                } else if (discoveryServiceCallback.getExistingDiscoveryResult(thingUID) != null) {
                                    logger.debug("Thing " + thingUID.toString() + " was discovered already");
                                } else {
                                    Map<String, Object> properties = new HashMap<>(1);
                                    properties.put(SynologySurveillanceStationBindingConstants.PROTOCOL,
                                            config.getProtocol());
                                    properties.put(SynologySurveillanceStationBindingConstants.PORT, config.getPort());
                                    properties.put(SynologySurveillanceStationBindingConstants.HOST, config.getHost());
                                    properties.put(SynologySurveillanceStationBindingConstants.PASSWORD, null);
                                    properties.put(SynologySurveillanceStationBindingConstants.USER_NAME, null);

                                    DiscoveryResult discoveryResult = DiscoveryResultBuilder.create(thingUID)
                                            .withProperties(properties).withLabel(thingId).build();

                                    thingDiscovered(discoveryResult);
                                }

                            }
                        } catch (Exception e) {
                            logger.debug("Error: ", e);
                        }
                    }

                }

            } else {
                logger.info("Automatic discovery fails: no LAN subnet found");
            }
        }
    };

    /**
     * Gets the host name of a given IP address in LAN
     *
     * @param host IP address of a host
     * @return Host name
     */
    private String getHostName(String host) {
        try {
            InetAddress inetAddress = InetAddress.getByName(host);
            return inetAddress.getHostName().replaceAll("\\.", "-");
        } catch (Exception ex) {
            return host.replaceAll("\\.", "-");
        }
    }

    /**
     * Fast pinging of a subnet
     *
     * @see https://stackoverflow.com/questions/3584210/preferred-java-way-to-ping-an-http-url-for-availability
     * @param host Host to ping
     * @param port Port to ping
     * @param timeout Timeout in milliseconds
     * @return Ping result
     */
    private boolean pingHost(String host, int port, int timeout) {
        try (Socket socket = new Socket()) {
            socket.connect(new InetSocketAddress(host, port), timeout);
            return true;
        } catch (IOException e) {
            return false; // Either timeout or unreachable or failed DNS lookup.
        }
    }

    /**
     * Scans all network adapters and takes the first occurrence of 192.0.0.0 or 10.0.0.0 subnet
     *
     * @return Subnet as String
     */
    private String getSubnet() {
        try {
            Enumeration<NetworkInterface> nis = NetworkInterface.getNetworkInterfaces();
            while (nis.hasMoreElements()) {
                NetworkInterface ni = nis.nextElement();
                Enumeration<InetAddress> ias = ni.getInetAddresses();
                while (ias.hasMoreElements()) {
                    InetAddress ia = ias.nextElement();
                    byte[] ip = ia.getAddress();
                    if (ip.length == 4) {// IPv4 support only
                        if (ip[0] == (byte) 192 || ip[1] == 10) {
                            String subnet = String.format("%d.%d.%d.", 0xff & ip[0], 0xff & ip[1], 0xff & ip[2]);
                            return subnet;
                        }
                    }
                }
            }
        } catch (Exception e) {
            // discovery fails
        }
        return null;
    }

    /**
     * Triggers manual scan
     */
    @Override
    protected void startScan() {
        startBackgroundDiscovery();
    }

    @Override
    protected void startBackgroundDiscovery() {
        logger.debug("Start Synology DiskStation background discovery");
        if (discoveryJob == null || discoveryJob.isCancelled() || discoveryJob.isDone()) {
            discoveryJob = scheduler.schedule(runnable, 0, TimeUnit.SECONDS);
        }
    }

    @Override
    protected void stopBackgroundDiscovery() {
        logger.debug("Stop Synology DiskStation background discovery");
        if (discoveryJob != null && !discoveryJob.isCancelled()) {
            discoveryJob.cancel(true);
            discoveryJob = null;
        }
    }

    @Override
    public void setDiscoveryServiceCallback(DiscoveryServiceCallback discoveryServiceCallback) {
        this.discoveryServiceCallback = discoveryServiceCallback;
    }

}
