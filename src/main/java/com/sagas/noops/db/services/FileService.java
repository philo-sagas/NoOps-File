package com.sagas.noops.db.services;

import com.sagas.noops.db.inputs.FileParam;
import com.sagas.noops.db.inputs.FileUploadParam;
import com.sagas.noops.db.outputs.FileResult;

import java.io.FileNotFoundException;
import java.io.IOException;

public interface FileService {
    FileResult listFiles(FileParam fileParam);

    String readContent(FileParam fileParam) throws IOException;

    void uploadFiles(FileUploadParam fileUploadParam);
}
