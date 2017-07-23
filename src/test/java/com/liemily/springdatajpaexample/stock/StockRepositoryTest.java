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
import java.util.UUID;

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
}
