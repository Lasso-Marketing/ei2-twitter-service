package io.lassomarketing.ei2.twitter.controller;

import io.lassomarketing.ei2.common.response.EI2ResponseBody;
import io.lassomarketing.ei2.twitter.service.TwitterService;
import io.lassomarketing.ei2.twitter.dto.AudienceIdDto;
import io.lassomarketing.ei2.twitter.dto.DataSourceDto;
import io.lassomarketing.ei2.twitter.dto.PreparePagesResponse;
import io.lassomarketing.ei2.twitter.dto.UploadPageRequest;
import io.lassomarketing.ei2.twitter.dto.UploadResultsResponse;
import io.lassomarketing.ei2.twitter.dto.UpsertAudienceRequest;
import io.micrometer.tracing.Tracer;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import jakarta.validation.Valid;
import java.util.List;
import java.util.Map;

@Slf4j
@AllArgsConstructor
@RestController
public class TwitterController {

    private final TwitterService twitterService;

    private final Tracer tracer;

    /**
     * @return externalIdDto
     */
    @PostMapping("/upsertAudience")
    public EI2ResponseBody<Map<String, String>> upsertAudience(@Valid @RequestBody UpsertAudienceRequest request) {
        String externalId = twitterService.upsertAudience(request);
        return new EI2ResponseBody<>(Map.of("external_id", externalId));
    }

    @PostMapping("/preparePagesList")
    public EI2ResponseBody<List<PreparePagesResponse>> preparePagesList(@RequestBody List<DataSourceDto> dataSources) {
        return new EI2ResponseBody<>(twitterService.preparePages(dataSources, getTraceId()));
    }

    @PostMapping("/uploadPage")
    public EI2ResponseBody<?> uploadPage(@RequestBody UploadPageRequest request) {
        twitterService.uploadAudiencePage(request.getSocialAccountId(), request.getExternalId(),
                                          request.getExpireMinutes(), request.getDataSource(), request.getPageNumber(),
                                          request.getPageSize(), getTraceId());
        return new EI2ResponseBody<>();
    }

    @PostMapping("/getUploadResults")
    public EI2ResponseBody<UploadResultsResponse> getUploadResults(@RequestBody AudienceIdDto audienceIdDto) {
        return new EI2ResponseBody<>(twitterService.getUploadResults(audienceIdDto, getTraceId()));
    }

    private String getTraceId() {
        return tracer.currentSpan().context().traceId();
    }

}
