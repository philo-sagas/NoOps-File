package com.sagas.noops.file.support;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.util.StreamUtils;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;

public class ShellExecutor {
    private static final Logger logger = LoggerFactory.getLogger(ShellExecutor.class);

    private Map<String, List<String>> shellExecutorScript;

    private Map<String, String> shellExecutorRedirect;

    public ShellExecutor(Map<String, List<String>> shellExecutorScript, Map<String, String> shellExecutorRedirect) {
        this.shellExecutorScript = shellExecutorScript;
        this.shellExecutorRedirect = shellExecutorRedirect;
    }

    public String executeScript(String path) throws IOException, InterruptedException {
        String key = String.format("[%s]", path);
        List<String> scriptList = shellExecutorScript.get(key);
        if (scriptList != null && !scriptList.isEmpty()) {
            boolean waitResult = !shellExecutorRedirect.containsKey(key);
            return command(scriptList, waitResult);
        }
        return null;
    }

    private String command(List<String> commands, boolean waitResult) throws IOException, InterruptedException {
        StringBuilder resultBuilder = new StringBuilder();
        String os = System.getProperty("os.name");
        ProcessBuilder pb = new ProcessBuilder();
        if (!waitResult) {
            pb.inheritIO();
        }
        pb.redirectErrorStream(true);
        for (String command : commands) {
            logger.info("command: {}", command);
            if (os.toLowerCase().startsWith("win")) {
                pb.command("cmd", "/c", command);
            } else {
                pb.command("sh", "-c", command);
            }
            Process process = pb.start();
            if (waitResult && process.waitFor(3, TimeUnit.MINUTES)) {
                String result = StreamUtils.copyToString(process.getInputStream(), StandardCharsets.UTF_8);
                logger.info("result: {}", result);
                resultBuilder.append("\n").append(result);
            }
        }
        if (resultBuilder.length() > 0) {
            resultBuilder.deleteCharAt(0);
        }
        return resultBuilder.toString();
    }
}
