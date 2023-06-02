package io.lassomarketing.ei2.snapchat.controller;

import com.google.cloud.logging.TraceLoggingEnhancer;
import io.lassomarketing.ei2.common.response.EI2ResponseBody;
import io.lassomarketing.ei2.snapchat.dto.*;
import io.lassomarketing.ei2.snapchat.service.SnapchatService;
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
public class SnapchatController {

    private final SnapchatService snapchatService;

    private final Tracer tracer;

    /**
     * @return externalIdDto
     */
    @PostMapping("/upsertAudience")
    public EI2ResponseBody<Map<String, String>> upsertAudience(@Valid @RequestBody UpsertAudienceRequest request) {
        String externalId = snapchatService.upsertAudience(request);
        return new EI2ResponseBody<>(Map.of("external_id", externalId));
    }

    @PostMapping("/preparePagesList")
    public EI2ResponseBody<List<PreparePagesResponse>> preparePagesList(@RequestBody List<DataSourceDto> dataSources) {
        return new EI2ResponseBody<>(snapchatService.preparePages(dataSources, getTraceId()));
    }

    @PostMapping("/uploadPage")
    public EI2ResponseBody<?> uploadPage(@RequestBody UploadPageRequest request) {
        snapchatService.uploadAudiencePage(request.getExternalId(), request.getDataSource(), request.getPageNumber(),
                                           getTraceId());
        return new EI2ResponseBody<>();
    }

    @PostMapping("/getUploadResults")
    public EI2ResponseBody<UploadResultsResponse> getUploadResults(@RequestBody AudienceIdDto audienceIdDto) {
        return new EI2ResponseBody<>(snapchatService.getUploadResults(audienceIdDto, getTraceId()));
    }

    private String getTraceId() {
        String traceId = tracer.currentSpan().context().traceId();
        TraceLoggingEnhancer.setCurrentTraceId(traceId);
        return traceId;
    }

}
