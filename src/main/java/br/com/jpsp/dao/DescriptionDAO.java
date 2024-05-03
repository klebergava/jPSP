package br.com.jpsp.dao;

import java.sql.SQLException;
import java.util.Set;
import java.util.TreeSet;

import br.com.jpsp.model.Description;
import br.com.jpsp.utils.Utils;

public class DescriptionDAO extends TaskSetDBDAOv1 {
	
	public static final DescriptionDAO instance = new DescriptionDAO();
	
	private DescriptionDAO() {
		super();
	}
	
	public void removeDesc(Description toRemove) {
		try {
			execute("DELETE FROM " + DESC_HIST_TABLE + " WHERE descricao = '" + toRemove.getDescription() + "';");
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public void addHistDesc(Description desc) {
		if (desc != null) {

			boolean exists = existsDesc(desc);

			if (!exists) {
				try {
					execute("INSERT INTO '" + DESC_HIST_TABLE + "' (descricao) values ('" + desc.getDescription() + "');");
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		}
	}
	
	private boolean existsDesc(Description desc) {
		boolean exists = false;
		try {
			openConnection(false);
			Result r = executeQuery("select count(*) from " + DESC_HIST_TABLE + " where lower(descricao) = '" + Utils.toLower(desc.getDescription()) + "';");
			if (!r.isEmpty()) {
				r.moveToNext();
				int count = r.getInt(1);
				exists = (count > 0);
			}
			r.close();
			
		} catch (SQLException ex) {
			ex.printStackTrace();
		} catch (ClassNotFoundException e) {
			e.printStackTrace();
		} finally {
			try {
				closeConnection(false);
			} catch (SQLException e) {
				e.printStackTrace();
			}
		}

		return exists;
	}

	public Set<Description> getAll() {
		Set<Description> descriptions = new TreeSet<Description>();
		try {
			openConnection(false);
			Result r = executeQuery("select descricao from " + DESC_HIST_TABLE + " order by descricao;");
			if (!r.isEmpty()) {
				while (r.moveToNext()) {
					descriptions.add(new Description(r.getString("descricao")));
				}
			}
			r.close();
		} catch (SQLException ex) {
			ex.printStackTrace();
		} catch (ClassNotFoundException e) {
			e.printStackTrace();
		} finally {
			try {
				closeConnection(false);
			} catch (SQLException e) {
				e.printStackTrace();
			}
		}

		return descriptions;
	}
}
