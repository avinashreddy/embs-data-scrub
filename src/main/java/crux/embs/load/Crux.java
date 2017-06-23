package crux.embs.load;

import autovalue.shaded.com.google.common.common.collect.ImmutableMap;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.common.base.Preconditions;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.FileSystemResource;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.StringUtils;
import org.springframework.web.client.RestTemplate;

import java.util.Map;

@Component
public class Crux {

    private final String apiurl;

    private final String apiKey;

    private final RestTemplate restTemplate = new RestTemplate();

    private final ObjectMapper objectMapper = new ObjectMapper();

    @Autowired
    public Crux(@Value("${cruxApiUrl}") String apiurl, @Value("${cruxApiKey}")String apiKey) {
        this.apiurl = apiurl;
        this.apiKey = apiKey;
    }

    public boolean tableExists(String datasetId, String tableName) {
        String obj = restTemplate.getForObject(
                apiurl + "/datasets/{datasetId}/tables/{tableName}?apikey={apikey}",
                String.class,
                 datasetId,  tableName, apiKey);
        return StringUtils.hasText(obj);
    }

    public void createTable(String datasetId, String tableName, String schema, String description) {
        Preconditions.checkState(!tableExists(datasetId, tableName), "Table with name %s exists", tableName);
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);

        HttpEntity<String> entity = new HttpEntity<>(toJson(ImmutableMap.of("name", tableName, "schema", schema)) ,headers);
        String table = restTemplate.postForObject(
                apiurl + "/datasets/{datasetId}/tables?apikey={apikey}",
                entity,
                String.class,
                datasetId  , apiKey);
    }

    public void uploadFile(String datasetId, String fileName, String filePath) {

        LinkedMultiValueMap<String, Object> map = new LinkedMultiValueMap<>();
        map.add(fileName, new FileSystemResource(filePath));
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.MULTIPART_FORM_DATA);

        HttpEntity<LinkedMultiValueMap<String, Object>> entity = new HttpEntity<>(map, headers);
        ResponseEntity<String> result = restTemplate.postForEntity(apiurl + "/pushfile/{datasetId}?apikey={apikey}",
                entity,
                String.class,
                datasetId  , apiKey);

    }

    public void loadFileToTable(String datasetId, String fileName, String tableName, char delimiter) {
        Preconditions.checkState(tableExists(datasetId, tableName), "No Table with name %s", tableName);
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        HttpEntity<String> entity = new HttpEntity<>(toJson(ImmutableMap.of("fileName", fileName, "delimiter", delimiter, "maxBadRecords", 0)) ,headers);

        ResponseEntity<String> result  = restTemplate.postForEntity(
                apiurl + "/datasets/{datasetId}/tables/{tableName}/loadfile?apikey={apikey}",
                entity,
                String.class,
                datasetId,  tableName, apiKey);
    }


    private String toJson(Object o) {
        try {
            return objectMapper.writeValueAsString(o);
        } catch (JsonProcessingException e) {
            throw new IllegalStateException(e);
        }
    }



    public static void main(String[] args) {
        String datsetid = "WyJQcm9maWxlIiwidU9tTFhtRGYydWJXdDRabkJzamR3dlFYVlB2MSIsIkRhdGFTZXQiLDU2Mjk0OTk1MzQyMTMxMjBd";
        Crux crux = new Crux("http://localhost:8082/api", "a84f086e70b6b14a85c47a9dc1f6da88");
//        System.out.println(crux.tableExists(datsetid, "t_01"));
//        System.out.println(crux.tableExists(datsetid, "a_01"));
//        crux.createTable(datsetid, "xxx01", "a:string", "");

//        crux.uploadFile(datsetid, "abcdef.csv", "/Users/avinash.palicharla/src/EMBS/work/03/csvgzip/ARMINDEX.DAT.0.csv.gz");
        for(int i = 0; i< 10; i++) {
            crux.loadFileToTable(datsetid, "a", "t_01", ',');
        }
    }


}
