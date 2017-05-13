package gedi.solutions.geode.operations.stats;

public interface StatValue {
    /**
     * {@link StatArchiveFile.StatValue} filter
     * that causes the statistic values to be unfiltered. This causes the raw
     * values written to the archive to be used. <p>This is the default filter
     * for non-counter statistics. To determine if a statistic is not a counter
     * use {@link StatArchiveFile.StatDescriptor#isCounter}.
     */
    public static final int FILTER_NONE = 0;
    /**
     * {@link StatArchiveFile.StatValue} filter
     * that causes the statistic values to be filtered to reflect how often they
     * change per second.  Since the difference between two samples is used to
     * calculate the value this causes the {@link StatArchiveFile.StatValue}
     * to have one less sample than {@link #FILTER_NONE}. The instance time
     * stamp that does not have a per second value is the instance's first time
     * stamp {@link StatArchiveFile.ResourceInst#getFirstTimeMillis}.
     * <p>This is the default filter for counter statistics. To determine if a
     * statistic is a counter use {@link StatArchiveFile.StatDescriptor#isCounter}.
     */
    public static final int FILTER_PERSEC = 1;
    /**
     * {@link StatArchiveFile.StatValue} filter
     * that causes the statistic values to be filtered to reflect how much they
     * changed between sample periods.  Since the difference between two samples
     * is used to calculate the value this causes the {@link
     * StatArchiveFile.StatValue} to have one less
     * sample than {@link #FILTER_NONE}. The instance time stamp that does not
     * have a per second value is the instance's first time stamp {@link
     * StatArchiveFile.ResourceInst#getFirstTimeMillis}.
     */
    public static final int FILTER_PERSAMPLE = 2;

    /**
     * Creates and returns a trimmed version of this stat value. Any samples
     * taken before <code>startTime</code> and after <code>endTime</code> are
     * discarded from the resulting value. Set a time parameter to
     * <code>-1</code> to not trim that side.
     */
    public StatValue createTrimmed(long startTime, long endTime);

    /**
     * Returns true if value has data that has been trimmed off it by a start
     * timestamp.
     */
    public boolean isTrimmedLeft();

    /**
     * Gets the {@link StatArchiveFile.ResourceType
     * type} of the resources that this value belongs to.
     */
    public ResourceType getType();

    /**
     * Gets the {@link StatArchiveFile.ResourceInst
     * resources} that this value belongs to.
     */
    public ResourceInst[] getResources();

    /**
     * Returns an array of timestamps for each unfiltered snapshot in this
     * value. Each returned time stamp is the number of millis since midnight,
     * Jan 1, 1970 UTC.
     */
    public long[] getRawAbsoluteTimeStamps();

    /**
     * Returns an array of timestamps for each unfiltered snapshot in this
     * value. Each returned time stamp is the number of millis since midnight,
     * Jan 1, 1970 UTC. The resolution is seconds.
     */
    public long[] getRawAbsoluteTimeStampsWithSecondRes();

    /**
     * Returns an array of doubles containing the unfiltered value of this
     * statistic for each point in time that it was sampled.
     */
    public double[] getRawSnapshots();

    /**
     * Returns an array of doubles containing the filtered value of this
     * statistic for each point in time that it was sampled.
     */
    public double[] getSnapshots();

    /**
     * Returns the number of samples taken of this statistic's value.
     */
    public int getSnapshotsSize();

    /**
     * Returns the smallest of all the samples taken of this statistic's value.
     */
    public double getSnapshotsMinimum();

    /**
     * Returns the largest of all the samples taken of this statistic's value.
     */
    public double getSnapshotsMaximum();

    /**
     * Returns the average of all the samples taken of this statistic's value.
     */
    public double getSnapshotsAverage();

    /**
     * Returns the standard deviation of all the samples taken of this
     * statistic's value.
     */
    public double getSnapshotsStandardDeviation();

    /**
     * Returns the most recent value of all the samples taken of this
     * statistic's value.
     */
    public double getSnapshotsMostRecent();

    /**
     * Returns true if sample whose value was different from previous values has
     * been added to this StatValue since the last time this method was called.
     */
    public boolean hasValueChanged();

    /**
     * Returns the current filter used to calculate this statistic's values. It
     * will be one of these values: <ul> <li> {@link #FILTER_NONE} <li> {@link
     * #FILTER_PERSAMPLE} <li> {@link #FILTER_PERSEC} </ul>
     */
    public int getFilter();

    /**
     * Sets the current filter used to calculate this statistic's values. The
     * default filter is {@link #FILTER_NONE} unless the statistic is a counter,
     * {@link StatArchiveFile.StatDescriptor#isCounter},
     * in which case its {@link #FILTER_PERSEC}.
     *
     * @param filter It must be one of these values: <ul> <li> {@link
     *               #FILTER_NONE} <li> {@link #FILTER_PERSAMPLE} <li> {@link
     *               #FILTER_PERSEC} </ul>
     * @throws IllegalArgumentException if <code>filter</code> is not a valid
     *                                  filter constant.
     */
    public void setFilter(int filter);

    /**
     * Returns a description of this statistic.
     */
    public StatDescriptor getDescriptor();
  }