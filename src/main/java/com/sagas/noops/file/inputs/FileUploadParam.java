package com.sagas.noops.file.inputs;

import lombok.Data;
import org.springframework.http.codec.multipart.FilePart;

@Data
public class FileUploadParam {
    private FilePart[] files;

    private String path;
}
