package org.openmbee.syncservice.core.utils;

import org.openmbee.syncservice.core.constants.SyncServiceConstants;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.BodyInserters;
import org.springframework.web.reactive.function.client.WebClient;

@Component
public class RestInterface {

    private static final Logger logger = LoggerFactory.getLogger(RestInterface.class);

    private WebClient.Builder webClientBuilder;

    @Autowired
    public void setWebClientBuilder(WebClient.Builder webClientBuilder) {
        this.webClientBuilder = webClientBuilder;
    }

    public <T> T post(String uri, String token, MediaType mediaType, Object body, Class<T> responseType) {
        WebClient.ResponseSpec response =  webClientBuilder.build().post()
                .uri(uri)
                .header(SyncServiceConstants.AUTHORIZATION, token)
                .contentType(mediaType)
                .body(BodyInserters.fromValue(body)).retrieve();
        ResponseEntity<T> apiResponse = response.toEntity(responseType).block();
        if(HttpStatus.OK == apiResponse.getStatusCode()) {
            try {
                return apiResponse.getBody();
            } catch(Exception ex) {
                logger.error("Post call failed with exception", ex);
                return null;
            }
        } else {
            logger.error("Post call failed with code " + apiResponse.getStatusCode());
        }
        return null;
    }

    public <T> T get(String uri, String token, Class<T> responseType) {
        ResponseEntity<T> apiResponse = webClientBuilder.build().get().uri(uri)
                .header(SyncServiceConstants.AUTHORIZATION, token)
                .retrieve().toEntity(responseType).block();
        if(HttpStatus.OK == apiResponse.getStatusCode()) {
            try {
                return apiResponse.getBody();
            } catch(Exception ex) {
                logger.error("Get call failed with exception", ex);
                return null;
            }
        } else {
            logger.error("Get call failed with code " + apiResponse.getStatusCode());
        }
        return null;
    }

    public <T> T delete(String uri, String token, MediaType mediaType, Object body, Class<T> responseType) {
        // using method(HttpMethod.DELETE) let's us use a body for deletion, the delete() version doesn't allow that
        WebClient.ResponseSpec response = webClientBuilder.build().method(HttpMethod.DELETE)
                .uri(uri)
                .header(SyncServiceConstants.AUTHORIZATION, token)
                .contentType(mediaType)
                .body(BodyInserters.fromValue(body)).retrieve();
        ResponseEntity<T> apiResponse = response.toEntity(responseType).block();
        if(HttpStatus.OK == apiResponse.getStatusCode()) {
            try {
                return apiResponse.getBody();
            } catch(Exception ex) {
                logger.error("Delete call failed with exception", ex);
                return null;
            }
        } else {
            logger.error("Delete call failed with code " + apiResponse.getStatusCode());
        }
        return null;
    }
}
