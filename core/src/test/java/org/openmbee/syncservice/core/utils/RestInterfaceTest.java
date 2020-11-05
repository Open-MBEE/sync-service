package org.openmbee.syncservice.core.utils;

import org.junit.Before;
import org.junit.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.mockito.Spy;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

public class RestInterfaceTest {

    @Mock
    private WebClient.Builder webClientBuilder;
    @Mock
    private WebClient client;
    @Mock
    private WebClient.RequestBodyUriSpec uriSpec;
    @Mock
    private WebClient.RequestHeadersUriSpec headersUriSpec;
    @Mock
    private WebClient.RequestHeadersSpec headersSpec;
    @Mock
    private WebClient.ResponseSpec responseSpec;
    @Mock
    private Mono mono;
    @Mock
    private ResponseEntity response;

    @Spy
    @InjectMocks
    private RestInterface restInterface;

    @Before
    public void setUp() {
        MockitoAnnotations.initMocks(this);
    }

    @Test
    public void postTestNormal() {
        when(webClientBuilder.build()).thenReturn(client);
        when(client.post()).thenReturn(uriSpec);
        when(uriSpec.uri(anyString())).thenReturn(uriSpec);
        when(uriSpec.header(anyString(),anyString())).thenReturn(uriSpec);
        when(uriSpec.contentType(any())).thenReturn(uriSpec);
        when(uriSpec.body(any())).thenReturn(headersSpec);
        when(headersSpec.retrieve()).thenReturn(responseSpec);
        when(responseSpec.toEntity(any(Class.class))).thenReturn(mono);
        when(mono.block()).thenReturn(response);
        when(response.getStatusCode()).thenReturn(HttpStatus.OK);
        when(response.getBody()).thenReturn("It works");

        String r = restInterface.post("something.com", "mytoken", MediaType.APPLICATION_JSON, new Object(), String.class);
        assertEquals("It works", r);
    }

    @Test
    public void postTestNotFound() {
        when(webClientBuilder.build()).thenReturn(client);
        when(client.post()).thenReturn(uriSpec);
        when(uriSpec.uri(anyString())).thenReturn(uriSpec);
        when(uriSpec.header(anyString(),anyString())).thenReturn(uriSpec);
        when(uriSpec.contentType(any())).thenReturn(uriSpec);
        when(uriSpec.body(any())).thenReturn(headersSpec);
        when(headersSpec.retrieve()).thenReturn(responseSpec);
        when(responseSpec.toEntity(any(Class.class))).thenReturn(mono);
        when(mono.block()).thenReturn(response);
        when(response.getStatusCode()).thenReturn(HttpStatus.NOT_FOUND);

        String r = restInterface.post("something.com", "mytoken", MediaType.APPLICATION_JSON, new Object(), String.class);
        assertNull(r);

        verify(response, times(0)).getBody();
    }

    @Test
    public void postTestThrows() {
        when(webClientBuilder.build()).thenReturn(client);
        when(client.post()).thenReturn(uriSpec);
        when(uriSpec.uri(anyString())).thenReturn(uriSpec);
        when(uriSpec.header(anyString(),anyString())).thenReturn(uriSpec);
        when(uriSpec.contentType(any())).thenReturn(uriSpec);
        when(uriSpec.body(any())).thenReturn(headersSpec);
        when(headersSpec.retrieve()).thenReturn(responseSpec);
        when(responseSpec.toEntity(any(Class.class))).thenReturn(mono);
        when(mono.block()).thenReturn(response);
        when(response.getStatusCode()).thenReturn(HttpStatus.OK);
        when(response.getBody()).thenThrow(new RuntimeException());

        String r = restInterface.post("something.com", "mytoken", MediaType.APPLICATION_JSON, new Object(), String.class);
        assertNull(r);
    }

    @Test
    public void getTestNormal() {
        when(webClientBuilder.build()).thenReturn(client);
        when(client.get()).thenReturn(headersUriSpec);
        when(headersUriSpec.uri(anyString())).thenReturn(headersUriSpec);
        when(headersUriSpec.header(anyString(),anyString())).thenReturn(headersUriSpec);
        when(headersUriSpec.retrieve()).thenReturn(responseSpec);
        when(responseSpec.toEntity(any(Class.class))).thenReturn(mono);
        when(mono.block()).thenReturn(response);
        when(response.getStatusCode()).thenReturn(HttpStatus.OK);
        when(response.getBody()).thenReturn("It works");

        String r = restInterface.get("something.com", "mytoken", String.class);
        assertEquals("It works", r);
    }

    @Test
    public void getTestNotFound() {
        when(webClientBuilder.build()).thenReturn(client);
        when(client.get()).thenReturn(headersUriSpec);
        when(headersUriSpec.uri(anyString())).thenReturn(headersUriSpec);
        when(headersUriSpec.header(anyString(),anyString())).thenReturn(headersUriSpec);
        when(headersUriSpec.retrieve()).thenReturn(responseSpec);
        when(responseSpec.toEntity(any(Class.class))).thenReturn(mono);
        when(mono.block()).thenReturn(response);
        when(response.getStatusCode()).thenReturn(HttpStatus.NOT_FOUND);
        when(response.getBody()).thenReturn("It works");

        String r = restInterface.get("something.com", "mytoken", String.class);
        assertNull(r);

        verify(response, times(0)).getBody();
    }

    @Test
    public void getTestThrows() {
        when(webClientBuilder.build()).thenReturn(client);
        when(client.get()).thenReturn(headersUriSpec);
        when(headersUriSpec.uri(anyString())).thenReturn(headersUriSpec);
        when(headersUriSpec.header(anyString(),anyString())).thenReturn(headersUriSpec);
        when(headersUriSpec.retrieve()).thenReturn(responseSpec);
        when(responseSpec.toEntity(any(Class.class))).thenReturn(mono);
        when(mono.block()).thenReturn(response);
        when(response.getStatusCode()).thenReturn(HttpStatus.OK);
        when(response.getBody()).thenThrow(new RuntimeException());

        String r = restInterface.get("something.com", "mytoken", String.class);
        assertNull(r);

    }
}