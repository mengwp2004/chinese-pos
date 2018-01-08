package main.java.com.custom;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

public class WordPos {

	/**
     * 统计语料库所有词性的个数
     * @param temp
     * @return 
     */
    private static Map<String, Integer> createAllNumOfS1(String[] temp){
        Map<String, Integer> all = new HashMap<String,Integer>();
        all.clear();
        for(int i=0;i<temp.length;i++){
            temp[i] = temp[i].toLowerCase().replaceAll("[^a-z]", "").trim();
            if(temp[i].length()>2){
                temp[i] = temp[i].substring(0, 1);
            }
            if(temp[i]!=""){
                all.put(temp[i], all.getOrDefault(temp[i], 0)+1);
            }
        }
        final Map<String,Integer> map =new HashMap<String,Integer>(all);
        //去除垃圾项
        
        /*all.forEach((key,value)->{if (value<100) {
            total--;
            map.remove(key);
        }});*/
        return map;
    }
    
    public static void main(String[] args) throws Exception {
    	
		// ----------------------------------------------------------------------------------
		// 统计出训练样本中词性种类及其频率
		String content = "";
		BufferedReader reader = null;
		try { // 读取199801train.txt文本中的内容，并保存在content的字符流中
			reader = new BufferedReader(new FileReader("src/main/resources/199801train.txt"));
			String line;
			while ((line = reader.readLine()) != null)
				content += line;
		} catch (IOException e) {
			e.printStackTrace();
		} finally {
			if (reader != null) {
				try {
					reader.close();
				} catch (IOException e) {
				}
			}
		}

		String[] text; // text[]用于存储训练样本中的词语
		text = content.split("(/[a-z]*\\s{0,})|(][a-z]*\\s{1,})"); // 去除词性标注
        //HMM hmm = HMMFactory.createHMM(FileRW.read("199801.txt").replaceAll("[0-9]{8}-[0-9]{2}-[0-9]{3}-[0-9]{3}/m", ""));
		HMM hmm = HMMFactory.createHMM(content.replaceAll("[0-9]{8}-[0-9]{2}-[0-9]{3}-[0-9]{3}/m", ""));
		//System.out.println(hmm.getEmission());
        String str[] = {
                "台湾 是 中国 领土 不可分割 的 部分",
                "这部 电视片 还 强调 表现 敦煌 文化 的 珍贵性 和 观赏性",
                "湖南 备耕 安排 早 动手 快",
                "我 第一 次 听到 这 首 歌 ， 是 在 六 年 前 的 大年三十 春节 联欢 晚会 上"};
        String s2[] ={
                "[ns, r, ns, n, l, u, n]",
                "[r, n, v, d, v, ns, n, u, n, c, n]",
                "[ns, vn, v, a, v, a]",
                "[r, m, q, v, r, q, n, w, v, p, m, q, f, u, t, t, vn, n, f]"};
        double value = 0;
        for(int i =  0;i<str.length;i++){
            value = value + access(hmm.viterbi(str[i].split(" ")),s2[i]);
        }
        System.out.println("评分结果:");
        System.out.printf("%.2f",value/str.length);
    }

    private static double access(String answer, String result) {
        System.out.println("计算结果: "+answer);
        System.out.println("真实结果: "+result);
        String[] s1 = answer.split(", ");
        String[] s2 = result.split(", ");
        double count = 0;
        for(int i = 0;i<s1.length;i++){
            if(s1[i].equals(s2[i]))
                count++;
        }
        System.out.println("命中率: "+count/s1.length);
        System.out.println("=======================================");
        return count/s1.length;
    }
}
