package ch.so.agi.cadastralinfo.services;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpClient.Redirect;
import java.net.http.HttpClient.Version;
import java.net.http.HttpRequest;
import java.net.http.HttpRequest.Builder;
import java.net.http.HttpResponse;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@Service
public class OerebService {
    private final Logger log = LoggerFactory.getLogger(this.getClass());

    @Value("${myService}")
    private String myVar; 
    
    //@Value("${app.oerebServiceBaseUrl}")
    private String serviceBaseUrl = "https://geo.so.ch/api/oereb/";

    public String getParcel(String egrid) throws IOException, InterruptedException {
        String tmpdir = Files.createTempDirectory("cadastralinfo").toFile().getAbsolutePath();
        
        HttpClient httpClient = HttpClient.newBuilder().version(Version.HTTP_1_1).followRedirects(Redirect.ALWAYS)
                .build();

        Builder requestBuilder = HttpRequest.newBuilder();
        requestBuilder.GET().uri(URI.create(serviceBaseUrl + "/extract/reduced/xml/" + egrid));   
        requestBuilder.setHeader("Accept", "application/xml");
        HttpRequest request = requestBuilder.build();
        Path xmlFile = Paths.get(tmpdir, "oereb_" + egrid + ".xml");
        HttpResponse<Path> response = httpClient.send(request, HttpResponse.BodyHandlers.ofFile(xmlFile));
        
        return Files.readString(xmlFile);
    } 
}
