package com.advancedit.ppms.models.files;

import lombok.Data;
import lombok.NoArgsConstructor;

//@Data
//@NoArgsConstructor
public class FileDescriptor {

    private String fileName;
    private String url;
    private String type;
    private String contentType;
    private String key;

    public FileDescriptor() {
    }

    public FileDescriptor(String fileName, String key, String url, String contentType) {
        this.fileName = fileName;
        this.key = key;
        this.url = url;
        this.contentType = contentType;
    }

    public String getFileName() {
        return fileName;
    }

    public void setFileName(String fileName) {
        this.fileName = fileName;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getContentType() {
        return contentType;
    }

    public void setContentType(String contentType) {
        this.contentType = contentType;
    }

    public String getKey() {
        return key;
    }

    public void setKey(String key) {
        this.key = key;
    }
}
