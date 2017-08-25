/**
 * 
 */
package com.mabsisa.oAadhaar;

import javax.xml.bind.JAXBContext;

import org.springframework.http.ResponseEntity;
import org.springframework.http.client.ClientHttpRequestFactory;
import org.springframework.web.client.RestTemplate;

/**
 * @author abhinab
 *
 */
public final class AadhaarRest {

	
	private static AadhaarRest _instance;
	private final RestTemplate restTemplate;

	private static int MAX_RETRY_COUNT = Integer.parseInt(System.getProperty("max.retry.count", "0"));

	static JAXBContext jaxbContext;

	private AadhaarRest() {
		restTemplate = new RestTemplate(clientHttpRequestFactory());
	}

	private ClientHttpRequestFactory clientHttpRequestFactory() {
		AadhaarHttpRequestFactory factory = new AadhaarHttpRequestFactory();
		factory.setReadTimeout(20000);
		factory.setConnectTimeout(Integer.parseInt(System.getProperty("connect.timeout", "5000")));
		factory.setBufferRequestBody(false);
		return factory;
	}
	
	public static synchronized AadhaarRest createObject() {
		if (null == _instance) {
			_instance = new AadhaarRest();			
		}
		return _instance;
	}
	
//	public <T> ResponseEntity<T> postForEntity(String url, Object request,
//			Class<T> responseType, Object... uriVariables) {
//		return restTemplate.postForEntity(url, entity, responseType,
//				uriVariables);
//	}

}
