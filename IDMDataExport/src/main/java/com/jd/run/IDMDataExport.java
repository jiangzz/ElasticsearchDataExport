package com.jd.run;

import com.jd.service.IIDMDataExportService;
import com.jd.write.impl.SystemDataWriteCallBack;
import com.jd.write.impl.XSSWorkBookWriteCallBack;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

/**
 * 导出Elasticsearch数据
 */
@Component
public class IDMDataExport implements CommandLineRunner {
    @Autowired
    private IIDMDataExportService exportService;
    @Override
    public void run(String... args) throws Exception {
        exportService.queryIDMData("smart_jtlas_v1",null,new XSSWorkBookWriteCallBack());
        System.exit(0);
    }
}
