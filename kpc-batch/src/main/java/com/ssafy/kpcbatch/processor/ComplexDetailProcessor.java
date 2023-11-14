package com.ssafy.kpcbatch.processor;

import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.ssafy.kpcbatch.dto.complex.ComplexDto;
import com.ssafy.kpcbatch.dto.complex.ComplexListDto;
import com.ssafy.kpcbatch.entity.complex.Complex;
import lombok.extern.slf4j.Slf4j;
import org.springframework.batch.item.ItemProcessor;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;

import java.util.ArrayList;
import java.util.List;

@Slf4j
public class ComplexDetailProcessor implements ItemProcessor<Long, List<Complex>> {
    private final RestTemplate restTemplate;
    private final String apiUrl;
    public ComplexDetailProcessor(String apiUrl, RestTemplate restTemplate) {
        this.apiUrl = apiUrl;
        this.restTemplate = restTemplate;
    }
    @Override
    public List<Complex> process(Long item) throws Exception {
        HttpHeaders headers = new HttpHeaders();
        headers.set("Accept", "application/json");
        headers.set("Authorization", "");
        UriComponentsBuilder uriBuilder = UriComponentsBuilder.fromHttpUrl(apiUrl+"/"+item);

        log.info("Fetching region data from an external API by using the url: {}", uriBuilder.toUriString());

        ResponseEntity<String> response = restTemplate.exchange(uriBuilder.toUriString(), HttpMethod.GET,
                new HttpEntity<>(headers), String.class);

        ObjectMapper objectMapper = new ObjectMapper();
        objectMapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
        ComplexListDto complexListDto = objectMapper.readValue(response.getBody(), ComplexListDto.class);
        List<Complex> list = new ArrayList<>();
        for (ComplexDto complexDto: complexListDto.getComplexList()) {
            Complex complex = Complex.builder()
                    .complexNo(complexDto.getComplexNo())
                    .complexName(complexDto.getComplexName())
                    .cortarNo(complexDto.getCortarNo())
                    .realEstateTypeCode(complexDto.getRealEstateTypeCode())
                    .realEstateTypeName(complexDto.getRealEstateTypeName())
                    .detailAddress(complexDto.getDetailAddress())
                    .latitude(complexDto.getLatitude())
                    .longitude(complexDto.getLongitude())
                    .totalHouseholdCount(complexDto.getTotalHouseholdCount())
                    .totalBuildingCount(complexDto.getTotalBuildingCount())
                    .highFloor(complexDto.getHighFloor())
                    .lowFloor(complexDto.getLowFloor())
                    .useApproveYmd(complexDto.getUseApproveYmd())
                    .dealCount(complexDto.getDealCount())
                    .leaseCount(complexDto.getLeaseCount())
                    .rentCount(complexDto.getRentCount())
                    .shirtTermRentCount(complexDto.getShirtTermRentCount())
                    .cortarAddress(complexDto.getCortarAddress())
                    .build();
            list.add(complex);
        }
        return list;
    }
}
