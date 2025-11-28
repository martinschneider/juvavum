package io.github.martinschneider.juvavumrest;

import com.oath.halodb.HaloDB;
import com.oath.halodb.HaloDBException;
import com.oath.halodb.HaloDBOptions;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.filter.CommonsRequestLoggingFilter;

@Configuration
public class SpringContext {
	// modify path as needed
	private static final String DB_PATH = "/home/martin/juvdb";

	@Bean(destroyMethod = "close")
	public HaloDB getDB() throws HaloDBException {
		return HaloDB.open(DB_PATH, new HaloDBOptions());
	}

	@Bean
	public CommonsRequestLoggingFilter logFilter() {
		CommonsRequestLoggingFilter filter = new CommonsRequestLoggingFilter();
		filter.setIncludeClientInfo(true);
		filter.setIncludeHeaders(true);
		return filter;
	}
}
