package Block;

/**
 * Abstract base class representing a building block on a cell.
 * Each block manages building levels and domes.
 */
public abstract class Block {
    protected int level;         // Building level (0~3)
    protected boolean hasDome;   // True if a dome is built on the block

    /**
     * Constructs a new Block with initial level 0 and no dome.
     */
    public Block() {
        this.level = 0;
        this.hasDome = false;
    }

    /**
     * Defines how building progresses on this block.
     */
    public abstract void build();

    /**
     * Gets the current building level.
     *
     * @return The building level.
     */
    public int getLevel() {
        return level;
    }

    /**
     * Sets the building level manually (used for special powers like Wrath).
     *
     * @param level The new level to assign
     */
    public void setLevel(int level) {
        this.level = Math.max(0, Math.min(level, 3)); // ensure range [0, 3]
    }

    /**
     * Checks if the block has a dome.
     *
     * @return true if there is a dome.
     */
    public boolean hasDome() {
        return hasDome;
    }


}
