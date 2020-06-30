/*
 * Copyright (c) 2018. Open Text Corporation. All Rights Reserved.
 */
package com.emc.documentum.rest.client.sample.client;

import java.lang.reflect.Proxy;
import java.util.ArrayList;
import java.util.List;

import com.emc.documentum.rest.client.sample.client.impl.AbstractRestTemplateClient;
import com.emc.documentum.rest.client.sample.client.impl.ClientAsyncRestTemplateClient;
import com.emc.documentum.rest.client.sample.client.impl.jackson.DCTMJacksonClient;
import com.emc.documentum.rest.client.sample.client.impl.jaxb.DCTMJaxbClient;
import com.emc.documentum.rest.client.sample.client.util.Debug;
import com.emc.documentum.rest.client.sample.model.Entry;
import com.emc.documentum.rest.client.sample.model.Feed;
import com.emc.documentum.rest.client.sample.model.Repository;

import static com.emc.documentum.rest.client.sample.client.util.Reader.read;

/**
 * build a DCTMRestClient with parameters
 */
public final class DCTMRestClientBuilder {
    private String contextRoot;
    private String repository;
    private String username;
    private String password;
    private DCTMRestClientBinding binding;
    private boolean useFormatExtension;
    private boolean debug;
    private boolean ignoreSslWarning;
    private boolean ignoreAuthenticateServer;
    
    /**
     * build the DCTMRestClient with the prompt
     * @return the DCTMRestClient
     */
    public static DCTMRestClient buildWithPrompt() {
        StringBuilder sb = new StringBuilder();
        sb.append("Please input the client representation type: ");
        for(DCTMRestClientBinding b : DCTMRestClientBinding.values()) {
            sb.append(b.name() + " ");
        }
        String bindingStr = read(sb.toString(), "XML");
        DCTMRestClientBinding binding = DCTMRestClientBinding.valueOf(bindingStr.toUpperCase());
        String contextRoot = read("Please input the REST context path:", "http://localhost:8080/dctm-rest");
        boolean ignoreAuthenticateServer = ignoreAuthenticateServer(contextRoot);
        boolean ignoreSslWarning = false;
        if(ignoreAuthenticateServer) {
            ignoreSslWarning = read("Please input whether allow all host:", false);
        }
        List<String> repositories = getRepositories(binding, contextRoot, ignoreAuthenticateServer, ignoreSslWarning);
        String repository = read("Please input the repository name: " + repositories, repositories.get(0));
        String username = read("Please input the username:");
        String password = read("Please input the password:");
        boolean useFormatExtension = read("Please input the whether add format extension .xml or .json for URI:", false);
        String debug = read("Please input whether print debug information:", "false");
        AbstractRestTemplateClient client = (AbstractRestTemplateClient)new DCTMRestClientBuilder().
                   bind(binding).
                   contextRoot(contextRoot).
                   ignoreAuthenticateServer(ignoreAuthenticateServer).
                   ignoreSslWarning(ignoreSslWarning).
                   credentials(username, password).
                   repository(repository).
                   useFormatExtension(useFormatExtension).
                   debug("true".equalsIgnoreCase(debug)).build();
        client.getHomeDocument();
        if(client.getMajorVersion() >= 7.2) {
            String msg = client.getMajorVersion() >= 7.3?"Please input whether enable the client token authentication with the server based CSRF:":"Please input whether enable the client token authentication:";
            boolean enableCSRFClientToken = "true".equalsIgnoreCase(read(msg, "true"));
            client.enableCSRFClientToken(enableCSRFClientToken);
            if("true".equalsIgnoreCase(debug)) {
                Debug.debug("Enable CSRF Client Token=" + enableCSRFClientToken);
            }
        }
        return client;
    }
    
    /**
     * build the DCTMRestClient without the prompt
     * @return the DCTMRestClient
     */
    public static DCTMRestClient buildSilently(DCTMRestClientBinding binding, String contextRoot, String repository, String username, String password) {
        return buildSilently(binding, contextRoot, repository, username, password, false, false, false);
    }

    /**
     * build the DCTMRestClient without the prompt
     * @return the DCTMRestClient
     */
    public static DCTMRestClient buildSilently(DCTMRestClientBinding binding, String contextRoot, String repository, String username, String password,
            boolean useFormatExtension, boolean debug, boolean enableCSRFClientToken) {
        boolean ignoreAuthenticateServer = ignoreAuthenticateServer(contextRoot);
        AbstractRestTemplateClient client = (AbstractRestTemplateClient)new DCTMRestClientBuilder().
                   bind(binding).
                   contextRoot(contextRoot).
                   ignoreAuthenticateServer(ignoreAuthenticateServer).
                   credentials(username, password).
                   repository(repository).
                   useFormatExtension(useFormatExtension).
                   debug(debug).build();
        client.getHomeDocument();
        if(client.getMajorVersion() >= 7.2) {
            client.enableCSRFClientToken(enableCSRFClientToken);
            if(debug) {
                Debug.debug("Enable CSRF Client Token=" + enableCSRFClientToken);
            }
        }
        return client;
    }
    
    /**
     * build the async DCTMRestClient
     * @return the DCTMRestClient
     */
    public static DCTMRestClient buildAsyncClient(DCTMRestClient client) {
        if(!(client instanceof AbstractRestTemplateClient)) {
            throw new UnsupportedOperationException(client.getClass().getName());
        }
        return (DCTMRestClient)Proxy.newProxyInstance(DCTMRestClient.class.getClassLoader(), new Class[]{DCTMRestClient.class}, new ClientAsyncRestTemplateClient(100, client));
    }
    
    private static List<String> getRepositories(DCTMRestClientBinding binding, String contextRoot, boolean ignoreAuthenticateServer, boolean ignoreSslWarning) {
        DCTMRestClient client = new DCTMRestClientBuilder().bind(binding).contextRoot(contextRoot).
                ignoreAuthenticateServer(ignoreAuthenticateServer).ignoreSslWarning(ignoreSslWarning).build();
        Feed<Repository> repositories = client.getRepositories();
        List<String> list = new ArrayList<>();
        for(Entry<Repository> r : repositories.getEntries()) {
            list.add(r.getTitle());
        }
        return list;
    }
    
    private static boolean ignoreAuthenticateServer(String contextRoot) {
        if("https".equalsIgnoreCase(contextRoot.substring(0, 5))) {
            if(System.getProperty("javax.net.ssl.trustStore") == null) {
                String useTrustStore = read("Please input whether authenticate the rest server:", "false");
                if("true".equalsIgnoreCase(useTrustStore)) {
                    if(System.getProperty("javax.net.ssl.trustStore") == null) {
                        String trustStore = read("Please input the trust store path:");
                        String password = read("Please input the password of the trust store:");
                        System.setProperty("javax.net.ssl.trustStore", trustStore);
                        System.setProperty("javax.net.ssl.trustStorePassword", password);
                    }
                } else {
                    return true;
                }
            } else {
                return true;
            }
        }
        return false;
    }
    
    /**
     * the context root the REST service
     * e.g.
     * http://localhost:8080/dctm-rest
     * @param contextRoot the contextRoot of the rest
     * @return the DCTMRestClientBuilder itself
     */
    public DCTMRestClientBuilder contextRoot(String contextRoot) {
        this.contextRoot = contextRoot.endsWith("/")?contextRoot.substring(0, contextRoot.length()-1):contextRoot;
        return this;
    }
    
    /**
     * @param repository the repository name
     * @return the DCTMRestClientBuilder itself
     */
    public DCTMRestClientBuilder repository(String repository) {
        this.repository = repository;
        return this;
    }
    
    /**
     * @param binding binding type
     * @return the DCTMRestClientBuilder itself
     */
    public DCTMRestClientBuilder bind(DCTMRestClientBinding binding) {
        this.binding = binding;
        return this;
    }

    /**
     * Ignore SSL certificate warning even when the certificate is untrusted. This should be used only in test environment.
     * @param ignoreSslWarning boolean to indicate ignoring the ssl warning
     * @return
     */
    public DCTMRestClientBuilder ignoreSslWarning(boolean ignoreSslWarning) {
        this.ignoreSslWarning = ignoreSslWarning;
        return this;
    }

    /**
     * whether append .xml or .json extension to the uri
     * e.g.
     * http://localhost:8080/dctm-rest/services.xml
     * or
     * http://localhost:8080/dctm-rest/services.json
     * @param useFormatExtension whether use format extension
     * @return the DCTMRestClientBuilder itself
     */
    public DCTMRestClientBuilder useFormatExtension(boolean useFormatExtension) {
        this.useFormatExtension = useFormatExtension;
        return this;
    }
    
    /**
     * whether print debug information
     * @param debug whether print debug information
     * @return the DCTMRestClientBuilder itself
     */
    public DCTMRestClientBuilder debug(boolean debug) {
        this.debug = debug;
        return this;
    }
    
    /**
     * @param username the user name
     * @param password the password
     * @return the DCTMRestClientBuilder itself
     */
    public DCTMRestClientBuilder credentials(String username, String password) {
        this.username = username;
        this.password = password;
        return this;
    }
    
    /**
     * @param ignoreAuthenticateServer whether ignore authenticate the server for ssl
     * @return the DCTMRestClientBuilder itself
     */
    public DCTMRestClientBuilder ignoreAuthenticateServer(boolean ignoreAuthenticateServer) {
        this.ignoreAuthenticateServer = ignoreAuthenticateServer;
        return this;
    }
    
    /**
     * build up the DCTMRestClient
     * @return DCTMRestClient instance
     */
    public DCTMRestClient build() {
        AbstractRestTemplateClient client = null;
        switch(binding) {
            case XML:
                if(debug) {
                    Debug.debug("Build DCTMRestClient with JAXB implementation");
                }
                client = new DCTMJaxbClient(contextRoot, repository, username, password, useFormatExtension, ignoreSslWarning);
                break;
            case JSON:
                if(debug) {
                    Debug.debug("Build DCTMRestClient with Jackson implementation");
                }
                client = new DCTMJacksonClient(contextRoot, repository, username, password, useFormatExtension, ignoreSslWarning);
                break;
            default:
                throw new IllegalArgumentException(binding + " binding is not supported yet");    
        }
        client.debug(debug);
        if(ignoreAuthenticateServer) {
            client.ignoreAuthenticateServer();
        }
        if(debug) {
            Debug.debug("Context Root=" + contextRoot);
            Debug.debug("Repository=" + repository);
            Debug.debug("User Name=" + username);
            Debug.debug("Password=" + password);
            Debug.debug("Use Format Extension=" + useFormatExtension);
        }
        return client;
    }
}
