package gedi.solutions.geode.operations.stats;

public interface StatSpec extends ValueFilter {
    /**
     * Causes all stats that matches this spec, in all archive files, to be
     * combined into a single global stat value.
     */
    public static final int GLOBAL = 2;
    /**
     * Causes all stats that matches this spec, in each archive file, to be
     * combined into a single stat value for each file.
     */
    public static final int FILE = 1;
    /**
     * No combination is done.
     */
    public final int NONE = 0;

    /**
     * @return one of the following values: {@link #GLOBAL}, {@link #FILE},
     * {@link #NONE}.
     */
    public int getCombineType();
  }