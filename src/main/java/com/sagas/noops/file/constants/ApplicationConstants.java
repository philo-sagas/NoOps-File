package com.sagas.noops.file.constants;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Map;

@Component
@ConfigurationProperties(prefix = "no-ops-file")
public class ApplicationConstants {
    private String adminPassword;

    private String guestPassword;

    private Map<String, List<String>> shellExecutorScript;

    private Map<String, String> shellExecutorRedirect;

    public String getAdminPassword() {
        return adminPassword;
    }

    public void setAdminPassword(String adminPassword) {
        this.adminPassword = adminPassword;
    }

    public String getGuestPassword() {
        return guestPassword;
    }

    public void setGuestPassword(String guestPassword) {
        this.guestPassword = guestPassword;
    }

    public Map<String, List<String>> getShellExecutorScript() {
        return shellExecutorScript;
    }

    public void setShellExecutorScript(Map<String, List<String>> shellExecutorScript) {
        this.shellExecutorScript = shellExecutorScript;
    }

    public Map<String, String> getShellExecutorRedirect() {
        return shellExecutorRedirect;
    }

    public void setShellExecutorRedirect(Map<String, String> shellExecutorRedirect) {
        this.shellExecutorRedirect = shellExecutorRedirect;
    }
}
