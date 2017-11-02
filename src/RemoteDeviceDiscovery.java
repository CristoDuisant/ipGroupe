import java.awt.BorderLayout;
import java.io.IOException;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.Date;
import java.util.Vector;
import javax.bluetooth.*;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JPanel;

import com.mysql.jdbc.Connection;
import com.mysql.jdbc.ResultSet;
import com.mysql.jdbc.Statement;

public class RemoteDeviceDiscovery {

	public static final Vector/* <RemoteDevice> */ devicesDiscovered = new Vector();

	public static void scanDevice() throws IOException, InterruptedException {
		final Object inquiryCompletedEvent = new Object();

		devicesDiscovered.clear();

		DiscoveryListener listener = new DiscoveryListener() {

			public void deviceDiscovered(RemoteDevice btDevice, DeviceClass cod) {
				System.out.println("Device " + btDevice.getBluetoothAddress() + " found");
				// sauverEnBase(btDevice.getBluetoothAddress());
				devicesDiscovered.addElement(btDevice);
				try {
					System.out.println("     name " + btDevice.getFriendlyName(false));
				} catch (IOException cantGetDeviceName) {
				}
			}

			public void inquiryCompleted(int discType) {
				System.out.println("Device Inquiry completed!");
				synchronized (inquiryCompletedEvent) {
					inquiryCompletedEvent.notifyAll();
				}
			}

			public void serviceSearchCompleted(int transID, int respCode) {
			}

			public void servicesDiscovered(int transID, ServiceRecord[] servRecord) {
			}

		};

		synchronized (inquiryCompletedEvent) {
			boolean started = LocalDevice.getLocalDevice().getDiscoveryAgent().startInquiry(DiscoveryAgent.GIAC,
					listener);
			if (started) {
				System.out.println("wait for device inquiry to complete...");
				inquiryCompletedEvent.wait();
				System.out.println(devicesDiscovered.size() + " device(s) found");
			}
		}

	}

	
}
