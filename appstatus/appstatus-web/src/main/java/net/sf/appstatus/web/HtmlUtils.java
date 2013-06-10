package net.sf.appstatus.web;

import java.io.IOException;
import java.io.InputStream;
import java.io.StringWriter;
import java.io.UnsupportedEncodingException;
import java.nio.charset.Charset;
import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import net.sf.appstatus.web.pages.Resources;

import org.apache.commons.io.IOUtils;
import org.apache.commons.lang3.text.StrBuilder;
import org.apache.commons.lang3.text.StrSubstitutor;

/**
 * Support class for generating Html tables.
 * 
 * @author Nicolas
 * 
 */
public class HtmlUtils {
	private static final String ENCODING = "UTF-8";
	private static Map<String, String> templates = new HashMap<String, String>();

	public static String applyLayout(Map<String, String> valuesMap,
			String templateName) throws IOException {
		String templateString = "";

		if (templates.containsKey(templateName)) {
			templateString = templates.get(templateName);
		} else {
			// get the file
			InputStream inputStream = Resources.class
					.getResourceAsStream("/templates/" + templateName);

			// convert to string
			StringWriter writer = new StringWriter();
			IOUtils.copy(inputStream, writer, Charset.defaultCharset());
			templateString = writer.toString();
			templates.put(templateName, templateString);
		}

		// substitution & return
		StrSubstitutor sub = new StrSubstitutor(valuesMap);
		return sub.replace(templateString);
	}

	public static String collectionToDelimitedString(Collection coll,
			String delim, String prefix, String suffix) {
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
	public static boolean generateBeginTable(StrBuilder sb, int size)
			throws UnsupportedEncodingException, IOException {
		if (size == 0) {
			sb.append("<p>No items</p>");
			return false;
		}

		sb.append("<table class=\"table table-hover table-condensed\">");
		return true;
	}

	public static void generateEndTable(StrBuilder sb, int size)
			throws UnsupportedEncodingException, IOException {

		if (size > 0) {
			sb.append("</tbody></table>");
		}
	}

	/**
	 * Outputs table headers
	 * 
	 * @param os
	 * @param cols
	 * @throws IOException
	 */
	public static void generateHeaders(StrBuilder sb, Object... cols)
			throws IOException {
		sb.append("<thead><tr>");
		for (Object obj : cols) {
			sb.append("<th>");
			if (obj != null) {

				if (obj instanceof Long) {
					Long l = (Long) obj;

				} else {
					sb.append(obj.toString());
				}
			}
			sb.append("</th>");

		}
		sb.append("</tr></thead><tbody>");
	}

	/**
	 * Outputs one table row
	 * 
	 * @param os
	 * @param status
	 * @param cols
	 * @throws IOException
	 */
	public static void generateRow(StrBuilder sb, String status, Object... cols)
			throws IOException {
		sb.append("<tr>");

		sb.append(("<td><img src='?icon=" + status + "'></td>"));

		for (Object obj : cols) {
			sb.append("<td>");
			if (obj != null) {
				sb.append(obj.toString());
			}
			sb.append("</td>");

		}
		sb.append("</tr>");
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
