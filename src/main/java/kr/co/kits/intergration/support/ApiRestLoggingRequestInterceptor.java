package kr.co.kits.intergration.support;

import java.nio.charset.Charset;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.http.HttpRequest;
import org.springframework.http.client.ClientHttpRequestExecution;
import org.springframework.http.client.ClientHttpRequestInterceptor;
import org.springframework.http.client.ClientHttpResponse;
import org.springframework.util.StreamUtils;

import lombok.SneakyThrows;

public class ApiRestLoggingRequestInterceptor implements ClientHttpRequestInterceptor {

	private static final Logger requestLog = LogManager.getLogger("Request");
	private static final Logger responseLog = LogManager.getLogger("Response");

	@SneakyThrows
	private void log(HttpRequest request, byte[] body, ClientHttpResponse response, BufferingClientHttpResponseWrapper responseWrapper) {
		if (requestLog.isInfoEnabled()) {
			requestLog.info("request method: {}, request URI: {}, request headers: {}, request body: {}",
                    request.getMethod(),
                    request.getURI(),
                    request.getHeaders(),
                    new String(body, Charset.forName("UTF-8"))
                    );
		}
		if (responseLog.isInfoEnabled()) {
			responseLog.info("request method: {}, request URI: {}, request headers: {}, request body: {}, response status code: {}, response headers: {}, response body: {}",
                    request.getMethod(),
                    request.getURI(),
                    request.getHeaders(),
                    new String(body, Charset.forName("UTF-8")),
                    response.getStatusCode(),
					response.getHeaders(),
                    new String(StreamUtils.copyToByteArray(responseWrapper.getBody()), Charset.forName("UTF-8")));
		}
	}
	
	@SneakyThrows
	public ClientHttpResponse intercept(HttpRequest request, byte[] body, ClientHttpRequestExecution execution) {
	        BufferingClientHttpResponseWrapper responseWrapper = null;
	        ClientHttpResponse response = null;
	        try {
	            response = execution.execute(request, body);
	        } finally {
	            responseWrapper = new BufferingClientHttpResponseWrapper(response);
	    		log(request, body, response, responseWrapper);
	        }
	        return responseWrapper;
	    }
	}
