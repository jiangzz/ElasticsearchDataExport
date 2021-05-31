package com.jd.rpc.util;

import org.yaml.snakeyaml.Yaml;
import org.yaml.snakeyaml.introspector.Property;
import org.yaml.snakeyaml.nodes.NodeTuple;
import org.yaml.snakeyaml.nodes.Tag;
import org.yaml.snakeyaml.representer.Representer;

public class YamlParser {
    private static Yaml yaml;
    static {
        Representer representer = new Representer() {

            @Override
            protected NodeTuple representJavaBeanProperty(Object javaBean, Property property, Object propertyValue, Tag customTag) {
                if (propertyValue == null) {
                    return null;
                }
                else {
                    return super.representJavaBeanProperty(javaBean, property, propertyValue, customTag);
                }
            }
        };
        yaml = new Yaml(representer);
    }
    public static String dumpObject(Object object) {
        return yaml.dumpAsMap(object);
    }
    public static <T> T loadObject(String content, Class<T> type) {
        return yaml.loadAs(content, type);
    }
}
