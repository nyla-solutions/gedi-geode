package gedi.solutions.geode.rest.app;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import gedi.solutions.geode.client.GeodeClient;
import gedi.solutions.geode.io.QuerierService;

@Configuration
public class GeodeGeodeRestAppConf
{
	
	@Bean
	QuerierService querierService()
	{
		return GeodeClient.connect().getQuerierService();
	}

}
