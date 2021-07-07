package org.activiti.app.conf;

import com.fasterxml.jackson.annotation.JsonAutoDetect;
import com.fasterxml.jackson.annotation.PropertyAccessor;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.env.Environment;
import org.springframework.data.redis.connection.jedis.JedisConnectionFactory;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.data.redis.serializer.Jackson2JsonRedisSerializer;
import org.springframework.data.redis.serializer.StringRedisSerializer;
import redis.clients.jedis.JedisPoolConfig;

import javax.inject.Inject;

@Configuration
public class RedisConfiguration {
  @Inject
  private Environment env;

  @Bean
  public JedisPoolConfig jedisPoolConfig(){
      JedisPoolConfig jedisPoolConfig = new JedisPoolConfig();
      jedisPoolConfig.setMaxTotal(env.getProperty("redis.max-total",Integer.class));
      jedisPoolConfig.setMaxIdle(env.getProperty("redis.max-idle",Integer.class));
      jedisPoolConfig.setMinIdle(env.getProperty("redis.min-idle",Integer.class));
      jedisPoolConfig.setMaxWaitMillis(env.getProperty("redis.max-waitmillis",Long.class));
      jedisPoolConfig.setTestOnBorrow(true);
      jedisPoolConfig.setTestOnReturn(true);
      jedisPoolConfig.setTestWhileIdle(true);
      return jedisPoolConfig;
  }

  @Bean
  public JedisConnectionFactory jedisConnectionFactory(){
      String host = env.getProperty("redis.hostname", "localhost");
      Integer port = env.getProperty("redis.port", Integer.class);
      String password = env.getProperty("redis.password", "");
      JedisConnectionFactory jedisConnectionFactory = new JedisConnectionFactory();
      jedisConnectionFactory.setHostName(host);
      jedisConnectionFactory.setPassword(password);
      jedisConnectionFactory.setPort(port==null?6379:port);
      //jedisConnectionFactory.setTimeout();
      jedisConnectionFactory.setUsePool(true);
      jedisConnectionFactory.setPoolConfig(jedisPoolConfig());
      jedisConnectionFactory.setDatabase(6);
      //jedisConnectionFactory.setClientName();
      //jedisConnectionFactory.setConvertPipelineAndTxResults();
      return jedisConnectionFactory;
  }

  @Bean
  public StringRedisTemplate stringRedisTemplate(){
    StringRedisTemplate stringRedisTemplate = new StringRedisTemplate(jedisConnectionFactory());
    stringRedisTemplate.afterPropertiesSet();
    return stringRedisTemplate;
  }

  @Bean(name="redisTemplate")
  public RedisTemplate<String, ?> redisTemplate() {
    RedisTemplate<String,?> redisTemplate = new RedisTemplate<>();
    redisTemplate.setConnectionFactory(jedisConnectionFactory());

    StringRedisSerializer stringRedisSerializer = new StringRedisSerializer();
    redisTemplate.setKeySerializer(stringRedisSerializer);
    redisTemplate.setHashKeySerializer(stringRedisSerializer);

    // 使用Jackson2JsonRedisSerialize 替换默认序列化
    Jackson2JsonRedisSerializer jackson2JsonRedisSerializer = new Jackson2JsonRedisSerializer(Object.class);

    ObjectMapper objectMapper = new ObjectMapper();
    objectMapper.setVisibility(PropertyAccessor.ALL, JsonAutoDetect.Visibility.ANY);
    objectMapper.enableDefaultTyping(ObjectMapper.DefaultTyping.NON_FINAL);

    jackson2JsonRedisSerializer.setObjectMapper(objectMapper);

    // 设置value的序列化规则和 key的序列化规则
    redisTemplate.setKeySerializer(new StringRedisSerializer());
    redisTemplate.setValueSerializer(jackson2JsonRedisSerializer);
    redisTemplate.setHashKeySerializer(new StringRedisSerializer());
    redisTemplate.setHashValueSerializer(jackson2JsonRedisSerializer);

    redisTemplate.afterPropertiesSet();
    return redisTemplate;
  }


}
