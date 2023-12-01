package com.example.Controller;


import org.apache.poi.ss.usermodel.DateUtil;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFCell;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.web.multipart.MultipartFile;

//import org.springframework.web.multipart.commons.CommonsMultipartFile;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.apache.poi.ss.usermodel.CellType.*;

public class ExcelDataProcessing {
    //总行数
    private static int totalRows = 0;
    //总条数
    private static int totalCells = 0;
    //错误信息接收器
    private static String errorMsg;

    //获取总行数
    public int getTotalRows()  { return totalRows;}
    //获取总列数
    public int getTotalCells() {  return totalCells;}
    //获取错误信息
    public String getErrorInfo() { return errorMsg; }

    /**
     * 验证EXCEL文件
     * @param filePath
     * @return
     */
    public static boolean validateExcel(String filePath){
        if (filePath == null || !(WDWUtil.isExcel2003(filePath) || WDWUtil.isExcel2007(filePath))){
            errorMsg = "文件名不是excel格式";
            return false;
        }
        return true;
    }

    /**
     * 根据excel里面的内容读取客户信息
     * @param is 输入流
     * @param isExcel2003 excel是2003还是2007版本
     * @return
     */
    public static List<Map<String,Object>> getExcelInfo(InputStream is, boolean isExcel2003, int sheetNo){
        List<Map<String,Object>> pds=null;
        try{
            /** 根据版本选择创建Workbook的方式 */
            Workbook wb = null;
            //当excel是2003时
            if(isExcel2003){
                wb = new HSSFWorkbook(is);
            }
            else{//当excel是2007时
                wb = new XSSFWorkbook(is);
            }
            //读取Excel里面的信息
            pds=readExcelValue(wb,sheetNo);
        }
        catch (IOException e)  {
            e.printStackTrace();
        }
        return pds;
    }

    /**
     * @description: 獲取excel文件中的數據
     * @author:  libie
     * @dateTime: 2021/2/3 下午 05:05
     *
     * @param file
     * @param sheetNo
     * @return
     */
    public static List<Map<String,Object>> getExcelInfo(MultipartFile file, int sheetNo){
        //把spring文件上传的MultipartFile转换成CommonsMultipartFile类型
        //CommonsMultipartFile cf= (CommonsMultipartFile)file; //获取本地存储路径
        //初始化客户信息的集合
        List<Map<String,Object>> pds=new ArrayList<>();
        //初始化输入流
        InputStream is = null;
        try{
            //根据文件名判断文件是2003版本还是2007版本
            boolean isExcel2003 = WDWUtil.isExcel2003(file.getName());
            //根据新建的文件实例化输入流
            is = file.getInputStream();
            //根据excel里面的内容读取信息
            pds = getExcelInfo(is, isExcel2003,sheetNo);
            is.close();
        }catch(Exception e){
            e.printStackTrace();
        } finally{
            if(is !=null)
            {
                try{
                    is.close();
                }catch(IOException e){
                    e.printStackTrace();
                }
            }
        }
        return pds;
    }


    /**
     * 读取Excel里面的信息
     * @param wb
     * @return
     */
    private static List<Map<String,Object>> readExcelValue(Workbook wb, int sheetNo){
        //得到第几个shell
        Sheet sheet=wb.getSheetAt(sheetNo);

        //得到Excel的行数
        totalRows=sheet.getPhysicalNumberOfRows();

        //得到Excel的列数(前提是有行数)
        if(totalRows>=1 && sheet.getRow(0) != null){
            totalCells=sheet.getRow(0).getPhysicalNumberOfCells();
        }
        //第一行的数据
        ArrayList<String> keys = new ArrayList<>();
        //获取第一行的数据
        Row row1 = sheet.getRow(0);
        for (int i = 0; i < totalCells; i++) {
            Cell cell = row1.getCell(i);
            String value = String.valueOf(getValueFromCell(cell));
            keys.add(value);
        }
        List<Map<String,Object>> pds=new ArrayList<>();
        Map<String,Object> pd;
        //循环Excel行数,从第二行开始。标题不入库
        for(int r=1;r<totalRows;r++){
            Row row = sheet.getRow(r);
            if (row == null) continue;
            pd = new HashMap();

            //循环Excel的列
            for(int c = 0; c <totalCells; c++){

                Cell cell = row.getCell(c);
                //将列的信息添加到Map
                pd.put(keys.get(c),getValueFromCell(cell));
            }
            //添加客户
            pds.add(pd);
        }
        return pds;
    }

    /**
     * @description: 讀取單元格值，返回Object（根據數值自動轉換為String、Integer、Double、Long、null）
     * @author:  libie
     * @dateTime: 2021/2/3 下午 07:14
     *
     * @param cell
     * @return
     */
    private static Object getValueFromCell(Cell cell) {
//        SimpleDateFormat sFormat = new SimpleDateFormat("yyyy/MM/dd");
//        DecimalFormat decimalFormat = new DecimalFormat("#.#");
        Object cellValue = null;
        if(cell == null) {
            return null;
        }
        else if(cell.getCellType() == STRING) {
            cellValue = cell.getStringCellValue();
        }

        else if(cell.getCellType() == NUMERIC) {
            if(DateUtil.isCellDateFormatted(cell)) {
                double d = cell.getNumericCellValue();
                cellValue = DateUtil.getJavaDate(d);
            }else {
                double d = cell.getNumericCellValue();
                double _1 = Math.floor(d);
                if (_1<d) { //有小數部分
                    cellValue = d;
                }else { //無小數部分
                    long _2 = Math.round(_1);
                    if (_2>Integer.MAX_VALUE || _2<Integer.MIN_VALUE) {
                        cellValue = _2;
                    }else {
                        cellValue = (int)_2;
                    }
                }
            }
        }

        else if(cell.getCellType() == BLANK) {
            cellValue = "";
        }

        else if(cell.getCellType() == BOOLEAN) {
            cellValue = cell.getBooleanCellValue();
        }

        else if(cell.getCellType() == ERROR) {
            cellValue = "";
        }

        else if(cell.getCellType() == FORMULA) {
            cellValue = cell.getCellFormula();
        }

        return cellValue;
    }

}


class WDWUtil {

    // @描述：是否是2003的excel，返回true是2003
    public static boolean isExcel2003(String filePath)  {
        return filePath.matches("^.+\\.(?i)(xls)$");
    }

    //@描述：是否是2007的excel，返回true是2007
    public static boolean isExcel2007(String filePath)  {
        return filePath.matches("^.+\\.(?i)(xlsx)$");
    }
}
