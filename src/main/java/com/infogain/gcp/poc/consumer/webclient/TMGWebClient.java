package com.infogain.gcp.poc.consumer.webclient;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import lombok.SneakyThrows;
import org.springframework.http.MediaType;
import org.springframework.web.reactive.function.client.ClientResponse;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

public class TMGWebClient {


    private WebClient client = WebClient.create("http://localhost:8080");

    private Mono<ClientResponse> result = client.post()
            .uri("/config/read")
            .bodyValue(this.requestBody())
            .accept(MediaType.APPLICATION_JSON)
            .exchange();

    public String getResult() {
        return ">> result = " + result.flatMap(res -> res.bodyToMono(String.class)).block();
    }

    @SneakyThrows
    public String requestBody() {
        ObjectMapper mapper = new ObjectMapper();
        ObjectNode rootNode = mapper.createObjectNode();

        rootNode.put("configType", "teletype_config");

        ObjectNode childNode1 = mapper.createObjectNode();
        rootNode.set("metaData", childNode1);

        String jsonString = mapper.writerWithDefaultPrettyPrinter().writeValueAsString(rootNode);

        return jsonString;
    }
}
