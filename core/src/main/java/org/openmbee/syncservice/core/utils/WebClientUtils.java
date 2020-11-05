package org.openmbee.syncservice.core.utils;


import org.apache.commons.codec.binary.Base64;
import org.springframework.stereotype.Component;

import java.net.URI;
import java.nio.charset.Charset;

@Component
public class WebClientUtils {
    //TODO this should be absorbed into RestInterface

    public String getBasicAuthHeader(String username, String password) {
        String auth = username + ":" + password;
        Base64 base64 = new Base64();
        byte[] encodedAuth = base64.encode(auth.getBytes(Charset.forName("US-ASCII")));
        String authHeader = "Basic " + new String(encodedAuth);
        return authHeader;
    }

    public URI getUri(String uri){
        if(uri != null && uri.isEmpty()){
            return URI.create(uri);
        }
        return null;
    }
}
