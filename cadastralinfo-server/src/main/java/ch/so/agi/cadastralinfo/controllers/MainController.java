package ch.so.agi.cadastralinfo.controllers;

import java.io.IOException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import ch.so.agi.cadastralinfo.Settings;
import ch.so.agi.cadastralinfo.services.AvService;
import ch.so.geo.schema.agi.cadastre._0_9.extract.GetExtractByIdResponse;

@RestController
public class MainController {
    private final Logger log = LoggerFactory.getLogger(this.getClass());

    @Autowired
    Settings settings;
    
    @Autowired
    AvService avService;
    
    @GetMapping("/settings")
    public ResponseEntity<?> getSettings() {
        return ResponseEntity.ok().body(settings);
    }
    
    //@GetMapping("/av")
    @RequestMapping(value = "/av", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
    public String getAv(@RequestParam(value = "egrid", required = true) String egrid) {
        try {
            return avService.getParcel(egrid);
        } catch (IOException | InterruptedException e) {
            e.printStackTrace();
            throw new IllegalArgumentException("could not process: CH...");
            // TODO: return json
        }
        //return ResponseEntity.ok().body("av");
    }
}
