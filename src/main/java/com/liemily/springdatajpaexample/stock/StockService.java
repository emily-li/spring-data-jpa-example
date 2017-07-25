package com.liemily.springdatajpaexample.stock;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

/**
 * Created by Emily Li on 23/07/2017.
 */
@Service
public class StockService {
    private StockRepository stockRepository;

    @Autowired
    public StockService(StockRepository stockRepository) {
        this.stockRepository = stockRepository;
    }

    @Transactional(readOnly = true)
    public Stock findOne(String stockSymbol) {
        return stockRepository.findOne(stockSymbol);
    }

    @Transactional(readOnly = true)
    public List<Stock> findAll() {
        List<Stock> stocks = new ArrayList<>();
        Iterator<Stock> stockIterator = stockRepository.findAll().iterator();
        stockIterator.forEachRemaining(stocks::add);
        return stocks;
    }

    @Transactional
    public Stock save(Stock stock) {
        return stockRepository.save(stock);
    }

    @Transactional
    public List<Stock> save(Iterable<Stock> stocks) {
        return stockRepository.save(stocks);
    }

    @Transactional
    public void delete(String stockSymbol) {
        stockRepository.delete(stockSymbol);
    }

    @Transactional
    public void delete(Iterable<Stock> stocks) {
        stockRepository.delete(stocks);
    }
}
