package org.activiti.app.conf;

import com.mongodb.MongoClient;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.env.Environment;
import org.springframework.data.authentication.UserCredentials;
import org.springframework.data.mapping.context.MappingContext;
import org.springframework.data.mongodb.MongoDbFactory;
import org.springframework.data.mongodb.core.SimpleMongoDbFactory;
import org.springframework.data.mongodb.core.convert.DbRefResolver;
import org.springframework.data.mongodb.core.convert.DefaultDbRefResolver;
import org.springframework.data.mongodb.core.convert.DefaultMongoTypeMapper;
import org.springframework.data.mongodb.core.convert.MappingMongoConverter;
import org.springframework.data.mongodb.core.mapping.MongoMappingContext;
import org.springframework.data.mongodb.core.mapping.MongoPersistentEntity;
import org.springframework.data.mongodb.core.mapping.MongoPersistentProperty;
import org.springframework.data.mongodb.gridfs.GridFsTemplate;

import javax.inject.Inject;
import java.net.UnknownHostException;

@Configuration
public class StoreModelConguration {
  @Inject
  private Environment env;

  @Bean
  public MongoClient mongoClient() throws UnknownHostException {
    String hostname = env.getProperty("mongo.hostname", "localhost");
    Integer port = env.getProperty("mongo.port", Integer.class);
    MongoClient mongoClient = new MongoClient(hostname,port==null?20017:port);
    return mongoClient;
  }

  @Bean
  public MongoDbFactory mongoDbFactory() throws UnknownHostException {
    String dbname = env.getProperty("mongo.db", "model");
    String username = env.getProperty("mongo.username", "");
    String password = env.getProperty("mongo.password", "");
    UserCredentials userCredentials = new UserCredentials(username,password);
    return new SimpleMongoDbFactory(mongoClient(),dbname,userCredentials);
  }

  @Bean
  public MappingContext<? extends MongoPersistentEntity<?>, MongoPersistentProperty> mappingContext(){
    return new MongoMappingContext();
  }
  /**
   *
   * @return
   */
  @Bean
  public MappingMongoConverter mappingMongoConverter() throws UnknownHostException {

    DbRefResolver dbRefResolver = new DefaultDbRefResolver(mongoDbFactory());
    MappingMongoConverter mappingConverter = new MappingMongoConverter(dbRefResolver, mappingContext());

    // Don't save _class to mongo
    mappingConverter.setTypeMapper(new DefaultMongoTypeMapper(null));

    return mappingConverter;
  }

  @Bean
  public GridFsTemplate gridFsTemplate() throws Exception{
    return new GridFsTemplate(mongoDbFactory(),mappingMongoConverter());
  }
}
