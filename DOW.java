package com.byteplan.fabric.seeddata.utils;

import cn.hutool.core.io.FileUtil;
import cn.hutool.http.HttpUtil;
import cn.hutool.json.JSONUtil;

import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

/**
 * <p>
 *
 * </p>
 *
 * @author bin.xie
 * @since 2025/2/7
 */
public class Test {
    private static Map<String, String> allMap = new LinkedHashMap<>();



    static {
        allMap.put("CCTV1", "#EXTINF:-1 tvg-name=\"CCTV1\" tvg-logo=\"https://live.fanmingming.cn/tv/CCTV1.png\" group-title=\"央视频道\",CCTV-1 综合");
        allMap.put("CCTV2", "#EXTINF:-1 tvg-name=\"CCTV2\" tvg-logo=\"https://live.fanmingming.cn/tv/CCTV2.png\" group-title=\"央视频道\",CCTV-2 财经");
        allMap.put("CCTV3", "#EXTINF:-1 tvg-name=\"CCTV3\" tvg-logo=\"https://live.fanmingming.cn/tv/CCTV3.png\" group-title=\"央视频道\",CCTV-3 综艺");
        allMap.put("CCTV4", "#EXTINF:-1 tvg-name=\"CCTV4\" tvg-logo=\"https://live.fanmingming.cn/tv/CCTV4.png\" group-title=\"央视频道\",CCTV-4 中文国际");
        allMap.put("CCTV5", "#EXTINF:-1 tvg-name=\"CCTV5\" tvg-logo=\"https://live.fanmingming.cn/tv/CCTV5.png\" group-title=\"央视频道\",CCTV-5 体育");
        allMap.put("CCTV5+", "#EXTINF:-1 tvg-name=\"CCTV5+\" tvg-logo=\"https://live.fanmingming.cn/tv/CCTV5+.png\" group-title=\"央视频道\",CCTV-5+ 体育赛事");
        allMap.put("CCTV6", "#EXTINF:-1 tvg-name=\"CCTV6\" tvg-logo=\"https://live.fanmingming.cn/tv/CCTV6.png\" group-title=\"央视频道\",CCTV-6 电影");
        allMap.put("CCTV7", "#EXTINF:-1 tvg-name=\"CCTV7\" tvg-logo=\"https://live.fanmingming.cn/tv/CCTV7.png\" group-title=\"央视频道\",CCTV-7 国防军事");
        allMap.put("CCTV8", "#EXTINF:-1 tvg-name=\"CCTV8\" tvg-logo=\"https://live.fanmingming.cn/tv/CCTV8.png\" group-title=\"央视频道\",CCTV-8 电视剧");
        allMap.put("CCTV9", "#EXTINF:-1 tvg-name=\"CCTV9\" tvg-logo=\"https://live.fanmingming.cn/tv/CCTV9.png\" group-title=\"央视频道\",CCTV-9 纪录");
        allMap.put("CCTV10", "#EXTINF:-1 tvg-name=\"CCTV10\" tvg-logo=\"https://live.fanmingming.cn/tv/CCTV10.png\" group-title=\"央视频道\",CCTV-10 科教");
        allMap.put("CCTV11", "#EXTINF:-1 tvg-name=\"CCTV11\" tvg-logo=\"https://live.fanmingming.cn/tv/CCTV11.png\" group-title=\"央视频道\",CCTV-11 戏曲");
        allMap.put("CCTV12", "#EXTINF:-1 tvg-name=\"CCTV12\" tvg-logo=\"https://live.fanmingming.cn/tv/CCTV12.png\" group-title=\"央视频道\",CCTV-12 社会与法");
        allMap.put("CCTV13", "#EXTINF:-1 tvg-name=\"CCTV13\" tvg-logo=\"https://live.fanmingming.cn/tv/CCTV13.png\" group-title=\"央视频道\",CCTV-13 新闻");
        allMap.put("CCTV14", "#EXTINF:-1 tvg-name=\"CCTV14\" tvg-logo=\"https://live.fanmingming.cn/tv/CCTV14.png\" group-title=\"央视频道\",CCTV-14 少儿");
        allMap.put("CCTV15", "#EXTINF:-1 tvg-name=\"CCTV15\" tvg-logo=\"https://live.fanmingming.cn/tv/CCTV15.png\" group-title=\"央视频道\",CCTV-15 音乐");
        allMap.put("CCTV16", "#EXTINF:-1 tvg-name=\"CCTV16\" tvg-logo=\"https://live.fanmingming.cn/tv/CCTV16.png\" group-title=\"央视频道\",CCTV-16 奥林匹克");
        allMap.put("CCTV17", "#EXTINF:-1 tvg-name=\"CCTV17\" tvg-logo=\"https://live.fanmingming.cn/tv/CCTV17.png\" group-title=\"央视频道\",CCTV-17 农业农村");


        allMap.put("湖南卫视", "#EXTINF:-1 tvg-id=\"湖南卫视\" tvg-name=\"湖南卫视\" tvg-logo=\"https://live.fanmingming.cn/tv/湖南卫视.png\" group-title=\"卫视频道\",湖南卫视");
        allMap.put("北京卫视", "#EXTINF:-1 tvg-id=\"北京卫视\" tvg-name=\"北京卫视\" tvg-logo=\"https://live.fanmingming.cn/tv/北京卫视.png\" group-title=\"卫视频道\",北京卫视");
        allMap.put("江苏卫视", "#EXTINF:-1 tvg-name=\"江苏卫视\" tvg-logo=\"https://live.fanmingming.cn/tv/江苏卫视.png\" group-title=\"卫视频道\",江苏卫视");
        allMap.put("东方卫视", "#EXTINF:-1 tvg-name=\"东方卫视\" tvg-logo=\"https://live.fanmingming.cn/tv/东方卫视.png\" group-title=\"卫视频道\",东方卫视");
        allMap.put("浙江卫视", "#EXTINF:-1 tvg-name=\"浙江卫视\" tvg-logo=\"https://live.fanmingming.cn/tv/浙江卫视.png\" group-title=\"卫视频道\",浙江卫视");
        allMap.put("安徽卫视", "#EXTINF:-1 tvg-name=\"安徽卫视\" tvg-logo=\"https://live.fanmingming.cn/tv/安徽卫视.png\" group-title=\"卫视频道\",安徽卫视");
        allMap.put("天津卫视", "#EXTINF:-1 tvg-name=\"天津卫视\" tvg-logo=\"https://live.fanmingming.cn/tv/天津卫视.png\" group-title=\"卫视频道\",天津卫视");
        allMap.put("湖北卫视", "#EXTINF:-1 tvg-name=\"湖北卫视\" tvg-logo=\"https://live.fanmingming.cn/tv/湖北卫视.png\" group-title=\"卫视频道\",湖北卫视");
        allMap.put("重庆卫视", "#EXTINF:-1 tvg-name=\"重庆卫视\" tvg-logo=\"https://live.fanmingming.cn/tv/重庆卫视.png\" group-title=\"卫视频道\",重庆卫视");
        allMap.put("山东卫视", "#EXTINF:-1 tvg-name=\"山东卫视\" tvg-logo=\"https://live.fanmingming.cn/tv/山东卫视.png\" group-title=\"卫视频道\",山东卫视");
        allMap.put("深圳卫视", "#EXTINF:-1 tvg-name=\"深圳卫视\" tvg-logo=\"https://live.fanmingming.cn/tv/深圳卫视.png\" group-title=\"卫视频道\",深圳卫视");
        allMap.put("广东卫视", "#EXTINF:-1 tvg-name=\"广东卫视\" tvg-logo=\"https://live.fanmingming.cn/tv/广东卫视.png\" group-title=\"卫视频道\",广东卫视");
        allMap.put("江西卫视", "#EXTINF:-1 tvg-name=\"江西卫视\" tvg-logo=\"https://live.fanmingming.cn/tv/江西卫视.png\" group-title=\"卫视频道\",江西卫视");
        allMap.put("河南卫视", "#EXTINF:-1 tvg-name=\"河南卫视\" tvg-logo=\"https://live.fanmingming.cn/tv/河南卫视.png\" group-title=\"卫视频道\",河南卫视");
        allMap.put("辽宁卫视", "#EXTINF:-1 tvg-name=\"辽宁卫视\" tvg-logo=\"https://live.fanmingming.cn/tv/辽宁卫视.png\" group-title=\"卫视频道\",辽宁卫视");
        allMap.put("黑龙江卫视", "#EXTINF:-1 tvg-name=\"黑龙江卫视\" tvg-logo=\"https://live.fanmingming.cn/tv/黑龙江卫视.png\" group-title=\"卫视频道\",黑龙江卫视");
        allMap.put("广西卫视", "#EXTINF:-1 tvg-name=\"广西卫视\" tvg-logo=\"https://live.fanmingming.cn/tv/广西卫视.png\" group-title=\"卫视频道\",广西卫视");
        allMap.put("河北卫视", "#EXTINF:-1 tvg-name=\"河北卫视\" tvg-logo=\"https://live.fanmingming.cn/tv/河北卫视.png\" group-title=\"卫视频道\",河北卫视");
        allMap.put("吉林卫视", "#EXTINF:-1 tvg-name=\"吉林卫视\" tvg-logo=\"https://live.fanmingming.cn/tv/吉林卫视.png\" group-title=\"卫视频道\",吉林卫视");
        allMap.put("四川卫视", "#EXTINF:-1 tvg-name=\"四川卫视\" tvg-logo=\"https://live.fanmingming.cn/tv/四川卫视.png\" group-title=\"卫视频道\",四川卫视");
        allMap.put("东南卫视", "#EXTINF:-1 tvg-name=\"东南卫视\" tvg-logo=\"https://live.fanmingming.cn/tv/东南卫视.png\" group-title=\"卫视频道\",东南卫视");
        allMap.put("青海卫视", "#EXTINF:-1 tvg-name=\"青海卫视\" tvg-logo=\"https://live.fanmingming.cn/tv/青海卫视.png\" group-title=\"卫视频道\",青海卫视");
        allMap.put("贵州卫视", "#EXTINF:-1 tvg-name=\"贵州卫视\" tvg-logo=\"https://live.fanmingming.cn/tv/贵州卫视.png\" group-title=\"卫视频道\",贵州卫视");
        allMap.put("甘肃卫视", "#EXTINF:-1 tvg-name=\"甘肃卫视\" tvg-logo=\"https://live.fanmingming.cn/tv/甘肃卫视.png\" group-title=\"卫视频道\",甘肃卫视");
        allMap.put("云南卫视", "#EXTINF:-1 tvg-name=\"云南卫视\" tvg-logo=\"https://live.fanmingming.cn/tv/云南卫视.png\" group-title=\"卫视频道\",云南卫视");
        allMap.put("陕西卫视", "#EXTINF:-1 tvg-name=\"陕西卫视\" tvg-logo=\"https://live.fanmingming.cn/tv/陕西卫视.png\" group-title=\"卫视频道\",陕西卫视");
        allMap.put("山西卫视", "#EXTINF:-1 tvg-name=\"山西卫视\" tvg-logo=\"https://live.fanmingming.cn/tv/山西卫视.png\" group-title=\"卫视频道\",山西卫视");

        allMap.put("湖南经视", "#EXTINF:-1,tvg-id=\"湖南经视\" tvg-name=\"湖南经视\" tvg-logo=\"https://epg.v1.mk/logo/湖南经视.png\" group-title=\"湖南\",湖南经视");
        allMap.put("湖南都市", "#EXTINF:-1,tvg-id=\"湖南都市\" tvg-name=\"湖南都市\" tvg-logo=\"https://epg.v1.mk/logo/湖南都市.png\" group-title=\"湖南\",湖南都市");
        allMap.put("湖南电视剧", "#EXTINF:-1,tvg-id=\"湖南电视剧\" tvg-name=\"湖南电视剧\" tvg-logo=\"https://epg.v1.mk/logo/湖南电视剧.png\" group-title=\"湖南\",湖南电视剧");
        allMap.put("金鹰卡通", "#EXTINF:-1,tvg-id=\"金鹰卡通\" tvg-name=\"金鹰卡通\" tvg-logo=\"https://epg.v1.mk/logo/金鹰卡通.png\" group-title=\"湖南\",金鹰卡通");
    }


    private static Map<String, Boolean> getStatus() {
        String s = HttpUtil.get("https://gyssi.link/iptv/chinaiptv/streams_status.json");
        return JSONUtil.toBean(s, Map.class);
    }

    public static void main(String[] args) throws Exception {
        Map<String, Boolean> status = getStatus();
        String title = "#EXTM3U x-tvg-url=\"https://epg.zbds.top\" catchup=\"append\" catchup-source=\"?playseek=${(b)yyyyMMddHHmmss}-${(e)yyyyMMddHHmmss}\"";
        Map<String, String> yunNan = listUrl("云南省", status);
        Map<String, String> shanDong = listUrl("山东省", status);
        Map<String, String> shangHai = listUrl("上海市", status);
        Map<String, String> huNan = listUrl("湖南省", status);
        String path = "e:/tmp/tv.m3u";
        List<String> result = new ArrayList<>();
        result.add(title);
        allMap.forEach((k, v) -> {
            result.add(v);
            if (yunNan.containsKey(k)) {
                result.add(yunNan.get(k));
            }
            if (shangHai.containsKey(k)) {
                result.add(shangHai.get(k));
            }
            if (shanDong.containsKey(k)) {
                result.add(shanDong.get(k));
            }
            if (huNan.containsKey(k)) {
                result.add(huNan.get(k));
            }
        });
        FileUtil.writeLines(result, path, Charset.defaultCharset());
        System.out.println(1);
        //String stmt = "cmd /c e:/fabric/api/111.bat";

        //String[] command = {"cmd.exe", "/c", stmt};

        // Process exec = Runtime.getRuntime().exec(stmt);
        //int i = exec.waitFor();
    }
    private static Map<String, String> listUrl(String name, Map<String, Boolean> status) {
        Map<String, String> map = new HashMap<>();
        if (!Boolean.TRUE.equals(status.get(name + ".m3u"))) {
            return map;
        }
        String path = "e:/tmp/" + name + ".m3u";
        String token = "";
        String url = String.format("https://gyssi.link/iptv/chinaiptv/%s.m3u?token=%s", HttpUtil.encodeParams(name, StandardCharsets.UTF_8), token);
        HttpUtil.downloadFile(url, path);
        List<String> strings = FileUtil.readLines(path, StandardCharsets.UTF_8);

        String reg = "[\u4e00-\u9fa5]";
        for (int i = 0; i < strings.size(); i++) {
            String s = strings.get(i);
            if (s.startsWith("#EXTINF:-1")) {
                String[] split = s.split(",");
                String tag = split[split.length - 1].toUpperCase();
                if (tag.startsWith("CCTV")) {
                    tag = tag.replaceAll(reg, "");
                }
                tag = tag.replaceAll("-", "").replaceAll("高清", "");
                map.put(tag, strings.get(i + 1));
            }
        }
        FileUtil.del(path);
        return map;
    }
}
