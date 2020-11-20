package org.openmbee.syncservice.rmq.services;

import org.openmbee.syncservice.core.constants.SyncServiceConstants;
import org.openmbee.syncservice.core.queue.dto.QueueDetailsResponseTO;
import org.openmbee.syncservice.core.queue.dto.QueueDetailsTO;
import org.openmbee.syncservice.core.queue.service.QueuingService;
import org.openmbee.syncservice.core.utils.SyncServiceUtil;
import org.openmbee.syncservice.core.utils.WebClientUtils;
import org.openmbee.syncservice.rmq.RabbitMQConfig;
import org.openmbee.syncservice.rmq.RabbitMQConstants;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.reactive.function.client.WebClient;

import java.net.URI;

import static org.openmbee.syncservice.core.constants.SyncServiceConstants.LoggerStatements.ERROR_LOG;

@Service
public class RabbitMQQueuingService extends QueuingService {

    private static final Logger logger = LoggerFactory.getLogger(RabbitMQQueuingService.class);

    private RabbitTemplate rabbitTemplate;
    private RabbitMQConfig appConfig;
    private WebClient.Builder webClientBuilder; //TODO look into switching to WebClient
    private WebClientUtils webClientUtils;


    @Autowired
    public void setWebClientBuilder(WebClient.Builder webClientBuilder) {
        this.webClientBuilder = webClientBuilder;
    }

    @Autowired
    public void setRabbitTemplate(RabbitTemplate rabbitTemplate) {
        this.rabbitTemplate = rabbitTemplate;
    }

    @Autowired
    public void setAppConfig(RabbitMQConfig appConfig) {
        this.appConfig = appConfig;
    }

    @Autowired
    public void setWebClientUtils(WebClientUtils webClientUtils) {
        this.webClientUtils = webClientUtils;
    }

    @Override
    public QueueDetailsResponseTO getStatus() {
        ResponseEntity<QueueDetailsTO> responseEntity = null;

        String vhost = this.rabbitTemplate.getConnectionFactory().getVirtualHost();
        if (vhost == "/") {
            vhost = "%2F";
        }

        String uri = String.format("%s%s/%s", SyncServiceUtil.getPropertyValue(RabbitMQConstants.RMQ_HTTP_API_QUEUES_URL)
                , vhost, SyncServiceUtil.getPropertyValue(RabbitMQConstants.QUEUE_NAME));

        URI rmqUri = webClientUtils.getUri(uri);

        QueueDetailsResponseTO queueStatusTO = null;
        try {
            String encodedAuthHeader = webClientUtils.getBasicAuthHeader(SyncServiceUtil.getPropertyValue(RabbitMQConstants.RMQ_USER_NAME),
                    SyncServiceUtil.getPropertyValue(RabbitMQConstants.RMQ_PASSWD));

            QueueDetailsTO queueDetailsTo = webClientBuilder.build().get().uri(rmqUri)
                    .header(SyncServiceConstants.AUTHORIZATION, encodedAuthHeader)
                    .retrieve().bodyToMono(QueueDetailsTO.class).block();

            queueStatusTO = new QueueDetailsResponseTO(queueDetailsTo);
        } catch (HttpClientErrorException ex) {
            logger.error(ERROR_LOG, "Exception while calling exchange() is : " + ex.getMessage());
            throw ex;
        }
        return queueStatusTO;
    }
}
