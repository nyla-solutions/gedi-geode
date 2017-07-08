package gedi.solutions.geode.functions;

import java.util.Set;

import org.apache.geode.cache.Region;
import org.apache.geode.cache.execute.Execution;
import org.apache.geode.cache.execute.ResultCollector;
import org.junit.Assert;
import org.junit.Test;
import org.mockito.Mockito;

/**
 * @author Gregory Green testing JvmExecution
 */
public class JvmExecutionTest {

    @Test
    public void test_withFilter(){

        Region<?,?> region = Mockito.mock(Region.class);
        JvmExecution jvm = new JvmExecution(region);

        Set<?> set = Mockito.mock(Set.class);
        Execution exe = jvm.withFilter(set);

        Assert.assertEquals(jvm,exe);

    }

    @Test
    public void test_withArgs(){

        Region<?,?> region = Mockito.mock(Region.class);
        JvmExecution jvm = new JvmExecution(region);

        Execution exe = jvm.withArgs(Mockito.anySet());

        Assert.assertEquals(jvm,exe);

    }
}
