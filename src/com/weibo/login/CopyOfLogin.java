package com.weibo.login;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;

import javax.script.Invocable;
import javax.script.ScriptEngine;
import javax.script.ScriptEngineManager;

import org.apache.commons.codec.binary.Base64;
import org.apache.commons.io.IOUtils;
import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.params.ClientPNames;
import org.apache.http.conn.ClientConnectionManager;
import org.apache.http.conn.scheme.PlainSocketFactory;
import org.apache.http.conn.scheme.Scheme;
import org.apache.http.conn.scheme.SchemeRegistry;
import org.apache.http.cookie.ClientCookie;
import org.apache.http.cookie.Cookie;
import org.apache.http.cookie.CookieOrigin;
import org.apache.http.cookie.CookieSpec;
import org.apache.http.cookie.CookieSpecFactory;
import org.apache.http.cookie.MalformedCookieException;
import org.apache.http.cookie.SetCookie;
import org.apache.http.impl.client.AbstractHttpClient;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.impl.conn.PoolingClientConnectionManager;
import org.apache.http.impl.cookie.BasicExpiresHandler;
import org.apache.http.impl.cookie.BrowserCompatSpec;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.params.HttpParams;
import org.apache.http.protocol.HTTP;
import org.apache.http.util.EntityUtils;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.select.Elements;

import com.weibo.common.FileProcess;

public class CopyOfLogin {

	/**
	 * @param args
	 * @throws IOException
	 */
	public static void main(String[] args) throws IOException {
		HttpClient client = CopyOfLogin.login();
	}

	private static HttpClient client;

	final static String[] DATE_PATTERNS = new String[] {
			"EEE, dd MMM yyyy HH:mm:ss", "EEE, dd MMM yyyy HH:mm:ss zzz",
			"EEE, dd-MMM-yy HH:mm:ss zzz", "EEE MMM d HH:mm:ss yyyy",
			"EEE, dd-MMM-yyyy HH:mm:ss z", "EEE, dd-MMM-yyyy HH-mm-ss z",
			"EEE, dd MMM yy HH:mm:ss z", "EEE dd-MMM-yyyy HH:mm:ss z",
			"EEE dd MMM yyyy HH:mm:ss z", "EEE dd-MMM-yyyy HH-mm-ss z",
			"EEE dd-MMM-yy HH:mm:ss z", "EEE dd MMM yy HH:mm:ss z",
			"EEE,dd-MMM-yy HH:mm:ss z", "EEE,dd-MMM-yyyy HH:mm:ss z",
			"EEE, dd-MM-yyyy HH:mm:ss z", "E, dd-MMM-yyyy HH:mm:ss zzz",
			"EEEE, dd-MMM-yy HH:mm:ss zzz" };
	static {
		SchemeRegistry schemeRegistry = new SchemeRegistry();
		schemeRegistry.register(new Scheme("http", 80, PlainSocketFactory
				.getSocketFactory()));
		ClientConnectionManager cm = new PoolingClientConnectionManager(
				schemeRegistry);
		client = new DefaultHttpClient(cm);

		class LenientCookieSpec extends BrowserCompatSpec {
			public LenientCookieSpec() {
				super();
				registerAttribHandler(ClientCookie.EXPIRES_ATTR,
						new BasicExpiresHandler(DATE_PATTERNS) {
							@Override
							public void parse(SetCookie cookie, String value)
									throws MalformedCookieException {
								super.parse(cookie, value);
							}
						});
			}
			@Override
			public void validate(Cookie cookie, CookieOrigin origin)
					throws MalformedCookieException {
			}
		}

		CookieSpecFactory csf = new CookieSpecFactory() {
			public CookieSpec newInstance(HttpParams params) {
				return new LenientCookieSpec();
			}
		};

		((AbstractHttpClient) client).getCookieSpecs().register("easy", csf);
		client.getParams().setParameter(ClientPNames.COOKIE_POLICY, "easy");
		
	}

	/** 重载使用默认帐号无参数登录 **/
	public static HttpClient login() {
		return login("jeffee@sina.cn", "ruoshui3");
		//return login("social_ck@sina.com", "jeffee");
	}

	public static HttpClient login(String u, String p) {
		try {
			// 获得rsaPubkey,rsakv,servertime等参数值
			HashMap<String, String> params = preLogin(encodeAccount(u));

			HttpPost post = new HttpPost(
					"http://login.sina.com.cn/sso/login.php?client=ssologin.js(v1.4.18)");
			post.setHeader("Host","login.sina.com.cn");
			post.setHeader("User-Agent",
					"Mozilla/5.0 (Windows NT 6.2; WOW64; rv:28.0) Gecko/20100101 Firefox/28.0");

			post.setHeader("Accept",
					"text/html,application/xhtml+xml,application/xml;q=0.9,*/*;q=0.8");
			
			post.setHeader("Accept-Language", "zh-cn,zh;q=0.8,en-us;q=0.5,en;q=0.3");
			post.setHeader("Accept-Encoding", "gzip, deflate");
			post.setHeader("Referer", "http://weibo.com/");
			post.setHeader("Content-Type", "application/x-www-form-urlencoded");
			
			
			String nonce = makeNonce(6);
			List<NameValuePair> nvps = new ArrayList<NameValuePair>();
			nvps.add(new BasicNameValuePair("encoding", "UTF-8"));
			nvps.add(new BasicNameValuePair("entry", "weibo"));
			nvps.add(new BasicNameValuePair("gateway", "1"));
			nvps.add(new BasicNameValuePair("from", ""));
			nvps.add(new BasicNameValuePair("savestate", "0"));
			nvps.add(new BasicNameValuePair("useticket", "1"));
			nvps.add(new BasicNameValuePair("pagerefer", "http://login.sina.com.cn/sso/logout.php?entry=miniblog&r=http%3A%2F%2Fweibo.com%2Flogout.php%3Fbackurl%3D%252F"));
			nvps.add(new BasicNameValuePair("vsnf", "1"));
			
			nvps.add(new BasicNameValuePair("service", "miniblog"));
			nvps.add(new BasicNameValuePair("nonce", nonce));
			nvps.add(new BasicNameValuePair("pwencode", "rsa2"));
			nvps.add(new BasicNameValuePair("rsakv", params.get("rsakv")));
	
			nvps.add(new BasicNameValuePair("prelt", "125"));
			nvps.add(new BasicNameValuePair("returntype", "META"));
			nvps.add(new BasicNameValuePair("servertime", params.get("servertime")));

			
			// nvps.add(new BasicNameValuePair("sp", new
			// SinaSSOEncoder().encode(p, data, nonce)));

			/******************** *加密密码 ***************************/
			ScriptEngineManager sem = new ScriptEngineManager();
			ScriptEngine se = sem.getEngineByName("javascript");
			File jsFile = new File("E:\\Sina\\all\\ssologin.js");
			se.eval(new FileReader(jsFile));
			String pass = "";
			//System.out.println(params.get("servertime")+"   "+nonce+"   "+params.get("pubkey"));
			if (se instanceof Invocable) {
				Invocable invoke = (Invocable) se;
				// 调用preprocess方法，并传入两个参数密码和验证码
				pass = invoke.invokeFunction("getpass", p,
						params.get("servertime"), nonce, params.get("pubkey"))
						.toString();
				/*String pubkey= "EB2A38568661887FA180BDDB5CABD5F21C7BFD59C090CB2D245A87AC253062882729293E5506350508E7F9AA3BB77F4333231490F915F6D63C55FE2F08A49B353F444AD3993CACC02DB784ABBB8E42A9B1BBFFFB38BE18D78E87A0E41B9B8F73A928EE0CCEE1F6739884B9777E4FE9E88A1BBE495927AC4A799B3181D6442443";
				pass = invoke.invokeFunction("getpass", p,
						"1398264858", "QS9KRH", pubkey)
						.toString();*/
				//pass= new BigIntegerRSA().rsaCrypt(params.get("pubkey"), nonce, pwdString); 
			}
			
			
			nvps.add(new BasicNameValuePair("sp", pass));
			nvps.add(new BasicNameValuePair("su", encodeAccount(u)));
			nvps.add(new BasicNameValuePair(
					"url",
					"http://weibo.com/ajaxlogin.php?framelogin=1&callback=parent.sinaSSOController.feedBackUrlCallBack"));
			
			post.setEntity(new UrlEncodedFormEntity(nvps, "UTF-8"));
			HttpResponse response = client.execute(post);

			
			String entity = EntityUtils.toString(response.getEntity(),"utf-8");

			System.out.println("第一次取得的"+entity);
			FileProcess.write("E:\\s.txt", entity);
			if (entity.replace("\"", "").indexOf("retcode=0") > -1) {
				String url = entity.substring(
						entity.indexOf("http://weibo.com/sso/login.php?"),
						entity.indexOf("code=0") + 6);

				String strScr = ""; // 首页用户script形式数据
				String nick = "暂无"; // 昵称

				// 获取到实际url进行连接
				HttpGet getMethod = new HttpGet(url);
				response = client.execute(getMethod);
				entity = EntityUtils.toString(response.getEntity());
				// System.out.println(entity);
				nick = entity.substring(entity.indexOf("displayname") + 14,
						entity.lastIndexOf("userdomain") - 3).trim();

				url = entity.substring(entity.indexOf("userdomain") + 13,
						entity.lastIndexOf("\""));
				getMethod = new HttpGet("http://weibo.com/" + url);
				response = client.execute(getMethod);
				entity = EntityUtils.toString(response.getEntity());
				Document doc = Jsoup.parse(entity);
				Elements els = doc.select("script");

				if (els != null && els.size() > 0) {
					for (int i = 0, leg = els.size(); i < leg; i++) {

						if (els.get(i).html().indexOf("$CONFIG") > -1) {
							strScr = els.get(i).html();
							break;
						}
					}
				}

				if (!strScr.equals("")) {
					ScriptEngineManager manager = new ScriptEngineManager();
					ScriptEngine engine = manager.getEngineByName("javascript");

					engine.eval("function getMsg(){" + strScr
							+ "return $CONFIG['onick'];}");
					if (engine instanceof Invocable) {
						Invocable invoke = (Invocable) engine;
						// 调用preprocess方法，并传入两个参数密码和验证码
						nick = invoke.invokeFunction("getMsg", null).toString();
					}
				}
				System.out.println("welcome, " + nick);
			}
		} catch (Exception e) {
			e.printStackTrace();
			System.out.println(e.getMessage());
			// logger.info(e.getMessage());
		}
		return client;
	}

	/**
	 * 根据URL,get网页
	 * 
	 * @param url
	 * @throws IOException
	 */
	public static String get(String url) {
		HttpGet get = new HttpGet(url);
		String result = "";
		try {
			HttpResponse response = client.execute(get);
			HttpEntity entity = response.getEntity();
			result = dump(entity);
			get.abort();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return result;
	}

	/**
	 * 新浪微博预登录，获取密码加密公钥
	 * 
	 * @param unameBase64
	 * @return 返回从结果获取的参数的哈希表
	 * @throws IOException
	 */
	private static HashMap<String, String> preLogin(String unameBase64)
			throws IOException {
		String url = "http://login.sina.com.cn/sso/prelogin.php?entry=weibo&callback=sinaSSOController.preloginCallBack&su=&rsakt=mod&client=ssologin.js(v1.4.11)&_="
				+ "_=" + new Date().getTime();
		
		
		return getParaFromResult(get(url));
	}

	/**
	 * 从新浪返回的结果字符串中获得参数
	 * 
	 * @param result
	 * @return
	 */
	private static HashMap<String, String> getParaFromResult(String result) {
		HashMap<String, String> hm = new HashMap<String, String>();
		result = result.substring(result.indexOf("{") + 1, result.indexOf("}"));
		String[] r = result.split(",");
		String[] temp;
		for (int i = 0; i < r.length; i++) {
			temp = r[i].split(":");
			for (int j = 0; j < 2; j++) {
				if (temp[j].contains("\""))
					temp[j] = temp[j].substring(1, temp[j].length() - 1);
			}
			hm.put(temp[0], temp[1]);
		}
		return hm;
	}

	/**
	 * 打印页面
	 * 
	 * @param entity
	 * @throws IOException
	 */
	private static String dump(HttpEntity entity) throws IOException {
		BufferedReader br = new BufferedReader(new InputStreamReader(
				entity.getContent(), "utf8"));
		return IOUtils.toString(br);
	}

	private static String encodeAccount(String account) {
		String userName = "";
		try {
			userName = Base64.encodeBase64String(URLEncoder.encode(account,
					"UTF-8").getBytes());
			// userName =
			// BASE64Encoder.encode(URLEncoder.encode(account,"UTF-8").getBytes());
		} catch (UnsupportedEncodingException e) {
			// TODO Auto-generated catch block
			System.out.println(e.getMessage());
			e.printStackTrace();
		}
		return userName;
	}

	private static String makeNonce(int len) {
		String x = "ABCDEFGHIJKLMNOPQRSTUVWXYZ0123456789";
		String str = "";
		for (int i = 0; i < len; i++) {
			str += x.charAt((int) (Math.ceil(Math.random() * 1000000) % x
					.length()));
		}
		return str;
	}

}
