package net.sf.appstatus.batch;

import static org.junit.Assert.assertSame;

import java.util.UUID;

import net.sf.appstatus.core.batch.IBatch;
import net.sf.appstatus.core.batch.IBatchManager;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = { "/appstatus-jdbc-test-config.xml" })
public class JdbcBatchManagerTest {

	@Autowired
	IBatchManager batchManager;

	/**
	 * Assert addBatch always returns the same object when using the same uuid.
	 */
	@Test
	public void testAddBatch() {
		String uuid = UUID.randomUUID().toString();
		IBatch b1 = batchManager.addBatch("name1", "group1", uuid);
		IBatch b2 = batchManager.addBatch("name2", "group2", uuid);

		assertSame(b1, b2);
	}
}
