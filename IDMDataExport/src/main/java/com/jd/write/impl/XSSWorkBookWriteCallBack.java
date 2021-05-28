package com.jd.write.impl;

import com.alibaba.druid.support.json.JSONUtils;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.jd.entities.IDMData;
import com.jd.write.DataWriteCallBack;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.apache.poi.ss.usermodel.BorderStyle;
import org.apache.poi.ss.usermodel.FillPatternType;
import org.apache.poi.ss.usermodel.HorizontalAlignment;
import org.apache.poi.xssf.usermodel.*;

import java.awt.*;
import java.io.FileOutputStream;
import java.lang.reflect.Field;
import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.Date;
import java.util.List;

@Slf4j
public class XSSWorkBookWriteCallBack implements DataWriteCallBack<IDMData> {
    private XSSFWorkbook wb = new XSSFWorkbook();
    List<String> columns= Arrays.asList("cluster" , "database" , "tableName" , "tableAlias", "columns", "owner",
            "tableType" , "inputFormat" , "outputFormat" , "tableFsPath" , "createTime" , "lastUpdateTime" , "tableSourceType" ,
            "aliveDays" , "marketOrProject" , "businessSystem" , "businessCategory" , "businessLine", "personInCharge" , "usageCountPerDay",
            "usageCountPerWeek" , "usageCountPerMonth" , "latestAccessTime" , "lastModifiedTime" , "clickCounts" , "authLevel" , "userDefineTags" ,
            "businessDescription" , "usageDescription" , "tasksMakeup" , "tasksUseTable" );

    String[] columnAlias=new String[]{"集群信息" , "模型层次" , "表英文名称" , "表中文名称", "字段信息", "系统租户",
            "表类型" , "输入格式" , "输出格式" , "存储路径" , "创建时间" , "最近更新时间" , "表的来源" ,
            "生命周期(天)" , "集市/工程" , "业务体系" , "业务大类" , "业务线", "负责人信息" , "每天访问频次",
            "每周访问频次" , "每月访问" , "最近数据访问时间" , "最近数据修改时间" , "点击数" , "认证等级" , "用户自定义标签" ,
            "业务描述信息" , "使用描述信息" , "下游任务" , "上游任务" };
    private  Integer rowIndex=0;
    private    XSSFSheet sheet = wb.createSheet();
    private XSSFCellStyle valueCellStyle;
    public XSSWorkBookWriteCallBack(){
        for (int i = 0; i < columnAlias.length; i++) {
            sheet.setColumnWidth(i,3500);
        }
        XSSFRow headerRow = sheet.createRow( rowIndex++);
        XSSFCellStyle headerCellStyle = wb.createCellStyle();
        headerCellStyle.setAlignment(HorizontalAlignment.CENTER);
        headerCellStyle.setBorderBottom(BorderStyle.THIN);
        headerCellStyle.setBorderLeft(BorderStyle.THIN);
        headerCellStyle.setBorderTop(BorderStyle.THIN);
        headerCellStyle.setBorderRight(BorderStyle.THIN);
        headerCellStyle.setFillPattern(FillPatternType.SOLID_FOREGROUND);
        headerCellStyle.setFillForegroundColor(new XSSFColor(new Color(181,181,181)));

        valueCellStyle = wb.createCellStyle();
        valueCellStyle.setAlignment(HorizontalAlignment.CENTER);
        valueCellStyle.setBorderBottom(BorderStyle.THIN);
        valueCellStyle.setBorderLeft(BorderStyle.THIN);
        valueCellStyle.setBorderTop(BorderStyle.THIN);
        valueCellStyle.setBorderRight(BorderStyle.THIN);


        for (int i = 0; i < columnAlias.length; i++) {
            XSSFCell cell = headerRow.createCell(i);
            cell.setCellStyle(headerCellStyle);
            cell.setCellValue(columnAlias[i]);
        }
        wb.setSheetName(0,"IDM数据");

    }
    @Override
    @SneakyThrows
    public void writeData(List<IDMData> dataList) {

        for (IDMData idmData : dataList) {
            XSSFRow dataRow = sheet.createRow( rowIndex++);
            Field[] declaredFields = idmData.getClass().getDeclaredFields();
            for (Field declaredField : declaredFields) {
                declaredField.setAccessible(true);
                String name = declaredField.getName();
                int i = columns.indexOf(name);
                XSSFCell cell = dataRow.createCell(i);
                Object filedValue = declaredField.get(idmData);
                if(filedValue instanceof List ){
                    String valueStr = new ObjectMapper().writeValueAsString(filedValue);
                    if(valueStr.length()> 32767){
                        valueStr=valueStr.substring(0,32767);
                    }
                    cell.setCellValue(valueStr);
                }else if(filedValue instanceof Date){
                    cell.setCellValue(filedValue!=null?new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(filedValue):"N/A");
                }else{
                    if(filedValue!=null){
                        String valueStr = filedValue.toString();
                        if(valueStr.length()> 32767){
                            valueStr=valueStr.substring(0,32767);
                        }
                        cell.setCellValue(valueStr);
                    }else{
                        cell.setCellValue("N/A");
                    }
                }
                cell.setCellStyle(valueCellStyle);
            }
        }
        wb.write(new FileOutputStream("IDM测试数据.xlsx"));
    }
}
