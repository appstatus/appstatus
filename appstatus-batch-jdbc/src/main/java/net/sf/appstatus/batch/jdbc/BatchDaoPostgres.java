package net.sf.appstatus.batch.jdbc;

/**
  *  Postgres Implementation
*/

public class BatchDaoPostgres  extends BatchDao{

	@Override
	protected String getSql(int query) {
		switch (query) {

        case BATCH_FETCH:
            return  "SELECT UUID_BATCH, ITEM, CURRENT_TASK, END_DATE, GROUP_BATCH, ITEMCOUNT, LAST_MSG, UPDATED, NAME_BATCH, PROGRESS, REJECT, START_DATE, STATUS,SUCCESS FROM "
                    + tableName + " WHERE STATUS IN ( %s )  ORDER BY UPDATED DESC LIMIT ?";

        case BATCH_FETCH_BY_NAME:
            return "SELECT UUID_BATCH, ITEM, CURRENT_TASK, END_DATE, GROUP_BATCH, ITEMCOUNT, LAST_MSG, UPDATED, NAME_BATCH, PROGRESS, REJECT, START_DATE, STATUS,SUCCESS FROM "
                    + tableName
                    + " WHERE GROUP_BATCH = ? AND NAME_BATCH = ? AND STATUS IN ( %s )  ORDER BY UPDATED DESC LIMIT ?";

        case BATCH_CREATE_TABLE:
            return "CREATE TABLE " + tableName + " (" //
                    + "UUID_BATCH character varying(256) COLLATE pg_catalog.\"default\" NOT NULL," //
                    + "GROUP_BATCH character varying(256) COLLATE pg_catalog.\"default\"," //
                    + "NAME_BATCH character varying(256) COLLATE pg_catalog.\"default\"," //
                    + "START_DATE DATE  NULL," //
                    + "END_DATE DATE NULL," //
                    + "UPDATED DATE NULL," //
                    + "STATUS character varying(64) COLLATE pg_catalog.\"default\"," //
                    + "SUCCESS character(10) COLLATE pg_catalog.\"default\"," //
                    + "ITEMCOUNT numeric(8,0)," //
                    + "ITEM character varying(256) COLLATE pg_catalog.\"default\"," //
                    + "CURRENT_TASK character varying(256) COLLATE pg_catalog.\"default\"," //
                    + "PROGRESS double precision," //
                    + "REJECT text COLLATE pg_catalog.\"default\"," //
                    + "LAST_MSG character varying(1024) COLLATE pg_catalog.\"default\", " //
                    + "CONSTRAINT "+tableName+"_pkey PRIMARY KEY (uuid_batch)" + ")  ";

        case BATCH_DELETE_SUCCESS:
            return "delete from " + tableName + " where STATUS = ? AND REJECT is NULL";
        default:
            return super.getSql(query);
}
	}
	
	

}
