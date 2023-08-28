package com.sagas.noops.file.outputs;

import com.sagas.noops.file.entities.FileInfo;
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
