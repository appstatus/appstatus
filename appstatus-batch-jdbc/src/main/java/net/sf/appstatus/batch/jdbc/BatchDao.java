package net.sf.appstatus.batch.jdbc;

import java.util.ArrayList;
import java.util.List;

import net.sf.appstatus.core.batch.IBatch;

import org.joda.time.DateTime;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.dao.DataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.support.rowset.SqlRowSet;

/**
 * 
 * <pre>
 * &lt;bean id="jdbcTemplate" class="org.springframework.jdbc.core.JdbcTemplate"
 * scope="singleton">
 *     &lt;constructor-arg ref="dataSource" /> 
 * &lt;/bean>
 * 
 * &lt;bean id="batchDao" class="net.sf.appstatus.batch.jdbc.BatchDao"
 * scope="singleton"> &lt;property name="jdbcTemplate" ref="jdbcTemplate" />
 * &lt;/bean>
 * 
 * 
 * 
 * &lt;bean id="jdbcBatchManager"
 * class="net.sf.appstatus.batch.jdbc.JdbcBatchManager" scope="singleton">
 * 		&lt;property name="batchDao" ref="batchDao" /> 
 * &lt;/bean>
 * 
 * </pre>
 * 
 * Create table: BATCH
 * 
 * <p>
 * <table>
 * <tr>
 * <td>UUID_BATCH</td>
 * <td>varchar (256)</td>
 * </tr>
 * <tr>
 * <td>GROUP_BATCH</td>
 * <td>varchar (256)</td>
 * </tr>
 * <tr>
 * <td>NAME_BATCH</td>
 * <td>varchar (256)</td>
 * </tr>
 * <tr>
 * <td>START_DATE</td>
 * <td>DATETIME</td>
 * </tr>
 * <tr>
 * <td>END_DATE</td>
 * <td>DATETIME</td>
 * </tr>
 * <tr>
 * <td>UPDATED</td>
 * <td>DATETIME</td>
 * </tr>
 * <tr>
 * <td>STATUS</td>
 * <td>varchar (64)</td>
 * </tr>
 * <tr>
 * <td>SUCCESS</td>
 * <td>BOOLEAN</td>
 * </tr>
 * <tr>
 * <td>ITEMCOUNT</td>
 * <td>LONG</td>
 * </tr>
 * <tr>
 * <td>ITEM</td>
 * <td>varchar (256)</td>
 * </tr>
 * </tr>
 * <tr>
 * <td>CURRENT_TASK</td>
 * <td>varchar (256)</td>
 * </tr>
 * <tr>
 * <td>PROGRESS</td>
 * <td>Float</td>
 * </tr>
 * <tr>
 * <td>REJECT</td>
 * <td>CLOB</td>
 * </tr>
 * <tr>
 * <td>LAST_MSG</td>
 * <td>varchar (1024)</td>
 * </tr>
 * </table>
 */
public class BatchDao {

	public static final int BATCHS_FETCH = 2;

	// Query codes
	public static final int CREATE_TABLE = 1;

	private static final String INSERT_SQL = "INSERT into BATCH "
			+ "(UUID_BATCH,GROUP_BATCH,NAME_BATCH,START_DATE,STATUS,ITEMCOUNT) values (?,?,?,?,?,0)";

	private static Logger logger = LoggerFactory.getLogger(BatchDao.class);

	private static final String SQL_BATCHS_FETCH = "SELECT UUID_BATCH, ITEM, CURRENT_TASK, END_DATE, GROUP_BATCH, ITEMCOUNT, LAST_MSG, UPDATED, NAME_BATCH, PROGRESS, REJECT, START_DATE, STATUS,SUCCESS FROM BATCH WHERE STATUS = ? ORDER BY UPDATED DESC LIMIT ?";

	private static final String SQL_DELETE = "delete from BATCH where UUID_BATCH = ?";

	private static final String SQL_DELETE_OLD_BATCH = "delete from BATCH where UPDATE =? AND STATUS != ?";

	private static final String SQL_DELETE_SUCCESS_BATCH = "delete from BATCH where STATUS != ?";
	private static final String SQL_UPDATE = "UPDATE BATCH "
			+ "set ITEM = ?, CURRENT_TASK = ?, END_DATE=?, GROUP_BATCH=?,  ITEMCOUNT=?, LAST_MSG = ?, UPDATED=?, NAME_BATCH=?, PROGRESS = ?, REJECT = ?, STATUS=?, SUCCESS=?  WHERE  UUID_BATCH=?";

	/**
	 * Spring JDBC template
	 */
	private JdbcTemplate jdbcTemplate;
	protected String tableName = "BATCH";

	public void setTableName(String tableName) {
		this.tableName = tableName;
	}

	/**
	 * Check for storage table and create if necessary.
	 * 
	 * @return true if database was created.
	 */
	public boolean createDbIfNecessary() {
		logger.info("Looking for table {}...", tableName);

		try {
			this.jdbcTemplate.execute("select count(*) from " + tableName);
			logger.info("Table {} found.", tableName);
			return false;
		} catch (DataAccessException e) {
			logger.warn("Table {} not found. Creating using \"{}\" ...", tableName, getSql(CREATE_TABLE));
			jdbcTemplate.execute(getSql(CREATE_TABLE));
			logger.info("Table {} created", tableName);
			return true;
		}
	}

	public void deleteBatch(final String uuidBatch) {
		Object[] parameters = new Object[] { uuidBatch };
		this.jdbcTemplate.update(SQL_DELETE, parameters);
		logger.info("Batch {} deleted.", uuidBatch);
	}

	public void deleteOldBatches(final int delay) {
		Object[] parameters = new Object[] { new DateTime().minusMonths(delay).toDate(), IBatch.STATUS_RUNNING };
		this.jdbcTemplate.update(SQL_DELETE_OLD_BATCH, parameters);
		logger.info("Batchs older than {} months deleted.", delay);
	}

	public void deleteSuccessBatches() {
		Object[] parameters = new Object[] { IBatch.STATUS_SUCCESS };
		this.jdbcTemplate.update(SQL_DELETE_SUCCESS_BATCH, parameters);
		logger.info("Batchs with success status deleted.");
	}

	private List<BdBatch> fetchBdBatch(final int max, String status) {
		List<BdBatch> results = new ArrayList<BdBatch>();
		Object[] parameters = new Object[] { status, max };
		SqlRowSet srs = this.jdbcTemplate.queryForRowSet(getSql(BATCHS_FETCH), parameters);

		while (srs.next()) {
			BdBatch bdBatch = mappinpBdbatch(srs);
			results.add(bdBatch);
		}

		return results;
	}

	public List<BdBatch> fetchError(final int max) {

		List<BdBatch> results = fetchBdBatch(max, IBatch.STATUS_FAILURE);
		return results;
	}

	public List<BdBatch> fetchFinished(final int max) {
		List<BdBatch> results = fetchBdBatch(max, IBatch.STATUS_SUCCESS);
		return results;
	}

	public List<BdBatch> fetchRunning(final int max) {
		List<BdBatch> results = fetchBdBatch(max, IBatch.STATUS_RUNNING);
		return results;
	}

	/**
	 * Get SQL query for the requested action.
	 * <p>
	 * Override this method to adapt to a new SQL Dialect.
	 * 
	 * @param query
	 *            {@link #BATCHS_FETCH} {@link #CREATE_TABLE}
	 * @return the SQL query
	 */
	protected String getSql(int query) {

		switch (query) {
		case BATCHS_FETCH:
			return SQL_BATCHS_FETCH;
		case CREATE_TABLE:
			return "CREATE TABLE " + tableName + " (" //
					+ " UUID_BATCH varchar(256) NOT NULL," //
					+ "GROUP_BATCH varchar(256) NULL," //
					+ "NAME_BATCH varchar(256) NULL," //
					+ "START_DATE DATE  NULL," //
					+ "END_DATE DATE NULL," //
					+ "UPDATED DATE NULL," //
					+ "STATUS varchar(64) NULL," //
					+ "SUCCESS BOOLEAN NULL," //
					+ "ITEMCOUNT BIGINT NULL," //
					+ "ITEM varchar(256) NULL," //
					+ "CURRENT_TASK varchar(256) NULL," //
					+ "PROGRESS Float NULL," //
					+ "REJECT CLOB NULL," //
					+ "LAST_MSG varchar(1024) NULL," //
					+ "PRIMARY KEY (UUID_BATCH)" + ")  ";

		default:
			return null;
		}
	}

	/**
	 * Read batch object from result set.
	 * 
	 * @param srs
	 * @return
	 */
	private BdBatch mappinpBdbatch(SqlRowSet srs) {
		BdBatch bdBatch = new BdBatch();
		bdBatch.setUuid(srs.getString("UUID_BATCH"));
		bdBatch.setCurrentItem(srs.getString("ITEM"));
		bdBatch.setEndDate(srs.getDate("END_DATE"));
		bdBatch.setGroup(srs.getString("GROUP_BATCH"));
		bdBatch.setItemCount(srs.getLong("ITEMCOUNT"));
		bdBatch.setLastMessage(srs.getString("LAST_MSG"));
		bdBatch.setLastUpdate(srs.getDate("UPDATED"));
		bdBatch.setName(srs.getString("NAME_BATCH"));
		bdBatch.setProgress(srs.getFloat("PROGRESS"));
		bdBatch.setReject(srs.getString("REJECT"));
		bdBatch.setStartDate(srs.getDate("START_DATE"));
		bdBatch.setStatus(srs.getString("STATUS"));
		bdBatch.setSuccess(srs.getBoolean("SUCCESS"));
		return bdBatch;
	}

	public BdBatch save(BdBatch bdBatch) {
		Object[] parameters = new Object[] { bdBatch.getUuid(), bdBatch.getGroup(), bdBatch.getName(),
				bdBatch.getStartDate(), bdBatch.getStatus() };
		logger.debug("PARAMETERS UUID BATCH:{} NAME: {} GROUP: {}", bdBatch.getUuid(), bdBatch.getName(),
				bdBatch.getGroup());
		int result = this.jdbcTemplate.update(INSERT_SQL, parameters);
		logger.debug("{} lines inserted.", result);
		return bdBatch;
	}

	public void setJdbcTemplate(JdbcTemplate jdbcTemplate) {
		this.jdbcTemplate = jdbcTemplate;
	}

	public void update(BdBatch bdBatch) {
		Object[] parameters = new Object[] { bdBatch.getCurrentItem(), bdBatch.getCurrentTask(), bdBatch.getEndDate(),
				bdBatch.getGroup(), bdBatch.getItemCount(), bdBatch.getLastMessage(), bdBatch.getLastUpdate(),
				bdBatch.getName(), bdBatch.getProgress(), bdBatch.getReject(), bdBatch.getStatus(),
				bdBatch.getSuccess(), bdBatch.getUuid() };
		this.jdbcTemplate.update(SQL_UPDATE, parameters);
		logger.debug("Batch {} updated ", bdBatch.getUuid());
	}
}
