package com.jd.write.impl;

import com.jd.write.DataWriteCallBack;
import lombok.extern.slf4j.Slf4j;

import java.util.List;
@Slf4j
public class SystemDataWriteCallBack implements DataWriteCallBack {
    @Override
    public void writeData(List dataList) {
      dataList.forEach(data->log.info(data.toString()));
    }
}
