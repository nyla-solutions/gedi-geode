package gedi.solutions.geode.operations.gfd;

import java.io.BufferedInputStream;
import java.io.Closeable;
import java.io.DataInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.nio.channels.FileChannel;
import java.util.Arrays;
import java.util.Map;

import org.apache.geode.DataSerializer;
import org.apache.geode.internal.cache.GemFireCacheImpl;
import org.apache.geode.internal.cache.snapshot.ExportedRegistry;
import org.apache.geode.internal.cache.snapshot.SnapshotPacket.SnapshotRecord;
import org.apache.geode.internal.i18n.LocalizedStrings;
import org.apache.geode.pdx.internal.EnumInfo;
import org.apache.geode.pdx.internal.PdxType;
import org.apache.geode.pdx.internal.TypeRegistry;


public class SnapshotRecordReader implements Closeable
{

	  /** the snapshot format version 1 */
	  public static final int SNAP_VER_1 = 1;

	  /** the snapshot format version 2 */
	  public static final int SNAP_VER_2 = 2;

	  /** the snapshot file format */
	  private static final byte[] SNAP_FMT = {0x47, 0x46, 0x53};
	  
	/** the snapshot file version */
    private final byte version;

    /** the region name */
    private final String region;

    /** the internal pdx registry (not the system-wide pdx registry) */
    private final ExportedRegistry pdx;

    /** the input stream */
    private final DataInputStream dis;

    public SnapshotRecordReader(File in) throws IOException, ClassNotFoundException {
      pdx = new ExportedRegistry();

      // read header and pdx registry
      long entryPosition;

      FileInputStream fis = new FileInputStream(in);
      FileChannel fc = fis.getChannel();
      DataInputStream tmp = new DataInputStream(fis);
      try {
        // read the snapshot file header
        version = tmp.readByte();
        if (version == SNAP_VER_1) {
          throw new IOException(
              LocalizedStrings.Snapshot_UNSUPPORTED_SNAPSHOT_VERSION_0.toLocalizedString(SNAP_VER_1)
                  + ": " + in);

        } else if (version == SNAP_VER_2) {
          // read format
          byte[] format = new byte[3];
          tmp.readFully(format);
          if (!Arrays.equals(format, SNAP_FMT)) {
            throw new IOException(LocalizedStrings.Snapshot_UNRECOGNIZED_FILE_TYPE_0
                .toLocalizedString(Arrays.toString(format)) + ": " + in);
          }

          // read pdx location
          long registryPosition = tmp.readLong();

          // read region
          region = tmp.readUTF();
          entryPosition = fc.position();

          // read pdx
          if (registryPosition != -1) {
            fc.position(registryPosition);
            pdx.fromData(tmp);
          }
        } else {
          throw new IOException(
              LocalizedStrings.Snapshot_UNRECOGNIZED_FILE_VERSION_0.toLocalizedString(version)
                  + ": " + in);
        }
      } finally {
        tmp.close();
      }

      // check compatibility with the existing pdx types so we don't have to
      // do any translation...preexisting types or concurrent put ops may cause
      // this check to fail
      checkPdxTypeCompatibility();
      checkPdxEnumCompatibility();

      // open new stream with buffering for reading entries
      dis = new DataInputStream(new BufferedInputStream(new FileInputStream(in)));
      dis.skip(entryPosition);
    }

    /**
     * Returns the snapshot file version.
     * 
     * @return the version
     */
    public byte getVersion() {
      return version;
    }

    /**
     * Returns the original pathname of the region used to create the snapshot.
     * 
     * @return the region name (full pathname)
     */
    public String getRegionName() {
      return region;
    }

    /**
     * Returns the pdx types defined in the snapshot file.
     * 
     * @return the pdx types
     */
    public ExportedRegistry getPdxTypes() {
      return pdx;
    }

    /**
     * Reads a snapshot entry. If the last entry has been read, a null value will be returned.
     * 
     * @return the entry or null
     * @throws IOException unable to read entry
     * @throws ClassNotFoundException unable to create entry
     */
    public SnapshotRecord readSnapshotRecord() throws IOException, ClassNotFoundException {
      byte[] key = DataSerializer.readByteArray(dis);
      if (key == null) {
        return null;
      }

      byte[] value = DataSerializer.readByteArray(dis);
      return new SnapshotRecord(key, value);
    }

    public void close() throws IOException {
      dis.close();
    }

    private TypeRegistry getRegistry() {
      GemFireCacheImpl gfc = GemFireCacheImpl.getInstance();
      if (gfc != null) {
        return gfc.getPdxRegistry();
      }
      return null;
    }

    private void checkPdxTypeCompatibility() {
      TypeRegistry tr = getRegistry();
      if (tr == null) {
        return;
      }

      for (Map.Entry<Integer, PdxType> entry : pdx.types().entrySet()) {
        tr.addImportedType(entry.getKey(), entry.getValue());
      }
    }

    private void checkPdxEnumCompatibility() {
      TypeRegistry tr = getRegistry();
      if (tr == null) {
        return;
      }

      for (Map.Entry<Integer, EnumInfo> entry : pdx.enums().entrySet()) {
        tr.addImportedEnum(entry.getKey(), entry.getValue());
      }
    }
    
}
