package helloworld07.controller;

import org.springframework.http.*;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.client.RestTemplate;

@Controller
public class HelloController {
    private final RestTemplate restTemplate;

    public HelloController(RestTemplate restTemplate) {
        this.restTemplate = restTemplate;
    }

    @GetMapping("")
    public String index() {
        return "index";
    }

    @GetMapping("/{id}")
    public ResponseEntity<byte[]> icon(@PathVariable("id") String id) {
        HttpHeaders headers = new HttpHeaders();
        headers.set(HttpHeaders.ACCEPT, "image/png");

        return restTemplate.exchange(String.format("/%s", id), HttpMethod.GET, new HttpEntity<>(headers), byte[].class);
    }
}
