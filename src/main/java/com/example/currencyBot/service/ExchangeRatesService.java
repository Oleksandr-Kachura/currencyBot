package com.example.currencyBot.service;

import com.example.currencyBot.exception.ServerException;

public interface ExchangeRatesService {

    String getCurrencyInfo(String currencyCode) throws ServerException;


}
