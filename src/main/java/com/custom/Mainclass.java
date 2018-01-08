package main.java.com.custom;

public class Mainclass {
	final static String QHMM="E:\\QHMMCX.txt";
	final static String AHMM="E:\\AHMMCX.txt";
	public static void main(String [] args)
	{
		long startTime;
		long endTime;
		startTime= System.currentTimeMillis();    //��ȡ��ʼʱ��
		HMMdictionaryforCX myHMMdictionary=new HMMdictionaryforCX("E:\\corpus.txt", "E:\\HMMCiXingDIC.txt", "GBK", "E:\\HMMcxINI.txt");
		endTime= System.currentTimeMillis();    //��ȡ����ʱ��
		System.out.println("�����ʵ���ʱ��" + (endTime - startTime) + "ms");
		if(!(IOforHMM.isFileExist(QHMM)&&IOforHMM.isFileExist(AHMM)))
		{
			startTime= System.currentTimeMillis();    //��ȡ��ʼʱ��
			myHMMdictionary.corpusYCL("E:\\corpusfortest.txt",QHMM,AHMM,"GBK");
			endTime= System.currentTimeMillis();    //��ȡ����ʱ��
			System.out.println("��������������ʱ��" + (endTime - startTime) + "ms");
		}
		startTime= System.currentTimeMillis();    //��ȡ��ʼʱ��
		HMMforCX myHmm=new HMMforCX(QHMM, AHMM, "GBK", myHMMdictionary, "E:\\CXResult", ".txt");
		myHmm.showfunctions();
		endTime= System.currentTimeMillis();    //��ȡ����ʱ��
		System.out.println("���Ա�ע��ʱ��" + (endTime - startTime) + "ms");
	}
}
