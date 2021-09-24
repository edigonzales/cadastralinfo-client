package ch.so.agi.cadastralinfo.services;

import org.springframework.core.io.ClassPathResource;
import org.springframework.stereotype.Service;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;

import org.apache.tomcat.util.http.fileupload.IOUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@Service
public class GrundbuchService {
    private final Logger log = LoggerFactory.getLogger(this.getClass());

    public String getParcel(String egrid) throws IOException, InterruptedException {
        String tmpdir = Files.createTempDirectory("cadastralinfo").toFile().getAbsolutePath();
                
        InputStream resource = new ClassPathResource("CH707716772202-gbdbs.xml").getInputStream();        
        File xmlFile = Paths.get(tmpdir, egrid + ".xml").toFile();
        Files.copy(resource,xmlFile.toPath(),StandardCopyOption.REPLACE_EXISTING);
        IOUtils.closeQuietly(resource);
        
        log.info(xmlFile.getAbsolutePath());
        
        String content = Files.readString(Paths.get(xmlFile.getAbsolutePath()));
        
        return content;
    } 
}
