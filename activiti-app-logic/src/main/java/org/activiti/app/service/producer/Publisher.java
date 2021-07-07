package org.activiti.app.service.producer;

import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.env.Environment;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import javax.annotation.PostConstruct;
import javax.inject.Inject;

@Service
public class Publisher {
  @Inject
  private Environment env;
  @Autowired
  private RedisTemplate redisTemplate;

  private String channel;

  private static final String CHANNEL_KEY= "redis.channel";
  private static final String DEFAULT_CHANNEL = "msg-queue";

  @PostConstruct
  public void init(){
    channel = env.getProperty(CHANNEL_KEY,DEFAULT_CHANNEL);
  }

  /**
   * 消息格式 courtId:agentId:mongoId:versionId
   * @param message
   */
  public void publishMessage(String message){
    if(StringUtils.isBlank(message)) {
      return;
    }
    try {
      Thread.sleep(3000);
      redisTemplate.convertAndSend(channel,message);
    } catch (InterruptedException e) {
      e.printStackTrace();
    }
  }

  /**
   * 消息格式 courtId:agentId:mongoId:versionId
   * @param message
   */
  public void publishMessage(String message,String channel){
    if(StringUtils.isBlank(message)) {
      return;
    }
    if(StringUtils.isBlank(channel)) {
      return;
    }
    redisTemplate.convertAndSend(channel,message);
  }


}
