package net.sf.appstatus.batch;

import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.notNullValue;
import static org.hamcrest.Matchers.nullValue;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import java.util.ArrayList;
import java.util.List;

import net.sf.appstatus.core.batch.IBatch;
import net.sf.appstatus.core.batch.IBatchProgressMonitor;

import org.joda.time.DateTime;
import org.junit.Test;

public class BatchTest {

	/**
	 * Test {@link Batch#equals(Object)}.
	 */
	@Test
	public void testEqualsObject() {
		Batch batch = new Batch("12345");

		// test equality on uuid
		assertThat(batch.equals(new Batch("12345")), is(true));
		assertThat(batch.equals(new Batch("54321")), is(false));
		assertThat(batch.equals(null), is(false));
	}

	@Test
	public void testGetCurrentItem() {
		// assert with response with no monitor
		assertNull(new Batch("12345", "batchName", "batchGroup").getCurrentItem());

		Batch batch = new Batch("12345");
		assertNull(batch.getCurrentItem());

		// create the progress monitor
		InProcessBatchProgressMonitor mockedProgressMonitor = mock(InProcessBatchProgressMonitor.class);
		batch.setProgressMonitor(mockedProgressMonitor);

		when(mockedProgressMonitor.getCurrentItem()).thenReturn(null);
		assertNull(batch.getCurrentItem());

		when(mockedProgressMonitor.getCurrentItem()).thenReturn(new String("current item"));
		assertThat(batch.getCurrentItem(), is("current item"));
	}

	@Test
	public void testGetCurrentTask() {
		// assert with response with no monitor
		assertNull(new Batch("12345", "batchName", "batchGroup").getCurrentTask());
		Batch batch = new Batch("12345");
		assertNull(batch.getCurrentTask());

		// create the progress monitor
		InProcessBatchProgressMonitor mockedProgressMonitor = mock(InProcessBatchProgressMonitor.class);
		batch.setProgressMonitor(mockedProgressMonitor);

		// assert response with the monitor
		when(mockedProgressMonitor.getTaskName()).thenReturn(null);
		assertNull(batch.getCurrentItem());

		when(mockedProgressMonitor.getTaskName()).thenReturn("taskName");
		assertThat(batch.getCurrentTask(), is("taskName"));
	}

	@Test
	public void testGetEndDate() {
		// assert with response with no monitor
		Batch batch = new Batch("12345");
		assertThat(batch.getEndDate(), nullValue());
		assertThat(new Batch("12345", "batchName", "batchGroup").getEndDate(), nullValue());

		// create the progress monitor
		InProcessBatchProgressMonitor mockedProgressMonitor = mock(InProcessBatchProgressMonitor.class);
		batch.setProgressMonitor(mockedProgressMonitor);

		// assert response with the monitor
		when(mockedProgressMonitor.getEndDate()).thenReturn(null);
		assertThat(batch.getEndDate(), nullValue());

		when(mockedProgressMonitor.getEndDate()).thenReturn(new DateTime("2012-01-12T12:56:34").toDate());
		assertThat(batch.getEndDate(), is(new DateTime("2012-01-12T12:56:34").toDate()));
	}

	@Test
	public void testGetGroup() {
		Batch batch = new Batch("12345");
		assertNull(batch.getGroup());

		batch = new Batch("12345", "name", "group");
		assertThat(batch.getGroup(), is("group"));
	}

	@Test
	public void testGetItemCount() {
		// assert with response with no monitor
		Batch batch = new Batch("12345");
		assertThat(batch.getItemCount(), is((long) 0));
		assertThat(new Batch("12345", "batchName", "batchGroup").getItemCount(), is((long) 0));

		// create the progress monitor
		InProcessBatchProgressMonitor mockedProgressMonitor = mock(InProcessBatchProgressMonitor.class);
		batch.setProgressMonitor(mockedProgressMonitor);

		// assert response with the monitor
		when(mockedProgressMonitor.getItemCount()).thenReturn((long) 0);
		assertThat(batch.getItemCount(), is((long) 0));

		when(mockedProgressMonitor.getItemCount()).thenReturn((long) 45);
		assertThat(batch.getItemCount(), is((long) 45));
	}

	@Test
	public void testGetLastMessage() {
		// assert with response with no monitor
		Batch batch = new Batch("12345");
		assertNull(batch.getLastMessage());
		assertNull(new Batch("12345", "batchName", "batchGroup").getLastMessage());

		// create the progress monitor
		InProcessBatchProgressMonitor mockedProgressMonitor = mock(InProcessBatchProgressMonitor.class);
		batch.setProgressMonitor(mockedProgressMonitor);

		// assert response with the monitor
		when(mockedProgressMonitor.getLastMessage()).thenReturn(null);
		assertNull(batch.getLastMessage());

		when(mockedProgressMonitor.getLastMessage()).thenReturn("last message");
		assertThat(batch.getLastMessage(), is("last message"));
	}

	@Test
	public void testGetLastUpdate() {
		// assert with response with no monitor
		Batch batch = new Batch("12345");
		assertThat(batch.getLastUpdate(), nullValue());
		assertThat(new Batch("12345", "batchName", "batchGroup").getLastUpdate(), nullValue());

		// create the progress monitor
		InProcessBatchProgressMonitor mockedProgressMonitor = mock(InProcessBatchProgressMonitor.class);
		batch.setProgressMonitor(mockedProgressMonitor);

		// assert response with the monitor
		when(mockedProgressMonitor.getLastUpdate()).thenReturn(null);
		assertThat(batch.getLastUpdate(), nullValue());

		when(mockedProgressMonitor.getLastUpdate()).thenReturn(new DateTime("2012-03-02T17:23:30").toDate());
		assertThat(batch.getLastUpdate(), is(new DateTime("2012-03-02T17:23:30").toDate()));
	}

	@Test
	public void testGetName() {
		Batch batch = new Batch("12345");
		assertNull(batch.getName());

		batch = new Batch("12345", "name", "group");
		assertThat(batch.getName(), is("name"));
	}

	@Test
	public void testGetProgressMonitor() {
		Batch batch = new Batch("12345");
		assertThat(batch.getProgressMonitor(), nullValue());

		batch = new Batch("12345", "name", "group");
		assertThat(batch.getProgressMonitor(), nullValue());

		// create the progress monitor
		InProcessBatchProgressMonitor mockedProgressMonitor = mock(InProcessBatchProgressMonitor.class);
		batch.setProgressMonitor(mockedProgressMonitor);

		assertThat(batch.getProgressMonitor(), is(equalTo((IBatchProgressMonitor) mockedProgressMonitor)));
	}

	@Test
	public void testGetProgressStatus() {
		// if the monitor is null
		Batch batch = new Batch("12345");
		assertThat(batch.getProgressStatus(), is(equalTo(-1f)));

		// create the progress monitor
		InProcessBatchProgressMonitor mockedProgressMonitor = mock(InProcessBatchProgressMonitor.class);
		batch.setProgressMonitor(mockedProgressMonitor);

		// if monitor's total work value is negative
		when(mockedProgressMonitor.getTotalWork()).thenReturn(IBatchProgressMonitor.UNKNOW);
		assertThat(batch.getProgressStatus(), is(equalTo((float) IBatchProgressMonitor.UNKNOW)));

		// if monitor's total work value is 0
		when(mockedProgressMonitor.getTotalWork()).thenReturn(0);
		assertThat(batch.getProgressStatus(), is(equalTo((float) IBatchProgressMonitor.UNKNOW)));

		// if monitor's total work value is positive and progress is 0
		when(mockedProgressMonitor.getTotalWork()).thenReturn(5);
		when(mockedProgressMonitor.getProgress()).thenReturn(0f);
		assertThat(batch.getProgressStatus(), is(equalTo(0f)));

		// if monitor's total work value is positive and progress is positive
		when(mockedProgressMonitor.getTotalWork()).thenReturn(5);
		when(mockedProgressMonitor.getProgress()).thenReturn(0.305f);
		assertThat(batch.getProgressStatus(), is(equalTo(6.1f)));
	}

	@Test
	public void testGetRejectedItemsId() {
		// assert with response with no monitor
		Batch batch = new Batch("12345");
		assertThat(batch.getRejectedItemsId(), notNullValue());
		assertThat(batch.getRejectedItemsId().size(), is(0));
		assertThat(new Batch("12345", "batchName", "batchGroup").getRejectedItemsId(), notNullValue());
		assertThat(new Batch("12345", "batchName", "batchGroup").getRejectedItemsId().size(), is(0));

		// create the progress monitor
		InProcessBatchProgressMonitor mockedProgressMonitor = mock(InProcessBatchProgressMonitor.class);
		batch.setProgressMonitor(mockedProgressMonitor);

		// assert response with the monitor

		final List<String> rejectedItems = new ArrayList<String>();
		rejectedItems.add("1234");
		rejectedItems.add("4321");
		rejectedItems.add("3412");
		rejectedItems.add("2143");
		when(mockedProgressMonitor.getRejectedItems()).thenReturn(rejectedItems);
		assertThat(batch.getRejectedItemsId(), notNullValue());
		assertThat(batch.getRejectedItemsId().size(), is(4));
		assertThat(batch.getRejectedItemsId().get(0), is("1234"));
		assertThat(batch.getRejectedItemsId().get(1), is("4321"));
		assertThat(batch.getRejectedItemsId().get(2), is("3412"));
		assertThat(batch.getRejectedItemsId().get(3), is("2143"));
	}

	@Test
	public void testGetStartDate() {
		// assert with response with no monitor
		Batch batch = new Batch("12345");
		assertThat(batch.getStartDate(), nullValue());
		assertThat(new Batch("12345", "batchName", "batchGroup").getStartDate(), nullValue());

		// create the progress monitor
		InProcessBatchProgressMonitor mockedProgressMonitor = mock(InProcessBatchProgressMonitor.class);
		batch.setProgressMonitor(mockedProgressMonitor);

		// assert response with the monitor
		when(mockedProgressMonitor.getStartDate()).thenReturn(null);
		assertThat(batch.getStartDate(), nullValue());

		when(mockedProgressMonitor.getStartDate()).thenReturn(new DateTime("2012-01-12T12:56:34").toDate());
		assertThat(batch.getStartDate(), is(new DateTime("2012-01-12T12:56:34").toDate()));
	}

	@Test
	public void testGetStatus() {
		// assert with response with no monitor
		Batch batch = new Batch("12345");
		assertNull(batch.getStatus());
		assertNull(new Batch("12345", "batchName", "batchGroup").getStatus());

		// create the progress monitor
		InProcessBatchProgressMonitor mockedProgressMonitor = mock(InProcessBatchProgressMonitor.class);
		batch.setProgressMonitor(mockedProgressMonitor);

		// monitor say is not done => status running
		when(mockedProgressMonitor.isDone()).thenReturn(false);
		assertThat(batch.getStatus(), is(IBatch.STATUS_RUNNING));

		// monitor say is done and it's not successful => status failure
		when(mockedProgressMonitor.isDone()).thenReturn(true);
		when(mockedProgressMonitor.isSuccess()).thenReturn(false);
		assertThat(batch.getStatus(), is(IBatch.STATUS_FAILURE));

		// monitor say is done and it's successful => status success
		when(mockedProgressMonitor.isDone()).thenReturn(true);
		when(mockedProgressMonitor.isSuccess()).thenReturn(true);
		assertThat(batch.getStatus(), is(IBatch.STATUS_SUCCESS));
	}

	@Test
	public void testGetUuid() {
		Batch batch = new Batch("12345");
		assertThat(batch.getUuid(), is("12345"));

		batch = new Batch("12345", "name", "group");
		assertThat(batch.getUuid(), is("12345"));
	}

	@Test
	public void testIsSuccess() {
		// assert with response with no monitor
		Batch batch = new Batch("12345");
		assertThat(batch.isSuccess(), is(false));
		assertThat(new Batch("12345", "batchName", "batchGroup").isSuccess(), is(false));

		// create the progress monitor
		InProcessBatchProgressMonitor mockedProgressMonitor = mock(InProcessBatchProgressMonitor.class);
		batch.setProgressMonitor(mockedProgressMonitor);

		// assert response with the monitor
		when(mockedProgressMonitor.isSuccess()).thenReturn(false);
		assertThat(batch.isSuccess(), is(false));

		when(mockedProgressMonitor.isSuccess()).thenReturn(true);
		assertThat(batch.isSuccess(), is(true));
	}

	@Test
	public void testSetProgressMonitor() {
		Batch batch = new Batch("12345");
		assertThat(batch.getProgressMonitor(), nullValue());

		// create the progress monitor
		InProcessBatchProgressMonitor mockedProgressMonitor = mock(InProcessBatchProgressMonitor.class);
		batch.setProgressMonitor(mockedProgressMonitor);

		assertThat(batch.getProgressMonitor(), is(equalTo((IBatchProgressMonitor) mockedProgressMonitor)));
	}

}
