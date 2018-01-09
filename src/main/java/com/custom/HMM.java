package main.java.com.custom;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import com.google.common.collect.HashBasedTable;
import com.google.common.collect.Table;

/**
 * HMM参数表 实现了Viterbi算法
 */
public class HMM {

	// 初始概率
	private Map<String, Double> initial = new HashMap<>();
	// 转移概率
	private Table<String, String, Double> transition = HashBasedTable.create();
	// 发射概率
	private Table<String, String, Double> emission = HashBasedTable.create();

	@Override
	public String toString() {
		return initial.toString() + "\n" + transition.toString() + "\n" + emission.toString();
	}

	// get,set方法
	public void setInitial(Map<String, Double> initial) {
		this.initial = initial;
	}

	public void setTransition(Table<String, String, Double> transition) {
		this.transition = transition;
	}

	public void setEmission(Table<String, String, Double> emission) {
		this.emission = emission;
	}

	public Map<String, Double> getInitial() {
		return initial;
	}

	public Table<String, String, Double> getTransition() {
		return transition;
	}

	public Table<String, String, Double> getEmission() {
		return emission;
	}

	/**
	 * 计算viterbi算法
	 * 
	 * @param strs
	 * @return
	 */
	public String viterbi(String... strs) {
		if (strs == null)
			return null;
		else if (strs.length == 1)
			return viterbiMin(viterbiMap(emission.row(strs[0]), initial)).getKey();
		else {
			return Arrays.toString(viterbi(strs, 1, viterbiMap(emission.row(strs[0]), initial)));
		}
	}
	 /**
     * 求解HMM模型
     * @param obs 观测序列
     * @param states 隐状态
     * @param start_p 初始概率（隐状态）
     * @param trans_p 转移概率（隐状态）
     * @param emit_p 发射概率 （隐状态表现为显状态的概率）
     * @return 最可能的序列
     */
	/*
    public static int[] viterbi1(String [] obs, Map<String, Integer> states, Map<String, Double> start_p, Table<String, String, Double>  trans_p, Table<String, String, Double>  emit_p)
    {
        double[][] V = new double[obs.length][states.size()];
        int[][] path = new int[states.size()][obs.length];

  
        for (Map.Entry<String, Integer> entry : states.entrySet()){
        	String y =entry.getKey();
            V[0][y] = start_p.get(y) * emit_p.get[obs[0]];
            path[y][0] = y;      	
            initial.put(entry.getKey(), ((double) entry.getValue())/total);
        }
        
        for (int y : states)
        {
            V[0][y] = start_p[y] * emit_p[y][obs[0]];
            path[y][0] = y;
        }

        for (int t = 1; t < obs.length; ++t)
        {
            int[][] newpath = new int[states.length][obs.length];

            for (int y : states)
            {
                double prob = -1;
                int state;
                for (int y0 : states)
                {
                    double nprob = V[t - 1][y0] * trans_p[y0][y] * emit_p[y][obs[t]];
                    if (nprob > prob)
                    {
                        prob = nprob;
                        state = y0;
                        // 记录最大概率
                        V[t][y] = prob;
                        // 记录路径
                        System.arraycopy(path[state], 0, newpath[y], 0, t);
                        newpath[y][t] = y;
                    }
                }
            }

            path = newpath;
        }

        double prob = -1;
        int state = 0;
        for (int y : states)
        {
            if (V[obs.length - 1][y] > prob)
            {
                prob = V[obs.length - 1][y];
                state = y;
            }
        }

        return path[state];
    }
    */
	
	/**
	 * 递归回溯法寻找最佳可能
	 * 
	 * @param strs
	 * @param i
	 * @param map
	 * @return
	 */
	private String[] viterbi(String[] strs, int i, Map<String, Double> preMap) {
		Map<String, Double> nowMap = new HashMap<String, Double>();
		Map<String, String> preAndNow = new HashMap<String, String>();
		// 寻找当前strs[i]所有可能情况的每一种最佳选择
		
		for (Entry<String, Double> now : emission.row(strs[i]).entrySet()) {
			double min = Double.MAX_VALUE;
			String best = null;
			for (Entry<String, Double> pre : preMap.entrySet()) {
				double value = viterbiCaculate(strs[i], now, pre);
				if (value < min) {
					min = value;
					best = pre.getKey();
				}
			}
			preAndNow.put(now.getKey(), best);
			nowMap.put(now.getKey(), min);
		}
		String[] list = null;
		// 最后一层
		if (i == strs.length - 1) {
			String key = viterbiMin(nowMap).getKey();
			list = new String[strs.length];
			list[i] = key;
			list[i - 1] = preAndNow.get(key);
		} else {
			list = viterbi(strs, ++i, nowMap);
			list[i - 2] = preAndNow.get(list[i - 1]);
		}
		return list;
	}

	/**
	 * 获取map里的最小值
	 * 
	 * @param map
	 * @return
	 */
	private Entry<String, Double> viterbiMin(Map<String, Double> map) {
		List<Entry<String, Double>> list = new ArrayList<>(map.entrySet());
		Collections.sort(list, new Comparator<Entry<String, Double>>() {
			@Override
			public int compare(Entry<String, Double> o1, Entry<String, Double> o2) {
				return o1.getValue() > o2.getValue() ? 1 : -1;
			}
		});
		return list.get(0);
	}

	/**
	 * 所有map1和map2相同key对应的value求对数后相加,返回一个新的map存储 一般用于求第一个数的viterbi算法的值
	 * 
	 * @param map1
	 * @param map2
	 * @return
	 */
	private Map<String, Double> viterbiMap(Map<String, Double> map1, Map<String, Double> map2) {
		System.out.println(map1);
		System.out.println("-------");
		System.out.print(map2);
		Map<String, Double> map = new HashMap<String, Double>();
		for (Entry<String, Double> entry : map1.entrySet()) {
			double value = Math.log(entry.getValue()) + Math.log(map2.get(entry.getKey()));
			map.put(entry.getKey(), -value);
		}
		System.out.println("input map:");
		System.out.print(map);
		return map;
	}

	/**
	 * 计算转移概率对数+发射概率对数+pre的值的和
	 * 
	 * @param word
	 * @param now
	 * @param pre
	 * @return
	 */
	private double viterbiCaculate(String word, Entry<String, Double> now, Entry<String, Double> pre) {
		// System.out.println(word);
		// System.out.println(now);
		// System.out.println(word,now,pre);
		// return Math.log(transition.get(pre.getKey(),
		// now.getKey()))+Math.log(now.getValue())+pre.getValue();
		try {

			if (now == null) {
				System.out.println("now is null");
				return 0.0;
			}
			if (pre == null) {
				System.out.println("pre is null");
				return 0.0;
			}

			if (transition == null) {
				System.out.println("transition is null");
				return 0.0;
			}
			double t = transition.get(pre.getKey(), now.getKey());
			if (t < 0) {
				System.out.println("t<0: " + t);
			}
			double n = now.getValue();
			if (n < 0) {
				System.out.println("n<0: " + n);
			}
			return Math.log(t) + Math.log(n) + pre.getValue();
		} catch (Exception e) {
			System.out.println("testEx, catch exception");
			return 0.0;
		}

	}

}
