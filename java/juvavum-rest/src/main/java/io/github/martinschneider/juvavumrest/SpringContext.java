package io.github.martinschneider.juvavumrest;

import com.oath.halodb.HaloDB;
import com.oath.halodb.HaloDBException;
import com.oath.halodb.HaloDBOptions;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.filter.CommonsRequestLoggingFilter;

@Configuration
public class SpringContext {
  @Bean(destroyMethod = "close")
  public HaloDB getDB() throws HaloDBException {
    return HaloDB.open("/home/martin/juvdb", new HaloDBOptions());
  }

  @Bean
  public CommonsRequestLoggingFilter logFilter() {
    CommonsRequestLoggingFilter filter = new CommonsRequestLoggingFilter();
    filter.setIncludeClientInfo(true);
    filter.setIncludeHeaders(true);
    return filter;
  }
}
