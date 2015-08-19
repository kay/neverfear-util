package org.neverfear.util.sequence;

import org.neverfear.util.sequence.api.Sequence;
import org.neverfear.util.sequence.api.SequenceException;

import java.io.Closeable;
import java.io.File;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.LongBuffer;
import java.nio.channels.FileChannel;
import java.nio.channels.FileLock;
import java.nio.file.StandardOpenOption;

public class ConcurrentFileLongSequence implements Sequence, Closeable {

    private final ByteBuffer byteBuffer = ByteBuffer.allocate(Long.BYTES);
    private final LongBuffer longBuffer = byteBuffer.asLongBuffer();
    private final File file;
    private FileChannel channel = null;

    public ConcurrentFileLongSequence(final File file) {
        super();
        this.file = file;
    }

    public void open() throws IOException {
        if (this.channel == null) {
            this.channel = FileChannel.open(this.file.toPath(),
                    StandardOpenOption.CREATE,
                    StandardOpenOption.READ,
                    StandardOpenOption.WRITE);
            CloseResource.closeOnExit(this.channel);
            if (this.channel.size() == 0) {
                this.longBuffer.put(0);
                this.channel.write(byteBuffer);
            }
        }
    }

    @Override
    public long next() {
        if (this.channel == null) {
            throw new SequenceException("File is not opened");
        }

        // TODO: Need a benchmark to prove that ThreadLocal is better than creating a little bit of garbage. Fairly constrained work load here (as this is not thread safe) so should be easy.
        byteBuffer.clear();

        try (FileLock lock = this.channel.lock()) {
            final int readCount = this.channel.read(byteBuffer, 0);
            if (readCount == -1) {
                throw new SequenceException("Failed to read buffer");
            } else if (readCount != byteBuffer.capacity()) {
                throw new SequenceException("Partial buffer read");
            }
            byteBuffer.flip();

            final long value = this.longBuffer.get(0);
            this.longBuffer.put(0, value + 1);
            this.channel.write(byteBuffer, 0);
            return value;
        } catch (final IOException e) {
            throw new SequenceException(e);
        }
    }

    @Override
    public void close() throws IOException {
        if (this.channel == null) {
            return;
        }
        this.channel.close();
    }
}
