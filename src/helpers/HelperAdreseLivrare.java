package helpers;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import my.logon.screen.CreareComanda;
import my.logon.screen.ModificareComanda;
import model.DateLivrare;
import model.UserInfo;
import android.content.Context;

public class HelperAdreseLivrare {

	private static final String livrareRapida = "TERR - Curier rapid";

	private static String localitatiAcceptate;

	public static boolean isConditiiCurierRapid() {

		if (!UserInfo.getInstance().getCodDepart().equals("02"))
			return false;

		if (CreareComanda.filialaAlternativa != null)
			return CreareComanda.filialaAlternativa.equalsIgnoreCase("BV90") && UserInfo.getInstance().getUnitLog().startsWith("BU");
		else if (ModificareComanda.filialaAlternativaM != null)
			return ModificareComanda.filialaAlternativaM.equalsIgnoreCase("BV90") && UserInfo.getInstance().getUnitLog().startsWith("BU");

		return false;

	}

	public static String[] adaugaTransportCurierRapid(String[] tipTransport) {

		String[] arrayTransport;

		List<String> listTransport = new ArrayList<String>(Arrays.asList(tipTransport));

		listTransport.add(livrareRapida);

		arrayTransport = (String[]) listTransport.toArray(new String[listTransport.size()]);

		return arrayTransport;

	}

	public static boolean isAdresaLivrareRapida() {
		return localitatiAcceptate.toUpperCase().contains(DateLivrare.getInstance().getOras().toUpperCase());
	}

	public static String getLocalitatiAcceptate() {
		return localitatiAcceptate;
	}

	public static void setLocalitatiAcceptate(String localitatiAcceptate) {
		HelperAdreseLivrare.localitatiAcceptate = localitatiAcceptate;
	}

	public static int getTonajSpinnerPos(String tonaj) {
		if (tonaj == null || tonaj.equals("0"))
			return 0;

		if (tonaj.equals("3.5"))
			return 1;
		else if (tonaj.equals("10"))
			return 2;
		else if (tonaj.equals("20"))
			return 3;

		return 0;
	}

	public static String getTonajSpinnerValue(int selectedPos) {

		switch (selectedPos) {
		case 1:
			return "3.5";
		case 2:
			return "10";
		case 3:
		default:
			return "20";

		}
	}

}
