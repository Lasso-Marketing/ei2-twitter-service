package io.lassomarketing.ei2.twitter.controller;

import io.lassomarketing.ei2.common.response.EI2ResponseBody;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

@Slf4j
@AllArgsConstructor
@RestController
public class PingController {

    /**
     * @return EI2ResponseBody with 'pong' content
     */
    @GetMapping("/ping")
    public EI2ResponseBody<Map<String, String>> test() {
        return new EI2ResponseBody<>(Map.of("pong", "pong"));
    }

}
