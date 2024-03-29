package dialogs;

import android.app.Dialog;
import android.content.Context;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.ListView;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

import my.logon.screen.R;
import adapters.AdapterRezumatComanda;
import beans.CostTransportMathaus;
import beans.RezumatComanda;
import listeners.ComandaMathausListener;
import listeners.RezumatListener;
import model.ArticolComanda;
import model.DateLivrare;
import model.ListaArticoleComanda;
import model.ListaArticoleComandaGed;

public class RezumatComandaDialog extends Dialog implements RezumatListener {

    private Context context;
    private List<ArticolComanda> listArticole;
    private ListView listViewComenzi;
    private String canalDistrib;
    private Button btnCancelComanda;
    private Button btnOkComanda;
    private ComandaMathausListener listener;
    private List<CostTransportMathaus> costTransport;
    private String tipTransport;
    private String filialeArondate;
    private LinearLayout layoutInfo;
    private Button btnAdresaLivrare;
    private boolean selectTransp;

    public RezumatComandaDialog(Context context, List<ArticolComanda> listArticole, String canal, List<CostTransportMathaus> costTransport, String tipTransport, String filialeArondate, boolean selectTransp) {
        super(context);
        this.context = context;
        this.listArticole = listArticole;
        this.canalDistrib = canal;
        this.costTransport = costTransport;
        this.tipTransport = tipTransport;
        this.filialeArondate = filialeArondate;
        this.selectTransp = selectTransp;

        setContentView(R.layout.rezumat_comanda_dialog);
        setTitle("Rezumat comanda");
        setCancelable(false);

        setupLayout();

    }

    private void setupLayout() {

        listViewComenzi = (ListView) findViewById(R.id.listComenzi);

        AdapterRezumatComanda adapterRezumat = new AdapterRezumatComanda(context, getRezumatComanda(), costTransport, tipTransport, filialeArondate, selectTransp);
        adapterRezumat.setRezumatListener(this);
        listViewComenzi.setAdapter(adapterRezumat);

        btnCancelComanda = (Button) findViewById(R.id.btnCancelComanda);
        setListenerBtnCancel();
        btnOkComanda = (Button) findViewById(R.id.btnOkComanda);
        setListenerComandaSalvata();

        layoutInfo = (LinearLayout) findViewById(R.id.layoutInfo);
        btnAdresaLivrare = (Button) findViewById(R.id.btnAdresaLivrare);
        setListenerAdresaLivrare();

    }

    private void setListenerBtnCancel() {
        btnCancelComanda.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                dismiss();

            }
        });
    }

    private void setListenerComandaSalvata() {
        btnOkComanda.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                if (listener != null) {

                    if (listViewComenzi.getAdapter().getCount() > 0)
                        listener.comandaSalvata();

                    dismiss();
                }

            }
        });
    }

    private void setListenerAdresaLivrare() {
        btnAdresaLivrare.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                listener.redirectDateLivrare();
                dismiss();
            }
        });
    }

    private List<RezumatComanda> getRezumatComanda() {

        Set<String> filiale = getFilialeComanda();

        List<RezumatComanda> listComenzi = new ArrayList<RezumatComanda>();

        for (String filiala : filiale) {

            RezumatComanda rezumat = new RezumatComanda();
            rezumat.setFilialaLivrare(filiala);
            List<ArticolComanda> listArtComanda = new ArrayList<ArticolComanda>();

            for (ArticolComanda articol : listArticole) {
                if (articol.getFilialaSite().equals(filiala)) {
                    listArtComanda.add(articol);
                }
            }

            rezumat.setListArticole(listArtComanda);
            listComenzi.add(rezumat);
        }

        return listComenzi;
    }

    private Set<String> getFilialeComanda() {

        Set<String> filiale = new HashSet<String>();
        for (final ArticolComanda articol : listArticole) {
            filiale.add(articol.getFilialaSite());
        }
        return filiale;

    }

    public void setRezumatListener(ComandaMathausListener listener) {
        this.listener = listener;
    }

    public void showDialog() {
        this.show();
    }

    @Override
    public void comandaEliminata(List<String> listArticoleEliminate, String filialaLivrare) {

        List<ArticolComanda> listArticoleComanda;
        if (canalDistrib.equals("10"))
            listArticoleComanda = ListaArticoleComanda.getInstance().getListArticoleComanda();
        else
            listArticoleComanda = ListaArticoleComandaGed.getInstance().getListArticoleComanda();

        Iterator<ArticolComanda> listIterator = listArticoleComanda.iterator();

        while (listIterator.hasNext()) {
            ArticolComanda articol = listIterator.next();

            for (String articolEliminat : listArticoleEliminate) {

                if (articol.getCodArticol().equals(articolEliminat) && articol.getFilialaSite().equals(filialaLivrare)) {
                    listIterator.remove();
                    break;
                }

            }

        }

        if (listener != null)
            listener.comandaEliminata();

    }

    @Override
    public void adaugaArticol(ArticolComanda articolComanda) {

        List<ArticolComanda> listArticoleComanda;
        if (canalDistrib.equals("10"))
            listArticoleComanda = ListaArticoleComanda.getInstance().getListArticoleComanda();
        else
            listArticoleComanda = ListaArticoleComandaGed.getInstance().getListArticoleComanda();

        listArticoleComanda.add(articolComanda);

        if (listener != null)
            listener.comandaEliminata();


    }

    @Override
    public void eliminaArticol(ArticolComanda articolComanda) {

        List<ArticolComanda> listArticoleComanda;
        if (canalDistrib.equals("10"))
            listArticoleComanda = ListaArticoleComanda.getInstance().getListArticoleComanda();
        else
            listArticoleComanda = ListaArticoleComandaGed.getInstance().getListArticoleComanda();

        Iterator<ArticolComanda> listIterator = listArticoleComanda.iterator();

        while (listIterator.hasNext()) {
            ArticolComanda articol = listIterator.next();

            if (articol.getCodArticol().equals(articolComanda.getCodArticol()) && articol.getFilialaSite().equals(articolComanda.getFilialaSite())) {
                listIterator.remove();
                break;
            }
        }


        if (listener != null)
            listener.comandaEliminata();

    }

    private boolean isCom1() {

        for (ArticolComanda articol : listArticole) {
            if (articol.getTipTransport() != null && articol.getTipTransport().equals("TRAP"))
                return true;
        }

        return false;
    }

    @Override
    public void setStareRezumat(String codStare, String filialaLivrare) {

        if (DateLivrare.getInstance().getFilialaLivrareTCLI() == null || DateLivrare.getInstance().getFilialaLivrareTCLI().isEmpty())
            return;

        if ((codStare.equals("0") && !isCom1()) || !selectTransp) {
            btnOkComanda.setVisibility(View.VISIBLE);
            layoutInfo.setVisibility(View.INVISIBLE);
        } else {
            btnOkComanda.setVisibility(View.INVISIBLE);
            layoutInfo.setVisibility(View.VISIBLE);
        }

    }


}
