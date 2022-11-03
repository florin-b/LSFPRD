package listeners;

import java.util.List;

import beans.CostTransportMathaus;

public interface ModifCmdTranspListener {
    void pretTranspModificat(List<CostTransportMathaus> listCostTransport);
    void pretTranspSalvat();
}
