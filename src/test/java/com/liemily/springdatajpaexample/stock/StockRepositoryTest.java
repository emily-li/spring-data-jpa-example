package com.liemily.springdatajpaexample.stock;

import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;
import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.test.context.junit4.SpringRunner;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import java.util.stream.IntStream;

/**
 * Created by Emily Li on 23/07/2017.
 */
@RunWith(SpringRunner.class)
@SpringBootTest
public class StockRepositoryTest {
    private static final Logger logger = LogManager.getLogger(StockRepositoryTest.class);

    @Autowired
    private StockRepository stockRepository;

    private String stockSymbol;

    private final int AVG_RUN_COUNT = 1000;
    private final int BATCH_COUNT = 100;

    @Before
    public void setup() {
        stockSymbol = "SYM" + UUID.randomUUID();
    }

    @After
    public void tearDown() {
        try {
            stockRepository.delete(stockSymbol);
        } catch (EmptyResultDataAccessException e) {
            logger.info("Attempted to delete stock " + stockSymbol + " but it was already deleted");
        }
    }

    @Test
    public void testWriteStock() {
        Stock stock = new Stock(stockSymbol, new BigDecimal(1.5), 1);
        stockRepository.save(stock);
        Stock foundStock = stockRepository.findOne(stockSymbol);
        Assert.assertEquals(stock, foundStock);
    }

    @Test
    public void testDeleteStock() {
        Stock stock = new Stock(stockSymbol, new BigDecimal(1.5), 1);
        stockRepository.save(stock);
        Stock foundStock = stockRepository.findOne(stockSymbol);
        Assert.assertEquals(stock, foundStock);

        stockRepository.delete(stockSymbol);
        Stock deletedStock = stockRepository.findOne(stockSymbol);
        Assert.assertNull(deletedStock);
    }

    @Test
    public void testWriteSingleStockAvgTime() {
        List<Stock> stocks = generateStocks(AVG_RUN_COUNT);
        try {
            long totalTimeMs = timeWriteStocks(stocks, 1);
            logger.info("Average run time for writing a single stock was " + (totalTimeMs / AVG_RUN_COUNT) + "ms");
            logger.info("Total time taken to write " + AVG_RUN_COUNT + " stocks was " + totalTimeMs);
        } finally {
            stockRepository.delete(stocks);
        }
    }

    @Test
    public void testWriteMultipleStocksAvgTime() {
        List<Stock> stocks = generateStocks(AVG_RUN_COUNT);
        try {
            long totalTimeMs = timeWriteStocks(stocks, BATCH_COUNT);
            logger.info("Average run time for writing a stock batch of " + BATCH_COUNT + " was " + (totalTimeMs / BATCH_COUNT) + "ms");
            logger.info("Total time taken to write " + AVG_RUN_COUNT + " stocks was " + totalTimeMs);
        } finally {
            stockRepository.delete(stocks);
        }
    }

    List<Stock> generateStocks(int numStocks) {
        List<Stock> stocks = new ArrayList<>();
        String id = UUID.randomUUID().toString();
        IntStream.range(0, numStocks).forEach(i -> stocks.add(new Stock(id + i, new BigDecimal(1.5), 1)));
        return stocks;
    }

    long timeWriteStocks(List<Stock> stocks, int batchCount) {
        long totalTimeMs = 0;
        long startTime = System.currentTimeMillis();
        for (int i = 0; i < stocks.size(); i += batchCount) {
            stockRepository.save(stocks.subList(i, i + batchCount));
            long endTime = System.currentTimeMillis();
            totalTimeMs += endTime - startTime;
            startTime = endTime;
        }
        return totalTimeMs;
    }
}
