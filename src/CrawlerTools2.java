package com.paic.hc.d2c.app.icd;

import org.apache.http.HttpEntity;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.util.EntityUtils;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.junit.Test;

import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by wuyuyang128 on 16/6/2.
 */
public class CrawlerTools2 {

    static List<Integer> errIds = new ArrayList<Integer>();

    public static String getHtml(int id) {
        CloseableHttpClient httpClient = HttpClients.createSystem();
//        HttpClientBuilder httpClientBuilder = HttpClientBuilder.create();
//        HttpHost proxy = new HttpHost("10.17.171.11", 8080);
//        httpClientBuilder.setProxy(proxy);
//        CloseableHttpClient httpClient = httpClientBuilder.build();
        CloseableHttpResponse response;
        String html = null;
        try {
            System.out.println("开始读取id为" + id + "数据");
            response = httpClient.execute(new HttpGet("http://www.bm8.com.cn/ICD/Show.asp?ID=" + id));
            HttpEntity entity = response.getEntity();
            if (entity != null) {
                html = EntityUtils.toString(entity, "gbk");
                //System.out.println("内容" + html);
            }
        } catch (IOException e) {
            e.printStackTrace();
            System.out.println("id为" + id + "数据失败");
            errIds.add(id);
        } finally {
            try {
                httpClient.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return html;
    }

    public static ICDEntity geICD(String html, int id) {
        ICDEntity icdEntity = new ICDEntity();
        if (html != null) {
            Document doc = Jsoup.parse(html);
            Elements tds = doc.getElementsByTag("td");
            for (Element td : tds) {
                Elements fonts = td.getElementsByTag("font");
                for (Element font : fonts) {
                    if (font.attr("face").equals("楷体_GB2312")) {
                        icdEntity.setName(font.html().trim());
                        System.out.println("内容" + font.html());
                    }
                    if (font.attr("color").equals("red")) {
                        icdEntity.setCode(font.html().trim());
                    }
                    if (font.attr("color").equals("#FF0000")) {
                        icdEntity.setHelpCode(font.html().trim());
                    }
                }
            }
        } else {
            return null;
        }
        return icdEntity;
    }

    public static void saveICD(int start, int end) {
        List<ICDEntity> icdEntityList = new ArrayList<ICDEntity>();
        for (int i = start; i < end; i++) {
            ICDEntity data = geICD(getHtml(i), i);
            if (data != null) {
                icdEntityList.add(data);
            }
            try {
                Thread.sleep(100);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
        for (Integer id : errIds) {
            ICDEntity data = geICD(getHtml(id), id);
            if (data != null) {
                icdEntityList.add(data);
            }
        }
        Workbook workbook = null;
        workbook = new XSSFWorkbook();
        Sheet sheet = workbook.createSheet("data");
        for (int i = 0; i < icdEntityList.size(); i++) {
            Row row = sheet.createRow(i);
            Cell cell = row.createCell(0, Cell.CELL_TYPE_STRING);
            cell.setCellValue(icdEntityList.get(i).getCode());
            cell = row.createCell(1);
            cell.setCellValue(icdEntityList.get(i).getName());
            cell = row.createCell(2);
            cell.setCellValue(icdEntityList.get(i).getHelpCode());
        }
        try {
            FileOutputStream outputStream = new FileOutputStream("/Users/wuyuyang128/icdData" + start + "-" + end + ".xlsx");
            workbook.write(outputStream);
            outputStream.flush();
            outputStream.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static void main(String[] args) {
        long start = System.currentTimeMillis();
        //CrawlerTools2.saveICD(1, 5000);
        //System.out.println("5000结束耗时:" + (System.currentTimeMillis() - start));
        //CrawlerTools2.saveICD(5000, 10000);
        //System.out.println("10000结束耗时:" + (System.currentTimeMillis() - start));
        CrawlerTools2.saveICD(10000, 15000);
        System.out.println("15000结束耗时:" + (System.currentTimeMillis() - start));
        CrawlerTools2.saveICD(15000, 20000);
        System.out.println("20000结束耗时:" + (System.currentTimeMillis() - start));
        CrawlerTools2.saveICD(20000, 25000);
        System.out.println("25000结束耗时:" + (System.currentTimeMillis() - start));
    }

}
