package main.java.com.custom;

import java.util.Vector;


public class HMMforCX {
		int CiXing_type;//�������࣬�ݶ�10
		int sentencenum;//��������
		int compromisednum;//��Ϊ���Ͽⲻ��������µĺ���EmitRobEmitRobMatrixֵ���зִʵľ��������������ľ������ǳ�Ϊcompromised sentence
		final HMMdictionaryforCX dictionary;
		String testQTxtSrcString;//�����ļ�·��
		String testATxtSrcString;//���Դ�·��
		String encodingString;//�����ʽ
		String resultsavepathString;//����洢·��
		String filetype;//����ļ���չ��
		IOforHMM myIOforHMM;
		AlgorithmEvaluation myEvaluation;
		public HMMforCX(String questionSrc,String answerSrc,String encoding,HMMdictionaryforCX dic,String rsltsavepath,String filetp)//����
		{
			testQTxtSrcString=questionSrc;
			testATxtSrcString=answerSrc;
			encodingString=encoding;
			dictionary=dic;
			resultsavepathString=rsltsavepath;
			filetype=filetp;
			compromisednum=0;
			sentencenum=0;
			myIOforHMM=new IOforHMM(2);
			myEvaluation=new AlgorithmEvaluation();
			CiXing_type=dic.getCXtypeNum();
		}
		private String onSentenceAnalyzeByHMM(String sen)//����HMM�з�
		{
			int i,j,k;
			int len=sen.length();
			final Vector wordVector=new Vector(200);
			//long a1,a2,a3,a4;
			
			//a1= System.currentTimeMillis(); 
			
			for(i=0;i<len;)
			{
				int x;
				for(x=i;x<len;x++)
				{
					if(sen.charAt(x)==' ')
						break;
				}
				wordVector.add(sen.substring(i,x));
				while(x<len&&sen.charAt(x)==' ')
				{
					x++;
				}
				i=x;
			}
			int size=wordVector.size();
			double weight[][];
			int path[][];
			weight=new double[CiXing_type][size];
			path=new int[CiXing_type][size];
			
			//a2= System.currentTimeMillis(); 
			
			for(i=0;i<CiXing_type;i++)
			{
				weight[i][0]=dictionary.getInitStatusValue(i)+dictionary.getEmitRobMatrixValue(i, (String)wordVector.get(0));
				path[i][0]=-1;
			}
			boolean compromisecounterswitch=true;
			//�������ӣ��±�i��1��ʼ����Ϊ�ղų�ʼ����ʱ���Ѿ���0��ʼ��������
			for(i = 1; i < size; i++)
			{
			    // �������ܵ�״̬
				boolean ischanged=false;
			    for(j = 0; j < CiXing_type; j++) 
			    {
			        weight[j][i] = Math.log(0);
			        path[j][i] = -1;
			        
			        //����ǰһ���ֿ��ܵ�״̬
			        for(k = 0; k < CiXing_type; k++)
			        {
			            double tmp = weight[k][i-1] + dictionary.getTransProbMatrixValue(k, j)+ dictionary.getEmitRobMatrixValue(j, (String)wordVector.get(i));
			            if(tmp > weight[j][i]) // �ҳ�����weight[j][i]ֵ
			            {
			                weight[j][i] = tmp;
			                path[j][i] = k;
			                ischanged=true;
			            }
			        }
			    }
			    if(ischanged==false)//HMM��δ������ֵ������compromise����,���ݴ��Ա�עδ֪�����ص㣬��עΪn/nr
		        {
			    	weight[dictionary.translateCX("n")][i]=0;//�˴ʱ�עΪn
			    	weight[dictionary.translateCX("nr")][i]=0;
			    	int x;
			    	double max=Math.log(0);
			    	int recx=-1;
			    	for(x=0;x<CiXing_type;x++)
			    	{
			    		if(weight[x][i-1]>max)
			    		{
			    			max=weight[x][i-1];
			    			recx=x;
			    			ischanged=true;
			    		}
			    	}
			    	path[dictionary.translateCX("n")][i]=recx;//�������һ��ѡ��pro����
			    	for(x=0;x<CiXing_type;x++)
			    	{
			    		if(weight[x][i-1]>max)
			    		{
			    			max=weight[x][i-1];
			    			ischanged=true;
			    			recx=x;
			    		}
			    	}
			    	path[dictionary.translateCX("nr")][i]=recx;//�������һ��ѡ��pro����
		        	if(compromisecounterswitch)//ͳ��compromised sentence������
		        	{
		        		compromisednum++;
		        		compromisecounterswitch=false;
		        	}
		        }
			    if(ischanged==false)
			    {
			    	System.out.println("HMMת�ƾ�������ȱʧ��������������Ͽ�ѵ��");
			    	System.out.println(sen);
			    	System.out.println(i);
			    }
			}
			//���ݣ�
			//a3= System.currentTimeMillis(); 
			StringBuilder resultStringbuf=new StringBuilder(1000);
			int thiswordtype=-1;
			double max=Math.log(0);
			for(i=0;i<CiXing_type;i++)
			{
				if(weight[i][size-1]>max)
				{
					max=weight[i][size-1];
					thiswordtype=i;
				}
			}
			for(int x=size-1;x>=0;x--)//thiswordtype�������
			{
				String tString=resultStringbuf.toString();
				resultStringbuf.setLength(0);
				resultStringbuf.append((char)(thiswordtype+65)+tString);
				thiswordtype=path[thiswordtype][x];
			}
			//a4= System.currentTimeMillis(); 
			//System.out.println("����vector��ʱ��" + (a2 - a1));
			//System.out.println("������ʱ��"+(a3-a2));
			//System.out.println("������ʱ:" + (a4-a3));
			return resultStringbuf.toString();
		}
		public void showfunctions()//����չʾ
		{
			HMManalyze();
		}
		private void HMManalyze()//HMM�ܿ��
		{
			myIOforHMM.startRead(testQTxtSrcString, encodingString, 0);
			myIOforHMM.startRead(testATxtSrcString, encodingString, 1);
			StringBuilder saveBuffer=new StringBuilder(1000);
			String tmpstoreString;
			String qString="",aString="";
			while((qString=myIOforHMM.readOneSentence(0))!=null)
			{
				sentencenum++;
				if(qString.length()==0) continue;
				//a1= System.currentTimeMillis(); 
				tmpstoreString=onSentenceAnalyzeByHMM(qString);
				//a2= System.currentTimeMillis();
				//System.out.println("osa��ʱ��"+(a2-a1));
				aString=myIOforHMM.readOneSentence(1);
				saveBuffer.append(tmpstoreString+"\r\n");
				myEvaluation.oneSentenceMatch(tmpstoreString, aString);
				//a3= System.currentTimeMillis(); 
				//System.out.println("osm��ʱ��"+(a3-a2));
			}
			myIOforHMM.startWrite(resultsavepathString+"byHMM"+filetype,encodingString,0);
			myIOforHMM.writechars(saveBuffer.toString().toCharArray(),0);
			myIOforHMM.endWrite(0);
			myIOforHMM.endRead(0);
			myIOforHMM.endRead(1);
			System.out.println("HMM���Ա�ע");
			myEvaluation.cal_Evaluation();
			myEvaluation.printEvaluation();
			System.out.println(compromisednum+" sentences  compromised duo to corpus which is too simple");
			System.out.println("total sentences:"+sentencenum);
		}
		public void match(String apath,String expapath)//�ȶ���������ļ�����һ�������ǽ��·�����ڶ��������Ǵ�·��
		{
			myIOforHMM.startRead(apath, encodingString, 0);
			myIOforHMM.startRead(expapath, encodingString, 1);
			String a,expa;
			while((a=myIOforHMM.readOneSentence(0))!=null)
			{
				expa=myIOforHMM.readOneSentence(1);
				myEvaluation.oneSentenceMatch(a, expa);
			}
			myEvaluation.cal_Evaluation();
			myEvaluation.printEvaluation();
			System.out.println(myEvaluation.resultnum);
			System.out.println(myEvaluation.rightnum);
		}
		public void HMMcal()
		{
			myIOforHMM.startRead(testQTxtSrcString, encodingString, 0);
			StringBuilder saveBuffer=new StringBuilder("");
			String tmpstoreString;
			String qString="";
			while((qString=myIOforHMM.readOneSentence(0))!=null)
			{
				sentencenum++;
				if(qString.length()==0) continue;
				tmpstoreString=onSentenceAnalyzeByHMM(qString);
				saveBuffer.append(tmpstoreString+"\r\n");
			}
			System.out.println("HMM���Ա�ע���");
			System.out.println(compromisednum+" sentences  compromised duo to corpus which is too simple");
			System.out.println("total sentences:"+sentencenum);
			myIOforHMM.startWrite(resultsavepathString+"calrstbyHMM"+filetype,encodingString,0);
			myIOforHMM.writechars(saveBuffer.toString().toCharArray(),0);
			myIOforHMM.endWrite(0);
			myIOforHMM.endRead(0);
			
		}
}
