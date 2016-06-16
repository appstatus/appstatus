package net.sf.appstatus.batch;

import static org.junit.Assert.assertSame;

import java.util.Properties;

import org.junit.Assert;
import org.junit.Test;

import net.sf.appstatus.core.batch.IBatch;

public class InProcessBatchManagerTest {

	/**
	 * Assert addBatch always returns the same object when using the same uuid.
	 */
	@Test
	public void testAddBatch() {
		InProcessBatchManager bm = new InProcessBatchManager();
		bm.setConfiguration(new Properties());

		IBatch b1 = bm.addBatch("name1", "group1", "1");
		IBatch b2 = bm.addBatch("name2", "group2", "1");

		assertSame(b1, b2);

	}

	@Test
	public void testLogInterval() {
		InProcessBatchManager bm = new InProcessBatchManager();
		Properties conf = new Properties();

		conf.setProperty("batch.logInterval", "20000");
		bm.setConfiguration(conf);

		IBatch b1 = bm.addBatch("name1", "group1", "1");
		InProcessBatchProgressMonitor pm = (InProcessBatchProgressMonitor) bm.getMonitor(b1);
		Assert.assertEquals(20000, pm.getWritingDelay());

	}
}
