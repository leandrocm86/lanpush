package lanpush;

import java.util.HashMap;

import io.Leitor;
import utils.Str;

public class Config {
	private static HashMap<Str, Str> map = Leitor.toMap(Files.getConfigPath(), true);
	
	public static Str get(String key) {
		return get(key, true);
	}
	
	public static Str get(String key, boolean obrigatorio) {
		Str value = map.get(new Str(key));
		if (obrigatorio && value == null)
			throw new IllegalArgumentException("Required configuration value was not found in config file: '" + key + "'");
		return value;
	}
	
	public static Str[] getAll(String key) {
		return getAll(key, true);
	}
	
	public static Str[] getAll(String key, boolean obrigatorio) {
		Str value = get(key, obrigatorio);
		if (value != null) {
			Str[] values = value.corta(",");
			for (Str v : values)
				v.val(v.trim());
			return values;
		}
		else return null;
	}
	
	public static Integer getInt(String key) {
		return getInt(key, true);
	}
	
	public static Integer getInt(String key, boolean obrigatorio) {
		Str value = get(key, obrigatorio);
		if (value != null)
			return value.toInt();
		else return null;
	}
	
	public static Boolean getBoolean(String key) {
		return getBoolean(key, true);
	}
	
	public static Boolean getBoolean(String key, boolean obrigatorio) {
		Str value = get(key, obrigatorio);
		if (value != null)
			return value.minusculo().em("true", "yes");
		else return null;
	}
	
}
