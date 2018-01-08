package main.java.com.custom;

import java.util.HashMap;
import java.util.Iterator;




class rtvalofComplexWordHandler{//����complexwordHandler���ظ��Ͻ��
	int end;
	int lastwordCX;
	public rtvalofComplexWordHandler(int e,int lst)
	{
		end=e;
		lastwordCX=lst;
	}
}
class probabitity{//Ƶ�ʣ����ڴ洢EmitProbMatrix
	double counter;
	double pro;
	public probabitity(double cnt,double p)
	{
		counter=cnt;
		pro=p;
	}
	public probabitity()
	{
		counter=1;
		pro=0;
	}
	public void inccounter()
	{
		counter+=1;
	}
	public void calpro(double total)
	{
		pro=Math.log(counter/total);
	}
}

public class HMMdictionaryforCX {
	//substring(23);
	private int CiXing_type=100;//�������࣬��ʼ��Ϊ100
	private double TransProbMatrix[][],InitStatus[];
	private String OriginalfilePath;
	private String dictionaryPath;
	private String encodingString;
	private String cxiniPath;//���������ļ�·��
	private int allsentencenum;//�ܾ�����
	private int alltransnum;//ת������
	private HashMap hashmaps[];
	private IOforHMM myIOforHMMdic;
	private HashMap cxmap,allwordMap;
	private int cxnum;
	private int idforN;//n��id
	private int idforNR;//nr��id
	boolean isCXiniExist;
	
	public HMMdictionaryforCX(String orgpath,String dicpath,String encoding,String cxpath)//����
	{
		myIOforHMMdic=new IOforHMM(2);
		OriginalfilePath=orgpath;
		dictionaryPath=dicpath;
		cxiniPath=cxpath;
		encodingString=encoding;
		allsentencenum=0;
		alltransnum=0;
		cxnum=0;
		cxmap=new HashMap();
		allwordMap=new HashMap();
		if(IOforHMM.isFileExist(cxiniPath))//���������ļ�
		{
			buildCXini();
			isCXiniExist=true;
			//saveCXini("E:\\HMMcxINITEST.txt");���Դ���
		}
		else {
			isCXiniExist=false;
		}
		TransProbMatrix=new double[CiXing_type][CiXing_type];
		InitStatus=new double[CiXing_type];
		int i,j;
		hashmaps=new HashMap[CiXing_type];
		for(i=0;i<CiXing_type;i++)
		{
			for(j=0;j<CiXing_type;j++)
			{
				TransProbMatrix[i][j]=0;
			}
			InitStatus[i]=0;
			hashmaps[i]=new HashMap();
		}
		if(isdicExist()&&isCXiniExist)
		{
			buildbyDicfile();
			//saveDic("E:\\HMMdicTEST.txt");���Դ���
		}
		else {
			buildbyOrgfile();
		}
		if(isCXiniExist==false)
		{
			saveCXini(cxiniPath);
			isCXiniExist=true;
		}
		idforN=translateCX("n");
		idforNR=translateCX("nr");
	}
	private void buildCXini()//��������������Ϣ
	{
		myIOforHMMdic.startRead(cxiniPath, encodingString, 0);
		String s;
		CiXing_type=Integer.valueOf(myIOforHMMdic.readOneSentence(0)).intValue();
		while((s=myIOforHMMdic.readOneSentence(0))!=null)
		{
			cxmap.put(s, new Integer(myIOforHMMdic.readOneSentence(0)));
		}
		myIOforHMMdic.endRead(0);
	}
	private void saveCXini(String path)//�������������Ϣ
	{
		myIOforHMMdic.startWrite(path, encodingString,1);
		myIOforHMMdic.writeOneString(CiXing_type+"\r\n", 1);//д������
		Iterator iter = cxmap.entrySet().iterator();
		while (iter.hasNext()) {
			HashMap.Entry entry = (HashMap.Entry) iter.next();
			String key = (String)entry.getKey();
			Integer val = (Integer)entry.getValue();
			myIOforHMMdic.writeOneString(key+"\r\n",1);
			myIOforHMMdic.writeOneString(val.intValue()+"\r\n",1);
		}
		myIOforHMMdic.endWrite(1);
	}
	private void buildbyDicfile()//ͨ���ʵ��ļ������ʵ�
	{
		myIOforHMMdic.startRead(dictionaryPath, encodingString, 0);
		int i,j;
		String s;
		for(i=0;i<CiXing_type;i++)
		{
			s=myIOforHMMdic.readOneSentence(0);
			InitStatus[i]=Double.valueOf(s).doubleValue();
			for(j=0;j<CiXing_type;j++)
			{
				s=myIOforHMMdic.readOneSentence(0);
				TransProbMatrix[i][j]=Double.valueOf(s).doubleValue();
			}
		}
		for(i=0;i<CiXing_type;i++)
		{
			int size=Integer.valueOf(myIOforHMMdic.readOneSentence(0)).intValue();
			for(j=0;j<size;j++)
			{
				s=myIOforHMMdic.readOneSentence(0);
				hashmaps[i].put(s, new probabitity(0, Double.valueOf(myIOforHMMdic.readOneSentence(0)).doubleValue()));
				allwordMap.put(s, null);
			}
		}
		myIOforHMMdic.endRead(0);
		System.out.println("�ʵ佨�����");
	}
	public String oneLineProcessForStandard(String orgString)//��������ԭʼ���ϵ�һ�д����Ϊ��׼��ʽ����ͬԭʼ����ֻ����д�˺���
	//��׼��ʽ���£�[�й�/ns  ����/n]nt  ��/d  ����/v  ���/v  ����/v  ��������/i  ��/u  ��ƽ/n  �⽻/n  ����/n  ��/w  ��/p  ��ƽ����/l  ��/m  ��/q  ԭ��/n  ��/u  ����/n  ��/f  Ŭ��/ad  ��չ/v  ͬ/p  ����/n  ����/r  ��/u  �Ѻ�/a  ��ϵ/n  ��/w  �й�/ns  Ը��/v  ��ǿ/v  ͬ/p  ���Ϲ�/nt  ��/c  ����/r  ����/n  ��֯/n  ��/u  Э��/vn  ��/w  �ٽ�/v  ��/p  ����/v  ��ó/j  �Ƽ�/n  ����/vn  ��/w  ����/v  ����/n  ��/w  ����/v  ƶ��/an  ��/w  ���/v  ����/n  ����/vn  ��/u  ����/n  ��/u  ����/n  ����/vn  ��/w  �й�/ns  ��Զ/d  ��/v  ά��/v  ����/n  ��ƽ/n  ��/c  �ȶ�/an  ��/u  ��Ҫ/a  ����/n  ��/w  �й�/ns  ����/n  Ը/v  ��/p  ����/n  ����/r  ����/n  һ��/d  ��/w  Ϊ/p  ����/v  �־�/a  ��ƽ/n  ��/w  ��ͬ/d  ��չ/v  ��/u  ��/a  ����/n  ��/c  ��иŬ��/l  ��/w 
	{
		return orgString.substring(23);
	}
	private void buildbyOrgfile()//�������Ͽ������ֵ䣨��ܣ�
	{
		myIOforHMMdic.startRead(OriginalfilePath, encodingString, 0);
		String tmpLineValString=null;
		while ((tmpLineValString=myIOforHMMdic.readOneSentence(0))!=null) {
			allsentencenum++;
			if(tmpLineValString.length()==0) //�޳�����
			{
				continue;
			}
			onesentenceAnalyze(oneLineProcessForStandard(tmpLineValString));//�˴�Ϊ�޳�19980101-01-001-001/m  ���ã��������Ͽ�����д
		}
		int i,j;
		for(i=0;i<CiXing_type;i++)
		{
			InitStatus[i]=Math.log((double)InitStatus[i]/(double)allsentencenum);//����InitStatus
			calproofEmitRobMatrix(hashmaps[i]);//����EmitRobMatrix
		}
		for(i=0;i<CiXing_type;i++)
		{
			for(j=0;j<CiXing_type;j++)
			{
				alltransnum+=TransProbMatrix[i][j];//����TransProbMatrix
			}
		}
		for(i=0;i<CiXing_type;i++)
		{
			for(j=0;j<CiXing_type;j++)
			{
				TransProbMatrix[i][j]=Math.log(TransProbMatrix[i][j]/alltransnum);//����TransProbMatrix
			}
		}
		myIOforHMMdic.endRead(0);
		CiXing_type=cxmap.size();//���´�������
		saveDic(dictionaryPath);
		System.out.println("�ʵ佨�����");
	}
	public int translateCX(String a)//�������,���ض�Ӧ���
	{
		if(isCXiniExist)
		{
			Integer tmpInteger;
			tmpInteger=(Integer)cxmap.get(a);
			return tmpInteger.intValue();
		}
		else {
			if(cxmap.containsKey(a))
			{
				Integer tmpInteger;
				tmpInteger=(Integer)cxmap.get(a);
				return tmpInteger.intValue();
			}
			else {
				Integer tmpInteger=new Integer(cxnum);
				cxmap.put(a, tmpInteger);
				cxnum++;
				return cxnum-1;
			}
		}
	}
	private rtvalofComplexWordHandler complexWordHandler(String tmpLineVal,int i,int len,int lastwordCX)//�Ը��ϴʵĴ������ڽ����ʵ䡣���磺[����/n  ����/n  �㲥/vn  ��̨/n]nt
	{
		rtvalofComplexWordHandler rt;
		int start=i+1;
		for(;i<len;i++)
		{
			if(tmpLineVal.substring(i,i+1).equals("]"))
				break;
		}
		int reci=i;//��¼������λ��
		for(;i<len;i++)
		{
			if(tmpLineVal.substring(i, i+1).equalsIgnoreCase(" "))
				break;
		}
		int bigwordcixing=translateCX(tmpLineVal.substring(reci+1,i));//���ϴʵĴ���
		String complexwordString=tmpLineVal.substring(start,reci);//�����ĸ��ϴʣ��������Ժ�׺
		int end=i;
		while(end<len&&tmpLineVal.charAt(end)==' ')//ʹendָ����һ���ʵĿ�ͷ
		{
			end++;
		}
		String bigwordString="";
		int complexwordlength=complexwordString.length();
		String s="";
		int lastcx=-1;//��¼���ϴ��и��ɷִ���һ���ɷִʵĴ���
		for(i=0;i<complexwordlength;)
		{
			while(i<complexwordlength&&complexwordString.charAt(i)!=' ')//��ȡ������
			{
				s+=complexwordString.charAt(i);
				i++;
			}
			int rec=0;
			while(rec<s.length()&&!s.substring(rec,rec+1).equalsIgnoreCase("/"))
			{
				rec++;
			}
			int cx=translateCX(s.substring(rec+1));//���ϴ���ĳ�����ʴ���
			s=s.substring(0,rec);//��ȥ���Ժ�׺
			if(lastcx!=-1)
			{
				TransProbMatrix[lastcx][cx]++;
			}
			lastcx=cx;
			if(hashmaps[cx].containsKey(s))//�Ѿ���������Ƶ+1
			{
				probabitity tmppro;
				tmppro=(probabitity) hashmaps[cx].get(s);
				tmppro.inccounter();
			}
			else {//�״γ��֣���Ƶ��1
				hashmaps[cx].put(s, new probabitity(1,0));
				allwordMap.put(s, null);
			}
			bigwordString+=s;
			if(s.length()==0) System.out.println("Wrong in:"+tmpLineVal);//������Ϣ
			s="";
			while(i<complexwordlength&&complexwordString.charAt(i)==' ')
			{
				i++;
			}
		}
		//�����ϴʼ���
		if(bigwordString.length()==0) System.out.println("Wrong in:"+tmpLineVal);//������Ϣ
		//System.out.println("XXX"+bigwordString);
		if(hashmaps[bigwordcixing].containsKey(bigwordString))//�Ѿ���������Ƶ+1
		{
			probabitity tmppro;
			tmppro=(probabitity) hashmaps[bigwordcixing].get(bigwordString);
			tmppro.inccounter();
		}
		else {//�״γ��֣���Ƶ��1
			hashmaps[bigwordcixing].put(bigwordString, new probabitity(1,0));
			allwordMap.put(bigwordString, null);
		}
		//���´���ת�ƾ���
		if(lastwordCX!=-1)
		{
			TransProbMatrix[lastwordCX][bigwordcixing]++;
		}
		else {//=-1˵�����״�
			InitStatus[bigwordcixing]++;
		}
		rt=new rtvalofComplexWordHandler(end, bigwordcixing);
		if(end<len&&tmpLineVal.charAt(end)=='[')
		{
			rt=complexWordHandler(tmpLineVal, end, len,bigwordcixing);
		}
		return rt;
	}
	private void onesentenceAnalyze(String tmpLineVal)//������������ڽ����ʵ�
	{
		int len=tmpLineVal.length();
		int i;
		String s="";
		int lastCX=-1;
		rtvalofComplexWordHandler rt;
		for(i=0;i<len;)
		{
			if(tmpLineVal.substring(i,i+1).equals("["))//���ϴʴ���
			{
				rt=complexWordHandler(tmpLineVal, i, len,lastCX);
				i=rt.end;
				lastCX=rt.lastwordCX;
				if(i>=len) break;
			}
			while(i<len&&tmpLineVal.charAt(i)!=' ')
			{
				s+=tmpLineVal.charAt(i);
				i++;
			}
			int rec=0;
			while(rec<s.length()&&!s.substring(rec,rec+1).equalsIgnoreCase("/"))
			{
				rec++;
			}
			int cx=translateCX(s.substring(rec+1));//��ȡ����
			s=s.substring(0,rec);//��ȥ���Ժ�׺
			
			//���´���ת�ƾ��󣬼�lastcx��
			if(lastCX!=-1)
			{
				TransProbMatrix[lastCX][cx]++;
			}
			else {
				InitStatus[cx]++;
			}
			lastCX=cx;
			//���ʲ���EmitProbMatrix
			if(hashmaps[cx].containsKey(s))//�Ѿ���������Ƶ+1
			{
				probabitity tmppro;
				tmppro=(probabitity) hashmaps[cx].get(s);
				tmppro.inccounter();
			}
			else {//�״γ��֣���Ƶ��1
				hashmaps[cx].put(s, new probabitity(1, 0));
				allwordMap.put(s, null);
			}
			if(s.length()==0) System.out.println(tmpLineVal);//������Ϣ
			//�����ÿ�
			s="";
			//ָ���ƶ��ҵ���һ���ʵ���ʼ����
			while(i<len&&tmpLineVal.charAt(i)==' ')
			{
				i++;
			}
		}
				
	}
	private void calproofEmitRobMatrix(HashMap map)//����EmitRobMatrix��ֵ������ȡln��
	{
		int size=map.size();
		Iterator iter = map.entrySet().iterator();
		while (iter.hasNext()) {
			HashMap.Entry entry = (HashMap.Entry) iter.next();
			probabitity tmpp = (probabitity)entry.getValue();
			tmpp.calpro(size);
		}
	}
	public double getEmitRobMatrixValue(int type,String word)//��ȡEmitRobMatrixֵ
	{
		/*if(iswordExist(word)==false&&(type==translateCX("n")||type==translateCX("nr"))) 
		{
			//System.out.println("δ��¼�ʣ�"+word);
			return 0;//�����δ��¼�Ĵʣ��²���������
		}*/
		if(!iswordExist(word)&&(type==idforN||type==idforNR))
		{
			//System.out.println("δ��¼�ʣ�"+word);
			return 0;//�����δ��¼�Ĵʣ��²���������
		}
		probabitity pro=(probabitity)hashmaps[type].get(word);
		if(pro==null) {return Math.log(0);}
		else {return pro.pro;}
	}
	public double getInitStatusValue(int type)//��ȡInitStatusValue
	{
		return InitStatus[type];
	}
	public double getTransProbMatrixValue(int i,int j)//��ȡTransProbMatrixValue
	{
		return TransProbMatrix[i][j];
	}
	public boolean isdicExist()//�жϴʵ��ļ��Ƿ����
	{
		return IOforHMM.isFileExist(dictionaryPath);
	}
	public boolean iswordExist(String word)//�ж�word�Ƿ���δ��¼��
	{
		/*boolean rstVal=false;
		int i;
		for(i=0;i<CiXing_type;i++)
		{
			rstVal=rstVal||hashmaps[i].containsKey(word);
		}
		return rstVal;*/
		return allwordMap.containsKey(word);
	}
	private void saveDic(String path)//�洢�ʵ�
	{
		int i,j;
		myIOforHMMdic.startWrite(path, encodingString, 0);
		for(i=0;i<CiXing_type;i++)
		{
			myIOforHMMdic.writeOneString(InitStatus[i]+"\r\n", 0);
			for(j=0;j<CiXing_type;j++)
			{
				myIOforHMMdic.writeOneString(TransProbMatrix[i][j]+"\r\n", 0);
			}
		}
		for(i=0;i<CiXing_type;i++)
		{
			Iterator iter = hashmaps[i].entrySet().iterator();
			myIOforHMMdic.writeOneString(hashmaps[i].size()+"\r\n", 0);//д������
			while (iter.hasNext()) {
				HashMap.Entry entry = (HashMap.Entry) iter.next();
				String key = (String)entry.getKey();
				probabitity val = (probabitity)entry.getValue();
				myIOforHMMdic.writeOneString(key+"\r\n",0);
				myIOforHMMdic.writeOneString(val.pro+"\r\n",0);
			}
		}
		myIOforHMMdic.endWrite(0);
	}
	public void corpusYCL(String path,String QHMMpath,String AHMMpath,String enString)//����Ԥ��������corpus������������
	{
		myIOforHMMdic.startWrite(QHMMpath, encodingString, 0);
		myIOforHMMdic.startWrite(AHMMpath, enString, 1);
		myIOforHMMdic.startRead(path, encodingString, 0);
		String sen;
		StringBuffer bufQ,bufA;
		bufQ=new StringBuffer();
		bufA=new StringBuffer();
		while((sen=myIOforHMMdic.readOneSentence(0))!=null)
		{
			if(sen.length()==0) continue;
			try {
				oneSentenceYCL(oneLineProcessForStandard(sen),bufQ,bufA);
			} catch (java.lang.StringIndexOutOfBoundsException e) {
				// TODO: handle exception
				System.out.println(sen);
				System.exit(1);
			}
			
			myIOforHMMdic.writeStringBufferIntoTXT(bufQ, 0);
			myIOforHMMdic.writechars(bufA.toString().toCharArray(), 1);
			bufQ.setLength(0);
			bufA.setLength(0);
		}
		myIOforHMMdic.endRead(0);
		myIOforHMMdic.endWrite(0);
		myIOforHMMdic.endWrite(1);
	}
	private int complexWordYCL(String tmpLineVal, int i, int len,StringBuffer bufQ,StringBuffer bufA)//���ϴʵ�Ԥ����
	{
		int start=i+1;
		for(;i<len;i++)
		{
			if(tmpLineVal.substring(i,i+1).equals("]"))
				break;
		}
		int reci=i;//��¼������λ��
		for(;i<len;i++)
		{
			if(tmpLineVal.substring(i, i+1).equalsIgnoreCase(" "))
				break;
		}
		int bigwordcixing=translateCX(tmpLineVal.substring(reci+1,i));//���ϴʵĴ���
		String complexwordString=tmpLineVal.substring(start,reci);//[]֮�ڵ�����
		int end=i;
		while(end<len&&tmpLineVal.charAt(end)==' ')//ʹendָ����һ���ʵĿ�ͷ
		{
			end++;
		}
		String bigwordString="";
		int complexwordlength=complexwordString.length();
		String s="";
		for(i=0;i<complexwordlength;)
		{
			while(i<complexwordlength&&complexwordString.charAt(i)!=' ')//��ȡ������
			{
				s+=complexwordString.charAt(i);
				i++;
			}
			int rec=0;
			while(rec<s.length()&&!s.substring(rec,rec+1).equalsIgnoreCase("/"))
			{
				rec++;
			}
			s=s.substring(0,rec);//��ȥ���Ժ�׺
			bigwordString+=s;//ƴ�ӳɸ��ϴ�
			if(s.length()==0) System.out.println("Wrong in:"+tmpLineVal);//������Ϣ
			s="";
			while(i<complexwordlength&&complexwordString.charAt(i)==' ')
			{
				i++;
			}
		}
		if(bigwordString.length()==0) System.out.println("Wrong in:"+tmpLineVal);//������Ϣ
		bufQ.append(bigwordString+"  ");
		bufA.append((char)(bigwordcixing+65));
		if(end<len&&tmpLineVal.charAt(end)=='[')
		{
			end=complexWordYCL(tmpLineVal, end, len,bufQ,bufA);
		}
		return end;
	}
	private void oneSentenceYCL(String tmpLineVal,StringBuffer bufQ,StringBuffer bufA)//����Ԥ����
	{
		int len=tmpLineVal.length();
		int i;
		String s="";
		for(i=0;i<len;)
		{
			if(tmpLineVal.substring(i,i+1).equals("["))//���ϴʴ���
			{
				i=complexWordYCL(tmpLineVal, i, len,bufQ,bufA);
				if(i>=len) break;
			}
			while(i<len&&tmpLineVal.charAt(i)!=' ')
			{
				s+=tmpLineVal.charAt(i);
				i++;
			}
			int rec=0;
			while(rec<s.length()&&!s.substring(rec,rec+1).equalsIgnoreCase("/"))
			{
				rec++;
			}
			int cx=translateCX(s.substring(rec+1));//��ȡ����
			s=s.substring(0,rec);//��ȥ���Ժ�׺
			bufA.append((char)(cx+65));
			bufQ.append(s+"  ");
			//�����ÿ�
			s="";
			//ָ���ƶ��ҵ���һ���ʵ���ʼ����
			while(i<len&&tmpLineVal.charAt(i)==' ')
			{
				i++;
			}
		}
		String tmp=bufQ.substring(0,bufQ.length()-2);//��ȥ��������ո�
		bufQ.setLength(0);
		bufQ.append(tmp);
		bufQ.append("\r\n");
		bufA.append("\r\n");
	}
	public int getCXtypeNum()//��ȡ��������
	{
		return CiXing_type;
	}
}
