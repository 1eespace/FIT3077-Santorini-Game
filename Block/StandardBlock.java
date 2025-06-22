package Block;

/**
 * Standard block behavior: build levels up to 3, then place a dome.
 */
public class StandardBlock extends Block {

    /**
     * Defines how building progresses for a standard block.
     * Builds up to 3 levels, then places a dome.
     */
    @Override
    public void build() {
        if (!hasDome) {
            if (level < 3) {
                level++;
            } else {
                hasDome = true;
            }
        }
    }
}