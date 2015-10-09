package net.sf.appstatus.batch.jdbc;

/**
 * Oracle-Compatible implementation.
 * <p>
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
public class BatchDaoOracle extends BatchDao {

    @Override
    protected String getSql(int query) {

        switch (query) {

        case BATCH_FETCH:
            return "SELECT " //
                    + " UUID_BATCH, ITEM, CURRENT_TASK, END_DATE, GROUP_BATCH, ITEMCOUNT, LAST_MSG, UPDATED, NAME_BATCH, PROGRESS, REJECT, START_DATE, STATUS,SUCCESS " //
                    + "FROM ( " //
                    + "SELECT UUID_BATCH, ITEM, CURRENT_TASK, END_DATE, GROUP_BATCH, ITEMCOUNT, LAST_MSG, UPDATED, NAME_BATCH, PROGRESS, REJECT, START_DATE, STATUS,SUCCESS FROM "
                    + tableName
                    + " WHERE STATUS IN ( %s )  ORDER BY UPDATED DESC " //
                    + ") WHERE ROWNUM <= ? ";

        case BATCH_CREATE_TABLE:
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

        case BATCH_DELETE_SUCCESS:
            return "delete from " + tableName + " where STATUS = ? AND REJECT is NULL";
        default:
            return super.getSql(query);
        }

    }
}
