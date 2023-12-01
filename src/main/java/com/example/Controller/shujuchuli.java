package com.example.Controller;

import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.RestController;

import java.io.FileInputStream;
import java.io.InputStream;
import java.text.SimpleDateFormat;
import java.util.*;

@Slf4j
public class shujuchuli {

    private static List<Map<String,Object>> zhongjishuju = new ArrayList();

    public void daochu(){
        try{
            InputStream is = new FileInputStream("C:\\Users\\15633\\Desktop\\33.xlsx");
            List<Map<String,Object>> aa =  ExcelDataProcessing.getExcelInfo(is,false,0);

            //加96小时
            for(Map<String,Object> data : aa){
                Integer count = 0;
                String originalOrder = ((String) data.get("运单号"));
                digui(data,originalOrder,count);
            }
            List<Taxi> taxiList = new ArrayList<>();
            SimpleDateFormat sdf =new SimpleDateFormat("yyyy/MM/dd HH:mm" );

            for(Map<String,Object> map: zhongjishuju){
                Taxi taxi = new Taxi();
                taxi.setYundanhao((String) map.get("运单号"));
                taxi.setChepaihao((String) map.get("车牌号"));
                taxi.setYanse((String) map.get("黄色"));
                taxi.setJiandanshijain(sdf.format(map.get("建单时间")));
                taxi.setShoudanshijian(sdf.format(map.get("收单时间")));
                taxi.setQidianmingcheng((String) map.get("起点名称"));
                taxi.setZhongdianmingcheng((String) map.get("终点名称"));
                taxi.setXianlumingcheng((String) map.get("线路名称"));
                taxiList.add(taxi);
            }

            daochu.createExcel(taxiList, "C:\\Users\\15633\\Desktop\\","处理数据");
            System.out.println("数据处理完成!");
            //log.info("数据处理完成!!!!!");
        }catch (Exception e){
            e.getMessage();
            //log.error("处理数据报错：",e);
            System.out.println("报错信息::"+e);
        }
    }


    public static void digui(Map<String, Object> data,String originalOrder, Integer count){
        try {
            if("java.lang.String".equals(data.get("建单时间").getClass().getName())){
                SimpleDateFormat sdf =new SimpleDateFormat("yyyy/MM/dd HH:mm" );
                Date aaaa = sdf.parse((String) data.get("建单时间"));
                Date bbbb = sdf.parse((String) data.get("收单时间"));
                data.remove("建单时间");
                data.remove("收单时间");
                data.put("建单时间",aaaa);
                data.put("收单时间",bbbb);
            }
            Calendar cal1 = Calendar.getInstance();
            cal1.setTime((Date) data.get("建单时间"));
            cal1.add(Calendar.DATE, 4);//  4天
            Date date1 = cal1.getTime();

            Date jieshu  = (Date) data.get("收单时间");

            //开始时间加96小时如果大于结束时间那么就拆分一条出来
            if(date1.compareTo(jieshu) < 0){
                Map<String,Object> zhongzhuan1 = new HashMap<>();
                zhongzhuan1.put("运单号",originalOrder+"_"+ ++count);
                zhongzhuan1.put("车牌号",data.get("车牌号"));
                zhongzhuan1.put("黄色",data.get("黄色"));
                zhongzhuan1.put("建单时间",data.get("建单时间"));
                zhongzhuan1.put("收单时间",date1);
                zhongzhuan1.put("起点名称",data.get("起点名称"));
                zhongzhuan1.put("终点名称",data.get("终点名称"));
                zhongzhuan1.put("线路名称",data.get("线路名称"));

                zhongjishuju.add(zhongzhuan1);

                //运单需要重新定义名字
                Map<String,Object> zhongzhuan2 = new HashMap<>();
                zhongzhuan2.put("运单号",originalOrder);
                zhongzhuan2.put("车牌号",data.get("车牌号"));
                zhongzhuan2.put("黄色",data.get("黄色"));
                zhongzhuan2.put("建单时间",date1);
                zhongzhuan2.put("收单时间",data.get("收单时间"));
                zhongzhuan2.put("起点名称",data.get("起点名称"));
                zhongzhuan2.put("终点名称",data.get("终点名称"));
                zhongzhuan2.put("线路名称",data.get("线路名称"));

                digui(zhongzhuan2,originalOrder,count);
            }else{
                data.put("运单号",originalOrder+"_"+ ++count);
                zhongjishuju.add(data);
            }
        }catch (Exception e){
            //log.error("报错信息::",e);
            System.out.println("报错信息::"+e);
        }
    }
}
