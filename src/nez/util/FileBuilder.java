package nez.util;

import java.io.BufferedOutputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;

import nez.main.Verbose;

public class FileBuilder {

	protected String fileName = null;
	private OutputStream out;
	private String CHARSET = "UTF8";

	public final static String TAB = "   ";
	public final static String LF = "\n";
	public final static String CRLF = "\r\n";

	public FileBuilder() {
		this.out = null;
	}

	public FileBuilder(String fileName) {
		try {
			this.out = new BufferedOutputStream(new FileOutputStream(fileName));
			this.fileName = fileName;
		} catch (NullPointerException e) {
			this.out = null;
		} catch (FileNotFoundException e) {
			ConsoleUtils.notice(e.getMessage());
			this.out = null;
		}
	}

	public final void write(String text) {
		try {
			if (out == null) {
				System.out.print(text);
			} else {
				out.write(text.getBytes(CHARSET));
			}
		} catch (IOException e) {
			ConsoleUtils.exit(1, "IO error: " + e.getMessage());
		}
	}

	public final void flush() {
		try {
			if (out == null) {
				System.out.flush();
			} else {
				out.flush();
			}
		} catch (IOException e) {
			ConsoleUtils.exit(1, "IO error: " + e.getMessage());
		}
	}

	public final void close() {
		this.flush();
		if (this.fileName != null) {
			Verbose.println("written to: " + this.fileName);
		}
	}

	public String Tab() {
		return TAB;
	}

	public String NewLine() {
		return LF;
	}

	int IndentLevel = 0;
	String currentIndentString = "";

	public final void incIndent() {
		this.IndentLevel = this.IndentLevel + 1;
		this.currentIndentString = null;
	}

	public final void decIndent() {
		this.IndentLevel = this.IndentLevel - 1;
		assert (this.IndentLevel >= 0);
		this.currentIndentString = null;
	}

	private final String Indent() {
		if (this.currentIndentString == null) {
			StringBuilder indentBuilder = new StringBuilder(64);
			for (int i = 0; i < this.IndentLevel; ++i) {
				indentBuilder.append(this.Tab());
			}
			this.currentIndentString = indentBuilder.toString();
		}
		return this.currentIndentString;
	}

	public final void writeNewLine() {
		this.write(this.NewLine());
		this.flush();
	}

	public final void writeIndent() {
		this.write(this.NewLine());
		this.flush();
		this.write(Indent());
	}

	public final void writeIndent(String text) {
		this.write(this.NewLine());
		this.flush();
		this.write(Indent());
		this.write(text);
	}

	public void writeMultiLine(String sub) {
		int start = 0;
		boolean empty = true;
		for (int i = 0; i < sub.length(); i++) {
			char ch = sub.charAt(i);
			if (ch == ' ' || ch == '\t') {
				continue;
			}
			if (ch == '\n') {
				if (!empty) {
					this.writeIndent(sub.substring(start, i));
				}
				start = i + 1;
				empty = true;
				continue;
			}
			empty = false;
		}
	}

}
