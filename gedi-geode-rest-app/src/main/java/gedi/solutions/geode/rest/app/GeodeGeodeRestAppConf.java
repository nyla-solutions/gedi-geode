package gedi.solutions.geode.rest.app;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import gedi.solutions.geode.client.GeodeClient;
import gedi.solutions.geode.io.QuerierService;

@Configuration
public class GeodeGeodeRestAppConf
{
	@Bean
	Gson gson()
	{
		return new GsonBuilder().create();
	}
	
	
	@Bean
	GeodeClient geodeClient()
	{
		return GeodeClient.connect();
	}
	
	@Bean
	QuerierService querierService()
	{
		return GeodeClient.connect().getQuerierService();
	}

}
