require 'java'

java_import 'org.springframework.boot.context.embedded.AnnotationConfigEmbeddedWebApplicationContext'
java_import 'fi.aalto.ekanban.MongoConfiguration'
java_import 'fi.aalto.ekanban.PortConfiguration'

module SpringContext

  def spring_ctx
    @ctx = AnnotationConfigEmbeddedWebApplicationContext.new
    @ctx.scan("fi.aalto.ekanban")
    @ctx.getEnvironment.setActiveProfiles("test")
    @ctx.register(MongoConfiguration.java_class)
    @ctx.register(PortConfiguration.java_class)
    @ctx.refresh
    return @ctx
  end

  def spring_port
    return @ctx.getEmbeddedServletContainer.getPort
  end

end