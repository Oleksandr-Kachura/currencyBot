package com.example.currencyBot.client;

import com.example.currencyBot.exception.ServerException;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestTemplate;

@Component
public class NbuClient {

    @Value("${nbu.api.url}")
    private String nbuApiUrl;

    private final RestTemplate restTemplate;

    public NbuClient(RestTemplate restTemplate) {
        this.restTemplate = restTemplate;
    }

    public String getExchangeRatesJson() throws ServerException {
        try {
            return restTemplate.getForObject(nbuApiUrl, String.class);
        } catch (RestClientException e) {
            throw new ServerException("Error get data from nbu", e);
        }
    }

}
