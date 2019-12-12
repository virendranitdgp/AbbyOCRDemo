/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package abbyyocr;

import com.abbyy.ocrsdk.*;
import java.util.*;

/**
 *
 * @author virendra
 */
public class ClientSettings {

   // Name of application you created
	public static final String APPLICATION_ID = "";
	// Password should be sent to your e-mail after application was created
	public static final String PASSWORD = "";
	
	public static void setupProxy()
	{
		// Uncomment this if you are behind a proxy
		/*
		String host = "";
		String port = "";
		final String user = "";
		final String password = "";

		Authenticator.setDefault(
			new Authenticator() {
		 		public PasswordAuthentication getPasswordAuthentication() {
					return new PasswordAuthentication( user, password.toCharArray());
				}
 			}
		);

		System.getProperties().put("http.proxyHost", host );
		System.getProperties().put("https.proxyHost", host );
		System.getProperties().put("http.proxyPort", port);
		System.getProperties().put("https.proxyPort", port);
		System.getProperties().put("http.proxyUser", user);
		System.getProperties().put("https.proxyUser", user);
		System.getProperties().put("http.proxyPassword", password);
		System.getProperties().put("https.proxyPassword", password);
		*/
	}
}
