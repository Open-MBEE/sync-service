package org.openmbee.syncservice.rmq.services;

import com.rabbitmq.client.Channel;
import org.openmbee.syncservice.core.constants.SyncServiceConstants;
import org.openmbee.syncservice.core.data.services.DataService;
import org.openmbee.syncservice.core.exceptions.ProjectSyncOverlapException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.amqp.core.Message;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.amqp.support.AmqpHeaders;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.handler.annotation.Header;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.util.concurrent.TimeoutException;

import static org.openmbee.syncservice.core.constants.SyncServiceConstants.LoggerStatements.METHOD_END_LOG;
import static org.openmbee.syncservice.core.constants.SyncServiceConstants.LoggerStatements.METHOD_START_LOG;

@Service
public class RabbitMQDataServiceWrapper {

	private static final Logger logger = LoggerFactory.getLogger(RabbitMQDataServiceWrapper.class);

	private DataService dataService;

	@Autowired
	public void setDataService(DataService dataService) {
		this.dataService = dataService;
	}

	@RabbitListener(queues = "#{'${mbse.rabbitmq.queue}'}", ackMode = "MANUAL", concurrency = "#{'${mbse.rabbitmq.concurrency}'}")
	public void receiveMessage(String messageContent, Message msg, Channel channel,
							   @Header(AmqpHeaders.DELIVERY_TAG) long tag) throws IOException, TimeoutException {
		
		logger.debug(METHOD_START_LOG, "RabbitMQDataService receiveMessage()");

		try {
			dataService.receiveMessage(messageContent);
			channel.basicAck(tag, false);
		} catch(ProjectSyncOverlapException e) {
			logger.info(SyncServiceConstants.LoggerStatements.INFO_LOG, "Project is already being processed");
			channel.basicNack(tag, false, true);
		} catch (Exception e) {
			logger.error("Project processing failed unexpectedly:", e);
			channel.basicAck(tag, false);
		}

		logger.debug(METHOD_END_LOG, "RabbitMQDataService receiveMessage()");
	}
}
