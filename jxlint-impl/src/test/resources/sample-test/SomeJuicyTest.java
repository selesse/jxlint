import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class SomeJuicyTest {
    private static final Logger logger = LoggerFactory.getLogger(SomeJuicyTest.class);

    public void testThisShouldFail() {
        // This is no good, there should be an @Test
        logger.debug(String.format("This is %s", bad));
    }

    public void testThisShouldFailToo() {
        // This is no good, there should be an @Test
    }

    @Test
    public void testThisShouldBeOk() {
        // This is no good, there should be an @Junit
    }
}
