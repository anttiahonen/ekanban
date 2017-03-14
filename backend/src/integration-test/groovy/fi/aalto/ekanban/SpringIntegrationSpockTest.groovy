package fi.aalto.ekanban

import org.springframework.beans.factory.annotation.Value
import org.springframework.boot.test.SpringApplicationContextLoader
import org.springframework.boot.test.WebIntegrationTest
import org.springframework.test.context.ContextConfiguration
import spock.lang.Specification
import spock.lang.Stepwise

@ContextConfiguration(loader = SpringApplicationContextLoader.class, classes = [Application.class] )
@WebIntegrationTest(randomPort = true)
@Stepwise
class SpringIntegrationSpockTest extends Specification {

    @Value('${local.server.port}')
    int port

}
