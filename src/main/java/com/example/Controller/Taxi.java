package com.example.Controller;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class Taxi{
    String yundanhao;

    String chepaihao;

    String yanse;

    String jiandanshijain;

    String  shoudanshijian;

    String qidianmingcheng;

    String zhongdianmingcheng;

    String xianlumingcheng;

    public String getYundanhao() {
        return yundanhao;
    }

    public void setYundanhao(String yundanhao) {
        this.yundanhao = yundanhao;
    }

    public String getChepaihao() {
        return chepaihao;
    }

    public void setChepaihao(String chepaihao) {
        this.chepaihao = chepaihao;
    }

    public String getYanse() {
        return yanse;
    }

    public void setYanse(String yanse) {
        this.yanse = yanse;
    }

    public String getJiandanshijain() {
        return jiandanshijain;
    }

    public void setJiandanshijain(String jiandanshijain) {
        this.jiandanshijain = jiandanshijain;
    }

    public String getShoudanshijian() {
        return shoudanshijian;
    }

    public void setShoudanshijian(String shoudanshijian) {
        this.shoudanshijian = shoudanshijian;
    }

    public String getQidianmingcheng() {
        return qidianmingcheng;
    }

    public void setQidianmingcheng(String qidianmingcheng) {
        this.qidianmingcheng = qidianmingcheng;
    }

    public String getZhongdianmingcheng() {
        return zhongdianmingcheng;
    }

    public void setZhongdianmingcheng(String zhongdianmingcheng) {
        this.zhongdianmingcheng = zhongdianmingcheng;
    }

    public String getXianlumingcheng() {
        return xianlumingcheng;
    }

    public void setXianlumingcheng(String xianlumingcheng) {
        this.xianlumingcheng = xianlumingcheng;
    }
}
