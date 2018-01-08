package main.java.com.custom;

import java.util.HashMap;
import java.util.Map;

import com.google.common.collect.HashBasedTable;
import com.google.common.collect.Table;
import com.google.common.collect.Table.Cell;

/**
 * HMM工厂
 */
public class HMMFactory {

    //词性标注统计集
    private static Map<String, Integer> allNumOfS;
    //统计的总个数
    private static int total = 0;

    public static HMM createHMM(String content){    
        // 分割后的词(如:迈向/v)
    	//服务/vn
    	//质量/n
    	//，/w
    	//进一步/d
        String[] text = content.toString().split("\\s{1,}");        
        // 分割后的词性(如:v)
        //for(int i=0;i<text.length;i++)
        //    System.out.println(text[i]);
        //System.out.println(text);
        String[] characters = content.split("[0-9|-]*/|\\s{1,}[^a-z]*"); 
        //for(int i=0;i<characters.length;i++)
        //  System.out.println(characters[i]);
        //生产词性标注统计容器，是个hashmap,如 key=n,value=100
        allNumOfS = createAllNumOfS(characters);
        total = characters.length;

        //生产一个HMM对象
        HMM hmm = new HMM();
        //生产初始概率
        hmm.setInitial(createInitial());
        //生产发射概率
        hmm.setEmission(createEmission(text));
        //生产转移概率
        hmm.setTransition(createTransition(characters));
        return hmm;
    }   


    /**
     * 转移概率
     * @param characters
     * @return 
     */
    private static Table<String, String, Double> createTransition(String[] characters) {
        Table<String,String,Integer> tranTotal = HashBasedTable.create();
        Table<String,String,Double> transition = HashBasedTable.create();
        String previous = null;
        String now = null;
        for(int i = 0 ;i<characters.length;i++){
            now = characters[i].trim();
            if(now.equals(""))
                continue;
            if(allNumOfS.containsKey(now)&&allNumOfS.containsKey(previous)){
                if(tranTotal.contains(previous, now))
                    tranTotal.put(previous, now,tranTotal.get(previous, now)+1);
                else{
                    tranTotal.put(previous, now, 1);
                }
            }
            previous = now;
        }
        for(String rowKey:tranTotal.rowKeySet()){
            for(String columnKey:tranTotal.row(rowKey).keySet()){
                transition.put(rowKey, columnKey, ((double) tranTotal.get(rowKey, columnKey))
                        /allNumOfS.get(rowKey));
            }
        }
        return transition;
    }

    /**
     * 发射概率，统计 某个单词，词性，数目/该词性的总数
     * @param text
     * @return 
     */
    private static Table<String, String, Double> createEmission(String[] text) {
        Table<String,String,Integer> emisTotal = HashBasedTable.create();
        Table<String,String,Double> emission = HashBasedTable.create();
        for(int i  = 0;i<text.length;i++){
            String s1[] = text[i].trim().split("/");
            if(s1.length==2&&allNumOfS.containsKey(s1[1].trim())){
                if(emisTotal.contains(s1[0], s1[1])){
                    int n = emisTotal.get(s1[0], s1[1]);
                    emisTotal.put(s1[0], s1[1], n+1);
                }else{
                    emisTotal.put(s1[0], s1[1], 1);
                    
                }
            }
        }
        
        for(Cell<String,String,Integer> cell:emisTotal.cellSet()){
            emission.put(cell.getRowKey(), cell.getColumnKey(), 
                    ((double)cell.getValue())/allNumOfS.get(cell.getColumnKey()));
            //System.out.println(cell.getRowKey() + ":" + cell.getColumnKey());
            //System.out.println(allNumOfS.get(cell.getColumnKey()));
            //System.out.println(cell.getValue());
            //System.out.print(((double)cell.getValue())/allNumOfS.get(cell.getColumnKey()));
        }
        return emission;
    }


    /**
     * 初始概率，//生成词性概率,如 key=n,double n在所有词性的比例
     * @return 
     */
    private static Map<String, Double> createInitial() {
        Map<String,Double> initial = new HashMap<String,Double>();
        for (Map.Entry<String, Integer> entry : allNumOfS.entrySet()){
        	
            initial.put(entry.getKey(), ((double) entry.getValue())/total);
        }
        return initial;
    }


    /**
     * 统计语料库所有词性的个数
     * @param temp
     * @return 
     */
    private static Map<String, Integer> createAllNumOfS(String[] temp){
        Map<String, Integer> all = new HashMap<String,Integer>();
        all.clear();
        for(int i=0;i<temp.length;i++){
            temp[i] = temp[i].toLowerCase().replaceAll("[^a-z]", "").trim();
            if(temp[i].length()>2){
                temp[i] = temp[i].substring(0, 1);
            }
            //统计每个词性的数目
            if(temp[i]!=""){
                all.put(temp[i], all.getOrDefault(temp[i], 0)+1);
            }
        }
        final Map<String,Integer> map =new HashMap<String,Integer>(all);
        //去除词性小于100的垃圾项
        all.forEach((key,value)->{if (value<100) {
            map.remove(key);
        }});
        return map;
    }

}
