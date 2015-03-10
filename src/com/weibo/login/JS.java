/**
 * 
 */
package com.weibo.login;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;

import javax.script.Invocable;
import javax.script.ScriptEngine;
import javax.script.ScriptEngineManager;
import javax.script.ScriptException;

/**
 * @author CHEN Kan
 * @date 2014年4月23日
 ***/

public class JS {

	/**
	 * @param args
	 * @throws ScriptException 
	 * @throws FileNotFoundException 
	 * @throws NoSuchMethodException 
	 */
	public static void main(String[] args) throws FileNotFoundException, ScriptException, NoSuchMethodException {
		
		ScriptEngineManager sem = new ScriptEngineManager();
		ScriptEngine se = sem.getEngineByName("javascript");
		File jsFile = new File("E:\\Sina\\all\\ssologin.js");
		se.eval(new FileReader(jsFile));
		String pass = "";
		
		if (se instanceof Invocable) {
			Invocable invoke = (Invocable) se;
			String p = "jeffee";
		
			String pubkey= "EB2A38568661887FA180BDDB5CABD5F21C7BFD59C090CB2D245A87AC253062882729293E5506350508E7F9AA3BB77F4333231490F915F6D63C55FE2F08A49B353F444AD3993CACC02DB784ABBB8E42A9B1BBFFFB38BE18D78E87A0E41B9B8F73A928EE0CCEE1F6739884B9777E4FE9E88A1BBE495927AC4A799B3181D6442443";
			pass = invoke.invokeFunction("getpass", p,
					"1398323891", "OI9CH3", pubkey)
					.toString();
			//pass= new BigIntegerRSA().rsaCrypt(params.get("pubkey"), nonce, pwdString); 
		}
		
		System.out.println(pass);
	}

}
