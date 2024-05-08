package br.com.jpsp.dao;

import java.sql.SQLException;
import java.util.Set;
import java.util.TreeSet;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import br.com.jpsp.model.Description;
import br.com.jpsp.utils.Utils;

public class DescriptionDAO extends TaskSetDBDAOv1 {
	private final static Logger log = LogManager.getLogger(DescriptionDAO.class);
	public static final DescriptionDAO instance = new DescriptionDAO();
	
	private DescriptionDAO() {
		super();
	}
	
	public void removeDesc(Description toRemove) {
		try {
			execute("DELETE FROM " + DESC_HIST_TABLE + " WHERE descricao = '" + toRemove.getDescription() + "';");
		} catch (Exception e) {
			log.error("removeDesc() " + e.getMessage());
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
					log.error("addHistDesc() " + e.getMessage());
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
			log.error("existsDesc() " + ex.getMessage());
			ex.printStackTrace();
		} catch (ClassNotFoundException e) {
			log.error("existsDesc() " + e.getMessage());
			e.printStackTrace();
		} finally {
			try {
				closeConnection(false);
			} catch (SQLException e) {
				log.error("existsDesc() " + e.getMessage());
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
			log.error("getAll() " + ex.getMessage());
			ex.printStackTrace();
		} catch (ClassNotFoundException e) {
			log.error("getAll() " + e.getMessage());
			e.printStackTrace();
		} finally {
			try {
				closeConnection(false);
			} catch (SQLException e) {
				log.error("getAll() " + e.getMessage());
				e.printStackTrace();
			}
		}

		return descriptions;
	}
}
