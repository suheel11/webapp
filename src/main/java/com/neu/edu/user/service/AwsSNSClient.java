package com.neu.edu.user.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import com.amazonaws.services.sns.AmazonSNS;
import com.amazonaws.services.sns.AmazonSNSClientBuilder;
import com.amazonaws.services.sns.model.PublishRequest;
import com.amazonaws.services.sns.model.PublishResult;

import javax.annotation.PostConstruct;

@Service("amazonSNSClient")
public class AwsSNSClient {
    @Value("${cloud.snsTopic}")
    private String snsTopic;

    private static String topicArn = "";

    private static final Logger logger = LoggerFactory.getLogger(AwsSNSClient.class);

    public void sendEmailToUser(String email) {
        logger.info("Sending mail to user using topic arn:::" + snsTopic);

        topicArn = snsTopic;

        AmazonSNS snsClient = AmazonSNSClientBuilder.defaultClient();

        final String domain = "api.prod.suheel.me";
        final String msg = email;
        final String topicarn = "arn:aws:sns:us-east-1:597569852494:user-updates-topic";
        final PublishRequest publishRequest = new PublishRequest(topicarn, msg);
        final PublishResult publishResponse = snsClient.publish(publishRequest);

        logger.info("MessageId: " + publishResponse.getMessageId());

    }
}
