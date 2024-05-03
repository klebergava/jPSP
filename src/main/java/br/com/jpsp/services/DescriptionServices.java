package br.com.jpsp.services;

import java.util.Set;

import br.com.jpsp.dao.DescriptionDAO;
import br.com.jpsp.model.Description;

public class DescriptionServices implements CRUDServices<Description> {
	
	public static final DescriptionServices instance = new DescriptionServices();
	private final DescriptionDAO dao = DescriptionDAO.instance;
	
	private DescriptionServices() {}

	@Override
	public Set<Description> getAll() {
		return dao.getAll();
	}

	@Override
	public void add(Description desc) {
		dao.addHistDesc(desc);
	}

	@Override
	public void remove(Description desc) throws Exception {
		dao.removeDesc(desc);
	}

	@Override
	public void update(Description desc) throws Exception {
	}

}
