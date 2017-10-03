package gedi.solutions.geode.operations.stats;

import static gedi.solutions.geode.operations.stats.StatArchiveFormat.ARCHIVE_VERSION;
import static gedi.solutions.geode.operations.stats.StatArchiveFormat.BOOLEAN_CODE;
import static gedi.solutions.geode.operations.stats.StatArchiveFormat.BYTE_CODE;
import static gedi.solutions.geode.operations.stats.StatArchiveFormat.CHAR_CODE;
import static gedi.solutions.geode.operations.stats.StatArchiveFormat.COMPACT_VALUE_2_TOKEN;
import static gedi.solutions.geode.operations.stats.StatArchiveFormat.DOUBLE_CODE;
import static gedi.solutions.geode.operations.stats.StatArchiveFormat.FLOAT_CODE;
import static gedi.solutions.geode.operations.stats.StatArchiveFormat.HEADER_TOKEN;
import static gedi.solutions.geode.operations.stats.StatArchiveFormat.ILLEGAL_RESOURCE_INST_ID;
import static gedi.solutions.geode.operations.stats.StatArchiveFormat.ILLEGAL_RESOURCE_INST_ID_TOKEN;
import static gedi.solutions.geode.operations.stats.StatArchiveFormat.ILLEGAL_STAT_OFFSET;
import static gedi.solutions.geode.operations.stats.StatArchiveFormat.INT_CODE;
import static gedi.solutions.geode.operations.stats.StatArchiveFormat.INT_TIMESTAMP_TOKEN;
import static gedi.solutions.geode.operations.stats.StatArchiveFormat.LONG_CODE;
import static gedi.solutions.geode.operations.stats.StatArchiveFormat.MAX_BYTE_RESOURCE_INST_ID;
import static gedi.solutions.geode.operations.stats.StatArchiveFormat.MIN_1BYTE_COMPACT_VALUE;
import static gedi.solutions.geode.operations.stats.StatArchiveFormat.RESOURCE_INSTANCE_CREATE_TOKEN;
import static gedi.solutions.geode.operations.stats.StatArchiveFormat.RESOURCE_INSTANCE_DELETE_TOKEN;
import static gedi.solutions.geode.operations.stats.StatArchiveFormat.RESOURCE_INSTANCE_INITIALIZE_TOKEN;
import static gedi.solutions.geode.operations.stats.StatArchiveFormat.RESOURCE_TYPE_TOKEN;
import static gedi.solutions.geode.operations.stats.StatArchiveFormat.SAMPLE_TOKEN;
import static gedi.solutions.geode.operations.stats.StatArchiveFormat.SHORT_CODE;
import static gedi.solutions.geode.operations.stats.StatArchiveFormat.SHORT_RESOURCE_INST_ID_TOKEN;
import static gedi.solutions.geode.operations.stats.StatArchiveFormat.WCHAR_CODE;

import java.io.BufferedInputStream;
import java.io.DataInputStream;
import java.io.EOFException;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.PrintWriter;
import java.nio.file.Paths;
import java.text.DateFormat;
import java.text.NumberFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;
import java.util.TimeZone;
import java.util.zip.GZIPInputStream;

import gedi.solutions.geode.operations.stats.visitors.GenericCsvStatsVisitor;
import gedi.solutions.geode.operations.stats.visitors.StatsVisitor;

/**
 * Based on GEODE-78
 * 
 * @author Gregory Green
 *
 */
public class GfStatsReader implements StatsInfo
{

	private static final String GEMFIRE_LOG_FORMAT = "yyyy/MM/dd HH:mm:ss.SSS z";

	protected static final NumberFormat nf = NumberFormat.getNumberInstance();

	static
	{
		nf.setMaximumFractionDigits(2);
		nf.setGroupingUsed(false);
	}

	private InputStream is;
	private DataInputStream dataIn;
	private ValueFilter[] filters;
	private final File archive;
	private /* final */ int archiveVersion;
	private /* final */ ArchiveInfo info;
	private final boolean compressed;
	private boolean updateOK;
	private final boolean dump;
	private boolean closed = false;
	protected int resourceInstSize = 0;
	protected ResourceInst[] resourceInstTable = null;
	private ResourceType[] resourceTypeTable = null;
	private final TimeStampSeries timeSeries = new TimeStampSeries();
	private final DateFormat timeFormatter = new SimpleDateFormat(GEMFIRE_LOG_FORMAT);
	private final static int BUFFER_SIZE = 1024 * 1024;
	private final ArrayList<ComboValue> fileComboValues = new ArrayList<ComboValue>();

	public GfStatsReader(String archiveName) throws IOException
	{
		this(archiveName, false, null);
	}

	public String getFileName()
	{
		return archive.getName();
	}// ------------------------------------------------

	public GfStatsReader(String archiveName, boolean dump, ValueFilter[] filters) throws IOException
	{
		this.archive = new File(archiveName);
		this.dump = dump;
		this.compressed = archive.getPath().endsWith(".gz");
		this.is = new FileInputStream(this.archive);
		if (this.compressed)
		{
			this.dataIn = new DataInputStream(
					new BufferedInputStream(new GZIPInputStream(this.is, BUFFER_SIZE), BUFFER_SIZE));
		}
		else
		{
			this.dataIn = new DataInputStream(new BufferedInputStream(this.is, BUFFER_SIZE));
		}
		this.updateOK = this.dataIn.markSupported();
		this.filters = createFilters(filters);

		update(false);
	}

	protected String getArchiveFileName()
	{
		return archive.getAbsolutePath();
	}

	public List<ResourceInst> getResourceInstList()
	{
		List<ResourceInst> result = new ArrayList<>();
		for (ResourceInst r : resourceInstTable)
		{
			if (r != null)
			{
				result.add(r);
			}
		}
		return result;
	}

	public List<ResourceType> getResourceTypeList()
	{
		List<ResourceType> result = new ArrayList<>();
		for (ResourceType t : resourceTypeTable)
		{
			if (t != null)
			{
				result.add(t);
			}
		}
		return result;
	}

	private ValueFilter[] createFilters(ValueFilter[] allFilters)
	{
		if (allFilters == null)
		{
			return new ValueFilter[0];
		}
		ArrayList<ValueFilter> l = new ArrayList<>();
		for (ValueFilter allFilter : allFilters)
		{
			if (allFilter.archiveMatches(archive))
			{
				l.add(allFilter);
			}
		}
		if (l.size() == allFilters.length)
		{
			return allFilters;
		}
		else
		{
			ValueFilter[] result = new ValueFilter[l.size()];
			return l.toArray(result);
		}
	}

	void matchSpec(StatSpec spec, List<StatValue> matchedValues)
	{
		if (spec.getCombineType() == StatSpec.FILE)
		{
			// search for previous ComboValue
			for (Object fileComboValue : this.fileComboValues)
			{
				ComboValue v = (ComboValue) fileComboValue;
				if (!spec.statMatches(v.getDescriptor().getName()))
				{
					continue;
				}
				if (!spec.typeMatches(v.getType().getName()))
				{
					continue;
				}
				ResourceInst[] resources = v.getResources();
				for (ResourceInst resource : resources)
				{
					if (!spec.instanceMatches(resource.getName(), resource.getId()))
					{
					}
					// note: we already know the archive file matches
				}
				matchedValues.add(v);
				return;
			}
			ArrayList<StatValue> l = new ArrayList<StatValue>();
			matchSpec(new RawStatSpec(spec), l);
			if (l.size() != 0)
			{
				ComboValue cv = new ComboValue(l);
				// save this in file's combo value list
				this.fileComboValues.add(cv);
				matchedValues.add(cv);
			}
		}
		else
		{
			for (int instIdx = 0; instIdx < resourceInstSize; instIdx++)
			{
				resourceInstTable[instIdx].matchSpec(spec, matchedValues);
			}
		}
	}

	/**
	 * Formats an archive timestamp in way consistent with GemFire log dates. It
	 * will also be formatted to reflect the time zone the archive was created
	 * in.
	 *
	 * @param ts
	 *            The difference, measured in milliseconds, between the time
	 *            marked by this time stamp and midnight, January 1, 1970 UTC.
	 *  @return format yyyy/MM/dd HH:mm:ss.SSS z
	 */
	public String formatTimeMillis(long ts)
	{
		synchronized (timeFormatter)
		{
			return timeFormatter.format(new Date(ts));
		}
	}

	/**
	 * sets the time zone this archive was written in.
	 */
	void setTimeZone(TimeZone z)
	{
		timeFormatter.setTimeZone(z);
	}

	/**
	 * @return the time series for this archive.
	 */
	TimeStampSeries getTimeStamps()
	{
		return timeSeries;
	}

	/**
	 * Checks to see if the archive has changed since the StatArchiverReader
	 * instance was created or last updated. If the archive has additional
	 * samples then those are read the resource instances maintained by the
	 * reader are updated.
	 * <p>
	 * Once closed a reader can no longer be updated.
	 *
	 *@param doReset update flag
	 * @return true if update read some new data.
	 * @throws java.io.IOException
	 *             if <code>archiveName</code> could not be opened read, or
	 *             closed.
	 */
	public boolean update(boolean doReset) throws IOException
	{
		if (this.closed)
		{
			return false;
		}
		if (!this.updateOK)
		{
			throw new RuntimeException("Update of this type of file is not supported");
		}

		if (doReset)
		{
			this.dataIn.reset();
		}

		int updateTokenCount = 0;
		while (this.readToken())
		{
			updateTokenCount++;
		}
		return updateTokenCount != 0;
	}//------------------------------------------------

	/**
	 * Print to standard out 
	 */
	public void dump()
	{
		try(PrintWriter pw = new PrintWriter(System.out))
		{
			dump(pw);
		}
	}//------------------------------------------------
	
	public void dumpCsvFiles()
	{
		GenericCsvStatsVisitor visitor = new GenericCsvStatsVisitor(this.archive);
		this.accept(visitor);
	}
	/**
	 * 
	 * @param stream the stream to print output
	 */
	public void dump(PrintWriter stream)
	{
		stream.print("archive=" + archive);
		if (info != null)
		{
			info.dump(stream);
		}
		for (ResourceType aResourceTypeTable : resourceTypeTable)
		{
			if (aResourceTypeTable != null)
			{
				aResourceTypeTable.dump(stream);
			}
		}
		stream.print("time=");
		timeSeries.dump(stream);
		for (ResourceInst inst : resourceInstTable)
		{
			if (inst != null)
			{
				inst.dump(stream);
			}
		}
	}

	/**
	 * Closes the archive.
	 * @throws IOException when an IO error occurs
	 */
	public void close() throws IOException
	{
		if (!this.closed)
		{
			this.closed = true;
			this.is.close();
			this.dataIn.close();
			this.is = null;
			this.dataIn = null;
			int typeCount = 0;
			if (this.resourceTypeTable != null)
			{ // fix for bug 32320
				for (int i = 0; i < this.resourceTypeTable.length; i++)
				{
					if (this.resourceTypeTable[i] != null)
					{
						if (this.resourceTypeTable[i].close())
						{
							this.resourceTypeTable[i] = null;
						}
						else
						{
							typeCount++;
						}
					}
				}
				ResourceType[] newTypeTable = new ResourceType[typeCount];
				typeCount = 0;
				for (ResourceType aResourceTypeTable : this.resourceTypeTable)
				{
					if (aResourceTypeTable != null)
					{
						newTypeTable[typeCount] = aResourceTypeTable;
						typeCount++;
					}
				}
				this.resourceTypeTable = newTypeTable;
			}

			if (this.resourceInstTable != null)
			{ // fix for bug 32320
				int instCount = 0;
				for (int i = 0; i < this.resourceInstTable.length; i++)
				{
					if (this.resourceInstTable[i] != null)
					{
						if (this.resourceInstTable[i].close())
						{
							this.resourceInstTable[i] = null;
						}
						else
						{
							instCount++;
						}
					}
				}
				ResourceInst[] newInstTable = new ResourceInst[instCount];
				instCount = 0;
				for (ResourceInst aResourceInstTable : this.resourceInstTable)
				{
					if (aResourceInstTable != null)
					{
						newInstTable[instCount] = aResourceInstTable;
						instCount++;
					}
				}
				this.resourceInstTable = newInstTable;
				this.resourceInstSize = instCount;
			}
			// optimize memory usage of timeSeries now that no more samples
			this.timeSeries.shrink();
			// filters are no longer needed since file will not be read from
			this.filters = null;
		}
	}

	/**
	 * @return global information about the read archive. @return null if no
	 * information is available.
	 */
	public ArchiveInfo getArchiveInfo()
	{
		return this.info;
	}

	private void readHeaderToken() throws IOException
	{
		byte archiveVersion = dataIn.readByte();
		long startTimeStamp = dataIn.readLong();
		long systemId = dataIn.readLong();
		long systemStartTimeStamp = dataIn.readLong();
		int timeZoneOffset = dataIn.readInt();
		String timeZoneName = dataIn.readUTF();
		String systemDirectory = dataIn.readUTF();
		String productVersion = dataIn.readUTF();
		String os = dataIn.readUTF();
		String machine = dataIn.readUTF();
		if (archiveVersion <= 1)
		{
			throw new RuntimeException("Archive version " + archiveVersion + " is no longer supported");
		}
		if (archiveVersion > ARCHIVE_VERSION)
		{
			throw new RuntimeException(
					"Unsupported archive version " + archiveVersion + ". The supported version is " + ARCHIVE_VERSION);
		}
		this.archiveVersion = archiveVersion;
		this.info = new ArchiveInfo(this, archiveVersion, startTimeStamp, systemStartTimeStamp, timeZoneOffset,
				timeZoneName, systemDirectory, systemId, productVersion, os, machine);
		// Clear all previously read types and instances
		this.resourceInstSize = 0;
		this.resourceInstTable = new ResourceInst[1024];
		this.resourceTypeTable = new ResourceType[256];
		timeSeries.setBase(startTimeStamp);
		if (dump)
		{
			info.dump(new PrintWriter(System.out));
		}
	}

	boolean loadType(String typeName)
	{
		// note we don't have instance data or descriptor data yet
		if (filters == null || filters.length == 0)
		{
			return true;
		}
		else
		{
			for (ValueFilter filter : filters)
			{
				if (filter.typeMatches(typeName))
				{
					return true;
				}
			}
			// System.out.println("DEBUG: don't load type=" + typeName);
			return false;
		}
	}

	boolean loadStatDescriptor(StatDescriptor stat, ResourceType type)
	{
		// note we don't have instance data yet
		if (!type.isLoaded())
		{
			return false;
		}
		if (filters == null || filters.length == 0)
		{
			return true;
		}
		else
		{
			for (ValueFilter filter : filters)
			{
				if (filter.statMatches(stat.getName()) && filter.typeMatches(type.getName()))
				{
					return true;
				}
			}
			// System.out.println("DEBUG: don't load stat=" + stat.getName());
			stat.unload();
			return false;
		}
	}

	boolean loadInstance(String textId, long numericId, ResourceType type)
	{
		if (!type.isLoaded())
		{
			return false;
		}
		if (filters == null || filters.length == 0)
		{
			return true;
		}
		else
		{
			for (ValueFilter filter : filters)
			{
				if (filter.typeMatches(type.getName()))
				{
					if (filter.instanceMatches(textId, numericId))
					{
						StatDescriptor[] stats = type.getStats();
						for (int j = 0; j < stats.length; j++)
						{
							if (stats[j].isLoaded())
							{
								if (filter.statMatches(stats[j].getName()))
								{
									return true;
								}
							}
						}
					}
				}
			}
			// System.out.println("DEBUG: don't load instance=" + textId);
			// type.unload();
			return false;
		}
	}

	boolean loadStat(StatDescriptor stat, ResourceInst resource)
	{
		ResourceType type = resource.getType();
		if (!resource.isLoaded() || !type.isLoaded() || !stat.isLoaded())
		{
			return false;
		}
		if (filters == null || filters.length == 0)
		{
			return true;
		}
		else
		{
			String textId = resource.getName();
			long numericId = resource.getId();
			for (ValueFilter filter : filters)
			{
				if (filter.statMatches(stat.getName()) && filter.typeMatches(type.getName())
						&& filter.instanceMatches(textId, numericId))
				{
					return true;
				}
			}
			return false;
		}
	}

	private void readResourceTypeToken() throws IOException
	{
		int resourceTypeId = dataIn.readInt();
		String resourceTypeName = dataIn.readUTF();
		String resourceTypeDesc = dataIn.readUTF();
		int statCount = dataIn.readUnsignedShort();
		while (resourceTypeId >= resourceTypeTable.length)
		{
			ResourceType[] tmp = new ResourceType[resourceTypeTable.length + 128];
			System.arraycopy(resourceTypeTable, 0, tmp, 0, resourceTypeTable.length);
			resourceTypeTable = tmp;
		}
		assert (resourceTypeTable[resourceTypeId] == null);

		ResourceType rt;
		if (loadType(resourceTypeName))
		{
			rt = new ResourceType(resourceTypeName, resourceTypeDesc, statCount);
			if (dump)
			{
				System.out.println("ResourceType id=" + resourceTypeId + " name=" + resourceTypeName + " statCount="
						+ statCount + " desc=" + resourceTypeDesc);
			}
		}
		else
		{
			rt = new ResourceType(resourceTypeName, statCount);
			if (dump)
			{
				System.out.println("Not loading ResourceType id=" + resourceTypeId + " name=" + resourceTypeName);
			}
		}
		resourceTypeTable[resourceTypeId] = rt;
		for (int i = 0; i < statCount; i++)
		{
			String statName = dataIn.readUTF();
			byte typeCode = dataIn.readByte();
			boolean isCounter = dataIn.readBoolean();
			boolean largerBetter = isCounter; // default
			if (this.archiveVersion >= 4)
			{
				largerBetter = dataIn.readBoolean();
			}
			String units = dataIn.readUTF();
			String desc = dataIn.readUTF();
			rt.addStatDescriptor(this, i, statName, isCounter, largerBetter, typeCode, units, desc);
			if (dump)
			{
				System.out.println("  " + i + "=" + statName + " isCtr=" + isCounter + " largerBetter=" + largerBetter
						+ " typeCode=" + typeCode + " units=" + units + " desc=" + desc);
			}
		}
	}

	private void readResourceInstanceCreateToken(boolean initialize) throws IOException
	{
		int resourceInstId = dataIn.readInt();
		String name = dataIn.readUTF();
		long id = dataIn.readLong();
		int resourceTypeId = dataIn.readInt();
		while (resourceInstId >= resourceInstTable.length)
		{
			ResourceInst[] tmp = new ResourceInst[resourceInstTable.length + 128];
			System.arraycopy(resourceInstTable, 0, tmp, 0, resourceInstTable.length);
			resourceInstTable = tmp;
		}
		assert (resourceInstTable[resourceInstId] == null);
		if ((resourceInstId + 1) > this.resourceInstSize)
		{
			this.resourceInstSize = resourceInstId + 1;
		}
		boolean loadInstance = loadInstance(name, id, resourceTypeTable[resourceTypeId]);
		resourceInstTable[resourceInstId] = new ResourceInst(this, name, id, resourceTypeTable[resourceTypeId],
				loadInstance);
		if (dump)
		{
			System.out.println((loadInstance ? "Loaded" : "Did not load") + " resource instance " + resourceInstId);
			System.out.println("  name=" + name + " id=" + id + " typeId=" + resourceTypeId);
		}
		if (initialize)
		{
			StatDescriptor[] stats = resourceInstTable[resourceInstId].getType().getStats();
			for (int i = 0; i < stats.length; i++)
			{
				long v;
				switch (stats[i].getTypeCode())
				{
				case BOOLEAN_CODE:
				case BYTE_CODE:
				case CHAR_CODE:
					v = dataIn.readByte();
					break;
				case WCHAR_CODE:
					v = dataIn.readUnsignedShort();
					break;
				case SHORT_CODE:
					v = dataIn.readShort();
					break;
				case INT_CODE:
				case FLOAT_CODE:
				case LONG_CODE:
				case DOUBLE_CODE:
					v = readCompactValue();
					break;
				default:
					throw new IOException("Unexpected typecode value" + stats[i].getTypeCode());
				}
				resourceInstTable[resourceInstId].initialValue(i, v);
			}
		}
	}

	private void readResourceInstanceDeleteToken() throws IOException
	{
		int resourceInstId = dataIn.readInt();
		assert (resourceInstTable[resourceInstId] != null);
		resourceInstTable[resourceInstId].makeInactive();
		if (dump)
		{
			System.out.println("Delete resource instance " + resourceInstId);
		}
	}

	private int readResourceInstId() throws IOException
	{
		/*
		 * if (this.archiveVersion <= 1) { return dataIn.readInt(); }
		 */
		int token = dataIn.readUnsignedByte();
		if (token <= MAX_BYTE_RESOURCE_INST_ID)
		{
			return token;
		}
		else if (token == ILLEGAL_RESOURCE_INST_ID_TOKEN)
		{
			return ILLEGAL_RESOURCE_INST_ID;
		}
		else if (token == SHORT_RESOURCE_INST_ID_TOKEN)
		{
			return dataIn.readUnsignedShort();
		}
		else
		{ /* token == INT_RESOURCE_INST_ID_TOKEN */
			return dataIn.readInt();
		}
	}

	private int readTimeDelta() throws IOException
	{
		int result = dataIn.readUnsignedShort();
		if (result == INT_TIMESTAMP_TOKEN)
		{
			result = dataIn.readInt();
		}
		return result;
	}

	private long readCompactValue() throws IOException
	{
		long v = dataIn.readByte();

		if (v < MIN_1BYTE_COMPACT_VALUE)
		{
			if (v == COMPACT_VALUE_2_TOKEN)
			{
				v = dataIn.readShort();
			}
			else
			{
				int bytesToRead = ((byte) v - COMPACT_VALUE_2_TOKEN) + 2;
				v = dataIn.readByte(); // note the first byte will be a signed
										// byte.

				bytesToRead--;
				while (bytesToRead > 0)
				{
					v <<= 8;
					v |= dataIn.readUnsignedByte();
					bytesToRead--;
				}
			}
		}
		return v;
	}

	private void readSampleToken() throws IOException
	{
		int millisSinceLastSample = readTimeDelta();
		if (dump)
		{
			System.out.println("ts=" + millisSinceLastSample);
		}
		int resourceInstId = readResourceInstId();
		while (resourceInstId != ILLEGAL_RESOURCE_INST_ID)
		{
			if (dump)
			{
				System.out.print("  instId=" + resourceInstId);
			}
			StatDescriptor[] stats = resourceInstTable[resourceInstId].getType().getStats();
			int statOffset = dataIn.readUnsignedByte();
			while (statOffset != ILLEGAL_STAT_OFFSET)
			{
				long statDeltaBits;
				switch (stats[statOffset].getTypeCode())
				{
				case BOOLEAN_CODE:
				case BYTE_CODE:
				case CHAR_CODE:
					statDeltaBits = dataIn.readByte();
					break;
				case WCHAR_CODE:
					statDeltaBits = dataIn.readUnsignedShort();
					break;
				case SHORT_CODE:
					statDeltaBits = dataIn.readShort();
					break;
				case INT_CODE:
				case FLOAT_CODE:
				case LONG_CODE:
				case DOUBLE_CODE:
					statDeltaBits = readCompactValue();
					break;
				default:
					throw new IOException("Unexepcted typecode value " + stats[statOffset].getTypeCode());
				}
				if (resourceInstTable[resourceInstId].addValueSample(statOffset, statDeltaBits))
				{
					if (dump)
					{
						System.out.print(" [" + statOffset + "]=" + statDeltaBits);
					}
				}
				statOffset = dataIn.readUnsignedByte();
			}
			if (dump)
			{
				System.out.println();
			}
			resourceInstId = readResourceInstId();
		}
		timeSeries.addTimeStamp(millisSinceLastSample);
		for (ResourceInst inst : resourceInstTable)
		{
			if (inst != null && inst.isActive())
			{
				inst.addTimeStamp();
			}
		}
	}

	/**
	 * @return true if token read, false if eof.
	 */
	private boolean readToken() throws IOException
	{
		byte token;
		try
		{
			if (this.updateOK)
			{
				this.dataIn.mark(BUFFER_SIZE);
			}
			token = this.dataIn.readByte();
			switch (token)
			{
			case HEADER_TOKEN:
				readHeaderToken();
				break;
			case RESOURCE_TYPE_TOKEN:
				readResourceTypeToken();
				break;
			case RESOURCE_INSTANCE_CREATE_TOKEN:
				readResourceInstanceCreateToken(false);
				break;
			case RESOURCE_INSTANCE_INITIALIZE_TOKEN:
				readResourceInstanceCreateToken(true);
				break;
			case RESOURCE_INSTANCE_DELETE_TOKEN:
				readResourceInstanceDeleteToken();
				break;
			case SAMPLE_TOKEN:
				readSampleToken();
				break;
			default:
				throw new IOException("Unexpected token byte value " + token);
			}
			return true;
		}
		catch (EOFException ignore)
		{
			return false;
		}
	}

	/**
	 * @return the approximate amount of memory used to implement this object.
	 */
	protected int getMemoryUsed()
	{
		int result = 0;
		for (int i = 0; i < resourceInstTable.length; i++)
		{
			if (resourceInstTable[i] != null)
			{
				result += resourceInstTable[i].getMemoryUsed();
			}
		}
		return result;
	}

	protected static double bitsToDouble(int type, long bits)
	{
		switch (type)
		{
		case BOOLEAN_CODE:
		case BYTE_CODE:
		case CHAR_CODE:
		case WCHAR_CODE:
		case SHORT_CODE:
		case INT_CODE:
		case LONG_CODE:
			return bits;
		case FLOAT_CODE:
			return Float.intBitsToFloat((int) bits);
		case DOUBLE_CODE:
			return Double.longBitsToDouble(bits);
		default:
			throw new RuntimeException("Unexpected typecode: " + type);
		}
	}

	@Override
	public void accept(StatsVisitor visitor)
	{

		if (info != null)
		{
			visitor.visitArchInfo(info);
		}

		if(resourceTypeTable != null)
		{
			for (ResourceType aResourceTypeTable : resourceTypeTable)
			{
				if (aResourceTypeTable != null)
				{
					visitor.visitResourceType(aResourceTypeTable);
				}
			}
		}
	
		visitor.visitTimeStampSeries(timeSeries);
		
		if(resourceInstTable != null)
		{
			for (ResourceInst inst : resourceInstTable)
			{
				if (inst != null)
				{
					visitor.visitResourceInst(inst);
				}
			}
			
		}

	}//------------------------------------------------
	/**
	 * Main method to extract GF Stats to file
	 * @param args archiveFile csvFile [statName ]*
	 */
	public static void main(String[] args)
	{
		File archiveFile, csvFile;
		
		if(args.length < 1)
		{
			System.err.println("Usage: java "+GfStatsReader.class.getName()+" archiveFile [csvFile [statName ]*]");
			return;
		}
		try
		{
			archiveFile = Paths.get(args[0]).toFile();
					
			if(args.length < 2)
			{
				GfStatsReader reader = new GfStatsReader(archiveFile.getAbsolutePath());
				reader.dumpCsvFiles();
				return;
			}
		
			String typeName = args[1];
			
		
			csvFile = Paths.get(args[2]).toFile();
			
			GenericCsvStatsVisitor visitor = null;
			
			if(args.length > 3)
			{
				String[] stateNames = Arrays.copyOfRange(args, 2, args.length-1);
				visitor = new GenericCsvStatsVisitor(csvFile,typeName,stateNames);
			}
			else
				visitor = new GenericCsvStatsVisitor(csvFile,typeName);
			
			System.out.println("accepting");
			GfStatsReader reader = new GfStatsReader(archiveFile.getAbsolutePath());
			reader.accept(visitor);
			
		}
		catch (IOException e)
		{
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

}
