#!/bin/bash
sudo /opt/aws/amazon-cloudwatch-agent/bin/amazon-cloudwatch-agent-ctl -a fetch-config -m ec2 -c file:/home/ubuntu/cloudwatch-config.json -s

cd /home/ubuntu/target
java -jar user-0.0.1-SNAPSHOT.jar

