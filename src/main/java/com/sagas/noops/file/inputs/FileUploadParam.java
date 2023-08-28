package com.sagas.noops.file.inputs;

import lombok.Data;
import org.springframework.web.multipart.MultipartFile;

@Data
public class FileUploadParam {
    private MultipartFile[] files;

    private String path;
}
