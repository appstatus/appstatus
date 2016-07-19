package net.sf.appstatus.web.pages;

import java.io.ByteArrayOutputStream;
import java.io.IOException;

import javax.servlet.ServletOutputStream;

public class StubServletOutputStream extends ServletOutputStream {
	public ByteArrayOutputStream baos = new ByteArrayOutputStream();

	@Override
	public void write(final int i) throws IOException {
		baos.write(i);
	}
}