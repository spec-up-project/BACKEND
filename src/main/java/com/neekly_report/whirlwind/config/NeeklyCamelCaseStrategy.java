package com.neekly_report.whirlwind.config;

import com.fasterxml.jackson.databind.PropertyNamingStrategy;
import com.fasterxml.jackson.databind.cfg.MapperConfig;
import com.fasterxml.jackson.databind.introspect.*;

public class NeeklyCamelCaseStrategy extends PropertyNamingStrategy {

    private String convertTPrefixToCamelCase(String name) {
        if (name != null && name.startsWith("T_")) {
            String[] parts = name.substring(2).split("_");
            StringBuilder camelCase = new StringBuilder("t");
            for (String part : parts) {
                if (!part.isEmpty()) {
                    camelCase.append(part.substring(0, 1).toUpperCase());
                    camelCase.append(part.substring(1).toLowerCase());
                }
            }
            return camelCase.toString();
        }
        return name;
    }

    @Override
    public String nameForField(MapperConfig<?> config, AnnotatedField field, String defaultName) {
        return convertTPrefixToCamelCase(defaultName);
    }

    @Override
    public String nameForGetterMethod(MapperConfig<?> config, AnnotatedMethod method, String defaultName) {
        return convertTPrefixToCamelCase(defaultName);
    }

    @Override
    public String nameForSetterMethod(MapperConfig<?> config, AnnotatedMethod method, String defaultName) {
        return convertTPrefixToCamelCase(defaultName);
    }

    @Override
    public String nameForConstructorParameter(MapperConfig<?> config, AnnotatedParameter parameter, String defaultName) {
        return convertTPrefixToCamelCase(defaultName);
    }
}
