package org.openmbee.syncservice.core.queue.service;

import org.springframework.amqp.AmqpException;

import java.io.IOException;


public interface Sender {

    String send(String request) throws IOException, AmqpException;

}
