/**
 * @author florinb
 *
 */
package model;

import android.os.Environment;

public class ConnectionStrings {

	private static ConnectionStrings instance = new ConnectionStrings();

	
	
	
	private String myUrl;
	private String myNamespace;
	private String myDatabase;

	private ConnectionStrings() {
		myUrl = "http://10.1.0.58/androidwebservices/service1.asmx";
		myNamespace = "http://SmartScan.org/";
		myDatabase = Environment.getExternalStorageDirectory().getPath() + "/download/AndroidLR";
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

	public String getDatabaseName() {
		return this.myDatabase;
	}

}
