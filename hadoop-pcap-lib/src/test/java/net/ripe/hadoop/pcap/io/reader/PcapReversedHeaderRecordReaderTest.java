package net.ripe.hadoop.pcap.io.reader;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import java.io.File;
import java.io.IOException;

import net.ripe.hadoop.pcap.PcapReader;
import net.ripe.hadoop.pcap.io.reader.PcapRecordReader;

import org.apache.hadoop.fs.FSDataInputStream;
import org.apache.hadoop.fs.FileSystem;
import org.apache.hadoop.fs.Path;
import org.apache.hadoop.mapred.JobConf;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

public class PcapReversedHeaderRecordReaderTest {
	private final File TEST_FILE = new File("src/test/resources/reversedtest.pcap");

	private PcapRecordReader recordReader;

	@Test
	public void progress() throws IOException {
		assertTrue(PcapReader.HEADER_SIZE / (float)TEST_FILE.length() == recordReader.getProgress());
		skipToEnd();
		assertTrue(1.0 == recordReader.getProgress());
	}

	@Test
	public void position() throws IOException {
		assertEquals(PcapReader.HEADER_SIZE, recordReader.getPos());
		skipToEnd();
		assertEquals(TEST_FILE.length(), recordReader.getPos());
	}

	private void skipToEnd() throws IOException {
		while (recordReader.nextKeyValue());
	}

	@Before
	public void startup() throws IOException {
		JobConf config = new JobConf();
		FileSystem fs = FileSystem.get(config);
		FSDataInputStream is = fs.open(new Path(TEST_FILE.getParent(), TEST_FILE.getName()));
		recordReader = new PcapRecordReader(new PcapReader(is), 0L, TEST_FILE.length(), is, is, new TestableTaskAttemptContext(config));
	}

	@After
	public void shutdown() throws IOException {
		recordReader.stream.close();
	}
}