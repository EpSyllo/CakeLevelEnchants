package dev.cakestudio.cakelevelenchants.listener;

import dev.cakestudio.cakelevelenchants.CakeLevelEnchants;
import dev.cakestudio.cakelevelenchants.config.AnvilConfig;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryOpenEvent;
import org.bukkit.event.inventory.PrepareAnvilEvent;
import org.bukkit.inventory.view.AnvilView;

public class AnvilListener implements Listener {

    private final CakeLevelEnchants p;

    public AnvilListener(CakeLevelEnchants p) {
        this.p = p;
    }

    @EventHandler
    public void onOpen(InventoryOpenEvent e) {
        AnvilConfig ac = p.getCm().getAc();
        if (ac == null || !ac.removeTooExpensive()) return;

        if (e.getView() instanceof AnvilView v) {
            v.setMaximumRepairCost(Integer.MAX_VALUE);
        }
    }

    @EventHandler
    public void onPrepare(PrepareAnvilEvent e) {
        AnvilConfig ac = p.getCm().getAc();
        if (ac == null || !ac.removeTooExpensive()) return;

        AnvilView v = e.getView();
        v.setMaximumRepairCost(Integer.MAX_VALUE);

        int cap = ac.costCap();
        if (cap > 0 && v.getRepairCost() > cap) {
            v.setRepairCost(cap);
        }
    }
}