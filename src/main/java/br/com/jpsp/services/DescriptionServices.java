package br.com.jpsp.services;

import java.util.Set;

import br.com.jpsp.model.Description;
import br.com.jpsp.utils.Utils;

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
		synchronized (DescriptionServices.this) {
			descDAO.add(desc);
		}
	}

	@Override
	public void remove(Description desc) throws Exception {
		synchronized (DescriptionServices.this) {
			descDAO.remove(desc);
		}
	}

	@Override
	public void update(Description desc) throws Exception {
	}
	
	public Set<String> getAllDescriptions() {
		return this.descDAO.getAllDescriptions();
	}

	public void addDescriptions(Set<String> descriptions) {
		if (!Utils.isEmpty(descriptions)) {
			descriptions.forEach(desc -> {
				Description newDesc = new Description().from(desc);
				if (!descDAO.exists(newDesc)) {
					descDAO.add(newDesc);
				}
			});
		}
	}
}
