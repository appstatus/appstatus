package net.sf.appstatus.agent;

import static org.junit.Assert.assertNotSame;
import static org.junit.Assert.assertSame;
import static org.junit.Assert.assertTrue;
import net.sf.appstatus.agent.batch.IBatchMonitor;
import net.sf.appstatus.agent.batch.IBatchMonitorAgent;
import net.sf.appstatus.agent.batch.helpers.NOPBatchMonitor;
import net.sf.appstatus.agent.batch.helpers.NOPBatchMonitorAgent;
import net.sf.appstatus.agent.batch.impl.MockBatchMonitorAgent;
import net.sf.appstatus.agent.service.IServiceMonitor;
import net.sf.appstatus.agent.service.IServiceMonitorAgent;
import net.sf.appstatus.agent.service.helpers.NOPServiceMonitor;
import net.sf.appstatus.agent.service.helpers.NOPServiceMonitorAgent;
import net.sf.appstatus.agent.service.impl.MockServiceMonitorAgent;

import org.junit.Test;

public class MonitorFactoryTest {

	@Test
	public void testGetAgent() {
		// test retrieve the agent service
		IServiceMonitorAgent agent = MonitorFactory.getAgent(
				IServiceMonitorAgent.class,
				MonitorFactory.DEFAULT_MONITOR_NAME, "myService");
		assertTrue(agent instanceof IServiceMonitorAgent);

		// test that we retrieve the same agent service
		IServiceMonitorAgent agent2 = MonitorFactory.getAgent(
				IServiceMonitorAgent.class,
				MonitorFactory.DEFAULT_MONITOR_NAME, "myService");
		assertSame(agent, agent2);

		// test that a new agent name create a new agent
		agent2 = MonitorFactory.getAgent(IServiceMonitorAgent.class,
				MonitorFactory.DEFAULT_MONITOR_NAME, "myService2");
		assertNotSame(agent, agent2);

		// test that we retrieve the needed implemenation
		agent2 = MonitorFactory.getAgent(IServiceMonitorAgent.class,
				"service-test", "myService3");
		assertTrue(agent2 instanceof MockServiceMonitorAgent);

		// test that we retrieve the NOP implementation if the implementation
		// requested is not known by the system
		agent2 = MonitorFactory.getAgent(IServiceMonitorAgent.class,
				"unknow-implementation", "myService4");
		assertTrue(agent2 instanceof NOPServiceMonitorAgent);

		// test retrieve the agent batch
		IBatchMonitorAgent agent3 = MonitorFactory.getAgent(
				IBatchMonitorAgent.class, MonitorFactory.DEFAULT_MONITOR_NAME,
				"myBatch");
		assertTrue(agent3 instanceof IBatchMonitorAgent);

		// test that we retrieve the same agent batch
		IBatchMonitorAgent agent4 = MonitorFactory.getAgent(
				IBatchMonitorAgent.class, MonitorFactory.DEFAULT_MONITOR_NAME,
				"myBatch");
		assertSame(agent3, agent4);

		// test that a new agent name create a new agent
		agent4 = MonitorFactory.getAgent(IBatchMonitorAgent.class,
				MonitorFactory.DEFAULT_MONITOR_NAME, "myBatch2");
		assertNotSame(agent3, agent4);

		// test that we retrieve the needed implementation
		agent4 = MonitorFactory.getAgent(IBatchMonitorAgent.class,
				"batch-test", "myBatch3");
		assertTrue(agent4 instanceof MockBatchMonitorAgent);

		// test that we retrieve the NOP implementation if the implementation
		// requested is not known by the system
		agent4 = MonitorFactory.getAgent(IBatchMonitorAgent.class,
				"unknow-implementation", "myBatch4");
		assertTrue(agent4 instanceof NOPBatchMonitorAgent);
	}

	@Test
	public void testGetMonitor() {
		// get the default implementation of a service monitor
		IServiceMonitor monitor = MonitorFactory.getMonitor(
				IServiceMonitor.class, MonitorFactory.DEFAULT_MONITOR_NAME,
				"myService");
		assertTrue(monitor instanceof IServiceMonitor);

		// get the same instance cause we have the same monitor name
		IServiceMonitor monitor2 = MonitorFactory.getMonitor(
				IServiceMonitor.class, MonitorFactory.DEFAULT_MONITOR_NAME,
				"myService");
		assertSame(monitor, monitor2);

		// get a different service monitor
		monitor2 = MonitorFactory.getMonitor(IServiceMonitor.class,
				MonitorFactory.DEFAULT_MONITOR_NAME, "myService2");
		assertNotSame(monitor, monitor2);

		// test that we retrieve the needed implemenation
		monitor2 = MonitorFactory.getMonitor(IServiceMonitor.class,
				"service-test", "myService3");
		assertTrue(monitor2 instanceof MockServiceMonitorAgent);

		// test that we retrieve the NOP implementation if the implementation
		// requested is not known by the system
		monitor2 = MonitorFactory.getMonitor(IServiceMonitor.class,
				"unknow-implementation", "myService4");
		assertTrue(monitor2 instanceof NOPServiceMonitor);

		// test retrieve the agent batch
		IBatchMonitor monitor3 = MonitorFactory.getMonitor(IBatchMonitor.class,
				MonitorFactory.DEFAULT_MONITOR_NAME, "myBatch");
		assertTrue(monitor3 instanceof IBatchMonitor);

		// test that we retrieve the same agent batch
		IBatchMonitor monitor4 = MonitorFactory.getMonitor(IBatchMonitor.class,
				MonitorFactory.DEFAULT_MONITOR_NAME, "myBatch");
		assertSame(monitor3, monitor4);

		// test that a new agent name create a new agent
		monitor4 = MonitorFactory.getMonitor(IBatchMonitor.class,
				MonitorFactory.DEFAULT_MONITOR_NAME, "myBatch2");
		assertNotSame(monitor3, monitor4);

		// test that we retrieve the needed implementation
		monitor4 = MonitorFactory.getMonitor(IBatchMonitor.class, "batch-test",
				"myBatch3");
		assertTrue(monitor4 instanceof MockBatchMonitorAgent);

		// test that we retrieve the NOP implementation if the implementation
		// requested is not known by the system
		monitor4 = MonitorFactory.getMonitor(IBatchMonitor.class,
				"unknow-implementation", "myBatch4");
		assertTrue(monitor4 instanceof NOPBatchMonitor);
	}

}
