package ch.so.agi.cadastralinfo.services;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.ClassPathResource;
import org.springframework.oxm.jaxb.Jaxb2Marshaller;
import org.springframework.stereotype.Service;

import ch.so.agi.cadastralinfo.models.av.RealEstateDPR;
import ch.so.geo.schema.agi.cadastre._0_9.extract.Extract;
import ch.so.geo.schema.agi.cadastre._0_9.extract.GetExtractByIdResponse;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
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
import java.nio.file.StandardCopyOption;

import javax.xml.transform.stream.StreamSource;

import org.apache.tomcat.util.http.fileupload.IOUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.github.underscore.lodash.U;

@Service
public class GrundbuchService {
    private final Logger log = LoggerFactory.getLogger(this.getClass());

//    @Value("${app.avServiceBaseUrl}")
//    private String serviceBaseUrl;

//    @Autowired
//    Jaxb2Marshaller marshaller;

    public String getParcel(String egrid) throws IOException, InterruptedException {
        String tmpdir = Files.createTempDirectory("cadastralinfo").toFile().getAbsolutePath();
                
        InputStream resource = new ClassPathResource("CH707716772202-gbdbs.xml").getInputStream();        
        File xmlFile = Paths.get(tmpdir, egrid + ".xml").toFile();
        Files.copy(resource,xmlFile.toPath(),StandardCopyOption.REPLACE_EXISTING);
        IOUtils.closeQuietly(resource);
        
        log.info(xmlFile.getAbsolutePath());
        
        String content = Files.readString(Paths.get(xmlFile.getAbsolutePath()));
        String contentJson = U.xmlToJson((String) content);
        
        return contentJson;
    } 
}
