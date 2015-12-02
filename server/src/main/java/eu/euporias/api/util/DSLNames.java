package eu.euporias.api.util;

import org.jooq.Field;
import org.jooq.Record;
import org.jooq.Table;
import org.jooq.impl.DSL;

public class DSLNames {

	private DSLNames(){}

	public static Table<Record> table(String name){
		return DSL.table(DSL.name(name));
	}
	
	public static <T> Field<T> field(Class<T> clazz, Table<Record> table, String column){
		return DSL.field(DSL.name(table.getName(), column), clazz);
	}
	
	public static Field<Object> field(Table<Record> table, String column){
		return DSL.field(DSL.name(table.getName(), column));
	}
	
}
