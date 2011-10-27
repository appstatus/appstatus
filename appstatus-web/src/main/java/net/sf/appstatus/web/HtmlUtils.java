package net.sf.appstatus.web;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.util.List;

import javax.servlet.ServletOutputStream;

import org.springframework.util.StringUtils;

/**
 * Support class for generating Html tables.
 * 
 * @author Nicolas
 * 
 */
public class HtmlUtils {
	private static final String ENCODING = "UTF-8";

	public static String countAndDetail(List<String> items) {

		String itemsList = StringUtils.collectionToDelimitedString(items, ", ");
		return "<a href='#' title='" + itemsList + "'>" + items.size() + "</a>"
				+ "<span style=\"display:none\" >" + itemsList + "</span>";

	}

	/**
	 * Prints table start tag, or a message if table is empty.
	 * 
	 * @param size
	 * @return true if we can go on with table generation.
	 * @throws IOException
	 * @throws UnsupportedEncodingException
	 */
	public static boolean generateBeginTable(ServletOutputStream os, int size)
			throws UnsupportedEncodingException, IOException {

		if (size == 0) {
			os.write("<p>No items</p>".getBytes(ENCODING));

			return false;
		}

		os.write("<table>".getBytes(ENCODING));
		return true;
	}

	public static void generateEndTable(ServletOutputStream os, int size)
			throws UnsupportedEncodingException, IOException {

		if (size < 0) {
			os.write("</table>".getBytes(ENCODING));
		}
	}

	public static void generateHeaders(ServletOutputStream os, Object... cols)
			throws IOException {
		os.write("<tr>".getBytes());
		for (Object obj : cols) {
			os.write("<th>".getBytes());
			if (obj != null) {

				if (obj instanceof Long) {
					Long l = (Long) obj;

				} else {
					os.write(obj.toString().getBytes(ENCODING));
				}
			}
			os.write("</th>".getBytes());

		}
		os.write("</tr>".getBytes());
	}

	/**
	 * Outputs one table row
	 * 
	 * @param os
	 * @param status
	 * @param cols
	 * @throws IOException
	 */
	public static void generateRow(ServletOutputStream os, String status,
			Object... cols) throws IOException {
		os.write("<tr>".getBytes());

		os.write(("<td><img src='?icon=" + status + "'></td>")
				.getBytes(ENCODING));

		for (Object obj : cols) {
			os.write("<td>".getBytes());
			if (obj != null) {
				os.write(obj.toString().getBytes(ENCODING));
			}
			os.write("</td>".getBytes());

		}
		os.write("</tr>".getBytes());
	}
}
