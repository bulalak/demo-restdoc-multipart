package sk.vub.demo.multipart.demorestdocmultipart;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestPart;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

@RestController
public class MultipartTestController {

    private final Log logger = LogFactory.getLog(getClass());

    @PostMapping("/test")
    public ResponseEntity<Void> postMultipart(@RequestPart("metadata") MetadataDTO metadataDTO, @RequestPart("file") MultipartFile multipartFile) {
        logger.info(metadataDTO);
        return ResponseEntity.noContent().build();
    }
}
