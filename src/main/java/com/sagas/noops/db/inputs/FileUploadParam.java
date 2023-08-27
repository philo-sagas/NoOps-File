package com.sagas.noops.db.inputs;

import lombok.Data;
import org.springframework.web.multipart.MultipartFile;

@Data
public class FileUploadParam {
    MultipartFile[] files;

    private String path;
}
