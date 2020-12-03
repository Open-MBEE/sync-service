package org.openmbee.syncservice.rmq.services;


import org.openmbee.syncservice.core.queue.service.Sender;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.amqp.AmqpException;
import org.springframework.amqp.rabbit.connection.CorrelationData;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.io.IOException;

import static org.openmbee.syncservice.core.constants.SyncServiceConstants.LoggerStatements.*;

@Service
public class RabbitMQSender implements Sender {

    private static final Logger logger = LoggerFactory.getLogger(RabbitMQSender.class);

    @Autowired
    private RabbitTemplate rabbitTemplate;

    @Value("${mbse.rabbitmq.exchange}")
    private String exchange;

    @Value("${mbse.rabbitmq.routingkey}")
    private String routingkey;

    public String send(String request) throws IOException, AmqpException {
        logger.debug(METHOD_START_LOG, "send()");

        setupCallbacks();
        CorrelationData correlationData = new CorrelationData("Msg Confirms");

        this.rabbitTemplate.convertAndSend(exchange, routingkey, request, correlationData);

        logger.debug(METHOD_END_LOG, "send()");
        return "Success, job queued";

    }

    private void setupCallbacks() {

        logger.debug(METHOD_START_LOG, "setupCallbacks()");

        this.rabbitTemplate.setConfirmCallback((correlation, ack, reason) -> {
            if (correlation != null) {
                logger.info(INFO_LOG, "Received " + (ack ? " ack " : " nack ") + "for correlation: " + correlation);
            }
        });

        this.rabbitTemplate.setReturnCallback((message, replyCode, replyText, exchange, routingKey) -> {
            logger.info(INFO_LOG, "Returned: " + message + "\nreplyCode: " + replyCode + "\nreplyText: " + replyText
                    + "\nexchange/rk: " + exchange + "/" + routingKey);

        });
        logger.debug(METHOD_END_LOG, "setupCallbacks()");
    }

}
