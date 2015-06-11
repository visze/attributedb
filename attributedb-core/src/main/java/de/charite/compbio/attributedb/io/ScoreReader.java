package de.charite.compbio.attributedb.io;

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.nio.charset.Charset;
import java.util.Iterator;
import java.util.List;
import java.util.zip.GZIPInputStream;

import org.apache.commons.compress.archivers.ArchiveException;
import org.apache.commons.compress.archivers.ArchiveInputStream;
import org.apache.commons.compress.archivers.ArchiveStreamFactory;
import org.apache.commons.compress.compressors.CompressorException;
import org.apache.commons.compress.compressors.CompressorInputStream;
import org.apache.commons.compress.compressors.CompressorStreamFactory;

import de.charite.compbio.attributedb.model.score.Attribute;
import de.charite.compbio.attributedb.model.score.AttributeType;

/**
 * @author <a href="mailto:max.schubach@charite.de">Max Schubach</a>
 *
 */
public abstract class ScoreReader implements Iterator<Attribute> {

	private AttributeType type;
	private String nextLine;
	private BufferedReader br;
	private Iterator<String> linesIterator;
	private Iterator<String> fileIterator;

	public ScoreReader(List<String> files, AttributeType type) throws IOException {
		this.type = type;
		this.fileIterator = files.iterator();
		setNextReader();
	}

	protected void setNextReader() throws IOException {
		if (fileIterator.hasNext()) {
			Reader reader = getReader();
			setBr(new BufferedReader(reader));
			linesIterator = getBr().lines().iterator();
		} else {
			fileIterator = null;
		}
	}

	private Reader getReader() throws IOException {
		String nextFile = fileIterator.next();
		Reader reader;
		InputStream fin = new FileInputStream(nextFile);
		BufferedInputStream in = new BufferedInputStream(fin);

		if (isGZipped(new FileInputStream(nextFile))) {
			try {
				CompressorInputStream gzIn = new CompressorStreamFactory().createCompressorInputStream(in);
//				ArchiveInputStream input = new ArchiveStreamFactory().createArchiveInputStream(gzIn);
				reader = new InputStreamReader(gzIn, Charset.defaultCharset());
			} catch (CompressorException e) {
				e.printStackTrace();
				GZIPInputStream gzIn = new GZIPInputStream(in);
				reader = new InputStreamReader(gzIn, Charset.defaultCharset());
			}
		} else {
			reader = new InputStreamReader(in, Charset.defaultCharset());
		}
		return reader;
	}

	public static boolean isGZipped(InputStream in) {
		if (!in.markSupported()) {
			in = new BufferedInputStream(in);
		}
		in.mark(2);
		int magic = 0;
		try {
			magic = in.read() & 0xff | ((in.read() << 8) & 0xff00);
			in.reset();
		} catch (IOException e) {
			e.printStackTrace(System.err);
			return false;
		}
		return magic == GZIPInputStream.GZIP_MAGIC;
	}

	@Override
	public boolean hasNext() {
		if (linesIterator != null) {
			if (nextLine == null && linesIterator.hasNext()) {
				nextLine = linesIterator.next();
				return hasNext();
			}
		}

		if (nextLine != null) {
			return true;
		}

		if (fileIterator != null && fileIterator.hasNext()) {
			try {
				setNextReader();
			} catch (IOException e) {
				e.printStackTrace();
			}
			return hasNext();
		}
		return false;
	}

	protected void setNextLine(String nextLine) {
		this.nextLine = nextLine;
	}

	public AttributeType getType() {
		return type;
	}

	protected String getNextLine() {
		if (nextLine != null)
			return nextLine.trim();
		else
			return nextLine;
	}

	protected Iterator<String> getLinesIterator() {
		return linesIterator;
	}

	protected void setBr(BufferedReader br) {
		this.br = br;
	}

	protected BufferedReader getBr() {
		return br;
	}

	@Override
	public void remove() {
		throw new UnsupportedOperationException();
	}

	protected Iterator<String> getFileIterator() {
		return fileIterator;
	}

}
