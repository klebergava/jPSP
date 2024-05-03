package br.com.jpsp.dao;

import java.math.BigDecimal;
import java.sql.SQLException;

public interface Result {
	boolean isEmpty();

	boolean moveToNext();

	int getColumnIndex(String paramString);

	float getFloat(int paramInt);

	int getInt(int paramInt);

	String getString(int paramInt);

	float getFloat(String paramString);

	BigDecimal getBigDecimal(int paramInt);

	int getInt(String paramString);

	long getLong(String paramString);

	String getString(String paramString);

	void close() throws SQLException;

	String getByColumnIndex(int paramInt);
}
