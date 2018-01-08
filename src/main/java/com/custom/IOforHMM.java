package main.java.com.custom;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.UnsupportedEncodingException;

public class IOforHMM {
	private InputStreamReader read[];
	private BufferedReader bufread[];
	private OutputStreamWriter writer[];
	private BufferedWriter bufwriter[];
	public IOforHMM(int MAXStreamnum)
	{
		read=new InputStreamReader[MAXStreamnum];
		bufread=new BufferedReader[MAXStreamnum];
		writer=new OutputStreamWriter[MAXStreamnum];
		bufwriter=new BufferedWriter[MAXStreamnum];
		int i;
		for(i=0;i<MAXStreamnum;i++)
		{
			read[i]=null;
			bufread[i]=null;
			writer[i]=null;
			bufwriter[i]=null;
		}
	}
	public void startRead(String filepath,String encodingString,int Readernum)
	{
		try {
			read[Readernum] = new InputStreamReader(new FileInputStream(filepath), encodingString);
			bufread[Readernum] = new BufferedReader(read[Readernum]);
		} catch (UnsupportedEncodingException e) {
			// TODO �Զ����ɵ� catch ��
			e.printStackTrace();
		} catch (FileNotFoundException e) {
			// TODO �Զ����ɵ� catch ��
			e.printStackTrace();
		}
		
	}
	public void endRead(int Readernum)
	{
		if(bufread!=null)
			try {
				bufread[Readernum].close();
				bufread[Readernum]=null;
			} catch (IOException e) {
				// TODO �Զ����ɵ� catch ��
				e.printStackTrace();
			}
		if(read!=null)
			try {
				read[Readernum].close();
				read[Readernum]=null;
			} catch (IOException e) {
				// TODO �Զ����ɵ� catch ��
				e.printStackTrace();
			}
	}
	public void startWrite(String filepath,String encodingString,int Writernum)
	{
		try {
			writer[Writernum]=new OutputStreamWriter(new FileOutputStream(filepath),encodingString);
			bufwriter[Writernum]=new BufferedWriter(writer[Writernum]);
		} catch (UnsupportedEncodingException e) {
			// TODO �Զ����ɵ� catch ��
			e.printStackTrace();
		} catch (FileNotFoundException e) {
			// TODO �Զ����ɵ� catch ��
			e.printStackTrace();
		}
	}
	public void endWrite(int Writernum)
	{
		try {
			if(bufwriter!=null)
			{
				bufwriter[Writernum].close();
				bufwriter[Writernum]=null;
			}
			if(writer!=null)
			{
				writer[Writernum].close();
				writer[Writernum]=null;
			}
			
		} catch (IOException e) {
			// TODO �Զ����ɵ� catch ��
			e.printStackTrace();
		}
		
	}
	public String readOneSentence(int Readernum)
	{
		try {
			return bufread[Readernum].readLine();
		} catch (IOException e) {
			// TODO �Զ����ɵ� catch ��
			System.out.println("�ļ�δ��ȷ�򿪣���ȡ����");
			e.printStackTrace();
			return null;
		}
	}
	public void writeOneString(String s,int Writernum)
	{
		try {
			bufwriter[Writernum].write(s);
		} catch (IOException e) {
			// TODO �Զ����ɵ� catch ��
			e.printStackTrace();
		}
	}
	public void writeStringBufferIntoTXT(StringBuffer buf,int Writernum)//��StringBufferд��txt
	{
		
			try {
				bufwriter[Writernum].write(buf.toString());
			} catch (IOException e) {
				// TODO �Զ����ɵ� catch ��
				e.printStackTrace();
			}
	}
	public static boolean isFileExist(String path)
	{
		File file=new File(path);
		return file.exists();
	}
	public int readint(int readernum)
	{
		int rst=-1;
		try {
			rst=bufread[readernum].read();
		} catch (IOException e) {
			// TODO �Զ����ɵ� catch ��
			e.printStackTrace();
		}
		return rst;
	}
	public void writechars(char []a,int writernum)
 	{
 		try {
			bufwriter[writernum].write(a);
		} catch (IOException e) {
			// TODO �Զ����ɵ� catch ��
			e.printStackTrace();
			System.out.println("Wrong in IOforHMM.writechars");
		}
 	}
}
