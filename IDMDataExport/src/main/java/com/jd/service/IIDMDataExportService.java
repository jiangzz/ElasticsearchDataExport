package com.jd.service;

import com.jd.write.DataWriteCallBack;
import org.elasticsearch.client.Client;
import org.springframework.beans.factory.annotation.Autowired;

public interface IIDMDataExportService {
    public void queryIDMData(String index, String searchAfterId, DataWriteCallBack dataWriteCallBack);
}
