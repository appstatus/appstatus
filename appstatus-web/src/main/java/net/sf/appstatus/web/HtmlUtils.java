package net.sf.appstatus.web;

import static java.text.DateFormat.getDateTimeInstance;
import static org.apache.commons.lang3.StringEscapeUtils.escapeHtml4;
import static org.apache.commons.lang3.StringEscapeUtils.escapeJson;

import java.io.IOException;
import java.io.InputStream;
import java.io.StringWriter;
import java.io.UnsupportedEncodingException;
import java.nio.charset.Charset;
import java.text.DateFormat;
import java.util.Collection;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.apache.commons.io.IOUtils;
import org.apache.commons.lang3.text.StrBuilder;
import org.apache.commons.lang3.text.StrSubstitutor;

import net.sf.appstatus.web.pages.Resources;

/**
 * Support class for generating Html tables.
 *
 * @author Nicolas
 *
 */
public class HtmlUtils {
	private static final String ENCODING = "UTF-8";
	private static Map<String, String> templates = new HashMap<String, String>();

	public static String applyLayout(Map<String, String> valuesMap, String templateName) throws IOException {
		String templateString = "";

		if (templates.containsKey(templateName)) {
			templateString = templates.get(templateName);
		} else {
			// get the file
			InputStream inputStream = Resources.class.getResourceAsStream("/templates/" + templateName);

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
		return "<a href='#' title='" + escapeHtml4(itemsList) + "'>" + items.size() + "</a>"
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
	public static boolean generateBeginTable(StrBuilder sb, int size) throws UnsupportedEncodingException, IOException {
		if (size == 0) {
			sb.append("<p>No items</p>");
			return false;
		}

		sb.append("<table class=\"table table-hover table-condensed\">");
		return true;
	}

	public static void generateEndTable(StrBuilder sb, int size) throws UnsupportedEncodingException, IOException {

		if (size > 0) {
			sb.append("</tbody></table>");
		}
	}

	/**
	 * Outputs table headers.
	 *
	 * <p>
	 * <b>WARNING</b> : this method accepts HTML content as table headers. Any
	 * sensitive value must be encoded before calling this method.
	 *
	 * @param sb
	 *            The target string builder.
	 * @param cols
	 *            Column titles (HTML).
	 */
	public static void generateHeaders(StrBuilder sb, Object... cols) {
		sb.append("<thead><tr>");
		for (Object obj : cols) {
			sb.append("<th>");
			if (obj != null) {

				if (obj instanceof Long) {
					sb.append(((Long) obj).longValue());
				} else {
					sb.append(obj.toString());
				}
			}
			sb.append("</th>");
		}
		sb.append("</tr></thead><tbody>");
	}

	/**
	 * Outputs one table row.
	 * <p>
	 * <b>WARNING</b> : this method accepts HTML content as row content. Any
	 * sensitive value must be encoded before calling this method.
	 *
	 *
	 * @param sb
	 *            The target string builder.
	 * @param status
	 *            status class name.
	 * @param cols
	 *            Column titles (HTML).
	 * @throws IOException
	 */
	public static void generateRow(StrBuilder sb, String status, Object... cols) throws IOException {
		sb.append("<tr>");

		sb.append(("<td class='icon'><img src='?icon=" + escapeHtml4(status) + "'></td>"));

		for (Object obj : cols) {
			sb.append("<td>");
			if (obj != null) {

				if (obj instanceof Date) {
					DateFormat dateFormat = getDateTimeInstance(DateFormat.SHORT, DateFormat.MEDIUM);
					sb.append(dateFormat.format((Date) obj));
				} else {
					sb.append(obj.toString());
				}
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

	public static String json(String name, Object o) {
		String result = "\"" + escapeJson(name) + "\" :";

		if (o == null) {
			result = result + "null";
		} else if (o instanceof String) {
			result = result + "\"" + escapeJson((String) o) + "\"";
		} else if (o instanceof Integer) {
			result = result + ((Integer) o).intValue();
		} else if (o instanceof Double) {
			result = result + ((Double) o).doubleValue();
		} else if (o instanceof Long) {
			result = result + ((Long) o).longValue();
		}

		return result + "\n";
	}
}
