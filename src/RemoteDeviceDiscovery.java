import java.io.IOException;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.Date;
import java.util.Vector;
import javax.bluetooth.*;

import com.mysql.jdbc.Connection;
import com.mysql.jdbc.ResultSet;
import com.mysql.jdbc.Statement;
//test test2
public class RemoteDeviceDiscovery {

	 public static final Vector/*<RemoteDevice>*/ devicesDiscovered = new Vector();

	    public static void main(String[] args) throws IOException, InterruptedException {

	        final Object inquiryCompletedEvent = new Object();

	        devicesDiscovered.clear();
	        
	        sauverEnBase();

	        DiscoveryListener listener = new DiscoveryListener() {

	            public void deviceDiscovered(RemoteDevice btDevice, DeviceClass cod) {
	                System.out.println("Device " + btDevice.getBluetoothAddress() + " found");
	                devicesDiscovered.addElement(btDevice);
	                try {
	                    System.out.println("     name " + btDevice.getFriendlyName(false));
	                } catch (IOException cantGetDeviceName) {
	                }
	            }

	            public void inquiryCompleted(int discType) {
	                System.out.println("Device Inquiry completed!");
	                synchronized(inquiryCompletedEvent){
	                    inquiryCompletedEvent.notifyAll();
	                }
	            }

	            public void serviceSearchCompleted(int transID, int respCode) {
	            }

	            public void servicesDiscovered(int transID, ServiceRecord[] servRecord) {
	            }
	        };

	        synchronized(inquiryCompletedEvent) {
	            boolean started = LocalDevice.getLocalDevice().getDiscoveryAgent().startInquiry(DiscoveryAgent.GIAC, listener);
	            if (started) {
	                System.out.println("wait for device inquiry to complete...");
	                inquiryCompletedEvent.wait();
	                System.out.println(devicesDiscovered.size() +  " device(s) found");
	            }
	        }
	    }

	    public static void sauverEnBase(){
	    	String url = "jdbc:mysql://localhost/bluetooth";
	    	String login = "root";
	    	String password = "";
	    	Connection cn = null;
	    	Statement st = null;
	    	ResultSet rs = null;
	    	
	    	try {
	    		Class.forName("com.mysql.jdbc.Driver");
	    		cn = (Connection) DriverManager.getConnection(url, login, password);
	    		st = (Statement) cn.createStatement();
	    		Date d = new Date();
	    		/*String sql = "INSERT INTO client (`nameDevice`,`mail`,`telephone`,`nameClient`,`dateDerniereVisite`) VALUE ('48DB50D48881', 'benoit@hotmail.com', '010203040506', 'benoit', '"+d+"') ";
	    		st.executeUpdate(sql);*/
	    		String sqlSelect = "SELECT * FROM `client` ";
	    		rs = (ResultSet) st.executeQuery(sqlSelect);
	    		while (rs.next()){
	    			System.out.println("Name Device: " + rs.getString("nameDevice")+"; Name Client : "+rs.getString("nameClient"));
	    		}
	    	}catch (SQLException e){
	    		e.printStackTrace();
	    	}catch (ClassNotFoundException e){
	    		e.printStackTrace();
	    	}finally{
	    		try {
	    			cn.close();
	    			st.close();
	    		}catch(SQLException e){
	    			e.printStackTrace();
	    		}
	    	}
	    }
}


