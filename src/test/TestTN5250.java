package test;

import org.testng.*;
import org.testng.annotations.*;

import org.tn5250j.Session5250;
import org.tn5250j.beans.ProtocolBean;
import org.tn5250j.framework.tn5250.Screen5250;
import org.tn5250j.framework.tn5250.ScreenField;
import org.tn5250j.framework.tn5250.ScreenFields;
import org.tn5250j.framework.tn5250.ScreenPlanes;


public class TestTN5250 {
	
	static Session5250 session = null;
	
	@Test
	public void test1() throws Exception {

		String server = "rikas.rikascom.net";
		String user = "test";
		String password = "123";

		Screen5250 screen = connect(server, user, password);

		printScreen(screen);

		exploreScreenFields(screen);
		
		ScreenFields sf = screen.getScreenFields();
		
		ScreenField userField = sf.getField(3);
		ScreenField passField = sf.getField(4);
		userField.setString("prueba");
		passField.setString("password de prueba");
		screen.sendKeys("[enter]");
		
		
		printScreen(screen);

		Assert.assertTrue(getScreenAsString(screen).contains("Welcome to the Rikas Communications IBM i Server"));

		session.disconnect();

	}

	private void exploreScreenFields(Screen5250 screen) {
		ScreenFields sf = screen.getScreenFields();
		String s = getScreenAsString(screen);
		
		String text = "";
		
		for (int i = 0; i < sf.getFieldCount(); i++) {
			if(!sf.getField(i).isBypassField()){
				
				int pos = sf.getField(i).startPos();
				int posIni = 0;
				if (pos>40){
					posIni = pos-40;
				}
				text = s.substring(posIni, pos);
				
				System.out.println("field " + i +
						" -> id = " + sf.getField(i).getFieldId()
						+  " str at left = " + text);
			}
		}
		
	}

	private Screen5250 connect(String server, String user, String password) throws Exception {
		ProtocolBean pb = new ProtocolBean(user, password);
		pb.setHostName(server);
		session = pb.getSession();
		pb.connect();

		Screen5250 screen = session.getScreen();
		Thread.sleep(3000L);
		System.err.println("Is connected? - " + session.isConnected());
		
		return screen;
	}

	private void printScreen(Screen5250 screen) {
		String showme = getScreenAsString(screen);
		String sb = "";
		
		for(int i=0;i<showme.length();i+=80) {
			sb += showme.substring(i, i+80);
			sb += "\n";
		}
		System.out.println(sb);
	}

	private String getScreenAsString(Screen5250 screen) {
		char [] buffer = new char[1920];
		screen.GetScreen(buffer, 1920, ScreenPlanes.PLANE_TEXT);
		return new String(buffer);
	}
}
