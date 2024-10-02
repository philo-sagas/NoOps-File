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
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.util.UriComponentsBuilder;
import reactor.core.publisher.Mono;

import java.io.File;
import java.io.FileNotFoundException;

@Log4j2
@Controller
@RequestMapping("/file")
public class FileController {
    public static final String HAS_ROLE_ADMIN = "hasRoleAdmin";

    @Autowired
    private FileService fileService;

    @RequestMapping
    public String execute(FileParam fileParam, Model model, Authentication authentication) {
        try {
            if (StringUtils.hasText(fileParam.getErrorMessage())) {
                model.addAttribute(DefaultExceptionHandler.ERROR_MESSAGE_NAME, fileParam.getErrorMessage());
            }
            boolean hasRoleAdmin = authentication.getAuthorities().stream().anyMatch(ga -> "ROLE_ADMIN".equals(ga.getAuthority()));
            model.addAttribute(HAS_ROLE_ADMIN, hasRoleAdmin);

            String currentPath = StringUtils.hasText(fileParam.getPath()) ? fileParam.getPath() : ".";
            File currentFile = new File(currentPath);
            if (!currentFile.exists()) {
                model.addAttribute(DefaultExceptionHandler.ERROR_MESSAGE_NAME,
                        String.format("Path (%s) Not Found!", currentPath));
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
    public Mono<ResultModel<String>> preview(FileParam fileParam) {
        try {
            String content = fileService.readContent(fileParam);
            return Mono.just(ResultModel.success(content));
        } catch (Throwable t) {
            log.error(t, t);
            return Mono.just(ResultModel.failure(t.getMessage()));
        }
    }

    @GetMapping("/download")
    public Object download(FileParam fileParam) {
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
            return UriComponentsBuilder.fromPath("redirect:/file")
                    .queryParam(DefaultExceptionHandler.ERROR_MESSAGE_NAME, t.getMessage())
                    .encode().toUriString();
        }
    }

    @PreAuthorize("hasRole('ROLE_ADMIN')")
    @PostMapping("/upload")
    public Mono<String> upload(Mono<FileUploadParam> fileUploadParamMono) {
        return fileUploadParamMono
                .doOnNext(fileService::uploadFiles)
                .map(param -> UriComponentsBuilder.fromPath("redirect:/file")
                        .queryParam(DefaultExceptionHandler.PATH_NAME, param.getPath())
                        .encode().toUriString());
    }
}
