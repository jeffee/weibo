/**
 * 
 */
package com.weibo.login;

import java.io.IOException;
import java.net.URI;
import java.util.Date;


import org.apache.http.HttpRequest;
import org.apache.http.HttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.params.CookiePolicy;
import org.apache.http.client.protocol.ClientContext;
import org.apache.http.impl.client.BasicCookieStore;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.impl.client.DefaultHttpRequestRetryHandler;
import org.apache.http.impl.cookie.BasicClientCookie;
import org.apache.http.impl.cookie.BasicClientCookie2;
import org.apache.http.params.HttpConnectionParams;
import org.apache.http.params.HttpParams;
import org.apache.http.protocol.BasicHttpContext;
import org.apache.http.protocol.ExecutionContext;
import org.apache.http.protocol.HttpContext;
import org.apache.http.util.EntityUtils;



/**
 * @author CHEN Kan
 * @date 2014年8月11日
 ***/

public class NewLogin {

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		// TODO Auto-generated method stub
		test();
	}
	private static BasicClientCookie setWeiboCookies(String name,String value,String date){
        BasicClientCookie2 cookie = new BasicClientCookie2(name,value);
        cookie.setDomain(".weibo.com");
        cookie.setPath("/");
        if (date!=null && date.trim().length()>0) {
            cookie.setExpiryDate(new Date(date));
        }else{
            cookie.setExpiryDate(null);
        }
        return cookie;
    }

    public static void test(){
        DefaultHttpClient httpclient = new DefaultHttpClient();
        httpclient.getParams().setParameter("http.protocol.cookie-policy",
                CookiePolicy.BROWSER_COMPATIBILITY);
        HttpParams params = httpclient.getParams();
        HttpConnectionParams.setConnectionTimeout(params, 5000);
        HttpConnectionParams.setSoTimeout(params, 1000*60*10);
        DefaultHttpRequestRetryHandler dhr = new DefaultHttpRequestRetryHandler(3,true);
        HttpContext localContext = new BasicHttpContext();
        HttpRequest request2 = (HttpRequest) localContext.getAttribute(
                ExecutionContext.HTTP_REQUEST);
        httpclient.setHttpRequestRetryHandler(dhr);
        BasicCookieStore cookieStore = new BasicCookieStore();

        /**
         *  weibo.com
         */
        String sus = "SID-2751730300-1407770818-GZ-odbal-a7653b57aaaf883d3bda7290f8e56457";
        String sup = "cv%3D1%26bt%3D1407770818%26et%3D1407857218%26d%3D40c3%26i%3D6457%26us%3D1%26vf%3D0%26vt%3D0%26ac%3D0%26st%3D0%26lt%3D1%26uid%3D2751730300%26user%3Dsocial_ck.%252A%252A%26ag%3D9%26name%3Dsocial_ck%2540sina.com%26nick%3Dspide%26sex%3D%26ps%3D0%26email%3Dsocial_ck%2540sina.com%26dob%3D%26ln%3Dsocial_ck%2540sina.com%26os%3D%26fmp%3D%26lcp%3D";
        String sue = "es%3D42bc1f10b0a13d5b3557e96c05faa72e%26ev%3Dv1%26es2%3D4837803afdd50e78e3e694640940668a%26rs0%3DcIH3bTRx975kL1wbbbQT4IP7%252F%252Bvvg3qlVQk4yrkKmY7pd4sXaFbSXxqk7Hr39IDMfVHmR18blCBzKi8EU%252FfhfWJdLjhMxg%252BzMUppOFJpKUTAMwVrplsSjq%252BltDXjIAnaa%252B679OD5IMNOeDhn5dUGJMW8AEdGVlP%252F6mUMQEoj82M%253D%26rv%3D0";
        cookieStore.addCookie(setWeiboCookies("SUS", sus, null)); //ok
        cookieStore.addCookie(setWeiboCookies("SUP", sup, null) ); //ok
        cookieStore.addCookie(setWeiboCookies("SUE",sue,null)); //ok

        localContext.setAttribute(ClientContext.COOKIE_STORE, cookieStore);
        HttpGet request = new HttpGet();
        request.setURI(URI.create("http://weibo.com"));
        HttpResponse response = null;
        try {
            response = httpclient.execute(request,localContext);
            System.out.println(EntityUtils.toString(response.getEntity(), "utf-8"));
        } catch (IOException e) {
            System.out.println(e);
        }
}
}
