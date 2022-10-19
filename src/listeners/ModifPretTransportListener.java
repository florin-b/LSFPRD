package listeners;

import adapters.AdapterRezumatComanda;
import beans.RezumatComanda;

public interface ModifPretTransportListener {
    void pretTransportModificat(RezumatComanda rezumat, String pretTransport, AdapterRezumatComanda.ViewHolder viewHolder);
}
