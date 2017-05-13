package gedi.solutions.geode.operations.stats;

import java.io.File;

/**
   * Specifies what data from a statistic archive will be of interest to the
   * reader. This is used when loading a statistic archive file to reduce the
   * memory footprint. Only statistic data that matches all four will be
   * selected for loading.
   */
  public interface ValueFilter {
    /**
     * Returns true if the specified archive file matches this spec. Any
     * archives whose name does not match this spec will not be selected for
     * loading by this spec.
     */
    public boolean archiveMatches(File archive);

    /**
     * Returns true if the specified type name matches this spec. Any types
     * whose name does not match this spec will not be selected for loading by
     * this spec.
     */
    public boolean typeMatches(String typeName);

    /**
     * Returns true if the specified statistic name matches this spec. Any stats
     * whose name does not match this spec will not be selected for loading by
     * this spec.
     */
    public boolean statMatches(String statName);

    /**
     * Returns true if the specified instance matches this spec. Any instance
     * whose text id and numeric id do not match this spec will not be selected
     * for loading by this spec.
     */
    public boolean instanceMatches(String textId, long numericId);
  }