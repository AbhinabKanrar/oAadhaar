/**
 * 
 */
package com.mabsisa.oAadhaar;

import java.util.concurrent.TimeUnit;

import javax.net.ssl.HostnameVerifier;
import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLSession;
import javax.net.ssl.TrustManager;

import org.apache.http.HttpHost;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClientBuilder;
import org.springframework.http.client.HttpComponentsClientHttpRequestFactory;

/**
 * @author abhinab
 *
 */
public class AadhaarHttpRequestFactory extends HttpComponentsClientHttpRequestFactory {

	private static final String TLS_VERSION = "tls.version";
	private static CloseableHttpClient httpClient = null;
	private static int MAX_REST_CONN = 20;
	private static final int MAX_REST_CONN_PER_ROUTE = 10;
	private static final Long MAX_REST_CONN_IDLE_SECS = 5l;
	private static final boolean BYPASS_HOST_NAME_CHECK = false;
	private static final String PROXY_HOST = System.getProperty("proxy.host");
	private static final String PROXY_PORT = System.getProperty("proxy.port");

	static {

		HttpClientBuilder builder = HttpClientBuilder.create().setMaxConnTotal(MAX_REST_CONN)
				.setMaxConnPerRoute(MAX_REST_CONN_PER_ROUTE).evictExpiredConnections()
				.evictIdleConnections(MAX_REST_CONN_IDLE_SECS, TimeUnit.SECONDS).setSSLContext(initSSLContext());

		if (BYPASS_HOST_NAME_CHECK) {
			builder.setSSLHostnameVerifier(new HostnameVerifier() {
				@Override
				public boolean verify(String hostname, SSLSession arg1) {
					return true;
				}
			});
		}

		if (PROXY_HOST != null && PROXY_PORT != null) {
			HttpHost host = new HttpHost(PROXY_HOST, Integer.parseInt(PROXY_PORT));
			builder.setProxy(host);
		}

		httpClient = builder.build();
	}

	public AadhaarHttpRequestFactory() {
		super(httpClient);
	}

	private static SSLContext initSSLContext() {
		try {
			String tlsVersion = System.getProperty(TLS_VERSION, "TLSv1");
			System.setProperty("https.protocols", tlsVersion);
			final SSLContext ctx = SSLContext.getInstance(tlsVersion);
			final AadhaarTrustManager trustManager = new AadhaarTrustManager();
			ctx.init(null, new TrustManager[] { trustManager }, null);
			return ctx;
		} catch (Exception e) {
			return null;
		}
	}

}
