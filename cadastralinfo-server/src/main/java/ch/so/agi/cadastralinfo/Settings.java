package ch.so.agi.cadastralinfo;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.stereotype.Component;

@Component
@EnableConfigurationProperties
@ConfigurationProperties(prefix = "app")
public class Settings {
    private String myVar;

    private String avServiceBaseUrl;
    
    private String oerebServiceBaseUrl;
    
    public String getMyVar() {
        return myVar;
    }

    public void setMyVar(String myVar) {
        this.myVar = myVar;
    }

    public String getAvServiceBaseUrl() {
        return avServiceBaseUrl;
    }

    public void setAvServiceBaseUrl(String avServiceBaseUrl) {
        this.avServiceBaseUrl = avServiceBaseUrl;
    }

    public String getOerebServiceBaseUrl() {
        return oerebServiceBaseUrl;
    }

    public void setOerebServiceBaseUrl(String oerebServiceBaseUrl) {
        this.oerebServiceBaseUrl = oerebServiceBaseUrl;
    }
}
