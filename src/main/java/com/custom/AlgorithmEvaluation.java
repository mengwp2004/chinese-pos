package main.java.com.custom;

public class AlgorithmEvaluation {
	float rightRate;//��ȷ��
	float callbackRate;//�ٻ��ʣ�����
	int rightnum;//��ȷ��
	int answernum;//���з���������
	int resultnum;//�ܴ���
	public AlgorithmEvaluation()
	{
		rightRate=0;
		callbackRate=0;
		rightnum=0;
		answernum=0;
		resultnum=0;
	}
	public void cal_Evaluation()//������ȷ��
	{
		rightRate=(float)rightnum/(float)resultnum;
	}
	public void printEvaluation()//�����ȷ��
	{
		System.out.println("��ȷ�ʣ�"+rightRate);
	}
 	public void oneSentenceMatch(int a[],int expa[])//һ�����ӵ�������֤�����Ӵ���int[]�У�����Ŀ���Ѳ��ã��������ýӿ�
	{
		int len=a.length;
		int i;
		if(a.length!=expa.length||a.length==0)
		{
			System.out.println("���ȴ���");
		}
		for(i=0;i<len;i++)
		{
			if(a[i]==expa[i])
			{
				rightnum++;
			}
			resultnum++;
		}
	}
 	public void oneSentenceMatch(String a,String expa)//����ƥ�䣬aΪ�����expaΪ��
 	{
 		int len=a.length();
 		int i;
 		for(i=0;i<len;i++)
 		{
 			if(a.substring(i,i+1).equals(expa.substring(i,i+1))==true)
 				rightnum++;
 			resultnum++;
 		}
 	}

}
