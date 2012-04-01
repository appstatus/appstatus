package net.sf.appstatus.batch;

import static org.junit.Assert.assertSame;
import net.sf.appstatus.core.batch.IBatch;

import org.junit.Test;

public class InProcessBatchManagerTest {

	/**
	 * Assert addBatch always returns the same object when using the same uuid.
	 */
	@Test
	public void testAddBatch() {
		InProcessBatchManager bm = new InProcessBatchManager();

		IBatch b1 = bm.addBatch("name1", "group1", "1");
		IBatch b2 = bm.addBatch("name2", "group2", "1");

		assertSame(b1, b2);

	}
}
