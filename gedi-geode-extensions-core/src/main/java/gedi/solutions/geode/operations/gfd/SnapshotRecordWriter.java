package gedi.solutions.geode.operations.gfd;

import java.io.BufferedOutputStream;
import java.io.Closeable;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.channels.FileChannel;

import org.apache.geode.DataSerializer;
import org.apache.geode.cache.CacheClosedException;
import org.apache.geode.internal.InternalDataSerializer;
import org.apache.geode.internal.cache.GemFireCacheImpl;
import org.apache.geode.internal.cache.snapshot.ExportedRegistry;
import org.apache.geode.internal.cache.snapshot.SnapshotPacket.SnapshotRecord;



public class SnapshotRecordWriter implements Closeable
{
	  /** the snapshot format version 1 */
	  public static final int SNAP_VER_1 = 1;

	  /** the snapshot format version 2 */
	  public static final int SNAP_VER_2 = 2;

	  /** the snapshot file format */
	  private static final byte[] SNAP_FMT = {0x47, 0x46, 0x53};
	  
	  /** the file channel, used for random access */
    private final FileChannel fc;

    /** the output stream */
    private final DataOutputStream dos;

    public SnapshotRecordWriter(File out, String region) throws IOException {
      FileOutputStream fos = new FileOutputStream(out);
      fc = fos.getChannel();

      dos = new DataOutputStream(new BufferedOutputStream(fos));

      // write snapshot version
      dos.writeByte(SNAP_VER_2);

      // write format type
      dos.write(SNAP_FMT);

      // write temporary pdx location in bytes 4-11
      dos.writeLong(-1);

      // write region name
      dos.writeUTF(region);
    }

    /**
     * Writes an entry in the snapshot.
     * 
     * @param entry the snapshot entry
     * @throws IOException unable to write entry
     */
    public void writeSnapshotEntry(SnapshotRecord entry) throws IOException {
      InternalDataSerializer.invokeToData(entry, dos);
    }

    public void close() throws IOException {
      // write entry terminator entry
      DataSerializer.writeByteArray(null, dos);

      // grab the pdx start location
      dos.flush();
      long registryPosition = fc.position();

      // write pdx types
      try {
        GemFireCacheImpl cache = GemFireCacheImpl
            .getForPdx("PDX registry is unavailable because the Cache has been closed.");
        new ExportedRegistry(cache.getPdxRegistry()).toData(dos);
      } catch (CacheClosedException e) {
        // ignore pdx types
        new ExportedRegistry().toData(dos);
      }

      // write the pdx position
      dos.flush();
      fc.position(4);
      dos.writeLong(registryPosition);

      dos.close();
    }
}
