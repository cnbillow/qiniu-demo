package com.qiniu.Demo;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

import org.json.JSONException;

import com.qiniu.api.auth.AuthException;
import com.qiniu.api.auth.digest.Mac;
import com.qiniu.api.io.IoApi;
import com.qiniu.api.io.PutExtra;
import com.qiniu.api.io.PutRet;
import com.qiniu.api.rs.PutPolicy;


public class QiNiuUpload {

	//设置好账号的ACCESS_KEY和SECRET_KEY
	private static String ACCESS_KEY;
	private static String SECRET_KEY;
	//要上传的空间
	private static String BUCKETNAME;
	//上传文件的路径
	String FilePath = "C:Users\\Administrator\\Desktop\\image\\5.jpg";
	
	static {
		//加载七牛 配置
		Properties properties = new Properties();  
        try {
			properties.load(QiNiuUpload.class.getResourceAsStream("/qiniu.properties"));
			ACCESS_KEY = properties.getProperty("qiniu.ak");  
			SECRET_KEY = properties.getProperty("qiniu.sk");  
			BUCKETNAME = properties.getProperty("qiniu.bucket");  
		} catch (IOException e) {
			e.printStackTrace();
		}  
	}
	
	//生成上传toKen
	public String getUpToken(){
		
		Mac mac = new Mac(ACCESS_KEY, SECRET_KEY);

		PutPolicy putPolicy = new PutPolicy(BUCKETNAME);
		// 设置返回值
		String returnBody = "{key: $(key), name:$(fname), size: $(fsize), w: $(imageInfo.width),"
		        + "h: $(imageInfo.height), hash: $(etag), suffix:$(suffix), imageAve:$(imageAve) }";
		
		putPolicy.returnBody = returnBody;
		
		String uptoken;
		try {
			uptoken = putPolicy.token(mac);
			return uptoken;
		} catch (AuthException e) {
			e.printStackTrace();
		} catch (JSONException e) {
			e.printStackTrace();
		}
		return null;
	}

	  
	public void upload() throws IOException{
		File f = new File(FilePath);  
	    InputStream inputStream = new FileInputStream(f);   
		PutExtra extra = new PutExtra();
		String uptoken = getUpToken();
		PutRet ret = IoApi.Put(uptoken, null, inputStream, extra);     
		System.out.println(ret.getResponse());
	}
	
	public static void main(String[] args) {
		try {
			new QiNiuUpload().upload();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
}
