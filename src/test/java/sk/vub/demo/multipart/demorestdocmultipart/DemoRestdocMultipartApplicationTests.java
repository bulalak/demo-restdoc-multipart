package sk.vub.demo.multipart.demorestdocmultipart;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.github.tomakehurst.wiremock.client.WireMock;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.restdocs.AutoConfigureRestDocs;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;

import java.nio.charset.StandardCharsets;
import java.util.UUID;

import static com.github.tomakehurst.wiremock.client.WireMock.*;
import static org.springframework.cloud.contract.wiremock.restdocs.SpringCloudContractRestDocs.dslContract;
import static org.springframework.cloud.contract.wiremock.restdocs.WireMockRestDocs.verify;
import static org.springframework.restdocs.mockmvc.MockMvcRestDocumentation.document;
import static org.springframework.restdocs.mockmvc.RestDocumentationRequestBuilders.fileUpload;
import static org.springframework.restdocs.operation.preprocess.Preprocessors.preprocessRequest;
import static org.springframework.restdocs.operation.preprocess.Preprocessors.prettyPrint;
import static org.springframework.restdocs.payload.PayloadDocumentation.fieldWithPath;
import static org.springframework.restdocs.payload.PayloadDocumentation.requestPartFields;
import static org.springframework.restdocs.request.RequestDocumentation.partWithName;
import static org.springframework.restdocs.request.RequestDocumentation.requestParts;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@RunWith(SpringRunner.class)
@SpringBootTest
@AutoConfigureRestDocs(outputDir = "target/generated-snippets")
@AutoConfigureMockMvc
public class DemoRestdocMultipartApplicationTests {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Test
    public void contextLoads() throws Exception {

        MetadataDTO metadataDTO = new MetadataDTO();
        metadataDTO.setName("test");
        MockMultipartFile file = new MockMultipartFile("file", "contract.pdf", MediaType.APPLICATION_PDF_VALUE, "pdfcontent".getBytes(StandardCharsets.UTF_8));
        MockMultipartFile metadata = new MockMultipartFile("metadata", "", MediaType.APPLICATION_JSON_VALUE, objectMapper.writeValueAsString(metadataDTO).getBytes(StandardCharsets.UTF_8));
        this.mockMvc.perform(fileUpload("/test")
                .file(file)
                .file(metadata))
                .andExpect(status().isNoContent())
                .andDo(verify()
                        .wiremock(
                                post(WireMock.urlPathEqualTo("/test"))
                                        .withHeader(HttpHeaders.CONTENT_TYPE, WireMock.containing(MediaType.MULTIPART_FORM_DATA_VALUE))
                                        .withMultipartRequestBody(
                                                aMultipart()
                                                        .withHeader("Content-Disposition", containing("name=file"))
                                                        .withHeader("Content-Type", WireMock.equalTo(MediaType.APPLICATION_PDF_VALUE))
                                                        .withBody(equalTo("pdfcontent"))
                                        )
                                        .withMultipartRequestBody(
                                                aMultipart()
                                                        .withHeader("Content-Disposition", containing("name=metadata"))
                                                        .withHeader("Content-Type", WireMock.containing(MediaType.APPLICATION_JSON_VALUE))
                                                        .withBody(WireMock.matchingJsonPath("$[?(@.name == 'test')]"))
                                        )
                        ).stub("testMultipartXXX"))

//                .andDo(print())
                .andDo(document("testMultipart",
                        preprocessRequest(prettyPrint()),
                        requestParts(
                                partWithName("file").description("The file to upload"),
                                partWithName("metadata").description("metadata")
                        ),
                        requestPartFields("metadata",
                                fieldWithPath("name").description("name")
                        )
                        , dslContract()
                ))
        ;

    }

}
