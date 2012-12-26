/**
 * This file was auto-generated by the Titanium Module SDK helper for Android
 * Appcelerator Titanium Mobile
 * Copyright (c) 2009-2010 by Appcelerator, Inc. All Rights Reserved.
 * Licensed under the terms of the Apache Public License
 * Please see the LICENSE included with this distribution for details.
 *
 */
package appersonlabs.carbon;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.appcelerator.kroll.KrollDict;
import org.appcelerator.kroll.KrollEventCallback;
import org.appcelerator.kroll.KrollFunction;
import org.appcelerator.kroll.KrollModule;
import org.appcelerator.kroll.annotations.Kroll;
import org.appcelerator.kroll.common.Log;
import org.appcelerator.titanium.io.TiBaseFile;
import org.appcelerator.titanium.io.TiFileFactory;
import org.appcelerator.titanium.proxy.TiViewProxy;
import org.appcelerator.titanium.proxy.TiWindowProxy;
import org.codehaus.jackson.JsonLocation;
import org.codehaus.jackson.JsonParseException;
import org.codehaus.jackson.JsonParser;
import org.codehaus.jackson.map.JsonMappingException;
import org.codehaus.jackson.map.ObjectMapper;

import ti.modules.titanium.ui.ActivityWindowProxy;
import ti.modules.titanium.ui.TabGroupProxy;
import ti.modules.titanium.ui.TabProxy;
import ti.modules.titanium.ui.UIModule;

@Kroll.module(name = "Carbon", id = "appersonlabs.carbon")
public class CarbonModule extends KrollModule {

    private static final String   LCAT                   = "Carbon";

    private static final String[] UIMODULE_PROXY_FORMATS = { "ti.modules.titanium.ui.%sProxy", "org.appcelerator.titanium.proxy.Ti%sProxy" };

    private static final Pattern  TEMPLATE_KEY_REGEX     = Pattern.compile("%([^%]+)%");

    private KrollFunction         createWindowFunction;

    private ObjectMapper          mapper                 = new ObjectMapper();

    private List<Stylesheet>      stylesheets            = new ArrayList<Stylesheet>();

    public CarbonModule() {
        super();
        mapper.configure(JsonParser.Feature.ALLOW_COMMENTS, true);
        mapper.configure(JsonParser.Feature.ALLOW_NUMERIC_LEADING_ZEROS, true);
        mapper.configure(JsonParser.Feature.ALLOW_SINGLE_QUOTES, true);
        mapper.configure(JsonParser.Feature.ALLOW_UNQUOTED_CONTROL_CHARS, true);
        mapper.configure(JsonParser.Feature.ALLOW_UNQUOTED_FIELD_NAMES, true);
    }

    @SuppressWarnings("unchecked")
    private TiViewProxy constructViewProxy(Map<String, Object> def, Map<String, Object> cache, Map<String,Object> templateValues) {
        if (def == null || def.size() == 0) {
            return null;
        }
        else if (def.size() > 1) {
            Log.e(LCAT, "Carbon source JSON must contain a single object");
            return null;
        }

        String key = def.keySet().iterator().next();

        // ignore elements that don't exist on this platform
        String lckey = key.toLowerCase();
        if (lckey.indexOf(".iphone") >= 0 || lckey.indexOf(".ipad") >= 0 || lckey.indexOf(".ios") >= 0 || lckey.indexOf(".mobileweb") >= 0) {
            Log.d(LCAT, String.format("Ignoring invalid element for platform: '%s'", key));
            return null;
        }

        // clean the key
        key = key.replaceAll("Ti", "");
        key = key.replaceAll("Titanium", "");
        key = key.replaceAll("\\.", "");

        // parse parameters
        Map<String, Object> params = (Map<String, Object>) def.get(key);
        if (params != null) {
            // reject elements that are not applicable for this platform
            List<String> platforms = (List<String>) params.get("platforms");
            if (platforms != null) {
                boolean isdroid = false;
                for (String platform : platforms) {
                    if ("android".equalsIgnoreCase(platform)) {
                        isdroid = true;
                        break;
                    }
                }
                if (!isdroid) {
                    return null;
                }
            }
            params.remove("platforms");
        }

        String id = (String) params.get("id");

        List<Map<String, Object>> children = (List<Map<String, Object>>) params.get("children");
        params.remove("children");

        List<Map<String, Object>> tabs = (List<Map<String, Object>>) params.get("tabs");
        params.remove("tabs");

        List<Map<String, Object>> items = (List<Map<String, Object>>) params.get("items");
        params.remove("items");

        // tab window
        Map<String, Object> window = (Map<String, Object>) params.get("window");
        params.remove("window");

        // convert constants to actual values
        for (String pk : params.keySet()) {
            Object pv = params.get(pk);
            if (pv instanceof String) {
                String str = (String) pv;
                if (str.endsWith(".FILL")) {
                    params.put(pk, UIModule.FILL);
                }
                else if (str.endsWith(".SIZE")) {
                    params.put(pk, UIModule.SIZE);
                }
            }
        }
        
        // replace template values
        if (templateValues != null && templateValues.size() > 0) {
            for (String pk : params.keySet()) {
                Object pv = params.get(pk);
                if (pv instanceof String) {
                    Matcher m = TEMPLATE_KEY_REGEX.matcher((String) pv);
                    if (m.matches()) {
                        String templateKey = m.group(1);
                        Object templateValue = templateValues.get(templateKey);
                        if (templateValue != null) {
                            params.put(pk, templateValue);
                        }
                    }
                }
            }
        }
        
        // apply TSS stylesheets
        for (Stylesheet stylesheet : stylesheets) {
            // TODO clean up the whole BaseWindow/Window dichotomy
            String k = "BaseWindow".equals(key) ? "Window" : key;
            stylesheet.applyStylesForKey(k, params);
        }

        if (items != null && items.size() > 0) {
            // convert items dictionaries to proxies
            List<TiViewProxy> itemProxies = new ArrayList<TiViewProxy>(items.size());
            for (Map<String, Object> item : items) {
                TiViewProxy itemProxy = constructViewProxy(item, cache, templateValues);
                if (itemProxy != null) {
                    itemProxies.add(itemProxy);
                }
            }
            params.put("items", itemProxies);
        }

        if (tabs != null && tabs.size() > 0) {
            // convert items dictionaries to proxies
            List<TiViewProxy> tabProxies = new ArrayList<TiViewProxy>(tabs.size());
            for (Map<String, Object> tab : tabs) {
                TiViewProxy tabProxy = constructViewProxy(tab, cache, templateValues);
                if (tabProxy != null) {
                    tabProxies.add(tabProxy);
                }
            }
            params.put("tabs", tabProxies);
        }

        if (window != null) {
            Map<String, Object> windowDef = new HashMap<String, Object>();
            windowDef.put("BaseWindow", window);
            TiViewProxy windowProxy = constructViewProxy(windowDef, cache, templateValues);
            if (windowProxy != null) {
                params.put("window", windowProxy);
            }
        }

        if (key.equalsIgnoreCase("Carbon")) {
            String path = (String) params.get("path");

            if (path != null) {

                Map<String, Object> proxy = loadUIDefFromPath(path);

                TiViewProxy rootElement = constructViewProxy(proxy, cache, templateValues);
                return rootElement;
            }
            else {
                Log.w(LCAT, "A UI type of json requires a path key value set to a json file in the local file path");
            }
        }
        else if (key.equalsIgnoreCase("Module")) {
            // TODO include CommonJS module
            return null;
        }
        else {
            TiViewProxy proxy = createProxy(key, params);
            if (proxy != null) {
                if (id != null) {
                    cache.put(id, proxy);
                }

                if (children != null) {
                    for (Map<String, Object> child : children) {
                        TiViewProxy childProxy = constructViewProxy(child, cache, templateValues);
                        if (childProxy != null) {
                            proxy.add(childProxy);
                        }
                    }
                }

                if (key.equals("TabGroup") && params.containsKey("tabs")) {

                    List<TabProxy> tabProxies = (List<TabProxy>) params.get("tabs");
                    for (TabProxy p : tabProxies) {
                        ((TabGroupProxy) proxy).addTab(p);
                    }
                }
            }
            return proxy;
        }

        return null;
    }

    @Kroll.method
    public KrollDict createFromFile(String path, @Kroll.argument(optional = true) KrollDict templateValues) {
        Map<String, Object> def = loadUIDefFromPath(path);
        Map<String, Object> cache = new HashMap<String, Object>();

        TiViewProxy rootElement = constructViewProxy(def, cache, templateValues);

        KrollDict result = new KrollDict();
        result.put("root_element", rootElement);
        result.put("proxy_cache", cache);
        return result;
    }

    @Kroll.method
    public KrollDict createFromObject(KrollDict def, @Kroll.argument(optional = true) KrollDict templateValues) {
        Map<String, Object> cache = new HashMap<String, Object>();

        TiViewProxy rootElement = constructViewProxy(def, cache, templateValues);

        KrollDict result = new KrollDict();
        result.put("root_element", rootElement);
        result.put("proxy_cache", cache);
        return result;
    }

    @Kroll.method
    public void tssFromPath(String path) {
        TiBaseFile f = TiFileFactory.createTitaniumFile(new String[] { "app://", path }, false);
        if (!f.exists()) {
            Log.w(LCAT, String.format("TSS file '%s' not found", path));
        }

        if (!f.isFile()) {
            Log.w(LCAT, String.format("Cannot read TSS file '%s'", path));
        }

        try {
            stylesheets.add(new Stylesheet(f));
        }
        catch (IOException e) {
            Log.e(LCAT, "error reading TSS file: " + e.getMessage());
        }
    }

    private TiViewProxy createProxy(String key, Map<String, Object> params) {
        if (key == null || key.length() < 1) {
            Log.e(LCAT, "key error: " + key);
        }

        TiViewProxy result = null;

        if ("Window".equals(key)) {
            // TODO determine if this is a lightweight or heavyweight window
            final ActivityWindowProxy win = new ActivityWindowProxy(); // heavyweight?
            result = win;
        }
        else if ("BaseWindow".equals(key)) {
            @SuppressWarnings("rawtypes")
            HashMap p = null; // TODO creation params?
            final TiWindowProxy win = (TiWindowProxy) createWindowFunction.call(getKrollObject(), p);
            // maybe add LW window to root activity
            win.addEventListener("open", new KrollEventCallback() {
                public void call(Object e) {
                    for (TiViewProxy child : win.getChildren()) {
                        win.add(child);
                    }
                }
            });
            result = win;
        }
        else {
            // dynamically locate the class
            for (String fmt : UIMODULE_PROXY_FORMATS) {
                try {
                    Class<?> clazz = Class.forName(String.format(fmt, key));
                    result = (TiViewProxy) clazz.newInstance();
                    break;
                }
                catch (ClassNotFoundException e) {
                    // ignore, might just be looking in the wrong package
                }
                catch (IllegalAccessException e) {
                    Log.e(LCAT, e.getMessage());
                }
                catch (InstantiationException e) {
                    Log.e(LCAT, e.getMessage());
                }
            }
        }

        if (result == null) {
            Log.w(LCAT, String.format("Could not load UI element '%s'", key));
            return null;
        }

        // TODO is this required for all proxies?
        result.setActivity(this.getActivity());

        if (params != null) {
            result.handleCreationDict(new KrollDict(params));
        }

        return result;
    }

    @SuppressWarnings("unchecked")
    private Map<String, Object> loadUIDefFromPath(String path) {
        // TODO add caching

        TiBaseFile f = TiFileFactory.createTitaniumFile(new String[] { "app://", path }, false);
        if (!f.exists()) {
            Log.w(LCAT, String.format("Carbon source file '%s' not found", path));
            return null;
        }

        if (!f.isFile()) {
            Log.w(LCAT, String.format("Cannot read Carbon source file '%s'", path));
            return null;
        }

        Map<String, Object> result = null;

        String src = null;
        try {
            src = f.read().getText();
            result = mapper.readValue(src, HashMap.class);
        }
        catch (JsonParseException e) {
            printLintedParseError(src, e);
        }
        catch (JsonMappingException e) {
            Log.e(LCAT, e.getMessage());
        }
        catch (IOException e) {
            Log.e(LCAT, e.getMessage());
        }

        return result;
    }

    private void printLintedParseError(String src, JsonParseException e) {
        if (e == null) {
            Log.e(LCAT, "Unexpected error parsing JSON");
            return;
        }

        Log.e(LCAT, "JSON parse error: " + e.getMessage());

        JsonLocation loc = e.getLocation();
        if (loc != null) {
            String[] lines = src.split("\n");
            for (int i = loc.getLineNr() - 2; i < loc.getLineNr() + 2; i++) {
                Log.e(LCAT, String.format("\t%5d: %s", (i + 1), lines[i]));
                if (i == loc.getLineNr() - 1) {
                    StringBuffer buf = new StringBuffer("\t     ");
                    for (int j = 0; j < loc.getColumnNr(); j++) {
                        buf.append(" ");
                    }
                    buf.append("^");
                    Log.e(LCAT, buf.toString());
                }
            }
        }
    }

    @Kroll.method
    public void setCreateWindow(KrollFunction createWindowFunction) {
        this.createWindowFunction = createWindowFunction;
    }
}
