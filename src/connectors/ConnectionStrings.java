/**
 * @author florinb
 *
 */
package connectors;

import android.os.Environment;

public class ConnectionStrings {

	private static ConnectionStrings instance = new ConnectionStrings();

	private String myUrl;
	private String myNamespace;

	private ConnectionStrings() {

		myUrl = "http://10.1.0.58/androidwebservices/service1.asmx";
		myNamespace = "http://SmartScan.org/";

	}

	public static ConnectionStrings getInstance() {
		return instance;
	}

	public String getUrl() {
		return this.myUrl;
	}

	public String getNamespace() {
		return this.myNamespace;
	}

}
