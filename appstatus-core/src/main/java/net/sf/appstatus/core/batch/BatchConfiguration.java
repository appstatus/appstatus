package net.sf.appstatus.core.batch;

import java.io.Serializable;
import java.util.Date;

/**
 * Object representation of a batch execution's configuration.
 *
 * @author Idriss Neumann
 *
 */
public class BatchConfiguration implements Serializable {
    private static final long serialVersionUID = 1L;

    private String executionExpr;

    private String group;

    private Date lastExecution;

    private String name;

    private Date nextExecution;

    /**
     * @return the executionExpr
     */
    public String getExecutionExpr() {
        return executionExpr;
    }

    /**
     * @return the group
     */
    public String getGroup() {
        return group;
    }

    /**
     * @return the lastExecution
     */
    public Date getLastExecution() {
        return lastExecution;
    }

    /**
     * @return the name
     */
    public String getName() {
        return name;
    }

    /**
     * @return the nextExecution
     */
    public Date getNextExecution() {
        return nextExecution;
    }

    /**
     * @param executionExpr
     *            the executionExpr to set
     */
    public void setExecutionExpr(String executionExpr) {
        this.executionExpr = executionExpr;
    }

    /**
     * @param group
     *            the group to set
     */
    public void setGroup(String group) {
        this.group = group;
    }

    /**
     * @param lastExecution
     *            the lastExecution to set
     */
    public void setLastExecution(Date lastExecution) {
        this.lastExecution = lastExecution;
    }

    /**
     * @param name
     *            the name to set
     */
    public void setName(String name) {
        this.name = name;
    }

    /**
     * @param nextExecution
     *            the nextExecution to set
     */
    public void setNextExecution(Date nextExecution) {
        this.nextExecution = nextExecution;
    }
}
