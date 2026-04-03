package com.example.qlns.DTO.Request;

public class CreateRequestRequest {
    private String title;           // Bắt buộc
    private String description;     // Tuỳ chọn
    private String fileUrl;         // Tuỳ chọn (link sau upload)
    private String fileName;        // Tuỳ chọn

    public CreateRequestRequest() {}

    public String getTitle() { return title; }
    public void setTitle(String title) { this.title = title; }

    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }

    public String getFileUrl() { return fileUrl; }
    public void setFileUrl(String fileUrl) { this.fileUrl = fileUrl; }

    public String getFileName() { return fileName; }
    public void setFileName(String fileName) { this.fileName = fileName; }
}
