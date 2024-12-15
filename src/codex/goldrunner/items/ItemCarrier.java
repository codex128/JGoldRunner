/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Interface.java to edit this template
 */
package codex.goldrunner.items;

/**
 *
 * @author gary
 */
public interface ItemCarrier {

    public abstract ItemControl getItem();

    /**
     * Specifies if this carrier is able to carry the asserted item.
     *
     * @param item
     * @return
     */
    public default boolean canAcceptItem(ItemControl item) {
        return getItem() == null;
    }

    /**
     * Specifies if this carrier to release the item it is carrying.
     *
     * @return
     */
    public default boolean canReleaseItem() {
        return true;
    }

    /**
     * Attaches the asserted item to this carrier.
     *
     * @param item
     */
    public abstract void acceptItem(ItemControl item);

    /**
     * Releases the item this carrier is carrying.
     */
    public abstract void releaseItem();

}
