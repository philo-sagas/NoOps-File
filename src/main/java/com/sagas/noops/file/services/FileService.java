package com.sagas.noops.file.services;

import com.sagas.noops.file.inputs.FileParam;
import com.sagas.noops.file.inputs.FileUploadParam;
import com.sagas.noops.file.outputs.FileResult;

import java.io.IOException;

public interface FileService {
    FileResult listFiles(FileParam fileParam);

    String readContent(FileParam fileParam) throws IOException;

    void uploadFiles(FileUploadParam fileUploadParam);
}
