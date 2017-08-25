package com.mabsisa.oAadhaar;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.security.KeyStore;
import java.security.cert.Certificate;
import java.security.cert.CertificateException;
import java.security.cert.CertificateFactory;
import java.security.cert.X509Certificate;

import javax.net.ssl.TrustManager;
import javax.net.ssl.TrustManagerFactory;
import javax.net.ssl.X509TrustManager;

public class AadhaarTrustManager implements X509TrustManager {

	static String DEFAULT_TRUST_STORE = "/etc/ssl/certs/java/cacerts";
	static X509TrustManager trustManager;

	static {
		try {
			KeyStore ts = KeyStore.getInstance(KeyStore.getDefaultType());
			InputStream in = new FileInputStream(DEFAULT_TRUST_STORE);
			try {
				ts.load(in, null);
			} finally {
				in.close();
			}
			CertificateFactory cf = CertificateFactory.getInstance("X.509");
			InputStream is = new FileInputStream(
					new File("/home/rsdpp/git/UPI-2.0/npci-upi/certificate/ssl/haproxy.crt"));
			InputStream caInput = new BufferedInputStream(is);
			Certificate ca;
			try {
				ca = cf.generateCertificate(caInput);
				ts.setCertificateEntry("" + System.currentTimeMillis(), ca);
			} finally {
				try {
					caInput.close();
				} catch (IOException e) {
				}
				try {
					is.close();
				} catch (IOException e) {
				}
			}
			TrustManagerFactory tmf = TrustManagerFactory.getInstance(TrustManagerFactory.getDefaultAlgorithm());
			tmf.init(ts);

			TrustManager tms[] = tmf.getTrustManagers();
			for (int i = 0; i < tms.length; i++) {
				if (tms[i] instanceof X509TrustManager) {
					trustManager = (X509TrustManager) tms[i];
					break;
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	@Override
	public void checkClientTrusted(X509Certificate[] chain, String authType) throws CertificateException {
		try {
			trustManager.checkServerTrusted(chain, authType);
		} catch (CertificateException ce) {
			throw ce;
		}
	}

	@Override
	public void checkServerTrusted(X509Certificate[] chain, String authType) throws CertificateException {
		trustManager.checkClientTrusted(chain, authType);
	}

	@Override
	public X509Certificate[] getAcceptedIssuers() {
		return trustManager.getAcceptedIssuers();
	}

}
