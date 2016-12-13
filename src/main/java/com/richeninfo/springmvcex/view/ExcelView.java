/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.richeninfo.springmvcex.view;

import com.richeninfo.common.util.DateUtils;
import org.apache.commons.lang.StringUtils;
import java.beans.PropertyDescriptor;
import java.lang.reflect.Method;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import org.apache.poi.hssf.usermodel.HSSFSheet;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.springframework.web.servlet.View;
import org.apache.commons.beanutils.PropertyUtils;
import org.apache.poi.hssf.usermodel.HSSFCell;
import org.apache.poi.hssf.usermodel.HSSFRow;

/**
 *
 * @author xiaolie
 */
public class ExcelView implements View {

    @Override
    public String getContentType() {
        return "application/x-excel";
    }

    @Override
    public void render(Map map, HttpServletRequest hsr, HttpServletResponse hsr1) throws Exception {
        Map<String, Integer> propertyIndexMap = new HashMap();
        String fileName = (String) map.get("fileName");
        if (StringUtils.isBlank(fileName)) {
            fileName = System.currentTimeMillis()+".xls";
        }
        hsr1.addHeader("Content-Disposition", "attachment;filename=" + fileName);
        HSSFWorkbook workbook = new HSSFWorkbook();
        List list = (List) map.get("list");
        Boolean excelExport = (Boolean) map.get("excelExport");
        if (excelExport == null) {
			excelExport = false;
		}
        HSSFSheet sheet = workbook.createSheet("内容");
        if (list.size() > 0) {
            PropertyDescriptor[] propertyDescs = PropertyUtils.getPropertyDescriptors(list.get(0).getClass());
            for (int i = 0; i < list.size(); i++) {
                HSSFRow row = sheet.createRow(0);
                //int count = 0;
                for (int j = 0; j < propertyDescs.length; j++) {
                    if ("class".equals(propertyDescs[j].getName())) continue;
                    Method method = propertyDescs[j].getReadMethod();
                    //if (method.isAnnotationPresent(AttributeTitleAnno.class)) {
                    if (method != null) {
                        //HSSFCell cell = row.createCell(count++);
                        AttributeTitleAnno attrAnno = method.getAnnotation(AttributeTitleAnno.class);
                        if (attrAnno != null) {
                            String title = attrAnno.value();
                            Integer index = Integer.valueOf(attrAnno.index());
                            propertyIndexMap.put(method.getName(), index);
                            HSSFCell cell = row.createCell(index);
                            cell.setCellValue(title);
                        } else {
                            //cell.setCellValue(propertyDescs[j].getDisplayName());
                        }
                    }
                    //} else {
                    //    cell.setCellValue(propertyDescs[j].getDisplayName());
                    //}
                }
            }
            for (int i = 0; i < list.size(); i++) {
                Object bean = list.get(i);
                propertyDescs = PropertyUtils.getPropertyDescriptors(bean.getClass());
                HSSFRow row = sheet.createRow(i + 1);
                //int count = 0;
                for (int j = 0; j < propertyDescs.length; j++) {
                    if ("class".equals(propertyDescs[j].getName())) continue;
                    Method method = propertyDescs[j].getReadMethod();
                    if (method != null) {
                        Integer index = propertyIndexMap.get(method.getName());
                        if(index == null) continue;
                        HSSFCell cell = row.createCell(index);
                        String propertyName = propertyDescs[j].getName();
                        Class pClass = propertyDescs[j].getPropertyType();
                        Object value = PropertyUtils.getProperty(bean, propertyName);
                        if (value != null) {
                            if (Date.class.isAssignableFrom(pClass)) {
                                Date d=(Date)value;
                                String dateString = "";
                                if (d != null) {
                                    dateString = DateUtils.formatTime(d);
                                }
                                cell.setCellValue(dateString);
                            } else if (Calendar.class.isAssignableFrom(pClass)) {
                                Calendar dc=(Calendar)value;
                                Date d=dc.getTime();
                                String dateString = "";
                                if (d != null) {
                                    dateString = DateUtils.formatTime(d);
                                }
                                cell.setCellValue(dateString);
                            } else if (Integer.class.isAssignableFrom(pClass) || "int".equals(pClass.getName())) {
                                cell.setCellValue((Integer)value);
                            } else if (Long.class.isAssignableFrom(pClass) || "long".equals(pClass.getName())) {
                                cell.setCellValue((Long)value);
                            } else if (Float.class.isAssignableFrom(pClass) || "float".equals(pClass.getName())) {
                                cell.setCellValue((Float)value);
                            } else if (Double.class.isAssignableFrom(pClass) || "double".equals(pClass.getName())) {
                                cell.setCellValue((Double)value);
                            } else if (Boolean.class.isAssignableFrom(pClass) || "boolean".equals(pClass.getName())) {
                                cell.setCellValue((Boolean)value);
                            } else {
                                cell.setCellValue(PropertyUtils.getProperty(bean, propertyName).toString());
                            }
                        }
                    }
                }
            }
        }
        workbook.write(hsr1.getOutputStream());
        hsr1.flushBuffer();
    }
    
}
