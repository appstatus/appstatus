package net.sf.appstatus.agent;

import static org.junit.Assert.assertNotSame;
import static org.junit.Assert.assertSame;
import static org.junit.Assert.assertTrue;
import net.sf.appstatus.agent.batch.IBatchExecutionMonitor;
import net.sf.appstatus.agent.batch.IBatchExecutionMonitorAgent;
import net.sf.appstatus.agent.batch.helpers.NOPBatchExecutionMonitor;
import net.sf.appstatus.agent.batch.helpers.NOPBatchExecutionMonitorAgent;
import net.sf.appstatus.agent.batch.impl.MockBatchMonitorAgent;
import net.sf.appstatus.agent.service.IServiceStatisticsMonitor;
import net.sf.appstatus.agent.service.IServiceStatisticsMonitorAgent;
import net.sf.appstatus.agent.service.helpers.NOPServiceStatisticsMonitor;
import net.sf.appstatus.agent.service.helpers.NOPServiceStatisticsMonitorAgent;
import net.sf.appstatus.agent.service.impl.MockServiceMonitorAgent;

import org.junit.Test;

public class MonitorFactoryTest {

	@Test
	public void testGetAgent() {
		// test retrieve the agent service
		IServiceStatisticsMonitorAgent agent = MonitorFactory.getAgent(
				IServiceStatisticsMonitorAgent.class,
				MonitorFactory.DEFAULT_MONITOR_NAME, "myService");
		assertTrue(agent instanceof IServiceStatisticsMonitorAgent);

		// test that we retrieve the same agent service
		IServiceStatisticsMonitorAgent agent2 = MonitorFactory.getAgent(
				IServiceStatisticsMonitorAgent.class,
				MonitorFactory.DEFAULT_MONITOR_NAME, "myService");
		assertSame(agent, agent2);

		// test that a new agent name create a new agent
		agent2 = MonitorFactory.getAgent(IServiceStatisticsMonitorAgent.class,
				MonitorFactory.DEFAULT_MONITOR_NAME, "myService2");
		assertNotSame(agent, agent2);

		// test that we retrieve the needed implemenation
		agent2 = MonitorFactory.getAgent(IServiceStatisticsMonitorAgent.class,
				"service-test", "myService3");
		assertTrue(agent2 instanceof MockServiceMonitorAgent);

		// test that we retrieve the NOP implementation if the implementation
		// requested is not known by the system
		agent2 = MonitorFactory.getAgent(IServiceStatisticsMonitorAgent.class,
				"unknow-implementation", "myService4");
		assertTrue(agent2 instanceof NOPServiceStatisticsMonitorAgent);

		// test retrieve the agent batch
		IBatchExecutionMonitorAgent agent3 = MonitorFactory.getAgent(
				IBatchExecutionMonitorAgent.class, MonitorFactory.DEFAULT_MONITOR_NAME,
				"myBatch");
		assertTrue(agent3 instanceof IBatchExecutionMonitorAgent);

		// test that we retrieve the same agent batch
		IBatchExecutionMonitorAgent agent4 = MonitorFactory.getAgent(
				IBatchExecutionMonitorAgent.class, MonitorFactory.DEFAULT_MONITOR_NAME,
				"myBatch");
		assertSame(agent3, agent4);

		// test that a new agent name create a new agent
		agent4 = MonitorFactory.getAgent(IBatchExecutionMonitorAgent.class,
				MonitorFactory.DEFAULT_MONITOR_NAME, "myBatch2");
		assertNotSame(agent3, agent4);

		// test that we retrieve the needed implementation
		agent4 = MonitorFactory.getAgent(IBatchExecutionMonitorAgent.class,
				"batch-test", "myBatch3");
		assertTrue(agent4 instanceof MockBatchMonitorAgent);

		// test that we retrieve the NOP implementation if the implementation
		// requested is not known by the system
		agent4 = MonitorFactory.getAgent(IBatchExecutionMonitorAgent.class,
				"unknow-implementation", "myBatch4");
		assertTrue(agent4 instanceof NOPBatchExecutionMonitorAgent);
	}

	@Test
	public void testGetMonitor() {
		// get the default implementation of a service monitor
		IServiceStatisticsMonitor monitor = MonitorFactory.getMonitor(
				IServiceStatisticsMonitor.class, MonitorFactory.DEFAULT_MONITOR_NAME,
				"myService");
		assertTrue(monitor instanceof IServiceStatisticsMonitor);

		// get the same instance cause we have the same monitor name
		IServiceStatisticsMonitor monitor2 = MonitorFactory.getMonitor(
				IServiceStatisticsMonitor.class, MonitorFactory.DEFAULT_MONITOR_NAME,
				"myService");
		assertSame(monitor, monitor2);

		// get a different service monitor
		monitor2 = MonitorFactory.getMonitor(IServiceStatisticsMonitor.class,
				MonitorFactory.DEFAULT_MONITOR_NAME, "myService2");
		assertNotSame(monitor, monitor2);

		// test that we retrieve the needed implemenation
		monitor2 = MonitorFactory.getMonitor(IServiceStatisticsMonitor.class,
				"service-test", "myService3");
		assertTrue(monitor2 instanceof MockServiceMonitorAgent);

		// test that we retrieve the NOP implementation if the implementation
		// requested is not known by the system
		monitor2 = MonitorFactory.getMonitor(IServiceStatisticsMonitor.class,
				"unknow-implementation", "myService4");
		assertTrue(monitor2 instanceof NOPServiceStatisticsMonitor);

		// test retrieve the agent batch
		IBatchExecutionMonitor monitor3 = MonitorFactory.getMonitor(IBatchExecutionMonitor.class,
				MonitorFactory.DEFAULT_MONITOR_NAME, "myBatch");
		assertTrue(monitor3 instanceof IBatchExecutionMonitor);

		// test that we retrieve the same agent batch
		IBatchExecutionMonitor monitor4 = MonitorFactory.getMonitor(IBatchExecutionMonitor.class,
				MonitorFactory.DEFAULT_MONITOR_NAME, "myBatch");
		assertSame(monitor3, monitor4);

		// test that a new agent name create a new agent
		monitor4 = MonitorFactory.getMonitor(IBatchExecutionMonitor.class,
				MonitorFactory.DEFAULT_MONITOR_NAME, "myBatch2");
		assertNotSame(monitor3, monitor4);

		// test that we retrieve the needed implementation
		monitor4 = MonitorFactory.getMonitor(IBatchExecutionMonitor.class, "batch-test",
				"myBatch3");
		assertTrue(monitor4 instanceof MockBatchMonitorAgent);

		// test that we retrieve the NOP implementation if the implementation
		// requested is not known by the system
		monitor4 = MonitorFactory.getMonitor(IBatchExecutionMonitor.class,
				"unknow-implementation", "myBatch4");
		assertTrue(monitor4 instanceof NOPBatchExecutionMonitor);
	}

}
