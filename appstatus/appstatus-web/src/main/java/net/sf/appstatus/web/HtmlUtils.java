package net.sf.appstatus.web;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;

import javax.servlet.ServletOutputStream;

/**
 * Support class for generating Html tables.
 * 
 * @author Nicolas
 * 
 */
public class HtmlUtils {
	private static final String ENCODING = "UTF-8";

	public static String collectionToDelimitedString(Collection coll, String delim, String prefix, String suffix) {
		if (isEmpty(coll)) {
			return "";
		}
		StringBuilder sb = new StringBuilder();
		Iterator it = coll.iterator();
		while (it.hasNext()) {
			sb.append(prefix).append(it.next()).append(suffix);
			if (it.hasNext()) {
				sb.append(delim);
			}
		}
		return sb.toString();
	}

	public static String countAndDetail(List<String> items) {
		String itemsList = collectionToDelimitedString(items, ", ", "", "");
		return "<a href='#' title='" + itemsList + "'>" + items.size() + "</a>" + "<span style=\"display:none\" >"
				+ itemsList + "</span>";
	}

	/**
	 * Prints table start tag, or a message if table is empty.
	 * 
	 * @param size
	 * @return true if we can go on with table generation.
	 * @throws IOException
	 * @throws UnsupportedEncodingException
	 */
	public static boolean generateBeginTable(ServletOutputStream os, int size) throws UnsupportedEncodingException,
			IOException {

		if (size == 0) {
			os.write("<p>No items</p>".getBytes(ENCODING));

			return false;
		}

		os.write("<table class=\"table table-hover table-condensed\">".getBytes(ENCODING));
		return true;
	}

	public static void generateEndTable(ServletOutputStream os, int size) throws UnsupportedEncodingException,
			IOException {

		if (size > 0) {
			os.write("</tbody></table>".getBytes(ENCODING));
		}
	}

	/**
	 * Outputs table headers
	 * 
	 * @param os
	 * @param cols
	 * @throws IOException
	 */
	public static void generateHeaders(ServletOutputStream os, Object... cols) throws IOException {
		os.write("<thead><tr>".getBytes());
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
		os.write("</tr></thead><tbody>".getBytes());
	}

	/**
	 * Outputs one table row
	 * 
	 * @param os
	 * @param status
	 * @param cols
	 * @throws IOException
	 */
	public static void generateRow(ServletOutputStream os, String status, Object... cols) throws IOException {
		os.write("<tr>".getBytes());

		os.write(("<td><img src='?icon=" + status + "'></td>").getBytes(ENCODING));

		for (Object obj : cols) {
			os.write("<td>".getBytes());
			if (obj != null) {
				os.write(obj.toString().getBytes(ENCODING));
			}
			os.write("</td>".getBytes());

		}
		os.write("</tr>".getBytes());
	}

	/**
	 * Null-safe empty test for Collections.
	 * 
	 * @param collection
	 * @return
	 */
	public static boolean isEmpty(Collection<?> collection) {
		return (collection == null || collection.isEmpty());
	}
}
