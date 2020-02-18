package com.royal.recreation.util;

import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.streaming.SXSSFWorkbook;

import java.io.IOException;
import java.io.OutputStream;
import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ExcelUtil {

    /**
     * 导出Excel 2007 OOXML (.xlsx)格式
     *
     * @param data 数据集
     * @param out  输出流
     */
    public static void exportExcelX(List data, OutputStream out) {
        Map<Class, List<Field>> fieldMap = new HashMap<>();

        // 声明一个工作薄
        Workbook workbook = new SXSSFWorkbook(1000);//缓存
        //表头样式
        CellStyle titleStyle = workbook.createCellStyle();
        titleStyle.setAlignment(HorizontalAlignment.CENTER);
        // 单元格样式
        CellStyle cellStyle = workbook.createCellStyle();
        getDefaultCellStyle(cellStyle);
        cellStyle.setVerticalAlignment(VerticalAlignment.CENTER);
        Sheet sheet = workbook.createSheet();
        // 遍历集合数据，产生数据行
        int rowIndex = 0;
        for (Object obj : data) {
            if (rowIndex % 50000 == 0) {
                if (rowIndex != 0) {
                    sheet = workbook.createSheet();//如果数据超过了，则在第二页显示
                }
                rowIndex = 0;
            }
            boolean first = fieldMap.get(obj.getClass()) == null;
            List<Field> fieldsList = fieldMap.computeIfAbsent(obj.getClass(), clazz -> {
                List<Field> fields = new ArrayList<>();
                while (clazz != null) {
                    Field[] declaredFields = clazz.getDeclaredFields();
                    for (Field field : declaredFields) {
                        field.setAccessible(true);
                        fields.add(field);
                    }
                    clazz = clazz.getSuperclass();
                }
                return fields;
            });
            if (first) {
                Row dataRow = sheet.createRow(rowIndex);
                for (int i = 0; i < fieldsList.size(); i++) {
                    Cell newCell = dataRow.createCell(i);
                    String cellValue;
                    Object o;
                    o = fieldsList.get(i).getName();
                    if (o == null) cellValue = "";
                    else cellValue = o.toString();
                    newCell.setCellValue(cellValue);
                    newCell.setCellStyle(cellStyle);
                    if (i == fieldsList.size() - 1) {
                        newCell = dataRow.createCell(i+1);
                        newCell.setCellValue(obj.getClass().toString());
                        newCell.setCellStyle(cellStyle);
                    }
                }
                rowIndex++;
            }
            Row dataRow = sheet.createRow(rowIndex);
            for (int i = 0; i < fieldsList.size(); i++) {
                Cell newCell = dataRow.createCell(i);
                String cellValue;
                Object o;
                try {
                    o = fieldsList.get(i).get(obj);
                } catch (IllegalAccessException e) {
                    o = null;
                }
                if (o == null) cellValue = "";
                else cellValue = o.toString();
                newCell.setCellValue(cellValue);
                newCell.setCellStyle(cellStyle);
            }
            rowIndex++;
        }
        try {
            workbook.write(out);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private static void getDefaultCellStyle(CellStyle headerStyle) {
        headerStyle.setBorderBottom(BorderStyle.THIN);
        headerStyle.setBorderLeft(BorderStyle.THIN);
        headerStyle.setBorderRight(BorderStyle.THIN);
        headerStyle.setBorderTop(BorderStyle.THIN);
        headerStyle.setAlignment(HorizontalAlignment.CENTER);
    }

}
