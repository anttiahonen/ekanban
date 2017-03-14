require 'java'
require 'singleton'

java_import 'org.springframework.boot.context.embedded.AnnotationConfigEmbeddedWebApplicationContext'

java_import 'fi.aalto.ekanban.MongoConfiguration'
java_import 'fi.aalto.ekanban.PortConfiguration'

class SpringContext
  include Singleton

  def initialize
    @ctx = AnnotationConfigEmbeddedWebApplicationContext.new
    @ctx.scan("fi.aalto.ekanban")
    @ctx.getEnvironment.setActiveProfiles("test")
    @ctx.register(MongoConfiguration.java_class)
    @ctx.register(PortConfiguration.java_class)
    @ctx.refresh
  end

  def spring_ctx
    @ctx
  end

  def spring_port
    @ctx.getEmbeddedServletContainer.getPort
  end

end