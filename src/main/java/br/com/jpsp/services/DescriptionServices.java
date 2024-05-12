package br.com.jpsp.services;

import java.util.Set;

import br.com.jpsp.model.Description;

/**
 *
 */
public class DescriptionServices extends RepositoryAccessServices implements CRUDServices<Description> {

	public static final DescriptionServices instance = new DescriptionServices();

	private DescriptionServices() {}

	@Override
	public Set<Description> getAll() {
		return descDAO.getAll();
	}

	@Override
	public void add(Description desc) {
		descDAO.add(desc);
	}

	@Override
	public void remove(Description desc) throws Exception {
		descDAO.remove(desc);
	}

	@Override
	public void update(Description desc) throws Exception {
	}

}
