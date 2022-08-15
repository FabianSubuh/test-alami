package com.test.alami.eod.writer;

import com.test.alami.eod.dto.TransactionDto;
import org.springframework.batch.item.ItemWriter;

import java.util.List;

public class CustomWriter implements ItemWriter<TransactionDto> {
    @Override
    public void write(List<? extends TransactionDto> list) throws Exception {

    }
}
