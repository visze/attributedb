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
import java.util.Optional;
import java.util.zip.GZIPInputStream;

import de.charite.compbio.attributedb.model.score.Attribute;
import de.charite.compbio.attributedb.model.score.AttributeType;

/**
 * @author <a href="mailto:max.schubach@charite.de">Max Schubach</a>
 *
 */
public abstract class ScoreReader implements Iterator<Attribute> {

	private AttributeType type;
	private Optional<String> nextLine;
	private BufferedReader br;
	private Optional<Iterator<String>> linesIterator;
	private Optional<Iterator<String>> fileIterator;

	public ScoreReader(List<String> files, AttributeType type) throws IOException {
		this.type = type;
		this.fileIterator = Optional.of(files.iterator());
		this.nextLine = Optional.empty();
		setNextReader();
	}

	protected void setNextReader() throws IOException {
		if (this.fileIterator.isPresent() && this.fileIterator.get().hasNext()) {
			Reader reader = getReader();
			setBr(new BufferedReader(reader));
			this.linesIterator = Optional.of(getBr().lines().iterator());
		} else {
			this.fileIterator = Optional.empty();
		}
	}

	private Reader getReader() throws IOException {
		String nextFile = this.fileIterator.get().next();
		Reader reader;
		InputStream fin = new FileInputStream(nextFile);
		BufferedInputStream in = new BufferedInputStream(fin);

		if (isGZipped(new FileInputStream(nextFile))) {
//			try {
//				CompressorInputStream gzIn = new CompressorStreamFactory().createCompressorInputStream(in);
////				ArchiveInputStream input = new ArchiveStreamFactory().createArchiveInputStream(gzIn);
//				reader = new InputStreamReader(gzIn, Charset.defaultCharset());
//			} catch (CompressorException e) {
//				e.printStackTrace();
				GZIPInputStream gzIn = new GZIPInputStream(in);
				reader = new InputStreamReader(gzIn, Charset.defaultCharset());
//			}
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
		if (this.linesIterator.isPresent()) {
			if (!this.nextLine.isPresent() && this.linesIterator.get().hasNext()) {
				this.nextLine = Optional.of(this.linesIterator.get().next());
				return hasNext();
			}
		}

		if (this.nextLine.isPresent()) {
			return true;
		}

		if (this.fileIterator.isPresent() && this.fileIterator.get().hasNext()) {
			try {
				setNextReader();
			} catch (IOException e) {
				e.printStackTrace();
			}
			return hasNext();
		}
		return false;
	}

	protected void setNextLine(Optional<String> nextLine) {
		this.nextLine = nextLine;
	}

	public AttributeType getType() {
		return this.type;
	}

	protected Optional<String> getNextLine() {
		if (this.nextLine.isPresent())
			return Optional.of(this.nextLine.get().trim());
		else
			return this.nextLine;
	}

	protected Optional<Iterator<String>> getLinesIterator() {
		return this.linesIterator;
	}

	protected void setBr(BufferedReader br) {
		this.br = br;
	}

	protected BufferedReader getBr() {
		return this.br;
	}

	@Override
	public void remove() {
		throw new UnsupportedOperationException();
	}

	protected Optional<Iterator<String>> getFileIterator() {
		return this.fileIterator;
	}

}
