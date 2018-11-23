package gedi.geode.extensions.rest

import org.junit.Test
import org.junit.runner.RunWith
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.test.context.junit4.SpringRunner
import gedi.geode.extensions.rest.AppConfig
import org.junit.Before
import gedi.solutions.geode.qa.GUnit
import org.apache.geode.cache.RegionShortcut
import org.junit.AfterClass
import org.junit.BeforeClass

//@RunWith(SpringRunner::class)
//@SpringBootTest
class GediGeodeExtensionsRestApplicationTests {

	companion object {
		
		@JvmStatic
		val gunit = GUnit()
		
		@BeforeClass
		@JvmStatic
		fun   setUp() 
		{
			gunit.startCluster()
			gunit.createRegion("test",RegionShortcut.PARTITION);
			
		}
		
		@AfterClass
		@JvmStatic
		fun shutdown()
		{
			gunit.shutdown();
		}
	}
	
	//@Test
	fun contextLoads() {
		AppConfig()
	}

}
