package com.jd.service.impl;

import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.jd.entities.IDMData;
import com.jd.service.IIDMDataExportService;
import com.jd.write.DataWriteCallBack;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.elasticsearch.action.search.SearchRequestBuilder;
import org.elasticsearch.action.search.SearchResponse;
import org.elasticsearch.client.Client;
import org.elasticsearch.index.query.QueryBuilder;
import org.elasticsearch.index.query.QueryBuilders;
import org.elasticsearch.index.query.TermQueryBuilder;
import org.elasticsearch.search.SearchHit;
import org.elasticsearch.search.SearchHits;
import org.elasticsearch.search.builder.SearchSourceBuilder;
import org.elasticsearch.search.sort.SortOrder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;
@Service
@Slf4j
public class IDMDataExportService implements IIDMDataExportService {
    @Autowired
    private Client client;
   private static  String[] includes=new String[]{"_id","cluster" , "database" , "tableName" , "tableAlias", "columns", "owner",
           "tableType" , "inputFormat" , "outputFormat" , "tableFsPath" , "createTime" , "lastUpdateTime" , "tableSourceType" ,
           "aliveDays" , "marketOrProject" , "businessSystem" , "businessCategory" , "businessLine", "personInCharge" , "usageCountPerDay",
           "usageCountPerWeek" , "usageCountPerMonth" , "latestAccessTime" , "lastModifiedTime" , "clickCounts" , "authLevel" , "userDefineTags" ,
           "businessDescription" , "usageDescription" , "tasksMakeup" , "tasksUseTable" };

    @SneakyThrows
    @Override
    public void queryIDMData(String index, String searchAfterId, DataWriteCallBack dataWriteCallBack){
        QueryBuilder termQueryBuilder = QueryBuilders.termQuery("database", "idm");
        SearchRequestBuilder request = client.prepareSearch(index)
                .setTypes("_doc")
                .setQuery(termQueryBuilder)
                .setSize(100)
                .setFrom(0)
                .setFetchSource(includes,null)
                .addSort("_id", SortOrder.DESC);

        if(searchAfterId!=null){
            request.searchAfter(new String[]{searchAfterId});
        }

        ObjectMapper objectMapper = new ObjectMapper();
        objectMapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
        objectMapper.configure(DeserializationFeature.FAIL_ON_IGNORED_PROPERTIES, false);

        SearchResponse response = request.execute().actionGet();
        SearchHits searchHits = response.getHits();
        List<IDMData> dataList = Arrays.stream(searchHits.getHits()).map(hit -> objectMapper.convertValue(hit.getSourceAsMap(), IDMData.class)).collect(Collectors.toList());

        if(dataList.size()>0){
            log.info("写入数据到");
            dataWriteCallBack.writeData(dataList);
            IDMData lastData = dataList.get(dataList.size() - 1);
            String _id=lastData.getDatabase()+"."+lastData.getTableName()+"@"+lastData.getCluster();
            queryIDMData(index,_id,dataWriteCallBack);
        }

    }
}
