import net.tccn.base.FileKit;
import net.tccn.base.Kv;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.junit.Test;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;


public class ZoneCodeTest<T> {

    @Test
    public void getArea() {
        String url = "http://www.stats.gov.cn/sj/tjbz/tjyqhdmhcxhfdm/2022/";
        getProvices(url);
//        getCitys("http://www.stats.gov.cn/tjsj/tjbz/tjyqhdmhcxhfdm/2022/43.html","430000");
//        getCountys("http://www.stats.gov.cn/tjsj/tjbz/tjyqhdmhcxhfdm/2022/43.html","430000");

    }

    private static Document connect(String url) {
        try {
            return Jsoup.connect(url).timeout(200 * 2000).get();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    private List<Kv> getProvices(String url) {


        Document connect = connect(url + "index.html");
        Elements elements = connect.select("tr.provincetr");


        for (Element element : elements) {
            Elements select = element.select("a");

            for (Element province : select) {

                String codeurl = province.select("a").attr("href");
                String bid = codeurl.replace(".html", "");

                //每天只同步两个，不然会出问题
                if (!"45".equals(bid)) {
                    continue;
                }
//                if (!"13".equals(bid)) {
//                    continue;
//                }
//                if (!"62".equals(bid)) {
//                    continue;
//                }
//                if (!"36".equals(bid)) {
//                    continue;
//                }

                StringBuilder builder = new StringBuilder();
                String sql = "INSERT INTO `district` (`id`,`bid`,`code`,`name`,`open`,`status`,`level`) VALUES  ";
                builder.append(sql);

                List<Kv> list = new ArrayList<>();

                String name = province.text();
                Kv kv = Kv.of("code", bid + "0000000000").set("bid", bid).set("name", name).set("level", 1);
                list.add(kv);
                System.out.println("开始获取" + name + "市区相关信息");
                String proviceurl = url + codeurl;

                try {
                    Thread.currentThread().sleep(5000);
                    List<Kv> citys = getCitys(proviceurl, bid);
                    list.addAll(citys);
                } catch (InterruptedException e) {
                    throw new RuntimeException(e);
                }

                list.forEach(y -> {
                    String rowsql = String.format("\n(%s,'%s', '%s', '%s', %s, %s, %s),", Integer.parseInt(y.get("bid").toString()), y.get("bid"), y.get("code"), y.get("name"), 1, 10, Integer.parseInt(y.get("level").toString()));
                    builder.append(rowsql);
                });
                builder.deleteCharAt(builder.length() - 1);
                FileKit.strToFile(builder.toString(), new File("tmp/" + name + "行政区划数据.sql"));
            }
        }

        return null;
    }

    private List<Kv> getCitys(String url, String bidsiff) {
        List<Kv> list = new ArrayList<>();
        Document connect = connect(url);
        Elements elements = connect.select("tr.citytr");
        for (Element cityelement : elements) {
            String codeurl = cityelement.select("a").attr("href");
            String name = cityelement.select("td").text();
            String[] split = name.split(" ");
            String bid = split[0].substring(0, 4);
//            Kv kv = Kv.of("addrcode", addrcode + "00000000").set("name", split[1]).set("type", 2).set("parentcode", fathercode);
            Kv kv = Kv.of("code", bid + "00000000").set("bid", bid).set("name", split[1]).set("level", 2);
            list.add(kv);

            try {
                Thread.currentThread().sleep(5000);
                System.out.println("开始获取" + name + "下属区县相关信息");
                String cityurl = "http://www.stats.gov.cn/sj/tjbz/tjyqhdmhcxhfdm/2022/" + codeurl;
                List<Kv> countys = getCountys(cityurl, bidsiff);
                list.addAll(countys);
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }

        }
//        System.out.println(convert.convertTo(list));
        return list;
    }

    private List<Kv> getCountys(String url, String bidsiff) {
        List<Kv> list = new ArrayList<>();
        Document connect = connect(url);
        Elements elements = connect.select("tr.countytr");
        for (Element cityelement : elements) {
            String codeurl = cityelement.select("a").attr("href");
            String name = cityelement.select("td").select("td").text();
            String[] split = name.split(" ");

            if (!"市辖区".equals(split[1])) {
                String bid = split[0].substring(0, 6);
                Kv kv = Kv.of("code", bid + "000000").set("bid", bid).set("name", split[1]).set("level", 3);
//                Kv kv = Kv.of("addrcode", addrcode + "000000").set("name", split[1]).set("type", 3);
                list.add(kv);
                try {
                    Thread.currentThread().sleep(5000);
                    System.out.println("开始获取" + name + "下属街道相关信息");
                    System.out.println("codeurl" + codeurl );
                    if (Utils.isEmpty(codeurl)){
                        continue;
                    }
                    String countyurl = "http://www.stats.gov.cn/sj/tjbz/tjyqhdmhcxhfdm/2022/" + bidsiff + "/" + codeurl;
                    List<Kv> streets = getStreets(countyurl, bidsiff);
                    list.addAll(streets);
                } catch (InterruptedException e) {
                    throw new RuntimeException(e);
                }
            }
        }
        return list;
    }

    private List<Kv> getStreets(String url, String bidsiff) {
        List<Kv> list = new ArrayList<>();
        Document connect = connect(url);
        Elements elements = connect.select("tr.towntr");
        for (Element cityelement : elements) {
            String codeurl = cityelement.select("a").attr("href");
            String name = cityelement.select("td").select("td").select("td").text();
            String[] split = name.split(" ");
            String bid = split[0].substring(0, 9);
            Kv kv = Kv.of("code", bid + "000").set("bid", bid).set("name", split[1]).set("level", 4);
//            Kv kv = Kv.of("addrcode", addrcode + "00").set("name", split[1]).set("type", "02");
            list.add(kv);
        }
        return list;
    }
}