package com.neu.edu.user.service;

import com.amazonaws.auth.DefaultAWSCredentialsProviderChain;
import com.amazonaws.auth.InstanceProfileCredentialsProvider;
import com.amazonaws.regions.Regions;
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
    //@Value("user-updates-topic")
    //private AmazonSNS snsClient;

    private Logger logger = LoggerFactory.getLogger(AwsSNSClient.class);

//    @PostConstruct
//    private void init() {
//        logger.info("Inside init sns client");
//
//    }

    public void sendEmailToUser(String message) {
        //this.snsClient =  AmazonSNSClientBuilder.standard().withRegion(Regions.US_EAST_1).withCredentials(DefaultAWSCredentialsProviderChain.getInstance()).build();
        //InstanceProfileCredentialsProvider provider = new InstanceProfileCredentialsProvider(true);
        AmazonSNS snsClient=  AmazonSNSClientBuilder.defaultClient();
        PublishRequest request = new PublishRequest("arn:aws:sns:us-east-1:597569852494:user-updates-topic", message);
        logger.info("AmazonSNSClientClass- Published Request : " + request.toString() + "--------");
        try{
            PublishResult result = snsClient.publish(request);
            logger.info("result----------"+result.toString());
        }catch (Exception e) {
            logger.error((e.getMessage()));
        }

//        final PublishRequest publishRequest = new PublishRequest("arn:aws:sns:us-east-1:597569852494:user-updates-topic", message);
//        logger.info("AmazonSNSClientClass- Published Request : " + publishRequest.toString() + "----");
//        try{
//            final PublishResult publishResponse = snsClient.publish(publishRequest);
//            logger.info("AmazonSNSClientClass- Published message with messageId :- " + publishResponse.getMessageId());
//        }
    }

}
