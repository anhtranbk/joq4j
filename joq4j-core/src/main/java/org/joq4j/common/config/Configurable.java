package org.joq4j.common.config;

import java.lang.reflect.Field;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

public interface Configurable {

    default void configure(Config config) {
        Class<?> cls = this.getClass();
        for (Field field : cls.getDeclaredFields()) {
            if (!field.isAnnotationPresent(ConfigKey.class)) continue;
            try {
                field.setAccessible(true);
                ConfigKey configKey = field.getAnnotation(ConfigKey.class);
                String name = configKey.value();
                String defVal = configKey.defaultValue();
                Class<?> type = field.getType();

                if (type.isAssignableFrom(Integer.class) || type.equals(int.class)) {
                    field.setInt(this, config.getInt(name, Integer.parseInt(defVal)));
                } else if (type.isAssignableFrom(Long.class) || type.equals(long.class)) {
                    field.setLong(this, config.getLong(name, Long.parseLong(defVal)));
                } else if (type.isAssignableFrom(Boolean.class) || type.equals(boolean.class)) {
                    field.setBoolean(this, config.getBool(name, Boolean.parseBoolean(defVal)));
                } else if (type.isAssignableFrom(Double.class) || type.equals(double.class)) {
                    field.setDouble(this, config.getDouble(name, Double.parseDouble(defVal)));
                } else if (type.isAssignableFrom(Float.class) || type.equals(float.class)) {
                    field.setFloat(this, (float) config.getDouble(name, Float.parseFloat(defVal)));
                } else if (type.isAssignableFrom(Date.class)) {
                    Date defDate = null;
                    try {
                        SimpleDateFormat df = new SimpleDateFormat(configKey.datetimeFormat());
                        defDate = df.parse(defVal);
                    } catch (ParseException ignored) {}
                    field.set(this, config.getDateTime(name, configKey.datetimeFormat(), defDate));
                } else if (type.isAssignableFrom(List.class)) {
                    field.set(this, config.getList(name));
                } else {
                    field.set(this, config.getString(name, defVal));
                }
            } catch (Exception e) {
                throw new ConfigException(e);
            } finally {
                field.setAccessible(false);
            }
        }
    }
}
