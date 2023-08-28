package com.sagas.noops.file.entities;

import lombok.Data;

import java.util.Date;

@Data
public class FileInfo {
    private static final String[] units = {"B", "KB", "MB", "GB", "TB"};
    private static final long[] conversions = {1024, 1024, 1024, 1024, 1024};

    private String name;

    private String path;

    private long size;

    private Date lastModified;

    public FileInfo(String name, String path) {
        this.name = name;
        this.path = path;
    }

    public String getSize() {
        long pre;
        String unit;
        long value = 0L;
        int i = 0;
        long l = size;
        do {
            pre = value;
            value = l % conversions[i];
            unit = units[i];
            l /= conversions[i];
            i++;
        } while (i < conversions.length && l > 0);
        return pre <= 0
                ? String.format("%d %s", value, unit)
                : String.format("%.2f %s", value + (1.0 * pre / conversions[i - 1]), unit);
    }
}
