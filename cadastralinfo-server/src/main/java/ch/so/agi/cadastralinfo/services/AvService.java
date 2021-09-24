package ch.so.agi.cadastralinfo.services;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpClient.Redirect;
import java.net.http.HttpClient.Version;
import java.net.http.HttpHeaders;
import java.net.http.HttpRequest;
import java.net.http.HttpRequest.Builder;
import java.net.http.HttpResponse;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.github.underscore.lodash.U;

@Service
public class AvService {
    private final Logger log = LoggerFactory.getLogger(this.getClass());

    @Value("${app.avServiceBaseUrl}")
    private String serviceBaseUrl;

    public String getParcel(String egrid) throws IOException, InterruptedException {
        String tmpdir = Files.createTempDirectory("cadastralinfo").toFile().getAbsolutePath();
        
        HttpClient httpClient = HttpClient.newBuilder().version(Version.HTTP_1_1).followRedirects(Redirect.ALWAYS)
                .build();

        Builder requestBuilder = HttpRequest.newBuilder();
        requestBuilder.GET().uri(URI.create(serviceBaseUrl + "/extract/xml/geometry/" + egrid));   
        requestBuilder.setHeader("Accept", "application/xml");
        HttpRequest request = requestBuilder.build();
        log.info(request.uri().toString());
        Path xmlFile = Paths.get(tmpdir, "xxxx_" + egrid + ".xml");
        log.info(xmlFile.toAbsolutePath().toString());
        HttpResponse<Path> response = httpClient.send(request, HttpResponse.BodyHandlers.ofFile(xmlFile));

        log.info(String.valueOf(response.statusCode()));
        
        HttpHeaders headers = response.headers();
        headers.map().forEach((k, v) -> System.out.println(k + ":" + v));
        
        log.info(xmlFile.toFile().getAbsolutePath());
        
        String content = Files.readString(xmlFile);
        String contentJson = U.xmlToJson(content);
                
        return contentJson;
    } 
}
