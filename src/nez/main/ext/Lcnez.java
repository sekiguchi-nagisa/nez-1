package nez.main.ext;

import nez.x.generator.GeneratorLoader;

public class Lcnez {
	static {
		GeneratorLoader.regist("cnez", nez.parser.generator.CParserGenerator.class);
		// File Extension
		GeneratorLoader.regist(".c", nez.parser.generator.CParserGenerator.class);
	}
}
