package com.pragmasphere.oika.automator.custom;

import org.springframework.beans.BeanUtils;
import org.springframework.shell.table.BeanListTableModel;
import org.springframework.shell.table.BorderStyle;
import org.springframework.shell.table.Table;
import org.springframework.shell.table.TableBuilder;

import java.beans.PropertyDescriptor;
import java.util.Arrays;
import java.util.LinkedHashMap;
import java.util.List;

public final class TableUtils {
    private TableUtils() {
    }

    public static <T> Table buildTable(final Class<T> clazz, final List<T> list) {
        final LinkedHashMap<String, Object> headers = new LinkedHashMap<>();
        for (final PropertyDescriptor propertyName : BeanUtils.getPropertyDescriptors(clazz)) {
            if ("class".equals(propertyName.getName())) {
                continue;
            }
            headers.put(propertyName.getName(), propertyName.getName());
        }

        final TableBuilder tableBuilder = new TableBuilder(new BeanListTableModel<>(list, headers));
        tableBuilder.addFullBorder(BorderStyle.fancy_light);
        tableBuilder.addHeaderBorder(BorderStyle.fancy_heavy);

        return tableBuilder.build();
    }

    public static <T> Table buildTable(final List<T> list, final List<String> properties) {
        final LinkedHashMap<String, Object> headers = new LinkedHashMap<>();
        for (final String propertyName : properties) {
            headers.put(propertyName, propertyName);
        }

        final TableBuilder tableBuilder = new TableBuilder(new BeanListTableModel<>(list, headers));
        tableBuilder.addFullBorder(BorderStyle.fancy_light);
        tableBuilder.addHeaderBorder(BorderStyle.fancy_heavy);

        return tableBuilder.build();
    }

    public static <T> Table buildTable(final List<T> list, final String... properties) {
        return buildTable(list, Arrays.asList(properties));
    }
}
