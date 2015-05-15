package nez.cc;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.TreeMap;

import nez.lang.Expression;
import nez.lang.Grammar;
import nez.lang.NameSpace;
import nez.lang.Production;
import nez.main.Verbose;
import nez.util.FileBuilder;
import nez.util.UList;

public abstract class GrammarGenerator {
	public abstract String getDesc();
	
	static private TreeMap<String, Class<?>> classMap = new TreeMap<String, Class<?>>();
	public static void regist(String key, Class<?> c) {
		classMap.put(key, c);
	}

	public final static boolean hasGenerator(String key) {
		if(!classMap.containsKey(key)) {
			try {
				Class.forName("nez.main.ext.L" + key);
			} catch (ClassNotFoundException e) {
			}
		}
		return classMap.containsKey(key);
	}
	
	public final static GrammarGenerator newGenerator(String command) {
		Class<?> c = classMap.get(command);
		if(c != null) {
			try {
				return (GrammarGenerator)c.newInstance();
			} catch (InstantiationException e) {
				Verbose.traceException(e);
			} catch (IllegalAccessException e) {
				Verbose.traceException(e);
			}
		}
		return null;
	}
	
	final protected FileBuilder file;
	
	public GrammarGenerator() {
		this.file = new FileBuilder();
	}

	public GrammarGenerator(String fileName) {
		this.file = new FileBuilder(fileName);
	}
	
	HashMap<Class<?>, Method> methodMap = new HashMap<Class<?>, Method>();
	
	public final void visit(Expression p) {
		Method m = lookupMethod("visit", p.getClass());
		if(m != null) {
			try {
				m.invoke(this, p);
			} catch (IllegalAccessException e) {
				e.printStackTrace();
			} catch (IllegalArgumentException e) {
				e.printStackTrace();
			} catch (InvocationTargetException e) {
				e.printStackTrace();
			}
		}
		else {
			visitUndefined(p);
		}
	}

	void visitUndefined(Expression p) {
		Verbose.todo("undefined: " + p.getClass());
	}

	protected final Method lookupMethod(String method, Class<?> c) {
		Method m = this.methodMap.get(c);
		if(m == null) {
			String name = method + c.getSimpleName();
			try {
				m = this.getClass().getMethod(name, c);
			} catch (NoSuchMethodException e) {
				return null;
			} catch (SecurityException e) {
				return null;
			}
			this.methodMap.put(c, m);
		}
		return m;
	}
	
	public void generate(Grammar grammar) {
		makeHeader();
		for(Production p: grammar.getSubProductionList()) {
			visitProduction(p);
		}
		makeFooter();
		file.writeNewLine();
		file.flush();
	}
	
	public void makeHeader() {
		file.write("// The following is generated by the Nez Grammar Generator ");
	}
	
	public void visitProduction(Production r) {
		file.writeIndent(r.toString());
	}

	public void makeFooter() {

	}

}
