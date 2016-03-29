package net.sf.appstatus.batch;

import static org.junit.Assert.assertSame;

import java.util.Properties;
import java.util.UUID;

import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import net.sf.appstatus.batch.jdbc.JdbcBatchManager;
import net.sf.appstatus.batch.jdbc.JdbcBatchProgressMonitor;
import net.sf.appstatus.core.batch.IBatch;
import net.sf.appstatus.core.batch.IBatchManager;

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
	
	/**
	 * Test batch.logInterval configuration.
	 * <p>
	 * Note : Batch Manager configuration is modified after run.
	 */
	@Test
	public void testLogInterval() {
		Properties conf = new Properties();
		conf.setProperty("batch.logInterval", "20000");
		batchManager.setConfiguration(conf);

		IBatch b1 = batchManager.addBatch("name1", "group1", "1");
		JdbcBatchProgressMonitor pm = (JdbcBatchProgressMonitor) batchManager.getMonitor(b1);
		Assert.assertEquals(20000, pm.getWritingDelay());

	}
}
