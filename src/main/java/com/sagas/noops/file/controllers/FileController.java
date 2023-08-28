package com.sagas.noops.file.controllers;

import com.sagas.noops.file.inputs.FileParam;
import com.sagas.noops.file.inputs.FileUploadParam;
import com.sagas.noops.file.outputs.FileResult;
import com.sagas.noops.file.outputs.ResultModel;
import com.sagas.noops.file.services.FileService;
import lombok.extern.log4j.Log4j2;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.FileSystemResource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.io.File;
import java.io.FileNotFoundException;

@Log4j2
@Controller
@RequestMapping("/file")
public class FileController {
    @Autowired
    private FileService fileService;

    @RequestMapping
    public String execute(FileParam fileParam, Model model, RedirectAttributes redirectAttributes) {
        try {
            String currentPath = StringUtils.hasText(fileParam.getPath()) ? fileParam.getPath() : ".";
            File currentFile = new File(currentPath);
            if (!currentFile.exists()) {
                redirectAttributes.addFlashAttribute(DefaultExceptionHandler.ERROR_MESSAGE_NAME,
                        String.format("Path (%s) Not Found!", currentPath));
                return "redirect:/file";
            }
            FileResult fileResult = fileService.listFiles(fileParam);
            model.addAttribute(fileResult);
        } catch (Throwable t) {
            log.error(t, t);
            model.addAttribute(DefaultExceptionHandler.ERROR_MESSAGE_NAME, t.getMessage());
        }
        return "index";
    }

    @ResponseBody
    @GetMapping("/preview")
    public ResultModel<String> preview(FileParam fileParam) {
        try {
            String content = fileService.readContent(fileParam);
            return ResultModel.success(content);
        } catch (Throwable t) {
            log.error(t, t);
            return ResultModel.failure(t.getMessage());
        }
    }

    @GetMapping("/download")
    public Object download(FileParam fileParam, RedirectAttributes redirectAttributes) {
        try {
            String currentPath = StringUtils.hasText(fileParam.getPath()) ? fileParam.getPath() : ".";
            File currentFile = new File(currentPath);
            if (currentFile.exists() && currentFile.isFile() && currentFile.canRead()) {
                HttpHeaders headers = new HttpHeaders();
                headers.setContentType(MediaType.APPLICATION_OCTET_STREAM);
                headers.setContentDispositionFormData("attachment", currentFile.getName());
                return ResponseEntity.ok()
                        .headers(headers)
                        .contentLength(currentFile.length())
                        .body(new FileSystemResource(currentFile));
            } else {
                throw new FileNotFoundException("File Not Found.");
            }
        } catch (Throwable t) {
            log.error(t, t);
            redirectAttributes.addFlashAttribute(DefaultExceptionHandler.ERROR_MESSAGE_NAME, t.getMessage());
        }
        return "redirect:/file";
    }

    @PreAuthorize("hasRole('ROLE_ADMIN')")
    @PostMapping("/upload")
    public String upload(FileUploadParam fileUploadParam, RedirectAttributes redirectAttributes) {
        fileService.uploadFiles(fileUploadParam);
        redirectAttributes.addAttribute(DefaultExceptionHandler.PATH_NAME, fileUploadParam.getPath());
        return "redirect:/file";
    }
}
