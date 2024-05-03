package br.com.jpsp.dao;

import java.math.BigDecimal;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

public abstract class DAO {
	
	protected Connection connection;
	protected Statement stmt;
	protected boolean noDatabase = false;

	protected void execute(String sql) {
		try {
			openConnection(true);
			this.stmt.execute(sql);
		} catch (SQLException e) {
			e.printStackTrace();
		} catch (ClassNotFoundException e) {
			e.printStackTrace();
		} finally {
			try {
				closeConnection(false);
			} catch (SQLException e) {
				e.printStackTrace();
			}
		}
	}

	public int executeUpdate(String sql) {
		try {
			return this.stmt.executeUpdate(sql);
		} catch (SQLException e) {
			e.printStackTrace();

			return 0;
		}
	}

	protected synchronized Result executeQuery(String sql) {
		try {
			return new MyResult(this.stmt.executeQuery(sql));
		} catch (SQLException e) {
			e.printStackTrace();

			return null;
		}
	}

	protected boolean isNewDatabase() {
		return this.noDatabase;
	}

	protected void openConnection(boolean autoCommit) throws ClassNotFoundException, SQLException {
		loadDatabase();
		connection.setAutoCommit(autoCommit);
	}

	protected abstract void loadDatabase();

	protected void closeConnection(boolean commit) throws SQLException {
		this.stmt.close();
		if (commit) {
			connection.commit();
		}
		connection.close();
	}

	protected boolean isVersionDifferent() {
		Result q = executeQuery("PRAGMA user_version");
		if (!q.isEmpty()) {
			return (q.getInt(1) != 1);
		}
		return true;
	}

	public class MyResult implements Result {
		ResultSet rs;

		boolean calledIsEmpty = false;

		public MyResult(ResultSet res) {
			this.rs = res;
		}

		public boolean isEmpty() {
			try {
				if (this.rs.getRow() == 0) {
					this.calledIsEmpty = true;
					return !this.rs.next();
				}
				return (this.rs.getRow() == 0);
			} catch (SQLException e) {
				e.printStackTrace();

				return false;
			}
		}

		public boolean moveToNext() {
			try {
				if (this.calledIsEmpty) {
					this.calledIsEmpty = false;
					return true;
				}
				return this.rs.next();
			} catch (SQLException e) {
				e.printStackTrace();

				return false;
			}
		}

		public int getColumnIndex(String name) {
			try {
				return this.rs.findColumn(name);
			} catch (SQLException e) {
				e.printStackTrace();

				return 0;
			}
		}

		public float getFloat(int columnIndex) {
			try {
				return this.rs.getFloat(columnIndex);
			} catch (SQLException e) {
				e.printStackTrace();

				return 0.0F;
			}
		}

		public BigDecimal getBigDecimal(int columnIndex) {
			try {
				return this.rs.getBigDecimal(columnIndex);
			} catch (SQLException e) {
				e.printStackTrace();

				return new BigDecimal(0);
			}
		}

		public int getInt(int columnIndex) {
			try {
				return this.rs.getInt(columnIndex);
			} catch (SQLException e) {
				e.printStackTrace();

				return 0;
			}
		}

		public String getString(int columnIndex) {
			try {
				return this.rs.getString(columnIndex);
			} catch (SQLException e) {
				e.printStackTrace();

				return "";
			}
		}

		public float getFloat(String columnName) {
			try {
				return this.rs.getFloat(columnName);
			} catch (SQLException e) {
				e.printStackTrace();

				return 0.0F;
			}
		}

		public int getInt(String columnName) {
			try {
				return this.rs.getInt(columnName);
			} catch (SQLException e) {
				e.printStackTrace();

				return 0;
			}
		}

		public long getLong(String columnName) {
			try {
				return this.rs.getLong(columnName);
			} catch (SQLException e) {
				e.printStackTrace();

				return 0L;
			}
		}

		public String getByColumnIndex(int index) {
			try {
				return this.rs.getString(index);
			} catch (SQLException e) {
				e.printStackTrace();

				return null;
			}
		}

		public String getString(String columnName) {
			try {
				return this.rs.getString(columnName);
			} catch (SQLException e) {
				e.printStackTrace();

				return "";
			}
		}

		public void close() throws SQLException {
			this.rs.close();
		}
	}
}
