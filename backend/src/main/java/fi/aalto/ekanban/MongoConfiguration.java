package fi.aalto.ekanban;

import com.mongodb.*;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;
import org.springframework.data.mongodb.config.AbstractMongoConfiguration;
import org.springframework.data.mongodb.repository.config.EnableMongoRepositories;
import static java.util.Collections.singletonList;

@EnableMongoRepositories(basePackages="fi.aalto.ekanban")
@Configuration
@Profile("test")
public class MongoConfiguration extends AbstractMongoConfiguration {

    @Override
    public String getDatabaseName() {
        return "ekanban-test";
    }

    @Override
    @Bean
    public Mongo mongo() throws Exception {
        return new MongoClient(singletonList(new ServerAddress("0.0.0.0", 27017)),
                singletonList(MongoCredential.createCredential("ekanban", "ekanban-test", "nabnake".toCharArray())));
    }
}
