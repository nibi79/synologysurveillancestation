package org.openhab.binding.synologysurveillancestation.internal.webapi;

/**
 * API configuration parameters
 *
 * @author Nils
 *
 */
public class SynoApiConfig {

    private String name = null;
    private String version = null;
    private String scriptpath = null;

    /**
     * @param name
     * @param version
     * @param scriptpath
     */
    public SynoApiConfig(String name, String version, String scriptpath) {
        super();
        this.name = name;
        this.version = version;
        this.scriptpath = scriptpath;
    }

    /**
     *
     * @return
     */
    public String getName() {
        return name;
    }

    /**
     * @param name
     */
    public void setName(String name) {
        this.name = name;
    }

    /**
     * @return
     */
    public String getVersion() {
        return version;
    }

    /**
     * @param version
     */
    public void setVersion(String version) {
        this.version = version;
    }

    /**
     * @return
     */
    public String getScriptpath() {
        return scriptpath;
    }

    /**
     * @param scriptpath
     */
    public void setScriptpath(String scriptpath) {
        this.scriptpath = scriptpath;
    }

}
