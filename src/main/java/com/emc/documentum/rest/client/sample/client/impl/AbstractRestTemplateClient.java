/*
 * Copyright (c) 2018. Open Text Corporation. All Rights Reserved.
 */
package com.emc.documentum.rest.client.sample.client.impl;

import static com.emc.documentum.rest.client.sample.client.util.Headers.ACCEPT_ATOM_HEADERS;
import static com.emc.documentum.rest.client.sample.client.util.Headers.ACCEPT_JSON_HEADERS;
import static com.emc.documentum.rest.client.sample.client.util.Headers.ACCEPT_JSON_HEADERS_WITH_CONTENT;
import static com.emc.documentum.rest.client.sample.client.util.Headers.ACCEPT_JSON_HEADERS_WITH_MULTIPART_CONTENT;
import static com.emc.documentum.rest.client.sample.client.util.Headers.ACCEPT_XML_HEADERS;
import static com.emc.documentum.rest.client.sample.client.util.Headers.ACCEPT_XML_HEADERS_WITH_CONTENT;
import static com.emc.documentum.rest.client.sample.client.util.Headers.ACCEPT_XML_HEADERS_WITH_MULTIPART_CONTENT;
import static com.emc.documentum.rest.client.sample.client.util.Headers.JSON_CONTENT;
import static com.emc.documentum.rest.client.sample.client.util.Headers.XML_CONTENT;
import static com.emc.documentum.rest.client.sample.model.LinkRelation.BATCHES;
import static com.emc.documentum.rest.client.sample.model.LinkRelation.EDIT;
import static com.emc.documentum.rest.client.sample.model.LinkRelation.PAGING_FIRST;
import static com.emc.documentum.rest.client.sample.model.LinkRelation.PAGING_LAST;
import static com.emc.documentum.rest.client.sample.model.LinkRelation.PAGING_NEXT;
import static com.emc.documentum.rest.client.sample.model.LinkRelation.PAGING_PREV;
import static com.emc.documentum.rest.client.sample.model.LinkRelation.SELF;
import static org.springframework.http.HttpMethod.DELETE;
import static org.springframework.http.HttpMethod.GET;
import static org.springframework.http.HttpMethod.POST;
import static org.springframework.http.HttpMethod.PUT;

import java.io.BufferedOutputStream;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.List;

//import javax.annotation.concurrent.NotThreadSafe;
import javax.net.ssl.SSLContext;

import org.apache.commons.codec.binary.Base64;
import org.apache.http.client.HttpClient;
import org.apache.http.conn.ssl.AllowAllHostnameVerifier;
import org.apache.http.conn.ssl.BrowserCompatHostnameVerifier;
import org.apache.http.conn.ssl.SSLConnectionSocketFactory;
import org.apache.http.conn.ssl.SSLContextBuilder;
import org.apache.http.conn.ssl.TrustSelfSignedStrategy;
import org.apache.http.conn.ssl.X509HostnameVerifier;
import org.apache.http.impl.client.HttpClients;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.http.client.ClientHttpRequest;
import org.springframework.http.client.ClientHttpRequestFactory;
import org.springframework.http.client.ClientHttpRequestInterceptor;
import org.springframework.http.client.ClientHttpResponse;
import org.springframework.http.client.HttpComponentsClientHttpRequestFactory;
import org.springframework.http.client.InterceptingClientHttpRequestFactory;
import org.springframework.http.client.SimpleClientHttpRequestFactory;
import org.springframework.util.FileCopyUtils;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RequestCallback;
import org.springframework.web.client.ResponseExtractor;
import org.springframework.web.client.RestTemplate;

import com.emc.documentum.rest.client.sample.client.DCTMRestClient;
import com.emc.documentum.rest.client.sample.client.DCTMRestErrorException;
import com.emc.documentum.rest.client.sample.client.converter.MultipartBatchHttpMessageConverter;
import com.emc.documentum.rest.client.sample.client.util.DCContentUploader;
import com.emc.documentum.rest.client.sample.client.util.Debug;
import com.emc.documentum.rest.client.sample.client.util.Headers;
import com.emc.documentum.rest.client.sample.client.util.Randoms;
import com.emc.documentum.rest.client.sample.client.util.Strings;
import com.emc.documentum.rest.client.sample.client.util.SupportedMediaTypes;
import com.emc.documentum.rest.client.sample.client.util.UriHelper;
import com.emc.documentum.rest.client.sample.model.Entry;
import com.emc.documentum.rest.client.sample.model.Feed;
import com.emc.documentum.rest.client.sample.model.FeedBase;
import com.emc.documentum.rest.client.sample.model.FutureModel;
import com.emc.documentum.rest.client.sample.model.HomeDocument;
import com.emc.documentum.rest.client.sample.model.LinkRelation;
import com.emc.documentum.rest.client.sample.model.Linkable;
import com.emc.documentum.rest.client.sample.model.Repository;
import com.emc.documentum.rest.client.sample.model.RestError;
import com.emc.documentum.rest.client.sample.model.RestObject;
import com.emc.documentum.rest.client.sample.model.batch.Attachment;
import com.emc.documentum.rest.client.sample.model.batch.Batch;
import com.emc.documentum.rest.client.sample.model.batch.Operation;
import com.emc.documentum.rest.client.sample.model.json.JsonRestError;
import com.emc.documentum.rest.client.sample.model.xml.jaxb.JaxbRestError;

import kr.co.kits.intergration.support.ApiRestLoggingRequestInterceptor;

/**
 * the basic implementation with Spring RestTemplate
 */
//@NotThreadSafe
public abstract class AbstractRestTemplateClient implements DCTMRestClient {
    protected static final String CSRF_HEADER_NAME_HEADER = "DOCUMENTUM-CSRF-HEADER-NAME";
    protected static final String CLIENT_TOKEN_NAME = "DOCUMENTUM-CLIENT-TOKEN";
    
    protected final RestTemplate restTemplate = new RestTemplate();
    
    protected final String contextRoot;
    protected final boolean useFormatExtension;
    protected final String repositoryName;
    protected final String username;
    protected final String password;
    protected HomeDocument homeDocument;
    protected RestObject productInfo;
    protected Feed<Repository> repositories;
    protected Repository repository;
    protected boolean enableStreaming = false;
    protected boolean debug;
    protected boolean enableCSRFClientToken = true;
    protected boolean ignoreSslWarning = false;
    
    protected String ifMatch;
    protected String ifNoneMatch;
    
    protected String clientToken;
    protected String csrfHeader;
    protected String csrfToken;
    
    protected HttpHeaders headers;
    protected HttpStatus status;
    protected RequestProcessor requestProcessor;
    
    protected final RequestProcessor defaultRequestProcessor = new DefaultRequestProcessor();
    
    public AbstractRestTemplateClient(String contextRoot, String repositoryName, String username, String password, boolean useFormatExtension) {
        this(contextRoot, repositoryName, username, password, useFormatExtension, false);
    }

    public AbstractRestTemplateClient(String contextRoot, String repositoryName, String username, String password, boolean useFormatExtension, boolean ignoreSslWarning) {
        this.contextRoot = contextRoot;
        this.repositoryName = repositoryName;
        this.username = username;
        this.password = password;
        this.useFormatExtension = useFormatExtension;
        this.ignoreSslWarning = ignoreSslWarning;
        initRestTemplate(restTemplate);
    }
    
    protected <T extends AbstractRestTemplateClient> T clone(T client) {
        client.homeDocument = homeDocument;
        client.productInfo = productInfo;
        client.repositories = repositories;
        client.repository = repository;
        client.enableStreaming = enableStreaming;
        client.debug = debug;
        client.enableCSRFClientToken = enableCSRFClientToken;
        
        client.clientToken = clientToken;
        client.csrfHeader = csrfHeader;
        client.csrfToken = csrfToken;
        return client;
    }
    
    /**
     * HomeDocument resource uri is the only fixed uri
     * it must be: "Rest context path"/services
     * e.g.
     * http://localhost:8080/dctm-rest/services 
     * @return the HomeDocument resource uri
     */
    protected String getHomeDocumentUri() {
        String servicesUri = contextRoot + "/services";
        if(useFormatExtension) {
            switch(getClientType()) {
                case XML:
                    servicesUri += ".xml";
                    break;
                case JSON:
                    servicesUri += ".json";
                    break;
            }
        }
        return servicesUri;
    }
    
    @Override
    public HttpHeaders getHeaders() {
        return headers;
    }
    
    @Override
    public HttpStatus getStatus() {
        return status;
    }
    
    @Override
    public void ifMatch(String ifMatch) {
        this.ifMatch = ifMatch;
    }

    @Override
    public void ifNoneMatch(String ifNoneMatch) {
        this.ifNoneMatch = ifNoneMatch;
    }

    public abstract AbstractRestTemplateClient clone();
    
    public AbstractRestTemplateClient debug(boolean debug) {
        this.debug = debug;
        return this;
    }
    
    @Override
    public String getCurrentRepositoryName() {
        return repositoryName;
    }
    
    private ClientHttpRequestFactory getClientHttpRequestFactory() {
        HttpComponentsClientHttpRequestFactory clientHttpRequestFactory = new HttpComponentsClientHttpRequestFactory();
        clientHttpRequestFactory.setConnectTimeout(60000);
        clientHttpRequestFactory.setReadTimeout(60000);
        return clientHttpRequestFactory;
    }
    
    protected void initRestTemplate(RestTemplate restTemplate) {
//        restTemplate.setRequestFactory(new HttpComponentsClientHttpRequestFactory());
        List<ClientHttpRequestInterceptor> interceptors = new ArrayList<>(1);
        interceptors.add(new ApiRestLoggingRequestInterceptor());
        restTemplate.setRequestFactory(new InterceptingClientHttpRequestFactory(getClientHttpRequestFactory(),interceptors));
        restTemplate.getMessageConverters().add(new MultipartBatchHttpMessageConverter());
    }
    
    private void setupHttp(ResponseEntity<?> entity) {
        if(entity == null) {
            headers = null;
            status = null;
        } else {
            headers = entity.getHeaders();
            status = entity.getStatusCode();
            setupCSRFClientToken(headers);
            if(debug) {
                Debug.debug("Response status: " + getStatus());
                Debug.debug("Response headers: " + getHeaders());
            }
        }
    }
    
    private void setupHttp(DCTMRestErrorException exception) {
        if(exception == null) {
            headers = null;
            status = null;
        } else {
            headers = exception.getHeaders();
            status = exception.getStatus();
            setupCSRFClientToken(headers);
            if(debug) {
                Debug.debug("Response status: " + getStatus());
                Debug.debug("Response headers: " + getHeaders());
            }
        }        
    }
    
    private void setupCSRFClientToken(HttpHeaders headers) {
        if(enableCSRFClientToken) {
            String cookie = headers.getFirst(HttpHeaders.SET_COOKIE);
            if(cookie != null) {
                if(CLIENT_TOKEN_NAME.equals(cookie.substring(0, cookie.indexOf('=')))) {
                    clientToken = cookie.substring(cookie.indexOf('=') + 1, cookie.indexOf(','));
                    if(debug) {
                        Debug.debug(CLIENT_TOKEN_NAME + "=" + clientToken);
                    }
                }
            }
            if(headers.containsKey(CSRF_HEADER_NAME_HEADER)) {
                csrfHeader = headers.getFirst(CSRF_HEADER_NAME_HEADER);
                if(csrfHeader != null) {
                    csrfToken = headers.getFirst(csrfHeader);
                    if(debug) {
                        Debug.debug(csrfHeader + "=" + csrfToken);
                    }
                }
            }
        }
    }
    
    private HttpHeaders setupRequestHeader(HttpHeaders headers) {
        HttpHeaders result = headers;
        if(enableCSRFClientToken && clientToken != null) {
            result = clone(headers);
            //the cookie is already processed by HttpComponentsClientHttpRequestFactory, need not to set client token header
            //otherwise, the header has to be set (or cookie)
            result.set(CLIENT_TOKEN_NAME, clientToken);
            if(debug) {
                Debug.debug("Authenticate with " + CLIENT_TOKEN_NAME + "=" + clientToken);
            }
            if(csrfHeader != null && csrfToken != null) {
                result.set(csrfHeader, csrfToken);
                if(debug) {
                    Debug.debug("Authenticate with csrf " + csrfHeader + "=" + csrfToken);
                }
            }
        } else if(username != null && password != null) {
            result = clone(headers);
            String usernameAndPassword = username + ":" + password;
            result.set(HttpHeaders.AUTHORIZATION, "Basic " + new String(Base64.encodeBase64(usernameAndPassword.getBytes())));
            if(debug) {
                Debug.debug("Authenticate with Basic " + new String(Base64.encodeBase64(usernameAndPassword.getBytes())));
            }
        }
        if(ifMatch != null) {
            result = clone(headers, result);
            result.setIfMatch(ifMatch);
            if(debug) {
                Debug.debug("Set the if-match header " + ifMatch);
            }
            ifMatch = null;
        }
        if(ifNoneMatch != null) {
            result = clone(headers, result);
            result.setIfNoneMatch(ifNoneMatch);
            if(debug) {
                Debug.debug("Set the if-none-match header " + ifNoneMatch);
            }
            ifNoneMatch = null;
        }
        return result;
    }
    
    private HttpHeaders clone(HttpHeaders headers) {
        HttpHeaders result = new HttpHeaders();
        result.putAll(headers);
        return result;
    }
    
    private HttpHeaders clone(HttpHeaders headers, HttpHeaders cloned) {
        return (cloned == null || headers == cloned)?clone(headers):cloned;
    }
        
    /**
     * send the request and process the response by Spring RestTemplate
     * @param uri the resource uri
     * @param httpMethod the HTTP method, e.g. GET, POST, DELETE...
     * @param headers the request http headers
     * @param requestBody the request http body
     * @param responseBodyClass the class to represent response
     * @param params request parameters
     * @param <T> the response type
     * @return the response
     */
    protected <T> T sendRequest(String uri,
                             HttpMethod httpMethod,
                             HttpHeaders headers,
                             Object requestBody,
                             Class<T> responseBodyClass,
                             String... params) {
        boolean noRequestProcessor = requestProcessor == null;
        if(debug && noRequestProcessor) {
            Debug.debug("Resource URI: " + uri);
            Debug.debug("HTTP method: " + httpMethod);
            Debug.debug("Request headers: " + headers);
            Debug.debugObject(requestBody);
            if(responseBodyClass != null) {
                Debug.debug("Expected response body class: " + responseBodyClass.getSimpleName());
            }
            if(params != null) {
                Debug.debug("Request parameters: " + UriHelper.queryString(params));
            }
        }
        RequestProcessor processor = noRequestProcessor ? defaultRequestProcessor : requestProcessor;
        requestProcessor = null;
        if(uri == null || uri.length() == 0) {
            throw new IllegalStateException("The resource URI is empty, or you do not have priviledge");
        }
        uri = UriHelper.decode(uri);
        if(noRequestProcessor) {
            headers = setupRequestHeader(headers);
        }
        HttpEntity<Object> requestEntity = requestBody == null ? 
                new HttpEntity<Object>(headers) :
                new HttpEntity<Object>(requestBody instanceof FutureModel?((FutureModel)requestBody).getModel():requestBody, headers);
        String requestUri = UriHelper.appendQueryString(uri, params);
        if(debug && noRequestProcessor) {
            Debug.debug("Sending " + httpMethod + " request to " + uri);
        }
        ResponseEntity<T> entity = null;
        if(enableStreaming && noRequestProcessor) {
            ClientHttpRequestFactory factory = restTemplate.getRequestFactory();
            if(factory instanceof SimpleClientHttpRequestFactory) {
                ((SimpleClientHttpRequestFactory)factory).setBufferRequestBody(false);
                if(debug) {
                    Debug.debug("Enable streaming mode for request body transfer");
                }
            } else if(factory instanceof HttpComponentsClientHttpRequestFactory) {
                ((HttpComponentsClientHttpRequestFactory)factory).setBufferRequestBody(false);
                if(debug) {
                    Debug.debug("Enable streaming mode for request body transfer");
                }
            }
        }
        try {
            entity = processor.process(requestUri, httpMethod, requestEntity, responseBodyClass);
            setupHttp(entity);
        } catch(DCTMRestErrorException e) {
            setupHttp(e);
            Debug.error(e.getError());
            throw e;
        } finally {
            if(enableStreaming && noRequestProcessor) {
                enableStreaming = false;
                ClientHttpRequestFactory factory = restTemplate.getRequestFactory();
                if(factory instanceof SimpleClientHttpRequestFactory) {
                    ((SimpleClientHttpRequestFactory)factory).setBufferRequestBody(true);
                    if(debug) {
                        Debug.debug("Disable streaming mode for request body transfer");
                    }
                } else if(factory instanceof HttpComponentsClientHttpRequestFactory) {
                    ((HttpComponentsClientHttpRequestFactory)factory).setBufferRequestBody(true);
                    if(debug) {
                        Debug.debug("Disable streaming mode for request body transfer");
                    }
                }
            }
        }
        return entity == null ? null : entity.getBody();
    }
    
    @Override
    public double getMajorVersion() {
        try {
            return Double.parseDouble((String)getProductInfo().getProperties().get("major"));
        } catch(Exception e) {
            return 7.2;
        }
    }

    /**
     * construct a new RestObject based on oldObject's class with new properties
     * @param oldObject the old object
     * @param newObject the new object
     * @return new object instance
     */
    protected RestObject newRestObject(RestObject oldObject, RestObject newObject) {
        try {
            return getModelClass(oldObject).getConstructor(RestObject.class).newInstance(newObject);
        } catch (Exception e) {
            throw new IllegalArgumentException(getModelClass(oldObject).getName());
        }
    }
    
    @Override
    public RestObject createObject(Linkable parent, RestObject objectToCreate, Object content, String contentMediaType, String... params) {
        return createObject(parent, LinkRelation.OBJECTS, objectToCreate, content, contentMediaType, params);
    }

    @Override
    public RestObject createObject(Linkable parent, RestObject objectToCreate, List<Object> contents, List<String> contentMediaTypes, String... params) {
        return createObject(parent, LinkRelation.OBJECTS, objectToCreate, contents, contentMediaTypes, params);
    }

    
    @Override
    public RestObject createObject(Linkable parent, RestObject objectToCreate, String... params) {
        return createObject(parent, LinkRelation.OBJECTS, objectToCreate, (Object)null, (String)null, params);
    }
    
    @Override
    public RestObject createObject(Linkable parent, LinkRelation rel, RestObject objectToCreate) {
        return createObject(parent, rel, objectToCreate, (Object)null, (String)null);
    }
    
    @Override
    public RestObject createDocument(Linkable parent, RestObject objectToCreate) {
        return createDocument(parent, objectToCreate, (Object)null, (String)null, null);
    }
    
    @Override
    public void uploadDistributedContent(String url, InputStream content) {
        try {
            DCContentUploader uploader = new DCContentUploader(url, content).upload();
            this.status = HttpStatus.valueOf(uploader.getStatus());
            this.headers = uploader.getHeaders();
            if(uploader.getStatus() != HttpStatus.OK.value() && uploader.getStatus() != HttpStatus.FOUND.value()) {
                RestError error;
                if(isXml()) {
                    JaxbRestError xerror = new JaxbRestError();
                    xerror.setMessage(uploader.getResponse());
                    xerror.setStatus(uploader.getStatus());
                    error = xerror;
                } else {
                    JsonRestError jerror = new JsonRestError();
                    jerror.setMessage(uploader.getResponse());
                    jerror.setStatus(uploader.getStatus());
                    error = jerror;
                }
                Debug.error(error);
                throw new DCTMRestErrorException(uploader.getHeaders(), HttpStatus.valueOf(uploader.getStatus()), error);
            }
        } catch (IOException e) {
            throw new IllegalArgumentException(e);
        }
    }

    protected <T> T get(String uri, HttpHeaders headers, Class<? extends T> responseBodyClass, String... params) {
        return sendRequest(uri, GET, headers, null, responseBodyClass, params);
    }
    
    protected <T> T get(String uri, boolean isCollection, Class<? extends T> responseBodyClass, String... params) {
        return get(uri, isXml()?(isCollection?ACCEPT_ATOM_HEADERS:ACCEPT_XML_HEADERS):ACCEPT_JSON_HEADERS, responseBodyClass, params);
    }
    
    public <T> T get(String uri, Class<T> clazz, String... params) {
        return get(uri, false, clazz, params);
    }

    public void delete(String uri, String... params) {
        sendRequest(uri, DELETE, isXml()?ACCEPT_XML_HEADERS:ACCEPT_JSON_HEADERS, null, null, params);
    }
    
    @Override
    public byte[] getContentBytes(String uri) {
        return get(uri, byte[].class);
    }
    
    @Override
    public File getContentFile(String uri, String destFilePath) {
        this.requestProcessor = new DownloadProcessor(destFilePath);
        return get(uri, File.class);
    }
    
    @Override
    public File getArchivedContents(final String destFilePath, final String objectId, final String... params) {
        String uri = getRepository().getHref(LinkRelation.ARCHIVED_CONTENTS);
        this.requestProcessor = new DownloadProcessor(destFilePath);
        return get(uri, File.class, Strings.combine(params, "object-id", objectId));
    }

    private class DownloadProcessor implements RequestProcessor {
        private final String filePath;
        DownloadProcessor(String filePath) {
            this.filePath = filePath.trim();
        }
        @SuppressWarnings("unchecked")
        @Override
        public <T> ResponseEntity<T> process(String url, HttpMethod method, final HttpEntity<?> requestEntity,
                Class<T> responseType) {
            RequestCallback requestCallback = new RequestCallback() {
                public void doWithRequest(ClientHttpRequest request) throws IOException {
                    HttpHeaders httpHeaders = request.getHeaders();
                    HttpHeaders requestHeaders = setupRequestHeader(requestEntity.getHeaders());
                    httpHeaders.putAll(requestHeaders);
                }
            };
            ResponseExtractor<ResponseEntity<File>> responseExtractor = new ResponseExtractor<ResponseEntity<File>>() {
                @Override
                public ResponseEntity<File> extractData(ClientHttpResponse response) throws IOException {
                    if(response.getStatusCode() != HttpStatus.OK) {
                        restTemplate.getErrorHandler().handleError(response);
                        return null;
                    } else {
                        File file = null;
                        File tmp = new File(filePath);
                        if(tmp.isDirectory()) {
                            String filename = Strings.getFilename(response.getHeaders().getFirst(HttpHeaders.CONTENT_DISPOSITION));
                            if(filename == null) {
                                filename = Randoms.nextString(10);
                            }
                            file = new File(filePath + filename);
                        } else if(filePath.lastIndexOf('.') == -1) {
                            String ext = Strings.getFilenameExt(response.getHeaders().getFirst(HttpHeaders.CONTENT_DISPOSITION));
                            if(ext != null) {
                                file = new File(filePath + ext);
                            }
                        }
                        if(file == null) {
                            file = new File(filePath);
                        }
                        while(file.exists()) {
                           file = new File(Strings.nextNameWithIndex(file.getAbsolutePath())); 
                        }
                        file.createNewFile();
                        OutputStream os = new BufferedOutputStream(new FileOutputStream(file));
                        FileCopyUtils.copy(response.getBody(), os);
                        return new ResponseEntity<File>(file, response.getHeaders(), response.getStatusCode());
                    }
                }
            };
            return (ResponseEntity<T>)restTemplate.execute(url, method, requestCallback, responseExtractor);
        }
    }
    
    @Override
    public void delete(Linkable linkable, String... params) {
        if(linkable.getHref(LinkRelation.DELETE) != null) {
            delete(linkable.getHref(LinkRelation.DELETE), params);
        } else if(linkable.getHref(SELF) != null) {
            delete(linkable.getHref(SELF), params);
        } else if(linkable.getHref(EDIT) != null) {
            delete(linkable.getHref(EDIT), params);
        } else {
            throw new IllegalArgumentException(String.valueOf(linkable));
        }
    }
    
    protected <T> T post(String uri, Object body, Class<? extends T> responseBodyClass, String... params) {
        return sendRequest(uri, POST, isXml()?ACCEPT_XML_HEADERS_WITH_CONTENT:ACCEPT_JSON_HEADERS_WITH_CONTENT, body, responseBodyClass, params);
    }
    
    protected <T> T post(String uri, Object body, HttpHeaders headers, Class<? extends T> responseBodyClass, String... params) {
        return sendRequest(uri, POST, headers, body, responseBodyClass, params);
    }
    
    protected <T> T post(String uri, Object content, String contentMediaType, Class<? extends T> responseBodyClass, String... params) {
        return sendRequest(uri, POST, new Headers().accept(isXml()?SupportedMediaTypes.APPLICATION_VND_DCTM_XML_VALUE:SupportedMediaTypes.APPLICATION_VND_DCTM_JSON_VALUE).contentType(contentMediaType).toHttpHeaders(),
                content, responseBodyClass, params);
    }
    
    @Override
    public <T> T put(String uri, Class<? extends T> responseBodyClass, String... params) {
        return sendRequest(uri, PUT, isXml()?ACCEPT_XML_HEADERS:ACCEPT_JSON_HEADERS, null, responseBodyClass, params);
    }
    
    @Override
    public RestObject update(RestObject oldObject, LinkRelation rel, RestObject newObject, HttpMethod method, String... params) {
        RestObject newRestObject = newRestObject(oldObject, newObject);
        if(method == PUT) {
            return put(oldObject.getHref(rel), newRestObject, getModelClass(newRestObject), params);
        } else  {
            return post(oldObject.getHref(rel), newRestObject, getModelClass(newRestObject), params);
        }
    }
    
    protected <T> T put(String uri, Object body, Class<? extends T> responseBodyClass, String... params) {
        return sendRequest(uri, PUT, isXml()?ACCEPT_XML_HEADERS_WITH_CONTENT:ACCEPT_JSON_HEADERS_WITH_CONTENT, body, responseBodyClass, params);
    }
    
    protected <T> T post(String uri, T object, Object content, String contentMediaType, Class<? extends T> responseBodyClass, String... params) {
        T t = null;
        if(content == null) {
            t = post(uri, object, responseBodyClass, params);
        } else if(object == null) {
            t = post(uri, content, contentMediaType==null?MediaType.APPLICATION_OCTET_STREAM_VALUE:contentMediaType, responseBodyClass, params);
        } else {
            MultiValueMap<String, Object> parts = new LinkedMultiValueMap<String, Object>();
            parts.add("metadata", new HttpEntity<Object>(object, isXml()?XML_CONTENT:JSON_CONTENT));
            parts.add("binary", contentMediaType==null?content:new HttpEntity<Object>(content, new Headers().contentType(contentMediaType).toHttpHeaders()));
            t = sendRequest(uri, POST, isXml()?ACCEPT_XML_HEADERS_WITH_MULTIPART_CONTENT:ACCEPT_JSON_HEADERS_WITH_MULTIPART_CONTENT, parts, responseBodyClass, params);
        }
        return t;
    }
    
    protected <T> T post(String uri, T object, List<Object> contents, List<String> contentMediaTypes, Class<? extends T> responseBodyClass, String... params) {
        T t = null;
        if(contents == null || contents.isEmpty()) {
            t = post(uri, object, responseBodyClass, params);
        } else if(contents.size() == 1) {
            t = post(uri, object, contents.get(0), contentMediaTypes!=null&&contentMediaTypes.size()>0?contentMediaTypes.get(0):null, responseBodyClass, params);
        } else {
            MultiValueMap<String, Object> parts = new LinkedMultiValueMap<String, Object>();
            if(object != null) {
                parts.add("metadata", new HttpEntity<Object>(object, isXml()?XML_CONTENT:JSON_CONTENT));
            }
            for(int i=0;i<contents.size();++i) {
                String contentMediaType = (contentMediaTypes != null && i<contentMediaTypes.size())?contentMediaTypes.get(i):null;
                parts.add("binary", contentMediaType==null?contents.get(i):new HttpEntity<Object>(contents.get(i), new Headers().contentType(contentMediaType).toHttpHeaders()));
            }
            t = sendRequest(uri, POST, isXml()?ACCEPT_XML_HEADERS_WITH_MULTIPART_CONTENT:ACCEPT_JSON_HEADERS_WITH_MULTIPART_CONTENT, parts, responseBodyClass, params);
        }
        return t;
    }
    
    protected Batch post(Batch batch, Class<? extends Batch> responseBodyClass) {
        Batch result = null;
        if(batch.hasAttachment()) {
            MultiValueMap<String, HttpEntity<InputStream>> parts = new LinkedMultiValueMap<String, HttpEntity<InputStream>>();
            HttpHeaders batchHeaders = new Headers().
                    contentType(isXml() ?
                            String.format("%s;type=\"%s\"",
                                SupportedMediaTypes.APPLICATION_XOP_STRING, SupportedMediaTypes.APPLICATION_VND_DCTM_XML_VALUE):
                            String.format("%s;type=\"%s\"",
                                SupportedMediaTypes.APPLICATION_JOP_STRING, SupportedMediaTypes.APPLICATION_VND_DCTM_JSON_VALUE)).
                    header("Content-ID", "batch").
                    header("Content-disposition", "form-data; name=batch").
                    toHttpHeaders();
            parts.add("batch", new HttpEntity<InputStream>(toInputStream(batch), batchHeaders));
            for(Operation op : batch.getOperations()) {
                if(op.getRequest().getAttachments() != null) {
                    for(Attachment a : op.getRequest().getAttachments()) {
                        addAttachment(parts, a);    
                    }
                } else if(op.getRequest().getAttachment() != null) {
                    addAttachment(parts, op.getRequest().getAttachment());
                }
            }
            HttpHeaders relatedHeaders = new Headers().accept(isXml()?
                    SupportedMediaTypes.APPLICATION_VND_DCTM_XML_VALUE :
                    SupportedMediaTypes.APPLICATION_VND_DCTM_JSON_VALUE).
                    contentType(String.format("%s;type=\"%s\";start=\"batch\";start-info=\"%s\"",
                            SupportedMediaTypes.MULTIPART_RELATED_STRING,
                            isXml() ?
                                    SupportedMediaTypes.APPLICATION_XOP_STRING :
                                    SupportedMediaTypes.APPLICATION_JOP_STRING,
                            isXml() ?
                                    SupportedMediaTypes.APPLICATION_VND_DCTM_XML_VALUE :
                                    SupportedMediaTypes.APPLICATION_VND_DCTM_JSON_VALUE)).
                    toHttpHeaders();
            result = sendRequest(getRepository().getHref(BATCHES), POST, relatedHeaders, parts, responseBodyClass);
        } else {
            result = post(getRepository().getHref(BATCHES), batch, responseBodyClass);
        }
        return result;
    }
    
    private void addAttachment(MultiValueMap<String, HttpEntity<InputStream>> parts, Attachment attachment) {
        HttpHeaders opHeaders = new Headers().
                contentType(attachment.getContentType()).
                header("Content-ID", attachment.getInclude().getRawHref()).
                header("Content-disposition", "form-data; name=" + attachment.getInclude().getRawHref()).
                toHttpHeaders();
        parts.add(attachment.getInclude().getRawHref(), new HttpEntity<InputStream>(attachment.getContentStream(), opHeaders));
    }
    
    public abstract void serialize(Object object, OutputStream os);

    private InputStream toInputStream(Object o) {
        ByteArrayOutputStream os = new ByteArrayOutputStream(50*1024);
        serialize(o, os);
        return new ByteArrayInputStream(os.toByteArray());
    }
    
    @Override
    public DCTMRestClient enableStreamingForNextRequest() {
        this.enableStreaming = true;
        return this;
    }

    public DCTMRestClient setRequestProcessor(RequestProcessor processor) {
        requestProcessor = processor;
        return this;
    }
    
    public boolean isXml() {
        return ClientType.XML == getClientType();
    }
    
    public boolean isJson() {
        return ClientType.JSON == getClientType();
    }

    public static enum ClientType {
        XML, JSON
    }
    
    public interface RequestProcessor {
        <T> ResponseEntity<T> process(String url, HttpMethod method, HttpEntity<?> requestEntity, Class<T> responseType);
    }
    
    public AbstractRestTemplateClient enableCSRFClientToken(boolean enableCSRFClientToken) {
        this.enableCSRFClientToken = enableCSRFClientToken;
        resetCSRFClientToken();
        return this;
    }
    
    public void resetCSRFClientToken() {
        this.clientToken = null;
        this.csrfHeader = null;
        this.csrfToken = null;
    }

    public AbstractRestTemplateClient ignoreAuthenticateServer() {
        //backward compatible with android httpclient 4.3.x
        if(restTemplate.getRequestFactory() instanceof HttpComponentsClientHttpRequestFactory) {
            try {
                SSLContext sslContext = new SSLContextBuilder().loadTrustMaterial(null, new TrustSelfSignedStrategy()).build();
                X509HostnameVerifier verifier = ignoreSslWarning ? new AllowAllHostnameVerifier() : new BrowserCompatHostnameVerifier();
                SSLConnectionSocketFactory socketFactory = new SSLConnectionSocketFactory(sslContext, verifier);
                HttpClient httpClient = HttpClients.custom().setSSLSocketFactory(socketFactory).build();
                ((HttpComponentsClientHttpRequestFactory)restTemplate.getRequestFactory()).setHttpClient(httpClient);
            } catch (Exception e) {
                e.printStackTrace();
            }
        } else {
            Debug.error("the request factory " + restTemplate.getRequestFactory().getClass().getName() + " does not support ignoreAuthenticateServer");
        }
        return this;
    }

    private class DefaultRequestProcessor implements RequestProcessor {
        @Override
        public <T> ResponseEntity<T> process(String url, HttpMethod method, HttpEntity<?> requestEntity, Class<T> responseType) {
            return restTemplate.exchange(url, method, requestEntity, responseType);
        }
    }
    
    protected Feed<?> feed(Linkable parent, LinkRelation rel, Class<? extends Feed<?>> clazz, String... params) {
        return get(parent.getHref(rel), true, clazz, params);
    }

    protected Feed<?> feed(LinkRelation rel, Class<? extends Feed<?>> clazz, String... params) {
        return feed(getRepository(), rel, clazz, params);
    }

    protected Repository getRepository(Class<? extends Repository> clazz) {
        if(repository == null) {
            boolean resetEnableStreaming = enableStreaming;
            Feed<Repository> repositories = getRepositories();
            for(Entry<Repository> e : repositories.getEntries()) {
                if(repositoryName.equals(e.getTitle())) {
                    repository = get(e.getContentSrc(), false, clazz);
                }
            }
            if(resetEnableStreaming) {
                enableStreaming = resetEnableStreaming;
            }
        }
        return repository;
    }
    
    protected RestObject getCabinet(String cabinet, Class<? extends RestObject> clazz, String... params) {
        RestObject obj = null; 
        Feed<RestObject> feed = getCabinets("filter", "starts-with(object_name,'" + cabinet + "')");
        List<Entry<RestObject>> entries = feed.getEntries();
        if(entries != null) {
            for(Entry<RestObject> e : (List<Entry<RestObject>>)entries) {
                if(cabinet.equals(e.getTitle())) {
                    obj = get(e.getContentSrc(), false, clazz);
                    break;
                }
            }
        }
        return obj;
    }
    
    @Override
    public <T extends Linkable> T get(T t, String... params) {
        return (T)get(t.self(), t instanceof FeedBase, getModelClass(t), params);
    }
    
    @Override
    public <T extends Linkable> Feed<T> nextPage(Feed<T> feed) {
        return page(feed.getHref(PAGING_NEXT), getModelClass(feed));
    }
    
    @Override
    public <T extends Linkable> Feed<T> previousPage(Feed<T> feed) {
        return page(feed.getHref(PAGING_PREV), getModelClass(feed));
    }

    @Override
    public <T extends Linkable> Feed<T> firstPage(Feed<T> feed) {
        return page(feed.getHref(PAGING_FIRST), getModelClass(feed));
    }

    @Override
    public <T extends Linkable> Feed<T> lastPage(Feed<T> feed) {
        return page(feed.getHref(PAGING_LAST), getModelClass(feed));
    }
    
    @SuppressWarnings("unchecked")
    protected <T> Class<T> getModelClass(T model) {
        if(model == null) {
            return null;
        }
        if(model instanceof FutureModel) {
            return (Class<T>)((FutureModel)model).getModelClass();
        }
        return (Class<T>)model.getClass();
    }
    
    @SuppressWarnings({ "unchecked", "rawtypes" })
    private <T extends Linkable> Feed<T> page(String uri, Class<? extends Feed> clazz) {
        Feed<T> feed = null;
        if(uri != null) {
            feed = get(uri, true, clazz);
        }
        return feed;
    }
}
