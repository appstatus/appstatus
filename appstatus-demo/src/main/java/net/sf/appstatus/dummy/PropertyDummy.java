package net.sf.appstatus.dummy;

import java.util.HashMap;
import java.util.Map;

import net.sf.appstatus.PropertyProvider;

public class PropertyDummy implements PropertyProvider {

	public Map<String, String> getProperties() {
		HashMap<String, String> hm = new HashMap<String,String>();
		hm.put("version", "1.0-demo");
		return hm;
	}

}
