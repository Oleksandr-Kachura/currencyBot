package com.example.currencyBot.service.impl;

import com.example.currencyBot.client.NbuClient;
import com.example.currencyBot.exception.ServerException;
import com.example.currencyBot.service.ExchangeRatesService;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.lang.Nullable;
import org.springframework.stereotype.Service;


@Service
public class ExchangeRatesServiceImpl implements ExchangeRatesService {

    private final NbuClient nbuClient;
    private final ObjectMapper objectMapper;

    @Autowired
    public ExchangeRatesServiceImpl(NbuClient nbuClient, ObjectMapper objectMapper) {
        this.nbuClient = nbuClient;
        this.objectMapper = objectMapper;
    }

    @Override
    public String getCurrencyInfo(String currencyCode) throws ServerException {
        String json = nbuClient.getExchangeRatesJson();
        JsonNode rootNode;
        try {
            rootNode = objectMapper.readTree(json);
        } catch (JsonProcessingException e) {
            throw new ServerException("Error during read json");
        }
        JsonNode currencyNode = findCurrencyNode(rootNode, currencyCode);
        return mapJsonNodeToCurrency(currencyNode);
    }

    @Nullable
    private JsonNode findCurrencyNode(JsonNode rootNode, String currencyCode) {
        for (JsonNode node : rootNode) {
            if (currencyCode.equals(node.path("cc").asText())) {
                return node;
            }
        }
        return null;
    }

    private String mapJsonNodeToCurrency(JsonNode node) {
        if (node == null) {
            return StringUtils.EMPTY;
        }
        return node.path("rate").asText();
    }

}
