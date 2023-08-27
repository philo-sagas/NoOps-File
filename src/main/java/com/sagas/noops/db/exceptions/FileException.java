package com.sagas.noops.db.exceptions;

public class FileException extends RuntimeException {
    private String path;

    public FileException(Throwable cause, String path) {
        super(cause);
        this.path = path;
    }

    public String getPath() {
        return path;
    }
}
