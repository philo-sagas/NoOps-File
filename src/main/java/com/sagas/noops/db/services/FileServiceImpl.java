package com.sagas.noops.db.services;

import com.sagas.noops.db.entities.FileInfo;
import com.sagas.noops.db.exceptions.FileException;
import com.sagas.noops.db.inputs.FileParam;
import com.sagas.noops.db.inputs.FileUploadParam;
import com.sagas.noops.db.outputs.FileResult;
import io.micrometer.common.util.StringUtils;
import org.apache.commons.io.IOUtils;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.Arrays;
import java.util.Date;
import java.util.LinkedList;
import java.util.List;

@Service
public class FileServiceImpl implements FileService {
    @Override
    public FileResult listFiles(FileParam fileParam) {
        String currentPath = StringUtils.isBlank(fileParam.getPath()) ? "." : fileParam.getPath();
        File currentFile = new File(currentPath);

        List<FileInfo> fileList = new LinkedList<>();
        List<FileInfo> pathList = new LinkedList<>();
        List<FileInfo> breadcrumbList = new LinkedList<>();
        FileResult fileResult = new FileResult();
        fileResult.setFileList(fileList);
        fileResult.setPathList(pathList);
        fileResult.setBreadcrumbList(breadcrumbList);
        fileResult.setCurrentPath(currentPath);
        fileResult.setPreviousPath(currentFile.getParent());

        File parentFile = currentFile;
        while (parentFile != null) {
            FileInfo fileInfo = new FileInfo(parentFile.getName(), parentFile.getPath());
            breadcrumbList.add(0, fileInfo);
            parentFile = parentFile.getParentFile();
        }
        File[] childrenFiles = currentFile.listFiles();
        if (childrenFiles != null && childrenFiles.length > 0) {
            Arrays.stream(childrenFiles).forEach(file -> {
                FileInfo fileInfo = new FileInfo(file.getName(), file.getPath());
                if (file.isDirectory()) {
                    pathList.add(fileInfo);
                } else {
                    fileInfo.setSize(file.length());
                    fileInfo.setLastModified(new Date(file.lastModified()));
                    fileList.add(fileInfo);
                }
            });
        }

        return fileResult;
    }

    @Override
    public String readContent(FileParam fileParam) throws IOException {
        String currentPath = StringUtils.isBlank(fileParam.getPath()) ? "." : fileParam.getPath();
        File currentFile = new File(currentPath);
        if (currentFile.exists() && currentFile.isFile() && currentFile.canRead()) {
            byte[] buffer = new byte[128 * 1024];
            int length = IOUtils.read(new FileInputStream(currentFile), buffer);
            return new String(buffer, 0, length, StandardCharsets.UTF_8);
        } else {
            throw new FileNotFoundException("File Not Found.");
        }
    }

    @Override
    public void uploadFiles(FileUploadParam fileUploadParam) {
        String path = fileUploadParam.getPath();
        MultipartFile[] files = fileUploadParam.getFiles();
        if (files != null && files.length > 0) {
            try {
                File filepath = new File(path);
                filepath = filepath.getAbsoluteFile();
                if (!filepath.exists()) {
                    filepath.mkdirs();
                }
                for (MultipartFile source : files) {
                    if (!source.isEmpty()) {
                        File target = new File(filepath, source.getOriginalFilename());
                        source.transferTo(target);
                    }
                }
            } catch (Throwable t) {
                throw new FileException(t, path);
            }
        }
    }
}
