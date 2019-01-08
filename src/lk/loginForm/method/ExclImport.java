package lk.loginForm.method;



import javafx.stage.FileChooser;
import jdk.nashorn.tools.Shell;
import org.apache.poi.hssf.usermodel.*;
import org.apache.poi.ss.formula.functions.T;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.xssf.usermodel.XSSFCell;
import org.apache.poi.xssf.usermodel.XSSFRow;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.apache.poi.hssf.usermodel.HSSFCell;
import org.apache.poi.hssf.usermodel.HSSFDateUtil;


import javax.swing.*;
import javax.swing.filechooser.FileNameExtensionFilter;
import javax.swing.filechooser.FileSystemView;
import java.awt.*;
import java.io.*;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.LinkedList;
import java.util.List;
import java.util.Properties;

import static javafx.application.ConditionalFeature.SWT;

/**
 * @Auther: Lk
 * @Date: 2019/1/7 0007 14:35
 * @Description:
 */
public class ExclImport{

    public ExclImport() {
    }

    public static List<List<Object>> importExcel(File file) throws IOException {
        String fileName = file.getName();
        String extension = fileName.lastIndexOf(".")==-1?"":fileName.substring(fileName.lastIndexOf(".")+1);
        if("xls".equals(extension)){
            return read2003Excel(file);
        }else if("xlsx".equals(extension)){
            return read2007Excel(file);
        }else{
            throw new IOException("不支持的文件类型");
        }
    }


    private static List<List<Object>> forexcl2003(HSSFSheet sheet, List<List<Object>> list){
        Object value = null;
        HSSFRow row = null;
        HSSFCell cell = null;

        for(int i = sheet.getFirstRowNum();i<= sheet.getPhysicalNumberOfRows();i++){
            row = sheet.getRow(i);
            if (row == null) {
                continue;
            }
            List<Object> linked = new LinkedList<Object>();
            for (int j = row.getFirstCellNum(); j <= row.getLastCellNum(); j++) {
                cell = row.getCell(j);
                if (cell == null) {
                    continue;
                }
                DecimalFormat df = new DecimalFormat("0");// 格式化 number String 字符
                SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");// 格式化日期字符串
                DecimalFormat nf = new DecimalFormat("0");// 格式化数字
                switch (cell.getCellType()) {
                    case XSSFCell.CELL_TYPE_STRING:
                        //  System.out.println(i+"行"+j+" 列 is String type");
                        value = cell.getStringCellValue();
                        break;
                    case XSSFCell.CELL_TYPE_NUMERIC:
                        //   System.out.println(i+"行"+j+" 列 is Number type ; DateFormt:"+cell.getCellStyle().getDataFormatString());
                        if("@".equals(cell.getCellStyle().getDataFormatString())){
                            value = df.format(cell.getNumericCellValue());
                        } else if("General".equals(cell.getCellStyle().getDataFormatString())){
                            value = nf.format(cell.getNumericCellValue());
                        }else{
                            value = sdf.format(HSSFDateUtil.getJavaDate(cell.getNumericCellValue()));
                        }
                        break;
                    case XSSFCell.CELL_TYPE_BOOLEAN:
                        //   System.out.println(i+"行"+j+" 列 is Boolean type");
                        value = cell.getBooleanCellValue();
                        break;
                    case XSSFCell.CELL_TYPE_BLANK:
                        //   System.out.println(i+"行"+j+" 列 is Blank type");
                        value = "";
                        break;
                    default:
                        //   System.out.println(i+"行"+j+" 列 is default type");
                        value = cell.toString();
                }
                if (value == null || "".equals(value)) {
                    continue;
                }
                linked.add(value);
            }
            list.add(linked);
        }
        return list;
    }
    private static List<List<Object>> forexcl2007(XSSFSheet sheet, List<List<Object>> list){
        Object value = null;
        XSSFRow row = null;
        XSSFCell cell = null;

        for(int i = sheet.getFirstRowNum();i<= sheet.getPhysicalNumberOfRows();i++){
            row = sheet.getRow(i);
            if (row == null) {
                continue;
            }
            List<Object> linked = new LinkedList<Object>();
            for (int j = row.getFirstCellNum(); j <= row.getLastCellNum(); j++) {
                cell = row.getCell(j);
                if (cell == null) {
                    continue;
                }
                DecimalFormat df = new DecimalFormat("0");// 格式化 number String 字符
                SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");// 格式化日期字符串
                DecimalFormat nf = new DecimalFormat("0");// 格式化数字
                switch (cell.getCellType()) {
                    case XSSFCell.CELL_TYPE_STRING:
                        //  System.out.println(i+"行"+j+" 列 is String type");
                        value = cell.getStringCellValue();
                        break;
                    case XSSFCell.CELL_TYPE_NUMERIC:
                        //   System.out.println(i+"行"+j+" 列 is Number type ; DateFormt:"+cell.getCellStyle().getDataFormatString());
                        if("@".equals(cell.getCellStyle().getDataFormatString())){
                            value = df.format(cell.getNumericCellValue());
                        } else if("General".equals(cell.getCellStyle().getDataFormatString())){
                            value = nf.format(cell.getNumericCellValue());
                        }else{
                            value = sdf.format(HSSFDateUtil.getJavaDate(cell.getNumericCellValue()));
                        }
                        break;
                    case XSSFCell.CELL_TYPE_BOOLEAN:
                        //   System.out.println(i+"行"+j+" 列 is Boolean type");
                        value = cell.getBooleanCellValue();
                        break;
                    case XSSFCell.CELL_TYPE_BLANK:
                        //   System.out.println(i+"行"+j+" 列 is Blank type");
                        value = "";
                        break;
                    default:
                        //   System.out.println(i+"行"+j+" 列 is default type");
                        value = cell.toString();
                }
                if (value == null || "".equals(value)) {
                    continue;
                }
                linked.add(value);
            }
            list.add(linked);
        }
        return list;
    }
    /**
     * 读取 office 2003 excel 所有信息
     * @throws IOException
     * @throws
     * */
    private static List<List<Object>> read2003Excel(File file) throws IOException{
        List<List<Object>> list = new LinkedList<List<Object>>();
        HSSFWorkbook hwb = new HSSFWorkbook(new FileInputStream(file));
        HSSFSheet sheet = hwb.getSheetAt(0);
        list=forexcl2003(sheet,list);
        return list;
    }


    /**
     * 读取Office 2007 excel 所有信息
     * */
    private static List<List<Object>> read2007Excel(File file) throws IOException {
        List<List<Object>> list = new LinkedList<List<Object>>();
        // 构造 XSSFWorkbook 对象，strPath 传入文件路径
        XSSFWorkbook xwb = new XSSFWorkbook(new FileInputStream(file));
        // 读取第一章表格内容
        XSSFSheet sheet = xwb.getSheetAt(0);
        String s=sheet.getClass().toString();
        //LKTODO:只读一列的方法
        //list=forexcl2007(sheet,list);//读取所有数据
        Object value = null;
        XSSFRow row = null;
        XSSFCell cell = null;
        int index = 0;//Tagname所在位置

        //计算出TagName的位置
        row = sheet.getRow(sheet.getFirstRowNum());
        for (int j = row.getFirstCellNum(); j <= row.getLastCellNum(); j++) {
            Properties prop = ReadProperties.getProp();
            if (prop.getProperty("TagName").equals(row.getCell(j).toString())) {
                index = j;
                break;
            }
        }

        DecimalFormat df = new DecimalFormat("0");// 格式化 number String 字符
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");// 格式化日期字符串
        DecimalFormat nf = new DecimalFormat("0");// 格式化数字

        List<Object> linked = new LinkedList<Object>();

        for (int i = sheet.getFirstRowNum(); i <= sheet.getPhysicalNumberOfRows(); i++) {
            row = sheet.getRow(i);
            if (row == null) {
                continue;
            }
            cell = row.getCell(index);
            switch (cell.getCellType()) {
                case XSSFCell.CELL_TYPE_STRING:
                    //   System.out.println(i+"行"+j+" 列 is String type");
                    value = cell.getStringCellValue();
                    break;
                case XSSFCell.CELL_TYPE_NUMERIC:
                    //   System.out.println(i+"行"+j+" 列 is Number type ; DateFormt:"+cell.getCellStyle().getDataFormatString());
                    if("@".equals(cell.getCellStyle().getDataFormatString())){
                        value = df.format(cell.getNumericCellValue());
                    } else if("General".equals(cell.getCellStyle().getDataFormatString())){
                        value = nf.format(cell.getNumericCellValue());
                    }else{
                        value = sdf.format(HSSFDateUtil.getJavaDate(cell.getNumericCellValue()));
                    }
                    break;
                case XSSFCell.CELL_TYPE_BOOLEAN:
                    //   System.out.println(i+"行"+j+" 列 is Boolean type");
                    value = cell.getBooleanCellValue();
                    break;
                case XSSFCell.CELL_TYPE_BLANK:
                    //   System.out.println(i+"行"+j+" 列 is Blank type");
                    value = "";
                    break;
                default:
                    //   System.out.println(i+"行"+j+" 列 is default type");
                    value = cell.toString();
            }
            linked.add(value);
        }

        list.add(linked);
        return list;
    }

    //遍历所有的demo
    public static void StartFunction(){
        File file = new File("C:\\Users\\Administrator\\Desktop\\test.xlsx");
        List<List<Object>> dataList= null;
        try {
            dataList = importExcel(file);
        } catch (IOException e) {
            e.printStackTrace();
        }
        for (int i = 1; i < dataList.size(); i++) {
            for (int j = 1; j < dataList.get(i).size(); j++) {
                System.out.println(dataList.get(i).get(j));
            }
            System.out.println("------------------");
        }

    }
    private static Frame jf;
    public static void exclimport() {
        //FileDialog选择器
//        {
//            FileDialog fdopen = new FileDialog(jf, "Excl Choose", FileDialog.LOAD);
//            fdopen.setFilenameFilter(new FilenameFilter() {
//                @Override
//                public boolean accept(File dir, String name) {
//                    if(name.endsWith(".xlsx")){
//                        return true;
//                    }
//                    return false;
//                }
//            });
//
//            fdopen.setFile("Koma<新建文本文档>.txt");
//            fdopen.setVisible(true);
//            String str = null;
//            //如果没选择文件
//            if(fdopen==null){
//                return;
//            }
//            str=fdopen.getDirectory() + fdopen.getFile();
//        }

        JFileChooser fileChooser = new JFileChooser();

        fileChooser.setCurrentDirectory(new File(".")); // 设置默认显示的文件夹为当前文件夹
        fileChooser.setFileSelectionMode(JFileChooser.FILES_ONLY);//设置文件选择的模式
        fileChooser.setMultiSelectionEnabled(false); // 设置是否允许多选
        fileChooser.setFileFilter(new FileNameExtensionFilter("Excl(*.xlsx,*.xls)", "xlsx", "xls"));// 设置默认使用的文件过滤器
        fileChooser.setDialogTitle("Excl Choose");
        fileChooser.setSize(800,600);
        // 打开文件选择框（线程将被阻塞, 直到选择框被关闭）
        int result = fileChooser.showOpenDialog(jf);
        File file=null;
        if (result == JFileChooser.APPROVE_OPTION) {
            // 如果点击了"确定", 则获取选择的文件路径
            file= fileChooser.getSelectedFile();
        }else {
            return;
        }
        //File file = new File(str);
        //File file = new File("C:\\Users\\Administrator\\Desktop\\test.xlsx");
        List<List<Object>> dataList= null;
        try {
            dataList = importExcel(file);
        } catch (IOException e) {
            e.printStackTrace();
        }
        for (int i = 0; i < dataList.size(); i++) {
            for (int j = 0; j < dataList.get(i).size(); j++) {
                System.out.println(dataList.get(i).get(j));
            }
            System.out.println("------------------");
        }
    }
}
