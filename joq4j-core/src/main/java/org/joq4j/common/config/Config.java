package org.joq4j.common.config;

import org.joq4j.common.utils.DateTimes;
import org.joq4j.common.utils.Strings;

import javax.naming.ConfigurationException;
import javax.security.auth.login.Configuration;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.PrintStream;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.HashMap;
import java.util.LinkedHashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Properties;
import java.util.Set;
import java.util.function.Function;

@SuppressWarnings("unchecked")
public class Config extends Properties {

    private static final String LIST_DELIMITER = ",";
    private static final String CONF_ENV = "job4j.conf";
    private static final String DEFAULT_NAME = "job4j.properties";

    /**
     * @param path Path to file use as default resource to load properties
     *             when init new Configuration object with empty constructor
     */
    public static void setDefaultResourcePath(String path) {
        defaultResPath = path;
    }

    private static final Properties defaultProps = new Properties();
    private static String defaultResPath = System.getProperty(CONF_ENV);

    static {
        try {
            // default properties from resource file
            defaultProps.load(Configuration.class.getClassLoader().getResourceAsStream(DEFAULT_NAME));
        } catch (IOException e) {
            Throwable t = new IOException("Cannot find config.properties in classpath");
            t.printStackTrace(new PrintStream(System.out));
        }
    }

    public Config() {
        this(defaultResPath);
    }

    public Config(String path) {
        try {
            // extra properties from external file have higher priority
            addResource(path);
        } catch (IOException e) {
            throw new ConfigException(e);
        }
    }

    public Config(InputStream is) {
        try {
            addResource(is);
        } catch (IOException e) {
            throw new ConfigException(e);
        }
    }

    public void addResource(InputStream is) throws IOException {
        this.addResource(is, false);
    }

    public void addResource(InputStream is, boolean closeAfterLoad) throws IOException {
        try {
            this.load(is);
        } finally {
            if (closeAfterLoad) is.close();
        }
    }

    public void addResource(String path) throws IOException {
        if (path != null) {
            this.addResource(new FileInputStream(path), true);
        }
    }

    @Override
    public String getProperty(String key) {
        String value = super.getProperty(key);
        return (value != null) ? value : defaultProps.getProperty(key);
    }

    public <T> void set(String key, T val) {
        setProperty(key, val.toString());
    }

    public void setString(String key, String val) {
        setProperty(key, val);
    }

    public void setInt(String key, int val) {
        setProperty(key, String.valueOf(val));
    }

    public void setLong(String key, long val) {
        setProperty(key, String.valueOf(val));
    }

    public void setDouble(String key, double val) {
        setProperty(key, String.valueOf(val));
    }

    public void setFloat(String key, float val) {
        setProperty(key, String.valueOf(val));
    }

    public void setBool(String key, boolean val) {
        setProperty(key, String.valueOf(val));
    }

    public void setDateTime(String key, Date date) {
        setDateTime(key, date, DateTimes.ISO_FORMAT);
    }

    public void setDateTime(String key, Date date, String format) {
        setProperty(key, DateTimes.format(date, format));
    }

    public void setList(String key, String ...elements) {
        setList(key, Arrays.asList(elements));
    }

    public void setList(String key, List<String> list) {
        setProperty(key, Strings.join(list, LIST_DELIMITER));
    }

    public void setClass(String key, Class<?> cls) {
        setProperty(key, cls.getName());
    }

    public void setClasses(String key, Class<?> ...classes) {
        setClasses(key, Arrays.asList(classes));
    }

    public void setClasses(String key, Collection<Class<?>> classes) {
        List<String> clsNames = new ArrayList<>(classes.size());
        for (Class<?> cls : classes) {
            clsNames.add(cls.getName());
        }
        setList(key, clsNames);
    }

    public <T> T get(String key, T defVal) {
        try {
            return (T) get(key);
        } catch (Exception ignored) {
            return defVal;
        }
    }

    public String getString(String key, String defVal) {
        return getProperty(key, defVal);
    }

    public int getInt(String key, int defVal) {
        try {
            return Integer.parseInt(getProperty(key));
        } catch (Exception ignored) {
            return defVal;
        }
    }

    public long getLong(String key, long defVal) {
        try {
            return Long.parseLong(getProperty(key));
        } catch (Exception ignored) {
            return defVal;
        }
    }

    public double getDouble(String key, double defVal) {
        try {
            return Double.parseDouble(getProperty(key));
        } catch (Exception ignored) {
            return defVal;
        }
    }

    public double getFloat(String key, float defVal) {
        try {
            return Float.parseFloat(getProperty(key));
        } catch (Exception ignored) {
            return defVal;
        }
    }

    public boolean getBool(String key, boolean defVal) {
        try {
            return Boolean.parseBoolean(getProperty(key));
        } catch (Exception ignored) {
            return defVal;
        }
    }

    public List<String> getList(String key) {
        try {
            return Arrays.asList(getProperty(key).split(LIST_DELIMITER));
        } catch (Exception ignored) {
            return Collections.emptyList();
        }
    }

    public Date getDateTime(String key, Date defVal) {
        return getDateTime(key, DateTimes.ISO_FORMAT, defVal);
    }

    public Date getDateTime(String key, String format, Date defVal) {
        try {
            DateFormat df = new SimpleDateFormat(format);
            return df.parse(getProperty(key));
        } catch (Exception e) {
            return defVal;
        }
    }

    public Class<?> getClass(String key, Class<?> defVal) {
        try {
            return Class.forName(getProperty(key));
        } catch (Exception ignored) {
            return defVal;
        }
    }

    public Collection<Class<?>> getClasses(String key) throws ClassNotFoundException {
        List<Class<?>> classes = new ArrayList<>();
        for (String className : getList(key)) {
            classes.add(Class.forName(className));
        }
        return classes;
    }

    public Map<String, Object> asMap() {
        Map<String, Object> map = new HashMap<>();
        for (Object key: keySet()) {
            map.put(key.toString(), this.get(key));
        }
        return map;
    }

    @Override
    public synchronized String toString() {
        Set<Object> keySet = new LinkedHashSet<>();
        keySet.addAll(this.keySet());
        keySet.addAll(defaultProps.keySet());

        List<Object> list = new ArrayList<>(keySet);
        list.sort(Comparator.comparing(Object::toString));

        StringBuilder sb = new StringBuilder("Configuration properties:\n");
        list.forEach(key -> sb.append(String.format(Locale.US, "\t%s = %s\n",
                key.toString(), getProperty(key.toString(), ""))));
        return sb.toString();
    }
}
