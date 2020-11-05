package org.openmbee.syncservice.core.queue.service;

import java.io.IOException;

import org.springframework.amqp.AmqpException;


public interface Sender {

    String send(String request) throws IOException, AmqpException;

}
