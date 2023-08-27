package com.sagas.noops.db.outputs;

import com.sagas.noops.db.entities.FileInfo;
import lombok.Data;

import java.util.List;

@Data
public class FileResult {
    private String currentPath;

    private String previousPath;

    private List<FileInfo> fileList;

    private List<FileInfo> pathList;

    private List<FileInfo> breadcrumbList;
}
