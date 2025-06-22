package GodCard.Wrath;

/**
 * Represents wrath-related statuses for a cell:
 * - Sealed: permanently blocked (Artemis)
 * - Flooded: permanently blocked (Triton)
 * - WrathSelected: UI indication for wrath targeting
 */
public class WrathStatus {
    private boolean sealed = false;
    private boolean flooded = false;
    private boolean wrathSelected = false;

    public void seal() {
        this.sealed = true;
    }

    public boolean isSealed() {
        return sealed;
    }

    public void flood() {
        this.flooded = true;
    }

    public boolean isFlooded() {
        return flooded;
    }

    public void setWrathSelected(boolean selected) {
        this.wrathSelected = selected;
    }

    public boolean isWrathSelected() {
        return wrathSelected;
    }
}
