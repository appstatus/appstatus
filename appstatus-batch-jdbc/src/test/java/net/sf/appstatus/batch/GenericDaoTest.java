package net.sf.appstatus.batch;

import net.sf.appstatus.batch.jdbc.BatchDao;

import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = { "/GenericDaoTest-config.xml" })
public class GenericDaoTest {

	@Autowired
	JdbcTemplate template;

	/**
	 * Ensure database is correctly detected and created.
	 */
	@Test
	public void testDatabaseCreation() {
		BatchDao dao = new BatchDao();
		dao.setJdbcTemplate(template);

		// Database should be created
		Assert.assertTrue(dao.createDbIfNecessary());
		// Database should be detected
		Assert.assertFalse(dao.createDbIfNecessary());
	}
}
