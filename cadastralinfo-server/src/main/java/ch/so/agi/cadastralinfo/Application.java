package ch.so.agi.cadastralinfo;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.builder.SpringApplicationBuilder;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.boot.web.servlet.ServletComponentScan;
import org.springframework.boot.web.servlet.support.SpringBootServletInitializer;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.PropertySource;
import org.springframework.http.converter.HttpMessageConverter;
import org.springframework.http.converter.xml.MarshallingHttpMessageConverter;
import org.springframework.oxm.jaxb.Jaxb2Marshaller;
import org.springframework.web.filter.ForwardedHeaderFilter;

@SpringBootApplication
@ServletComponentScan
@Configuration
@PropertySource("classpath:application.yml")
//@EnableConfigurationProperties(Settings.class)
public class Application extends SpringBootServletInitializer {
  
  public static void main(String[] args) {
    SpringApplication.run(Application.class,
                          args);
  }
  
  @Override
  protected SpringApplicationBuilder configure(SpringApplicationBuilder builder) {
    return builder.sources(Application.class);
  }
  
  @Bean
  public ForwardedHeaderFilter forwardedHeaderFilter() {
      return new ForwardedHeaderFilter();
  }
  
  @Bean
  public HttpMessageConverter<Object> createXmlHttpMessageConverter(Jaxb2Marshaller marshaller) {
      MarshallingHttpMessageConverter xmlConverter = new MarshallingHttpMessageConverter();
      xmlConverter.setMarshaller(marshaller);
      xmlConverter.setUnmarshaller(marshaller);
      return xmlConverter;
  }

  @Bean
  public Jaxb2Marshaller createMarshaller() {
      Jaxb2Marshaller marshaller = new Jaxb2Marshaller();
      marshaller.setPackagesToScan("ch.ehi.oereb.schemas", "ch.so.geo.schema");
      marshaller.setSupportJaxbElementClass(true);
      marshaller.setLazyInit(true);
      return marshaller;
  }
}
