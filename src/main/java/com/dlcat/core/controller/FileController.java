package com.dlcat.core.controller;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import org.apache.poi.hssf.usermodel.HSSFCell;
import org.apache.poi.hssf.usermodel.HSSFCellStyle;
import org.apache.poi.hssf.usermodel.HSSFRow;
import org.apache.poi.hssf.usermodel.HSSFSheet;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;

import com.dlcat.common.BaseController;
import com.jfinal.core.Controller;

/** 
* @author zhaozhongyuan
* @date 2017年5月2日 下午2:45:56 
* @Description: 对文件的操作
*/
public class FileController extends BaseController {
	
	
	/**
	* @author:zhaozhongyuan 
	* @Description:下载生成的excel
	* @return void   
	* @date 2017年5月2日 下午6:21:06  
	*/
	public void index() {
		File file=new File(getPara("path"));
		renderFile(file);
	}
	/**
	* @author:zhaozhongyuan 
	* @Description:首页生成excel
	* @return void   
	 * @throws IOException 
	* @date 2017年5月2日 下午2:46:11  
	*/
	public void exportData() throws IOException {
        Integer size=getParaToInt("size");
        // 第一步，创建一个webbook，对应一个Excel文件
		HSSFWorkbook wb = new HSSFWorkbook();
		// 第二步，在webbook中添加一个sheet,对应Excel文件中的sheet
		HSSFSheet sheet = wb.createSheet("机构年度数据");
		// 第三步，在sheet中添加表头第0行,注意老版本poi对Excel的行数列数有限制short
		HSSFRow row = sheet.createRow(0);
		// 第四步，创建单元格，并设置值表头 设置表头居中
		HSSFCellStyle style = wb.createCellStyle();
		//style.setAlignment(HorizontalAlignment.CENTER);

		HSSFCell cell = row.createCell(0);
		cell.setCellValue("门店名字");
		cell.setCellStyle(style);
		cell = row.createCell(1);
		cell.setCellValue("金额");
		cell.setCellStyle(style);		

        for (int i = 0; i < size; i++) {
        	row = sheet.createRow(i + 1);
        	row.createCell(0).setCellValue(getPara("data["+i+"][name]"));
        	row.createCell(1).setCellValue(getPara("data["+i+"][data][]"));
		}
          // 自适应列宽
     		for (int i = 0; i < 2; i++) {
     			sheet.autoSizeColumn(i, true);
     		}
     		
     		File file=new File("E:/1.xls");
     	// 第六步，将文件存到指定位置
    		try {
    			FileOutputStream fout = new FileOutputStream(file);
    			wb.write(fout);
    			fout.close();
    		} catch (Exception e) {
    			e.printStackTrace();
    		}
    		Map<String, String> map=new HashMap<String, String>();
    		map.put("success", "true");
    		map.put("path", "E:/1.xls");
    		
		renderJson(map);
    
	}
}
