package com.byteplan.fabric.base.web;

import cn.hutool.core.io.FileUtil;
import cn.hutool.core.util.StrUtil;

import java.io.File;
import java.nio.charset.StandardCharsets;
import java.util.List;

/**
 * <p>
 *
 * </p>
 *
 * @author bin.xie
 * @since 2023/12/7
 */
public class Test {

    public static void main(String[] args) {
        cctv();
        System.out.println("");
        System.out.println("");
        cntv();
        System.out.println("");
        System.out.println("");
        huya();
    }

    private static void cctv() {
        String path = "/Users/bin.xie/Downloads/tv/cctv.txt";
        List<String> strings = FileUtil.readLines(new File(path), StandardCharsets.UTF_8);
        for (String string : strings) {
            List<String> items = StrUtil.split(string, ',', 2);
            String tvName = items.get(0);
            if (tvName.contains("CCTV-17")) {
                System.out.println("#EXTINF:-1 tvg-id=\"CCTV17\" tvg-name=\"CCTV17\" tvg-logo=\"https://live.fanmingming.com/tv/CCTV17.png\" group-title=\"央视频道\",CCTV-17 农业农村");
            } else if (tvName.contains("CCTV-16")) {
                System.out.println("#EXTINF:-1 tvg-id=\"CCTV16\" tvg-name=\"CCTV16\" tvg-logo=\"https://live.fanmingming.com/tv/CCTV16.png\" group-title=\"央视频道\",CCTV-16 奥林匹克");
            } else if (tvName.contains("CCTV-15")) {
                System.out.println("#EXTINF:-1 tvg-id=\"CCTV15\" tvg-name=\"CCTV15\" tvg-logo=\"https://live.fanmingming.com/tv/CCTV15.png\" group-title=\"央视频道\",CCTV-15 音乐");
            }else if (tvName.contains("CCTV-14")) {
                System.out.println("#EXTINF:-1 tvg-id=\"CCTV14\" tvg-name=\"CCTV14\" tvg-logo=\"https://live.fanmingming.com/tv/CCTV14.png\" group-title=\"央视频道\",CCTV-14 少儿");
            }else if (tvName.contains("CCTV-13")) {
                System.out.println("#EXTINF:-1 tvg-id=\"CCTV13\" tvg-name=\"CCTV13\" tvg-logo=\"https://live.fanmingming.com/tv/CCTV13.png\" group-title=\"央视频道\",CCTV-13 新闻");
            }else if (tvName.contains("CCTV-12")) {
                System.out.println("#EXTINF:-1 tvg-id=\"CCTV12\" tvg-name=\"CCTV12\" tvg-logo=\"https://live.fanmingming.com/tv/CCTV12.png\" group-title=\"央视频道\",CCTV-12 社会与法");
            }else if (tvName.contains("CCTV-11")) {
                System.out.println("#EXTINF:-1 tvg-id=\"CCTV11\" tvg-name=\"CCTV11\" tvg-logo=\"https://live.fanmingming.com/tv/CCTV11.png\" group-title=\"央视频道\",CCTV-11 戏曲");
            }else if (tvName.contains("CCTV-10")) {
                System.out.println("#EXTINF:-1 tvg-id=\"CCTV10\" tvg-name=\"CCTV10\" tvg-logo=\"https://live.fanmingming.com/tv/CCTV10.png\" group-title=\"央视频道\",CCTV-10 科教");
            }else if (tvName.contains("CCTV-5+")) {
                System.out.println("#EXTINF:-1 tvg-id=\"CCTV5+\" tvg-name=\"CCTV5+\" tvg-logo=\"https://live.fanmingming.com/tv/CCTV5+.png\" group-title=\"央视频道\",CCTV-5+ 体育赛事");
            }else if (tvName.contains("CCTV-9")) {
                System.out.println("#EXTINF:-1 tvg-id=\"CCTV9\" tvg-name=\"CCTV9\" tvg-logo=\"https://live.fanmingming.com/tv/CCTV9.png\" group-title=\"央视频道\",CCTV-9 纪录");
            }else if (tvName.contains("CCTV-8")) {
                System.out.println("#EXTINF:-1 tvg-id=\"CCTV8\" tvg-name=\"CCTV8\" tvg-logo=\"https://live.fanmingming.com/tv/CCTV8.png\" group-title=\"央视频道\",CCTV-8 电视剧");
            }else if (tvName.contains("CCTV-7")) {
                System.out.println("#EXTINF:-1 tvg-id=\"CCTV7\" tvg-name=\"CCTV7\" tvg-logo=\"https://live.fanmingming.com/tv/CCTV7.png\" group-title=\"央视频道\",CCTV-7 国防军事");
            }else if (tvName.contains("CCTV-6")) {
                System.out.println("#EXTINF:-1 tvg-id=\"CCTV6\" tvg-name=\"CCTV6\" tvg-logo=\"https://live.fanmingming.com/tv/CCTV6.png\" group-title=\"央视频道\",CCTV-6 电影");
            }else if (tvName.contains("CCTV-5")) {
                System.out.println("#EXTINF:-1 tvg-id=\"CCTV5\" tvg-name=\"CCTV5\" tvg-logo=\"https://live.fanmingming.com/tv/CCTV5.png\" group-title=\"央视频道\",CCTV-5 体育");
            }else if (tvName.contains("CCTV-4")) {
                System.out.println("#EXTINF:-1 tvg-id=\"CCTV4\" tvg-name=\"CCTV4\" tvg-logo=\"https://live.fanmingming.com/tv/CCTV4.png\" group-title=\"央视频道\",CCTV-4 中文国际");
            }else if (tvName.contains("CCTV-3")) {
                System.out.println("#EXTINF:-1 tvg-id=\"CCTV3\" tvg-name=\"CCTV3\" tvg-logo=\"https://live.fanmingming.com/tv/CCTV3.png\" group-title=\"央视频道\",CCTV-3 综艺");
            }else if (tvName.contains("CCTV-2")) {
                System.out.println("#EXTINF:-1 tvg-id=\"CCTV2\" tvg-name=\"CCTV2\" tvg-logo=\"https://live.fanmingming.com/tv/CCTV2.png\" group-title=\"央视频道\",CCTV-2 财经");
            }else if (tvName.contains("CCTV-1")) {
                System.out.println("#EXTINF:-1 tvg-id=\"CCTV1\" tvg-name=\"CCTV1\" tvg-logo=\"https://live.fanmingming.com/tv/CCTV1.png\" group-title=\"央视频道\",CCTV-1 综合");
            }
            System.out.println(items.get(1));
        }
    }

    private static void cntv() {
        String path = "/Users/bin.xie/Downloads/tv/cntv.txt";
        String name = "卫视频道";
        String replace = "#EXTINF:-1 tvg-id=\"%s\" tvg-name=\"%s\" tvg-logo=\"https://live.fanmingming.com/tv/%s.png\" group-title=\"" + name +"\",%s";
        List<String> strings = FileUtil.readLines(new File(path), StandardCharsets.UTF_8);
        for (String string : strings) {
            List<String> items = StrUtil.split(string, ',', 2);
            String s = replace.replaceAll("%s", items.get(0));
            System.out.println(s);
            System.out.println(items.get(1));
        }
    }

    private static void huya() {
        String path = "/Users/bin.xie/Downloads/tv/huya.txt";
        String name = "虎牙直播";
        String replace = "#EXTINF:-1 tvg-id=\"%s\" tvg-name=\"%s\" tvg-logo=\"https://live.fanmingming.com/tv/4K.png\" group-title=\"" + name +"\",%s";
        List<String> strings = FileUtil.readLines(new File(path), StandardCharsets.UTF_8);
        for (String string : strings) {
            List<String> items = StrUtil.split(string, ',', 2);
            String s = replace.replaceAll("%s", items.get(0));
            System.out.println(s);
            System.out.println(items.get(1));
        }
    }
}
