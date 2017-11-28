package org.openhab.binding.synologysurveillancestation.internal.webapi;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;

import org.apache.commons.io.IOUtils;
import org.apache.http.HttpEntity;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.protocol.HttpClientContext;
import org.apache.http.client.utils.URIBuilder;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.openhab.binding.synologysurveillancestation.internal.Config;

public class SynoApiCameraGetSnapshot {

    private Config config = null;
    private String sessionID = null;

    /**
     * @param config
     * @param sessionID
     */
    public SynoApiCameraGetSnapshot(Config config, String sessionID) {
        this.config = config;
        this.sessionID = sessionID;
    }

    /**
     * Get the up-to-date snapshot of the selected camera in JPEG format.
     *
     * @throws WebApiException
     * @throws IOException
     * @throws UnsupportedOperationException
     * @throws URISyntaxException
     *
     */
    public ByteArrayOutputStream getSnapshot(String cameraId) throws IOException, URISyntaxException {

        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        CloseableHttpResponse response = null;

        try {

            String apiUrl = createWebApiUrl(cameraId);

            URL url = new URL(apiUrl);
            URI uri = url.toURI();

            CloseableHttpClient httpclient = HttpClients.createDefault();
            HttpClientContext context = HttpClientContext.create();
            HttpGet httpget = new HttpGet(uri);

            response = httpclient.execute(httpget, context);

            HttpEntity entity = response.getEntity();
            IOUtils.copy(entity.getContent(), baos);
            entity.getContent();

            return baos;

        } finally {
            if (response != null) {

                response.close();
            }

        }

    }

    /**
     * *
     * /webapi/entry.cgi?camStm=1&version=8&cameraId=1&api=SYNO.SurveillanceStation.Camera&preview=true&method=GetSnapshot&_sid=123456
     *
     * @return
     * @throws URISyntaxException
     * @throws MalformedURLException
     */
    private String createWebApiUrl(String cameraId) throws MalformedURLException, URISyntaxException {

        URIBuilder b = new URIBuilder();

        b.setScheme(config.getProtocol());
        b.setHost(config.getHost());
        b.setPort(Integer.parseInt(config.getPort()));
        b.setPath("/webapi/entry.cgi");

        b.addParameter("api", "SYNO.SurveillanceStation.Camera");
        b.addParameter("method", "GetSnapshot");
        b.addParameter("version", "8");

        b.addParameter("camStm", "1");
        b.addParameter("cameraId", cameraId);
        b.addParameter("preview", "true");

        b.addParameter("_sid", sessionID);
        URL url = b.build().toURL();

        return url.toString();
    }

}
