package ch.so.agi.cadastralinfo.services;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.ClassPathResource;
import org.springframework.oxm.jaxb.Jaxb2Marshaller;
import org.springframework.stereotype.Service;

import ch.so.agi.cadastralinfo.models.av.RealEstateDPR;
import ch.so.geo.schema.agi.cadastre._0_9.extract.Extract;
import ch.so.geo.schema.agi.cadastre._0_9.extract.GetExtractByIdResponse;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;

import javax.xml.transform.stream.StreamSource;

import org.apache.tomcat.util.http.fileupload.IOUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.github.underscore.lodash.U;

@Service
public class AvService {
    private final Logger log = LoggerFactory.getLogger(this.getClass());

    @Autowired
    Jaxb2Marshaller marshaller;

    public String getParcel(String egrid) throws IOException {
        String tmpdir = Files.createTempDirectory("cadastralinfo").toFile().getAbsolutePath();
        
        InputStream resource = new ClassPathResource("av_CH357232700652.xml").getInputStream();        
        File targetFile = Paths.get(tmpdir, egrid + ".xml").toFile();
        Files.copy(resource,targetFile.toPath(),StandardCopyOption.REPLACE_EXISTING);
        IOUtils.closeQuietly(resource);
        
        log.info(targetFile.getAbsolutePath());

        StreamSource xmlSource = new StreamSource(targetFile);
        GetExtractByIdResponse obj = (GetExtractByIdResponse) marshaller.unmarshal(xmlSource);
        Extract xmlExtract = obj.getExtract();
        ch.so.geo.schema.agi.cadastre._0_9.extract.RealEstateDPR xmlRealEstate = xmlExtract.getRealEstate();
        
        RealEstateDPR realEstateDPR = new RealEstateDPR();
        realEstateDPR.setEgrid(egrid);
        realEstateDPR.setLandRegistryArea(xmlRealEstate.getLandRegistryArea());
        
        String content = Files.readString(targetFile.toPath());
        String contentJson = U.xmlToJson((String) content);
        
        
        
        return contentJson;

    } 
}
